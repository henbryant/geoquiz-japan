package com.example.geoquiz.api

import com.example.geoquiz.BuildConfig
import com.example.geoquiz.api.data.OpenAIRequest
import com.example.geoquiz.api.data.OpenAIResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface OpenAIApi {
    @Headers("Content-Type: application/json")
    @POST("v1/chat/completions")
    suspend fun getCompletion(
        @Body body: OpenAIRequest,
        @Header("Authorization") key: String = "Bearer ${BuildConfig.OPENAI_KEY}"
    ): Response<OpenAIResponse>
}
