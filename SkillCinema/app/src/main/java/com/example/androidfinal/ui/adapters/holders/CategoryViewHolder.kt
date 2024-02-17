package com.example.androidfinal.ui.adapters.holders

import androidx.core.view.isInvisible
import androidx.recyclerview.widget.RecyclerView
import com.example.androidfinal.data.CategoriesFilms
import com.example.androidfinal.databinding.ItemCategoryListBinding
import com.example.androidfinal.ui.adapters.MyAdapterTypes
import com.example.androidfinal.ui.adapters.MyListAdapter
import com.example.androidfinal.ui.home.HomeViewModel

class CategoryViewHolder(private val binding: ItemCategoryListBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bindItem(
        maxListSize: Int,
        item: HomeViewModel.Companion.HomeList,
        clickNextButton: (category: CategoriesFilms) -> Unit,
        clickFilms: (filmId: Int) -> Unit
    ){
        val adapter = MyListAdapter( maxListSize,
            { clickNextButton(item.category) },
            { clickFilms(it) })
        adapter.submitList(item.filmList.map { MyAdapterTypes.ItemFilmWithGenre(it) })
        binding.titleCategory.text = item.category.text
        binding.filmList.adapter = adapter
        binding.tvTitleShowAll.apply {
            this.isInvisible = item.filmList.size < maxListSize
            this.setOnClickListener { clickNextButton(item.category) }
        }
    }
}