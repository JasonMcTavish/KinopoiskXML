package com.example.androidfinal.domain

import com.example.androidfinal.data.RepositoryAPI
import com.example.androidfinal.entity.ResponseGenresCountries
import javax.inject.Inject

class GetGenresCountriesUseCase @Inject constructor(private val repositoryAPI: RepositoryAPI) {
    suspend fun executeGenresCountries(): ResponseGenresCountries {
        return repositoryAPI.getGenresCountries()
    }
}