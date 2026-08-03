package com.li_routi.feature.mypage.vm

import androidx.lifecycle.viewModelScope
import com.li_routi.core.common.android.architecture.BaseViewModel
import com.li_routi.feature.mypage.navigation.MyPageScreenActions
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * 마이페이지 ViewModel.
 *
 * 메뉴 탭의 실제 화면 전환은 다른 담당자가 [MyPageScreenActions] 구현을 연결한다 —
 * "프로필 수정"/"업적"/"리포트"/"앱 정보"/"계정 관리"만 [MyPageUiEvent]로 이 모듈 안에서 직접 연결한다.
 */
class MyPageViewModel(
    initialState: MyPageUiState = MyPageUiState(),
) : BaseViewModel(), MyPageScreenActions {

    private val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<MyPageUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<MyPageUiEvent>(extraBufferCapacity = 1)
    val uiEvent: SharedFlow<MyPageUiEvent> = _uiEvent.asSharedFlow()

    /** 닉네임 변경 화면에서 "저장" 탭 시 호출된다. */
    fun onNicknameSaved(newNickname: String) {
        _uiState.update { it.copy(nickname = newNickname) }
    }

    override fun onNotificationClick() = Unit
    override fun onSettingsClick() = Unit
    override fun onEditProfileClick() {
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
}
