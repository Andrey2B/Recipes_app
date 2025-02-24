package com.example.recipeappkotlinproject

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import android.widget.ImageView


class DetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_detail)

        val name = intent.getStringExtra("RECIPE_NAME")
        val image = intent.getIntExtra("RECIPE_IMAGE", 0)
        val description = intent.getStringExtra("RECIPE_DESCRIPTION")

        val nameTextView: TextView = findViewById(R.id.name)
        val descriptionTextView: TextView = findViewById(R.id.description)
        val imageView: ImageView = findViewById(R.id.image)

        nameTextView.text = name
        descriptionTextView.text = description
        imageView.setImageResource(image)
    }

}