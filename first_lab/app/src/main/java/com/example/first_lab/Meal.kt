package com.example.first_lab

data class Meal(
    val id: String,
    val name: String,
    val imageRes: Int,
    val category: String,
    val area: String?,
    val cookingTime: Int
)