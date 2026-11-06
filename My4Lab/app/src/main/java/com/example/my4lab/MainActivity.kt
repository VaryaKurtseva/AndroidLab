package com.example.my4lab


import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.example.my4lab.databinding.ActivityMainBinding
import com.example.my4lab.databinding.FragmentStartBinding
import com.example.my4lab.extensions.debugging

import com.example.my4lab.fragments.ContentFragment
import com.example.my4lab.fragments.RegistrationFragment
import com.example.my4lab.fragments.StartFragment


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
        // Показываем стартовый фрагмент только при первом создании
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(binding.container.id, StartFragment(), "StartFragment")
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

    fun navigateToRegister() {
        debugging("navigateToRegistration")
        supportFragmentManager.beginTransaction()
            .replace(binding.container.id, RegistrationFragment(), "RegistrationFragment")
            .addToBackStack(BACK_STACK)
            .commit()
    }





    override fun onStart() { super.onStart(); debugging("onStart") }
    override fun onResume() { super.onResume(); debugging("onResume") }
    override fun onPause() { super.onPause(); debugging("onPause") }
    override fun onStop() { super.onStop(); debugging("onStop") }
    override fun onDestroy() { super.onDestroy(); debugging("onDestroy") }
    override fun onRestart() { super.onRestart(); debugging("onRestart") }
}

