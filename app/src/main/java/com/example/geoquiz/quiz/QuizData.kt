package com.example.geoquiz.quiz

data class QuizData(
    val question: String,
    val choices: List<String>,
    val answer: String,
    var address: String? = null,
    var lat: String? = null,
    var lon: String? = null
)
