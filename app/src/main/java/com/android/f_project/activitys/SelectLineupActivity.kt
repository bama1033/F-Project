package com.android.f_project.activitys

import android.content.ClipData
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.View.*
import androidx.appcompat.app.AppCompatActivity
import com.android.f_project.R
import com.android.f_project.datamodel.Team_model
import kotlinx.android.synthetic.main.activity_lineup.*


class SelectLineupActivity : AppCompatActivity() {

    private val formations = ArrayList<androidx.constraintlayout.widget.Group>()
    private var selectedFormation: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lineup)

        formations.add(group0)
        formations.add(group1)

        interaction_two_lineup.setOnClickListener {
            startGame()
        }

        previous_button_formation.setOnClickListener {
            previousFormation()
        }

        next_button_formation.setOnClickListener {
            nextFormation()
        }

        player_1.setOnTouchListener(MyTouchListener())
        player_12.setOnTouchListener(MyTouchListener())
    }

    private fun previousFormation() {
        if (selectedFormation - 1 >= 0) selectedFormation -= 1
        else selectedFormation = formations.size - 1
        toggleView()

    }

    private fun nextFormation() {
        if (selectedFormation + 1 < formations.size && selectedFormation + 1 >= 0) selectedFormation += 1
        else selectedFormation = 0
        toggleView()
    }


    fun toggleView() {
        for (i in formations) {
            i.visibility = View.GONE
        }
        formations[selectedFormation].visibility = VISIBLE
    }

    private fun startGame() {
        this.startActivity(Intent(this, GameActivity::class.java).apply {
            putExtra("selectedTeam", intent.getParcelableExtra<Team_model>("selectedTeam"))
            putExtra("selectedTeam2", intent.getParcelableExtra<Team_model>("selectedTeam2"))
        })
    }

    private class MyTouchListener : OnTouchListener {
        override fun onTouch(
            view: View,
            motionEvent: MotionEvent
        ): Boolean {
            return if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                val data = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    ClipData.newPlainText("", "")
                } else {
                    TODO("VERSION.SDK_INT < HONEYCOMB")
                }
                val shadowBuilder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    DragShadowBuilder(
                        view
                    )
                } else {
                    TODO("VERSION.SDK_INT < HONEYCOMB")
                }
                view.startDrag(data, shadowBuilder, view, 0)
//                view.visibility = VISIBLE
                true
            } else {
//                view.visibility = INVISIBLE
                false
            }
        }
    }

    //First select Aufstellung
    //Then select players(init highest rated players take place)
    //Then safe
//    IMplement button that switches between playername -- number == position


//    changeFormation(){
//        2-3-1-4
//        3-1-2-4

//        1-2-3-4
//        1-3-1-5
//        2-1-2-5
//        1-1-3-5
//        1-3-2-4
//        1-2-4-3
//    }
}



