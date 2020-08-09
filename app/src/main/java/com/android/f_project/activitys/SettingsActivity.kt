package com.android.f_project.activitys

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.f_project.R
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {
    var clicked = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val sharedPref = this.getSharedPreferences("AccountId", Context.MODE_PRIVATE) ?: return
        val highScore = sharedPref.getString("AccountId", "ERROR")

        //TODO CHECK IF ACCID IS PLAUSIBLE E.. CONSISTS ONLY OF NUMBER CHARS 8 long AND NOTHIGN ELSE
        Account_Textview.setText(highScore)

        Restore_Button.setOnClickListener {
            if (clicked) {
                if (AccountRestore_Textview.text.isNullOrEmpty())
                    Toast.makeText(
                        this,
                        "Field is empty!",
                        Toast.LENGTH_LONG
                    ).show()
                else {
                    replaceAccountID(sharedPref)
                }
            } else {
                Toast.makeText(
                    this,
                    "Are u sure u want to restore old Account? Press Button again",
                    Toast.LENGTH_LONG
                ).show()
                clicked = true
            }
        }
    }

    private fun replaceAccountID(sharedPref: SharedPreferences) {
        with(sharedPref.edit()) {
            putString("AccountId", AccountRestore_Textview.text.toString())
            commit()
        }
        Account_Textview.text = AccountRestore_Textview.text
    }
}
