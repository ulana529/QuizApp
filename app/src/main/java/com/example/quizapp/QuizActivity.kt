package com.example.quizapp

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

    private val questionList = mutableListOf<Question>()
    private var currentQuestionIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_quiz)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        loadQuestionsFromAssets()

        if (questionList.isNotEmpty()) {
            showQuestion(0)
        }

        val buttons = listOf(
            findViewById<Button>(R.id.answer1),
            findViewById<Button>(R.id.answer2),
            findViewById<Button>(R.id.answer3),
            findViewById<Button>(R.id.answer4)
        )

        buttons.forEachIndexed { index, button ->
            button.setOnClickListener {
                checkAnswer(index)
            }
        }
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


    private fun showQuestion(index: Int) {
        val question = questionList[index]

        val counter = findViewById<TextView>(R.id.questionCounter)
        val questionText = findViewById<TextView>(R.id.questionText)
        val answer1 = findViewById<Button>(R.id.answer1)
        val answer2 = findViewById<Button>(R.id.answer2)
        val answer3 = findViewById<Button>(R.id.answer3)
        val answer4 = findViewById<Button>(R.id.answer4)

        counter.text = "Вопрос ${index + 1} из ${questionList.size}"
        questionText.text = question.questionText

        answer1.text = question.options[0]
        answer2.text = question.options[1]
        answer3.text = question.options[2]
        answer4.text = question.options[3]
    }


    private fun checkAnswer(selectedIndex: Int) {
        val currentQuestion = questionList[currentQuestionIndex]

        if (selectedIndex == currentQuestion.correctIndex) {
            Toast.makeText(this, "Правильно!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Неправильно!", Toast.LENGTH_SHORT).show()
        }

        currentQuestionIndex++
        if (currentQuestionIndex < questionList.size) {
            showQuestion(currentQuestionIndex)
        } else {
            Toast.makeText(this, "Викторина завершена!", Toast.LENGTH_LONG).show()
            finish()
        }
    }
}