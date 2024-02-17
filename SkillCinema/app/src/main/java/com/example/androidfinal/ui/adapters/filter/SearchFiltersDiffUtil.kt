package com.example.androidfinal.ui.adapters.filter

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil
import com.example.androidfinal.entity.FilterCountryGenre

class SearchFiltersDiffUtil : DiffUtil.ItemCallback<FilterCountryGenre>() {
    override fun areItemsTheSame(
        oldItem: FilterCountryGenre,
        newItem: FilterCountryGenre
    ) = oldItem.name == newItem.name

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(
        oldItem: FilterCountryGenre,
        newItem: FilterCountryGenre
    ) = oldItem == newItem
}