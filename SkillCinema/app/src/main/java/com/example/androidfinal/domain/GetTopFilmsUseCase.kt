package com.example.androidfinal.domain

import androidx.paging.PagingData
import com.example.androidfinal.data.CinemaRepository
import com.example.androidfinal.db.model.FilmWithGenres
import com.example.androidfinal.entity.ParamsFilterFilm
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTopFilmsUseCase @Inject constructor(
    private val repository: CinemaRepository
) {

    suspend fun executeTopFilms(
        topType: String,
        page: Int? = 1,
    ): List<FilmWithGenres> {
        return repository.getFilmsTopByCategoryList(topType, page)
    }

    fun executeTopFilmsPaging(categoryName: String): Flow<PagingData<FilmWithGenres>> {
        return repository.getFilmsTopByCategoryPaging(categoryName)
    }
}