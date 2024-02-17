package com.example.androidfinal.ui.gallery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidfinal.db.model.FilmImage
import com.example.androidfinal.domain.GetGalleryByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GalleryFullscreenViewModel @Inject constructor(
    private val getGalleryByIdUseCase: GetGalleryByIdUseCase
) : ViewModel() {
    private val _images = MutableStateFlow<List<FilmImage>>(emptyList())
    val images = _images.asStateFlow()

    fun getGallery(filmId: Int, galleryType: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = getGalleryByIdUseCase.executeGalleryByFilmId(filmId)
            _images.value = response.filter { it.imageCategory == galleryType }
        }
    }
}