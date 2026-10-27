package com.example.my3laba.fragments


import android.R.id.message
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar

import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.my3laba.MainActivity
import com.example.my3laba.R
import com.example.my3laba.databinding.FragmentContentBinding
import com.example.my3laba.extensions.debugging

import com.example.my3laba.model.Meal
import com.example.my3laba.model.MealAdapter
import com.example.my3laba.network.ApiMealClient
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import kotlinx.io.IOException


class ContentFragment : Fragment() {

    private val apiClient by lazy {ApiMealClient(requireContext().applicationContext)}
    lateinit var recyclerMeals : RecyclerView
    lateinit var adapter : MealAdapter
    lateinit var btnLoadMore : Button
    lateinit var progressMore : ProgressBar
    lateinit var stateLoading : View
    lateinit var stateEmpty: View

    private var allMeals = mutableListOf<Meal>()

    private var visibleCount = 0
    private val pageSize = 10
    companion object { private const val TAG = "ContentFragment"}

    private var _binding : FragmentContentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContentBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        ViewCompat.setOnApplyWindowInsetsListener(binding.main) {v,insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        debugging("onViewCreated")

        recyclerMeals = binding.recyclerMeals
        btnLoadMore = binding.btnLoadMore
        progressMore = binding.progressMore
        stateLoading = binding.stateLoading
        stateEmpty = binding.stateEmpty

        val toolbar : MaterialToolbar = binding.toolbar
        toolbar.title = "Рецепты"
        toolbar.inflateMenu(R.menu.menu_content)


        adapter = MealAdapter() {meal ->
            Snackbar.make(binding.main, "Выбрано: ${meal.name}", Snackbar.LENGTH_SHORT).show()
        }
        recyclerMeals.layoutManager = GridLayoutManager(requireContext(), 2)
        recyclerMeals.adapter = adapter

        btnLoadMore.setOnClickListener {
            debugging("Click load more")
            loadMore()
        }
        fetchMeals(isFirstPage=true)

    }
    private fun fetchMeals(isFirstPage: Boolean){
        if(isFirstPage) {
            stateLoading.visibility = View.VISIBLE
            recyclerMeals.visibility = View.GONE
            stateEmpty.visibility = View.GONE
            btnLoadMore.visibility = View.GONE
        }else {
            progressMore.visibility = View.VISIBLE
            btnLoadMore.visibility = View.GONE
        }

        viewLifecycleOwner.lifecycleScope.launch{
            try {
                val newDtos = if (isFirstPage) {
                    apiClient.getMeals()
                } else{
                    apiClient.getMeals(pageSize)
                }

                val newMeals = newDtos.map {dto ->
                    Meal(
                        id = dto.idMeal,
                        name= dto.strMeal,
                        imageRes = R.mipmap.ic_launcher,
                        category = dto.strCategory ?: "Unknow",
                        area = dto.strArea,
                        cookingTime = 0
                    )
                }
                if (isFirstPage) {
                    allMeals.clear()
                    allMeals.addAll(newMeals)
                } else {
                    allMeals.addAll(newMeals)

                }
                showSuccess()
            } catch (e: IOException) {
                // Ошибка сети (нет интернета)
                handleError("Нет подключения к интернету")
            } catch (e: Exception) {
                // Любая другая ошибка (ошибка парсинга, 404 и т.д.)
                handleError("Ошибка загрузки: ${e.message}")
            } finally {
                // Прячем индикаторы загрузки в любом случае
                stateLoading.visibility = View.GONE
                progressMore.visibility = View.GONE
            }
        
        }


    }

    fun handleError(string: String) {
        if (allMeals.isEmpty()) {
            showEmpty() // Если данных вообще нет, показываем "Пусто"
        } else {
            // Если данные уже есть, просто показываем ошибку внизу
            Snackbar.make(binding.main, string, Snackbar.LENGTH_LONG).show()
            btnLoadMore.visibility = View.VISIBLE
        }
    }


    private fun showSuccess() {
        stateLoading.visibility = View.GONE
        stateEmpty.visibility = View.GONE
        recyclerMeals.visibility = View.VISIBLE

        adapter.submitList(allMeals.toList()) // Передаем копию списка

        // Показываем кнопку, если сервер вернул данные
        if (allMeals.isNotEmpty()) {
            btnLoadMore.visibility = View.VISIBLE
        } else {
            btnLoadMore.visibility = View.GONE
        }
    }

    private fun showEmpty() {
        recyclerMeals.visibility = View.GONE
        btnLoadMore.visibility = View.GONE
        stateLoading.visibility = View.GONE
        progressMore.visibility = View.GONE
        stateEmpty.visibility = View.VISIBLE
    }










    fun loadMore() {
        if (visibleCount >= allMeals.size) return

        progressMore.visibility = View.VISIBLE
        btnLoadMore.visibility = View.GONE

        visibleCount = (visibleCount + pageSize).coerceAtMost(allMeals.size)
        adapter.submitList(allMeals.take(visibleCount))

        progressMore.visibility = View.GONE

        if (visibleCount >= allMeals.size) {
            btnLoadMore.visibility = View.GONE
        } else {
            btnLoadMore.visibility = View.VISIBLE
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