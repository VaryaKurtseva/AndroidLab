package com.example.my4lab.extensions


import android.util.Log
import androidx.fragment.app.Fragment
import com.example.my4lab.BuildConfig


fun Fragment.debugging(message: String) {
    if (BuildConfig.DEBUG) Log.d("${TAG}_${javaClass.simpleName}", message)
}