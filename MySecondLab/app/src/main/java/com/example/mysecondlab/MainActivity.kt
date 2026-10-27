package com.example.mysecondlab

import android.os.Bundle
import android.util.Log
import com.example.mysecondlab.databinding.ActivityMainBinding
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.example.mysecondlab.fragments.ContentFragment
import com.example.mysecondlab.fragments.RegistrationFragment
import com.example.mysecondlab.fragments.SettingFragment
import com.example.mysecondlab.fragments.StartFragment


class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG ="MainActivity"
        private const val BACK_STACK = "MainBackStack"
    }
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        logLifecycle("onCreate")
        // Показываем стартовый фрагмент только при первом создании
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(binding.container.id, StartFragment(), "StartFragment")
                .commit()
        }

    }

    private fun logLifecycle(method: String) {
        Log.d(TAG, "$TAG: $method")
    }

    fun navigateToContent() {
        logLifecycle("navigateToContent")
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        supportFragmentManager.beginTransaction()
            .replace(binding.container.id, ContentFragment(), "ContentFragment")
            .commit()

    }

    fun navigateToRegister() {
        logLifecycle("navigateToRegistration")
        supportFragmentManager.beginTransaction()
            .replace(binding.container.id, RegistrationFragment(), "RegistrationFragment")
            .addToBackStack(BACK_STACK)
            .commit()
    }



    fun navigateToSettings() {
        logLifecycle("navigateToSettings")
        // Очищаем back stack: оставляем только Content → Settings
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        supportFragmentManager.beginTransaction()
            .replace(binding.container.id, SettingFragment(), "SettingFragment")
            .addToBackStack(BACK_STACK)   // ← теперь назад ведёт к ContentFragment
            .commit()
    }

    override fun onStart() { super.onStart(); logLifecycle("onStart") }
    override fun onResume() { super.onResume(); logLifecycle("onResume") }
    override fun onPause() { super.onPause(); logLifecycle("onPause") }
    override fun onStop() { super.onStop(); logLifecycle("onStop") }
    override fun onDestroy() { super.onDestroy(); logLifecycle("onDestroy") }
    override fun onRestart() { super.onRestart(); logLifecycle("onRestart") }

}

