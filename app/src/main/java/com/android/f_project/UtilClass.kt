package com.android.f_project

import android.util.Log
import com.android.f_project.datamodel.PlayerModel
import kotlin.math.floor

fun shuffle(): Int {
    return (1..10).shuffled().first()
}

object GlobalVariable {
    val listAttackingPositions =
        mutableListOf("ST", "LAM", "RAM", "CAM", "RM", "LM", "LF", "RF", "CF")
    val listMidfieldingPositions =
        mutableListOf("CM", "LCM", "RCM", "RCM", "CDM", "RDM", "LDM")
    val listDefendingPositions =
        mutableListOf("RB", "LB", "CB", "RCB", "LCB", "RWB", "LWB")
    val listGKPositions =
        mutableListOf("GK")
}

fun getRandomInt(numba: Double): Int {
    var randomNumba = floor(Math.random() * floor(numba)).toInt()
    if (randomNumba == 0) {
        randomNumba = 1
    }
    return randomNumba
}

fun getStatRandomizer(): Int {
    var randomNumba = floor(Math.random() * floor(15.0)).toInt()
    if (randomNumba == 0) {
        randomNumba = 1
    }
    return randomNumba
}

fun statsBuilder(initStats: Double?, overallStats: Double?): Double {
    // how about roll twice and add flat to calc and then show user what was rolled/
    val x = initStats?.let { overallStats?.plus(it) }!!.div(2).plus(getStatRandomizer())

    Log.i("CalcResults", "Init" + x)

    return x
}

fun getDefaultPlayer(): PlayerModel {
    return PlayerModel(
        "99",
        "Placeholder",
        "99",
        "Empty Team",
        "Zulu-Nations",
        "99",
        "99",
        "99",
        "99",
        "99",
        "99",
        "MS",
        "99"
    )
}

fun doesCalculation(
    statsOne: String?,
    playerOne: PlayerModel,
    statsTwo: String?,
    playerTwo: PlayerModel
): Boolean {
    return statsBuilder(statsOne?.toDouble(), playerOne.overall?.toDouble()) >= statsBuilder(
        statsTwo?.toDouble()!!, playerTwo.overall?.toDouble()
    )
}