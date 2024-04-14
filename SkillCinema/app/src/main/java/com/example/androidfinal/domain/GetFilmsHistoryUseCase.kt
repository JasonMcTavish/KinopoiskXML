package com.example.androidfinal.domain

import com.example.androidfinal.data.CinemaRepository
import com.example.androidfinal.db.model.CollectionFilms
import com.example.androidfinal.db.model.FilmWithDetailInfo
import com.example.androidfinal.db.model.FilmWithGenres
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFilmsHistoryUseCase @Inject constructor(
    private val repository: CinemaRepository
) {
    suspend fun addFilmToHistory(filmId: Int) {
        repository.insertFilmToHistory(filmId)
    }

    fun executeAllHistoryFilms(): Flow<List<FilmWithGenres>> {
        return repository.getAllFilmsHistory()
    }

    suspend fun clearHistoryFilms() {
        repository.clearAllHistoryFilms()
    }

    suspend fun clearViewedFilms() {
        repository.clearAllViewedFilms()
    }

    fun executeAllViewedFilms(): Flow<List<FilmWithGenres>> {
        return repository.getAllViewedFilms()
    }

    fun executeCountFavoriteFilms(): Int {
        return repository.getCountFavoriteFilms()
    }

    fun executeCollectionsList(): Flow<List<CollectionFilms>> {
        return repository.getAllCollections()
    }

    fun checkFilmInCollection(collectionName: String, filmId: Int): Int {
        return repository.checkFilmInCollection(collectionName, filmId)
    }


    suspend fun addNewCollection(collectionName: String) {
        return repository.addCollection(name = collectionName)
    }

    suspend fun addNewFilmInCollections(filmId: Int, collectionName: String){
        return repository.insertFilmInCollection(filmId,collectionName)
    }

    suspend fun deleteFilmFromCollections(filmId: Int, collectionName: String){
        return repository.deleteFilmFromCollection(filmId,collectionName)
    }
}