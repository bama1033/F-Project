package com.android.f_project.datamodel

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Player_model(
    val id: String?,
    val name: String?,
    val age: String?,
    val team: String?,
    val nationality: String?,
    val jerseyNumber: String?,
    val position: String?,
    val overall: String?
) :Parcelable