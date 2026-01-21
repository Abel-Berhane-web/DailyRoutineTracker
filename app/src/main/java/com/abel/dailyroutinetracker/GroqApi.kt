package com.abel.dailyroutinetracker

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

data class Message(val role: String, val content: String)
data class ChatRequest(val model: String, val messages: List<Message>)
data class Choice(val message: Message)
data class ChatResponse(val choices: List<Choice>)

interface GroqApi {
    @POST("chat/completions")
    suspend fun chatCompletion(
        @Header("Authorization") auth: String,
        @Body body: ChatRequest
    ): ChatResponse
}