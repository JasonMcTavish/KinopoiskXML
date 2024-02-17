package com.example.androidfinal.entity

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ResponseGenresCountries(
    @Json(name = "countries") val countries: List<FilterCountry>,
    @Json(name = "genres") val genres: List<FilterGenre>
)

@JsonClass(generateAdapter = true)
data class FilterCountry(
    @Json(name = "id") override val id: Int,
    @Json(name = "country") override val name: String
): FilterCountryGenre

@JsonClass(generateAdapter = true)
data class FilterGenre(
    @Json(name = "id") override val id: Int,
    @Json(name = "genre") override val name: String
) : FilterCountryGenre

@JsonClass(generateAdapter = true)
data class Genre(
    @Json(name = "genre") val genre: String
)

@JsonClass(generateAdapter = true)
data class Country(
    @Json(name = "country") val country: String
)