package com.example.tsj_app

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.children
import com.example.auto_booking.api.api_resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.util.Date
import java.util.Locale

class AddMeterActivity : AppCompatActivity() {
    var userPhoto: File? = null
    private lateinit var currentPhotoPath: String
    private var currentPhotoBlockView: LinearLayout? = null
    private var meter_id: Int? = null

    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 1
        private const val PERMISSION_REQUEST_CODE = 2
    }
    data class MeterReadingData(
        val meterType: String,
        val value: String,
        val photoUri: Uri
    )

    private lateinit var container: LinearLayout
//    private lateinit var addBlockButton: Button
    private lateinit var save_meter_inf: Button

    private var blockCount = 0
    private val maxBlocks = 5
    private var createdMeterId: Int? = null // этот ID получаем после создания Meters

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_meter)

        val actionBar: ActionBar? = supportActionBar
        actionBar?.hide()

        container = findViewById(R.id.meter_blocks_container)
        //addBlockButton = findViewById(R.id.add_block_button)
        save_meter_inf = findViewById(R.id.save_meter_inf)

        val sharedPreferences = getSharedPreferences("myPreferences", Context.MODE_PRIVATE)
        val profile_id = sharedPreferences.getString("profile_id", "")
        val rawValue = sharedPreferences.getString("count_meters", "")
        val count_meters = rawValue?.toIntOrNull()

        if (count_meters == null) {
            Log.e("DEBUG", "count_meters is null. rawValue = $rawValue")
        } else {
            for (meter in 0 until count_meters) {
                Log.e("ERERWR", "$meter")
                addMeterBlock()
            }
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            // Запрашиваем все необходимые разрешения
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA),
                CreateNewRequestActivity.PERMISSION_REQUEST_CODE
            )
        }

