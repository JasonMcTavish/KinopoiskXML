package com.example.androidfinal.ui.adapters.profile

import com.example.androidfinal.db.model.FilmWithGenres

sealed class ProfileAdapterTypes {
    data class ItemDB(val film: FilmWithGenres): ProfileAdapterTypes()
    data class ItemCollection(val collection: CollectionDB): ProfileAdapterTypes()
}

data class CollectionDB(
    val name: String,
    val count: Int,
    val icon: Int
)

