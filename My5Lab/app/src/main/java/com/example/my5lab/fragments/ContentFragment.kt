package com.example.my5lab.fragments


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
import com.example.my5lab.R
import com.example.my5lab.databinding.FragmentContentBinding

import com.example.my5lab.database.AppDatabase
import com.example.my5lab.database.MealEntity

import com.example.my5lab.extensions.debugging
import com.example.my5lab.model.Meal
import com.example.my5lab.model.MealAdapter
import com.example.my5lab.network.ApiMealClient
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import kotlinx.io.IOException
import kotlin.collections.map

class ContentFragment : Fragment() {

    private val apiClient by lazy { ApiMealClient(requireContext().applicationContext) }
    lateinit var recyclerMeals: RecyclerView
    lateinit var adapter: MealAdapter
    lateinit var btnLoadMore: Button
    lateinit var progressMore: ProgressBar
    lateinit var stateLoading: View
    lateinit var stateEmpty: View

    private var allMeals = mutableListOf<Meal>()
    private var visibleCount = 0
    private val pageSize = 10

    companion object {
        private const val TAG = "ContentFragment"
    }

    private var _binding: FragmentContentBinding? = null
    private val binding get() = _binding!!

    // База данных
    private val db by lazy { AppDatabase.getDatabase(requireContext()) }
    private val mealDao by lazy { db.mealDao() }

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

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        debugging("onViewCreated")

        // Инициализация View
        recyclerMeals = binding.recyclerMeals
        btnLoadMore = binding.btnLoadMore
        progressMore = binding.progressMore
        stateLoading = binding.stateLoading
        stateEmpty = binding.stateEmpty

        val toolbar: MaterialToolbar = binding.toolbar
        toolbar.title = "Рецепты"
        toolbar.inflateMenu(R.menu.menu_content)

        adapter = MealAdapter() { meal ->
            Snackbar.make(binding.main, "Выбрано: ${meal.name}", Snackbar.LENGTH_SHORT).show()
        }
        recyclerMeals.layoutManager = GridLayoutManager(requireContext(), 2)
        recyclerMeals.adapter = adapter

        // Обработчики кнопок
        btnLoadMore.setOnClickListener {
            debugging("Click load more")
            loadMore()
        }

        binding.btnRefresh.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                mealDao.clearAll()
                fetchMeals(isFirstPage = true)
            }
        }

        // 1. Подписка на Flow из базы данных (Реактивное обновление)
        viewLifecycleOwner.lifecycleScope.launch {
            mealDao.getAllMealsFlow().collect { entities ->
                val mealsFromDb = entities.map { entity ->
                    Meal(
                        id = entity.id,
                        name = entity.name,
                        imageRes = R.mipmap.ic_launcher, // Пока используем стандартную иконку
                        category = entity.category,
                        area = entity.area,
                        cookingTime = entity.cookingTime
                    )
                }
                if (mealsFromDb.isNotEmpty()) {
                    allMeals.clear()
                    allMeals.addAll(mealsFromDb)
                    adapter.submitList(allMeals.toList())

                    stateLoading.visibility = View.GONE
                    recyclerMeals.visibility = View.VISIBLE
                }
            }
        }

        // Логика "Холодного старта"
        viewLifecycleOwner.lifecycleScope.launch {
            val mealsInDb = mealDao.getAllMeals()
            if (mealsInDb.isEmpty()) {
                fetchMeals(isFirstPage = true)
            } else {
                stateLoading.visibility = View.GONE
            }
        }
    }

    private fun fetchMeals(isFirstPage: Boolean) {
        if (isFirstPage) {
            stateLoading.visibility = View.VISIBLE
            recyclerMeals.visibility = View.GONE
            stateEmpty.visibility = View.GONE
            btnLoadMore.visibility = View.GONE
        } else {
            progressMore.visibility = View.VISIBLE
            btnLoadMore.visibility = View.GONE
        }

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val newDtos = if (isFirstPage) {
                    apiClient.getMeals()
                } else {
                    apiClient.getMeals(pageSize)
                }

                val newMeals = newDtos.map { dto ->
                    Meal(
                        id = dto.idMeal,
                        name = dto.strMeal,
                        imageRes = R.mipmap.ic_launcher,
                        category = dto.strCategory ?: "Unknown",
                        area = dto.strArea,
                        cookingTime = 0
                    )
                }

                // Сохранение в Room
                val entitiesToSave = newMeals.map {
                    MealEntity(
                        id = it.id,
                        name = it.name,
                        category = it.category,
                        area = it.area,
                        cookingTime = it.cookingTime
                    )
                }

                if (isFirstPage) {
                    mealDao.clearAll()
                }
                mealDao.insertMeals(entitiesToSave)

                // showSuccess() больше не нужен, так как Flow обновит UI автоматически
            } catch (e: IOException) {
                handleError("Нет подключения к интернету")
            } catch (e: Exception) {
                handleError("Ошибка загрузки: ${e.message}")
            } finally {
                stateLoading.visibility = View.GONE
                progressMore.visibility = View.GONE
            }
        }
    }

    private fun handleError(string: String) {
        if (allMeals.isEmpty()) {
            showEmpty()
        } else {
            Snackbar.make(binding.main, string, Snackbar.LENGTH_LONG).show()
            btnLoadMore.visibility = View.VISIBLE
        }
    }

    private fun showEmpty() {
        recyclerMeals.visibility = View.GONE
        btnLoadMore.visibility = View.GONE
        stateLoading.visibility = View.GONE
        progressMore.visibility = View.GONE
        stateEmpty.visibility = View.VISIBLE
    }

    private fun loadMore() {
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