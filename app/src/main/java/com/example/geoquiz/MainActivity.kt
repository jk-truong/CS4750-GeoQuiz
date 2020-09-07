package com.example.geoquiz

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders

private const val TAG = "MainActivity"
private const val KEY_INDEX = "index"
private const val REQUEST_CODE_CHEAT = 0

//TEst
class MainActivity : AppCompatActivity() {

    private lateinit var trueButton: Button
    private lateinit var falseButton: Button
    private lateinit var nextButton: ImageButton
    private lateinit var previousButton: ImageButton
    private lateinit var cheatButton:Button
    private lateinit var questionTextView: TextView

    private val quizViewModel: QuizViewModel by lazy {
        ViewModelProviders.of(this).get(QuizViewModel::class.java)
    }
    //lazy allows us to make a val instead of var

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "oncCreate(Bundle?) called")
        setContentView(R.layout.activity_main)

        //Grabs the saved state when app is relaunched
        val currentIndex = savedInstanceState?.getInt(KEY_INDEX, 0) ?: 0
        quizViewModel.currentIndex = currentIndex

        val provider: ViewModelProvider = ViewModelProviders.of(this)
        val quizViewModel = provider.get(QuizViewModel::class.java)
        Log.d(TAG, "Got a QuizViewModel: $quizViewModel")

        trueButton = findViewById(R.id.true_button)
        falseButton = findViewById(R.id.false_button)
        nextButton = findViewById(R.id.next_button)
        previousButton = findViewById(R.id.previous_button)
        cheatButton = findViewById(R.id.cheat_button)
        questionTextView = findViewById(R.id.question_text_view)

        trueButton.setOnClickListener { _: View ->
            checkAnswer(true)
        }
        falseButton.setOnClickListener { _: View ->
            checkAnswer(false)
        }
        nextButton.setOnClickListener{
            quizViewModel.moveToNext()
            updateQuestion()
        }
        previousButton.setOnClickListener{
            quizViewModel.moveToPrevious()
            updateQuestion()
        }
        cheatButton.setOnClickListener {
            //start CheatActivity
            val answerIsTrue = quizViewModel.currentQuestionAnswer //sends the answer to the activity
            val intent = CheatActivity.newIntent(this@MainActivity, answerIsTrue) //this constructor will start the activity.
            startActivityForResult(intent, REQUEST_CODE_CHEAT)   //pass the intent here to start it. We are also expecting a return.
        }
        updateQuestion()
    }

    //This checks if the user cheated and updates isCheater if needed
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) {
            return
        }

        if (requestCode == REQUEST_CODE_CHEAT) {
            quizViewModel.isCheater =
                data?.getBooleanExtra(EXTRA_ANSWER_SHOWN, false) ?: false
        }
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart() called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume() called")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause() called")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop() called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy() called")
    }

    //Saves instance state so when app remembers where it left off (if terminated by OS)
    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        Log.i(TAG, "onSaveInstanceState")
        savedInstanceState.putInt(KEY_INDEX, quizViewModel.currentIndex)
    }

    private fun updateQuestion() {
        val questionTextResId = quizViewModel.currentQuestionText
        questionTextView.setText(questionTextResId)
    }

    private fun checkAnswer(userAnswer: Boolean) {
        val correctAnswer = quizViewModel.currentQuestionAnswer

        //Cheating does not give points
        if ((userAnswer == correctAnswer) && !quizViewModel.isCheater) {
            quizViewModel.score += 1
        }
        //Checks to see if user cheated
        val messageResId = when {
            quizViewModel.isCheater -> R.string.judgement_toast
            userAnswer == correctAnswer -> R.string.correct_toast
            else -> R.string.incorrect_toast
        }
        val toast = Toast.makeText(this, messageResId, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.TOP, 0, 500)
        //This will adjust the toast to display towards the top of the screen instead of bottom
        toast.show()

        //Shows the score
        if (quizViewModel.currentIndex == quizViewModel.questionBankSize - 1) {
            val score = (quizViewModel.score * 100) / quizViewModel.questionBankSize //Convert to percentage(rounded)
            val toastScore =
                Toast.makeText(this, "Score: " + score + "%", Toast.LENGTH_SHORT)
            toastScore.setGravity(Gravity.TOP, 0, 500)
            toastScore.show()
        }
    }
}
