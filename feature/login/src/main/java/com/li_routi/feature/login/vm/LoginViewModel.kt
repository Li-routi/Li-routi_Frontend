package com.li_routi.feature.login.vm

import android.app.Activity
import android.content.Context
import androidx.lifecycle.viewModelScope
import com.li_routi.core.common.android.architecture.BaseViewModel
import com.li_routi.core.common.kotlin.util.ResultState
import com.li_routi.core.data.di.AuthContainer
import com.li_routi.core.domain.auth.SocialProvider
import com.li_routi.feature.login.BuildConfig
import com.li_routi.feature.login.auth.GoogleAuthHelper
import com.li_routi.feature.login.auth.KakaoAuthHelper
import com.li_routi.feature.login.navigation.LoginScreenActions
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 로그인 화면 ViewModel.
 *
 * SDK(카카오톡/구글 Credential Manager)로 provider 토큰을 획득한 뒤,
 * `/api/auth/social-login`에 검증을 요청해 서비스 토큰을 발급받는다. 발급된 토큰은
 * [AuthContainer]의 리포지토리 계층에서 저장까지 책임진다(이 ViewModel은 저장소를 모른다).
 *
 * 온보딩/홈 라우팅 등 로그인 성공 이후의 화면 전환은 이번 범위 밖 — [LoginUiEvent.LoginSucceeded]를
 * 구독하는 쪽(다른 담당자)이 연결한다.
 */
class LoginViewModel(
    private val kakaoAuthHelper: KakaoAuthHelper = KakaoAuthHelper(),
    private val googleAuthHelper: GoogleAuthHelper = GoogleAuthHelper(BuildConfig.GOOGLE_WEB_CLIENT_ID),
) : BaseViewModel(), LoginScreenActions {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<LoginUiEvent>(extraBufferCapacity = 1)
    val uiEvent: SharedFlow<LoginUiEvent> = _uiEvent.asSharedFlow()

    override fun onKakaoLoginClick(context: Context) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            runCatching { kakaoAuthHelper.login(context) }
                .onSuccess { accessToken -> socialLogin(SocialProvider.KAKAO, accessToken, nonce = null) }
                .onFailure { emitEvent(LoginUiEvent.ShowError(it.message ?: "카카오 로그인에 실패했습니다.")) }
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    override fun onGoogleLoginClick(activity: Activity) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            when (val nonceResult = AuthContainer.issueGoogleNonceUseCase()) {
                is ResultState.Success -> {
                    val nonce = nonceResult.data
                    runCatching { googleAuthHelper.getGoogleIdToken(activity, nonce) }
                        .onSuccess { idToken -> socialLogin(SocialProvider.GOOGLE, idToken, nonce) }
                        .onFailure { emitEvent(LoginUiEvent.ShowError(it.message ?: "구글 로그인에 실패했습니다.")) }
                }
                is ResultState.Error -> emitEvent(LoginUiEvent.ShowError(nonceResult.message))
                ResultState.Loading -> Unit
            }
            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    private suspend fun socialLogin(provider: SocialProvider, providerToken: String, nonce: String?) {
        when (val result = AuthContainer.socialLoginUseCase(provider, providerToken, nonce)) {
            is ResultState.Success -> emitEvent(LoginUiEvent.LoginSucceeded(result.data))
            is ResultState.Error -> emitEvent(LoginUiEvent.ShowError(result.message))
            ResultState.Loading -> Unit
        }
    }

    private fun emitEvent(event: LoginUiEvent) {
        viewModelScope.launch {
            _uiEvent.emit(event)
        }
    }
}
