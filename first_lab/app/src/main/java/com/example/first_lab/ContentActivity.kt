package com.example.first_lab

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.first_lab.extensions.debugging
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.snackbar.Snackbar

class ContentActivity : AppCompatActivity()  {
    lateinit var recyclerMeals : RecyclerView
    lateinit var adapter : MealAdapter
    lateinit var btnLoadMore : Button
    lateinit var progressMore : ProgressBar
    lateinit var stateLoading : View
    lateinit var stateEmpty: View

    lateinit var allMeals : MutableList<Meal>

    private var visibleCount: Int = 0
    private val pageSize: Int = 10




    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_content)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        debugging("onCreate")

        recyclerMeals = findViewById(R.id.recycler_meals)
        btnLoadMore = findViewById(R.id.btn_load_more)
        progressMore = findViewById(R.id.progress_more)
        stateLoading = findViewById(R.id.state_loading)
        stateEmpty = findViewById(R.id.state_empty)


        val toolbar: MaterialToolbar = findViewById(R.id.toolbar)
        toolbar.title = "Рецепты"
        toolbar.inflateMenu(R.menu.menu_content)
        toolbar.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.action_settings) {
                openSettings()
                true
            } else false
        }

        adapter = MealAdapter() { meal ->
            Snackbar.make(findViewById(R.id.main), "Выбрано: ${meal.name}", Snackbar.LENGTH_SHORT).show()
        }
        recyclerMeals.layoutManager = GridLayoutManager(this, 2)
        recyclerMeals.adapter = adapter

        btnLoadMore.setOnClickListener {
            debugging("Click load more")
            loadMore()
        }

        loadMeals()
        if (allMeals.isEmpty()) {
            showEmpty()
        } else {
            showSuccess()
        }


    }
    private fun loadMeals() {

        allMeals = mutableListOf(
            Meal("1", "Seafood Paella", R.mipmap.ic_launcher, "Seafood", "Spanish", 45),
            Meal("2", "Miso Glazed Salmon", R.mipmap.ic_launcher, "Seafood", "Japanese", 30),
            Meal("3", "Clam Linguine", R.mipmap.ic_launcher, "Seafood", "Italian", 25),
            Meal("4", "Baja Fish Tacos", R.mipmap.ic_launcher, "Seafood", "Mexican", 23),
            Meal("5", "Bouillabaisse", R.mipmap.ic_launcher, "Seafood", "French", 90),
            Meal("6", "Garlic Butter Prawns", R.mipmap.ic_launcher, "Seafood", "Spanish", 10),
            Meal("7", "Ahi Tuna Poke", R.mipmap.ic_launcher, "Seafood", "Hawaiian", 15),
            Meal("8", "Crab Cakes", R.mipmap.ic_launcher, "Seafood", "American", 30),
            Meal("9", "Fish Pie", R.mipmap.ic_launcher, "Seafood", "British", 50),
            Meal("10", "Grilled Sardines", R.mipmap.ic_launcher, "Seafood", "Portuguese", 20),
            Meal("11", "Kedgeree", R.mipmap.ic_launcher, "Seafood", "British", 35),
            Meal("12", "Laksa King Prawn", R.mipmap.ic_launcher, "Seafood", "Malaysian", 40),
            Meal("13", "Pad Thai", R.mipmap.ic_launcher, "Seafood", "Thai", 25),
            Meal("14", "Salt & Pepper Squid", R.mipmap.ic_launcher, "Seafood", "Vietnamese", 15),
            Meal("15", "Shrimp Chow Fun", R.mipmap.ic_launcher, "Seafood", "Chinese", 20),
            Meal("16", "Sushi", R.mipmap.ic_launcher, "Seafood", "Japanese", 60),
            Meal("17", "Tom Yum Soup", R.mipmap.ic_launcher, "Seafood", "Thai", 30),
            Meal("18", "Tuna Nicoise", R.mipmap.ic_launcher, "Seafood", "French", 20),
            Meal("19", "Fish Fofos", R.mipmap.ic_launcher, "Seafood", "Portuguese", 40),
            Meal("20", "Escovitch Fish", R.mipmap.ic_launcher, "Seafood", "Jamaican", 35)
        )
        visibleCount = pageSize
    }

    private fun showSuccess() {
        stateLoading.visibility = View.GONE
        stateEmpty.visibility = View.GONE
        recyclerMeals.visibility = View.VISIBLE

        adapter.submitList(allMeals.take(visibleCount))

        if (visibleCount >= allMeals.size) {
            btnLoadMore.visibility = View.GONE
        } else {
            btnLoadMore.visibility = View.VISIBLE
        }
    }



    private fun showEmpty() {
        recyclerMeals.visibility = View.GONE
        btnLoadMore.visibility = View.GONE
        stateLoading.visibility = View.GONE
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



    private fun openSettings() {
        debugging("Open settings")
        startActivity(Intent(this, SettingsActivity::class.java))
        finish()

    }

}