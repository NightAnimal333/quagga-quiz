package com.example.quizapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.google.firebase.firestore.FirebaseFirestore


class ResultsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)

        val db = FirebaseFirestore.getInstance()

        findViewById<TextView>(R.id.results_score_view).text = getString(R.string.your_score, intent.getIntExtra("score", 0))

        findViewById<Button>(R.id.back_to_main_menu_button).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }

        findViewById<Button>(R.id.submit_score_button).setOnClickListener {
            val nicknameField: EditText = findViewById<EditText>(R.id.nickname_field)

            val answers_parsed: MutableMap<String, Boolean> = HashMap()
            val answers = intent.getStringArrayListExtra("answers")

            for (i in 0 until answers!!.size){
                val data = answers[i].split(":")

                answers_parsed[data[0]] = data[1] == "true"
            }

            val score: MutableMap<String, Any> = HashMap()
            score["nickname"] = if (nicknameField.text.toString() != "") nicknameField.text.toString() else nicknameField.hint.toString()
            score["score"] = intent.getIntExtra("score", -20)
            score["answers"] = answers_parsed

            db.collection("highscores")
                    .add(score)
                    .addOnSuccessListener { documentReference -> Log.d("DEB", "DocumentSnapshot added with ID: " + documentReference.id) }
                    .addOnFailureListener { e -> Log.w("DEB", "Error adding document", e) }

            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()

        }
    }
}