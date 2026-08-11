package com.li_routi.feature.mypage.vm

import androidx.lifecycle.viewModelScope
import com.li_routi.core.common.android.architecture.BaseViewModel
import com.li_routi.core.common.kotlin.util.ResultState
import com.li_routi.core.data.di.AuthContainer
import com.li_routi.core.domain.auth.GetMyVerificationsUseCase
import com.li_routi.core.domain.auth.MyVerificationEntry
import com.li_routi.core.domain.auth.VerificationReviewStatus
import com.li_routi.feature.mypage.component.MyVerificationCardUiModel
import com.li_routi.feature.mypage.component.PendingVerificationUiModel
import com.li_routi.feature.mypage.component.SimpleDate
import com.li_routi.feature.mypage.component.toApiDateString
import com.li_routi.feature.mypage.component.todaySimpleDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * "내 인증" 화면 ViewModel. `GET /api/members/me/verifications`로 [MyVerificationUiState.selectedDate]에
 * 남긴 인증(챌린지·개인 루틴·그룹 루틴 통합)을 조회한다 — 날짜를 좌우로 넘길 때마다 [onDateSelected]가
 * 다시 호출한다.
 *
 * reviewStatus가 PENDING인 항목은 Figma 디자인(node `4869:36868`) 기준 화면 상단 "대기 중인 인증" 가로
 * 목록으로, 나머지는 메인 목록으로 나눠 담는다([toPendingUiModel]/[toCardUiModel] 참고). 이 API는 AI
 * 검증 남은 시간을 내려주지 않아 [PendingVerificationUiModel.remainingTimeLabel]에 실제 카운트다운 대신
 * API 문서가 명시한 "심사 중" 문구를 고정으로 채운다.
 */
class MyVerificationViewModel(
    initialDate: SimpleDate = todaySimpleDate(),
    private val getMyVerificationsUseCase: GetMyVerificationsUseCase = AuthContainer.getMyVerificationsUseCase,
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(MyVerificationUiState(selectedDate = initialDate))
    val uiState: StateFlow<MyVerificationUiState> = _uiState.asStateFlow()

    init {
        loadVerifications()
    }

    fun onDateSelected(date: SimpleDate) {
        if (date == _uiState.value.selectedDate) return
        _uiState.update { it.copy(selectedDate = date) }
        loadVerifications()
    }

    private fun loadVerifications() {
        val requestedDate = _uiState.value.selectedDate
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = getMyVerificationsUseCase(date = requestedDate.toApiDateString())) {
                is ResultState.Success -> _uiState.update { current ->
                    // 응답이 오는 동안 다른 날짜로 넘어갔으면 무시한다 — 안 그러면 이전 요청의 응답이
                    // 나중에 도착해 방금 선택한 날짜의 목록을 덮어쓸 수 있다.
                    if (current.selectedDate != requestedDate) return@update current
                    val (pending, settled) = result.data.verifications.partition {
                        it.reviewStatus == VerificationReviewStatus.PENDING
                    }
                    current.copy(
                        verifications = settled.map { it.toCardUiModel() },
                        pendingVerifications = pending.map { it.toPendingUiModel() },
                        isLoading = false,
                    )
                }
                is ResultState.Error -> _uiState.update { current ->
                    // 실패해도 직전 날짜의 목록을 그대로 남겨두면, 예를 들어 세션이 끊겨 모든 요청이
                    // 401로 실패하는 상황에서 날짜를 넘겨도 화면이 안 바뀌는 것처럼 보인다 — 요청한
                    // 날짜에 대한 응답이 실패했으면 그 날짜에 대해선 빈 목록으로 비워 보여준다.
                    if (current.selectedDate != requestedDate) {
                        current
                    } else {
                        current.copy(verifications = emptyList(), pendingVerifications = emptyList(), isLoading = false)
                    }
                }
                ResultState.Loading -> Unit
            }
        }
    }
}

data class MyVerificationUiState(
    val selectedDate: SimpleDate,
    val verifications: List<MyVerificationCardUiModel> = emptyList(),
    val pendingVerifications: List<PendingVerificationUiModel> = emptyList(),
    val isLoading: Boolean = false,
)

private fun MyVerificationEntry.toCardUiModel(): MyVerificationCardUiModel = MyVerificationCardUiModel(
    routineName = "$categoryName·$title",
    memo = content.orEmpty(),
    imageUrl = imageUrl.ifBlank { null },
)

private fun MyVerificationEntry.toPendingUiModel(): PendingVerificationUiModel = PendingVerificationUiModel(
    routineName = "$categoryName·$title",
    memo = content.orEmpty(),
    imageUrl = imageUrl.ifBlank { null },
    remainingTimeLabel = "심사 중",
)
