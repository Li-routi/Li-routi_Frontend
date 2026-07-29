package com.li_routi.feature.login

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.feature.login.navigation.LoginRoute

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LiroutiFrontendTheme {
                LoginRoute(modifier = Modifier.fillMaxSize())
            }
        }
    }
}


