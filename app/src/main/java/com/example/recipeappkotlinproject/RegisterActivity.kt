package com.example.recipeappkotlinproject

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import android.Manifest
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import com.example.recipeappkotlinproject.databinding.UserRegBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.io.File
import java.io.IOException
import com.yandex.disk.rest.Credentials
import com.yandex.disk.rest.RestClient


class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: UserRegBinding
    private var imageUri: Uri? = null

    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
        if (isSuccess) {
            imageUri?.let {
                binding.profileImageView.setImageURI(it)
            }
        } else {
            Toast.makeText(this, "Не удалось сделать снимок.", Toast.LENGTH_SHORT).show()
        }
    }

    private val openGalleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            binding.profileImageView.setImageURI(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = UserRegBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        binding.regButton.setOnClickListener {
            signUpUser()
        }

        binding.selectPhotoButton.setOnClickListener {
            showImageSourceDialog()
        }
    }

    private fun signUpUser() {
        val email = binding.emailEditText.text.toString().trim()
        val pass = binding.passwordEditText.text.toString().trim()
        val confirmPassword = binding.confirmPasswordEditText.text.toString().trim()
        val nickname = binding.nicknameEditText.text.toString().trim()

        if (email.isBlank() || pass.isBlank() || confirmPassword.isBlank() || nickname.isBlank()) {
            Toast.makeText(this, "Все обязательные поля должны быть заполнены.", Toast.LENGTH_SHORT).show()
            return
        }

        if (pass != confirmPassword) {
            Toast.makeText(this, "Пароли не совпадают.", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d("RegisterActivity", "Checking nickname uniqueness")
        checkNicknameUnique(nickname) { isUnique ->
            Log.d("RegisterActivity", "Nickname uniqueness check finished")
            if (!isUnique) {
                Toast.makeText(this, "Никнейм уже занят, выберите другой.", Toast.LENGTH_SHORT).show()
            } else {
                Log.d("RegisterActivity", "Creating user in Firebase")
                auth.createUserWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            Log.d("RegisterActivity", "User created successfully")
                            imageUri?.let {
                                uploadProfileImageToYandexDisk(it) { imageUrl ->
                                    saveUserToDatabase(nickname, email, imageUrl)
                                }
                            } ?: saveUserToDatabase(nickname, email, null)
                        } else {
                            Toast.makeText(this, "Ошибка регистрации: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
    }


    private fun checkNicknameUnique(nickname: String, callback: (Boolean) -> Unit) {
        val database = FirebaseDatabase.getInstance().reference
        database.child("users")
            .orderByChild("name_user")
            .equalTo(nickname)
            .get()
            .addOnSuccessListener { snapshot ->
                Log.d("RegisterActivity", "Nickname check successful: ${snapshot.exists()}")
                if (snapshot.exists()) {
                    Log.d("RegisterActivity", "Nickname already exists")
                } else {
                    Log.d("RegisterActivity", "Nickname is unique")
                }
                callback(!snapshot.exists())  // Никнейм уникален, если его нет в базе
            }
            .addOnFailureListener { e ->
                Log.e("RegisterActivity", "Error checking nickname: ${e.message}")
                Toast.makeText(this, "Ошибка проверки никнейма.", Toast.LENGTH_SHORT).show()
                callback(false)
            }
    }

    private fun saveUserToDatabase(nickname: String, email: String, profileImageUrl: String?) {
        val userId = auth.currentUser?.uid ?: return
        val database = FirebaseDatabase.getInstance().reference

        val user = mapOf(
            "id_user" to userId,
            "name_user" to nickname,
            "email" to email,
            "id_favourite_recipes" to "",
            "face_pic" to (profileImageUrl ?: "")
        )

        database.child("users").child(userId).setValue(user)
            .addOnSuccessListener {
                Log.d("RegisterActivity", "Данные пользователя успешно сохранены в Firebase.")
                Toast.makeText(this, "Регистрация завершена успешно!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            .addOnFailureListener { e ->
                Log.e("RegisterActivity", "Ошибка сохранения данных: ${e.message}")
                Toast.makeText(this, "Ошибка сохранения данных пользователя: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun uploadProfileImageToYandexDisk(imageUri: Uri, callback: (String?) -> Unit) {
        val accessToken = "y0__wgBEMeJlMkBGMyKNCDQuNDtEUkzRTuBEJdy2AesftXpV8eXwaFL&token_type=bearer&expires_in=31536000"
        val credentials = Credentials("com.example.recipeappkotlinproject", accessToken)
        val restClient = RestClient(credentials)

        val file = File(imageUri.path ?: run {
            Toast.makeText(this, "Ошибка: файл изображения не найден.", Toast.LENGTH_SHORT).show()
            return callback(null)
        })

        if (!file.exists()) {
            Toast.makeText(this, "Файл изображения не существует.", Toast.LENGTH_SHORT).show()
            return callback(null)
        }

        val remotePath = "/profile_photos/${file.name}"

        try {
            // Upload the file to Yandex Disk
            //restClient.uploadFile(remotePath, true, file, null)

            // Receive a link to publish the file
            val link = restClient.publish(remotePath)

            // Check and return the link to the downloaded file
            callback(link.href)
        } catch (e: Exception) {
            Log.e("RegisterActivity", "Error uploading to Yandex.Disk: ${e.message}")
            Toast.makeText(this, "Ошибка загрузки фотографии профиля.", Toast.LENGTH_SHORT).show()
            callback(null)
        }
    }



    private fun showImageSourceDialog() {
        val options = arrayOf("Галерея", "Камера")
        AlertDialog.Builder(this)
            .setTitle("Выберите источник изображения")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> openGallery()
                    1 -> openCamera()
                }
            }
            .show()
    }

    private fun openGallery() {
        openGalleryLauncher.launch("image/*")
    }

    private fun openCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            try {
                val photoFile = createImageFile()
                imageUri = FileProvider.getUriForFile(
                    this,
                    "com.example.recipeappkotlinproject.fileprovider",
                    photoFile
                )
                imageUri?.let { takePictureLauncher.launch(it) }
            } catch (ex: IOException) {
                Toast.makeText(this, "Ошибка создания файла для фото", Toast.LENGTH_SHORT).show()
            }
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 101)
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val storageDir: File = getExternalFilesDir(null) ?: throw IOException("Невозможно получить каталог для хранения")
        return File.createTempFile("profile_photo", ".jpg", storageDir)
    }
}
