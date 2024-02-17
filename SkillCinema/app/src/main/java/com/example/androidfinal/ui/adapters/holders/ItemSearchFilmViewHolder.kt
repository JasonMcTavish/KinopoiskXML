package com.example.androidfinal.ui.adapters.holders

import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.androidfinal.app.loadImage
import com.example.androidfinal.databinding.ItemSearchFilmBinding
import com.example.androidfinal.ui.adapters.MyAdapterTypes


class ItemSearchFilmViewHolder(val binding: ItemSearchFilmBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bindItem(item: MyAdapterTypes.ItemSearchFilms, onClick: (Int) -> Unit) {
        binding.apply {
            itemFilmPoster.loadImage(item.film.posterUrl)
            itemFilmName.text = item.film.nameRu
            itemFilmGenre.text = item.film.genres.joinToString(", ") { it.genre }
            itemFilmRating.isVisible = false
        }
        binding.itemFilmPoster.setOnClickListener { onClick(item.film.filmId) }
    }
}