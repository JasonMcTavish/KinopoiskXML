package com.example.androidfinal.domain

import androidx.paging.PagingData
import com.example.androidfinal.data.CinemaRepository
import com.example.androidfinal.entity.ItemPerson
import com.example.androidfinal.entity.ParamsFilterFilm
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class GetPersonByNameUseCase @Inject constructor(
    private val repository: CinemaRepository
) {
    fun executePerson(personName: StateFlow<ParamsFilterFilm>): Flow<PagingData<ItemPerson>> {
        return repository.getPersonsByName(personName)
    }
}