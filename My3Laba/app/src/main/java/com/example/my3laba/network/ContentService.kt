package com.example.my3laba.network

import io.ktor.utils.io.core.Closeable

interface ContentService<T>: Closeable {
    suspend fun getMeals(): List<T>
    suspend fun getMeals(count: Int): List<T>
}
