package com.abel.dailyroutinetracker

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.textfield.TextInputEditText

class LoginActivity : AppCompatActivity() {

    private lateinit var imageViewProfile: ImageView
    private lateinit var editTextName: TextInputEditText
    private lateinit var buttonStart: Button
    private var imageUri: Uri? = null
    private lateinit var prefs: SharedPreferences

    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            imageUri = it
            
            // Use Glide to load, crop, and display the image
            Glide.with(this)
                .load(it)
                .apply(RequestOptions.circleCropTransform())
                .into(imageViewProfile)
                
            // Remove padding to show full image
            imageViewProfile.setPadding(0, 0, 0, 0)
            
            // Persist permission to access this URI
            try {
                contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        prefs = getSharedPreferences("daily_routine_prefs", Context.MODE_PRIVATE)
        
        // Check if already logged in
        if (prefs.getBoolean("is_logged_in", false)) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_login)

        imageViewProfile = findViewById(R.id.imageViewProfile)
        editTextName = findViewById(R.id.editTextName)
        buttonStart = findViewById(R.id.buttonStart)

        imageViewProfile.setOnClickListener {
            pickImage.launch("image/*")
        }

        buttonStart.setOnClickListener {
            val name = editTextName.text.toString().trim()
            if (name.isNotEmpty()) {
                saveUserData(name)
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Please enter your name", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveUserData(name: String) {
        val editor = prefs.edit()
        editor.putString("user_name", name)
        imageUri?.let { editor.putString("user_image_uri", it.toString()) }
        editor.putBoolean("is_logged_in", true)
        editor.apply()
    }
}