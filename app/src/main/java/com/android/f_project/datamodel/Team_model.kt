package com.android.f_project.datamodel

data class Team_model(val id: String?,
                      val name: String?,
                      val country: String?,
                      val league: String?,
                      val logo_res:Int?,
                      val players: List<Player_model?>)