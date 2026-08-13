package com.li_routi.feature.login.vm

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.li_routi.core.common.android.architecture.BaseViewModel
import com.li_routi.core.common.android.image.readProfileImageBytes
import com.li_routi.core.common.kotlin.util.ResultState
import com.li_routi.core.data.di.AuthContainer
import com.li_routi.core.designsystem.R
import com.li_routi.core.domain.auth.ProfileImageUpload
import com.li_routi.core.domain.auth.SocialProvider
import com.li_routi.feature.login.BuildConfig
import com.li_routi.feature.login.auth.GoogleAuthHelper
import com.li_routi.feature.login.auth.KakaoAuthHelper
import com.li_routi.feature.login.navigation.LoginScreenActions
import java.io.ByteArrayOutputStream
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 로그인 화면 ViewModel.
 *
 * SDK(카카오톡/구글 Credential Manager)로 provider 토큰을 획득한 뒤,
 * `/api/auth/social-login`에 검증을 요청해 서비스 토큰을 발급받는다. 발급된 토큰은
 * [AuthContainer]의 리포지토리 계층에서 저장까지 책임진다(이 ViewModel은 저장소를 모른다).
 *
 * 온보딩/홈 라우팅 등 로그인 성공 이후의 화면 전환은 [LoginUiEvent.LoginSucceeded]를 구독하는
 * [com.li_routi.feature.login.navigation.LoginRoute]가 담당한다.
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

    /**
     * 온보딩 첫 로그인 시 [com.li_routi.feature.login.screen.ProfileScreen]의 "저장" 클릭에서 호출된다.
     * 이미 저장 요청이 진행 중이면(연타 등) 새 요청을 막는다 — 동시에 여러 PATCH가 나가면
     * 늦게 도착한 응답이 최신 상태를 덮어쓸 수 있기 때문이다.
     */
    fun onProfileSaveClick(context: Context, nickname: String, profileImageUri: Uri?) {
        if (_uiState.value.isLoading) return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            // finally로 감싸 이미지 읽기 실패든 API 실패든 어떤 경로로 빠져나가도 로딩 상태가 풀리도록 한다.
            try {
                // profileImageUri가 null이면 "사진 선택 안 함"(정상 케이스)이라 imageResult도 null.
                // profileImageUri가 있는데 읽기에 실패하면 Result.failure로 남아 아래에서 구분해 처리한다.
                val imageResult = profileImageUri?.let { uri ->
                    runCatching {
                        withContext(Dispatchers.IO) {
                            ProfileImageUpload(
                                bytes = readProfileImageBytes(context, uri, PROFILE_IMAGE_MAX_DIMENSION, PROFILE_IMAGE_JPEG_QUALITY),
                                contentType = "image/jpeg",
                            )
                        }
                    }
                }
                if (imageResult != null && imageResult.isFailure) {
                    // 사용자가 사진을 골랐는데 읽기가 실패한 경우 — 이미지 없이 조용히 저장하면 안 되므로 여기서 중단한다.
                    emitEvent(LoginUiEvent.ShowError("프로필 이미지를 불러오지 못했습니다."))
                    return@launch
                }
                // 서버 API가 profileImageKey를 필수값으로 받는다(서버 쪽 디폴트 프로필 이미지 기능은 아직 없음).
                // 사용자가 사진을 고르지 않았으면 앱 로고를 임시 기본 이미지로 대신 올린다 —
                // 서버에 진짜 디폴트 이미지 처리가 추가되면 이 대체 로직은 제거한다.
                val image = imageResult?.getOrNull()
                    ?: withContext(Dispatchers.IO) { readDefaultProfileImageUpload(context) }
                when (val result = AuthContainer.updateProfileUseCase(nickname, image)) {
                    is ResultState.Success -> emitEvent(LoginUiEvent.ProfileSaveSucceeded)
                    is ResultState.Error -> emitEvent(LoginUiEvent.ShowError(result.message))
                    ResultState.Loading -> Unit
                }
            } finally {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    /**
     * 사용자가 사진을 고르지 않았을 때 대신 올리는 임시 기본 이미지(앱 로고).
     * TODO: 서버에 디폴트 프로필 이미지 기능이 추가되면 이 함수와 호출부를 제거한다.
     */
    private fun readDefaultProfileImageUpload(context: Context): ProfileImageUpload {
        val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.img_default_profile)
            ?: throw IOException("기본 프로필 이미지를 불러올 수 없습니다.")
        val bytes = ByteArrayOutputStream().use { output ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, PROFILE_IMAGE_JPEG_QUALITY, output)
            output.toByteArray()
        }
        bitmap.recycle()
        return ProfileImageUpload(bytes = bytes, contentType = "image/jpeg")
    }

    private suspend fun socialLogin(provider: SocialProvider, providerToken: String, nonce: String?) {
        when (val result = AuthContainer.socialLoginUseCase(provider, providerToken, nonce)) {
            is ResultState.Success -> {
                if (!result.data.onboardingCompleted) {
                    // 프로필 설정 화면에 서버가 아는 초기 닉네임(소셜 프로필 기반)을 미리 채워준다.
                    // 조회에 실패해도 화면 진입은 막지 않는다 — ProfileScreen이 기본 닉네임으로 대체한다.
                    loadNicknameForProfileSetup()
                }
                emitEvent(LoginUiEvent.LoginSucceeded(result.data))
            }
            is ResultState.Error -> emitEvent(LoginUiEvent.ShowError(result.message))
            ResultState.Loading -> Unit
        }
    }

    private suspend fun loadNicknameForProfileSetup() {
        val myInfo = AuthContainer.getMyInfoUseCase()
        if (myInfo is ResultState.Success) {
            _uiState.value = _uiState.value.copy(nickname = myInfo.data.nickname)
        }
    }

    private fun emitEvent(event: LoginUiEvent) {
        viewModelScope.launch {
            _uiEvent.emit(event)
        }
    }

    private companion object {
        /** 프로필 사진 업로드 시 리사이징 목표가 되는 긴 변의 최대 픽셀 수. 원본 화질까지는 필요 없다. */
        const val PROFILE_IMAGE_MAX_DIMENSION = 1024
        const val PROFILE_IMAGE_JPEG_QUALITY = 85
    }
}
