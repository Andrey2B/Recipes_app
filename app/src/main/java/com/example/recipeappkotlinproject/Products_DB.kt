package com.example.recipeappkotlinproject

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class Products_DB {

    data class Recipe(
        val id_recipe: String = "",
        val name_recipe: String = "",
        val image_url: String = "",
        val time_recipe: String = ""
    )

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

    fun saveRecipeToDatabase(
        databaseRef: DatabaseReference,
        recipe: Recipe,

        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        databaseRef.child("recipes").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Определяем максимальный id_recipe
                var maxId = 0
                for (recipeSnapshot in snapshot.children) {
                    val idRecipe = recipeSnapshot.child("id_recipe").value.toString().toInt()
                    if (idRecipe != null && idRecipe > maxId) {
                        maxId = idRecipe
                    }
                }


                val newId = maxId + 1


                val recipeWithId = recipe.copy(id_recipe = newId.toString())

                // Сохраняем данные рецепта
                databaseRef.child("recipes").child(newId.toString()).setValue(recipeWithId)
                    .addOnSuccessListener {
                        onSuccess()
                    }
                    .addOnFailureListener { error ->
                        onError(error.message ?: "Неизвестная ошибка")
                    }
            }

            override fun onCancelled(error: DatabaseError) {
                onError(error.message)
            }
        })
    }

    data class User(
        val id_user: String = "",
        val name_user: String = "",
        val password: String = "",
        val id_favourite_recipes: String = ""
    )

    fun deleteUser(
        databaseRef: DatabaseReference,
        id_user: Int
    )
    {
        databaseRef.child("users").child(id_user.toString())
    }

    fun saveUserToDatabase(
        databaseRef: DatabaseReference,
        user: User,
        onSuccess: () -> Unit,
        onError: (String) -> Unit)
    {
        databaseRef.child("users").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Определяем максимальный id_user
                var maxId = 0
                for (userSnapshot in snapshot.children) {
                    val idUser = userSnapshot.child("id_user").value.toString().toInt()
                    if (idUser != null && idUser > maxId) {
                        maxId = idUser
                    }
                }

                // Новый id_user - это максимум + 1
                val newId = maxId + 1

                // Создаём нового пользователя с новым id_user
                val userWithId = user.copy(id_user = newId.toString())

                // Сохраняем данные пользователя
                databaseRef.child("users").child(newId.toString()).setValue(userWithId)
                    .addOnSuccessListener {
                        onSuccess()
                    }
                    .addOnFailureListener { error ->
                        onError(error.message ?: "Неизвестная ошибка")
                    }
            }

            override fun onCancelled(error: DatabaseError) {
                onError(error.message)
            }
        })
    }

    fun Read_DB() {
        val database = real_db.reference
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    println(snapshot.value.toString())
                    println(snapshot.child("users").child("5").child("id_favourite_recipes").value?.javaClass?.name)
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

    fun getFavoriteRecipes(userId: Int, onSuccess: (List<Recipe_fav>) -> Unit, onFailure: (String) -> Unit) {
        val database = real_db.reference

        //Get a list of favorite recipes for the user by id
        database.child("users").child(userId.toString()).child("id_favourite_recipes")
            .get()
            .addOnSuccessListener { snapshot ->
                val favoriteIds = snapshot.value.toString()
                    .split(",")
                    .mapNotNull { it.trim().toIntOrNull() }

                if (favoriteIds.isEmpty()) {
                    onFailure("No favorite recipes found for user.")
                    return@addOnSuccessListener
                }

                //Load data from tables of recipes by id
                database.child("recipes").addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val favoriteRecipes = favoriteIds.mapNotNull { id ->
                            val recipeSnapshot = dataSnapshot.child(id.toString())
                            if (recipeSnapshot.exists()) {
                                Recipe_fav(
                                    name = recipeSnapshot.child("name_recipe").value.toString(),
                                    image = recipeSnapshot.child("image_url").value.toString()
                                )
                            } else null
                        }

                        if (favoriteRecipes.isEmpty()) {
                            onFailure("No recipes found in the database.")
                        } else {
                            onSuccess(favoriteRecipes)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        onFailure("Database error: ${error.message}")
                    }
                })
            }
            .addOnFailureListener {
                onFailure("Failed to fetch user data: ${it.message}")
            }
    }


}