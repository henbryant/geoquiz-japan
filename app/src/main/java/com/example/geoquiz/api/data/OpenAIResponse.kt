package com.example.geoquiz.api.data

data class OpenAIResponse(
    val choices: List<Choice>
) {
    data class Choice(
        val message: OpenAIRequest.Message
    )
}
