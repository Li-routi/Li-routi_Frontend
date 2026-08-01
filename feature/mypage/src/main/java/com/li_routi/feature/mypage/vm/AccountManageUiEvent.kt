package com.li_routi.feature.mypage.vm

/** 계정 관리 화면에서 발생하는 일회성 이벤트. */
sealed interface AccountManageUiEvent {
    /** 로그아웃 API가 성공했다 — 토큰은 이미 로컬에서 삭제된 상태다. */
    data object LogoutSucceeded : AccountManageUiEvent

    /** 로그아웃 API 실패. */
    data class ShowError(val message: String) : AccountManageUiEvent
}
