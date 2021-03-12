package com.example.quizapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class ResultsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)

        findViewById<TextView>(R.id.results_score_view).text = intent.getIntExtra("score", 0).toString()

        findViewById<Button>(R.id.back_to_main_menu_button).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            super.finish()
            finish()
        }
    }
}