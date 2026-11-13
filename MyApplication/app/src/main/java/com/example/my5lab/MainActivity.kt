package com.example.my5lab


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager


import com.example.my5lab.extensions.debugging

import com.example.my5lab.fragments.ContentFragment
import com.example.myapplication.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    companion object {
        private const val TAG ="MainActivity"
        private const val BACK_STACK = "MainBackStack"

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        debugging("onCreate")
        // Показываем фрагмент с рецептами сразу при запуске
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(binding.container.id, ContentFragment(), "ContentFragment") // <--- ИЗМЕНЕНО
                .commit()
        }
    }
    fun navigateToContent() {
        debugging("navigateToContent")
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        supportFragmentManager.beginTransaction()
            .replace(binding.container.id, ContentFragment(), "ContentFragment")
            .commit()

    }







    override fun onStart() { super.onStart(); debugging("onStart") }
    override fun onResume() { super.onResume(); debugging("onResume") }
    override fun onPause() { super.onPause(); debugging("onPause") }
    override fun onStop() { super.onStop(); debugging("onStop") }
    override fun onDestroy() { super.onDestroy(); debugging("onDestroy") }
    override fun onRestart() { super.onRestart(); debugging("onRestart") }
}

