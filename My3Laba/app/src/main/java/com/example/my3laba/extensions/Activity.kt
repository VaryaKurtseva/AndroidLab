package com.example.my3laba.extensions


import android.util.Log
import androidx.appcompat.app.AppCompatActivity

const val DEBUG: Boolean = true
const val TAG: String = "TAG"

fun AppCompatActivity.debugging(message: String) {
    if (DEBUG) Log.d("${TAG}_${localClassName}", message)
}