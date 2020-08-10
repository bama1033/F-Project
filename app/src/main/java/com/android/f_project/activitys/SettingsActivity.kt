package com.android.f_project.activitys

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.f_project.R
import com.android.f_project.checkPlausibleAccountId
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {
    var clicked = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val sharedPref = this.getSharedPreferences("AccountId", Context.MODE_PRIVATE) ?: return
        val highScore = sharedPref.getString("AccountId", "ERROR")

        Account_Textview.setText(highScore)

        Restore_Button.setOnClickListener {
            if (clicked) {
                if (AccountRestore_Textview.text.isNullOrEmpty())
                    longToast("Field is empty!")
                else {
                    if (checkPlausibleAccountId(AccountRestore_Textview.text.toString()))
                        replaceAccountID(sharedPref)
                    else {
                        longToast("Entered AccountId is not valid(8 letters long/only letters and number)")
                    }
                }
            } else {
                longToast("Are u sure u want to restore old Account? Press Button again")
                clicked = true
            }
        }
    }

    private fun longToast(text: String) {
        Toast.makeText(
            this,
            text,
            Toast.LENGTH_LONG
        ).show()
    }

    private fun replaceAccountID(sharedPref: SharedPreferences) {
        with(sharedPref.edit()) {
            putString("AccountId", AccountRestore_Textview.text.toString())
            commit()
        }
        Account_Textview.text = AccountRestore_Textview.text
    }
}
