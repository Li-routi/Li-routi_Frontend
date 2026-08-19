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
import com.li_routi.core.data.home.HomeContentPrefetcher
import com.li_routi.feature.login.auth.findActivity
import com.li_routi.feature.login.screen.LoadingScreen
import com.li_routi.feature.login.screen.LoginScreen
import com.li_routi.feature.login.screen.ProfileDefaultNickname
import com.li_routi.feature.login.screen.ProfileScreen
import com.li_routi.feature.login.vm.LoginUiEvent
import com.li_routi.feature.login.vm.LoginViewModel


private const val MainActivityClassName = "com.cmc.li_routi_frontend.MainActivity"

private enum class LoginRouteScreen { Login, Profile, Loading }


@Composable
fun LoginRoute(
    modifier: Modifier = Modifier,
    startAtProfileSetup: Boolean = false,
    /** [com.cmc.li_routi_frontend.MainActivity]가 이미 조회해 둔 닉네임(재로그인 경로). */
    initialNickname: String? = null,
    viewModel: LoginViewModel = viewModel { LoginViewModel() },
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var currentScreen by remember {
        mutableStateOf(if (startAtProfileSetup) LoginRouteScreen.Profile else LoginRouteScreen.Login)
    }

    fun goToMainActivity() {
        context.startActivity(Intent().setClassName(context.packageName, MainActivityClassName))
        context.findActivity()?.finish()
    }

    LaunchedEffect(viewModel) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is LoginUiEvent.LoginSucceeded -> {
                    Toast.makeText(context, "로그인 성공", Toast.LENGTH_SHORT).show()
                    currentScreen = if (event.token.onboardingCompleted) {
                        LoginRouteScreen.Loading
                    } else {
                        LoginRouteScreen.Profile
                    }
                }
                LoginUiEvent.ProfileSaveSucceeded -> currentScreen = LoginRouteScreen.Loading
                is LoginUiEvent.ShowError ->
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    when (currentScreen) {
        LoginRouteScreen.Login -> {
            LoginScreen(
                isLoading = uiState.isLoading,
                onKakaoClick = { viewModel.onKakaoLoginClick(context) },
                onGoogleClick = {
                    context.findActivity()?.let(viewModel::onGoogleLoginClick)
                },
                modifier = modifier,
            )
        }
        LoginRouteScreen.Profile -> {
            ProfileScreen(
                modifier = modifier,
                // 재로그인 경로(MainActivity가 이미 조회한 값)를 우선하고, 신규 가입 경로에서는
                // socialLogin() 성공 시 ViewModel이 비동기로 채워 넣은 값을 쓴다.
                initialNickname = initialNickname ?: uiState.nickname ?: ProfileDefaultNickname,
                isLoading = uiState.isLoading,
                onSaveClick = { nickname, profileImageUri ->
                    viewModel.onProfileSaveClick(context, nickname, profileImageUri)
                },
            )
        }
        LoginRouteScreen.Loading -> {
            // 이 화면이 떠 있는 동안 홈 데이터/이미지를 미리 받아둔다 — 완료(또는 타임아웃)되기
            // 전까지는 넘어가지 않아서, 홈 화면이 뜨자마자 깜빡임 없이 완성된 상태로 보인다.
            LaunchedEffect(Unit) {
                HomeContentPrefetcher.prefetchWithMinDuration()
                goToMainActivity()
            }
            LoadingScreen(modifier = modifier)
        }
    }
}
