package com.example.androidfinal.ui.filmdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.example.androidfinal.db.model.FilmMarkers
import com.example.androidfinal.db.model.FilmWithDetailInfo
import com.example.androidfinal.db.model.FilmWithGenres
import com.example.androidfinal.domain.GetFilmByIdUseCase
import com.example.androidfinal.domain.GetFilmMarkersUseCase
import com.example.androidfinal.domain.GetFilmsHistoryUseCase
import com.example.androidfinal.entity.BottomSheetItemDataModel
import com.example.androidfinal.entity.ParamsFilterGallery
import com.example.androidfinal.ui.StateLoading
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.io.path.createTempDirectory

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

    val collectionsList = getFilmsHistoryUseCase.executeCollectionsList()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(500L),
            initialValue = emptyList()
        )


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

    fun addNewCollection(name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            getFilmsHistoryUseCase.addNewCollection(name)
        }
    }

    fun addFIlmInCollection(filmId: Int, collectionName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            getFilmsHistoryUseCase.addNewFilmInCollections(filmId, collectionName)
        }
    }

    fun deleteFilmFromCollection(filmId: Int, collectionName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            getFilmsHistoryUseCase.deleteFilmFromCollections(filmId, collectionName)
        }
    }

    suspend fun checkFilmInCollection(collectionName: String, filmId: Int): Boolean {
        var temp = false
        viewModelScope.launch(Dispatchers.IO) {
            temp = getFilmsHistoryUseCase.checkFilmInCollection(collectionName, filmId) != 0
        }
        return temp
    }

    fun chechBSClick(item: BottomSheetItemDataModel, filmInFragment: FilmWithDetailInfo) {
        viewModelScope.launch(Dispatchers.IO) {
            val filmInCollection =
                checkFilmInCollection(item.collectionName, filmInFragment.film.filmId)

            if (filmInCollection && item.includesCurrentFilm) {
                deleteFilmFromCollection(filmInFragment.film.filmId, item.collectionName)
            }
            if (!filmInCollection && !item.includesCurrentFilm) {
                addFIlmInCollection(filmInFragment.film.filmId, item.collectionName)
            }
            item.includesCurrentFilm = !item.includesCurrentFilm
        }
    }

    companion object {
        private var currentParamsFilterGallery = ParamsFilterGallery(
            filmId = 328,
            galleryType = "STILL"
        )
    }
}