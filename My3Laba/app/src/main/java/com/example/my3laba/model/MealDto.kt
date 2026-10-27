package com.example.my3laba.model

import kotlinx.serialization.Serializable

@Serializable
data class MealResponse(
    val meals: List<MealDto>?
)

@Serializable
data class MealDto(val idMeal: String,
                   val strMeal: String,
                   val strCategory: String?,
                   val strArea: String?,)
