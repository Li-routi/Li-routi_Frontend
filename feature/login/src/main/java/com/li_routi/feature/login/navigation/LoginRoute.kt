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

/** :app의 MainActivity 정규화된 클래스명. `feature:login`은 `:app`을 컴파일 타임에 참조할 수 없어(:app -> feature:login 방향으로만 의존) [GroupRoutineActivity]와 동일하게 문자열 + [Intent.setClassName]로 넘긴다. */
private const val MainActivityClassName = "com.cmc.li_routi_frontend.MainActivity"

/**
 * 로그인 화면 진입점. [LoginViewModel]과 [LoginScreen]을 연결한다.
 *
 * 로그인 성공 시 `AuthToken.onboardingCompleted`로 첫 로그인 여부를 분기한다.
 * - 첫 로그인(회원가입, `onboardingCompleted == false`): 이 화면 안에서 [ProfileScreen]으로 전환하고,
 *   저장을 눌러야 비로소 MainActivity로 넘어간다.
 * - 재로그인(`onboardingCompleted == true`): 기존과 동일하게 바로 MainActivity로 되돌아간다.
 */
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
            onSaveClick = {
                // 저장버튼기능없음: 프로필 저장 로직은 아직 없고, 홈 화면으로 이동만 수행한다.
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
