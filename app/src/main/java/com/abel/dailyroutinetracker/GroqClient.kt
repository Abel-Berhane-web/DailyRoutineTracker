package com.abel.dailyroutinetracker

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object GroqClient {
    private const val BASE_URL = "https://api.groq.com/openai/v1/"

    val api: GroqApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GroqApi::class.java)
    }
}