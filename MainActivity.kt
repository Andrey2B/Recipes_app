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
import androidx.recyclerview.widget.LinearLayoutManager

data class Recipe(
    val name: String,
    val image: Int,
    val description: String
)

////type???
class RecyclerAdapter(private var recipes: List<Recipe>) : RecyclerView.Adapter<RecyclerAdapter.RecipeViewHolder>() {

    //Метод, который вызывается RecyclerView при создании нового представления для элемента списка
    override fun onCreateViewHolder (parent: ViewGroup, viewType: Int): RecipeViewHolder {

        //LayoutInflater – это класс, который умеет из содержимого layout-файла создать View-элемент.
        // Метод который это делает называется inflate.

        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_recipe, parent, false)
        //Вике. Создай отдельнвй layout (item_recipe)
        // (в нем распиши элементы с индефикаторами recipeTitle, recipeDescription, recipeImage),
        //т.е. все, что юует содержаться в ячейке с рецептом
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = recipes[position]
        holder.bind(recipe)
    }

    override fun getItemCount(): Int {
        return recipes.size
    }

    //Класс связывает данные с View, отображаемым в элементе списка
    inner class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.recipeTitle)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.recipeDescription)
        private val imageView: ImageView = itemView.findViewById(R.id.recipeImage)

        //функция призывает данные о рецепте(картинки и др)
        fun bind(recipe: Recipe) {
            titleTextView.text = recipe.name
            descriptionTextView.text = recipe.description
            imageView.setImageResource(recipe.image)
        }
    }

    //Заполним адаптер данными с БД
    fun fillAdapter(){

    }
}


class MainActivity : AppCompatActivity() {

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

        //Пример рецептов без БД
        //Вика попытайся прикрепить картинки с 3 рецептами (картинки в drawable), нужно для кода под комментом
        //Если что-то исправишь - пиши

        val recipes = listOf(
            Recipe("Рецепт1", R.drawable.ic_launcher_foreground, "Описание1"),
            Recipe("Рецепт2", R.drawable.ic_launcher_foreground, "Описание2"),
            Recipe("Рецепт3", R.drawable.ic_launcher_foreground, "Описание3")
        )

        val searchRecipe: SearchView
        val recipesList: RecyclerView

        searchRecipe = findViewById(R.id.searchRecipe)
        recipesList = findViewById(R.id.recipesList)

        //установим для RecycleView LayoutManager и Adapter
        recipesList.layoutManager = LinearLayoutManager(this)
        recipesList.adapter = RecyclerAdapter(recipes)


        //Обработка запросов поиска

    }

}
