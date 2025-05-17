package com.example.tsj_app.ui.news

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.auto_booking.api.api_resource
import com.example.tsj_app.ProfileActivity
import com.example.tsj_app.R
import com.example.tsj_app.databinding.FragmentNewsBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.chromium.base.task.AsyncTask
import java.net.HttpURLConnection
import java.net.URL

class NewsFragment : Fragment() {

    private var _binding: FragmentNewsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val newsViewModel =
            ViewModelProvider(this).get(NewsViewModel::class.java)

        _binding = FragmentNewsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        (activity as? ProfileActivity)?.act_bar()

        val linear_container = binding.ContainerBlocks
        val progressBar = binding.progressBar
        progressBar.visibility = View.GONE

        GlobalScope.launch(Dispatchers.Main) {
            try {
                progressBar.visibility = View.VISIBLE
                val data = api_resource()
                val result = data.get_all_news()
                if (result.isNotEmpty()) {
                    //вызов функции отрисовки блоков

                    val title = TextView(requireContext())
                    title.text = "Новости"
                    title.setPadding(100,100,100,100)
                    title.gravity = Gravity.CENTER
                    title.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                    title.textSize = 30F

                    linear_container.addView(title)

                    for (news in result) {
                        val block = createBlockNews(
                            news.title,
                            news.content,
                            news.created_at,
                            news.image_id,
                            news.category_news_id)
                        linear_container.addView(block)
                        linear_container.gravity = Gravity.CENTER
                    }
                    progressBar.visibility = View.GONE
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
        progressBar.visibility = View.GONE

        return root
    }

    private fun createBlockNews(
        title: String,
        content: String,
        createAt: String,
        imageId: Int,
        categoryNews: String
    ): LinearLayout {

        val context = requireContext()

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
            text = categoryNews.uppercase()
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

        // Заголовок
        val titleTextView = TextView(context).apply {
            text = title
            setTextAppearance(R.style.PageTitle)
            setTextColor(ContextCompat.getColor(context, R.color.black))
            textSize = 20f
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 8
            }
        }

        // Изображение
        // Изображение (Glide для загрузки по URL)
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
            .placeholder(R.mipmap.ic_launcher) // Плейсхолдер во время загрузки
            .error(R.mipmap.ic_launcher_round) // Если ошибка загрузки
            .into(imageView)

        // Описание (полный текст без обрезки)
        val contentTextView = TextView(context).apply {
            text = content
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
            text = createAt
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
            addView(titleTextView)
            addView(imageView)
            addView(contentTextView)
            addView(dateTextView)
        }

        return block
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}