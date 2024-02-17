package com.example.androidfinal.domain

import com.example.androidfinal.data.CinemaRepository
import javax.inject.Inject

class GetSeasonsUseCase @Inject constructor(private val repository: CinemaRepository) {
    fun executeSeasons(seriesId: Int) = repository.getSeasonsById(seriesId)
}