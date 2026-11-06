package com.example.my5lab.network

import io.ktor.utils.io.core.Closeable

interface ContentService<T>: Closeable {
    suspend fun getMeals(): List<T>
    suspend fun getMeals(count: Int): List<T>
}
