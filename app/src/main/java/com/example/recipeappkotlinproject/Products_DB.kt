package com.example.recipeappkotlinproject

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class Products_DB {

    data class Recipe(val image_url: String, val name_recipe: String, val id_recipe: String)

    val test_db = FirebaseDatabase.getInstance("https://aaa1-8022d-default-rtdb.firebaseio.com/")
    val real_db = FirebaseDatabase.getInstance("https://eat-eat-5f6b6-default-rtdb.firebaseio.com/")

    fun Save_DB(key: String, value: String):Int {
        val database = test_db
        val myRef = database.getReference(key)
        myRef.setValue(value)
        myRef.removeValue()
        Log.d("SAVE", "Saved")
        return 1
    }

    fun Read_DB() {
        val database = real_db.reference
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    println(snapshot.value.toString())
                    println(snapshot.child("recipes").child("1").child("name_recipe").value)
                    for (snap in snapshot.children) {

                        val data = snap.value
                        Log.d("FirebaseData", "Данные: $data")
                        Log.d("READ_DB",snap.toString())
                    }
                } else {
                    Log.d("EMPTY", "EMPTY data")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("READ_ERR", "read error: ${error.message}")
            }
        })
    }


    fun findRecipeByName(databaseRef: DatabaseReference, keyword: String, resultCallback: (List<Recipe>) -> Unit) {
        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val recipesList = mutableListOf<Pair<Recipe, Int>>()

                if (snapshot.exists()) {
                    for (snap in snapshot.child("recipes").children) {
                        val recipeMap = snap.value as? Map<*, *>
                        println(recipeMap.toString())
                        if (recipeMap != null) {
                            val recipe = Recipe(
                                id_recipe = recipeMap["id_recipe"]?.toString() ?: "",
                                name_recipe = recipeMap["name_recipe"]?.toString() ?: "",
                                image_url = recipeMap["image_url"]?.toString() ?: ""
                            )
                            val count = countKeywordOccurrences(recipe.name_recipe, keyword)
                            if (count > 0) {
                                recipesList.add(recipe to count)
                            }
                        }
                    }
                }

                val sortedRecipes = recipesList.sortedByDescending { it.second }.map { it.first }

                resultCallback(sortedRecipes)
            }

            override fun onCancelled(error: DatabaseError) {
                println("Ошибка при чтении данных: ${error.message}")
                resultCallback(emptyList())
            }
        })
    }

    fun countKeywordOccurrences(text: String, keyword: String): Int {
        return Regex(keyword, RegexOption.IGNORE_CASE).findAll(text).count()
    }



}