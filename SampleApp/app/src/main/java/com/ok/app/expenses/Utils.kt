package com.ok.app.expenses

import android.content.Context
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import com.ok.app.R

/**
 * Created by Olga Kuzmina.
 */
@ColorInt
fun String.categoryColor(context: Context): Int? {
    return categoryColors[lowercase()]?.let { color -> ContextCompat.getColor(context, color) }
}

val categoryColors = mapOf(
    "clothes" to R.color.clothes,
    "cafe" to R.color.cafe,
    "fastfood" to R.color.fastfood,
    "farmacy" to R.color.farmacy,
    "grocery" to R.color.grocery,
    "income" to R.color.income
)