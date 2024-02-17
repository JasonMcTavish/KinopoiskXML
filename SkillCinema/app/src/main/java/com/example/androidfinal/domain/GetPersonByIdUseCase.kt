package com.example.androidfinal.domain

import com.example.androidfinal.data.CinemaRepository
import com.example.androidfinal.db.model.FilmsShortInfo
import com.example.androidfinal.db.model.PersonFilms
import com.example.androidfinal.db.model.PersonWithDetailInfo
import javax.inject.Inject

class GetPersonByIdUseCase @Inject constructor(
    private val repository: CinemaRepository
) {
    suspend fun executePersonDetailInfo(personId: Int): PersonWithDetailInfo {
        return repository.getPersonDetailInfo(personId)
    }

    suspend fun executeFilmShortInfo(filmId: Int): FilmsShortInfo {
        return repository.getFilmShortInfo(filmId)
    }

    suspend fun executeFilmsByPerson(personId: Int): List<PersonFilms> {
        return repository.getFilmsByPerson(personId)
    }
}