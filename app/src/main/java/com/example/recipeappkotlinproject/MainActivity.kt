package com.example.recipeappkotlinproject

import android.content.ContentValues.TAG
import android.os.Bundle
import android.provider.ContactsContract.Data
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.getValue
import com.google.firebase.database.values
import kotlinx.coroutines.tasks.await


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


                                                    //"https://eat-eat-5f6b6-default-rtdb.firebaseio.com"












        //processing of the request and the search

    }

}