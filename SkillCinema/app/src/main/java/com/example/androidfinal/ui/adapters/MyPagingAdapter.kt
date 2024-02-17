package com.example.androidfinal.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.androidfinal.R
import com.example.androidfinal.databinding.ItemFilmBinding
import com.example.androidfinal.databinding.ItemSearchFilmBinding
import com.example.androidfinal.databinding.ItemSearchPersonBinding
import com.example.androidfinal.ui.adapters.holders.ItemFilmViewHolder
import com.example.androidfinal.ui.adapters.holders.ItemSearchFilmViewHolder
import com.example.androidfinal.ui.adapters.holders.ItemSearchPersonViewHolder

class MyPagingAdapter(
    private val onClick: (Int) -> Unit
) : PagingDataAdapter<MyAdapterTypes, RecyclerView.ViewHolder>(MyDiffUtil()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            R.layout.item_film -> {
                ItemFilmViewHolder(
                    ItemFilmBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                )
            }
            R.layout.item_search_film -> {
                ItemSearchFilmViewHolder(
                    ItemSearchFilmBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                )
            }
            R.layout.item_search_person -> {
                ItemSearchPersonViewHolder(
                    ItemSearchPersonBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                )
            }
            else -> throw Exception("onCreateViewHolder - Unknown ViewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ItemFilmViewHolder -> {
                val item = getItem(position) as MyAdapterTypes.ItemFilmWithGenre
                holder.bindItem(item) { onClick(it) }
            }
            is ItemSearchFilmViewHolder -> {
                val item = getItem(position) as MyAdapterTypes.ItemSearchFilms
                holder.bindItem(item) { onClick(it) }
            }
            is ItemSearchPersonViewHolder -> {
                val item = getItem(position) as MyAdapterTypes.ItemSearchPersons
                holder.bindItem(item) { onClick(it) }
            }
            else -> throw Exception("onBindViewHolder - Unknown ViewType")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is MyAdapterTypes.ItemFilmWithGenre -> R.layout.item_film
            is MyAdapterTypes.ItemSearchFilms -> R.layout.item_search_film
            is MyAdapterTypes.ItemSearchPersons -> R.layout.item_search_person
            else -> 4
        }
    }
}