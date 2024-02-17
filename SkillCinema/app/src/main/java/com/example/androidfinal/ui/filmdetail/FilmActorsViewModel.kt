package com.example.androidfinal.ui.filmdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidfinal.db.model.FilmPersons
import com.example.androidfinal.domain.GetActorsListUseCase
import com.example.androidfinal.ui.StateLoading
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FilmActorsViewModel @Inject constructor(
    private val getActorsByFilmIdUseCase: GetActorsListUseCase
) : ViewModel() {

    private val _loadCategoryState = MutableStateFlow<StateLoading>(StateLoading.Default)
    val loadCategoryState = _loadCategoryState.asStateFlow()

    private val _persons = MutableStateFlow<List<FilmPersons>>(emptyList())
    val persons = _persons.asStateFlow()

    fun getStaffsByFilm(filmId: Int, professionKey: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _loadCategoryState.value = StateLoading.Loading
            val persons = getActorsByFilmIdUseCase.executePersonsList(filmId)

            val resultList = if (professionKey == "ACTOR") {
                persons.filter { it.professionKey == professionKey }
            } else {
                persons.filter { it.professionKey != professionKey && it.professionKey != "ACTOR" }
            }
            _persons.value = resultList
            _loadCategoryState.value = StateLoading.Success
        }
    }
}