package com.example.recipeappkotlinproject

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recipeappkotlinproject.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

data class Recipe(
    val id_recipe: Int,
    val name: String,
    val image: String,
    val description: String
)

data class Recipe_fav(
    val name: String,
    val image: String
)

class MainActivity : AppCompatActivity(){

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference



    lateinit var binding: ActivityMainBinding
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        //setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = FirebaseAuth.getInstance()

        binding.imageView.setOnClickListener{
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.filter_holder, FilterFragment.newInstance())
                .commit()

        }

        //Examples of categories
        val categories = listOf(
            Category("Завтраки", R.drawable.recipe1),
            Category("Обеды", R.drawable.recipe2),
            Category("Ужины", R.drawable.recipe3)
        )

        val categoryAdapter = CategoryAdapter(categories)
        binding.categoriesRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.categoriesRecyclerView.adapter = categoryAdapter

        /*val fav_recipes = listOf(
            Recipe_fav("Пирог", R.drawable.recipe1),
            Recipe_fav("Салат", R.drawable.recipe2),
            Recipe_fav("Блины", R.drawable.recipe3)
        )*/

        /*val recipeAdapter = FavoriteRecipeAdapter(fav_recipes)
        binding.favoriteRecipesRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.favoriteRecipesRecyclerView.adapter = recipeAdapter*/

        //Examples of recipes
        /*var recipes = listOf(
            Recipe("Рецепт1", R.drawable.recipe1, "Описание1"),
            Recipe("Рецепт2", R.drawable.recipe2, "Описание2"),
            Recipe("Рецепт3", R.drawable.recipe3, "Описание3")
        )*/

        val database = Products_DB()

        //val DB = Products_DB()

        database.Read_DB()
        val databaseRef = database.real_db.reference
        val recipeName = "макароны"


        database.findRecipeByName(databaseRef, recipeName) { recipes ->
            if (recipes.isNotEmpty()) {
                println("Найдены рецепты с ключевым словом \"$recipeName\":")
                recipes.forEach { recipe ->
                    println("ID: ${recipe.id_recipe}, Название: ${recipe.name_recipe}")
                }
            } else {
                println("Рецептов с ключевым словом \"$recipeName\" не найдено.")
            }
        }

        //Current user ID
        val userId = auth.currentUser?.uid

        database.getFavoriteRecipes(
            userId,
            onSuccess = { favoriteRecipes ->
                //Installing the adapter with data from Firebase
                val recipeAdapter = FavoriteRecipeAdapter(favoriteRecipes)
                binding.favoriteRecipesRecyclerView.layoutManager =
                    LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                binding.favoriteRecipesRecyclerView.adapter = recipeAdapter
            },
            onFailure = { error ->
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
            }
        )







        val searchRecipe: SearchView
        val filterIkon: ImageView
        val shoppingBasket: Button
        val categoryRecipes1: ImageView
        val categoryRecipes2: ImageView
        val categoryRecipes3: ImageView
        val categoryRecipes4: ImageView
        val categoryRecipes5: ImageView
        val categoryRecipes6: ImageView
        val homeIkon: ImageView
        val favoriteIkon: ImageView
        val profileIkon: ImageView


        // val DB = Products_DB()
        // DB.Save_DB("test1", "122")
        //DB.Read_DB()



        //var recipesList: RecyclerView

        searchRecipe = findViewById(R.id.searchView)
        filterIkon = findViewById(R.id.imageView)
        //shoppingBasket = findViewById(R.id.button2)
        favoriteIkon = findViewById(R.id.imageView9)
        homeIkon = findViewById(R.id.imageView10)
        profileIkon = findViewById(R.id.imageView11)



        /*
        homeIkon.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            this.startActivity(intent)
        }*/


        favoriteIkon.setOnClickListener{
            checkUserStatusInFavorite()
        }


        profileIkon.setOnClickListener{
            checkUserStatusInProfile()
        }

    }

    private fun checkUserStatusInFavorite() {
        val currentUser = auth.currentUser

        if (currentUser != null) {
            //User in the account, perform the action
            doSomethingForLoggedInUserInFavorite()
        } else {
            //The user is not logged in, go to the registration/login screen
            navigateToLoginOrRegisterInFavorite()
        }
    }

    private fun doSomethingForLoggedInUserInFavorite() {
        val userId = auth.currentUser?.uid
        val intent = Intent(this, FavoriteActivity::class.java)
        intent.putExtra("USER_ID", userId)
        this.startActivity(intent)

        //Toast.makeText(this, "You are logged in!", Toast.LENGTH_SHORT).show()
    }

    private fun navigateToLoginOrRegisterInFavorite() {
        //Toast.makeText(this, "You are not logged in.", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }



    private fun checkUserStatusInProfile() {
        val currentUser = auth.currentUser

        if (currentUser != null) {
            //User in the account, perform the action
            doSomethingForLoggedInUserInProfile()
        } else {
            //The user is not logged in, go to the registration/login screen
            navigateToLoginOrRegisterInProfile()
        }
    }

    private fun doSomethingForLoggedInUserInProfile() {
        val userId = auth.currentUser?.uid
        val intent = Intent(this, ProfileActivity::class.java)
        intent.putExtra("USER_ID", userId)
        this.startActivity(intent)

        //Toast.makeText(this, "You are logged in!", Toast.LENGTH_SHORT).show()
    }

    private fun navigateToLoginOrRegisterInProfile() {
        //Toast.makeText(this, "You are not logged in.", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}