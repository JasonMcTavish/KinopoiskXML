package com.example.androidfinal.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.androidfinal.data.CategoriesFilms
import com.example.androidfinal.databinding.ItemCategoryListBinding
import com.example.androidfinal.ui.adapters.holders.CategoryViewHolder
import com.example.androidfinal.ui.home.HomeViewModel

class CategoryAdapter(
    private val maxListSize: Int,
    private val category: List<HomeViewModel.Companion.HomeList>,
    private val clickNextButton: (category: CategoriesFilms) -> Unit,
    private val clickFilms: (filmId: Int) -> Unit
) : RecyclerView.Adapter<CategoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = CategoryViewHolder(
        ItemCategoryListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bindItem(
            maxListSize,
            category[position],
            { clickNextButton(it) },
            { clickFilms(it) }
        )
    }

    override fun getItemCount() = category.size
}