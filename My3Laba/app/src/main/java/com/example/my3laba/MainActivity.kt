package com.example.my3laba


import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.example.my3laba.extensions.debugging

import com.example.my3laba.fragments.ContentFragment
import com.example.my3laba.fragments.StartFragment


class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG ="MainActivity"
        private const val BACK_STACK = "MainBackStack"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // в 3 не должно быть

        debugging("onCreate")
        // Показываем стартовый фрагмент только при первом создании
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, StartFragment(), "StartFragment")
                .commit()
        }

    }
    fun navigateToContent() {
        debugging("navigateToContent")
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, ContentFragment(), "ContentFragment")
            .commit()

    }





    override fun onStart() { super.onStart(); debugging("onStart") }
    override fun onResume() { super.onResume(); debugging("onResume") }
    override fun onPause() { super.onPause(); debugging("onPause") }
    override fun onStop() { super.onStop(); debugging("onStop") }
    override fun onDestroy() { super.onDestroy(); debugging("onDestroy") }
    override fun onRestart() { super.onRestart(); debugging("onRestart") }
}

