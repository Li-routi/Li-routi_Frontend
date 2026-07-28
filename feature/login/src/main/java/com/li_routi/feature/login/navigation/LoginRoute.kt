package com.li_routi.feature.login.navigation

import android.content.Intent
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.li_routi.feature.login.auth.findActivity
import com.li_routi.feature.login.screen.LoginScreen
import com.li_routi.feature.login.vm.LoginUiEvent
import com.li_routi.feature.login.vm.LoginViewModel

/** :app의 MainActivity 정규화된 클래스명. `feature:login`은 `:app`을 컴파일 타임에 참조할 수 없어(:app -> feature:login 방향으로만 의존) [GroupRoutineActivity]와 동일하게 문자열 + [Intent.setClassName]로 넘긴다. */
private const val MainActivityClassName = "com.cmc.li_routi_frontend.MainActivity"

/**
 * 로그인 화면 진입점. [LoginViewModel]과 [LoginScreen]을 연결한다.
 *
 * 로그인 성공 시 앱의 실제 진입점인 MainActivity로 되돌아간다. 온보딩 여부
 * ([com.li_routi.core.domain.auth.AuthToken.onboardingCompleted])에 따른 분기는 `feature:onboarding`이
 * 아직 실제 화면을 갖추기 전까지는 범위 밖 — 일단 둘 다 메인으로 보낸다.
 */
@Composable
fun LoginRoute(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = viewModel { LoginViewModel() },
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(viewModel) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is LoginUiEvent.LoginSucceeded -> {
                    Toast.makeText(context, "로그인 성공", Toast.LENGTH_SHORT).show()
                    context.startActivity(Intent().setClassName(context.packageName, MainActivityClassName))
                    context.findActivity()?.finish()
                }
                is LoginUiEvent.ShowError ->
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    LoginScreen(
        isLoading = uiState.isLoading,
        onKakaoClick = { viewModel.onKakaoLoginClick(context) },
        onGoogleClick = {
            context.findActivity()?.let(viewModel::onGoogleLoginClick)
        },
        modifier = modifier,
    )
}
