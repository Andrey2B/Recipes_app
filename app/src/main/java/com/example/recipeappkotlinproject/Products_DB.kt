package com.example.recipeappkotlinproject

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.concurrent.CountDownLatch
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException


class Products_DB {


    val test_db = FirebaseDatabase.getInstance("https://aaa1-8022d-default-rtdb.firebaseio.com/")
    val real_db = FirebaseDatabase.getInstance("https://eat-eat-5f6b6-default-rtdb.firebaseio.com/")

    fun Save_DB(key: String, value: String){
        val database = test_db
        val myRef = database.getReference(key)
        myRef.setValue(value)
        myRef.removeValue()
        Log.d("SAVE", "Saved")
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

    fun findRecipeByName(databaseRef: DatabaseReference, recipeName: String) {

        databaseRef.child("recipes").orderByChild("name_recipe").equalTo(recipeName)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (child in snapshot.children) {

                            val recipe = child.value as? Map<String, Any>
                            if (recipe != null) {
                                println("Рецепт найден:")
                                println("ID: ${recipe["id_recipe"]}")
                                println("Название: ${recipe["name_recipe"]}")
                                return
                            }
                        }
                    } else {
                        println("Рецепт с названием \"$recipeName\" не найден")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    println("Ошибка при чтении данных: ${error.message}")
                }
            })
    }


}