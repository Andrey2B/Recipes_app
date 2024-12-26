package com.example.recipeappkotlinproject

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recipeappkotlinproject.databinding.ActivityFavoriteBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class FavoriteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavoriteBinding
    private val database =
        FirebaseDatabase.getInstance("https://eat-eat-5f6b6-default-rtdb.firebaseio.com/").reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.favoriteRecyclerView.layoutManager = LinearLayoutManager(this)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val noFavoritesTextView = findViewById<TextView>(R.id.noFavoritesTextView)
        noFavoritesTextView.visibility = View.GONE // Initially hidden


        // Get userId from Intent
        val userId = intent.getIntExtra("USER_ID", -1) //Default -1 if no data

        if (userId != -1) {
            loadFavoriteRecipes(userId)
        } else {
            Log.e("FavoriteActivity", "Failed to load favorite recipes. Please try again.")
        }

        loadFavoriteRecipes(userId)


        val backButton: ImageView = findViewById(R.id.imageView2)
        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()  //back previous Activity
        }

    }


    private fun loadFavoriteRecipes(userId: Int) {
        database.child("users").child(userId.toString()).get()
            .addOnSuccessListener { userSnapshot ->
                // Get a list favorite recipes IDs
                val favoriteIds = userSnapshot.child("id_favourite_recipes").value.toString()
                    .split(",").mapNotNull { it.trim().toIntOrNull() }

                if (favoriteIds.isEmpty()) {
                    Log.e(TAG, "No favorite recipe")
                    val noFavoritesTextView = findViewById<TextView>(R.id.noFavoritesTextView)

                    noFavoritesTextView.visibility = View.VISIBLE // Show the "no favorites" message
                    noFavoritesTextView.text = "У вас нет любимых рецептов."
                    binding.favoriteRecyclerView.visibility = View.GONE // Hide the RecyclerView
                    //Toast.makeText(this, "Любимых рецептов не найдено.", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                // Load data of recipes
                database.child("recipes").addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(recipesSnapshot: DataSnapshot) {
                        val favoriteRecipes = favoriteIds.mapNotNull { id ->
                            val recipeSnapshot = recipesSnapshot.child(id.toString())
                            if (recipeSnapshot.exists()) {
                                Recipe_fav(
                                    name = recipeSnapshot.child("name_recipe").value.toString(),
                                    image = recipeSnapshot.child("image_url").value.toString()
                                )
                            } else {
                                Log.w(TAG, "Recipe with ID $id not found in database.")
                                null
                            }
                        }

                        if (favoriteRecipes.isEmpty()) {
                            Log.e(TAG, "No favorite recipes found.")
                            val noFavoritesTextView = findViewById<TextView>(R.id.noFavoritesTextView)
                            noFavoritesTextView.visibility = View.VISIBLE // Show the "no favorites" message
                            noFavoritesTextView.text = "У Вас нет любимых рецептов."
                            binding.favoriteRecyclerView.visibility = View.GONE // Hide the RecyclerView
                            //Toast.makeText(this@FavoriteActivity, "Любимых рецептов не найдено.", Toast.LENGTH_SHORT).show()
                        } else {
                            val noFavoritesTextView = findViewById<TextView>(R.id.noFavoritesTextView)
                            noFavoritesTextView.visibility = View.GONE // Hide the "no favorites" message
                            binding.favoriteRecyclerView.visibility = View.VISIBLE // Show the RecyclerView
                            // Set Adapter for RecyclerView
                            binding.favoriteRecyclerView.adapter = FavoriteRecipeAdapter(favoriteRecipes)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(this@FavoriteActivity, "Не удалось загрузить рецепты.", Toast.LENGTH_SHORT).show()
                        Log.e(TAG, "Failed to fetch recipes: ${error.message}")
                    }
                })
            }
            .addOnFailureListener { error ->
                Log.e(TAG, "Failed to fetch user data: ${error.message}")
                Toast.makeText(this, "Не удалось загрузить пользовательские данные", Toast.LENGTH_SHORT).show()
            }
    }

}