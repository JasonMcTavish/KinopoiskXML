package com.example.androidfinal.domain

import com.example.androidfinal.data.CinemaRepository
import com.example.androidfinal.db.model.FilmWithDetailInfo
import javax.inject.Inject

class GetFilmByIdUseCase @Inject constructor(
    private val repository: CinemaRepository
) {
    suspend fun executeFilmDetailInfoById(filmId: Int): FilmWithDetailInfo {
        return repository.getDetailInfoByFilm(filmId)
    }
}