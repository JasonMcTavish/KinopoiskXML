package com.example.androidfinal.ui.adapters.holders

import androidx.recyclerview.widget.RecyclerView
import com.example.androidfinal.app.loadImage
import com.example.androidfinal.databinding.ItemSearchPersonBinding
import com.example.androidfinal.ui.adapters.MyAdapterTypes


class ItemSearchPersonViewHolder(private val binding: ItemSearchPersonBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bindItem(item: MyAdapterTypes.ItemSearchPersons, onClick: (personId: Int) -> Unit) {
        binding.apply {
            itemPersonPoster.loadImage(item.person.posterUrl)
            itemPersonName.text = item.person.nameRu
        }
        binding.root.setOnClickListener { onClick(item.person.personId) }
    }
}