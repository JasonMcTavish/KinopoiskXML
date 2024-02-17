package com.example.androidfinal.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.androidfinal.api.CinemaApi
import com.example.androidfinal.entity.ItemPerson
import com.example.androidfinal.entity.ParamsFilterFilm
import kotlinx.coroutines.flow.StateFlow

class SearchPersonsPagingSource(
    private val apiService: CinemaApi,
    private val personName: StateFlow<ParamsFilterFilm>
) : PagingSource<Int, ItemPerson>() {
    override fun getRefreshKey(state: PagingState<Int, ItemPerson>): Int = FIRST_PAGE

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ItemPerson> {
        val page = params.key ?: 1
        val pageSize = params.loadSize.coerceAtMost(20)
        return kotlin.runCatching {
            val result = apiService.getPersonByName(personName.value.keyword, page).persons
            result
        }.fold(
            onSuccess = {
                LoadResult.Page(
                    data = it,
                    prevKey = if (it.size < pageSize) null else page - 1,
                    nextKey = if (it.isEmpty()) null else page + 1
                )
            },
            onFailure = {
                LoadResult.Error(it)
            }
        )
    }

    private companion object {
        private const val FIRST_PAGE = 1
    }
}