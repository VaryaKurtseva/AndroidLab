package com.example.my3laba.model



data class Meal(
    val id: String,
    val name: String,
    val imageRes: Int,
    val category: String,
    val area: String?,
    val cookingTime: Int
)