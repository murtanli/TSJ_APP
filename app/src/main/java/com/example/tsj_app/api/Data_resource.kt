package com.example.auto_booking.api

import android.provider.ContactsContract.Profile


//data class loginResponse(
//    val message: String,
//    val user_id: Int,
//    val fio: String
//)
data class TokenResponse(
    val error: String,
    val message: String
)
data class ProfileResponse(
    val pk: Int,
    val first_name: String,
    val last_name: String,
    val surname: String,
    val address: String,
    val phone: String,
    val email: String,
    val count_meters: Int
)

data class loginResponse(
    val error: String,
    val user_id: Int,
    val profile: ProfileResponse
)

data class ProfileInfo(
    val profile: ProfileResponse,
    val error: String
)


data class sign_inResponse(
    val message: String,
    val error: String
)

data class newsResponse (
    val title: String,
    val content: String,
    val created_at: String,
    val image_id: Int,
    val category_news_id: String,
)

data class Category (
    val name: String
)

data class SpisokCategories (
    val categories: List<Category>,
    val error: String
)

data class RequestResponse(
    val message: String,
    val request_id: Int?,
    val error: String
)


data class RequestsResponse(
    val id: Int,
    val profile_id: Int,
    val category_name: String,
    val description: String,
    val photo_url_id: Int,
    val status: String,
    val created_at: String
)

data class NewMeterResponse(
    val message: String,
    val meter_id: Int
)

data class MeterReadingResponse(
    val type: String,
    val value: Double,
    val photo_url: Int,
    val created_at: String
)

data class MeterReading(
    val meter_id: Int,
    val created_at: String,
    val readings: List<MeterReadingResponse>
)

data class AllMeterResponse(
    val spisok: List<MeterReading>
)





data class service_list(
    val id: Int,
    val name: String,
    val price: Float
)
data class busy_ser(
    val services: String,
    val date: String,
    val time: String
)

data class response(
    val rec: busy_ser
)

data class sel_res(
    val auto_service_name: String,
    val auto_service_address: String,
    val service: String,
    val date: String,
    val time: String
)