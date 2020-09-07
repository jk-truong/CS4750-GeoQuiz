package com.example.geoquiz

import android.util.Log
import androidx.lifecycle.ViewModel

private const val TAG = "QuizViewModel"

class QuizViewModel : ViewModel(){

    var currentIndex = 0 //This is no longer private so it can be accessed by MainActivity
    var isCheater = false //Holds the value of returned intent from CheatActivity
    var score = 0
    var button = true
    var userClickedCheat = false //Save instance for CheatActivity if user cheated

    private val questionBank = listOf(
        Question(R.string.question_australia, true),
        Question(R.string.question_oceans, true),
        Question(R.string.question_mideast, false),
        Question(R.string.question_africa, false),
        Question(R.string.question_americas, true),
        Question(R.string.question_asia, true)
    )

    var questionBankSize = questionBank.size

    val currentQuestionAnswer: Boolean
        get() = questionBank[currentIndex].answer

    val currentQuestionText: Int
        get() = questionBank[currentIndex].textResId

    fun moveToNext() {
        currentIndex = (currentIndex + 1) % questionBank.size
        isCheater = false //resets the cheater counter for the next question
        button = true //resets the button for the next question
    }

    fun moveToPrevious() {
        if (currentIndex !=0){
            currentIndex = (currentIndex - 1) % questionBank.size
        }
    }

    fun userCheated() {
        userClickedCheat = true
    }
}