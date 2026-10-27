package com.example.my3laba.extensions


import android.util.Log
import androidx.fragment.app.Fragment
import com.example.my3laba.BuildConfig


fun Fragment.debugging(message: String) {
    if (BuildConfig.DEBUG) Log.d("${TAG}_${javaClass.simpleName}", message)
}