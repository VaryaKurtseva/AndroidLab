package com.example.first_lab



import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.first_lab.extensions.debugging
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import java.io.File

class SettingsActivity : AppCompatActivity() {

    lateinit var toolbar: MaterialToolbar
    lateinit var etName: TextInputEditText
    lateinit var etAge: TextInputEditText
    lateinit var toggleGender: MaterialButtonToggleGroup
    lateinit var switchTheme: MaterialSwitch
    lateinit var sliderFont: com.google.android.material.slider.Slider
    lateinit var tvBackupStatus: TextView
    lateinit var btnCreateBackup: Button
    lateinit var btnDeleteBackup: Button
    lateinit var btnRestoreBackup: Button
    lateinit var btnSaveData: Button
    lateinit var btnBackToRecipes: Button

    private val backupFileName = "backup.txt"

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        debugging("onCreate")


        toolbar = findViewById(R.id.toolbar)
        etName = findViewById(R.id.et_name)
        etAge = findViewById(R.id.et_age)
        toggleGender = findViewById(R.id.toggle_gender)
        switchTheme = findViewById(R.id.switch_theme)
        sliderFont = findViewById(R.id.slider_font)
        tvBackupStatus = findViewById(R.id.tv_backup_status)
        btnCreateBackup = findViewById(R.id.btn_create_backup)
        btnDeleteBackup = findViewById(R.id.btn_delete_backup)
        btnRestoreBackup = findViewById(R.id.btn_restore_backup)
        btnSaveData = findViewById(R.id.btn_save_data)
        btnBackToRecipes = findViewById(R.id.btn_back_to_recipes)


        toolbar.setNavigationOnClickListener {
            debugging("Click back")
            goBackToContent()
        }


        fillFieldsFromPrefs()


        btnSaveData.setOnClickListener {
            debugging("Click save data")
            saveUserData()
        }
        btnCreateBackup.setOnClickListener {
            debugging("Click create backup")
            createBackup()
        }
        btnDeleteBackup.setOnClickListener {
            debugging("Click delete backup")
            deleteBackup()
        }
        btnRestoreBackup.setOnClickListener {
            debugging("Click restore backup")
            restoreBackup()
        }
        btnBackToRecipes.setOnClickListener {
            debugging("Click back to recipes")
            goBackToContent()
        }




        sliderFont.addOnSliderTouchListener(object : com.google.android.material.slider.Slider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: com.google.android.material.slider.Slider) {
                // палец коснулся — ничего не делаем
            }
            override fun onStopTrackingTouch(slider: com.google.android.material.slider.Slider) {
                // палец отпущен — применяем
                debugging("Font size: ${slider.value}")
                applyFontSize(slider.value)
            }
        })


        updateBackupStatus()
    }



    private fun fillFieldsFromPrefs() {
        val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        etName.setText(prefs.getString(StartActivity.NAME, ""))
        val age = prefs.getInt("AGE", 0)
        if (age > 0) etAge.setText(age.toString())
        val isMale = prefs.getBoolean(StartActivity.GENDER, true)
        toggleGender.check(if (isMale) R.id.btn_man else R.id.btn_woman)
    }

    private fun saveUserData() {
        val name = etName.text.toString().trim()
        val age = etAge.text.toString().toIntOrNull() ?: 0
        val isMale = toggleGender.checkedButtonId == R.id.btn_man

        if (name.isBlank()) {
            Snackbar.make(findViewById(R.id.main), "Введите имя", Snackbar.LENGTH_LONG).show()
            return
        }
        if (age <= 0 || age > 120) {
            Snackbar.make(findViewById(R.id.main), "Введите корректный возраст", Snackbar.LENGTH_LONG).show()
            return
        }

        getSharedPreferences("user_prefs", MODE_PRIVATE)
            .edit()
            .putString(StartActivity.NAME, name)
            .putInt("AGE", age)
            .putBoolean(StartActivity.GENDER, isMale)
            .apply()

        Snackbar.make(findViewById(R.id.main), "Данные сохранены", Snackbar.LENGTH_SHORT).show()
    }


    private fun applyTheme(isDark: Boolean) {
        getSharedPreferences("user_prefs", MODE_PRIVATE)
            .edit()
            .putBoolean("DARK_THEME", isDark)
            .apply()

        AppCompatDelegate.setDefaultNightMode(
            if (isDark) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
    private fun applyFontSize(size: Float) {
        getSharedPreferences("user_prefs", MODE_PRIVATE)
            .edit()
            .putFloat("FONT_SIZE", size)
            .apply()
        recreate()   // теперь вызывается один раз, а не 20
    }




    private fun createBackup() {
        val backup = "Meal1,Meal2,Meal3,Meal4,Meal5"
        try {
            openFileOutput(backupFileName, MODE_PRIVATE).use { it.write(backup.toByteArray()) }
            Snackbar.make(findViewById(R.id.main), "Резервная копия создана", Snackbar.LENGTH_SHORT).show()
            updateBackupStatus()
        } catch (e: Exception) {
            Snackbar.make(findViewById(R.id.main), "Ошибка: ${e.message}", Snackbar.LENGTH_LONG).show()
        }
    }

    private fun deleteBackup() {
        val file = File(filesDir, backupFileName)
        if (file.exists()) {
            file.delete()
            Snackbar.make(findViewById(R.id.main), "Резервная копия удалена", Snackbar.LENGTH_SHORT).show()
            updateBackupStatus()
        }
    }

    private fun restoreBackup() {
        val file = File(filesDir, backupFileName)
        if (!file.exists()) {
            Snackbar.make(findViewById(R.id.main), "Копия не найдена", Snackbar.LENGTH_LONG).show()
            return
        }
        try {
            val content = openFileInput(backupFileName).bufferedReader().use { it.readText() }
            Snackbar.make(findViewById(R.id.main), "Восстановлено: $content", Snackbar.LENGTH_LONG).show()
        } catch (e: Exception) {
            Snackbar.make(findViewById(R.id.main), "Ошибка: ${e.message}", Snackbar.LENGTH_LONG).show()
        }
    }

    private fun updateBackupStatus() {
        val file = File(filesDir, backupFileName)
        val exists = file.exists()

        if (exists) {
            tvBackupStatus.text = "Копия от ${file.lastModified()}"
        } else {
            tvBackupStatus.text = "Резервная копия не найдена"
        }


        btnDeleteBackup.isEnabled = exists
        btnRestoreBackup.isEnabled = exists
        btnCreateBackup.isEnabled = true
    }



    private fun goBackToContent() {
        debugging("Back to content")
        val intent = Intent(this, ContentActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
        finish()
    }
}