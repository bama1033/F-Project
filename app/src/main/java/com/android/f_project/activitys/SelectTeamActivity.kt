package com.android.f_project.activitys

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.android.f_project.R
import com.android.f_project.datamodel.Team_model
import kotlinx.android.synthetic.main.activity_select_team.*

class SelectTeamActivity : AppCompatActivity() {

    private val teams = ArrayList<Team_model>()
    private var counter: Int = 0
    private var counter2: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_team)
        getTeams()
        if (teams.isNotEmpty()) {
            populateView(teams)
            populateView2(teams)
        }

        previous_button_team.setOnClickListener {
            previousTeam()
        }

        next_button_team.setOnClickListener {
            nextTeam()
        }

        previous_button_team2.setOnClickListener {
            previousTeam2()
        }

        next_button_team2.setOnClickListener {
            nextTeam2()
        }

        select_button.setOnClickListener {
            selectLineup()
        }
    }

    private fun previousTeam() {
        if (counter - 1 >= 0) counter -= 1
        else counter = teams.size - 1
        populateView(teams)
    }

    private fun nextTeam() {
        if (counter + 1 < teams.size && counter + 1 >= 0) counter += 1
        else counter = 0
        populateView(teams)
    }

    private fun previousTeam2() {
        if (counter2 - 1 >= 0) counter2 -= 1
        else counter2 = teams.size - 1
        populateView2(teams)
    }

    private fun nextTeam2() {
        if (counter2 + 1 < teams.size && counter2 + 1 >= 0) counter2 += 1
        else counter2 = 0
        populateView2(teams)
    }


    private fun getTeams() {
        teams.add(
            Team_model(
                "1",
                getString(R.string.team_bayern_muenchen),
                "Germany",
                "Bundesliga",
                R.drawable.bayern_muenchen,
                null
//                ArrayList<Team_model>(
//                    Player_model(
//                        "1",
//                        "asa",
//                        "10",
//                        "asdas",
//                    "dassa",
//                        "10",
//                        "dasd",
//                        "as"
//                    )
//                )
            )
        )
        teams.add(
            Team_model(
                "2",
                getString(R.string.team_borussia_dortmund),
                "Germany",
                "Bundesliga",
                R.drawable.borussia_dortmund,
                null
            )
        )
        teams.add(
            Team_model(
                "3",
                getString(R.string.team_liverpool),
                "England",
                "Premier League",
                R.drawable.liverpool,
                null
            )
        )
        teams.add(
            Team_model(
                "4",
                getString(R.string.team_real_madrid),
                "Spain",
                "Primiera Division",
                R.drawable.real_madrid,
                null
            )
        )
        teams.add(
            Team_model(
                "5",
                getString(R.string.team_paris),
                "France",
                "Ligue 1",
                R.drawable.paris_saint_germain,
                null
            )
        )
        teams.add(
            Team_model(
                "6",
                getString(R.string.team_manchester_city),
                "England",
                "Premier League",
                R.drawable.manchester_city,
                null
            )
        )
    }

    private fun populateView(teamList: ArrayList<Team_model>) {
        val chosen = teamList[counter]
        team_name.text = chosen.name
        league_name.text = chosen.league
        chosen.logo_res?.let { team_logo.setImageResource(it) }
    }

    private fun populateView2(teamList: ArrayList<Team_model>) {
        val chosen = teamList[counter2]
        team_name2.text = chosen.name
        league_name2.text = chosen.league
        chosen.logo_res?.let { team_logo2.setImageResource(it) }
    }


    private fun selectLineup() {
        this.startActivity(Intent(this, SelectLineupActivity::class.java).apply {
            putExtra("selectedTeam", teams[counter])
            putExtra("selectedTeam2", teams[counter2])
        })
    }
}
