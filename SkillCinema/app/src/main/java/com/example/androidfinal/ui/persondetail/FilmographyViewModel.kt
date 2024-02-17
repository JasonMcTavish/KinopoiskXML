package com.example.androidfinal.ui.persondetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.androidfinal.db.model.FilmsShortInfo
import com.example.androidfinal.db.model.PersonFilms
import com.example.androidfinal.domain.GetPersonByIdUseCase
import com.example.androidfinal.ui.StateLoading
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class FilmographyViewModel @Inject constructor(
    private val getStaffByIdUseCase: GetPersonByIdUseCase
) : ViewModel() {
    private val _loadCurrentStaff = MutableStateFlow<StateLoading>(StateLoading.Default)
    val loadCurrentStaff = _loadCurrentStaff.asStateFlow()

    private lateinit var allFilmsByPerson: List<PersonFilms>

    private val _films = MutableStateFlow<List<FilmsShortInfo>>(emptyList())
    val films = _films.asStateFlow()

    fun getFilmsByPerson(personId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _loadCurrentStaff.value = StateLoading.Loading
                allFilmsByPerson = getStaffByIdUseCase.executeFilmsByPerson(personId)
                val startProfessionForFilmList =
                    allFilmsByPerson.first { it.professionKey != null }.professionKey
                if (startProfessionForFilmList != null) getFilmsByCurrentProfession(
                    startProfessionForFilmList
                )
                _loadCurrentStaff.value = StateLoading.Success
            } catch (e: Exception) {
                _loadCurrentStaff.value = StateLoading.Error(e.message.toString())
            }
        }
    }

    fun getFilmsByCurrentProfession(professionKey: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val filmsIdList: List<Int> = allFilmsByPerson
                .filter { it.professionKey == professionKey }
                .map { it.filmId }
            val tempFilmList = mutableListOf<FilmsShortInfo>()
            filmsIdList.forEach { id ->
                tempFilmList.add(getStaffByIdUseCase.executeFilmShortInfo(id))
            }
            _films.value = tempFilmList
        }
    }

    fun getAllFilmWithProfession() = allFilmsByPerson
}