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
 * 계정 관리 화면 ViewModel. 로그아웃(`POST /api/members/logout`)과 회원 탈퇴 둘 다 연동한다.
 */
class AccountManageViewModel(
    private val logoutUseCase: LogoutUseCase = AuthContainer.logoutUseCase,
    private val withdrawUseCase: WithdrawUseCase = AuthContainer.withdrawUseCase,
) : BaseViewModel() {

    private val _uiEvent = MutableSharedFlow<AccountManageUiEvent>(extraBufferCapacity = 1)
    val uiEvent: SharedFlow<AccountManageUiEvent> = _uiEvent.asSharedFlow()

    private var isWithdrawing = false

    fun onLogoutConfirmed() {
        viewModelScope.launch {
            when (val result = logoutUseCase()) {
                is ResultState.Success -> _uiEvent.emit(AccountManageUiEvent.LogoutSucceeded)
                is ResultState.Error -> _uiEvent.emit(AccountManageUiEvent.ShowError(result.message))
                ResultState.Loading -> Unit
            }
        }
    }

    // 탈퇴 확인 모달은 확정 즉시 닫히지만, 재호출(예: 프로그램적 재진입)로 DELETE가 중복 발생하지
    // 않도록 진행 중 여부를 별도로 막는다.
    fun onWithdrawConfirmed() {
        if (isWithdrawing) return
        isWithdrawing = true
        viewModelScope.launch {
            when (val result = withdrawUseCase()) {
                is ResultState.Success -> _uiEvent.emit(AccountManageUiEvent.WithdrawSucceeded)
                is ResultState.Error -> _uiEvent.emit(AccountManageUiEvent.ShowError(result.message))
                ResultState.Loading -> Unit
            }
            isWithdrawing = false
        }
    }
}
