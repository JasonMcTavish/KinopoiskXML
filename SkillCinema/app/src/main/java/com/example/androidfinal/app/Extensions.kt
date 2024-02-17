package com.example.androidfinal.app

import android.content.Context
import android.graphics.RectF
import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.androidfinal.R
import com.example.androidfinal.data.Month

fun <T> List<T>.prepareToShow(size: Int): List<T> {
    val resultList = mutableListOf<T>()

    if (this.size <= size) {
        this.forEach { resultList.add(it) }
    } else {
        repeat(size) {
            resultList.add(this[it])
        }
    }
    resultList.add(this[0])
    return resultList.toList()
}

fun ImageView.loadImage(imageUrl: String) {
    Glide
        .with(this)
        .load(imageUrl)
        .placeholder(R.drawable.no_poster)
        .into(this)
}

fun Int.converterInMonth(): String {
    var textMonth = ""
    if (this <= 0 || this > 12)
        textMonth = Month.AUGUST.name
    else
        Month.values().forEach { month ->
            if (this == month.count) textMonth = month.name
        }
    return textMonth
}

