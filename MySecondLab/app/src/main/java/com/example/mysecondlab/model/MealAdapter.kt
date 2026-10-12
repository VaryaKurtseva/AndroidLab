package com.example.mysecondlab.model

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mysecondlab.R


class MealAdapter(private val onMealClick: (Meal) -> Unit)
    : RecyclerView.Adapter<MealAdapter.MealViewHolder>() {

    private var meals: List<Meal> = emptyList()

    fun submitList(newList: List<Meal>) {
        meals = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_meal, parent, false)
        return MealViewHolder(view)
    }

    override fun onBindViewHolder(holder: MealViewHolder, position: Int) {
        holder.bind(meals[position], onMealClick)
    }

    override fun getItemCount(): Int = meals.size

    class MealViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val ivImage: ImageView = view.findViewById(R.id.iv_meal_image)
        private val tvName: TextView = view.findViewById(R.id.tv_meal_name)
        private val tvArea: TextView = view.findViewById(R.id.tv_meal_area)
        private val tvRating: TextView = view.findViewById(R.id.tv_meal_rating)
        private val tvTime: TextView = view.findViewById(R.id.tv_meal_time)

        fun bind(meal: Meal, onClick: (Meal) -> Unit) {
            ivImage.setImageResource(meal.imageRes)
            tvName.text = meal.name
            tvArea.text = "${meal.area ?: "Unknown"} · ${meal.category}"
            tvRating.text = "${(meal.cookingTime % 10)}"
            tvTime.text = "${meal.cookingTime} min"

            // Клик по всей карточке
            itemView.setOnClickListener { onClick(meal) }
        }
    }
}