package com.example.first_lab

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.first_lab.extensions.debugging
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText


class StartActivity : AppCompatActivity() {

    companion object {
        const val LOGIN : String = "LOGIN"
        const val GENDER : String = "GENDER"

        const val NAME : String = "NAME"
        const val PASSWORD : String = "PASSWORD"
    }

    lateinit var resultLauncher : ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        enableEdgeToEdge()  // reed https://developer.android.com/develop/ui/views/layout/edge-to-edge

        setContentView(R.layout.activity_start)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        debugging("onCreate")

        val btnFinal : Button = findViewById(R.id.btn_login)
        btnFinal.setOnClickListener {
            debugging("Click to final")

            val loginField = findViewById<TextInputEditText>(R.id.login)
            val passwordField = findViewById<TextInputEditText>(R.id.password)

            val loginText = loginField.text.toString().trim()
            val passwordText = passwordField.text.toString()

            val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
            val savedPassword = prefs.getString(PASSWORD, null)
            val savedLogin = prefs.getString(LOGIN, null)

            if (savedPassword == null || savedLogin == null) {
                Snackbar.make(findViewById(R.id.main), "Сначала зарегистрируйтесь", Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if (passwordText != savedPassword) {
                Snackbar.make(findViewById(R.id.main), "Неверный пароль", Snackbar.LENGTH_LONG).show()
                return@setOnClickListener
            }

            openContentActivity()
        }

        val btnReturning : Button = findViewById(R.id.btn_go_to_recipes)
        btnReturning.setOnClickListener {
            debugging("Click to returning")
            openContentActivity()
        }
        val btnRegister : Button = findViewById(R.id.btn_register)
        btnRegister.setOnClickListener {
            debugging("Click to final")
            openActivityForResult()
        }

        // define before onStart()
        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                useResult(data)
            }
        }

    }

    private fun openContentActivity() {
        val intent = Intent(this, ContentActivity::class.java)
        startActivity(intent)
        finish()
    }



    private fun openActivityForResult(): Unit {
        val intent = Intent(this, RegistrationActivity::class.java)

//        startActivityForResult(intent, REQUEST)   // @Deprecated way
        resultLauncher.launch(intent)   // New way
    }

//    // @Deprecated
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        if (requestCode == REQUEST && resultCode == RESULT_OK){
//            useResult(data)
//        } else {
//            logToDebug("onActivityResult: request = $REQUEST, result = $resultCode" )
//        }
//        super.onActivityResult(requestCode, resultCode, data)
//    }

    private fun useResult(data: Intent?) : Unit {
        if (data != null && data.hasExtra(NAME)){

            val name = data.getStringExtra(NAME) ?: "User"
            val gender = data.getBooleanExtra(GENDER, false)
            val login = data.getStringExtra(LOGIN) ?: ""
            val password = data.getStringExtra(PASSWORD) ?: ""

            // 1. Показываем блок первого входа (с полями)
            findViewById<View>(R.id.group_first_login).visibility = View.VISIBLE
            findViewById<View>(R.id.group_returning).visibility = View.GONE

            // 2. Предзаполняем поле email
            val loginField = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.login)
            loginField.setText(login)

            val passwordField = findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.password)
            passwordField.requestFocus()

            getSharedPreferences("user_prefs", MODE_PRIVATE)
                .edit()
                .putString(LOGIN, login)
                .putString(NAME, name)
                .putBoolean(GENDER, gender)
                .putString(PASSWORD, password)
                .apply()





        }
    }
}