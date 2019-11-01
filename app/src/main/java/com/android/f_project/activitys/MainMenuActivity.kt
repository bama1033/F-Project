package com.android.f_project.activitys

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.f_project.R
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main_menu.*
import kotlin.system.exitProcess

class MainMenuActivity : AppCompatActivity() {

    private lateinit var mDocRef: DocumentReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)

        button_start.setOnClickListener {
            startGame()
        }
        button_scores.setOnClickListener {
            showHighscore()
        }
        button_quit.setOnClickListener {
            exitProcess(-1)
        }
    }

    private fun startGame() {
        this.startActivity(Intent(this, SelectTeamActivity::class.java))
    }

    private fun showHighscore() {


        mDocRef = FirebaseFirestore.getInstance().document("Score/HighScores")
        mDocRef.get().addOnSuccessListener { result ->
            if (result != null) {
                Log.d("FirebaseManager", "Data: ${result.data}")
                Toast.makeText(
                    this,
                    "Your highest score of ${result.data?.get("Dortmund")} to ${result.data?.get("Bayern")}",
                    Toast.LENGTH_SHORT
                ).show()
            } else Toast.makeText(this, "You have no Highscore yet", Toast.LENGTH_SHORT).show()
        }
            .addOnFailureListener { exception ->
                Log.w("FirebaseManager", "Error getting documents.", exception)
            }
    }
}
