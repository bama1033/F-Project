package com.android.f_project.activitys

import android.content.ClipData
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.View.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import com.android.f_project.R
import com.android.f_project.datamodel.FormationModel
import com.android.f_project.datamodel.PlayerModel
import com.android.f_project.datamodel.TeamModel
import com.android.f_project.util.OnSwipeTouchListener
import kotlinx.android.synthetic.main.activity_lineup.*


class SelectLineupActivity : AppCompatActivity() {

    private val formations = ArrayList<androidx.constraintlayout.widget.Group>()
    private var selectedFormation: Int = 0

    private var formation0: FormationModel = FormationModel("0", "Hängende Spitze", "2-3-1-4")
    private var formation1: FormationModel = FormationModel("1", "Standard", "3-1-2-4")
    private var formation2: FormationModel = FormationModel("2", "Bollwerk", "1-3-1-5")
    private var formation3: FormationModel = FormationModel("3", "Balanced", "1-2-4-3")
    private lateinit var selectedTeamHome: TeamModel
    private var listOfFormations = mutableListOf(formation0, formation1, formation2, formation3)
    //    changeFormation(){
    //        1-2-3-4
    //        2-1-2-5
    //        1-1-3-5
    //        1-3-2-4


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lineup)
        selectedTeamHome = intent.getParcelableExtra("selectedTeam")

        val player = selectedTeamHome.players.take(11)
        formations.add(group0)
        formations.add(group1)
        formations.add(group2)
        formations.add(group3)

        toggleView()

        populateViews(player)

        constraintlayout_lineup.setOnTouchListener(object : OnSwipeTouchListener() {
            override fun onSwipeLeft(y: Float) {
                nextFormation()
            }

            override fun onSwipeRight(y: Float) {
                previousFormation()
            }
        })
        interaction_one_lineup.setOnClickListener {
            swapNameNumber()
        }

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

    private fun swapNameNumber() {
        for (i in 0 until constraintlayout_lineup.childCount) {
            val v = constraintlayout_lineup.getChildAt(i)
            if (v is ConstraintLayout) {
                var boolTextViewNumberNameSwitch = true
                v.children.forEach {
                    if (it is AppCompatTextView)
                        boolTextViewNumberNameSwitch = if (boolTextViewNumberNameSwitch) {
                            switchVisibility(it)
                            false
                        } else {
                            switchVisibility(it)
                            true
                        }
                }
            }
        }
    }

    private fun populateViews(player: List<PlayerModel>) {
        var counter = 0
        for (i in 0 until constraintlayout_lineup.childCount) {
            val v = constraintlayout_lineup.getChildAt(i)
            if (v is ConstraintLayout) {
                var boolTextViewNumberNameSwitch = true
                v.children.forEach {
                    if (it is AppCompatTextView)
                        boolTextViewNumberNameSwitch = if (boolTextViewNumberNameSwitch) {
                            it.text = player[counter].number
                            false
                        } else {
                            it.text = player[counter].name
                            counter += 1
                            if (counter == 11) {
                                counter = 0
                            }
                            true
                        }
                }
            }
        }
    }

    private fun switchVisibility(it: AppCompatTextView) {
        if (it.visibility == VISIBLE) {
            it.visibility = GONE
        } else
            it.visibility = VISIBLE
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
            putExtra("highscore", intent.getIntExtra("highscore", 0))
        })
        finish()
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
}



