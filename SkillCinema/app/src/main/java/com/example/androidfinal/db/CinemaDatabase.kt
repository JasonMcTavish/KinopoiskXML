package com.example.androidfinal.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.androidfinal.db.model.CollectionFilms
import com.example.androidfinal.db.model.FilmCountries
import com.example.androidfinal.db.model.FilmDetailInfo
import com.example.androidfinal.db.model.FilmGenres
import com.example.androidfinal.db.model.FilmImage
import com.example.androidfinal.db.model.FilmInCollection
import com.example.androidfinal.db.model.FilmMarkers
import com.example.androidfinal.db.model.FilmPersons
import com.example.androidfinal.db.model.FilmSimilar
import com.example.androidfinal.db.model.FilmTopType
import com.example.androidfinal.db.model.FilmsShortInfo
import com.example.androidfinal.db.model.HistoryFilms
import com.example.androidfinal.db.model.PersonFilms
import com.example.androidfinal.db.model.PersonShortInfo
import com.example.androidfinal.db.model.SeasonEpisode


@Database(
    entities = [
        FilmsShortInfo::class,
        FilmDetailInfo::class,
        FilmPersons::class,
        FilmImage::class,
        FilmGenres::class,
        FilmCountries::class,
        PersonShortInfo::class,
        PersonFilms::class,
        CollectionFilms::class,
        FilmInCollection::class,
        FilmSimilar::class,
        SeasonEpisode::class,
        FilmMarkers::class,
        HistoryFilms::class,
        FilmTopType::class
    ],
    version = 1
)
abstract class CinemaDatabase : RoomDatabase() {
    abstract fun cinemaDao(): CinemaDao
}