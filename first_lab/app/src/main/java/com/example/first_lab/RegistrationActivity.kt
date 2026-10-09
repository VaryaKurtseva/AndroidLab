package com.example.first_lab
import android.content.Intent                    // для передачи данных между Activity
import android.os.Bundle                         // для onCreate
import android.widget.Button                     // для кнопки
import androidx.activity.enableEdgeToEdge        // для современного режима отображения
import androidx.appcompat.app.AppCompatActivity  // базовый класс Activity
import androidx.core.view.ViewCompat             // для работы с отступами (WindowInsets)
import androidx.core.view.WindowInsetsCompat     // для системных панелей
import com.example.first_lab.extensions.debugging
import com.google.android.material.button.MaterialButtonToggleGroup // переключатель Mr./Mrs.
import com.google.android.material.snackbar.Snackbar                 // всплывающие сообщения
import com.google.android.material.textfield.TextInputEditText       // поля ввода

class RegistrationActivity : AppCompatActivity() {

    lateinit var login: TextInputEditText
    lateinit var password: TextInputEditText
    lateinit var confirmPassword: TextInputEditText
    lateinit var name: TextInputEditText
    lateinit var toggle: MaterialButtonToggleGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_registration)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        debugging("onCreate")

        login = findViewById(R.id.login)
        password = findViewById(R.id.password)
        confirmPassword = findViewById(R.id.confirm_password)
        name = findViewById(R.id.name)
        toggle = findViewById(R.id.toggle_button)

        val btnRegister: Button = findViewById(R.id.btn_register)
        btnRegister.setOnClickListener {
            debugging("Click to register")
            returnToActivity()
        }
    }
    private fun returnToActivity() {
        val loginText = login.text.toString().trim()
        val passwordText = password.text.toString()
        val confirmText = confirmPassword.text.toString()
        val nameText = name.text.toString().trim()


        if (loginText.isBlank() || !loginText.contains("@")) {
            Snackbar.make(findViewById(R.id.main), "Введите корректный email", Snackbar.LENGTH_LONG).show()
            return
        }


        if (passwordText.length < 6) {
            Snackbar.make(findViewById(R.id.main), "Пароль должен быть не короче 6 символов", Snackbar.LENGTH_LONG).show()
            return
        }


        if (passwordText != confirmText) {
            Snackbar.make(findViewById(R.id.main), "Пароли не совпадают", Snackbar.LENGTH_LONG).show()
            return
        }


        if (nameText.isBlank()) {
            Snackbar.make(findViewById(R.id.main), "Введите имя", Snackbar.LENGTH_LONG).show()
            return
        }


        if (toggle.checkedButtonId == R.id.btn_not_defined) {
            Snackbar.make(findViewById(R.id.main), "Выберите пол", Snackbar.LENGTH_LONG).show()
            return
        }

        // Всё ок — возвращаем результат
        val intent = Intent().apply {
            putExtra(StartActivity.LOGIN, loginText)
            putExtra(StartActivity.GENDER, toggle.checkedButtonId == R.id.btn_man)
            putExtra(StartActivity.NAME, nameText)
            putExtra(StartActivity.PASSWORD, passwordText)
        }
        setResult(RESULT_OK, intent)
        finish()
    }
}
