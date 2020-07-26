package com.android.f_project.activitys

import android.content.Intent
import android.graphics.drawable.Animatable
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
import com.android.f_project.R
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
import kotlin.math.floor


class GameActivity : AppCompatActivity() {
    private var scoreHome = 0
    private var scoreAway = 0
    private var gameTimer = 0
    private val contentList = ArrayList<Scene_model>()
    private var timeToken = 0
    private var gameOverToken = false
    private var halfTimeToken = false
    private lateinit var selectedTeam: Team_model
    private lateinit var selectedTeam2: Team_model
    private var gameStatus: Status_model = Status_model.Midfield
    private lateinit var adapter: ListAdapter
    private lateinit var rv: RecyclerView
    private lateinit var mDocRef: DocumentReference
    private val listAttackingPositions =
        mutableListOf("ST", "LAM", "RAM", "CAM", "RM", "LM", "LF", "RF", "CF")
    private val listMidfieldingPositions =
        mutableListOf("CM", "LCM", "RCM", "RCM", "CDM", "RDM", "LDM")
    private val listDefendingPositions =
        mutableListOf("RB", "LB", "CB", "RCB", "LCB", "RWB", "LWB")
    private val listGKPositions =
        mutableListOf("GK")

/*
        TODO -Implementing Spielverlauf ✔
        TODO -Implementing 0..90 ✔
        TODO States(wo sind wir im Feld) ✔
        TODO MakeTeams interact (GK Def MFs,Atk und suche einen random pro Scene aus)
        TODO -Implementing BaseLogic ✔
        TODO -Implementing PlayerStats
        TODO -Implementing Strategy
        TODO -Implementing Tactics
        TODO -Implementing Moral

        TODO -Teamselector UI ✔
        TODO -Mainmenu(welcomescreen, modeselect, highscore, exit) ✔

        TODO -adding Teams  ---> API? --> SQL ✔
        TODO -adding Players ---> API? --> SQL ✔
        TODO -Animationen/Screens für actions
        TODO -Firebase-Anbindung um Highscore zu speichern ✔
        TODO -Gamemodes (Create a Team_model, Online)

        Auswechslung -->immer
        Taktik-->immer? bei Standardsituationen wenn condition(condition wäre rückstand?)

        TODO-NEXT Create Auftsellung Sturm mittelfeld Def ( MF vs MF, Attacker vs Def/GK) ✔
        TODO expand playerstats by skillmoves,defending,passing,gk,strike
        TODO playercards
        als strategie passen oder dribbeln, dann entscheidet sich mit was gerollt wird
        +anzahl der spieler umso mehr spieler umso besser verteidigung
 */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gamesimulation)

        selectedTeam = intent.getParcelableExtra<Team_model>("selectedTeam")
        selectedTeam2 = intent.getParcelableExtra<Team_model>("selectedTeam2")
        setTeams(
            selectedTeam,
            selectedTeam2
        )

        rv = findViewById(R.id.game_course)

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
        finish()
        this.startActivity(Intent(this, MainMenuActivity::class.java))
    }

    private fun interactionTwo() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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
//            addSceneSpecial
//            addHalfTime();
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
                return true
            }
        }
    }

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
                content = getString(getPassingText())
                when (dice) {
                    in 1..5 -> {
                        gameStatus = Status_model.Defense
                    }
                    in 6..9 -> {
                        gameStatus = Status_model.Attack
                    }
                    10 -> {
                        gameStatus = Status_model.Midfield
                        //Ball zirtkuliert in der mitte
                    }
                }
            }
            Status_model.Attack -> {
                content = getString(getAttackingText())
                when (shuffle()) {
                    in 1..5 -> {

                        val playaname = getAttackingPlayerText()
                        content =
                            "$content $playaname mit dem " + getString(R.string.scene_goal_success1)
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
                        content = content + " " + getString(getDefendingText())
                    }
                }
                gameStatus = Status_model.Midfield
            }
        }
        contentList.add(Scene_model(gameTimer.toString(), content, gameStatus))
        updateTime()
        updateAdapter()
    }

    private fun getAttackingPlayerText(): String? {
        val noOfPlayersSelected2 = selectedTeam.players

        val contains = noOfPlayersSelected2.filter { it.position in listAttackingPositions }
        val noOfPlayersSelected = contains.size.toDouble()
        return contains[getRandomInt(noOfPlayersSelected)].name
    }

    private fun getRandomInt(numba: Double): Int {
        var randomNumba = floor(Math.random() * floor(numba)).toInt()
        if (randomNumba == 0) {
            randomNumba = 1
        }
        return randomNumba
    }

    private fun getAttackingText(): Int {
        val attackingTextList: MutableList<Int> = ArrayList()
        attackingTextList.add(R.string.scene_shot1)
        attackingTextList.add(R.string.scene_shot2)
        attackingTextList.add(R.string.scene_shot3)
        return attackingTextList.random()
    }

    private fun getPassingText(): Int {
        val passingTextList: MutableList<Int> = ArrayList()
        passingTextList.add(R.string.scene_pass1)
        passingTextList.add(R.string.scene_pass2)
        passingTextList.add(R.string.scene_pass3)
        return passingTextList.random()
    }

    private fun getDefendingText(): Int {
        val defendingTextList: MutableList<Int> = ArrayList()
        defendingTextList.add(R.string.scene_gk_block1)
        defendingTextList.add(R.string.scene_defender_block1)
        return defendingTextList.random()
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
                selectedTeam.name,
                selectedTeam2.name
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
            Log.d("FirebaseManager", "Upload hat nicht geklappt")
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
