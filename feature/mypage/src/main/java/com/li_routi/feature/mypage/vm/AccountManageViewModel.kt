package com.li_routi.feature.mypage.vm

import androidx.lifecycle.viewModelScope
import com.li_routi.core.common.android.architecture.BaseViewModel
import com.li_routi.core.common.kotlin.util.ResultState
import com.li_routi.core.data.di.AuthContainer
import com.li_routi.core.domain.auth.LogoutUseCase
import com.li_routi.core.domain.auth.WithdrawUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

/**
 * 계정 관리 화면 ViewModel.
 *
 * `POST /api/members/logout` 연동만 담당한다 — "회원 탈퇴"는 API 명세가 아직 없어 범위 밖이다
 * ([com.li_routi.feature.mypage.screen.AccountManageScreen]의 `onWithdrawClick`은 여전히 UI만).
 */
class AccountManageViewModel(
    private val logoutUseCase: LogoutUseCase = AuthContainer.logoutUseCase,
    private val withdrawUseCase: WithdrawUseCase = AuthContainer.withdrawUseCase,
) : BaseViewModel() {

    private val _uiEvent = MutableSharedFlow<AccountManageUiEvent>(extraBufferCapacity = 1)
    val uiEvent: SharedFlow<AccountManageUiEvent> = _uiEvent.asSharedFlow()

    fun onLogoutConfirmed() {
        viewModelScope.launch {
            when (val result = logoutUseCase()) {
                is ResultState.Success -> _uiEvent.emit(AccountManageUiEvent.LogoutSucceeded)
                is ResultState.Error -> _uiEvent.emit(AccountManageUiEvent.ShowError(result.message))
                ResultState.Loading -> Unit
            }
        }
    }

    fun onWithdrawConfirmed() {
        viewModelScope.launch {
            when (val result = withdrawUseCase()) {
                is ResultState.Success -> _uiEvent.emit(AccountManageUiEvent.WithdrawSucceeded)
                is ResultState.Error -> _uiEvent.emit(AccountManageUiEvent.ShowError(result.message))
                ResultState.Loading -> Unit
            }
        }
    }
}
