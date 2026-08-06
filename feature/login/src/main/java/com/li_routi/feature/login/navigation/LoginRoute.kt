package com.li_routi.feature.login.navigation

import android.content.Intent
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.li_routi.feature.login.auth.findActivity
import com.li_routi.feature.login.screen.LoginScreen
import com.li_routi.feature.login.screen.ProfileScreen
import com.li_routi.feature.login.vm.LoginUiEvent
import com.li_routi.feature.login.vm.LoginViewModel


private const val MainActivityClassName = "com.cmc.li_routi_frontend.MainActivity"


@Composable
fun LoginRoute(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = viewModel { LoginViewModel() },
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var showProfileScreen by remember { mutableStateOf(false) }

    fun goToMainActivity() {
        context.startActivity(Intent().setClassName(context.packageName, MainActivityClassName))
        context.findActivity()?.finish()
    }

    LaunchedEffect(viewModel) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is LoginUiEvent.LoginSucceeded -> {
                    Toast.makeText(context, "로그인 성공", Toast.LENGTH_SHORT).show()
                    if (event.token.onboardingCompleted) {
                        goToMainActivity()
                    } else {
                        showProfileScreen = true
                    }
                }
                is LoginUiEvent.ShowError ->
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    if (showProfileScreen) {
        ProfileScreen(
            modifier = modifier,
            onSaveClick = { _, _ ->
                // 저장 API 없음: 백엔드 프로필 저장 로직은 아직 없고, 홈 화면으로 이동만 수행한다.
                goToMainActivity()
            },
        )
    } else {
        LoginScreen(
            isLoading = uiState.isLoading,
            onKakaoClick = { viewModel.onKakaoLoginClick(context) },
            onGoogleClick = {
                context.findActivity()?.let(viewModel::onGoogleLoginClick)
            },
            modifier = modifier,
        )
    }
}
