package com.li_routi.feature.login.vm

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.li_routi.core.common.android.architecture.BaseViewModel
import com.li_routi.core.common.kotlin.util.ResultState
import com.li_routi.core.data.di.AuthContainer
import com.li_routi.core.data.di.ProfileContainer
import com.li_routi.core.domain.auth.SocialProvider
import com.li_routi.core.domain.profile.ProfileImageUpload
import com.li_routi.feature.login.BuildConfig
import com.li_routi.feature.login.auth.GoogleAuthHelper
import com.li_routi.feature.login.auth.KakaoAuthHelper
import com.li_routi.feature.login.navigation.LoginScreenActions
import java.io.ByteArrayOutputStream
import java.io.IOException
import kotlin.math.max
import kotlin.math.roundToInt
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
                    runCatching { withContext(Dispatchers.IO) { readProfileImageUpload(context, uri) } }
                }
                if (imageResult != null && imageResult.isFailure) {
                    // 사용자가 사진을 골랐는데 읽기가 실패한 경우 — 이미지 없이 조용히 저장하면 안 되므로 여기서 중단한다.
                    emitEvent(LoginUiEvent.ShowError("프로필 이미지를 불러오지 못했습니다."))
                    return@launch
                }
                val image = imageResult?.getOrNull()
                when (val result = ProfileContainer.updateProfileUseCase(nickname, image)) {
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
     * content:// Uri에서 프로필 이미지를 읽어 긴 변 [PROFILE_IMAGE_MAX_DIMENSION]px 이하로 리사이징하고
     * JPEG로 압축한 뒤 업로드용 바이트로 반환한다. 원본을 그대로 ByteArray로 올리면 고해상도 사진에서
     * 메모리 부족으로 앱이 죽을 수 있어, 디코딩 단계(inSampleSize)부터 다운샘플링해 최대 메모리 사용량을 줄인다.
     * IO/CPU 작업이라 호출부에서 IO 디스패처로 실행한다.
     *
     * 스트림을 열지 못하거나 디코딩에 실패하면 이미지 없이 조용히 넘어가지 않고 [IOException]을 던진다 —
     * 호출부(runCatching)가 이를 잡아 저장을 중단하고 사용자에게 실패를 알려야 하기 때문이다.
     */
    private fun readProfileImageUpload(context: Context, uri: Uri): ProfileImageUpload {
        val resolver = context.contentResolver

        // 1) inJustDecodeBounds: 픽셀 데이터 없이 가로/세로 크기만 먼저 읽는다.
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        resolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }
            ?: throw IOException("프로필 이미지를 열 수 없습니다: $uri")
        if (bounds.outWidth <= 0 || bounds.outHeight <= 0) {
            throw IOException("프로필 이미지 크기를 읽을 수 없습니다: $uri")
        }

        // 2) inSampleSize로 목표 크기에 가깝게 다운샘플링한 상태로 디코딩해, 원본 해상도 전체를 메모리에 올리지 않는다.
        val decodeOptions = BitmapFactory.Options().apply {
            inSampleSize = calculateInSampleSize(bounds.outWidth, bounds.outHeight, PROFILE_IMAGE_MAX_DIMENSION)
        }
        var bitmap = resolver.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it, null, decodeOptions)
        } ?: throw IOException("프로필 이미지를 디코딩할 수 없습니다: $uri")

        // 3) inSampleSize는 2의 배수 단위로만 줄어들기 때문에, 목표 크기에 정확히 맞추기 위해 한 번 더 스케일링한다.
        bitmap = bitmap.scaleDownTo(PROFILE_IMAGE_MAX_DIMENSION)

        // 4) 카메라로 찍은 세로 사진은 EXIF 방향 정보에 픽셀 회전을 위임하는 경우가 많다.
        // 재인코딩 과정에서 EXIF가 유지된다는 보장이 없으므로, 픽셀 자체를 회전시켜 방향을 고정한다.
        val orientation = resolver.openInputStream(uri)?.use {
            ExifInterface(it).getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        } ?: ExifInterface.ORIENTATION_NORMAL
        bitmap = bitmap.rotateForExifOrientation(orientation)

        val bytes = ByteArrayOutputStream().use { output ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, PROFILE_IMAGE_JPEG_QUALITY, output)
            output.toByteArray()
        }
        bitmap.recycle()

        return ProfileImageUpload(bytes = bytes, contentType = "image/jpeg")
    }

    /** 디코딩 시점에 대략적으로만 줄어드는 [BitmapFactory.Options.inSampleSize] 값을 계산한다(2의 거듭제곱 단위). */
    private fun calculateInSampleSize(width: Int, height: Int, maxDimension: Int): Int {
        var inSampleSize = 1
        var longerSide = max(width, height)
        while (longerSide / 2 >= maxDimension) {
            inSampleSize *= 2
            longerSide /= 2
        }
        return inSampleSize
    }

    /** 긴 변이 [maxDimension]을 넘으면 비율을 유지한 채 정확히 그 크기로 축소한다. 이미 작으면 원본 비트맵을 그대로 반환한다. */
    private fun Bitmap.scaleDownTo(maxDimension: Int): Bitmap {
        val longerSide = max(width, height)
        if (longerSide <= maxDimension) return this
        val scale = maxDimension.toFloat() / longerSide
        val scaledWidth = (width * scale).roundToInt().coerceAtLeast(1)
        val scaledHeight = (height * scale).roundToInt().coerceAtLeast(1)
        val scaled = Bitmap.createScaledBitmap(this, scaledWidth, scaledHeight, true)
        if (scaled !== this) recycle()
        return scaled
    }

    /** EXIF 방향 값에 맞춰 픽셀을 회전시킨다. 회전이 필요 없으면(NORMAL 등) 원본 비트맵을 그대로 반환한다. */
    private fun Bitmap.rotateForExifOrientation(orientation: Int): Bitmap {
        val degrees = when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90f
            ExifInterface.ORIENTATION_ROTATE_180 -> 180f
            ExifInterface.ORIENTATION_ROTATE_270 -> 270f
            else -> return this
        }
        val matrix = Matrix().apply { postRotate(degrees) }
        val rotated = Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
        if (rotated !== this) recycle()
        return rotated
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

    private companion object {
        /** 프로필 사진 업로드 시 리사이징 목표가 되는 긴 변의 최대 픽셀 수. 원본 화질까지는 필요 없다. */
        const val PROFILE_IMAGE_MAX_DIMENSION = 1024
        const val PROFILE_IMAGE_JPEG_QUALITY = 85
    }
}
