package com.example.androidfinal.ui.adapters.holders

import androidx.recyclerview.widget.RecyclerView
import com.example.androidfinal.app.loadImage
import com.example.androidfinal.databinding.ItemGalleryImageBinding
import com.example.androidfinal.ui.adapters.MyAdapterTypes

class ItemGalleryViewHolder(val binding: ItemGalleryImageBinding) :
    RecyclerView.ViewHolder(binding.root) {
        fun bindItem(image: MyAdapterTypes.ItemGalleryImage, onClick: () -> Unit) {
            binding.galleryImage.loadImage(image.image.image)
            binding.root.setOnClickListener { onClick() }
        }
}