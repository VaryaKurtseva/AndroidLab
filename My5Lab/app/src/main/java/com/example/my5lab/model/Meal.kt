package com.example.my5lab.model

import kotlinx.serialization.Serializable

@Serializable
data class Meal(
    val id: String,
    val name: String,
    val imageRes: Int, // Пока оставляем Int, раз у вас не подключен Coil
    val category: String,
    val area: String?,
    val cookingTime: Int
)