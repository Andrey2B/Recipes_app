package com.example.recipeappkotlinproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class CategoryAdapter(
    private val categories: List<Category>,
    private val onCategoryClick: (String) -> Unit // Лямбда-функция для обработки кликов
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    data class Category(val name: String, val image: Int)

    class CategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val categoryImage: ImageView = view.findViewById(R.id.category_image)
        val categoryName: TextView = view.findViewById(R.id.category_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        holder.categoryImage.setImageResource(category.image)
        holder.categoryName.text = category.name
        holder.itemView.setOnClickListener {
            onCategoryClick(category.name) // Передаем имя категории в обработчик
        }
    }

    override fun getItemCount(): Int = categories.size
}
