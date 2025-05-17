package com.example.tsj_app

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.auto_booking.api.api_resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.Console
import java.io.File
import java.io.IOException
import java.util.Date
import java.util.Locale

class CreateNewRequestActivity : AppCompatActivity() {
    var userPhoto: File? = null
    private lateinit var currentPhotoPath: String

    companion object {
        const val REQUEST_IMAGE_CAPTURE = 1
        const val PERMISSION_REQUEST_CODE = 2
    }

    private lateinit var getCategory: Spinner
    private lateinit var descriptionEditText: EditText
    private lateinit var uploadPhotoButton: Button
    private lateinit var photoPreview: ImageView
    private lateinit var submitButton: Button
    lateinit var progressBar: ProgressBar

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_create_new_request)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val actionBar: ActionBar? = supportActionBar
        actionBar?.hide()

        progressBar = findViewById(R.id.progressBar)
        progressBar.visibility = View.GONE
        val sharedPreferences = getSharedPreferences("myPreferences", Context.MODE_PRIVATE)
        val profile_id = sharedPreferences.getString("profile_id", "")

        getCategory = findViewById(R.id.categorySpinner)
        descriptionEditText = findViewById(R.id.descriptionEditText)
        uploadPhotoButton = findViewById(R.id.uploadPhotoButton)
        photoPreview = findViewById(R.id.photoPreview)
        submitButton = findViewById(R.id.submitButton)

        // Запрашиваем разрешения на доступ к хранилищу и камере
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            // Запрашиваем все необходимые разрешения
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA),
                PERMISSION_REQUEST_CODE
            )
        }

        // Обработчик нажатия на кнопку загрузки фото
        uploadPhotoButton.setOnClickListener {
            dispatchTakePictureIntent() // Снимок с камеры
        }

        GlobalScope.launch(Dispatchers.Main) {
            try {
                val data = api_resource()
                val result = data.get_all_categories()
                if (result.categories.isNotEmpty()) {
                    // Устанавливаем адаптер с кастомным layout для элементов spinner
                    val categoryNames = result.categories.map { it.name }

                    val adapter = ArrayAdapter(
                        this@CreateNewRequestActivity,
                        R.layout.spinner_item_black,  // Ваш кастомный layout для Spinner
                        categoryNames // Используем список строк
                    )
                    adapter.setDropDownViewResource(R.layout.spinner_dropdown_item_black)

                    getCategory.adapter = adapter
                } else {
                    Log.e("CreateNewRequestActivity", "Response failed - result is empty")
                }
            } catch (e: Exception) {
                Log.e("CreateNewRequestActivity", "Error during response", e)
                e.printStackTrace()
            }
        }
        //val selectedCategory = getCategory.selectedItem.toString()
        //Log.e("EREREERER", "ERRRRRRRRRR - $selectedCategory")

        submitButton.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            // Отправка запроса с фото
            if (userPhoto != null) {
                if (descriptionEditText.text != null) {
                    val photoUri = Uri.fromFile(userPhoto)
                    photoPreview.setImageURI(photoUri)

                    GlobalScope.launch(Dispatchers.Main) {
                        try {
                            val profileId = profile_id?.toIntOrNull()
                            val categoryName = getCategory.selectedItem.toString()
                            val description = descriptionEditText.text.toString()
                            Log.e("WWWWW", description)

                            val apiResource = api_resource() // Инициализация API-ресурса
                            val result = apiResource.create_new_request(profileId, categoryName, description, userPhoto)

                            if (result.message.isNotEmpty()) {
                                Log.d("CreateNewRequestActivity", "Request created successfully, ID: ${result.request_id}")
                                Toast.makeText(this@CreateNewRequestActivity, result.message, Toast.LENGTH_SHORT).show()
                                val intent = Intent(this@CreateNewRequestActivity, ProfileActivity::class.java)
                                startActivity(intent)
                            } else {
                                // Ошибка
                                progressBar.visibility = View.GONE
                                Toast.makeText(this@CreateNewRequestActivity, "Ошибка в заполнении заявки", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            progressBar.visibility = View.GONE
                            Toast.makeText(this@CreateNewRequestActivity, "Ошибка сервера", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    progressBar.visibility = View.GONE
                    Toast.makeText(this@CreateNewRequestActivity, "Введите описание заявки", Toast.LENGTH_SHORT).show()
                }

            } else {
                Log.e("CreateNewRequestActivity", "Не выбрано фото")
            }
        }
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                // Создаем файл для фото
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    Log.e("Error", "Error creating image file: ${ex.message}", ex)
                    null
                }
                photoFile?.also {
                    val photoURI = FileProvider.getUriForFile(
                        this,
                        "com.example.tsj_app.fileprovider", // Убедитесь, что это правильно настроено в AndroidManifest
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }

    // Создание файла для сохранения фото
    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        // Используем getExternalFilesDir, который является лучшей практикой для работы с хранилищем
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    // Обработка результата получения фото
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            userPhoto = File(currentPhotoPath) // Сохраняем фото
            val photoURI = Uri.fromFile(userPhoto)
            val photoPreview = findViewById<ImageView>(R.id.photoPreview)
            photoPreview.setImageURI(photoURI) // Отображаем изображение в интерфейсе
            Toast.makeText(this, "Файл загружен", Toast.LENGTH_SHORT).show()
        }
    }

    // Обработка результата запроса разрешений
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                Log.d("CreateNewRequestActivity", "All permissions granted")
            }
        }
    }
}
