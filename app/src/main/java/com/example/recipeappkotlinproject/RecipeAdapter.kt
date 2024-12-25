package com.example.recipeappkotlinproject

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class RecipeAdapter(
    private var recipeList: MutableList<Products_DB.Recipe> = mutableListOf()
) : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {




    fun updateRecipes(newRecipes: List<Products_DB.Recipe>) {
        recipeList.clear()
        recipeList.addAll(newRecipes)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.search_recipe1, parent, false)
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = recipeList[position]
        holder.bind(recipe)
    }

    override fun getItemCount(): Int {
        return recipeList.size
    }

    class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.recipeNameTextView)
        private val imageView: ImageView = itemView.findViewById(R.id.recipeImageView)

        fun bind(recipe: Products_DB.Recipe) {
            Log.d("URL_IMG", recipe.image_url.toString())
            nameTextView.text = recipe.name_recipe
            Glide.with(itemView.context)
                .load(recipe.image_url)
                .placeholder(R.drawable.placeholder)
                .into(imageView)
        }
    }
}

