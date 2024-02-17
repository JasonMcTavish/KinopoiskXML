package com.example.androidfinal.ui.adapters.holders

import androidx.core.view.isInvisible
import androidx.recyclerview.widget.RecyclerView
import com.example.androidfinal.app.loadImage
import com.example.androidfinal.databinding.ItemFilmBinding
import com.example.androidfinal.db.model.FilmGenres
import com.example.androidfinal.ui.adapters.MyAdapterTypes


class ItemFilmViewHolder(private val binding: ItemFilmBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bindNextShow(clickNextButton: () -> Unit) {
        binding.apply {
            showAll.isInvisible = false
            itemFilm.isInvisible = true
        }

        binding.btnArrowShowAll.setOnClickListener { clickNextButton() }
    }

    fun bindItem(item: MyAdapterTypes.ItemFilmWithGenre, clickFilms: (filmId: Int) -> Unit) {
        binding.apply {
            showAll.isInvisible = true
            itemFilm.isInvisible = false
            itemFilmName.text = item.filmWithGenre.film.name
            itemFilmGenre.text = createGenreName(item.filmWithGenre.genres)
            itemFilmPoster.loadImage(item.filmWithGenre.film.poster)
            itemFilmRating.text = item.filmWithGenre.film.rating
            itemFilmRating.isInvisible = item.filmWithGenre.film.rating == null
        }
        binding.itemFilm.setOnClickListener {
            clickFilms(item.filmWithGenre.film.filmId)
        }
    }

    private fun createGenreName(genres: List<FilmGenres>): String {
        var genreName = ""
        genres.forEachIndexed { index, genre ->
            genreName += if (index == genres.lastIndex) genre.genre
            else "${genre.genre}, "
        }
        return genreName
    }

    companion object {
        private const val TAG = "ItemFilmVH"
    }
}