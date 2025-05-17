package com.example.tsj_app

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.auto_booking.api.api_resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class Sign_in_Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_in)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

         val edit_text_first_name = findViewById<EditText>(R.id.edit_text_first_name)
         val edit_text_last_name = findViewById<EditText>(R.id.edit_text_last_name)
         val edit_text_surname = findViewById<EditText>(R.id.edit_text_surname)
         val edit_text_phone = findViewById<EditText>(R.id.edit_text_phone)
         val edit_text_address = findViewById<EditText>(R.id.edit_text_address)
         val edit_text_email = findViewById<EditText>(R.id.edit_text_email)
         val edit_text_login = findViewById<EditText>(R.id.edit_text_login)
         val edit_text_password = findViewById<EditText>(R.id.edit_text_password)
         val button_register = findViewById<Button>(R.id.button_register)
         val error_text = findViewById<TextView>(R.id.error_text)

        //Убрал верхнюю панель
        val actionBar: ActionBar? = supportActionBar
        actionBar?.hide()

        button_register.setOnClickListener {
            if (!edit_text_login.text.isNullOrEmpty() && !edit_text_password.text.isNullOrEmpty() && !edit_text_first_name.text.isNullOrEmpty() && !edit_text_last_name.text.isNullOrEmpty() && !edit_text_surname.text.isNullOrEmpty() && !edit_text_phone.text.isNullOrEmpty() && !edit_text_address.text.isNullOrEmpty() && !edit_text_email.text.isNullOrEmpty()) {

                val first_name = edit_text_first_name.text.toString()
                val last_name = edit_text_last_name.text.toString()
                val surname = edit_text_surname.text.toString()
                val phone = edit_text_phone.text.toString()
                val address = edit_text_address.text.toString()
                val email = edit_text_email.text.toString()
                val login = edit_text_login.text.toString()
                val password = edit_text_password.text.toString()


                GlobalScope.launch(Dispatchers.Main) {
                    try {
                        val data = api_resource()
                        val result = data.Sign_in(
                            first_name,
                            last_name,
                            surname,
                            phone,
                            address,
                            email,
                            login,
                            password)

                        if (result != null) {

                            if (result.error != null) {
                                error_text.text = result.error
                            } else {
                                val intent = Intent(this@Sign_in_Activity, LoginActivity::class.java)
                                startActivity(intent)
                                error_text.text = result.message

                                val sharedPreferences = getSharedPreferences("myPreferences", Context.MODE_PRIVATE)
                                val editor = sharedPreferences.edit()
                                editor.putString("login", "false")
                                editor.apply()
                            }


                        } else {
                            // Обработка случая, когда result равен null
                            Log.e("LoginActivity", "Login failed - result is null")
                            error_text.text = "Ошибка в процессе авторизации ${result.error}"
                        }
                    } catch (e: Exception) {
                        // Ловим и обрабатываем исключения, например, связанные с сетевыми ошибками
                        Log.e("LoginActivity", "Error during login", e)
                        e.printStackTrace()
                        error_text.text = "Ошибка входа: Неправильный пароль или профиль уже существует"
                    }
                }
            } else {
                error_text.text = "Пустые поля ! либо пороли не совпадают"
            }
        }
    }
}