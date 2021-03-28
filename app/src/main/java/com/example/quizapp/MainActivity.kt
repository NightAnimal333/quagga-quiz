package com.example.quizapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import org.w3c.dom.Text


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val db = FirebaseFirestore.getInstance()

        db.collection("highscores")
            .orderBy("score", Query.Direction.DESCENDING)
            .limit(3)
            .get()
            .addOnCompleteListener(OnCompleteListener<QuerySnapshot> { task ->
                if (task.isSuccessful) {
                    findViewById<TextView>(R.id.high_score_one).text = ""
                    for (document in task.result!!) {
                        findViewById<TextView>(R.id.high_score_one).text = (findViewById<TextView>(R.id.high_score_one).text.toString() + document.data["nickname"] + " : " + document.data["score"] + '\n')
                        Log.d(
                            "DEB",
                            document.id + " => " + document.data
                        )
                    }
                } else {
                    findViewById<TextView>(R.id.high_score_one).text = getString(R.string.no_highscore)
                        Log.w(
                        "DEB",
                        "Error getting documents",
                        task.exception
                    )
                }
            })

        val startQuizButton = findViewById<Button>(R.id.start_quiz_button)
        startQuizButton.setOnClickListener {
            val intent = Intent(this, QuizActivity::class.java)
            startActivity(intent)
        }



    }



}