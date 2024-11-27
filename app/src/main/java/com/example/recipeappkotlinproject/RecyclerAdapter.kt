package com.example.recipeappkotlinproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RecyclerAdapter(private var recipes: List<Recipe>) : RecyclerView.Adapter<RecyclerAdapter.RecipeViewHolder>() {

    //Метод, который вызывается RecyclerView при создании нового представления для элемента списка
    override fun onCreateViewHolder (parent: ViewGroup, viewType: Int): RecipeViewHolder {

        //LayoutInflater – это класс, который умеет из содержимого layout-файла создать View-элемент.
        // Метод который это делает называется inflate.

        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recipe, parent, false)

        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = recipes[position]
        holder.bind(recipe)
    }

    override fun getItemCount(): Int {
        return recipes.size
    }

    //Класс связывает данные с View, отображаемым в элементе списка
    class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.recipeTitle)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.recipeDescription)
        private val imageView: ImageView = itemView.findViewById(R.id.recipeImage)


        fun bind(recipe: Recipe) {
            imageView.setImageResource(recipe.image)
            titleTextView.text = recipe.name
            descriptionTextView.text = recipe.description

        }
    }

    //Заполним адаптер данными с БД
    fun fillAdapter(){

    }
}