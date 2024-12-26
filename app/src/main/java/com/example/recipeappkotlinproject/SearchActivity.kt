package com.example.recipeappkotlinproject

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SearchActivity : AppCompatActivity() {

    private lateinit var databaseRef: DatabaseReference
    private lateinit var recipeAdapter: RecipeAdapter
    private lateinit var searchView: SearchView
    private lateinit var emptyStateTextView: TextView
    private lateinit var recipesRecyclerView: RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search_recycler)

        databaseRef = FirebaseDatabase.getInstance().reference
        searchView = findViewById(R.id.searchView)
        emptyStateTextView = findViewById(R.id.emptyStateTextView)
        recipesRecyclerView = findViewById(R.id.recipesRecyclerView)

        recipeAdapter = RecipeAdapter()
        recipesRecyclerView.layoutManager = LinearLayoutManager(this)
        recipesRecyclerView.adapter = recipeAdapter

        // Получаем данные из Intent
        val categoryName = intent.getStringExtra("categoryName")

        if (!categoryName.isNullOrEmpty()) {
            searchRecipesByCategory(categoryName) // Поиск по категории
        }

        setupSearchView()
    }

    private fun setupSearchView() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    searchRecipes(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (!newText.isNullOrEmpty()) {
                    searchRecipes(newText)
                }
                return true
            }
        })
    }

    fun searchRecipesByCategory(categoryName: String) {
        Products_DB().findRecipesByCategory(databaseRef, categoryName) { recipes ->
            if (recipes.isNotEmpty()) {
                recipeAdapter.updateRecipes(recipes)
                recipesRecyclerView.visibility = View.VISIBLE
                emptyStateTextView.visibility = View.GONE
            } else {
                recipesRecyclerView.visibility = View.GONE
                emptyStateTextView.visibility = View.VISIBLE
            }
        }
    }

    fun searchRecipes(keyword: String) {
        Products_DB().findRecipeByName(databaseRef, keyword) { recipes ->
            if (recipes.isNotEmpty()) {
                recipeAdapter.updateRecipes(recipes)
                recipesRecyclerView.visibility = View.VISIBLE
                emptyStateTextView.visibility = View.GONE
            } else {
                recipesRecyclerView.visibility = View.GONE
                emptyStateTextView.visibility = View.VISIBLE
            }
        }
    }
}