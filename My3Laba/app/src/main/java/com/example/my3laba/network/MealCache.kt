package com.example.my3laba.network

import android.content.Context
import android.content.SharedPreferences
import com.example.my3laba.model.MealDto
import kotlinx.serialization.json.Json

class MealCache(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("meals_cache", Context.MODE_PRIVATE)
    // Сохраняем список в кэш
    fun saveMeals(meals: List<MealDto>) {
        val jsonString = Json.encodeToString(meals)
        prefs.edit().putString("cached_meals", jsonString).apply()
    }
    // Загружаем список из кэша
    fun loadMeals(): List<MealDto> {
        val jsonString = prefs.getString("cached_meals", null) ?: return emptyList()
        return try {
            Json.decodeFromString<List<MealDto>>(jsonString)
        } catch (e: Exception) {
            emptyList() // Если данные битые, возвращаем пустой список
        }
    }




}