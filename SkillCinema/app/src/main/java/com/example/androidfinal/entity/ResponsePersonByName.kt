package com.example.androidfinal.entity

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ResponsePersonByName(
    @Json(name = "items") val persons: List<ItemPerson>,
    @Json(name = "total") val total: Int
)

@JsonClass(generateAdapter = true)
data class ItemPerson(
    @Json(name = "kinopoiskId") val personId: Int,
    @Json(name = "nameRu") val nameRu: String,
    @Json(name = "nameEn") val nameEn: String,
    @Json(name = "posterUrl") val posterUrl: String,
)