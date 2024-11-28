package com.example.recipeappkotlinproject

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout

data class Recipe(
    val name: String,
    val image: Int,
    val description: String
)

class MainActivity : AppCompatActivity(){

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {

        //setTheme(R.style.Theme_RecipeApp)

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //Examples of recipes
        /*var recipes = listOf(
            Recipe("Рецепт1", R.drawable.recipe1, "Описание1"),
            Recipe("Рецепт2", R.drawable.recipe2, "Описание2"),
            Recipe("Рецепт3", R.drawable.recipe3, "Описание3")
        )*/

        val searchRecipe: SearchView
        val filterButton: Button
        val favoriteButton: Button
        val shoppingBasket: Button
        val categoryRecipes1: ImageView
        val categoryRecipes2: ImageView
        val categoryRecipes3: ImageView
        val categoryRecipes4: ImageView
        val categoryRecipes5: ImageView
        val categoryRecipes6: ImageView
        val homeIkon: ImageView
        val newsIkon: ImageView
        val profileIkon: ImageView

        val DB: Products_DB = Products_DB()
        DB.Save_DB("test1", "122")
        DB.Read_DB()



        //var recipesList: RecyclerView

        searchRecipe = findViewById(R.id.searchView)
        filterButton = findViewById(R.id.button4)
        favoriteButton = findViewById(R.id.button)
        shoppingBasket = findViewById(R.id.button2)
        categoryRecipes1 = findViewById(R.id.imageView2)
        categoryRecipes2 = findViewById(R.id.imageView3)
        categoryRecipes3 = findViewById(R.id.imageView4)
        categoryRecipes4 = findViewById(R.id.imageView5)
        categoryRecipes5 = findViewById(R.id.imageView6)
        categoryRecipes6 = findViewById(R.id.imageView7)
        newsIkon = findViewById(R.id.imageView9)
        homeIkon = findViewById(R.id.imageView10)
        profileIkon = findViewById(R.id.imageView11)



        favoriteButton.setOnClickListener {
            val intent = Intent(this, FavoriteList::class.java)
            this.startActivity(intent)
        }

        filterButton.setOnClickListener{
            //LayoutInflater.from(this).inflate(R.layout.item_recipe, this, false)

            val parent = findViewById<ConstraintLayout>(R.id.main)
            val itemView = LayoutInflater.from(this).inflate(R.layout.item_recipe, parent, false)
            parent.addView(itemView)

        }

        /*
        homeIkon.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            this.startActivity(intent)
        }
        */

            newsIkon.setOnClickListener{
            val intent = Intent(this, NewsActivity::class.java)
            this.startActivity(intent)
        }


        profileIkon.setOnClickListener{
            val intent = Intent(this, ProfileActivity::class.java)
            this.startActivity(intent)
        }


        //processing of the request and the search

    }
}
