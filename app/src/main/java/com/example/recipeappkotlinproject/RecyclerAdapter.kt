package com.example.recipeappkotlinproject

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView

class RecyclerAdapter(private var recipes: List<Recipe>) : RecyclerView.Adapter<RecyclerAdapter.RecipeViewHolder>() {

    //Method that RecyclerView calls when creating a new view for a list item
    override fun onCreateViewHolder (parent: ViewGroup, viewType: Int): RecipeViewHolder {
        //LayoutInflater is a class that can create a View element from the contents of a layout file
        // The method that does this is called inflate.

        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recipe, parent, false)
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = recipes[position]
        holder.bind(recipe)

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, DetailActivity::class.java)
            intent.putExtra("RECIPE_NAME", recipe.name)
            intent.putExtra("RECIPE_IMAGE", recipe.image)
            intent.putExtra("RECIPE_DESCRIPTION", recipe.description)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return recipes.size
    }


    //The class binds data to the View displayed in the list item
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

    //Let's fill the adapter with data from the database
    fun fillAdapter(){

    }

}