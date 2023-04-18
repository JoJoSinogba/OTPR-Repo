package com.example.dashboard_test

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface API {
        @POST("messages")
        fun sendMessages(@Body messages: List<messageData>): Call<Response<ResponseBody>>
}
