package com.example.androidfinal.data

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.androidfinal.api.CinemaApi
import com.example.androidfinal.entity.FilmByFilter
import com.example.androidfinal.entity.ParamsFilterFilm
import kotlinx.coroutines.flow.StateFlow

class SearchFilmsPagingSource(
    private val apiService: CinemaApi,
    private val filters: StateFlow<ParamsFilterFilm>
) : PagingSource<Int, FilmByFilter>() {
    override fun getRefreshKey(state: PagingState<Int, FilmByFilter>): Int = FIRST_PAGE

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, FilmByFilter> {
        val page = params.key ?: 1
        val pageSize = params.loadSize.coerceAtMost(20)
        return kotlin.runCatching {
            val response = apiService.getFilmsByFilter(
                countries = if (filters.value.countries.isNotEmpty()) filters.value.countries.keys.first()
                    .toString() else "",
                genres = if (filters.value.genres.isNotEmpty()) filters.value.genres.keys.first()
                    .toString() else "",
                order = filters.value.order.ifEmpty { "RATING" },
                type = filters.value.type.ifEmpty { "ALL" },
                ratingFrom = filters.value.ratingFrom,
                ratingTo = filters.value.ratingTo,
                yearFrom = filters.value.yearFrom,
                yearTo = filters.value.yearTo,
                keyword = filters.value.keyword,
                page = page
            )
            response
        }.fold(
            onSuccess = {
                LoadResult.Page(
                    data = it.films,
                    prevKey = if (it.films.size < pageSize) null else page - 1,
                    nextKey = if (it.films.isEmpty()) null else page + 1
                )
            },
            onFailure = {
                Log.d(TAG, "load: ${it.message}")
                LoadResult.Error(it) }
        )
    }

    private companion object {
        private const val FIRST_PAGE = 1
    }
}