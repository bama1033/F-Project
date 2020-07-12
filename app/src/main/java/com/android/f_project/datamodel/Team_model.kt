package com.android.f_project.datamodel

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Team_model(
    val id: String?,
    val name: String?,
    val country: String?,
    val league: String?,
    val logo_res: Int?,
    var players: MutableList<Player_model>
) :Parcelable