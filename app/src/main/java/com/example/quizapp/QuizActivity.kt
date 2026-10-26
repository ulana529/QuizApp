package com.example.quizapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.BufferedReader
import java.io.InputStreamReader

class QuizActivity : AppCompatActivity() {

    // Переменные для элементов экрана
    private lateinit var tvQuestionCount: TextView
    private lateinit var tvQuestionText: TextView
    private lateinit var tvScore: TextView
    private lateinit var btnOptions: List<Button>

    // Переменные для логики
    private val questionList = mutableListOf<Question>()
    private var currentQuestionIndex = 0
    private var score = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_quiz)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 1. Находим визуальные элементы
        tvQuestionCount = findViewById(R.id.questionCounter)
        tvQuestionText = findViewById(R.id.questionText)
        tvScore = findViewById(R.id.tvScore)
        btnOptions = listOf(
            findViewById(R.id.answer1),
            findViewById(R.id.answer2),
            findViewById(R.id.answer3),
            findViewById(R.id.answer4)
        )

        // 2. Загружаем вопросы
        loadQuestionsFromAssets()

        // 3. Вешаем листенеры на кнопки ответов
        btnOptions.forEachIndexed { index, button ->
            button.setOnClickListener {
                checkAnswer(index)
            }
        }

        // 4. Показываем первый вопрос
        showNextQuestion()
    }

    private fun loadQuestionsFromAssets() {
        try {
            val reader = BufferedReader(InputStreamReader(assets.open("questions.txt")))
            reader.useLines { lines ->
                lines.forEach { line ->
                    val parts = line.split("|")
                    if (parts.size >= 6) {
                        val question = Question(
                            questionText = parts[0],
                            options = listOf(parts[1], parts[2], parts[3], parts[4]),
                            correctIndex = parts[5].trim().toInt()
                        )
                        questionList.add(question)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showNextQuestion() {
        if (currentQuestionIndex < questionList.size) {
            val currentQuestion = questionList[currentQuestionIndex]

            tvQuestionCount.text = "Вопрос ${currentQuestionIndex + 1} из ${questionList.size}"
            tvQuestionText.text = currentQuestion.questionText
            tvScore.text = "Счет: $score"

            for (i in btnOptions.indices) {
                btnOptions[i].text = currentQuestion.options[i]
            }
        } else {
            // Переход на экран результатов
            val intent = Intent(this, ResultActivity::class.java)
            intent.putExtra("SCORE", score)
            intent.putExtra("TOTAL", questionList.size)
            startActivity(intent)
            finish()
        }
    }

    private fun checkAnswer(selectedIndex: Int) {
        val currentQuestion = questionList[currentQuestionIndex]

        if (selectedIndex == currentQuestion.correctIndex) {
            Toast.makeText(this, "Правильно!", Toast.LENGTH_SHORT).show()
            score++
        } else {
            Toast.makeText(this, "Неправильно!", Toast.LENGTH_SHORT).show()
        }

        currentQuestionIndex++
        showNextQuestion()
    }
}