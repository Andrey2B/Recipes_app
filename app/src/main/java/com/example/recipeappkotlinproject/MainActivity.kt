package com.example.recipeappkotlinproject

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
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

        val DB = Products_DB()

        DB.Read_DB()
        val databaseRef = DB.real_db.reference
        val recipeName = "макароны"

        DB.findRecipeByName(databaseRef, recipeName) { recipes ->
            if (recipes.isNotEmpty()) {
                println("Найдены рецепты с ключевым словом \"$recipeName\":")
                recipes.forEach { recipe ->
                    println("ID: ${recipe.id_recipe}, Название: ${recipe.name_recipe}")
                }
            } else {
                println("Рецептов с ключевым словом \"$recipeName\" не найдено.")
            }
        }


// Идентификатор текущего пользователя
        val userId = 1

        database.getFavoriteRecipes(
            userId,
            onSuccess = { favoriteRecipes ->
                // Устанавливаем адаптер с данными из Firebase
                val recipeAdapter = FavoriteRecipeAdapter(favoriteRecipes)
                binding.favoriteRecipesRecyclerView.layoutManager =
                    LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                binding.favoriteRecipesRecyclerView.adapter = recipeAdapter
            },
            onFailure = { error ->
                // Обрабатываем ошибку
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
            val intent = Intent(this, FavoriteActivity::class.java)
            this.startActivity(intent)
        }

        searchRecipe.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Когда пользователь завершает ввод и нажимает "поиск"
                if (!query.isNullOrEmpty()) {
                    val intent = Intent(this@MainActivity, SearchActivity::class.java)
                    intent.putExtra("searchQuery", query) // Передаем запрос в SearchActivity
                    startActivity(intent)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Здесь можно реализовать динамическую логику, если нужно обрабатывать ввод в реальном времени
                return false // Возвращаем false, если не используем обработку в реальном времени
            }
        })


        profileIkon.setOnClickListener{
            val intent = Intent(this, ProfileActivity::class.java)
            this.startActivity(intent)
        }



        //processing of the request and the search

        database.Read_DB()
    }
}