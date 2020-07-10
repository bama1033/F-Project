package com.android.f_project.activitys

import android.graphics.drawable.Animatable
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.f_project.ListAdapter
import com.android.f_project.MyDbHelper
import com.android.f_project.R
import com.android.f_project.datamodel.Player_model
import com.android.f_project.datamodel.Scene_model
import com.android.f_project.datamodel.Status_model
import com.android.f_project.datamodel.Team_model
import com.android.f_project.shuffle
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_gamesimulation.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


class GameActivity : AppCompatActivity() {
    private var scoreHome = 0
    private var scoreAway = 0
    private var gameTimer = 0
    private val contentList = ArrayList<Scene_model>()
    private var timeToken = 0
    private var gameOverToken = false
    private var halfTimeToken = false
    private var gameStatus: Status_model = Status_model.Midfield
    private lateinit var adapter: ListAdapter
    private lateinit var rv: RecyclerView
    private lateinit var mDocRef: DocumentReference

/*
        TODO -Implementing Spielverlauf ✔
        TODO -Implementing 0..90 ✔
        TODO States(wo sind wir im Feld) ✔
        TODO MakeTeams interact (GK Def MFs,Atk und suche einen random pro Scene aus)
        TODO -Implementing BaseLogic ✔
        TODO -Implementing Logic(PlayerStats, Strategy, Tactics, Moral)
        TODO -Buttons mit Actions versehen

        TODO -Teamselector UI ✔
        TODO -Mainmenu(welcomescreen, modeselect, highscore, exit) ✔

        TODO -adding Teams  ---> API? --> SQL ✔
        TODO -adding Players ---> API? --> SQL ✔
        TODO -Animationen für actions
        TODO -Firebase-Anbindung um Highscore zu speichern ✔
        TODO -Gamemodes (Create a Team_model, Online)

        pass shoot midfield attack defense
        event,action,followup --> scene each of this?=listitem?
        strategie,auswechslung,taktik(verteidiger nach vorne)
        Strategie-->HalbZeit und bei verlangerung
        Auswechslung -->immer
        Taktik-->immer? bei Standardsituationen wenn condition(condition wäre rückstand?)

        TODO-NEXT Create Auftsellung Sturm mittelfeld Def ( MF vs MF, Attacker vs Def/GK)
 */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gamesimulation)

        val selectedTeam = intent.getParcelableExtra<Team_model>("selectedTeam")
        val selectedTeam2 = intent.getParcelableExtra<Team_model>("selectedTeam2")
        setTeams(
            selectedTeam,
            selectedTeam2
        )
        rv = findViewById(R.id.game_course)

//        val bundle = intent.getBundleExtra("selected_person")
//        var team  = bundle.getParcelable("selectedTeam") as Team_model

//        selectedTeam= intent.getParcelableExtra("selectedTeam") as Team_model

        adapter = ListAdapter(contentList)

        interaction_one.setOnClickListener {
            //TODO init auf disabled setzen
            interactionOne()
        }
        interaction_two.setOnClickListener {
            interactionTwo()
        }
        rv.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        rv.adapter = adapter


        sqlconnections()
        startGame()
        animateClock()
        delayMainStart(adapter, rv)

    }

    private fun animateClock() {
        for (drawable in anima.compoundDrawables) {
            if (drawable is Animatable) {
                (drawable as Animatable).stop()
                (drawable as Animatable).start()
            }
            //http://blog.sqisland.com/2014/10/first-look-at-animated-vector-drawable.html
        }
    }

    private fun interactionOne() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun interactionTwo() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


    private fun sqlconnections() {
        val myDatabase = MyDbHelper(this).readableDatabase

        var playaList = mutableListOf<Player_model>()
        AsyncTask.execute {
            val cursor = myDatabase.rawQuery(
                "SELECT _id,Name,Age,Nationality,\"Jersey Number\",Position,Overall FROM data\n" +
                        "WHERE Club=?;", arrayOf("Borussia Dortmund")
            )
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast) {
                    //your code to implement
                    val id = cursor.getString(0)
                    val name = cursor.getString(1)
                    val age = cursor.getString(2)
                    val nationality = cursor.getString(3)
                    val overall = cursor.getString(4)
                    val position = cursor.getString(5)
                    val number = cursor.getString(6)
                    val player =
                        Player_model(
                            id,
                            name,
                            age,
                            "Borussia Dortmund",
                            nationality,
                            overall,
                            position,
                            number
                        )
                    playaList.add(player)
                    cursor.moveToNext()
                }
            }
            cursor.close()

            val x = myDatabase.rawQuery("SELECT Age FROM data WHERE name = ?;", arrayOf("De Gea"))
