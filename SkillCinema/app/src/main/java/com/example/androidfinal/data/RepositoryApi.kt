package com.example.androidfinal.data

import com.example.androidfinal.api.CinemaApi
import com.example.androidfinal.entity.ResponseGenresCountries
import javax.inject.Inject

class RepositoryAPI @Inject constructor(
    private val apiService: CinemaApi
) {
    suspend fun getGenresCountries(): ResponseGenresCountries = apiService.getGenresCountries()
}