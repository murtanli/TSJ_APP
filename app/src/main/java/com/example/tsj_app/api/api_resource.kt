package com.example.auto_booking.api

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class api_resource {

    suspend fun login(login: String, password: String): loginResponse {
        val apiUrl = "http://194.67.84.26:8000/login/"
        val url = URL(apiUrl)

        return withContext(Dispatchers.IO) {
            try {
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"  // Используйте POST вместо GET
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true

                // Создаем JSON-строку с логином и паролем
                val jsonInputString = "{\"login\":\"$login\",\"password\":\"$password\"}"

                // Отправляем JSON в тело запроса
                val outputStream = connection.outputStream
                outputStream.write(jsonInputString.toByteArray())
                outputStream.close()

                val inputStream = connection.inputStream
                val reader = BufferedReader(InputStreamReader(inputStream))
                val response = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }

                val gson = Gson()
                gson.fromJson(response.toString(), loginResponse::class.java)
            } catch (e: Exception) {
                Log.e("LoginError", "Error fetching or parsing login data ", e)
                throw e
            }
        }
    }

    suspend fun save_token(token: String, user_id: String): TokenResponse {
        val apiUrl = "http://194.67.84.26:8000/save_fcm_token/"
        val url = URL(apiUrl)

        return withContext(Dispatchers.IO) {
            try {
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"  // Используйте POST вместо GET
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true

                val jsonInputString = "{\"token\":\"$token\",\"user_id\":\"$user_id\"}"

                val outputStream = connection.outputStream
                outputStream.write(jsonInputString.toByteArray())
                outputStream.close()

                val inputStream = connection.inputStream
                val reader = BufferedReader(InputStreamReader(inputStream))
                val response = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }

                val gson = Gson()
                gson.fromJson(response.toString(), TokenResponse::class.java)
            } catch (e: Exception) {
                Log.e("LoginError", "Error fetching or parsing login data ", e)
                throw e
            }
        }
    }


    suspend fun Sign_in(first_name: String, last_name:String, surname: String, phone: String, address: String, email: String, login: String, password: String): sign_inResponse {
        val apiUrl = "http://194.67.84.26:8000/sign_in/"
        val url = URL(apiUrl)

        return withContext(Dispatchers.IO) {
            try {
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true

                // Создаем JSON-строку с логином и паролем
                val jsonInputString = "{" +
                        "\"first_name\":\"$first_name\"," +
                        "\"last_name\":\"$last_name\"," +
                        "\"surname\":\"$surname\"," +
                        "\"phone\":\"$phone\"," +
                        "\"address\":\"$address\"," +
                        "\"email\":\"$email\"," +
                        "\"login\":\"$login\"," +
                        "\"password\":\"$password\"" +
                        "}"


                // Отправляем JSON в тело запроса
                val outputStream = connection.outputStream
                outputStream.write(jsonInputString.toByteArray())
                outputStream.close()
                val inputStream = connection.inputStream
                val reader = BufferedReader(InputStreamReader(inputStream))
                val response = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }

                val gson = Gson()
                gson.fromJson(response.toString(), sign_inResponse::class.java)
            } catch (e: Exception) {
                Log.e("LoginError", "Error fetching or parsing register data ", e)
                throw e
            }
        }
    }

    suspend fun Get_profile_info(user_id: Int?): ProfileInfo {
        val apiUrl = "http://194.67.84.26:8000/get_profile_data/"
        val url = URL(apiUrl)

        return withContext(Dispatchers.IO) {
            try {
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true

                // Создаем JSON-строку с логином и паролем
                val jsonInputString = "{" +
                        "\"user_id\":\"$user_id\"" +
                        "}"


                // Отправляем JSON в тело запроса
                val outputStream = connection.outputStream
                outputStream.write(jsonInputString.toByteArray())
                outputStream.close()
                val inputStream = connection.inputStream
                val reader = BufferedReader(InputStreamReader(inputStream))
                val response = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }

                val gson = Gson()
                gson.fromJson(response.toString(), ProfileInfo::class.java)
            } catch (e: Exception) {
                Log.e("LoginError", "Error fetching or parsing register data ", e)
                throw e
            }
        }
    }

    suspend fun get_all_news(): List<newsResponse> {
        val apiUrl = "http://194.67.84.26:8000/get_news/"
        val url = URL(apiUrl)

        return withContext(Dispatchers.IO) {
            try {
                val connection = url.openConnection() as HttpURLConnection
                val inputStream = connection.inputStream
                val reader = BufferedReader(InputStreamReader(inputStream))
                val response = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }

                val gson = Gson()
                val driversArray = gson.fromJson(response.toString(), Array<newsResponse>::class.java)
                driversArray.toList()
            } catch (e: Exception) {
                throw e
            }
        }
    }


    suspend fun get_all_categories(): SpisokCategories {
        val apiUrl = "http://194.67.84.26:8000/get_category/"
        val url = URL(apiUrl)

        return withContext(Dispatchers.IO) {
            try {
                val connection = url.openConnection() as HttpURLConnection
                val inputStream = connection.inputStream
                val reader = BufferedReader(InputStreamReader(inputStream))
                val response = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }

                val gson = Gson()
                gson.fromJson(response.toString(), SpisokCategories::class.java)
            } catch (e: Exception) {
                throw e
            }
        }
    }

    suspend fun create_new_request(
        profile_id: Int?,
        category_name: String,
        description: String,
        user_photo: File?
    ): RequestResponse {
        val apiUrl = "http://194.67.84.26:8000/create_new_requests/"
        val url = URL(apiUrl)

        return withContext(Dispatchers.IO) {
            try {
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=$BOUNDARY")
                connection.doOutput = true
                connection.doInput = true

                val outputStream = DataOutputStream(connection.outputStream)
                val writer = outputStream.writer(Charsets.UTF_8)

                // Записываем текстовые данные в UTF-8
                writeFormField(writer, "profile_id", profile_id.toString())
                writeFormField(writer, "category_name", category_name)
                writeFormField(writer, "description", description)

                // Добавляем фото, если есть
                writer.flush()
                if (user_photo != null) {
                    outputStream.writeBytes("--$BOUNDARY\r\n")
                    outputStream.writeBytes("Content-Disposition: form-data; name=\"photo\"; filename=\"${user_photo.name}\"\r\n")
                    outputStream.writeBytes("Content-Type: image/jpeg\r\n\r\n")
                    user_photo.inputStream().use { it.copyTo(outputStream) }
                    outputStream.writeBytes("\r\n")
                }

                // Закрываем запрос
                outputStream.writeBytes("--$BOUNDARY--\r\n")
                outputStream.flush()
                outputStream.close()

                // Получаем ответ от сервера
                val responseCode = connection.responseCode
                val responseMessage = connection.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }

                val gson = Gson()
                val response = gson.fromJson(responseMessage, RequestResponse::class.java)

                if (responseCode == HttpURLConnection.HTTP_CREATED) {
                    response
                } else {
                    throw Exception("Ошибка: $responseMessage")
                }
            } catch (e: Exception) {
                throw e
            }
        }
    }
    // Функция для записи полей формы (UTF-8)
    private fun writeFormField(writer: OutputStreamWriter, name: String, value: String) {
        writer.write("--$BOUNDARY\r\n")
        writer.write("Content-Disposition: form-data; name=\"$name\"\r\n\r\n")
        writer.write(value)
        writer.write("\r\n")
        writer.flush()
    }

    // Константа для boundary
    private val BOUNDARY = "----WebKitFormBoundary7MA4YWxkTrZu0gW"

    suspend fun create_meter_readings(
        meter_id: Int?,
        type: String?,
        value: Int?,
        user_photo: File?
    ): RequestResponse {
        val apiUrl = "http://194.67.84.26:8000/create_meter_readings/"
        val url = URL(apiUrl)

        return withContext(Dispatchers.IO) {
            try {
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=$BOUNDARY")
                connection.doOutput = true
                connection.doInput = true

                connection.connectTimeout = 30000
                connection.readTimeout = 30000

                val outputStream = DataOutputStream(connection.outputStream)
                val writer = outputStream.writer(Charsets.UTF_8)
                val compressedPhoto = compressImage(user_photo)

                // Записываем текстовые данные в UTF-8
                writeFormField(writer, "meter_id", meter_id.toString())
                writeFormField(writer, "type", type.toString())
                writeFormField(writer, "value", value.toString())

                // Добавляем фото, если есть
                writer.flush()
                if (user_photo != null) {
                    outputStream.writeBytes("--$BOUNDARY\r\n")
                    outputStream.writeBytes("Content-Disposition: form-data; name=\"photo\"; filename=\"${compressedPhoto.name}\"\r\n")
                    outputStream.writeBytes("Content-Type: image/jpeg\r\n\r\n")
                    user_photo.inputStream().use { it.copyTo(outputStream) }
                    outputStream.writeBytes("\r\n")
                }

                // Закрываем запрос
                outputStream.writeBytes("--$BOUNDARY--\r\n")
                outputStream.flush()
                outputStream.close()

                // Получаем ответ от сервера
                val responseCode = connection.responseCode
                val responseMessage = connection.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }

                val gson = Gson()
                val response = gson.fromJson(responseMessage, RequestResponse::class.java)

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    response
                } else {
                    throw Exception("Ошибка: $responseMessage")
                }
            } catch (e: Exception) {
                throw e
            }
        }
    }

    fun compressImage(imageFile: File?): File {
        val bitmap = BitmapFactory.decodeFile(imageFile?.absolutePath)
        val outputFile = File(imageFile?.parent, "compressed_${imageFile?.name}")
        val outputStream = FileOutputStream(outputFile)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream) // Сжимаем до 80% качества
        outputStream.flush()
        outputStream.close()
        return outputFile
    }


    suspend fun create_new_meter(profile_id: Int?): NewMeterResponse {
        val apiUrl = "http://194.67.84.26:8000/create_meter/"
        val url = URL(apiUrl)

        return withContext(Dispatchers.IO) {
            try {
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true

                // Создаем JSON-строку с логином и паролем
                val jsonInputString = "{" +
                        "\"profile_id\":\"$profile_id\"" +
                        "}"


                // Отправляем JSON в тело запроса
                val outputStream = connection.outputStream
                outputStream.write(jsonInputString.toByteArray())
                outputStream.close()
                val inputStream = connection.inputStream
                val reader = BufferedReader(InputStreamReader(inputStream))
                val response = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }

                val gson = Gson()
                gson.fromJson(response.toString(), NewMeterResponse::class.java)
            } catch (e: Exception) {
                Log.e("LoginError", "Error fetching or parsing register data ", e)
                throw e
            }
        }
    }



    suspend fun get_all_requests(profile_id: Int?): List<RequestsResponse> {
        val apiUrl = "http://194.67.84.26:8000/get_all_requests/"
        val url = URL(apiUrl)

        return withContext(Dispatchers.IO) {
            try {
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"  // Используйте POST вместо GET
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true

                // Создаем JSON-строку с логином и паролем
                val jsonInputString = "{\"profile_id\":\"$profile_id\"}"

                // Отправляем JSON в тело запроса
                val outputStream = connection.outputStream
                outputStream.write(jsonInputString.toByteArray())
                outputStream.close()
                val inputStream = connection.inputStream
                val reader = BufferedReader(InputStreamReader(inputStream))
                val response = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }

                val gson = Gson()
                val driversArray = gson.fromJson(response.toString(), Array<RequestsResponse>::class.java)
                driversArray.toList()
            } catch (e: Exception) {
                // Обработка ошибок, например, логирование
                throw e
            }
        }
    }


    suspend fun get_all_meter_readings(profile_id: Int?): AllMeterResponse {
        val apiUrl = "http://194.67.84.26:8000/get_all_meter_readings/"
        val url = URL(apiUrl)

        return withContext(Dispatchers.IO) {
            try {
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"  // Используйте POST вместо GET
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true

                // Создаем JSON-строку с логином и паролем
                val jsonInputString = "{\"profile_id\":\"$profile_id\"}"

                // Отправляем JSON в тело запроса
                val outputStream = connection.outputStream
                outputStream.write(jsonInputString.toByteArray())
                outputStream.close()
                val inputStream = connection.inputStream
                val reader = BufferedReader(InputStreamReader(inputStream))
                val response = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }

                val gson = Gson()
                gson.fromJson(response.toString(), AllMeterResponse::class.java)

            } catch (e: Exception) {
                throw e
            }
        }
    }

    suspend fun create_req(user_id: Int?, auto_service_id: Int?, service_id: Int?, date_time: String): sign_inResponse {
        val apiUrl = "http://194.58.121.142:8100/create_req/"
        val url = URL(apiUrl)

        return withContext(Dispatchers.IO) {
            try {
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"  // Используйте POST вместо GET
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true

                // Создаем JSON-строку с логином и паролем
                val jsonInputString = "{\"user_id\":\"$user_id\",\"auto_service_id\":\"$auto_service_id\",\"service_id\":\"$service_id\",\"date_time\":\"$date_time\"}"

                // Отправляем JSON в тело запроса
                val outputStream = connection.outputStream
                outputStream.write(jsonInputString.toByteArray())
                outputStream.close()
                val inputStream = connection.inputStream
                val reader = BufferedReader(InputStreamReader(inputStream))
                val response = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }

                val gson = Gson()
                gson.fromJson(response.toString(), sign_inResponse::class.java)
            } catch (e: Exception) {
                Log.e("LoginError", "Error fetching or parsing register data ", e)
                throw e
            }
        }
    }

    suspend fun show_busy_autser(auto_service_id: Int?): List<busy_ser> {
        val apiUrl = "http://194.58.121.142:8100/show_busy_req/"
        val url = URL(apiUrl)

        return withContext(Dispatchers.IO) {
            try {
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true
                val jsonInputString = "{\"auto_service_id\":$auto_service_id}"

                // Отправляем JSON в тело запроса
                val outputStream = connection.outputStream
                outputStream.write(jsonInputString.toByteArray())
                outputStream.close()

                // Обрабатываем полученный JSON-ответ
                val inputStream = connection.inputStream
                val reader = BufferedReader(InputStreamReader(inputStream))
                val response = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }

                val gson = Gson()
                val cargoArray = gson.fromJson(response.toString(), Array<busy_ser>::class.java)
                cargoArray.toList()
            } catch (e: Exception) {
                // Обработка ошибок, например, логирование
                throw e
            }
        }
    }

    suspend fun show_busy_autser_user(user_id: Int?): List<sel_res> {
        val apiUrl = "http://194.58.121.142:8100/show_busy_req_user/"
        val url = URL(apiUrl)

        return withContext(Dispatchers.IO) {
            try {
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true
                val jsonInputString = "{\"user_id\":$user_id}"

                // Отправляем JSON в тело запроса
                val outputStream = connection.outputStream
                outputStream.write(jsonInputString.toByteArray())
                outputStream.close()

                // Обрабатываем полученный JSON-ответ
                val inputStream = connection.inputStream
                val reader = BufferedReader(InputStreamReader(inputStream))
                val response = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    response.append(line)
                }

                val gson = Gson()
                val cargoArray = gson.fromJson(response.toString(), Array<sel_res>::class.java)
                cargoArray.toList()
            } catch (e: Exception) {
                // Обработка ошибок, например, логирование
                throw e
            }
        }
    }
}