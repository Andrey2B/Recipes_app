package com.example.recipeappkotlinproject

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase


data class Recipe(
    val name: String,
    val image: Int,
    val description: String
)



class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //Examples of recipes
        var recipes = listOf(
            Recipe("Рецепт1", R.drawable.recipe1, "Описание1"),
            Recipe("Рецепт2", R.drawable.recipe2, "Описание2"),
            Recipe("Рецепт3", R.drawable.recipe3, "Описание3")
        )

        val searchRecipe: SearchView
        val recipesList: RecyclerView
        val searchView: SearchView

        recipesList = findViewById(R.id.recyclerView)

        //Set for RecycleView "LayoutManager" and "Adapter"
        recipesList.layoutManager = LinearLayoutManager(this)
        recipesList.adapter = RecyclerAdapter(recipes)

        searchView = findViewById(R.id.searchView)

        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("message")

        myRef.setValue("Hello, World!")
        //processing of the request and the search

    }

}