package com.example.recipeappkotlinproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide


class FavoriteRecipeAdapter(private val recipes: List<Recipe_fav>) :
    RecyclerView.Adapter<FavoriteRecipeAdapter.FavoriteRecipeViewHolder>() {

    class FavoriteRecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val recipeName: TextView = itemView.findViewById(R.id.FavoriteRecipeTitle)
        val recipeImage: ImageView = itemView.findViewById(R.id.FavoriteRecipeImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteRecipeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_favorite_recipe, parent, false)
        return FavoriteRecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavoriteRecipeViewHolder, position: Int) {
        val recipe = recipes[position]
        holder.recipeName.text = recipe.name
        Glide.with(holder.itemView.context)
            .load(recipe.image)
            //.placeholder(R.drawable.placeholder_image)  //Replace real image to reloading image or gif (VIKA)
            //.error(R.drawable.error_image)  //Replce real image to error image or gif(VIKA)
            .into(holder.recipeImage)
    }

    override fun getItemCount(): Int = recipes.size
}
