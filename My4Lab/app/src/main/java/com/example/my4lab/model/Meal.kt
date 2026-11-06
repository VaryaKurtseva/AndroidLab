package com.example.my4lab.model

import kotlinx.serialization.Serializable


@Serializable
data class Meal(
    val id: String,
    val name: String,
    val imageRes: Int,
    val category: String,
    val area: String?,
    val cookingTime: Int
)