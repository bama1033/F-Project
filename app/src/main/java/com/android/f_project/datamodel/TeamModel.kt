package com.android.f_project.datamodel

import android.os.Parcelable
import com.android.f_project.util.GlobalVariable
import com.android.f_project.util.getRandomInt
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TeamModel(
    val id: String?,
    val name: String?,
    val country: String?,
    val league: String?,
    val logo_res: Int?,
    var players: MutableList<PlayerModel>
) : Parcelable {

    fun getAttackingPlayer(): PlayerModel {
        val contains =
            this.players.filter { it.position in GlobalVariable.listAttackingPositions }
        return contains[getRandomInt(contains.size.toDouble())]
    }

    fun getAttackingPlayerText(): String? {
        val contains =
            this.players.filter { it.position in GlobalVariable.listAttackingPositions }
        return contains[getRandomInt(contains.size.toDouble())].name
    }

    fun getMidfieldText(team: TeamModel): String? {
        val contains =
            this.players.filter { it.position in GlobalVariable.listMidfieldingPositions }
        return contains[getRandomInt(contains.size.toDouble())].name
    }

    fun getDefendingPlayer(): PlayerModel {
        val contains =
            this.players.filter { it.position in GlobalVariable.listDefendingPositions }
        return contains[getRandomInt(contains.size.toDouble())]
    }

    fun getGKPlayer(): PlayerModel {
        val contains =
            this.players.filter { it.position in GlobalVariable.listGKPositions }
        return contains[getRandomInt(contains.size.toDouble())]
    }

    fun getDefendingPlayerText(team: TeamModel): String? {
        val contains =
            this.players.filter { it.position in GlobalVariable.listDefendingPositions }
        return contains[getRandomInt(contains.size.toDouble())].name
    }

    fun getGKPlayerText(team: TeamModel): String? {
        val contains =
            this.players.filter { it.position in GlobalVariable.listGKPositions }
        return contains[getRandomInt(contains.size.toDouble())].name
    }
}