package com.example.mysecondlab.fragments

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.mysecondlab.MainActivity
import com.example.mysecondlab.R
import com.example.mysecondlab.databinding.FragmentSettingBinding


import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import java.io.File

class SettingFragment : Fragment() {
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

    companion object { private const val TAG = "SettingFragment"}

    private var _binding : FragmentSettingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        logLifecycle("onViewCreated")

        toolbar = binding.toolbar
        etName = binding.etName
        etAge = binding.etAge
        toggleGender = binding.toggleGender
        tvBackupStatus = binding.tvBackupStatus
        btnCreateBackup = binding.btnCreateBackup
        btnDeleteBackup = binding.btnDeleteBackup
        btnRestoreBackup = binding.btnRestoreBackup
        btnSaveData = binding.btnSaveData
        btnBackToRecipes = binding.btnBackToRecipes
        sliderFont = binding.sliderFont
        switchTheme = binding.switchTheme


        toolbar.setNavigationOnClickListener {
            logLifecycle("Click back")

            (requireActivity() as MainActivity).navigateToContent()
        }

        btnSaveData.setOnClickListener {
            logLifecycle("Click save data")
            saveUserData()
        }
        btnCreateBackup.setOnClickListener {
            logLifecycle("Click create backup")
            createBackup()
        }
        btnDeleteBackup.setOnClickListener {
            logLifecycle("Click delete backup")
            deleteBackup()
        }
        btnRestoreBackup.setOnClickListener {
            logLifecycle("Click restore backup")
            restoreBackup()
        }
        btnBackToRecipes.setOnClickListener {
            logLifecycle("Click back to recipes")
            (requireActivity() as MainActivity).navigateToContent()
        }
        fillFieldsFromPrefs()
        updateBackupStatus()

        sliderFont.addOnSliderTouchListener(object :
            com.google.android.material.slider.Slider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: com.google.android.material.slider.Slider) {}
            override fun onStopTrackingTouch(slider: com.google.android.material.slider.Slider) {
                logLifecycle("Font size: ${slider.value}")
                applyFontSize(slider.value)
            }
        })

        switchTheme.setOnCheckedChangeListener { _, isChecked ->
            logLifecycle("Theme: $isChecked")
            applyTheme(isChecked)
        }

    }
    private fun saveUserData() {
        val name = etName.text.toString().trim()
        val age = etAge.text.toString().toIntOrNull() ?: 0
        val isMale = toggleGender.checkedButtonId == R.id.btn_man

        if (name.isBlank()) {
            Snackbar.make(binding.main, "Введите имя", Snackbar.LENGTH_LONG).show()
            return
        }
        if (age <= 0 || age > 120) {
            Snackbar.make(binding.main, "Введите корректный возраст", Snackbar.LENGTH_LONG).show()
            return
        }

        requireContext().getSharedPreferences("users_prefs", MODE_PRIVATE)
            .edit()
            .putString(StartFragment.NAME, name)
            .putInt("AGE", age)
            .putBoolean(StartFragment.GENDER, isMale)
            .apply()

        Snackbar.make(binding.main, "Данные сохранены", Snackbar.LENGTH_SHORT).show()
    }
    private fun createBackup() {
        val backup = "Meal1,Meal2,Meal3,Meal4,Meal5"
        try {
            requireContext().openFileOutput(backupFileName, MODE_PRIVATE)
                .use { it.write(backup.toByteArray()) }
            Snackbar.make(binding.main, "Резервная копия создана", Snackbar.LENGTH_SHORT).show()
            updateBackupStatus()
        } catch (e: Exception) {
            Snackbar.make(binding.main, "Ошибка: ${e.message}", Snackbar.LENGTH_LONG).show()
        }
    }

    private fun deleteBackup() {
        val file = File(requireContext().filesDir, backupFileName)
        if (file.exists()) {
            file.delete()
            Snackbar.make(binding.main, "Резервная копия удалена", Snackbar.LENGTH_SHORT).show()
            updateBackupStatus()
        }
    }
    private fun updateBackupStatus() {
        val file = File(requireContext().filesDir, backupFileName)
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
    private fun fillFieldsFromPrefs() {
        val prefs = requireContext().getSharedPreferences("user_prefs", MODE_PRIVATE)
        etName.setText(prefs.getString(StartFragment.NAME, ""))
        val age = prefs.getInt("AGE", 0)
        if (age > 0) etAge.setText(age.toString())
        val isMale = prefs.getBoolean(StartFragment.GENDER, true)
        toggleGender.check(if (isMale) R.id.btn_man else R.id.btn_woman)
    }

    private fun applyTheme(isDark: Boolean) {
        requireContext().getSharedPreferences("user_prefs", MODE_PRIVATE)
            .edit()
            .putBoolean("DARK_THEME", isDark)
            .apply()
        AppCompatDelegate.setDefaultNightMode(
            if (isDark) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }

    private fun applyFontSize(size: Float) {
        requireContext().getSharedPreferences("user_prefs", MODE_PRIVATE)
            .edit()
            .putFloat("FONT_SIZE", size)
            .apply()
        requireActivity().recreate()
    }

    private fun restoreBackup() {
        val file = File(requireContext().filesDir, backupFileName)
        if (!file.exists()) {
            Snackbar.make(binding.main, "Копия не найдена", Snackbar.LENGTH_LONG).show()
            return
        }
        try {
            val content = requireContext().openFileInput(backupFileName)
                .bufferedReader().use { it.readText() }
            Snackbar.make(binding.main, "Восстановлено: $content", Snackbar.LENGTH_LONG).show()
        } catch (e: Exception) {
            Snackbar.make(binding.main, "Ошибка: ${e.message}", Snackbar.LENGTH_LONG).show()
        }
    }



    // --- Логирование жизненного цикла ---
    private fun logLifecycle(method: String) {
        android.util.Log.d(SettingFragment.Companion.TAG, "${SettingFragment.Companion.TAG}: $method")
    }
    override fun onAttach(context: android.content.Context) {
        super.onAttach(context)
        logLifecycle("onAttach")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logLifecycle("onCreate")
    }

    override fun onStart() {
        super.onStart()
        logLifecycle("onStart")
    }

    override fun onResume() {
        super.onResume()
        logLifecycle("onResume")
    }

    override fun onPause() {
        super.onPause()
        logLifecycle("onPause")
    }

    override fun onStop() {
        super.onStop()
        logLifecycle("onStop")
    }



    override fun onDestroy() {
        super.onDestroy()
        logLifecycle("onDestroy")
    }

    override fun onDetach() {
        super.onDetach()
        logLifecycle("onDetach")
    }


}