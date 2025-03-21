package com.example.androidfinal.ui.seasons

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidfinal.db.model.SeasonEpisode
import com.example.androidfinal.domain.GetSeasonsUseCase
import com.example.androidfinal.ui.StateLoading
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SeasonsViewModel @Inject constructor(
    private val getSeasonsUseCase: GetSeasonsUseCase
) : ViewModel() {
    private val _seasons = MutableStateFlow<List<SeasonEpisode>>(emptyList())
    val seasons = _seasons.asStateFlow()

    private val _loadCategoryState = MutableStateFlow<StateLoading>(StateLoading.Default)
    val loadCategoryState = _loadCategoryState.asStateFlow()

    fun getSeasons(seriesId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _loadCategoryState.value = StateLoading.Loading
            val temp: List<SeasonEpisode> = getSeasonsUseCase.executeSeasons(seriesId)
            _seasons.value = temp
            _loadCategoryState.value = StateLoading.Success
        }
    }
}