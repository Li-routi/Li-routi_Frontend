package com.li_routi.feature.grouproutine

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.feature.grouproutine.screen.GroupRoutineRoute

class GroupRoutineActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LiroutiFrontendTheme {
                GroupRoutineRoute()
            }
        }
    }
}
