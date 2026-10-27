package com.example.my3laba.network

import android.content.Context
import android.util.Log
import com.example.my3laba.model.MealDto
import com.example.my3laba.model.MealResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.request.get
import io.ktor.client.request.parameter

import io.ktor.client.statement.HttpResponse

import io.ktor.http.URLProtocol

import io.ktor.http.path
import io.ktor.serialization.kotlinx.json.json

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.security.KeyStore

class ApiMealClient(context: Context) : ContentService<MealDto>{

    private val cache = MealCache(context)

    private val client: HttpClient by lazy {
        HttpClient(Android){
            defaultRequest {
                url{
                    protocol = URLProtocol.HTTPS
                    host="www.themealdb.com"
                    path("api/json/v1/1/search.php")
                }
            }

            install(ContentNegotiation){
                json(Json {
                    isLenient=true
                    prettyPrint= true
                    explicitNulls=false
                    ignoreUnknownKeys=true
                })
            }

            install(HttpRequestRetry) {
                retryOnServerErrors(maxRetries = 3)
                exponentialDelay()
            }
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.HEADERS
            }
        }
    }

    override fun close(){
        client.close()
    }


    private suspend fun fetchMealsFromNetwork(query: String): List<MealDto> {
        return withContext(Dispatchers.IO) {
            try {
                val response: HttpResponse = client.get {
                    parameter("f", query)
                }
                val meals = response.body<MealResponse>().meals ?: emptyList()

                // Если данные пришли, сохраняем их в кэш!
                if (meals.isNotEmpty()) {
                    cache.saveMeals(meals)
                }
                meals
            } catch (e: Exception) {
                Log.e("TAG_APIKtorClient", "Network error: ${e.message}")
                // Если произошла ошибка (нет интернета), берем данные из кэша
                cache.loadMeals()
            }
        }
    }
    override suspend fun getMeals(): List<MealDto> {
        // Загружаем первую букву 'a'
        return fetchMealsFromNetwork("a")
    }

    private val alphabet = ('a'..'z').toList()
    private var currentLetterIndex = 0

    override suspend fun getMeals(count: Int): List<MealDto> {
        return withContext(Dispatchers.IO) {
            val letter = alphabet[currentLetterIndex % alphabet.size].toString()
            currentLetterIndex++

            val response: HttpResponse = client.get {
                parameter("f", letter)
            }
            (response.body<MealResponse>().meals ?: emptyList())
        }

    }
}