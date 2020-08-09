package com.android.f_project.activitys

import android.content.ClipData
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.View.*
import androidx.appcompat.app.AppCompatActivity
import com.android.f_project.R
import com.android.f_project.datamodel.FormationModel
import com.android.f_project.datamodel.TeamModel
import kotlinx.android.synthetic.main.activity_lineup.*


class SelectLineupActivity : AppCompatActivity() {

    private val formations = ArrayList<androidx.constraintlayout.widget.Group>()
    private var selectedFormation: Int = 0

    private var formation0: FormationModel = FormationModel("0", "Hängende Spitze", "2-3-1-4")
    private var formation1: FormationModel = FormationModel("1", "Standard", "3-1-2-4")
    private var formation2: FormationModel = FormationModel("2", "Bollwerk", "1-3-1-5")
    private var formation3: FormationModel = FormationModel("3", "Balanced", "1-2-4-3")
    private var listOfFormations = mutableListOf(formation0, formation1, formation2, formation3)
    //    changeFormation(){
    //        1-2-3-4
    //        2-1-2-5
    //        1-1-3-5
    //        1-3-2-4

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lineup)

        constraintlayout_lineup.setOnTouchListener(object : OnSwipeTouchListener() {
            override fun onSwipeLeft(y: Float) {
                nextFormation()
            }

            override fun onSwipeRight(y: Float) {
                previousFormation()
            }
        })


        formations.add(group0)
        formations.add(group1)
        formations.add(group2)
        formations.add(group3)

        toggleView()

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


    private fun toggleView() {
        for (i in formations) {
            i.visibility = GONE
        }

        formation_text.text = listOfFormations[selectedFormation].name
        formation.text = listOfFormations[selectedFormation].distribution
        formations[selectedFormation].visibility = VISIBLE
    }

    private fun startGame() {
        this.startActivity(Intent(this, GameActivity::class.java).apply {
            putExtra("selectedTeam", intent.getParcelableExtra<TeamModel>("selectedTeam"))
            putExtra("selectedTeam2", intent.getParcelableExtra<TeamModel>("selectedTeam2"))
            putExtra("highscore", intent.getIntExtra("highscore",0))
        })
    }

    private class MyTouchListener : OnTouchListener {
        override fun onTouch(
            view: View,
            motionEvent: MotionEvent
        ): Boolean {
            return if (motionEvent.action == MotionEvent.ACTION_DOWN) {
                val data =
                    ClipData.newPlainText("", "")
                val shadowBuilder =
                    DragShadowBuilder(
                        view
                    )
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
    //TODO    Implement button that switches between playername -- number == position
}



