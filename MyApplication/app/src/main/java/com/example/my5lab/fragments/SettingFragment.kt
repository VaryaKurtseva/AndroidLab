package com.example.my5lab.fragments

import android.content.ContentValues
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.SeekBar
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.my5lab.MainActivity
import com.example.my5lab.MobileApplication

import com.example.my5lab.storage.SettingsDSStore
import com.example.myapplication.R
import com.example.myapplication.databinding.FragmentSettingBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class SettingFragment : Fragment() {

    lateinit var toolbar: Toolbar
    lateinit var etName: EditText
    lateinit var etAge: EditText
    lateinit var toggleGender: RadioGroup
    lateinit var switchTheme: Switch
    lateinit var switchLanguage: Switch
    lateinit var sliderFont: SeekBar
    lateinit var tvBackupStatus: TextView
    lateinit var btnCreateBackup: Button
    lateinit var btnDeleteBackup: Button
    lateinit var btnRestoreBackup: Button
    lateinit var btnSaveData: Button
    lateinit var btnBackToRecipes: Button

    companion object {
        private const val TAG = "SettingFragment"
    }

    private val internalBackupFile: File by lazy {
        File(requireContext().filesDir, "backup_internal.txt")
    }

    private var _binding: FragmentSettingBinding? = null
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
        super.onViewCreated(view, savedInstanceState)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        logLifecycle("onViewCreated")

        // Инициализация View
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
        switchLanguage = binding.switchLanguage

        fillFieldsFromPrefs()
        updateBackupStatus()

        viewLifecycleOwner.lifecycleScope.launch {
            val ds = SettingsDSStore()
            val mode = ds.loadLightOrNightMode().first()
            switchTheme.isChecked = (mode == AppCompatDelegate.MODE_NIGHT_YES)
            val isRu = ds.isAlwaysRuLanguage().first()
            switchLanguage.isChecked = isRu
        }

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

        switchTheme.setOnCheckedChangeListener { _, isChecked ->
            logLifecycle("Theme: $isChecked")
            applyTheme(isChecked)
        }

        switchLanguage.setOnCheckedChangeListener { _, isChecked ->
            viewLifecycleOwner.lifecycleScope.launch {
                val ds = SettingsDSStore()
                ds.saveAlwaysRuLanguage(isChecked)
                MobileApplication.setLocaleAlwaysRu(isChecked)
            }
        }

        // Слушатель SeekBar (вместо Slider)
        sliderFont.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {}
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                logLifecycle("Font size: ${seekBar?.progress}")
                val newSize = 12f + (seekBar?.progress ?: 0) // 12 — минимальный, + progress
                applyFontSize(newSize)
            }
        })
    }

    private fun saveUserData() {
        val name = etName.text.toString().trim()
        val age = etAge.text.toString().toIntOrNull() ?: 0
        val isMale = toggleGender.checkedRadioButtonId == R.id.btn_man

        if (name.isBlank()) {
            Snackbar.make(binding.main, "Введите имя", Snackbar.LENGTH_LONG).show()
            return
        }
        if (age <= 0 || age > 120) {
            Snackbar.make(binding.main, "Введите корректный возраст", Snackbar.LENGTH_LONG).show()
            return
        }

        requireContext().getSharedPreferences("user_prefs", MODE_PRIVATE)
            .edit()
            .putString(StartFragment.NAME, name)
            .putInt("AGE", age)
            .putBoolean(StartFragment.GENDER, isMale)
            .apply()

        Snackbar.make(binding.main, "Данные сохранены", Snackbar.LENGTH_SHORT).show()
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
        viewLifecycleOwner.lifecycleScope.launch {
            val ds = SettingsDSStore()
            val mode = if (isDark) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
            ds.saveLightOrNightMode(mode)
        }
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

    // --- РАБОТА С ФАЙЛАМИ ---
    private fun createBackup() {
        val prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val json = prefs.getString("LAST_MEALS_JSON", null)
        if (json == null) {
            Snackbar.make(binding.main, "Нет данных для сохранения", Snackbar.LENGTH_LONG).show()
            return
        }
        val fileName = "backup_${System.currentTimeMillis()}.txt"
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val resolver = requireContext().contentResolver
            val values = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, "text/plain")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS)
            }
            val uri = resolver.insert(MediaStore.Files.getContentUri("external"), values)
            if (uri != null) {
                resolver.openOutputStream(uri)?.use { stream -> stream.write(json.toByteArray()) }
                prefs.edit().putString("LAST_BACKUP_URI", uri.toString()).apply()
                withContext(Dispatchers.Main) {
                    Snackbar.make(binding.main, "Файл сохранен в Documents", Snackbar.LENGTH_SHORT).show()
                    updateBackupStatus()
                }
            } else {
                withContext(Dispatchers.Main) {
                    Snackbar.make(binding.main, "Ошибка создания файла", Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun updateBackupStatus() {
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            val prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            val savedUriString = prefs.getString("LAST_BACKUP_URI", null)
            var fileInfo = "Файл не найден"
            var fileExists = false
            var publicFileUri: Uri? = null

            if (savedUriString != null) {
                publicFileUri = Uri.parse(savedUriString)
                val cursor = requireContext().contentResolver.query(
                    publicFileUri,
                    arrayOf(MediaStore.MediaColumns.DISPLAY_NAME, MediaStore.MediaColumns.SIZE, MediaStore.MediaColumns.DATE_ADDED),
                    null, null, null
                )
                cursor?.use {
                    if (it.moveToFirst()) {
                        fileExists = true
                        val name = it.getString(it.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME))
                        val size = it.getLong(it.getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE))
                        val date = it.getLong(it.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_ADDED))
                        fileInfo = "Файл: $name\nРазмер: ${size / 1024} КБ\nСоздан: ${java.util.Date(date * 1000)}"
                    }
                }
            }
            val internalExists = internalBackupFile.exists()
            withContext(Dispatchers.Main) {
                tvBackupStatus.text = fileInfo
                btnDeleteBackup.isEnabled = fileExists
                btnRestoreBackup.isEnabled = internalExists
                btnDeleteBackup.tag = publicFileUri
            }
        }
    }

    private fun deleteBackup() {
        val publicFileUri = btnDeleteBackup.tag as? Uri ?: return
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            try {
                requireContext().contentResolver.openInputStream(publicFileUri)?.use { input ->
                    internalBackupFile.outputStream().use { output -> input.copyTo(output) }
                }
                requireContext().contentResolver.delete(publicFileUri, null, null)
                withContext(Dispatchers.Main) {
                    Snackbar.make(binding.main, "Файл удален, копия сохранена", Snackbar.LENGTH_SHORT).show()
                    updateBackupStatus()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Snackbar.make(binding.main, "Ошибка: ${e.message}", Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun restoreBackup() {
        if (!internalBackupFile.exists()) return
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            try {
                val content = internalBackupFile.readText()
                val fileName = "backup_restored_${System.currentTimeMillis()}.txt"
                val resolver = requireContext().contentResolver
                val values = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "text/plain")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS)
                }
                val uri = resolver.insert(MediaStore.Files.getContentUri("external"), values)
                uri?.let {
                    resolver.openOutputStream(it)?.use { stream -> stream.write(content.toByteArray()) }
                    internalBackupFile.delete()
                    requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                        .edit().putString("LAST_BACKUP_URI", uri.toString()).apply()
                    withContext(Dispatchers.Main) {
                        Snackbar.make(binding.main, "Файл восстановлен", Snackbar.LENGTH_SHORT).show()
                        updateBackupStatus()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Snackbar.make(binding.main, "Ошибка восстановления: ${e.message}", Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }

    // --- ЛОГИРОВАНИЕ И ЖИЗНЕННЫЙ ЦИКЛ ---
    private fun logLifecycle(method: String) {
        android.util.Log.d(TAG, "$TAG: $method")
    }

    override fun onAttach(context: android.content.Context) {
        super.onAttach(context); logLifecycle("onAttach")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState); logLifecycle("onCreate")
    }

    override fun onStart() {
        super.onStart(); logLifecycle("onStart")
    }

    override fun onResume() {
        super.onResume(); logLifecycle("onResume")
    }

    override fun onPause() {
        super.onPause(); logLifecycle("onPause")
    }

    override fun onStop() {
        super.onStop(); logLifecycle("onStop")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        logLifecycle("onDestroyView")
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy(); logLifecycle("onDestroy")
    }

    override fun onDetach() {
        super.onDetach(); logLifecycle("onDetach")
    }
}