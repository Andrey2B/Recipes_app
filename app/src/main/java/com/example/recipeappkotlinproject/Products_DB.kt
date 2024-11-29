package com.example.recipeappkotlinproject

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class Products_DB {

    val test_db = FirebaseDatabase.getInstance("https://aaa1-8022d-default-rtdb.firebaseio.com/")

    fun Save_DB(key: String, value: String){
        val database = test_db
        val myRef = database.getReference(key)
        myRef.setValue(value)
        Log.d("SAVE", "Saved")
    }

    fun Read_DB() {
        val database = test_db.reference
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {

                    for (snap in snapshot.children) {

                        val data = snap.value
                        Log.d("FirebaseData", "������: $data")
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

}