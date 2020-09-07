package com.example.geoquiz

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders

const val EXTRA_ANSWER_SHOWN = "com.example.geoquiz.answer_shown"
private const val EXTRA_ANSWER_IS_TRUE = "com.example.geoquiz.answer_is_true"
private const val TAG = "CheatActivity"
private const val KEY_INDEX = "userClickedCheat"

//GitHubTEst
class CheatActivity : AppCompatActivity() {

    private lateinit var answerTextView: TextView
    private lateinit var showAnswerButton: Button

    private  var answerIsTrue = false

    private val quizViewModel: QuizViewModel by lazy {
        ViewModelProviders.of(this).get(QuizViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "oncCreate(Bundle?) called")
        setContentView(R.layout.activity_cheat)

        //Loads the instance state
        val cheater = savedInstanceState?.getBoolean(KEY_INDEX, false) ?: false
        quizViewModel.userClickedCheat = cheater

        val provider: ViewModelProvider = ViewModelProviders.of(this)
        val quizViewModel = provider.get(QuizViewModel::class.java)
        Log.d(TAG, "Got a QuizViewModel: $quizViewModel")

        answerIsTrue = intent.getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false) //sees what was sent from MainActivity
        answerTextView = findViewById(R.id.answer_text_view)
        showAnswerButton = findViewById(R.id.show_answer_button)
        showAnswerButton.setOnClickListener {
            val answerText = when {
                answerIsTrue -> R.string.true_button
                else -> R.string.false_button
            }
            answerTextView.setText(answerText)
            quizViewModel.userCheated()
            setAnswerShownResult(true) //sets answer to true and returns it to MainActivity
        }
        if (cheater) {
            showAnswerButton.performClick()
        }
    }

    private fun setAnswerShownResult(isAnswerShown: Boolean) {
        val data = Intent().apply {
            putExtra(EXTRA_ANSWER_SHOWN, isAnswerShown) //adds this to the intent
        }
        setResult(Activity.RESULT_OK, data) //Sends the result back
    }

    companion object {
        fun newIntent(packageContext: Context, answerIsTrue: Boolean): Intent {
            return Intent(packageContext, CheatActivity::class.java).apply {
                putExtra(EXTRA_ANSWER_IS_TRUE, answerIsTrue) //sets what was sent in the intent to extra answer
            }
        }
    }

    //Saves the instance
    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        Log.i(TAG, "onSaveInstanceState")
        savedInstanceState.putBoolean(KEY_INDEX, quizViewModel.userClickedCheat)
    }
}