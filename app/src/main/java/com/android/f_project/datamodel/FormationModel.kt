package com.android.f_project.datamodel

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FormationModel(
    val id: String?,
    val name: String?,
    val distribution: String?
) : Parcelable