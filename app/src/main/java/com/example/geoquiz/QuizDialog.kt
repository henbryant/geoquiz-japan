package com.example.geoquiz

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.geoquiz.databinding.DialogQuizBinding
import com.example.geoquiz.quiz.QuizData
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit

class QuizDialog {
    interface QuizDialogListener {
        fun onCorrectAnswer()
    }

    companion object {

        private val party = Party(
            speed = 0f,
            maxSpeed = 30f,
            damping = 0.9f,
            spread = 360,
            colors = listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def),
            emitter = Emitter(duration = 500, TimeUnit.MILLISECONDS).max(100),
            position = Position.Relative(0.5, 0.3)
        )

        fun newInstance(
            context: Context,
            quizData: QuizData,
            quizDialogListener: QuizDialogListener? = null
        ): AlertDialog {
            val binding = DialogQuizBinding.inflate(
                LayoutInflater.from(context),
                null,
                false
            )
            val alertDialog = AlertDialog.Builder(context)
                .setView(binding.root)
                .create()

            var isSelected = false

            fun showAnswer(
                button: Button,
                answer: String
            ) {
                val correct = button.text == answer
                val symbol = if (correct) {
                    "○"
                } else {
                    "×"
                }
                button.text = "${symbol} ${button.text}"

                val backgroundColor = if (correct) {
                    ContextCompat.getColorStateList(context, R.color.correct_answer)
                } else {
                    ContextCompat.getColorStateList(context, R.color.wrong_answer)
                }
                button.backgroundTintList = backgroundColor
            }

            fun onSelect(
                choice: String,
                answer: String
            ) {
                if (isSelected) {
                    return
                }
                val isCorrect = choice == answer
                binding.apply {
                    win.isVisible = isCorrect
                    lose.isVisible = !isCorrect
                    close.isVisible = true
                    listOf(choice1, choice2, choice3, choice4).forEach { button ->
                        showAnswer(button, answer)
                    }
                    if (isCorrect) {
                        viewKonfetti.start(party)
                    }
                }
                isSelected = true
            }

            binding.apply {
                questionTitle.text = if (quizData.address.isNullOrEmpty()) "緯度${quizData.lat}、経度${quizData.lon}に関するクイズ"
                    else "${quizData.address}に関するクイズ"
                questionBody.text = quizData.question
                choice1.text = quizData.choices[0]
                choice2.text = quizData.choices[1]
                choice3.text = quizData.choices[2]
                choice4.text = quizData.choices[3]

                choice1.setOnClickListener {
                    onSelect(quizData.choices[0], quizData.answer)
                }

                choice2.setOnClickListener {
                    onSelect(quizData.choices[1], quizData.answer)
                }

                choice3.setOnClickListener {
                    onSelect(quizData.choices[2], quizData.answer)
                }

                choice4.setOnClickListener {
                    onSelect(quizData.choices[3], quizData.answer)
                }

                close.setOnClickListener {
                    alertDialog.dismiss()
                }
            }

            return alertDialog
        }
    }
}