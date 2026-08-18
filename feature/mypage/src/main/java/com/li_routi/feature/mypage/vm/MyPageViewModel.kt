package com.li_routi.feature.mypage.vm

import android.content.Context
import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.li_routi.core.common.android.architecture.BaseViewModel
import com.li_routi.core.common.android.image.readProfileImageBytes
import com.li_routi.core.common.kotlin.util.ResultState
import com.li_routi.core.data.di.AuthContainer
import com.li_routi.core.data.di.NotificationContainer
import com.li_routi.core.data.profile.MemberProfileCache
import com.li_routi.core.domain.auth.GetMyInfoUseCase
import com.li_routi.core.domain.auth.ProfileImageUpload
import com.li_routi.core.domain.auth.UpdateProfileUseCase
import com.li_routi.core.domain.notification.HasUnreadNotificationUseCase
import com.li_routi.feature.mypage.navigation.MyPageScreenActions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 마이페이지 ViewModel.
 *
 * "프로필 수정"/"업적"/"리포트"/"앱 정보"/"계정 관리"는 [MyPageUiEvent]로 이 모듈 안에서 직접
 * 화면을 전환한다. 알림벨/설정은 목적지 화면(알림 목록/알림 설정)이 `feature/home`에 있어 이 모듈이
 * 직접 전환할 수 없으므로, 이벤트만 emit하고 실제 전환은 [MyPageRoute]가 외부(app 모듈)로부터 받은
 * 콜백에 위임한다.
 */
