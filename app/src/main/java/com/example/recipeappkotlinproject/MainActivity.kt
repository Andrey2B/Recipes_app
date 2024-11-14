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
import com.google.firebase.database.ValueEventListener

data class Recipe(
    val name: String,
    val image: Int,
    val description: String
)

class newUser(){
    val Name:String = "Kolya"
    val Email:String = "@gmail.com"
    val id:Int = 1
}

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

        val database = FirebaseDatabase.getInstance("https://eat-eat-5f6b6-default-rtdb.firebaseio.com")
        val myRef = database.getReference("test-message")
        val myDB = database.getReference()
        val user = newUser()
        myRef.setValue(user)






        //processing of the request and the search

    }

}