package com.example.androidfinal.ui.adapters.profile


import android.graphics.drawable.Icon
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.RecyclerView
import com.example.androidfinal.R
import com.example.androidfinal.app.loadImage
import com.example.androidfinal.databinding.ItemFilmBinding

class ProfileDBViewHolder(private val binding: ItemFilmBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bindNextShow(onClick: () -> Unit) {
        binding.apply {
            showAll.isInvisible = false
            tvShowAll.text = binding.root.resources.getString(R.string.profile_item_clear_label)
            btnArrowShowAll.setImageIcon(Icon.createWithResource(binding.root.context, R.drawable.ic_delete))
            itemFilm.isInvisible = true
        }

        binding.btnArrowShowAll.setOnClickListener { onClick() }
    }

    fun bindItem(item: ProfileAdapterTypes.ItemDB, clickFilms: (filmId: Int) -> Unit) {
        binding.apply {
            showAll.isInvisible = true
            itemFilm.isInvisible = false
            itemFilmName.text = item.film.film.name
            itemFilmGenre.text = item.film.genres.joinToString(", ") { it.genre }
            itemFilmPoster.loadImage(item.film.film.poster)
            if (item.film.film.rating != null) {
                itemFilmRating.isInvisible = false
                itemFilmRating.text = item.film.film.rating
            } else itemFilmRating.isInvisible = true
        }
        binding.itemFilm.setOnClickListener { clickFilms(item.film.film.filmId) }
    }
}