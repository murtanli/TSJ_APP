package com.example.tsj_app

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.auto_booking.api.MeterReadingResponse
import com.example.auto_booking.api.api_resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class CheckMeterReadingActivity : AppCompatActivity() {
    private lateinit var container: LinearLayout
    private lateinit var progressBar: ProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_check_meter_reading)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val actionBar: ActionBar? = supportActionBar
        actionBar?.hide()
        progressBar = findViewById(R.id.progressBar)
        container = findViewById(R.id.Container_blocks)

        val sharedPreferences = getSharedPreferences("myPreferences", Context.MODE_PRIVATE)
        val profile_id = sharedPreferences.getString("profile_id", "")

        GlobalScope.launch(Dispatchers.Main) {
            try {
                val data = api_resource()
                val result = data.get_all_meter_readings(profile_id?.toIntOrNull())
                progressBar.visibility  = View.GONE

                if (result.spisok.isNotEmpty()) {
                    //вызов функции отрисовки блоков
                    for (meter in result.spisok) {
                        addMetersBlock(meter.meter_id, meter.created_at, meter.readings)
                    }



                } else {
                    // Обработка случая, когда список пуст
                    Log.e("BusActivity", "Response failed - result is empty")

                    //val error = createBusEpty()
                    //BusesContainer.addView(error)
                }
            } catch (e: Exception) {
                // Ловим и обрабатываем исключения, например, связанные с сетевыми ошибками
                Log.e("BusActivity", "Error during response", e)
                e.printStackTrace()
            }
        }

    }

    private fun addMetersBlock(meter_id: Int, meter_created_at: String, meter_readings: List<MeterReadingResponse>) {
        val inflater = LayoutInflater.from(this)
        val blockView = inflater.inflate(R.layout.item_meter_reading, container, false)

        val dateblock = blockView.findViewById<TextView>(R.id.textViewCreatedAt)
        val date = formatDateLegacy(meter_created_at)
        dateblock.text = "Показание счетчиков №${meter_id} за ${date}"

        for (meter in meter_readings) {
            addMeterBlock(blockView, meter.type, meter.value, meter.photo_url, meter.created_at)
        }


        // Добавляем блок в контейнер
        container.addView(blockView)
    }

    @SuppressLint("MissingInflatedId")
    private fun addMeterBlock(blockView: View, type: String, value: Double, photo_id: Int, created_at: String){
        val infl = LayoutInflater.from(this)
        val conteiner_block = blockView.findViewById<LinearLayout>(R.id.layout_meters_block)
        val block_meter = infl.inflate(R.layout.item_block_meter, conteiner_block, false)

        val date = formatDateLegacy(created_at, true)

        block_meter.findViewById<TextView>(R.id.text_type).text = "Тип счетчика: $type"
        block_meter.findViewById<TextView>(R.id.text_value).text = "Значение: $value"
        block_meter.findViewById<TextView>(R.id.textViewCreatedAt_meter).text = "Дата: $date"
        val imageView = block_meter.findViewById<ImageView>(R.id.image_preview)

        val imageUrl = "http://194.67.84.26:8000/get_image/$photo_id"
        imageView.scaleType = ImageView.ScaleType.CENTER_CROP
        Glide.with(this)
            .load(imageUrl) // URL изображения
            .placeholder(R.color.white) // Плейсхолдер во время загрузки
            .error(R.color.gray) // Если ошибка загрузки
            .into(imageView)

        // Добавь в readingsContainer
        conteiner_block.addView(block_meter)
    }

    fun formatDateLegacy(original: String, time: Boolean = false): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.getDefault())
        inputFormat.timeZone = TimeZone.getTimeZone("UTC") // учитываем Z — это UTC

        val date = inputFormat.parse(original)

        val outputFormat = if (time) {
            SimpleDateFormat("d MMMM yyyy 'г.' HH:mm", Locale("ru")) // пример: 12 апреля 2025 г. 23:12
        } else {
            SimpleDateFormat("d MMMM yyyy 'г.'", Locale("ru")) // пример: 12 апреля 2025 г.
        }

        return outputFormat.format(date!!)
    }


}