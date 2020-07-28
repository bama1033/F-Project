package com.android.f_project.activitys

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.android.f_project.MyDbHelper
import com.android.f_project.R
import com.android.f_project.datamodel.Player_model
import com.android.f_project.datamodel.Team_model
import kotlinx.android.synthetic.main.activity_select_team.*

class SelectTeamActivity : AppCompatActivity() {

    private val teams = ArrayList<Team_model>()
    private var counter: Int = 0
    private var counter2: Int = 1
    private lateinit var selectedTeam: Team_model
    private lateinit var selectedTeam2: Team_model

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
                sqlconnections(getString(R.string.team_bayern_muenchen))
            )
        )
        teams.add(
            Team_model(
                "2",
                getString(R.string.team_borussia_dortmund),
                "Germany",
                "Bundesliga",
                R.drawable.borussia_dortmund,
                sqlconnections(getString(R.string.team_borussia_dortmund))
            )
        )
        teams.add(
            Team_model(
                "3",
                getString(R.string.team_liverpool),
                "England",
                "Premier League",
                R.drawable.liverpool,
                sqlconnections(getString(R.string.team_liverpool))
            )
        )
        teams.add(
            Team_model(
                "4",
                getString(R.string.team_real_madrid),
                "Spain",
                "Primiera Division",
                R.drawable.real_madrid,
                sqlconnections(getString(R.string.team_real_madrid))
            )
        )
        teams.add(
            Team_model(
                "5",
                getString(R.string.team_paris),
                "France",
                "Ligue 1",
                R.drawable.paris_saint_germain,
                sqlconnections(getString(R.string.team_paris))
            )
        )
        teams.add(
            Team_model(
                "6",
                getString(R.string.team_manchester_city),
                "England",
                "Premier League",
                R.drawable.manchester_city,
                sqlconnections(getString(R.string.team_manchester_city))
            )
        )
    }

    private fun sqlconnections(teamName: String): MutableList<Player_model> {
        val myDatabase = MyDbHelper(this).readableDatabase
        val playerList = mutableListOf<Player_model>()
        lateinit var playa: Player_model
        AsyncTask.execute {
            val cursor = myDatabase.rawQuery(
                "SELECT _id,Name,Age,Nationality,\"Jersey Number\",Position,Overall,Dribbling,ShortPassing,Finishing,Interceptions,GKReflexes FROM data\n" +
                        "WHERE Club=?;", arrayOf(teamName)
            )
            if (cursor.moveToFirst()) {

                while (!cursor.isAfterLast) {
                    //your code to implement
                    val id = cursor.getString(0)
                    val name = cursor.getString(1)
                    val age = cursor.getString(2)
                    val nationality = cursor.getString(3)
                    val number = cursor.getString(4)
                    val position = cursor.getString(5)
                    val overall = cursor.getString(6)
                    val dribbling = cursor.getString(7)
                    val passing = cursor.getString(8)
                    val shooting = cursor.getString(9)
                    val defending = cursor.getString(10)
                    val goalkeeping = cursor.getString(11)
                    playa =
                        Player_model(
                            id,
                            name,
                            age,
                            teamName,
                            nationality,
                            dribbling,
                            passing,
                            shooting,
                            defending,
                            goalkeeping,
                            number,
                            position,
                            overall
                        )
                    playerList.add(playa)
                    cursor.moveToNext()
                }
            }
            cursor.close()
        }
        return playerList
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
