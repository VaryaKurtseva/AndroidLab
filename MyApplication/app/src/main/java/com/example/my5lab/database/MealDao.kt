package com.example.my5lab.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MealDao {
    // Наблюдаемый запрос для реактивного обновления
    @Query("SELECT * FROM meals")
    fun getAllMealsFlow(): Flow<List<MealEntity>>

    // Обычный запрос для проверки "Холодного старта"
    @Query("SELECT * FROM meals")
    suspend fun getAllMeals(): List<MealEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeals(meals: List<MealEntity>)

    @Query("DELETE FROM meals")
    suspend fun clearAll()
}