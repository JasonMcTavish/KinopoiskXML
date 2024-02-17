package com.example.androidfinal.ui.allfilms

import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import com.example.androidfinal.data.CategoriesFilms
import com.example.androidfinal.data.TOP_TYPES
import com.example.androidfinal.db.model.FilmWithGenres
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import com.example.androidfinal.domain.GetTopFilmsUseCase
import javax.inject.Inject

@HiltViewModel
class AllFilmsViewModel @Inject constructor(
    private val getTopFilmsUseCase: GetTopFilmsUseCase
) : ViewModel() {

    lateinit var allFilmsByCategory: Flow<PagingData<FilmWithGenres>>

    fun setCurrentCategory(category: CategoriesFilms) {
        val categoryForRequest =
            if (category.name == CategoriesFilms.PREMIERS.name) category.name
            else TOP_TYPES.getValue(category)

        allFilmsByCategory = getTopFilmsUseCase
            .executeTopFilmsPaging(categoryName = categoryForRequest)
    }
}