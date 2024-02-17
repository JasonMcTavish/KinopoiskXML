package com.example.androidfinal.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.androidfinal.domain.GetFilmListUseCase
import com.example.androidfinal.domain.GetGenresCountriesUseCase
import com.example.androidfinal.domain.GetPersonByNameUseCase
import com.example.androidfinal.entity.FilmByFilter
import com.example.androidfinal.entity.FilterCountry
import com.example.androidfinal.entity.FilterCountryGenre
import com.example.androidfinal.entity.FilterGenre
import com.example.androidfinal.entity.ItemPerson
import com.example.androidfinal.entity.ParamsFilterFilm
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val getGenresCountriesUseCase: GetGenresCountriesUseCase,
    getFilmListUseCase: GetFilmListUseCase,
    getPersonByNameUseCase: GetPersonByNameUseCase
) : ViewModel() {

    private val _filterFlow = MutableStateFlow(ParamsFilterFilm())
    val filterFlow = _filterFlow.asStateFlow()

    private lateinit var countriesList: List<FilterCountry>
    private lateinit var genresList: List<FilterGenre>

    private val _filterValuesCountriesGenres =
        MutableStateFlow<List<FilterCountryGenre>>(emptyList())
    val filterValuesCountriesGenres = _filterValuesCountriesGenres.asStateFlow()

    val newFilms: Flow<PagingData<FilmByFilter>> = getFilmListUseCase
        .executeFilmsByFilter(filters = _filterFlow).cachedIn(viewModelScope)

    val persons: Flow<PagingData<ItemPerson>> = getPersonByNameUseCase
        .executePerson(personName = _filterFlow).cachedIn(viewModelScope)

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val response = getGenresCountriesUseCase.executeGenresCountries()
            countriesList = response.countries.sortedBy { it.name }.filter { it.name.isNotEmpty() }
            genresList = response.genres.sortedBy { it.name }.filter { it.name.isNotEmpty() }
        }
    }

    fun getFiltersFull() = _filterFlow.value

    fun updateFiltersFull(filterFilm: ParamsFilterFilm) {
        viewModelScope.launch {
            if (_filterFlow.value != filterFilm) _filterFlow.value = filterFilm
        }
    }

    fun updateFilterCountriesGenres(type: String, keyword: String) {
        _filterValuesCountriesGenres.value = when (type) {
            KEY_COUNTRY -> {
                countriesList.filter { it.name.startsWith(keyword, ignoreCase = true) }
            }

            KEY_GENRE -> {
                genresList.filter { it.name.startsWith(keyword, ignoreCase = true) }
            }

            else -> {
                emptyList()
            }
        }
    }

    fun setFilterValues(filterType: String) {
        when (filterType) {
            KEY_COUNTRY -> _filterValuesCountriesGenres.value = countriesList
            KEY_GENRE -> _filterValuesCountriesGenres.value = genresList
        }
    }

    companion object {
        private const val KEY_COUNTRY = "country"
        private const val KEY_GENRE = "genre"
    }
}