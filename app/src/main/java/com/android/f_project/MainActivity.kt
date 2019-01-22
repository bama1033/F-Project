package com.android.f_project

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.f_project.datamodel.Scene_model
import com.android.f_project.datamodel.Status_model
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.collections.HashMap


class MainActivity : AppCompatActivity() {
    private var home_team = "home_team"
    private var away_team = "away_team"
    private var scoreHome = 0
    private var scoreAway = 0
    private var gameTimer = 0
    private val contentList = ArrayList<Scene_model>()
    private var timeToken = 0
    private var gameOverToken = false
    var gameStatus: Status_model = Status_model.Midfield
    private lateinit var adapter: ListAdapter
    private lateinit var rv: RecyclerView
    private lateinit var mDocRef: DocumentReference
/*
        TODO -Implementing Spielverlauf ✔
        TODO -Implementing 0..90 ✔
        TODO States(wo sind wir im Feld) ✔
        TODO MakeTeams (GK Def MFs,Atk und suche einen random pro Scene aus)
        TODO -Implementing BaseLogic ✔
        TODO -Implementing Logic(PlayerStats, Strategy, Tactics, Moral)
        TODO -Buttons mit Actions versehen

        TODO -Teamselector UI ✔
        TODO -Mainmenu(welcomescreen, modeselect, highscore, exit)
        TODO -Gamemenu

        TODO -adding Teams  ---> API?
        TODO -adding Players ---> API?
        TODO -Animationen für actions
        TODO -Firebase-Anbindung um Highscore zu speichern
        TODO -Gamemodes (Create a Team_model, Online Competitive)
 */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setTeams(getString(R.string.team_dortmund), getString(R.string.team_bayern))
        rv = findViewById(R.id.game_course)

        adapter = ListAdapter(contentList)
        rv.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        rv.adapter = adapter

        startGame()
        delayMainStart(adapter, rv)
    }

    private fun setTeams(team_name_home: String, team_name_two: String) {
        setViewText(team_home, team_name_home)
        setViewText(team_away, team_name_two)
        setImage(image_home, R.drawable.borussia_dortmund)
        setImage(image_away, R.drawable.bayern_muenchen)
    }

    private fun setImage(view: ImageView, text: Int) {
        view.setImageResource(text)
    }

    private fun updateScore(team: String) {
        when (team) {
            "home" -> {
                scoreHome += 1
                setViewText(counter_home, scoreHome.toString())
            }
            "away" -> {
                scoreAway += 1
                setViewText(counter_away, scoreAway.toString())
            }
        }
    }

    private fun createScene() {
        val endgame = createTime()
        when (endgame) {
            true -> {
                addScene()
            }
            false -> {
                endGame()
            }
        }

        //pass shoot midfield attack defense
        //event,action,followup --> scene each of this?=listitem?
    }

    private fun createTime(): Boolean {
//        when (gameOverToken) {
//            true -> {
//                endGame()
//            }
//            false -> {
        val time: Int
        when (timeToken) {
            0, 1 -> {
                time = (2..5).shuffled().first()
                timeToken += 1
            }
            else -> {
                time = (6..9).shuffled().first()
                timeToken = 0
            }
        }
        return checkTimer(gameTimer + time)
    }

    private fun checkTimer(timer: Int): Boolean {
        //check halftime aswell
        when (timer) {
            in 88..90 -> {
                gameTimer = 90
                gameOverToken = true
                return false

            }
            in 91..96 -> {
                gameTimer = 93
                gameOverToken = true
                return false
            }
            else -> {
                gameTimer = timer
                //addScene(string)
                return true
            }
        }
    }

    //replace this for addScene
    private fun addSceneSpecial(special: String) {
        contentList.add(Scene_model(gameTimer.toString(), special, gameStatus))
        updateTime()
        updateAdapter()
    }

    private fun addScene(special: String = "special") {
        lateinit var content: String
        val dice = shuffle()
        when (gameStatus) {
            Status_model.Midfield -> {
                content = getString(R.string.scene_pass)
                when (dice) {
                    in 1..5 -> {
                        gameStatus = Status_model.Defense
                    }
                    in 6..10 -> {
                        gameStatus = Status_model.Attack
                    }
                }

            }
            Status_model.Attack -> {
                content = getString(R.string.scene_shot)
                when (shuffle()) {
                    in 1..5 -> {
                        content = content + " " + getString(R.string.scene_goal_success)
                        updateScore("home")
                    }
                    in 6..10 -> {
                        content = content + " " + getString(R.string.scene_goal_fail)
                    }
                }
                gameStatus = Status_model.Midfield
            }
            Status_model.Defense -> {
                content = getString(R.string.scene_defending)
                when (shuffle()) {
                    in 1..5 -> {
                        content = content + " " + getString(R.string.scene_got_goal)
                        updateScore("away")
                    }
                    in 6..10 -> {
                        when (shuffle()) {
                            in 1..5 -> {
                                content = content + " " + getString(R.string.scene_gk_block)
                            }
                            in 6..10 -> {
                                content = content + " " + getString(R.string.scene_block)
                            }
                        }
                    }
                }
                gameStatus = Status_model.Midfield
            }
        }
        contentList.add(Scene_model(gameTimer.toString(), content, gameStatus))
        updateTime()
        updateAdapter()
    }

    private fun shuffle(): Int {
        return (1..10).shuffled().first()
    }

    private fun updateTime() {
        game_clock.text = gameTimer.toString()

    }

    private fun startGame() {
        game_clock.text = gameTimer.toString()
        addSceneSpecial(
            getString(
                R.string.scene_beginning,
                getString(R.string.team_dortmund),
                getString(R.string.team_bayern)
            )
        )

    }

    private fun endGame() {
        addSceneSpecial(getString(R.string.scene_ending))
        saveStats()
    }

    private fun saveStats() {
        val datasave = HashMap<String, String>()

        datasave.put("User", "Me")
        datasave.put("Dortmund", counter_home.text.toString())
        datasave.put("Bayern", counter_away.text.toString())
        mDocRef = FirebaseFirestore.getInstance().document("Score/HighScores")
        mDocRef.set(datasave as Map<String, Any>).addOnSuccessListener {
            Log.d("FirebaseManager", "Upload Successful")
        }.addOnFailureListener {
            Log.d("FirebaseManager", "Klappt net")
        }
    }

    private fun setViewText(view: TextView, text: String) {
        view.text = text
    }

    private fun updateAdapter() {
        adapter.notifyDataSetChanged()
        rv.smoothScrollToPosition(adapter.itemCount - 1)
    }

    private fun delayMainStart(adapter: ListAdapter, rv: RecyclerView) {
        Handler().postDelayed({
            this.runOnUiThread {
                if (!gameOverToken) {
                    createScene()
                    //Let's change background's color from blue to red.
//                    val color = arrayOf(ColorDrawable(Color.WHITE), ColorDrawable(Color.RED))
//                    val trans = TransitionDrawable(color)
//                    //This will work also on old devices. The latest API says you have to use setBackground instead.
//                    button_one.setBackgroundDrawable(trans)
//                    trans.startTransition(5000)
                    delayMainStart(adapter, rv)
                }
            }
        }, 2000)
    }
}
