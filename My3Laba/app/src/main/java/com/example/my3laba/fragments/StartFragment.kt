package com.example.my3laba.fragments

import android.app.Activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.my3laba.MainActivity
import com.example.my3laba.databinding.FragmentStartBinding
import com.example.my3laba.extensions.debugging
import com.google.android.material.snackbar.Snackbar


class StartFragment : Fragment() {
    companion object {
        const val LOGIN : String = "LOGIN"
        const val GENDER : String = "GENDER"

        const val NAME : String = "NAME"
        const val PASSWORD : String = "PASSWORD"
        private const val TAG = "StartFragment"
    }

    private var _binding : FragmentStartBinding? = null
    private val binding get() = _binding!!



    // Создаем пользовательский интерфейс
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        debugging("onViewCreated")
        
        //иницилизирую лаунчер до использования
        parentFragmentManager.setFragmentResultListener(
            "registration_result",
            viewLifecycleOwner
        ) {_, bundle ->
            useResult(bundle)
        }

        binding.btnLogin.setOnClickListener {
            debugging("Click login")

            openContentScreen()
        }

    }

    fun openContentScreen() {
        (requireActivity() as MainActivity).navigateToContent()
    }


    fun useResult(data: Bundle?) {
        if (data != null && data.containsKey(NAME)) {
            val name = data.getString(NAME) ?: "User"
            val gender = data.getBoolean(GENDER, false)
            val login = data.getString(LOGIN) ?: ""
            val password = data.getString(PASSWORD) ?: ""

            binding.groupFirstLogin.visibility = View.VISIBLE
            binding.groupReturning.visibility = View.GONE
            binding.login.setText(login)
            binding.password.requestFocus()

            requireContext().getSharedPreferences("users_prefs", Activity.MODE_PRIVATE)
                .edit()
                .putString(LOGIN, login)
                .putString(NAME, name)
                .putBoolean(GENDER, gender)
                .putString(PASSWORD, password)
                .apply()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // --- Логирование жизненного цикла ---

    override fun onAttach(context: android.content.Context) {
        super.onAttach(context)
        debugging("onAttach")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        debugging("onCreate")
    }

    override fun onStart() {
        super.onStart()
        debugging("onStart")
    }

    override fun onResume() {
        super.onResume()
        debugging("onResume")
    }

    override fun onPause() {
        super.onPause()
        debugging("onPause")
    }

    override fun onStop() {
        super.onStop()
        debugging("onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        debugging("onDestroy")
    }

    override fun onDetach() {
        super.onDetach()
        debugging("onDetach")
    }


}

