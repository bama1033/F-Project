package com.android.f_project

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