class MyPageViewModel(
    // 마지막으로 알던 닉네임/뱃지 값으로 먼저 그려서 조회 완료 전 깜빡임을 없앤다(HomeViewModel과 동일 패턴).
    initialState: MyPageUiState = MyPageUiState(
        nickname = MemberProfileCache.nickname.value ?: "",
        hasUnreadNotification = MemberProfileCache.hasUnreadNotification.value,
    ),
    private val getMyInfoUseCase: GetMyInfoUseCase = AuthContainer.getMyInfoUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase = AuthContainer.updateProfileUseCase,
    private val hasUnreadNotificationUseCase: HasUnreadNotificationUseCase = NotificationContainer.hasUnreadNotificationUseCase,
) : BaseViewModel(), MyPageScreenActions {

    private val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<MyPageUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<MyPageUiEvent>(extraBufferCapacity = 1)
    val uiEvent: SharedFlow<MyPageUiEvent> = _uiEvent.asSharedFlow()

    init {
        loadMyInfo()
        loadUnreadNotificationStatus()
    }

    /** 마이 탭을 다시 눌러 들어올 때 등, 외부에서 프로필을 다시 불러오라는 신호가 왔을 때 호출한다. */
    fun refresh() {
        loadMyInfo()
        loadUnreadNotificationStatus()
    }

    /** 종 아이콘 뱃지용 "안 읽은 알림 있음" 여부. [HasUnreadNotificationUseCase] 참고. */
    private fun loadUnreadNotificationStatus() {
        viewModelScope.launch {
            val result = hasUnreadNotificationUseCase()
            if (result is ResultState.Success) {
                MemberProfileCache.hasUnreadNotification.value = result.data
                _uiState.update { it.copy(hasUnreadNotification = result.data) }
            }
        }
    }

    private fun loadMyInfo() {
        viewModelScope.launch {
            when (val result = getMyInfoUseCase()) {
                is ResultState.Success -> {
                    MemberProfileCache.nickname.value = result.data.nickname
                    _uiState.update {
                        it.copy(
                            nickname = result.data.nickname,
                            email = result.data.email,
                            profileImageUrl = result.data.profileImageUrl,
                            socialProvider = result.data.socialProvider,
                            isProfileLoaded = true,
                        )
                    }
                }
                is ResultState.Error -> {
                    // isProfileLoaded는 true로 둔다(그렇지 않으면 onEditProfileClick이 영영 막힘). 에러는
                    // uiEvent(SharedFlow)로 곧장 emit하지 않는다 — init에서 바로 조회가 시작돼 MyPageRoute의
                    // uiEvent 구독이 아직 시작되기 전에 끝날 수 있고, 그러면 재생 없이 조용히 유실된다.
                    // 대신 상태에 담아 UI가 구독을 시작한 뒤에도 확실히 보이게 한다.
                    _uiState.update { it.copy(isProfileLoaded = true, profileLoadError = result.message) }
                }
                ResultState.Loading -> Unit
            }
        }
    }

    /**
     * 프로필 수정 화면에서 "저장" 탭 시 호출된다. [imageUri]가 null이면(사진을 새로 고르지 않았으면)
     * 기존 프로필 사진을 그대로 두고 닉네임만 수정한다. [resetToDefault]가 true면(바텀시트에서 "기본
     * 이미지로 변경"을 눌렀으면) [imageUri]는 무시하고 프로필 사진을 지워 기본 이미지 상태로 되돌린다.
     */
    fun onSaveProfile(context: Context, newNickname: String, imageUri: Uri?, resetToDefault: Boolean = false) {
        viewModelScope.launch {
            if (_uiState.value.isSavingProfile) return@launch
            _uiState.update { it.copy(isSavingProfile = true) }

            val imageResult = imageUri?.takeUnless { resetToDefault }?.let { uri ->
                runCatching {
                    withContext(Dispatchers.IO) {
                        ProfileImageUpload(bytes = readProfileImageBytes(context, uri), contentType = "image/jpeg")
                    }
                }
            }
            if (imageResult != null && imageResult.isFailure) {
                _uiState.update { it.copy(isSavingProfile = false) }
                _uiEvent.emit(MyPageUiEvent.ShowError("프로필 이미지를 불러오지 못했습니다."))
                return@launch
            }

            when (
                val result = updateProfileUseCase(newNickname, imageResult?.getOrNull(), removeImage = resetToDefault)
            ) {
                is ResultState.Success -> {
                    MemberProfileCache.nickname.value = result.data.nickname
                    _uiState.update {
                        it.copy(
                            nickname = result.data.nickname,
                            email = result.data.email,
                            profileImageUrl = result.data.profileImageUrl,
                            isSavingProfile = false,
                        )
                    }
                    _uiEvent.emit(MyPageUiEvent.ProfileSaved)
                }
                is ResultState.Error -> {
                    _uiState.update { it.copy(isSavingProfile = false) }
                    _uiEvent.emit(MyPageUiEvent.ShowError(result.message))
                }
                ResultState.Loading -> Unit
            }
        }
    }

    override fun onNotificationClick() {
        viewModelScope.launch { _uiEvent.emit(MyPageUiEvent.NavigateToNotification) }
    }
    override fun onSettingsClick() {
        viewModelScope.launch { _uiEvent.emit(MyPageUiEvent.NavigateToNotificationSettings) }
    }

    // 조회 응답이 오기 전엔 진입을 막는다 — MyPageUiState.isProfileLoaded 문서 참고.
    override fun onEditProfileClick() {
        if (!_uiState.value.isProfileLoaded) return
        viewModelScope.launch { _uiEvent.emit(MyPageUiEvent.NavigateToEditProfile) }
    }
    override fun onMyVerificationClick() {
        viewModelScope.launch { _uiEvent.emit(MyPageUiEvent.NavigateToMyVerification) }
    }
    override fun onAchievementClick() {
        viewModelScope.launch { _uiEvent.emit(MyPageUiEvent.NavigateToAchievement) }
    }
    override fun onReportClick() {
        viewModelScope.launch { _uiEvent.emit(MyPageUiEvent.NavigateToReport) }
    }
    override fun onAccountManageClick() {
        viewModelScope.launch { _uiEvent.emit(MyPageUiEvent.NavigateToAccountManage) }
    }
    override fun onAppInfoClick() {
        viewModelScope.launch { _uiEvent.emit(MyPageUiEvent.NavigateToAppInfo) }
    }

    /** [MyPageUiState.profileLoadError]를 화면에 보여준 뒤 호출 — 다시 보이지 않도록 지운다. */
    fun onProfileLoadErrorShown() {
        _uiState.update { it.copy(profileLoadError = null) }
    }
}
