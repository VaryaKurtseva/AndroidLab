package com.example.mysecondlab.fragments

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.mysecondlab.R
import com.example.mysecondlab.databinding.FragmentRegisterBinding
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText

class RegistrationFragment : Fragment(){

    lateinit var login: TextInputEditText
    lateinit var password: TextInputEditText
    lateinit var confirmPassword: TextInputEditText
    lateinit var name: TextInputEditText
    lateinit var toggle: MaterialButtonToggleGroup
    companion object { private const val TAG = "RegisterFragment"}

    private var _binding : FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    // Создаем пользовательский интерфейс
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        logLifecycle("onViewCreated")

        login = binding.login
        password = binding.password
        confirmPassword = binding.confirmPassword
        name = binding.name
        toggle = binding.toggleButton

        binding.btnRegister.setOnClickListener {
            logLifecycle("Click to register")
            returnToActivity()
        }

    }
    private fun returnToActivity() {
        val loginText = login.text.toString().trim()
        val passwordText = password.text.toString()
        val confirmText = confirmPassword.text.toString()
        val nameText = name.text.toString().trim()

        if (loginText.isBlank() || !loginText.contains("@")) {
            Snackbar.make(binding.main, "Введите корректный email", Snackbar.LENGTH_LONG).show()
            return
        }

        if (passwordText.length < 6) {
            Snackbar.make(binding.main, "Пароль должен быть не короче 6 символов", Snackbar.LENGTH_LONG).show()
            return
        }


        if (passwordText != confirmText) {
            Snackbar.make(binding.main, "Пароли не совпадают", Snackbar.LENGTH_LONG).show()
            return
        }


        if (nameText.isBlank()) {
            Snackbar.make(binding.main, "Введите имя", Snackbar.LENGTH_LONG).show()
            return
        }


        if (toggle.checkedButtonId == R.id.btn_not_defined) {
            Snackbar.make(binding.main, "Выберите пол", Snackbar.LENGTH_LONG).show()
            return
        }
        val intent = Bundle().apply {
            putString(StartFragment.LOGIN, loginText)
            putBoolean(StartFragment.GENDER, toggle.checkedButtonId == R.id.btn_man)
            putString(StartFragment.NAME, nameText)
            putString(StartFragment.PASSWORD, passwordText)
        }
        parentFragmentManager.setFragmentResult("registration_result", intent)

        // возврат на старт
        parentFragmentManager.popBackStack()


    }

    private fun logLifecycle(method: String) {
        android.util.Log.d(TAG, "$TAG: $method")
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

    override fun onDestroyView() {
        super.onDestroyView()
        logLifecycle("onDestroyView")
        _binding = null
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