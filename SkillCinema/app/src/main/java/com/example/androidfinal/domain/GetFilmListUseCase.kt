package com.example.androidfinal.domain

import androidx.paging.PagingData
import com.example.androidfinal.data.CinemaRepository
import com.example.androidfinal.entity.FilmByFilter
import com.example.androidfinal.entity.ParamsFilterFilm
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class GetFilmListUseCase @Inject constructor(
    private val repository: CinemaRepository
) {
    fun executeFilmsByFilter(
        filters: StateFlow<ParamsFilterFilm>
    ): Flow<PagingData<FilmByFilter>> {
        return repository.getFilmsByFilter(filters)
    }
}