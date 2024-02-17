package com.example.androidfinal.domain

import com.example.androidfinal.data.CinemaRepository
import javax.inject.Inject

class GetGalleryByIdUseCase @Inject constructor(
    private val repository: CinemaRepository
) {
    suspend fun executeGalleryByFilmId(filmId: Int) =
        repository.getFilmGallery(filmId)
}