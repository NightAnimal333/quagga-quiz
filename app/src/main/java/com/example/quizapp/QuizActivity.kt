package com.example.quizapp

import android.content.Intent
import android.content.res.ColorStateList
import android.content.res.XmlResourceParser
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.google.android.material.snackbar.Snackbar
import org.xmlpull.v1.XmlPullParser


class QuizActivity : AppCompatActivity() {

    private val amountOfQuestions = 10

    private var score = 0
    private val answers: ArrayList<String> = ArrayList()
    private var viewedQuestion = 0

    private var defaultButtonTextColour: ColorStateList? = null

    public lateinit var questions: List<Question>

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_quiz)

        defaultButtonTextColour = findViewById<Button>(R.id.option_one).getTextColors()

        questions = generateQuestions()

        showQuestion(this.viewedQuestion)
        updateScoreUI()

        findViewById<Button>(R.id.option_one).setOnClickListener {
            submitAnswer(0)
        }
        findViewById<Button>(R.id.option_two).setOnClickListener {
            submitAnswer(1)
        }
        findViewById<Button>(R.id.option_three).setOnClickListener {
            submitAnswer(2)
        }
        findViewById<Button>(R.id.option_four).setOnClickListener {
            submitAnswer(3)
        }
        findViewById<ImageButton>(R.id.previous_question_button).setOnClickListener {
            navigateToQuestion(viewedQuestion - 1)
        }
        findViewById<ImageButton>(R.id.next_question_button).setOnClickListener {
            navigateToQuestion(viewedQuestion + 1)
        }

    }

    private fun showQuestion(questionNumber: Int){

        val question = questions[questionNumber]

        findViewById<TextView>(R.id.question_text).text = question.text

        findViewById<Button>(R.id.option_one).text = question.options[0].text
        findViewById<Button>(R.id.option_two).text = question.options[1].text
        findViewById<Button>(R.id.option_three).text = question.options[2].text
        findViewById<Button>(R.id.option_four).text = question.options[3].text

        findViewById<TextView>(R.id.question_number).text = getString(R.string.current_question, (viewedQuestion + 1), amountOfQuestions)

        if (question.isAnswered){
            showCorrectAnswers(questionNumber)
        } else {
            resetTextColours()
        }

        findViewById<ImageButton>(R.id.previous_question_button).isVisible = viewedQuestion != 0

    }

    private fun showCorrectAnswers(questionNumber: Int){

        val question = questions[questionNumber]

        if (question.options[0].isCorrect){
            findViewById<Button>(R.id.option_one).setTextColor(Color.GREEN)
        } else {
            findViewById<Button>(R.id.option_one).setTextColor(Color.RED)
        }
        if (question.options[1].isCorrect){
            findViewById<Button>(R.id.option_two).setTextColor(Color.GREEN)
        } else {
            findViewById<Button>(R.id.option_two).setTextColor(Color.RED)
        }
        if (question.options[2].isCorrect){
            findViewById<Button>(R.id.option_three).setTextColor(Color.GREEN)
        } else {
            findViewById<Button>(R.id.option_three).setTextColor(Color.RED)
        }
        if (question.options[3].isCorrect){
            findViewById<Button>(R.id.option_four).setTextColor(Color.GREEN)
        } else {
            findViewById<Button>(R.id.option_four).setTextColor(Color.RED)
        }

    }

    private fun resetTextColours(){

        findViewById<Button>(R.id.option_one).setTextColor(defaultButtonTextColour)
        findViewById<Button>(R.id.option_two).setTextColor(defaultButtonTextColour)
        findViewById<Button>(R.id.option_three).setTextColor(defaultButtonTextColour)
        findViewById<Button>(R.id.option_four).setTextColor(defaultButtonTextColour)

    }

    private fun updateScoreUI(){
        findViewById<TextView>(R.id.player_score).text = getString(R.string.your_score, score)
    }

    private fun navigateToQuestion(questionNumber: Int){

        when {
            questionNumber < 0 -> {
               return
            }
            questionNumber >= questions.size -> {

                if (!isAllAnswered()){
                    val snack = Snackbar.make(findViewById(R.id.activity_quiz),getString(R.string.unanswered_questions),Snackbar.LENGTH_LONG)
                    snack.show()

                    return
                }

                val intent = Intent(this, ResultsActivity::class.java)
                intent.putExtra("score", score)
                intent.putStringArrayListExtra("answers", answers)
                startActivity(intent)
                finish()
            }
            else -> {
                viewedQuestion = questionNumber
                showQuestion(viewedQuestion)
            }
        }
    }

    private fun isAllAnswered(): Boolean{
        for (i in questions){
            if (!i.isAnswered){
                return false
            }
        }
        return true
    }

    private fun submitAnswer(optionNumber: Int){

        if (questions[viewedQuestion].isAnswered){

            val snack = Snackbar.make(findViewById(R.id.activity_quiz),getString(R.string.already_answered),Snackbar.LENGTH_LONG)
            snack.show()

            return

        } else {

            val question = questions[viewedQuestion]

            question.isAnswered = true

            if (question.options[optionNumber].isCorrect) {
                score++
                answers.add(question.id.toString() + ":" + "true")
            } else {
                score -= 2
                answers.add(question.id.toString() + ":" + "false")
            }

            updateScoreUI()
            showCorrectAnswers(viewedQuestion)

        }

    }

    private fun generateQuestions(): MutableList<Question> {

        val xmlResourceParser: XmlResourceParser = resources.getXml(R.xml.questions)

        var eventType  = xmlResourceParser.eventType

        val allQuestions: MutableList<Question> = mutableListOf<Question>()
        val selectedQuestions: MutableList<Question> = mutableListOf<Question>()

        var question = Question()
        var option = Option()

        var tagValue: String = ""


        while (eventType != XmlPullParser.END_DOCUMENT){
            when (eventType) {
                XmlPullParser.START_DOCUMENT -> {
                    Log.d("DEB", "Start document")
                }
                XmlPullParser.START_TAG -> {
                    Log.d("DEB", "Start tag: " + xmlResourceParser.name)
                    tagValue = xmlResourceParser.name
                    if (tagValue == "question"){
                        question = Question()
                        question.id = xmlResourceParser.getAttributeValue(null, "id").toInt()
                    }
                    if (tagValue == "option"){
                        option = Option()
                        val isCorrectString = xmlResourceParser.getAttributeValue(null, "is_correct")
                        option.isCorrect = isCorrectString == "true"
                    }
                }
                XmlPullParser.END_TAG -> {
                    Log.d("DEB", "End tag: " + xmlResourceParser.name)
                    if (xmlResourceParser.name == "question"){
                        question.options.shuffle()
                        allQuestions.add(question)
                    }
                    if (xmlResourceParser.name == "option"){
                        question.options.add(option)
                    }
                }
                XmlPullParser.TEXT -> {
                    if (tagValue == "question"){
                        question.text = xmlResourceParser.text
                    }
                    if (tagValue == "option"){
                        option.text = xmlResourceParser.text
                    }
                    Log.d("DEB", "Text name: " + xmlResourceParser.name)
                    Log.d("DEB", "Text text: " + xmlResourceParser.text)
                }
            }
            eventType = xmlResourceParser.next()
        }

        Log.d("DEB", "All questions: $allQuestions")

        allQuestions.shuffle()

        for (i in 0 until amountOfQuestions){
            selectedQuestions.add(allQuestions[i])
        }

        Log.d("DEB", "Selected questions: $selectedQuestions")

        return selectedQuestions

    }

}