package com.example.recipeappkotlinproject

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.io.IOException


class ProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    private lateinit var profileImageView: ImageView
    private lateinit var nicknameTextView: TextView
    private lateinit var emailTextView: TextView

    // Register for activity result to pick image from gallery
    private val getImageFromGallery = registerForActivityResult(ActivityResultContracts.GetContent(), ActivityResultCallback { uri ->
        uri?.let {
            // Updating your profile picture
            profileImageView.setImageURI(it)

            // Upload the photo to Firebase Storage and update the link in the database
            //uploadProfileImageToFirebase(it)
        }
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        profileImageView = findViewById(R.id.profileImageView)
        nicknameTextView = findViewById(R.id.nicknameTextView)
        emailTextView = findViewById(R.id.emailTextView)
        val changePasswordButton : Button = findViewById(R.id.changePasswordButton)
        val changePhotoButton : Button = findViewById(R.id.changePhotoButton)
        val logoutButton : Button = findViewById(R.id.logoutButton)
        val deleteAccountButton : Button = findViewById(R.id.deleteAccountButton)
        val deletePhotoButton : Button = findViewById<Button>(R.id.deletePhotoButton)


        //Get the current user's data
        val currentUser = auth.currentUser

        if (currentUser != null) {
            val userId = currentUser.uid
            loadUserData(userId)
        }

        changePasswordButton.setOnClickListener {
            //Logic of change
            //Go to activity to change password
            //val intent = Intent(this, ChangePasswordActivity::class.java)
            //startActivity(intent)
        }

        changePhotoButton.setOnClickListener {
            //Open the gallery to select a new image
            getImageFromGallery.launch("image/*")
        }

        logoutButton.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        deleteAccountButton.setOnClickListener {
            deleteAccount()
        }

        //deletePhotoButton.setOnClickListener {
        //    deleteProfilePhoto()
        //}
    }

    private fun loadUserData(userId: String) {
        val currentUser = auth.currentUser
        val userEmail = currentUser?.email

        database.child("users").get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists() && snapshot.hasChildren()) {
                    // Iterate through all elements of the users list
                    for (child in snapshot.children) {
                        val email = child.child("email").getValue(String::class.java)
                        if (email == userEmail) {
                            val nickname = child.child("name_user").getValue(String::class.java)
                            val idUser = child.child("id_user").getValue(String::class.java)

                            if (nickname != null && idUser != null) {
                                nicknameTextView.text = nickname
                                emailTextView.text = userEmail
                                Log.i("ProfileActivity", "User data loaded successfully")
                            } else {
                                Log.e("FirebaseData", "Some user data is missing")
                            }
                            return@addOnSuccessListener //Data found, exit the loop
                        }
                    }
                    //If a user with this email is not found
                    Log.e("FirebaseData", "User with email: $userEmail not found")
                } else {
                    Log.e("FirebaseData", "No user data available")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("FirebaseData", "Error when requesting data", exception)
            }
    }



    private fun deleteAccount() {
        val user = auth.currentUser
        val userEmail = user?.email

        // Create a dialog for entering a password
        val passwordEditText = EditText(this)
        passwordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

        val dialog = AlertDialog.Builder(this)
            .setTitle("Введите пароль")
            .setMessage("Пожалуйста, введите ваш пароль для подтверждения удаления аккаунта.")
            .setView(passwordEditText)
            .setPositiveButton("Подтвердить") { _, _ ->

                val password = passwordEditText.text.toString()

                if (password.isEmpty()) {
                    Toast.makeText(this, "Введите пароль.", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                // Creating credentials for re-authentication
                val credential = userEmail?.let { EmailAuthProvider.getCredential(it, password) }

                if (credential == null) {
                    Toast.makeText(this, "Ошибка: невозможно создать учетные данные.", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                // Do re-authentication
                user.reauthenticate(credential).addOnCompleteListener { reauthTask ->
                    if (reauthTask.isSuccessful) {
                        deleteUserData(user.uid) // Remove data from the database
                    } else {
                        Toast.makeText(this, "Reauthentication failed: ${reauthTask.exception?.message}", Toast.LENGTH_SHORT).show()
                        Log.e("DeleteAccount", "Reauthentication error: ${reauthTask.exception?.message}")
                    }
                }
            }
            .setNegativeButton("Отменить") { dialog, _ ->
                dialog.dismiss()
            }

        dialog.show()
    }

    private fun deleteUserData(userId: String) {
        // Remove data from Firebase Realtime Database
        val userRef = database.child("users").child(userId)

        userRef.removeValue()
            .addOnSuccessListener {
                // The data has been deleted, now we delete the user account
                val user = auth.currentUser
                user?.delete()?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Аккаунт успешно удален.", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, "Не удалось удалить аккаунт: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        Log.e("DeleteAccount", "Error: ${task.exception?.message}")
                    }
                }
            }
            .addOnFailureListener { exception ->
                // Если не удалось удалить данные пользователя из базы
                Toast.makeText(this, "Не удалось удалить данные пользователя: ${exception.message}", Toast.LENGTH_SHORT).show()
                Log.e("DeleteAccount", "Error deleting user data: ${exception.message}")
            }
    }


}