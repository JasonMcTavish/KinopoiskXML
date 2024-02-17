package com.example.androidfinal.ui.filmdetail

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidfinal.R
import com.example.androidfinal.app.loadImage
import com.example.androidfinal.data.CategoriesFilms
import com.example.androidfinal.data.TOP_TYPES
import com.example.androidfinal.databinding.FragmentFilmDetailBinding
import com.example.androidfinal.db.model.FilmPersons
import com.example.androidfinal.db.model.FilmWithDetailInfo
import com.example.androidfinal.ui.StateLoading
import com.example.androidfinal.ui.adapters.MyAdapterTypes
import com.example.androidfinal.ui.adapters.MyListAdapter
import com.example.androidfinal.ui.seasons.SeasonsFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FilmDetailFragment : Fragment() {

    private var _binding: FragmentFilmDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FilmDetailViewModel by viewModels()
    private lateinit var actorAdapter: MyListAdapter
    private lateinit var makersAdapter: MyListAdapter
    private lateinit var galleryAdapter: MyListAdapter
    private lateinit var similarAdapter: MyListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFilmDetailBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args: FilmDetailFragmentArgs by navArgs()
        viewModel.getFilmById(args.filmId)

        stateLoadingListener()                                      // Setup load listener


        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.filmDetailInfo.collect { film ->
                    if (film != null) {
                        setFilmDetails(film)                        // Setup poster and film describe
                        setFilmPersons(film)                        // Setup film actors/makers list
                        setFilmGallery(film)                        // Setup film gallery
                        setSimilar(film)                            // Setup similar film list
                        setButtonsForDB(film.film.filmId)           // Setup buttons for DB-collections
                    }
                }
            }
        }


        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun stateLoadingListener() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.loadCurrentFilmState.collect { state ->
                    when (state) {
                        is StateLoading.Loading -> {
                            binding.apply {
                                filmDetail.progress = 1f

                                progressGroup.isVisible = true
                                loadingProgress.isVisible = true
                                loadingBanner.isVisible = true
                                loadingRefreshBtn.isVisible = false

                                filmName.isVisible = false
                                filmMainGroup.isVisible = false
                                filmDescriptionGroup.isVisible = false
                                myScroll.isVisible = false
                            }
                        }

                        is StateLoading.Success -> {
                            binding.apply {
                                filmDetail.progress = 0f

                                progressGroup.isVisible = false
                                loadingBanner.isVisible = false
                                loadingRefreshBtn.isVisible = false
                                loadingProgress.isVisible = false

                                filmName.isVisible = true
                                filmMainGroup.isVisible = true
                                filmDescriptionGroup.isVisible = true
                                myScroll.isVisible = true
                            }
                        }

                        else -> {
                            binding.apply {
                                filmDetail.progress = 1f

                                progressGroup.isVisible = true
                                loadingBanner.isVisible = true
                                loadingRefreshBtn.isVisible = true
                                loadingProgress.isVisible = false

                                filmName.isVisible = false
                                filmMainGroup.isVisible = false
                                filmDescriptionGroup.isVisible = false
                                myScroll.isVisible = false
                            }
                        }
                    }
                }
            }
        }
    }

    // Film details
    private fun setFilmDetails(film: FilmWithDetailInfo) {
        binding.apply {
            filmName.text = film.film.name
            filmPoster.loadImage(film.film.poster)
            filmDescriptionShort.text = film.detailInfo?.shortDescription
            filmDescriptionFull.text = film.detailInfo?.description
            filmRatingNameTv.text = getRatingName(film)
            filmYearGenresTv.text = getYearGenres(film, requireContext())
            filmCountryLengthAgeLimitTv.text = getStrCountriesLengthAge(film)

        }
        if (film.detailInfo?.type == CategoriesFilms.TV_SERIES.name) {
            binding.seasonsGroup.isVisible = true
            getSeriesSeasons(film)
        } else {
            binding.seasonsGroup.isVisible = false
        }
    }

    // Seasons details
    private fun getSeriesSeasons(film: FilmWithDetailInfo) {
        val seasonsCount = film.seriesEpisodes?.map { it.seriesNumber }?.toSet()?.size
        val seriesCount = film.seriesEpisodes?.size

        val seasonStr = seasonsCount?.let {
            resources.getQuantityString(R.plurals.film_details_series_count, it, it)
        }
        val episodeStr = seriesCount?.let {
            resources.getQuantityString(R.plurals.film_details_episode_count, it, it)
        }

        binding.seriesSeasonsCount.text =
            resources.getString(R.string.seasons_episodes_count, seasonStr, episodeStr)

        binding.seriesSeasonsBtn.setOnClickListener {
            onClickShowAllSeasons(film.film.filmId, film.film.name)
        }
    }

    // Persons list
    private fun setFilmPersons(film: FilmWithDetailInfo) {
        actorAdapter = MyListAdapter(
            maxListSize = 20,
            clickEndButton = {},
            clickItem = { onClickItemPerson(it) }
        )
        makersAdapter = MyListAdapter(
            maxListSize = 20,
            clickEndButton = {},
            clickItem = { onClickItemPerson(it) }
        )
        binding.filmActorsList.layoutManager =
            GridLayoutManager(
                requireContext(), MAX_ACTORS_ROWS, GridLayoutManager.HORIZONTAL, false
            )
        binding.filmMakersList.layoutManager =
            GridLayoutManager(
                requireContext(), MAX_MAKERS_ROWS, GridLayoutManager.HORIZONTAL, false
            )
        binding.filmActorsList.adapter = actorAdapter
        binding.filmMakersList.adapter = makersAdapter

        val actors = mutableListOf<FilmPersons>()
        val makers = mutableListOf<FilmPersons>()

        film.persons?.forEach {
            if (it.professionKey == "ACTOR") { actors.add(it) }
            else { makers.add(it) }
        }

        // film actors
        actorAdapter.submitList(
            if (actors.size < MAX_ACTORS_COLUMN * MAX_ACTORS_ROWS) {
                binding.filmActorsBtn.isVisible = false
                binding.filmActorsCount.isVisible = false
                actors.map { MyAdapterTypes.ItemFilmPerson(it) }
            } else {
                binding.filmActorsBtn.isVisible = true
                binding.filmActorsCount.isVisible = true
                binding.filmActorsCount.text = actors.size.toString()
                val tempActors = actors.take(MAX_ACTORS_COLUMN * MAX_ACTORS_ROWS)
                tempActors.map { MyAdapterTypes.ItemFilmPerson(it) }
            }
        )
        binding.filmActorsBtn.setOnClickListener { onClickShowAllPersons(film.film.filmId, "ACTOR") }
        binding.filmActorsCount.setOnClickListener { onClickShowAllPersons(film.film.filmId, "ACTOR") }

        // film makers
        makersAdapter.submitList(
            if (makers.size < MAX_MAKERS_COLUMN * MAX_MAKERS_ROWS) {
                binding.filmMakersBtn.isVisible = false
                binding.filmMakersCount.isVisible = false
                makers.map { MyAdapterTypes.ItemFilmPerson(it) }
            } else {
                binding.filmMakersBtn.isVisible = true
                binding.filmMakersCount.isVisible = true
                binding.filmMakersCount.text = makers.size.toString()
                val tempMakers = makers.take(MAX_MAKERS_COLUMN * MAX_MAKERS_ROWS)
                tempMakers.map { MyAdapterTypes.ItemFilmPerson(it) }
            }
        )
        binding.filmMakersBtn.setOnClickListener { onClickShowAllPersons(film.film.filmId, "") }
        binding.filmMakersCount.setOnClickListener { onClickShowAllPersons(film.film.filmId, "") }
    }

    // Film gallery
    private fun setFilmGallery(film: FilmWithDetailInfo) {
        binding.filmGalleryCount.text = film.gallery.size.toString()

        galleryAdapter = MyListAdapter(20, {}) {}
        binding.filmGalleryList.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.filmGalleryList.adapter = galleryAdapter

        val temp = film.gallery.take(10)
        galleryAdapter.submitList(temp.map { MyAdapterTypes.ItemFilmImage(it) })

        binding.filmGalleryCount.setOnClickListener { onClickShowAllGallery(film.film.filmId) }
        binding.filmGalleryBtn.setOnClickListener { onClickShowAllGallery(film.film.filmId) }
    }

    // Similar films
    private fun setSimilar(film: FilmWithDetailInfo) {
        similarAdapter =
            MyListAdapter(20, { onClickShowAllSimilar(film.film.filmId) }, { onClickSimilarItem(it) })
        binding.filmSimilarList.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.filmSimilarList.adapter = similarAdapter

        if (film.similar != null) {
            binding.filmSimilarCount.text = film.similar.size.toString()
            similarAdapter.submitList(film.similar.map { MyAdapterTypes.ItemFilmSimilar(it) })
        } else binding.filmSimilarCount.isVisible = false


        binding.filmSimilarCount.setOnClickListener { onClickShowAllSimilar(film.film.filmId) }
        binding.filmSimilarBtn.setOnClickListener { onClickShowAllSimilar(film.film.filmId) }
    }

    // ClickListeners
    private fun onClickShowAllSeasons(filmId: Int, filmName: String) {
        findNavController().navigate(
            R.id.action_fragmentFilmDetail_to_fragmentSeries,
            bundleOf(
                SeasonsFragment.KEY_SEASON_ID to filmId,
                SeasonsFragment.KEY_SERIES_NAME to filmName
            )
        )
    }

    private fun onClickShowAllPersons(filmId: Int, professionKey: String) {
        findNavController().navigate(
            R.id.action_fragmentFilmDetail_to_fragmentPersonsByFilm,
            bundleOf(
                FilmActorsFragment.KEY_ALL_STAFF_FILM to filmId,
                FilmActorsFragment.KEY_ALL_STAFF_PROFESSION to professionKey
            )
        )
    }

    private fun onClickItemPerson(personId: Int) {
        val action =
            FilmDetailFragmentDirections.actionFragmentFilmDetailToFragmentPersonDetail(personId)
        findNavController().navigate(action)
    }

    private fun onClickShowAllGallery(filmId: Int) {
        val action = FilmDetailFragmentDirections
            .actionFragmentFilmDetailToFragmentGallery(filmId)
        findNavController().navigate(action)
    }

    private fun onClickShowAllSimilar(filmId: Int) {
        val action =
            FilmDetailFragmentDirections.actionFragmentFilmDetailToFragmentSimilarFilms(filmId)
        findNavController().navigate(action)
    }

    private fun onClickSimilarItem(filmId: Int) = viewModel.getFilmById(filmId)

    private fun setButtonsForDB(filmId: Int) {
        var isInFavorite = 0
        var isInBookmark = 0
        var isViewed = 0

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.checkFilmInDB(filmId).collect { markers ->
                    if (markers != null) {
                        isInFavorite = markers.isFavorite
                        isInBookmark = markers.inCollection
                        isViewed = markers.isViewed
                        if (isInFavorite == 1) binding.btnToFavorite.setColorFilter(Color.BLUE)
                        else binding.btnToFavorite.setColorFilter(Color.parseColor("#B5B5C9"))
                        if (isInBookmark == 1) binding.btnToBookmark.setColorFilter(Color.BLUE)
                        else binding.btnToBookmark.setColorFilter(Color.parseColor("#B5B5C9"))
                        if (isViewed == 1) binding.btnIsViewed.setColorFilter(Color.BLUE)
                        else binding.btnIsViewed.setColorFilter(Color.parseColor("#B5B5C9"))
                    }
                }
            }
        }
        binding.btnToFavorite.setOnClickListener {
            isInFavorite = if (isInFavorite == 1) 0 else 1
            viewModel.updateFilmMarkers(filmId, isInFavorite, isInBookmark, isViewed)
        }
        binding.btnToBookmark.setOnClickListener {
            isInBookmark = if (isInBookmark == 1) 0 else 1
            viewModel.updateFilmMarkers(filmId, isInFavorite, isInBookmark, isViewed)
        }
        binding.btnIsViewed.setOnClickListener {
            isViewed = if (isViewed == 1) 0 else 1
            viewModel.updateFilmMarkers(filmId, isInFavorite, isInBookmark, isViewed)
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        private const val MAX_ACTORS_COLUMN = 5
        private const val MAX_ACTORS_ROWS = 4
        private const val MAX_MAKERS_COLUMN = 3
        private const val MAX_MAKERS_ROWS = 2

        private fun getRatingName(film: FilmWithDetailInfo): String {
            val result = mutableListOf<String>()

            val tempRating = film.film.rating
            val rating = if (tempRating != null) {
                if (tempRating.contains("%")) {
                    (tempRating.substringBefore(".").removeSuffix(".").toInt() / 10).toString()
                } else tempRating
            } else null

            if (rating != null) result.add(rating)
            val name = film.film.name
            if (name.isNotEmpty()) result.add(name)
            return result.joinToString(", ")
        }

        private fun getYearGenres(film: FilmWithDetailInfo, context: Context): String {
            val result = mutableListOf<String>()

            if (film.detailInfo?.type == TOP_TYPES.getValue(CategoriesFilms.TV_SERIES)) {
                val tempStr = mutableListOf<String>()
                if (film.detailInfo.startYear != null && film.detailInfo.endYear != null) {
                    tempStr.add(film.detailInfo.startYear.toString())
                    tempStr.add(film.detailInfo.endYear.toString())
                } else {
                    if (film.detailInfo.startYear != null) tempStr.add(film.detailInfo.startYear.toString())
                    else context.getString(R.string.placeholder_series_start_year)
                    if (film.detailInfo.endYear != null) tempStr.add(film.detailInfo.endYear.toString())
                    else context.getString(R.string.placeholder_series_end_year)
                }
                result.add(tempStr.joinToString("-"))
            } else {
                if (film.detailInfo?.year != null) result.add(film.detailInfo.year.toString())
            }

            if (film.genres.size > 1) {
                film.genres.take(2).map { result.add(it.genre) }
            } else if (film.genres.size == 1) {
                result.add(film.genres.first().genre)
            } else result.add("")

            return result.joinToString(", ")
        }

        private fun getStrCountriesLengthAge(film: FilmWithDetailInfo): String {
            val result = mutableListOf<String?>()

            val countries = film.countries
            val resultCountries = if (countries != null) {
                if (countries.size == 1) {
                    countries.first().country
                } else if (countries.size > 1) {
                    countries.joinToString(", ") { it.country }
                } else {
                    null
                }
            } else {
                null
            }
            result.add(resultCountries)

            val length = film.detailInfo?.length
            val resultLength = if (length != null) {
                val hours = length.div(60)
                val minutes = length.rem(60)
                "$hours ч $minutes мин"
            } else null
            result.add(resultLength)

            val resultAgeLimit = film.detailInfo?.ageLimit?.removePrefix("age")
            result.add("$resultAgeLimit+")

            return result.joinToString(", ")
        }
    }

}