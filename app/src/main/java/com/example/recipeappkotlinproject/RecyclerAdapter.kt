package com.example.recipeappkotlinproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RecyclerAdapter(private var recipes: List<Recipe>) : RecyclerView.Adapter<RecyclerAdapter.RecipeViewHolder>() {

    //Method that RecyclerView raises when creating a new view for a list of items
    override fun onCreateViewHolder (parent: ViewGroup, viewType: Int): RecipeViewHolder {

        //LayoutInflater – this is a class that can create a View element from the contents of a layout file
        //The method that does this is called inflate.

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

    //fill the adapter with data from the database
    fun fillAdapter(){

    }
}