//            val age=x.getColumnIndexOrThrow("Age")
            val actualage: String
//            if (x.moveToFirst())
//                actualage = x.getString(3)
            // Prevent a crash if there is no data for this name
//            else
//            val actualage=x.getString(3)
        }
    }

    private fun setTeams(team_one: Team_model, team_two: Team_model) {
        setViewText(team_home, team_one.name)
        setViewText(team_away, team_two.name)
        setImage(image_home, team_one.logo_res)
        setImage(image_away, team_two.logo_res)
    }

    private fun setImage(view: ImageView, text: Int?) {
        text?.let { view.setImageResource(it) }
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
        when (halfTimeToken) {
            //TODO add halftimeScene and activate halfTimeToken somehow
//            addHalfTime();
//                addScene()
        }
        when (endgame) {
            true -> {
                addScene()
            }
            false -> {
                endGame()
            }
        }
    }

    private fun createTime(): Boolean {
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
            in 37..46 -> {
                gameTimer = 45
                this.halfTimeToken = true
                return true
            }
            in 88..90 -> {
                gameTimer = 90
                this.gameOverToken = true
                return false

            }
            in 91..96 -> {
                gameTimer = 93
                this.gameOverToken = true
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
        animateClock()
        lateinit var content: String
        val dice = shuffle()
        when (gameStatus) {
            Status_model.Midfield -> {
                content = getString(R.string.scene_pass1)
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
                content = getString(getStringText())
                when (shuffle()) {
                    in 1..5 -> {
                        content = content + " " + getString(R.string.scene_goal_success1)
                        updateScore("home")
                    }
                    in 6..10 -> {
                        content = content + " " + getString(R.string.scene_goal_fail1)
                    }
                }
                gameStatus = Status_model.Midfield
            }
            Status_model.Defense -> {
                content = getString(R.string.scene_defending1)
                when (shuffle()) {
                    in 1..5 -> {
                        content = content + " " + getString(R.string.scene_opponent_goal1)
                        updateScore("away")
                    }
                    in 6..10 -> {
                        when (shuffle()) {
                            in 1..5 -> {
                                content = content + " " + getString(R.string.scene_gk_block1)
                            }
                            in 6..10 -> {
                                content = content + " " + getString(R.string.scene_defender_block1)
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

    private fun getStringText(): Int {
        val list4: MutableList<Int> = ArrayList()
        list4.add(R.string.scene_shot1)
        list4.add(R.string.scene_shot2)
        list4.add(R.string.scene_shot3)
        return list4.random()
//        return R.string.scene_shot + "1"
    }

    private fun updateTime() {
        if (gameTimer <= 90)
            game_clock.text = gameTimer.toString()
        else {
            val newValue = convertNinetyPlusTime()
            game_clock.text = "90+$newValue"
        }
    }

    private fun convertNinetyPlusTime(): Int {
        return gameTimer.minus(90)

    }

    private fun startGame() {
        game_clock.text = gameTimer.toString()
        addSceneSpecial(
            getString(
                R.string.scene_beginning,
                getString(R.string.team_borussia_dortmund),
                getString(R.string.team_bayern_muenchen)
            )
        )
    }

    private fun endGame() {
        addSceneSpecial(getString(R.string.scene_ending))
        interaction_one.visibility = View.VISIBLE
        interaction_two.visibility = View.VISIBLE
//        updateAdapter()
        saveStats()
    }

    private fun saveStats() {
        val dataSave = HashMap<String, String>()
        val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
        val currentDate = sdf.format(Date())

        dataSave["User"] = "Me"
        dataSave["Date"] = currentDate.toString()
        dataSave["Dortmund"] = counter_home.text.toString()
        dataSave["Bayern"] = counter_away.text.toString()
        mDocRef = FirebaseFirestore.getInstance().document("Score/HighScores")
        mDocRef.set(dataSave as Map<String, Any>).addOnSuccessListener {
            Log.d("FirebaseManager", "Upload Successful")
        }.addOnFailureListener {
            Log.d("FirebaseManager", "Klappt net")
        }
    }

    private fun setViewText(view: TextView, text: String?) {
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
//                    interaction_one.setBackgroundDrawable(trans)
//                    trans.startTransition(5000)
                    delayMainStart(adapter, rv)
                }
            }
        }, 2000)
    }
}
