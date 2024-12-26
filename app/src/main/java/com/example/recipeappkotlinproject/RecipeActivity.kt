package com.example.recipeappkotlinproject

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class RecipeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.item_recipe)

        val recipeName = intent.getStringExtra("RECIPE_NAME")
        val recipeImage = intent.getStringExtra("RECIPE_IMAGE")
        val recipeDescription = intent.getStringExtra("RECIPE_DESCRIPTION")
        val recipeProducts = intent.getStringArrayListExtra("RECIPE_PRODUCTS")

        val nameTextView: TextView = findViewById(R.id.recipeTitle)
        val descriptionTextView: TextView = findViewById(R.id.recipeDescription)
        val imageView: ImageView = findViewById(R.id.recipeImage)
        val productsTextView: TextView = findViewById(R.id.recipeProducts)

        nameTextView.text = recipeName
        descriptionTextView.text = recipeDescription
        productsTextView.text = recipeProducts?.joinToString(separator = "\n") ?: "Продукты не указаны"
        Glide.with(this)
            .load(recipeImage)
            .placeholder(R.drawable.placeholder)
            .into(imageView)
    }
}