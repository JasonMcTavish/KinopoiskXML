package com.example.androidfinal.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import com.example.androidfinal.app.prepareToShow
import com.example.androidfinal.data.*
import com.example.androidfinal.db.model.FilmWithGenres
import com.example.androidfinal.domain.*
import com.example.androidfinal.entity.ParamsFilterFilm
import com.example.androidfinal.ui.StateLoading
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getTopFilmsUseCase: GetTopFilmsUseCase
) : ViewModel() {

    init {
        getFilmsByCategories()
    }

    private val _homePageList = MutableStateFlow<List<HomeList>>(emptyList())
    val homePageList = _homePageList.asStateFlow()

    private val _loadCategoryState = MutableStateFlow<StateLoading>(StateLoading.Default)
    val loadCategoryState = _loadCategoryState.asStateFlow()

    fun getFilmsByCategories() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _loadCategoryState.value = StateLoading.Loading
                val list = listOf(
                    HomeList(
                        category = CategoriesFilms.BEST,
                        filmList = getTopFilmsUseCase.executeTopFilms(
                            topType = TOP_TYPES.getValue(CategoriesFilms.BEST),
                            page = 1
                        ).prepareToShow(20)
                    ),
                    HomeList(
                        category = CategoriesFilms.PREMIERS,
                        filmList = getTopFilmsUseCase.executeTopFilms(
                            topType = CategoriesFilms.PREMIERS.name,
                            page = null
                        ).prepareToShow(20)
                    ),
                    HomeList(
                        category = CategoriesFilms.AWAIT,
                        filmList = getTopFilmsUseCase.executeTopFilms(
                            topType = TOP_TYPES.getValue(CategoriesFilms.AWAIT),
                            page = 1
                        ).prepareToShow(20)
                    ),
                    HomeList(
                        category = CategoriesFilms.POPULAR,
                        filmList = getTopFilmsUseCase.executeTopFilms(
                            topType = TOP_TYPES.getValue(CategoriesFilms.POPULAR),
                            page = 1
                        ).prepareToShow(20)
                    ),
                    HomeList(
                        category = CategoriesFilms.TV_SERIES,
                        filmList = getTopFilmsUseCase.executeTopFilms(
                            topType = TOP_TYPES.getValue(CategoriesFilms.TV_SERIES),
                            page = 1
                        ).prepareToShow(20)
                    )
                )
                _homePageList.value = list
                Log.d(TAG, "getFilmsByCategories: list : $list")
                _loadCategoryState.value = StateLoading.Success
            } catch (e: Throwable) {
                    _loadCategoryState.value = StateLoading.Error(e.message.toString())
                    Log.d(TAG, "getFilmsByCategories: ${e.message}")
            }
        }
    }

    companion object {
        data class HomeList(
            val category: CategoriesFilms,
            val filmList: List<FilmWithGenres>
        )
    }
}