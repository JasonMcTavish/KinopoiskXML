package com.example.androidfinal.ui.persondetail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.androidfinal.R
import com.example.androidfinal.app.loadImage
import com.example.androidfinal.databinding.FragmentPersonDetailBinding
import com.example.androidfinal.db.model.FilmsShortInfo
import com.example.androidfinal.ui.StateLoading
import com.example.androidfinal.ui.adapters.MyAdapterTypes
import com.example.androidfinal.ui.adapters.MyListAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PersonDetailFragment : Fragment() {

    private var _binding:FragmentPersonDetailBinding?=null
    private val binding get()=_binding!!

    private  val viewModel: PersonDetailViewModel by viewModels()
    private lateinit var filmAdapter: MyListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPersonDetailBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args: PersonDetailFragmentArgs by navArgs()
        viewModel.getPersonDetail(args.personId)

        setLoadingStateAndDetails(args.personId)    // set loading listener
        setBestFilmsAdapter()                       // set adapter
        getPersonInfo()                             // set person info
        setFilmList()                               // set film list
        setButtonsListeners(args.personId)          // set titles and ClickListeners
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setLoadingStateAndDetails(personId: Int) {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.loadCurrentStaff.collect { state ->
                    when (state) {
                        is StateLoading.Loading -> {
                            binding.apply {
                                progressGroup.isVisible = true
                                loadingProgress.isVisible = true
                                loadingBanner.isVisible = true
                                loadingRefreshBtn.isVisible = false

                                staffDetailMainGroup.isVisible = false
                                staffDetailBestGroup.isVisible = false
                                staffDetailFilmographyGroup.isVisible = false
                            }
                        }

                        is StateLoading.Success -> {
                            binding.apply {
                                progressGroup.isVisible = false
                                loadingBanner.isVisible = false
                                loadingRefreshBtn.isVisible = false
                                loadingProgress.isVisible = false

                                staffDetailMainGroup.isVisible = true
                                staffDetailBestGroup.isVisible = true
                                staffDetailFilmographyGroup.isVisible = true
                            }
                        }

                        else -> {
                            binding.apply {
                                progressGroup.isVisible = true
                                loadingBanner.isVisible = true
                                loadingRefreshBtn.isVisible = true
                                loadingProgress.isVisible = false

                                staffDetailMainGroup.isVisible = false
                                staffDetailBestGroup.isVisible = false
                                staffDetailFilmographyGroup.isVisible = false
                                loadingRefreshBtn.setOnClickListener {
                                    viewModel.getPersonDetail(
                                        personId
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun getPersonInfo() {
        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.currentPerson.collect { person ->
                    if (person != null) {
                        binding.apply {
                            staffDetailPoster.loadImage(person.personFilms.poster)
                            staffDetailName.text = person.personFilms.name
                            if (person.personFilms.profession != null) staffDetailProfession.text =
                                person.personFilms.profession
                            else staffDetailProfession.isVisible = false
                        }
                    }
                }
            }
        }
    }

    private fun setBestFilmsAdapter() {
        filmAdapter = MyListAdapter(10, {}, { onClickFilm(it) })
        binding.staffDetailBestList.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.staffDetailBestList.adapter = filmAdapter
    }

    private fun setFilmList() {
        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.films.collect { films ->
                    if (films.isNotEmpty()) {
                        binding.staffDetailFilmsCount.text =
                            resources.getQuantityString(
                                R.plurals.staff_details_film_count,
                                films.size,
                                films.size
                            )
                        val list: MutableList<FilmsShortInfo> = films.toMutableList()
                        list.removeAll { it.rating == null || it.rating.contains("null") }
                        val tempResult: List<FilmsShortInfo?> = list
                            .groupBy { it.filmId }
                            .map { it.value.maxBy { rating -> rating.rating.toString() } }
                            .sortedBy { it.rating }
                            .reversed()
                        val result = if (tempResult.size > 10) tempResult.take(10) else tempResult

                        filmAdapter.submitList(result.map { MyAdapterTypes.ItemFilmShortInfo(it!!) })
                    }
                }
            }
        }
    }

    private fun onClickFilm(filmId: Int) {
        val action =
            PersonDetailFragmentDirections.actionFragmentPersonDetailToFragmentFilmDetail(filmId)
        findNavController().navigate(action)
    }

    private fun onClickAllFilmsByPerson(personId: Int) {
        val action = PersonDetailFragmentDirections
            .actionFragmentPersonDetailToFragmentFilmography(personId)
        findNavController().navigate(action)
    }

    private fun setButtonsListeners(personId: Int) {
        binding.staffDetailBackBtn.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        binding.staffDetailShowAllFilmsBtn.setOnClickListener { onClickAllFilmsByPerson(personId) }
        binding.staffDetailShowAllFilmsTv.setOnClickListener { onClickAllFilmsByPerson(personId) }
        binding.staffDetailShowAllBestBtn.setOnClickListener { onClickAllFilmsByPerson(personId) }
        binding.staffDetailShowAllBestTv.setOnClickListener { onClickAllFilmsByPerson(personId) }
    }

}