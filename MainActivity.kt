package com.example.recipeappnewmisis

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.ImageView
import android.widget.SearchView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater



class MainActivity : AppCompatActivity() {

    private lateinit var searchRecipe: SearchView
    private lateinit var recipesList: RecyclerView

    //activity_main - XML файл с макетом с EditText и RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        searchRecipe = findViewById(R.id.searchRecipe)
        recipesList = findViewById(R.id.recipesList)

        class Recipe(
            val name: String,
            val image: Int,
            val description: String
        )

        //Пример рецептов без БД
        //Вика попытайся прикрепить картинки с 3 рецептами (картинки в drawable), нужно для кода под комментом
        //Если что-то исправишь - пиши
        var recipes = listOf(
            Recipe("Рецепт1", R.drawable.ic_launcher_foreground, "Описание1"),
            Recipe("Рецепт2", R.drawable.ic_launcher_foreground, "Описание2"),
            Recipe("Рецепт3", R.drawable.ic_launcher_foreground, "Описание3")
        )


        //Обработка запросов поиска

    }

////type??? 
    class RecyclerAdapter(private var recipes) : RecyclerView.Adapter<RecyclerAdapter.RecipeViewHolder>() {

        //Метод, который вызывается RecyclerView при создании нового представления для элемента списка
        override fun onCreateViewHolder (parent: ViewGroup, viewType: Int): RecipeViewHolder {

            //LayoutInflater – это класс, который умеет из содержимого layout-файла создать View-элемент.
            // Метод который это делает называется inflate.

            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recipe, parent, false)
            //Вике. Либо заменить item_recipe на activity_main, либо создать еще xml-файл
            // (в нем распиши элементы с индефикаторами recipeTitle, recipeDescription, recipeImage)
            return RecipeViewHolder(view)
        }

        //нужно подкрепить БД
        override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
            val recipe = recipes[position]
            holder.bind(recipe)
        }

        //нужно?
        override fun getItemCount(): Int {
            return recipes.size
        }

        //Класс связывает данные с View, отображаемым в элементе списка
        inner class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val titleTextView: TextView = itemView.findViewById(R.id.recipeTitle)
            private val descriptionTextView: TextView = itemView.findViewById(R.id.recipeDescription)
            private val imageView: ImageView = itemView.findViewById(R.id.recipeImage)


            fun bind(recipe: Recipe) {
                //функция призывает данные о рецепте(картинки и др)
            }
        }
    }

    //с БД
    fun fillAdapter(){

    }
}
