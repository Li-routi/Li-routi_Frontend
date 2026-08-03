package com.li_routi.feature.grouproutine

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.li_routi.core.common.ui.nav.AppBottomTab
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.feature.grouproutine.screen.GroupRoutineRoute

private const val MainActivityClassName = "com.cmc.li_routi_frontend.MainActivity"

class GroupRoutineActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.WHITE, Color.WHITE),
        )
        setContent {
            LiroutiFrontendTheme {
                GroupRoutineRoute(
                    onTabSelected = { tab ->
                        if (tab != AppBottomTab.GroupRoutine) {
                            startActivity(Intent().setClassName(packageName, MainActivityClassName))
                            finish()
                        }
                    },
                )
            }
        }
    }
}
