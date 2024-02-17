package com.example.androidfinal.ui.filmdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidfinal.db.model.FilmMarkers
import com.example.androidfinal.db.model.FilmWithDetailInfo
import com.example.androidfinal.domain.GetFilmByIdUseCase
import com.example.androidfinal.domain.GetFilmMarkersUseCase
import com.example.androidfinal.domain.GetFilmsHistoryUseCase
import com.example.androidfinal.entity.ParamsFilterGallery
import com.example.androidfinal.ui.StateLoading
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FilmDetailViewModel @Inject constructor(
    private val getFilmByIdUseCase: GetFilmByIdUseCase,
    private val getFilmsHistoryUseCase: GetFilmsHistoryUseCase,
    private val getFilmMarkersUseCase: GetFilmMarkersUseCase
) : ViewModel() {

    private var currentFilmId: Int = 0

    private val _loadCurrentFilmState = MutableStateFlow<StateLoading>(StateLoading.Default)
    val loadCurrentFilmState = _loadCurrentFilmState.asStateFlow()

    private val _filmDetailInfo = MutableStateFlow<FilmWithDetailInfo?>(null)
    val filmDetailInfo = _filmDetailInfo.asStateFlow()

    fun getFilmById(filmId: Int) {
        currentFilmId = filmId

        updateParamsFilterGallery()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _loadCurrentFilmState.value = StateLoading.Loading
                getFilmsHistoryUseCase.addFilmToHistory(filmId)
                getFilmMarkersUseCase.addMarkers(filmId)
                val tempFilm: FilmWithDetailInfo =
                    getFilmByIdUseCase.executeFilmDetailInfoById(filmId)
                _filmDetailInfo.value = tempFilm
                _loadCurrentFilmState.value = StateLoading.Success
            } catch (e: Throwable) {
                _loadCurrentFilmState.value = StateLoading.Error(e.message.toString())
            }
        }
    }

    private fun updateParamsFilterGallery(
        filmId: Int = currentFilmId,
        galleryType: String = "STILL"
    ) {
        currentParamsFilterGallery =
            currentParamsFilterGallery.copy(filmId = filmId, galleryType = galleryType)
    }

    // work with database
    fun checkFilmInDB(filmId: Int): Flow<FilmMarkers?> {
        return getFilmMarkersUseCase.executeMarkersByFilm(filmId)
    }

    fun updateFilmMarkers(filmId: Int, isFavorite: Int, inCollection: Int, isViewed: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            getFilmMarkersUseCase.updateMarkers(filmId, isFavorite, inCollection, isViewed)
        }
    }

    companion object {
        private var currentParamsFilterGallery = ParamsFilterGallery(
            filmId = 328,
            galleryType = "STILL"
        )
    }
}