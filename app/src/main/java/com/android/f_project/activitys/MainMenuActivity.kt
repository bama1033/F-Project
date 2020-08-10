package com.android.f_project.activitys

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.f_project.generateAccountID
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main_menu.*


class MainMenuActivity : AppCompatActivity() {

    private lateinit var mDocRef: DocumentReference
    private var highScore: String? = ""
    var highestGoals: String? = "0"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(com.android.f_project.R.layout.activity_main_menu)
        val sharedPref = this.getSharedPreferences(
            "AccountId", Context.MODE_PRIVATE
        ) ?: return
        highScore = sharedPref.getString("AccountId", "")
        if (highScore?.isEmpty()!!) {
            addAccountId(sharedPref)
        }
        getHighscore()

        button_start.setOnClickListener {
            this.startActivity(Intent(this, SelectTeamActivity::class.java).apply {
                putExtra("highscore", highestGoals?.toInt())
            })
        }
        button_scores.setOnClickListener {
            showHighscore()
        }
        button_settings.setOnClickListener {
            this.startActivity(Intent(this, SettingsActivity::class.java))
        }
        button_quit.setOnClickListener {
            this.finishAffinity()
        }
    }

    private fun getHighscore() {
        mDocRef = FirebaseFirestore.getInstance().document("Score/$highScore")
        mDocRef.get().addOnSuccessListener { result ->
            Log.d("FirebaseManager", "Data: ${result.data}")
            highestGoals = result.data?.get("HomeTeamCounter").toString()
        }
    }

    private fun addAccountId(sharedPref: SharedPreferences) {
        with(sharedPref.edit()) {
            putString("AccountId", generateAccountID())
            commit()
        }
    }

    override fun onBackPressed() {
        this.finishAffinity()
    }

    private fun showHighscore() {
        if (highScore.equals("") || highScore.equals("ERROR")) {
            Log.w("FirebaseManager", "Error with AccountId")
            Toast.makeText(
                this,
                "Problem with AccountId",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            mDocRef = FirebaseFirestore.getInstance().document("Score/$highScore")
            Toast.makeText(
                this,
                "Fetching Highscores...",
                Toast.LENGTH_LONG
            ).show()
            button_scores.isEnabled = false
            mDocRef.get().addOnSuccessListener { result ->
                if (result != null) {
                    Log.d("FirebaseManager", "Data: ${result.data}")
                    Toast.makeText(
                        this,
                        "Your highest score was:  ${result.data?.get("HomeTeam")} " +
                                "${result.data?.get("HomeTeamCounter")} " + "to" +
                                " ${result.data?.get("AwayTeam")} " +
                                "${result.data?.get("AwayTeamCounter")}",
                        Toast.LENGTH_SHORT
                    ).show()
                    button_scores.isEnabled = true
                } else Toast.makeText(this, "You have no Highscore yet", Toast.LENGTH_SHORT).show()
                button_scores.isEnabled = true
            }
                .addOnFailureListener { exception ->
                    Log.w("FirebaseManager", "Error getting documents.", exception)
                    Toast.makeText(
                        this,
                        "Service currently disabled",
                        Toast.LENGTH_SHORT
                    ).show()
                    button_scores.isEnabled = true
                }
        }
    }
}
