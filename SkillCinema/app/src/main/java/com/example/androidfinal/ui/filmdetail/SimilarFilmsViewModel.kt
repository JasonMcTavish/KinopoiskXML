package com.example.androidfinal.ui.filmdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidfinal.db.model.FilmSimilar
import com.example.androidfinal.domain.GetFilmByIdUseCase
import com.example.androidfinal.ui.StateLoading
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SimilarFilmsViewModel @Inject constructor(
    private val getFilmByIdUseCase: GetFilmByIdUseCase
) : ViewModel() {
    private val _currentFilmSimilar = MutableStateFlow<List<FilmSimilar>>(emptyList())
    val currentFilmSimilar = _currentFilmSimilar.asStateFlow()

    private val _loadCategoryState = MutableStateFlow<StateLoading>(StateLoading.Default)
    val loadCategoryState = _loadCategoryState.asStateFlow()

    fun getSimilarFilms(filmId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _loadCategoryState.value = StateLoading.Loading
            val response = getFilmByIdUseCase.executeFilmDetailInfoById(filmId).similar
            if (!response.isNullOrEmpty()) _currentFilmSimilar.value = response
            _loadCategoryState.value = StateLoading.Success
        }
    }
}