//        addBlockButton.setOnClickListener {
//            // Считаем только блоки с тегом "meter_block"
//            val meterBlockCount = container.children.count { it.tag == "meter_block" }
//
//            Log.d("DEBUG", "Actual meter block count: $meterBlockCount")
//
//            if (meterBlockCount < maxBlocks) {
//                addMeterBlock()
//            } else {
//                Toast.makeText(this, "Максимум 5 счётчиков", Toast.LENGTH_SHORT).show()
//            }
//        }


        save_meter_inf.setOnClickListener {
            var allFieldsValid = true

            for (i in 0 until container.childCount) {
                val blockView = container.getChildAt(i)
                if (blockView.tag != "meter_block") continue

                val spinner = blockView.findViewById<Spinner>(R.id.spinner_type)
                val editValue = blockView.findViewById<EditText>(R.id.edit_value)
                val selectedPhotoUri = blockView.getTag(R.xml.file_paths) as? Uri

                if (spinner == null || editValue == null) {
                    Log.e("DEBUG", "Required views not found in meter block at index $i")
                    allFieldsValid = false
                    break
                }

                val selectedType = spinner.selectedItem?.toString()?.trim()
                val valueText = editValue.text?.toString()?.trim()

                if (selectedType.isNullOrEmpty() || valueText.isNullOrEmpty() || selectedPhotoUri == null) {
                    allFieldsValid = false
                    break
                }
            }

            suspend fun saveMeterReadings() {
                var allSuccessful = true
                val apiResource = api_resource()

                for (i in 0 until container.childCount) {
                    val blockView = container.getChildAt(i)

                    if (blockView.tag != "meter_block") continue

                    val spinner = blockView.findViewById<Spinner>(R.id.spinner_type)
                    val editValue = blockView.findViewById<EditText>(R.id.edit_value)
                    val selectedPhotoUri = blockView.getTag(R.xml.file_paths) as? Uri

                    if (spinner == null || editValue == null) {
                        Log.e("DEBUG", "Missing views in block at index $i")
                        allSuccessful = false
                        continue
                    }

                    val selectedMeterType = spinner.selectedItem?.toString()?.trim()
                    val valueText = editValue.text?.toString()?.trim()

                    if (selectedMeterType.isNullOrEmpty() || valueText.isNullOrEmpty() || selectedPhotoUri == null) {
                        allSuccessful = false
                        continue
                    }

                    try {
                        val user_photo = getFileFromUri(selectedPhotoUri)

                        val result = apiResource.create_meter_readings(
                            meter_id,
                            selectedMeterType,
                            valueText.toInt(),
                            user_photo
                        )

                        if (result.message.isEmpty()) {
                            allSuccessful = false
                        }

                    } catch (e: Exception) {
                        Log.e("ERROR 666", "Error response", e)
                        allSuccessful = false
                    }
                }

                // Один выход на главный поток
                withContext(Dispatchers.Main) {
                    if (allSuccessful) {
                        Toast.makeText(this@AddMeterActivity, "Показания успешно отправлены", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@AddMeterActivity, ProfileActivity::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this@AddMeterActivity, "Произошла ошибка при отправке", Toast.LENGTH_SHORT).show()
                    }
                }
            }



            if (!allFieldsValid) {
                Toast.makeText(this, "Заполните все поля перед сохранением", Toast.LENGTH_SHORT).show()
            } else {
                try {
                    GlobalScope.launch(Dispatchers.Main) {
                        try {
                            // Получение meter_id
                            val data = api_resource()
                            val result = data.create_new_meter(profile_id?.toInt())
                            if (result.message.isNotEmpty()) {
                                meter_id = result.meter_id
                                // После того как meter_id получен, начинаем сохранять показания
                                saveMeterReadings() // Ваша функция для сохранения показаний
                            } else {
                                Toast.makeText(this@AddMeterActivity, "Ошибка", Toast.LENGTH_SHORT).show()
                                Log.e("CreateNewRequestActivity", "Response failed - result is empty")
                            }
                        } catch (e: Exception) {
                            Log.e("CreateNewRequestActivity", "Error during response", e)
                            e.printStackTrace()
                        }
                    }
                } catch (e: Exception) {
                    Log.e("ERROR_RESPONSE", "Error response", e)
                    e.printStackTrace()
                }
            }



        }
    }

    private fun getFileFromUri(uri: Uri): File {
        val inputStream = contentResolver.openInputStream(uri)
        val tempFile = File.createTempFile("photo", ".jpg", cacheDir)
        tempFile.outputStream().use { output ->
            inputStream?.copyTo(output)
        }
        return tempFile
    }
    private fun createMeterOnServer() {
        // Здесь должна быть логика POST-запроса на создание записи Meters
        // После получения ID:
        createdMeterId = 42
        addMeterBlock()
    }

    private fun addMeterBlock() {
        val inflater = LayoutInflater.from(this)
        val blockView = inflater.inflate(R.layout.item_meter_block, container, false)
        blockView.tag = "meter_block"

        val btn_upload_photo = blockView.findViewById<Button>(R.id.btn_upload_photo)

        // Создаем список типов счетчиков
        val meterTypes = listOf("Электричество", "Горячая вода", "Холодная вода", "Отопление")

        // Лог для отладки, чтобы проверить инфлейтинг блока
        Log.d("DEBUG", "Inflated block view: $blockView")

        val spinner = blockView.findViewById<Spinner>(R.id.spinner_type)

        val spinnerAdapter = ArrayAdapter(this, R.layout.spinner_item_black_meter, meterTypes)
        spinnerAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item_black)

        spinner.adapter = spinnerAdapter

        val editText = blockView.findViewById<EditText>(R.id.edit_value)

        btn_upload_photo.setOnClickListener{
            currentPhotoBlockView = blockView as LinearLayout?
            dispatchTakePictureIntent()
        }

        // Добавляем блок в контейнер
        //container.addView(blockView)
        container.addView(blockView, container.childCount - 1)
        blockCount++
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
                    startActivityForResult(takePictureIntent,
                        CreateNewRequestActivity.REQUEST_IMAGE_CAPTURE
                    )
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
    // Обработка результата получения фото
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CreateNewRequestActivity.REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            userPhoto = File(currentPhotoPath) // Сохраняем фото
            val photoURI = Uri.fromFile(userPhoto)

            // Теперь находим только тот блок, к которому привязан currentPhotoBlockView
            currentPhotoBlockView?.let { blockView ->
                val photoPreview = blockView.findViewById<ImageView>(R.id.image_preview)
                photoPreview?.scaleType = ImageView.ScaleType.CENTER_CROP
                photoPreview?.setImageURI(photoURI)

                // сохраняем URI в tag для дальнейшей отправки
                blockView.setTag(R.xml.file_paths, photoURI)
            }
            Toast.makeText(this, "Фото сохранено", Toast.LENGTH_SHORT).show()
        }
    }


    // Обработка результата запроса разрешений
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CreateNewRequestActivity.PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                Log.d("CreateNewRequestActivity", "All permissions granted")
            }
        }
    }
}
