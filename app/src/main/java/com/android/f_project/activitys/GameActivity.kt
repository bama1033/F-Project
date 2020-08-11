package com.android.f_project.activitys

import android.content.Context
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
import com.android.f_project.*
import com.android.f_project.datamodel.PlayerModel
import com.android.f_project.datamodel.SceneModel
import com.android.f_project.datamodel.StatusModel
import com.android.f_project.datamodel.TeamModel
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_gamesimulation.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class GameActivity : AppCompatActivity() {
    private var scoreHome = 0
    private var scoreAway = 0
    private var gameTimer = 0
    private val contentList = ArrayList<SceneModel>()
    private var timeToken = 0
    private var gameOverToken = false
    private var halfTimeToken = false
    private lateinit var selectedTeamHome: TeamModel
    private lateinit var selectedTeamAway: TeamModel
    private var highscore = 0
    private var gameStatus: StatusModel = StatusModel.Midfield
    private lateinit var adapter: ListAdapter
    private lateinit var rv: RecyclerView
    private lateinit var mDocRef: DocumentReference
    private var attackingPlayer: PlayerModel = getDefaultPlayer()

/*
        TODO MakeTeams interact (GK Def MFs,Atk und suche einen random pro Scene aus)
        TODO -Implementing Strategy
                als strategie passen oder dribbeln, dann entscheidet sich mit was gerollt wird
                anzahl der spieler umso mehr spieler umso besser verteidigung

        TODO Progressbar of current gamestatus, like progressbar

        TODO Bugfix:Invalid document reference. Document references must have an even number of segments, but mylist has 1
        TODO Bugfix: SelectLineup hwne numbernameswitch adjust layout
        TODO Intro screen T1 vs T2 Stars being calculated acc to players selected, also best player presented by overall
        TODO Sidedrawer bei lineup und taktik view

        TODO -Animationen/Screens für actions
        TODO -Gamemodes (Create a Team_model, Online)

        Done:
        Add Settings generate UserID
        -Firebase-Anbindung um Highscore zu speichern ✔ --> make it usefull compare data and write userBYid? oder google acc?
        ADD STATUS-Model Anst0ss ✔
        -Implementing Spielverlauf ✔
        -Implementing 0..90 ✔
        States(wo sind wir im Feld) ✔
        -Implementing BaseLogic ✔
        -Implementing PlayerStats ✔
        -Teamselector UI ✔
        -Mainmenu(welcomescreen, modeselect, highscore, exit) ✔
        -adding Teams  ---> API? --> SQL ✔
        -adding Players ---> API? --> SQL ✔
        Add Swipe functionality  ✔
        NEXT Create Auftsellung Sturm mittelfeld Def ( MF vs MF, Attacker vs Def/GK) ✔
        expand playerstats by skillmoves,defending,passing,gk,strike ✔
        playercards ✔
 */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gamesimulation)

        selectedTeamHome = intent.getParcelableExtra("selectedTeam")
        selectedTeamAway = intent.getParcelableExtra("selectedTeam2")
        highscore = intent.getIntExtra("highscore", 0)

        setTeams(selectedTeamHome, selectedTeamAway)

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
        recreate()
    }

    private fun setTeams(team_one: TeamModel, team_two: TeamModel) {
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
        contentList.add(SceneModel(gameTimer.toString(), special, gameStatus))
        updateTime()
        updateAdapter()
    }

    private fun addScene(special: String = "special") {
        animateClock()
        var content = ""
        val dice = shuffle()
        when (gameStatus) {
            StatusModel.Midfield -> {
                content = getString(getPassingText())
                when (dice) {
                    in 1..5 -> {
                        gameStatus = StatusModel.Defense
                    }
                    in 6..9 -> {
                        gameStatus = StatusModel.Attack
                    }
                    10 -> {
                        content = getString(R.string.scene_circulating)
                        gameStatus = StatusModel.Midfield
                    }
                }
            }
            StatusModel.Attack -> {
                val contentText = getAttackingText()
                attackingPlayer = selectedTeamHome.getAttackingPlayer()
                val defendingPlayer = selectedTeamAway.getDefendingPlayer()
                if (doesCalculation(
                        attackingPlayer.shooting,
                        attackingPlayer,
                        defendingPlayer.defending,
                        defendingPlayer
                    )
                ) {
                    content =
                        getString(
                            contentText,
                            attackingPlayer.name,
                            defendingPlayer.name
                        )
                    gameStatus = StatusModel.Shot
                } else {
                    //TODO add scene defending success
                    content = content + " " + getString(R.string.scene_defender_block1)
                    gameStatus = StatusModel.Midfield
                }
            }
            StatusModel.Shot -> {
                content = getString(getShootingText())
                val goalkeepingPlayer = selectedTeamAway.getGKPlayer()
                if (doesCalculation(
                        attackingPlayer.shooting,
                        attackingPlayer,
                        goalkeepingPlayer.goalkeeping,
                        goalkeepingPlayer
                    )
                ) {
                    content =
                        "$content ${attackingPlayer.name} mit dem " + getString(R.string.scene_goal_success1)
                    updateScore("home")
                    gameStatus = StatusModel.KickOff
                } else {
                    content = content + " " + getString(R.string.scene_goal_fail1)
                    gameStatus = StatusModel.Midfield
                }
            }
            StatusModel.Defense -> {
                content = getString(R.string.scene_defending1)
                when (shuffle()) {
                    in 1..5 -> {
                        content = content + " " + getString(R.string.scene_opponent_goal1)
                        updateScore("away")
                        gameStatus = StatusModel.KickOff
                    }
                    in 6..10 -> {
                        content = content + " " + getString(getDefendingText())
                        gameStatus = StatusModel.Midfield
                    }
                }
            }
            StatusModel.KickOff -> {
                content =
                    getString(
                        R.string.scene_kickoff,
                        scoreHome.toString(),
                        scoreAway.toString()
                    )
                gameStatus = StatusModel.Midfield
            }
        }
        contentList.add(SceneModel(gameTimer.toString(), content, gameStatus))
        updateTime()
        updateAdapter()
    }


    private fun getShootingText(): Int {
        val attackingTextList: MutableList<Int> = ArrayList()
        attackingTextList.add(R.string.scene_shot1)
        attackingTextList.add(R.string.scene_shot2)
        attackingTextList.add(R.string.scene_shot3)
        return attackingTextList.random()
    }

    private fun getAttackingText(): Int {
        val attackingTextList: MutableList<Int> = ArrayList()
        attackingTextList.add(R.string.scene_attacking1)
        attackingTextList.add(R.string.scene_attacking2)
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
                selectedTeamHome.name,
                selectedTeamAway.name
            )
        )
    }

    private fun endGame() {
        addSceneSpecial(getString(R.string.scene_ending))
        interaction_one.visibility = View.VISIBLE
        interaction_two.visibility = View.VISIBLE
        updateAdapter()
        saveStats()
    }

    private fun saveStats() {
        if (highscore < scoreHome) {
            val dataSave = HashMap<String, String>()
            val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
            val currentDate = sdf.format(Date())

            val sharedPref = this.getSharedPreferences("AccountId", Context.MODE_PRIVATE) ?: return
            val highScore = sharedPref.getString("AccountId", "ERROR")
            dataSave["User"] = highScore.toString()
            dataSave["Date"] = currentDate.toString()
            dataSave["HomeTeamCounter"] = counter_home.text.toString()
            dataSave["HomeTeam"] = selectedTeamHome.name.toString()
            dataSave["AwayTeamCounter"] = counter_away.text.toString()
            dataSave["AwayTeam"] = selectedTeamAway.name.toString()
            if (highScore == "ERROR") {
                Log.d("FirebaseManager", "Problem mit der AccountId")
            } else {
                mDocRef = FirebaseFirestore.getInstance().document("Score/$highScore")
                mDocRef.set(dataSave as Map<String, Any>).addOnSuccessListener {
                    Log.d("FirebaseManager", "Upload Successful")
                }.addOnFailureListener {
                    Log.d("FirebaseManager", "Upload hat nicht geklappt")
                }
            }
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
