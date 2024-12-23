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

    // UI elements
    private lateinit var profileImageView: ImageView
    private lateinit var nicknameTextView: TextView
    private lateinit var emailTextView: TextView

    // Register for activity result to pick image from gallery
    private val getImageFromGallery = registerForActivityResult(ActivityResultContracts.GetContent(), ActivityResultCallback { uri ->
        uri?.let {
            // Обновляем изображение профиля
            profileImageView.setImageURI(it)

            // Загружаем фотографию в Firebase Storage и обновляем ссылку в базе данных
            uploadProfileImageToFirebase(it)
        }
    })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        // Инициализация UI элементов
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
            startActivity(intent)
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

        deletePhotoButton.setOnClickListener {
            deleteProfilePhoto()
        }
    }

    private fun loadUserData(userId: String) {
        val currentUser = auth.currentUser
        val userEmail = currentUser?.email

        // Запрос к базе данных
        database.child("users").get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists() && snapshot.hasChildren()) {
                    // Проходим по всем элементам массива users
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

    private fun uploadProfileImageToFirebase(uri: Uri) {
        //Upload an image to Firebase Storage
        val storageReference = FirebaseStorage.getInstance().getReference("profile_pics/${auth.currentUser?.uid}")

        storageReference.putFile(uri)
            .addOnSuccessListener {
                storageReference.downloadUrl.addOnSuccessListener { downloadUri ->
                    //Save a link to an image in the Firebase database
                    val database = FirebaseDatabase.getInstance().getReference("users").child(auth.currentUser?.uid!!)
                    database.child("face_pic").setValue(downloadUri.toString())
                        .addOnSuccessListener {
                            Toast.makeText(this, "Profile picture updated successfully", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Failed to update profile picture: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to upload image: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun deleteProfilePhoto() {
        //Remove a profile photo from Firebase Storage
        val storageReference = FirebaseStorage.getInstance().getReference("profile_pics/${auth.currentUser?.uid}")
        storageReference.delete()
            .addOnSuccessListener {
                //Remove a link to a photo from the Firebase Realtime Database
                val database = FirebaseDatabase.getInstance().getReference("users").child(auth.currentUser?.uid!!)
                database.child("face_pic").setValue("")
                    .addOnSuccessListener {
                        Toast.makeText(this, "Profile picture deleted successfully", Toast.LENGTH_SHORT).show()
                        //profileImageView.setImageResource(R.drawable.default_profile_picture)
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Failed to delete profile picture: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to delete profile picture: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun deleteAccount() {
        // Получаем текущего пользователя
        val user = auth.currentUser
        val userEmail = user?.email

        // Создаем диалог для ввода пароля
        val passwordEditText = EditText(this)
        passwordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

        val dialog = AlertDialog.Builder(this)
            .setTitle("Enter Password")
            .setMessage("Please enter your password to confirm account deletion.")
            .setView(passwordEditText)
            .setPositiveButton("Confirm") { _, _ ->
                val password = passwordEditText.text.toString()

                // Проверяем, что введен пароль
                if (password.isEmpty()) {
                    Toast.makeText(this, "Password cannot be empty.", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                // Создаем учетные данные для повторной аутентификации
                val credential = userEmail?.let { EmailAuthProvider.getCredential(it, password) }

                // Проверяем, что credential не равно null
                if (credential == null) {
                    Toast.makeText(this, "Error: Unable to create credentials.", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                // Выполняем повторную аутентификацию
                user.reauthenticate(credential).addOnCompleteListener { reauthTask ->
                    if (reauthTask.isSuccessful) {
                        // Повторная аутентификация успешна, теперь можно удалять аккаунт
                        user.delete().addOnCompleteListener { deleteTask ->
                            if (deleteTask.isSuccessful) {
                                // Если аккаунт успешно удален
                                Toast.makeText(this, "Account deleted successfully", Toast.LENGTH_SHORT).show()

                                // Переход на экран регистрации
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                                finish()  // Завершаем текущую активность
                            } else {
                                // Если не удалось удалить аккаунт, показываем ошибку
                                Toast.makeText(this, "Failed to delete account: ${deleteTask.exception?.message}", Toast.LENGTH_SHORT).show()
                                Log.e("DeleteAccount", "Error: ${deleteTask.exception?.message}")
                            }
                        }
                    } else {
                        // Если повторная аутентификация не удалась
                        Toast.makeText(this, "Reauthentication failed: ${reauthTask.exception?.message}", Toast.LENGTH_SHORT).show()
                        Log.e("DeleteAccount", "Reauthentication error: ${reauthTask.exception?.message}")
                    }
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }

        // Показываем диалог
        dialog.show()
    }
}