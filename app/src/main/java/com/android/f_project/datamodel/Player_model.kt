package com.android.f_project.datamodel

data class Player_model(val id: String?,
                        val firstName: String?,
                        val lastName: String?,
                        val team: String?,
                        val nationality:String?,
                        val number: Int?,
                        val position: String?,
                        val stats: List<String>,
                        val stats2: Map<String, Int>?)