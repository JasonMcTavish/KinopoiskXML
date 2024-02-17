package com.example.androidfinal.ui.adapters.holders

import androidx.recyclerview.widget.RecyclerView
import com.example.androidfinal.app.loadImage
import com.example.androidfinal.databinding.ItemGalleryFullscreenBinding
import com.example.androidfinal.ui.adapters.MyAdapterTypes


class ItemGalleryFullscreenViewHolder(val binding: ItemGalleryFullscreenBinding) :
    RecyclerView.ViewHolder(binding.root) {
        fun bindItem(image: MyAdapterTypes.ItemGalleryFullScreen) {
            binding.galleryImageFullscreen.loadImage(image.image.image)
        }
}