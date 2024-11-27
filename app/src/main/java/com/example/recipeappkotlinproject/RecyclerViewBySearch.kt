package com.example.recipeappkotlinproject

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class RecyclerViewBySearch : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_recycler_view_by_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    //val searchRecipe: SearchView
    //var recipesList: RecyclerView

    //Examples of recipes(repaet from mainActivity)
    var recipes = listOf(
        Recipe("Рецепт1", R.drawable.recipe1, "Описание1"),
        Recipe("Рецепт2", R.drawable.recipe2, "Описание2"),
        Recipe("Рецепт3", R.drawable.recipe3, "Описание3")
    )


    //recipesList = findViewById(R.id.recyclerView)

    //Set for RecycleView "LayoutManager" and "Adapter"
    //recipesList.layoutManager = LinearLayoutManager(this)
    //recipesList.adapter = RecyclerAdapter(recipes)

}