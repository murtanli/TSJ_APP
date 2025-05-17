package com.example.tsj_app

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.auto_booking.api.api_resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class CheckRequestActivity : AppCompatActivity() {

    private lateinit var linear_container: LinearLayout
    lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_check_request)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        progressBar = findViewById(R.id.progressBar)
        linear_container = findViewById(R.id.Container_blocks)
        val sharedPreferences = getSharedPreferences("myPreferences", Context.MODE_PRIVATE)
        val profile_id = sharedPreferences.getString("profile_id", "")

        val actionBar: ActionBar? = supportActionBar
        actionBar?.hide()


        progressBar.visibility = View.GONE

        GlobalScope.launch(Dispatchers.Main) {
            try {
                progressBar.visibility = View.VISIBLE
                val data = api_resource()
                val result = data.get_all_requests(
                    profile_id?.toIntOrNull()
                )
                if (result.isNotEmpty()) {
                    //вызов функции отрисовки блоков

                    val title = TextView(this@CheckRequestActivity)
                    title.text = "Заявки"
                    title.setPadding(100,100,100,100)
                    title.gravity = Gravity.CENTER
                    title.setTextColor(ContextCompat.getColor(this@CheckRequestActivity, R.color.black))
                    title.textSize = 30F

                    linear_container.addView(title)

                    for (request in result) {
                        val block = createBlockRequest(
                            request.category_name,
                            request.description,
                            request.status,
                            request.photo_url_id,
                            request.created_at
                        )
                        linear_container.addView(block)
                        linear_container.gravity = Gravity.CENTER
                    }
                    progressBar.visibility = View.GONE

                } else {
                    // Обработка случая, когда список пуст
                    Log.e("BusActivity", "Response failed - result is empty")

                    //val error = createBusEpty()
                    //BusesContainer.addView(error)
                    progressBar.visibility = View.GONE
                }
            } catch (e: Exception) {
                // Ловим и обрабатываем исключения, например, связанные с сетевыми ошибками
                Log.e("BusActivity", "Error during response", e)
                e.printStackTrace()
                progressBar.visibility = View.GONE
            }
        }

    }


    private fun createBlockRequest(
        category_name: String,
        description: String,
        status: String,
        imageId: Int,
        createdAt: String // Дата создания
    ): LinearLayout {

        val context = this

        // Общий блок
        val block = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            background = ContextCompat.getDrawable(context, R.drawable.rounded_background)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(30, 15, 30, 15) // Отступы по бокам и между блоками
            }
            setPadding(20, 20, 20, 20) // Внутренние отступы
            elevation = 8f // Тень для красивого эффекта
        }

        // Категория новости
        val categoryTextView = TextView(context).apply {
            text = category_name.uppercase()
            setTextColor(ContextCompat.getColor(context, R.color.white))
            setBackgroundResource(R.drawable.category_background) // Фон для категории
            setPadding(20, 5, 20, 5)
            textSize = 12f
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.START
                bottomMargin = 10
            }
        }

        // Статус заявки
        val statusTextView = TextView(context).apply {
            text = "Статус: $status"

            when (status) {
                "Новая заявка" -> setTextColor(ContextCompat.getColor(context, R.color.green))
                "Выполненно" -> setTextColor(ContextCompat.getColor(context, R.color.green))
                "Отклонено" -> setTextColor(ContextCompat.getColor(context, R.color.red))
                else -> setTextColor(ContextCompat.getColor(context, R.color.yellow))
            }
            //setTextColor(ContextCompat.getColor(context, R.color.status_color))
            textSize = 14f
            gravity = Gravity.START
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 8
            }
        }

        // Изображение
        val imageUrl = "http://194.67.84.26:8000/get_image/$imageId"
        val imageView = ImageView(context).apply {
            scaleType = ImageView.ScaleType.CENTER_CROP
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                300
            ).apply {
                bottomMargin = 10
            }
            clipToOutline = true
        }

        // Glide для загрузки изображения
        Glide.with(context)
            .load(imageUrl) // URL изображения
            .placeholder(R.color.white) // Плейсхолдер во время загрузки
            .error(R.color.gray) // Если ошибка загрузки
            .into(imageView)

        // Описание
        val contentTextView = TextView(context).apply {
            text = description
            setTextColor(ContextCompat.getColor(context, R.color.dark_gray))
            textSize = 16f
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 12
            }
        }

        // Дата создания
        val dateTextView = TextView(context).apply {
            text = createdAt
            setTextColor(ContextCompat.getColor(context, R.color.gray))
            textSize = 14f
            gravity = Gravity.END
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        // Добавляем все элементы в блок
        block.apply {
            addView(categoryTextView)
            addView(statusTextView)
            addView(imageView)
            addView(contentTextView)
            addView(dateTextView)
        }

        return block
    }

}