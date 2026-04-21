package com.example.geoquiz.quiz

import android.util.Log
import com.example.geoquiz.api.RetrofitInstance
import com.example.geoquiz.api.data.OpenAIRequest
import com.google.gson.Gson

class QuizCreator {
    companion object {
        private const val CHOICE_COUNT = 4

        private val quizFormat = QuizData(
            question = "[問題文]",
            choices = (1..CHOICE_COUNT).map { "[選択肢${it}]" },
            answer = "[答え]"
        )

        suspend fun createQuiz(address: String?, lat: String, lon: String, difficulty: String): QuizData? {
            val prompt = if (address.isNullOrEmpty()) {
                "緯度${lat}、経度${lon}に関するクイズを難易度${difficulty}で1つ作成してください。"
            } else {
                "${address}に関するクイズを難易度${difficulty}で1つ作成してください。"
            }

            val response = RetrofitInstance.openAIService.getCompletion(
                OpenAIRequest(
                    "gpt-4",
                    listOf(
                        OpenAIRequest.Message(
                            "user",
                            "${prompt}フォーマットは${Gson().toJson(quizFormat)}のjson形式でお願いします。"
                        )
                    )
                )
            )
            val body = response.body()
            body?.choices?.firstOrNull()?.message?.content?.let { Log.i("MainActivity", it) }

            return try {
                val quizData = Gson().fromJson(
                    body?.choices?.firstOrNull()?.message?.content,
                    QuizData::class.java
                ).apply {
                    this.address = address
                    this.lat = lat
                    this.lon = lon
                }

                if (quizData.choices.size != CHOICE_COUNT) {
                    return null
                }

                quizData
            } catch (e: Exception) {
                null
            }
        }
    }
}
