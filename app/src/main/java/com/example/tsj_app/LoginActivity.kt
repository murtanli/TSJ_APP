package com.example.tsj_app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.auto_booking.api.api_resource
import com.example.tsj_app.ui.news.NewsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import com.google.firebase.messaging.FirebaseMessaging

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val buttonLogin = findViewById<Button>(R.id.button_login)
        val edit_text_login = findViewById<EditText>(R.id.edit_text_login)
        val edit_text_password = findViewById<EditText>(R.id.edit_text_password)
//        val text_sign = findViewById<TextView>(R.id.textView3)
        val errorText = findViewById<TextView>(R.id.textView2)

        val sharedPreferences = getSharedPreferences("myPreferences", Context.MODE_PRIVATE)
        val login_save = sharedPreferences.getString("login", "")



        if (login_save == "true") {
            val intent = Intent(this@LoginActivity, ProfileActivity::class.java)
            startActivity(intent)
        }
        //Убрал верхнюю панель
        val actionBar: ActionBar? = supportActionBar
        actionBar?.hide()

        buttonLogin.setOnClickListener {
            val loginText = edit_text_login?.text?.toString()
            val passwordText = edit_text_password?.text?.toString()

            if (loginText.isNullOrBlank() || passwordText.isNullOrBlank()) {
                errorText.text = "Введите данные в поля"
            } else {
                GlobalScope.launch(Dispatchers.Main) {
                    try {
                        // Вызываем функцию logIn для выполнения запроса
                        val data = api_resource()
                        val result = data.login(loginText, passwordText)

                        if (result != null) {
                            if (result.error == null) {
                                // Если успешно авторизованы, выводим сообщение об успешной авторизации и обрабатываем данные
                                Log.d("LoginActivity", "Login successful")
                                //Log.d("LoginActivity", "User ID: ${result.user_data.user_id}")
                                errorText.text = result.error

                                val sharedPreferences = getSharedPreferences("myPreferences", Context.MODE_PRIVATE)
                                val editor = sharedPreferences.edit()
                                try {
                                    editor.putString("user_id", result.user_id.toString())

                                    editor.putString("user_name", "${result.profile.first_name.toString()} ${result.profile.last_name.toString()}")
                                    editor.putString("login", "true")
                                    editor.putString("profile_id", "${result.profile.pk}")

                                    GlobalScope.launch(Dispatchers.Main) {
                                        try {
                                            val data = api_resource()
                                            val profile = data.Get_profile_info(result.user_id)
                                            //Log.e("WEWEWEWEWEW", "${profile.profile.count_meters}")
                                            editor.putString("count_meters", "${profile.profile.count_meters}")
                                            editor.apply()
                                        } catch (e: Exception) {
                                            e.printStackTrace()

                                        }
                                    }
                                    editor.apply()

                                    FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            val token = task.result
                                            Log.d("FCM Token", token.toString())
                                            GlobalScope.launch(Dispatchers.Main) {
                                                try {
                                                    val data = api_resource()
                                                    val response = data.save_token(token.toString(), result.user_id.toString())
                                                    editor.putString("token", "${token}")
                                                    Log.e("TOKEN", token.toString())
                                                    Log.e("RESPONSE", response.message)
                                                    editor.apply()
                                                } catch (e: Exception) {
                                                    e.printStackTrace()
                                                }
                                            }
                                        }
                                    }
                                } catch(e: Exception) {
                                    editor.putString("user_id", result.user_id.toString())
                                    editor.putString("login", "true")
                                    editor.apply()
                                }

                                val intent = Intent(this@LoginActivity, ProfileActivity::class.java)
                                startActivity(intent)


                            } else {
                                // Если произошла ошибка, выводим сообщение об ошибке
                                Log.e("LoginActivity", "Login failed")
                                errorText.text = result.error
                                val sharedPreferences = getSharedPreferences("myPreferences", Context.MODE_PRIVATE)
                                val editor = sharedPreferences.edit()
                                editor.putString("login", "false")
                                editor.apply()
                            }
                        } else {
                            // Обработка случая, когда result равен null
                            Log.e("LoginActivity", "Login failed - result is null")
                            errorText.text = "Ошибка в процессе авторизации ${result.error}"
                        }
                    } catch (e: Exception) {
                        // Ловим и обрабатываем исключения, например, связанные с сетевыми ошибками
                        Log.e("LoginActivity", "Error during login", e)
                        e.printStackTrace()
                        errorText.text = "Ошибка входа: Неправильный пароль или профиль не найден"
                        val sharedPreferences = getSharedPreferences("myPreferences", Context.MODE_PRIVATE)
                        val editor = sharedPreferences.edit()
                        editor.putString("login", "false")
                        editor.apply()
                    }
                }

            }
        }
//        text_sign.setOnClickListener {
//            val intent = Intent(this, Sign_in_Activity::class.java)
//            startActivity(intent)
//        }
    }
    override fun onBackPressed() {

    }
}