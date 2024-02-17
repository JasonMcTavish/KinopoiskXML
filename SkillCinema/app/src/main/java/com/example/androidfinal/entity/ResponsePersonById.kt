package com.example.androidfinal.entity

import com.example.androidfinal.db.model.PersonShortInfo
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ResponsePersonById(
    @Json(name = "personId") val personId: Int,
    @Json(name = "nameRu") val nameRu: String?,
    @Json(name = "nameEn") val nameEn: String?,
    @Json(name = "posterUrl") val posterUrl: String,
    @Json(name = "profession") val profession: String?,
    @Json(name = "films") val films: List<FilmsByPerson>?,

    )

@JsonClass(generateAdapter = true)
data class FilmsByPerson(
    @Json(name = "filmId") val filmId: Int,
    @Json(name = "nameRu") val nameRu: String?,
    @Json(name = "nameEn") val nameEn: String?,
    @Json(name = "rating") val rating: String?,
    @Json(name = "professionKey") val professionKey: String?,
    @Json(name = "general") val general: Boolean,
)

fun ResponsePersonById.convertToDb(): PersonShortInfo {
    return PersonShortInfo(
        personId = personId,
        name = nameRu ?: nameEn ?: "",
        poster = posterUrl,
        profession = profession
    )
}