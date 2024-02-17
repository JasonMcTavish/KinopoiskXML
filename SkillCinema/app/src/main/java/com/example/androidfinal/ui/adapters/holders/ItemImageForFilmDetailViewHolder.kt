package com.example.androidfinal.ui.adapters.holders

import androidx.recyclerview.widget.RecyclerView
import com.example.androidfinal.app.loadImage
import com.example.androidfinal.databinding.ItemGalleryFilmDetailBinding
import com.example.androidfinal.ui.adapters.MyAdapterTypes


class ItemImageForFilmDetailViewHolder(
    private val binding: ItemGalleryFilmDetailBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bindItem(item: MyAdapterTypes.ItemFilmImage) {
        binding.galleryImageFilmDetail.loadImage(item.image.preview)
    }
}