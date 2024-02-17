package com.example.androidfinal.domain


import com.example.androidfinal.data.CinemaRepository
import com.example.androidfinal.db.model.FilmPersons
import javax.inject.Inject

class GetActorsListUseCase @Inject constructor(
    private val newRepository: CinemaRepository
) {
    suspend fun executePersonsList(filmId: Int): List<FilmPersons> {
        return newRepository.getPersonsByFilm(filmId)
    }
}