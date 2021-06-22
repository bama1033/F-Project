package com.android.f_project.activitys

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.f_project.databinding.ActivitySettingsBinding
import com.android.f_project.util.checkPlausibleAccountId

class SettingsActivity : AppCompatActivity() {
    private var clicked = false
    private lateinit var binding: ActivitySettingsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val sharedPref = this.getSharedPreferences("AccountId", Context.MODE_PRIVATE) ?: return
        val highScore = sharedPref.getString("AccountId", "ERROR")
        binding.AccountTextview.setText(highScore)

        binding.RestoreButton.setOnClickListener {
            if (clicked) {
                if (binding.AccountRestoreTextview.text.isNullOrEmpty())
                    longToast("Field is empty!")
                else {
                    if (checkPlausibleAccountId(
                            binding.AccountRestoreTextview.text.toString()
                        )
                    )
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
            putString("AccountId", binding.AccountRestoreTextview.text.toString())
            commit()
        }
        binding.AccountTextview.text = binding.AccountRestoreTextview.text
    }
}
