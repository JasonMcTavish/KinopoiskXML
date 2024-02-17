package com.example.androidfinal.ui.adapters

import com.example.androidfinal.entity.FilmByFilter
import com.example.androidfinal.entity.ItemPerson
import com.example.androidfinal.db.model.FilmImage
import com.example.androidfinal.db.model.FilmPersons
import com.example.androidfinal.db.model.FilmSimilar
import com.example.androidfinal.db.model.FilmWithGenres
import com.example.androidfinal.db.model.FilmsShortInfo
import com.example.androidfinal.db.model.SeasonEpisode

sealed class MyAdapterTypes {
    data class ItemFilmWithGenre(val filmWithGenre: FilmWithGenres) : MyAdapterTypes()
    data class ItemFilmShortInfo(val filmShortInfo: FilmsShortInfo) : MyAdapterTypes()
    data class ItemFilmSimilar(val similar: FilmSimilar) : MyAdapterTypes()
    data class ItemFilmPerson(val person: FilmPersons) : MyAdapterTypes()
    data class ItemFilmImage(val image: FilmImage) : MyAdapterTypes()
    data class ItemEpisode(val season: SeasonEpisode) : MyAdapterTypes()
    data class ItemGalleryImage(val image: FilmImage) : MyAdapterTypes()
    data class ItemGalleryFullScreen(val image: FilmImage) : MyAdapterTypes()
    data class ItemSearchPersons(val person: ItemPerson) : MyAdapterTypes()
    data class ItemSearchFilms(val film: FilmByFilter) : MyAdapterTypes()
}
