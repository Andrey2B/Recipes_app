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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.FirebaseStorage
import java.io.File
import java.io.IOException


class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var imageView : ImageView //for prof_pic
    private var imageUri: Uri? = null



    //Register a handler for the camera
    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
        if (isSuccess) {
            imageUri?.let {
                imageView.setImageURI(it) //Displaying photos in ImageView
            }
        } else {
            Toast.makeText(this, "Camera failed to capture image", Toast.LENGTH_SHORT).show()
        }
    }

    //Register a handler for selecting photos from the gallery
    private val openGalleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            imageView.setImageURI(it) //Displaying photos in ImageView
        }
    }

    private val galleryRequestCode = 100
    private val cameraRequestCode = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.user_reg)

        //Initializing FirebaseAuth
        auth = FirebaseAuth.getInstance()
        imageView = findViewById(R.id.imageView)

        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val confirmPasswordEditText = findViewById<EditText>(R.id.confirmPasswordEditText)
        val nicknameEditText = findViewById<EditText>(R.id.nicknameEditText)
        val registerButton = findViewById<Button>(R.id.regButton)


        val selectPhotoButton = findViewById<Button>(R.id.selectPhotoButton)

        selectPhotoButton.setOnClickListener{
            showImageSourceDialog()
        }

        registerButton.setOnClickListener {
            val nickname = nicknameEditText.text.toString()
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            val confirmPassword = confirmPasswordEditText.text.toString()

            if (email.isNotEmpty() && nickname.isNotEmpty() && password.isNotEmpty()  && confirmPassword.isNotEmpty()) {
                if (password != confirmPassword) {
                    Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                checkNicknameUnique(nickname, email, password)
            } else {
                Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkNicknameUnique(nickname: String, email: String, password: String) {
        val database = FirebaseDatabase.getInstance().reference

        //Request to the database to check if such a nickname already exists
        database.child("users").orderByChild("name_user").equalTo(nickname).get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    Toast.makeText(this, "Nickname already taken. Please choose another.", Toast.LENGTH_SHORT).show()
                } else {
                    saveUserToDatabase(nickname, email, password)
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Error checking nickname: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            galleryRequestCode -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Permission to read the gallery has been received, open the gallery
                    openGallery()
                } else {
                    Toast.makeText(this, "Permission denied for gallery", Toast.LENGTH_SHORT).show()
                }
            }
            cameraRequestCode -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Permission for the camera has been received, open the camera
                    openCamera()
                } else {
                    Toast.makeText(this, "Permission denied for camera", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }



    //Show dialog for selecting photo source (gallery or camera)
    private fun showImageSourceDialog() {
        val options = arrayOf("Gallery", "Camera")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select Image Source")
        builder.setItems(options) { dialog, which ->
            when (which) {
                0 -> openGallery()  //Gallery
                1 -> openCamera()   //Camera
            }
        }
        builder.show()
    }


    private fun openGallery() {
        openGalleryLauncher.launch("image/*")
    }

    private fun openCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            try {
                //Create a file for photo
                val photoFile = createImageFile()
                //Getting the URI for a file using FileProvider
                imageUri = FileProvider.getUriForFile(
                    this,
                    "com.example.recipeappkotlinproject.fileprovider",
                    photoFile
                )
                //Launch the camera with the correct URI
                imageUri?.let {
                    takePictureLauncher.launch(it)
                } ?: run {
                    Toast.makeText(this, "Error: Image URI is null", Toast.LENGTH_SHORT).show()
                }
            } catch (ex: IOException) {
                Toast.makeText(this, "Error creating photo file", Toast.LENGTH_SHORT).show()
            }
        } else {
            //If there is no permission for the camera, request it
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), cameraRequestCode)
        }
    }


    // Create a file for photo
    @Throws(IOException::class)
    private fun createImageFile(): File {
        val imageFileName = "profile_photo"
        val storageDir: File = getExternalFilesDir(null) ?: throw IOException("Unable to get storage directory")
        return File.createTempFile(imageFileName, ".jpg", storageDir)
    }

    //Processing the result of selecting a photo or shooting
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                galleryRequestCode -> {
                    val selectedImageUri: Uri? = data?.data
                    selectedImageUri?.let {
                        imageView.setImageURI(it)
                    }
                }
                cameraRequestCode -> {
                    imageUri?.let {
                        imageView.setImageURI(it)
                    }
                }
            }
        }
    }


    private fun saveUserToDatabase(nickname: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    Toast.makeText(this, "Registration successful for ${user?.email}", Toast.LENGTH_SHORT).show()

                    //Create a User object
                    val newUser = Products_DB.User(
                        id_user = user?.uid.toString(),  //Use the user UID for id_user
                        name_user = nickname,
                        email = email,
                        password = password,
                        id_favourite_recipes = ""
                    )

                    //Checking if the photo has been selected
                    if (imageUri != null) {
                        //Uploading photos to Firebase Storage
                        val storageRef = FirebaseStorage.getInstance().reference.child("profile_photos/${user?.uid}.jpg")
                        storageRef.putFile(imageUri!!)
                            .addOnSuccessListener { taskSnapshot ->
                                //We get a link to the image after downloading
                                storageRef.downloadUrl.addOnSuccessListener { uri ->
                                    //Add a link to the photo to the user object in the face_pic field
                                    newUser.face_pic = uri.toString()

                                    //Save the user in Firebase Database
                                    val usersRef = database.child("users")
                                    usersRef.child(user?.uid ?: "").setValue(newUser)
                                        .addOnCompleteListener { dbTask ->
                                            if (dbTask.isSuccessful) {
                                                Toast.makeText(this, "User saved to database", Toast.LENGTH_SHORT).show()
                                                //Go to the Home screen
                                                val intent = Intent(this, MainActivity::class.java)
                                                startActivity(intent)
                                                finish()  //Close the registration screen
                                            } else {
                                                Toast.makeText(this, "Failed to save user in database", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                }
                            }
                            .addOnFailureListener { exception ->
                                Toast.makeText(this, "Failed to upload photo: ${exception.message}", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        //If no photo is selected, save the user without photo
                        val usersRef = database.child("users")
                        usersRef.child(user?.uid ?: "").setValue(newUser)
                            .addOnCompleteListener { dbTask ->
                                if (dbTask.isSuccessful) {
                                    Toast.makeText(this, "User saved to database", Toast.LENGTH_SHORT).show()
                                    //Go to the Home screen
                                    val intent = Intent(this, MainActivity::class.java)
                                    startActivity(intent)
                                    finish()  //Close the registration screen
                                } else {
                                    Toast.makeText(this, "Failed to save user in database", Toast.LENGTH_SHORT).show()
                                }
                            }
                    }

                    val database = FirebaseDatabase.getInstance().reference
                    val productsDb = Products_DB()

                    productsDb.saveUserToDatabase(database, newUser, {
                        Toast.makeText(this, "User saved in DB", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }, {
                        Toast.makeText(this, "Failed to save user: $it", Toast.LENGTH_SHORT).show()
                    })
                } else {
                    Toast.makeText(this, "${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

}