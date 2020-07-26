package com.android.f_project.datamodel

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Formation_model(
    val id: String?,
    val name: String?,
    val distribution: String?
) : Parcelable