package com.example.recipeappkotlinproject

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Products_DB() {
    val testDB = "https://aaa1-8022d-default-rtdb.firebaseio.com"
    val realDB = "https://eat-eat-5f6b6-default-rtdb.firebaseio.com"
    private val productsDB = FirebaseDatabase.getInstance(testDB)

    fun dbSave(key: String, value: String) {
        var productsDB = FirebaseDatabase.getInstance()
        val myRef = productsDB.getReference(key)
        myRef.setValue(value)
        Log.d("SAVE_DATA", "saved")
    }

    fun dbRead(){
        val myRef = productsDB.reference

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                for (snap in dataSnapshot.children) {
                    // Чтение данных как Map или преобразование в модель данных
                    val data = snap.value
                    Log.d("FirebaseData", "Данные: $data")
                    Log.d("1111", snap.toString())
                }
            }


            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException())
            }
        })



    }

}