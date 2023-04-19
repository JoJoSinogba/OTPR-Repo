package com.example.dashboard_test

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Retrofit {
    val retrofit = Retrofit.Builder()
        .baseUrl("http://10.0.1.14:3000/send/telegram") // Replace with your cloud service's API endpoint
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}