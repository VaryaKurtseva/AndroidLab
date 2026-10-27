package com.example.mysecondlab.model

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mysecondlab.databinding.ItemMealBinding

class MealAdapter(private val onMealClick: (Meal) -> Unit)
    : RecyclerView.Adapter<MealAdapter.MealViewHolder>() {

    private var meals: List<Meal> = emptyList()

    fun submitList(newList: List<Meal>) {
        meals = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealViewHolder {

        val binding = ItemMealBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MealViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MealViewHolder, position: Int) {
        holder.bind(meals[position], onMealClick)
    }

    override fun getItemCount(): Int = meals.size


    class MealViewHolder(private val binding: ItemMealBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(meal: Meal, onClick: (Meal) -> Unit) {

            binding.ivMealImage.setImageResource(meal.imageRes)
            binding.tvMealName.text = meal.name
            binding.tvMealArea.text = "${meal.area ?: "Unknown"} · ${meal.category}"
            binding.tvMealRating.text = "${(meal.cookingTime % 10)}"
            binding.tvMealTime.text = "${meal.cookingTime} min"

            // Клик по всей карточке
            binding.root.setOnClickListener { onClick(meal) }
        }
    }
}