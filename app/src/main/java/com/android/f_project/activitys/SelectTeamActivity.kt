package com.android.f_project.activitys

import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import com.android.f_project.R
import com.android.f_project.databinding.ActivitySelectTeamBinding
import com.android.f_project.datamodel.PlayerModel
import com.android.f_project.datamodel.TeamModel
import com.android.f_project.util.MyDbHelper
import com.android.f_project.util.OnSwipeTouchListener

class SelectTeamActivity : AppCompatActivity() {

    private val teams = ArrayList<TeamModel>()
    private var teamCounter: Int = 0
    private var teamCounter2: Int = 1
    private lateinit var binding: ActivitySelectTeamBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectTeamBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val halfHeight = displayMetrics.heightPixels.div(2)

        getTeams()

        if (teams.isNotEmpty()) {
            populateView(teams)
            populateView2(teams)
        }

        binding.previousButtonTeam.setOnClickListener {
            previousTeam()
        }

        binding.nextButtonTeam.setOnClickListener {
            nextTeam()
        }

        binding.previousButtonTeam2.setOnClickListener {
            previousTeam2()
        }

        binding.nextButtonTeam2.setOnClickListener {
            nextTeam2()
        }

        binding.selectButton.setOnClickListener {
            selectLineup()
        }

        binding.constraintlayoutSelectteam.setOnTouchListener(object : OnSwipeTouchListener() {
            override fun onSwipeLeft(y: Float) {
                if (y >= halfHeight) {
                    nextTeam2()

                } else {
                    nextTeam()

                }
            }

            override fun onSwipeRight(y: Float) {
                if (y >= halfHeight) {
                    previousTeam2()

                } else {
                    previousTeam()
                }
            }
        })
    }

    private fun previousTeam() {
        if (teamCounter - 1 >= 0) teamCounter -= 1
        else teamCounter = teams.size - 1
        populateView(teams)
    }

    private fun nextTeam() {
        if (teamCounter + 1 < teams.size && teamCounter + 1 >= 0) teamCounter += 1
        else teamCounter = 0
        populateView(teams)
    }

    private fun previousTeam2() {
        if (teamCounter2 - 1 >= 0) teamCounter2 -= 1
        else teamCounter2 = teams.size - 1
        populateView2(teams)
    }

    private fun nextTeam2() {
        if (teamCounter2 + 1 < teams.size && teamCounter2 + 1 >= 0) teamCounter2 += 1
        else teamCounter2 = 0
        populateView2(teams)
    }


    private fun getTeams() {
        teams.add(
            TeamModel(
                "1",
                getString(R.string.team_bayern_muenchen),
                "Germany",
                "Bundesliga",
                R.drawable.bayern_muenchen,
                sqlconnections(getString(R.string.team_bayern_muenchen))
            )
        )
        teams.add(
            TeamModel(
                "2",
                getString(R.string.team_borussia_dortmund),
                "Germany",
                "Bundesliga",
                R.drawable.borussia_dortmund,
                sqlconnections(getString(R.string.team_borussia_dortmund))
            )
        )
        teams.add(
            TeamModel(
                "3",
                getString(R.string.team_liverpool),
                "England",
                "Premier League",
                R.drawable.liverpool,
                sqlconnections(getString(R.string.team_liverpool))
            )
        )
        teams.add(
            TeamModel(
                "4",
                getString(R.string.team_real_madrid),
                "Spain",
                "Primiera Division",
                R.drawable.real_madrid,
                sqlconnections(getString(R.string.team_real_madrid))
            )
        )
        teams.add(
            TeamModel(
                "5",
                getString(R.string.team_paris),
                "France",
                "Ligue 1",
                R.drawable.paris_saint_germain,
                sqlconnections(getString(R.string.team_paris))
            )
        )
        teams.add(
            TeamModel(
                "6",
                getString(R.string.team_manchester_city),
                "England",
                "Premier League",
                R.drawable.manchester_city,
                sqlconnections(getString(R.string.team_manchester_city))
            )
        )
    }

    private fun sqlconnections(teamName: String): MutableList<PlayerModel> {
        val myDatabase = MyDbHelper(this).readableDatabase
        val playerList = mutableListOf<PlayerModel>()
        lateinit var playa: PlayerModel
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
                        PlayerModel(
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

    private fun populateView(teamList: ArrayList<TeamModel>) {
        val chosen = teamList[teamCounter]
        binding.teamName.text = chosen.name
        binding.leagueName.text = chosen.league
        chosen.logo_res?.let { binding.teamLogo.setImageResource(it) }
    }

    private fun populateView2(teamList: ArrayList<TeamModel>) {
        val chosen = teamList[teamCounter2]
        binding.teamName2.text = chosen.name
        binding.leagueName2.text = chosen.league
        chosen.logo_res?.let { binding.teamLogo2.setImageResource(it) }
    }

    private fun selectLineup() {
        this.startActivity(Intent(this, SelectLineupActivity::class.java).apply {
            putExtra("selectedTeam", teams[teamCounter])
            putExtra("selectedTeam2", teams[teamCounter2])
            putExtra("highscore", intent.getIntExtra("highscore", 0))
        })
    }
}
