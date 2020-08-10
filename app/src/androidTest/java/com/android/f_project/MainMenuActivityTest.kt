package com.android.f_project

import android.util.Log
//import com.google.common.truth.Truth.assertThat
import org.junit.Test

class MainMenuActivityTest {

    @Test
    fun generateAccId() {
        val genID = generateAccountID()
        Log.i("Test", "GenerateId: $genID")
//        assertThat(genID.length).isEqualTo(8)
    }
}