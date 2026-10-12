package com.example.mysecondlab

import android.os.Bundle
import android.util.Log
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        logLifecycle("onCreate")
        // Показываем стартовый фрагмент только при первом создании
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, StartFragment(), "StartFragment")
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
            .replace(R.id.container, ContentFragment(), "ContentFragment")
            .commit()

    }

    fun navigateToRegister() {
        logLifecycle("navigateToRegistration")
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, RegistrationFragment(), "RegistrationFragment")
            .addToBackStack(BACK_STACK)
            .commit()
    }



    fun navigateToSettings() {
        logLifecycle("navigateToSettings")
        // Очищаем back stack: оставляем только Content → Settings
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, SettingFragment(), "SettingFragment")
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

