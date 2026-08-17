package com.li_routi.core.data.profile

import kotlinx.coroutines.flow.MutableStateFlow

/**
 * 닉네임/안 읽은 알림 여부처럼 홈·마이페이지가 각자 조회하지만 사실상 같은 최신값인 정보를 앱
 * 전역에서 기억해둔다.
 *
 * 홈은 하단 탭을 누를 때마다 `AppNavHost.selectTab`이 저장된 화면 상태를 지우고 ViewModel을
 * 통째로 새로 만든다(다른 탭 데이터를 새로고침하기 위함). 그 결과 닉네임/뱃지도 매번 기본값(닉네임
 * placeholder, 뱃지 숨김)부터 다시 그려졌다가 조회 응답이 와야 실제 값으로 바뀌는 깜빡임이 있었다.
 * 마지막으로 알고 있던 값을 여기 남겨두고, 화면이 새로 만들어질 때 이 값으로 먼저 그리게 한다.
 */
object MemberProfileCache {
    val nickname = MutableStateFlow<String?>(null)
    val hasUnreadNotification = MutableStateFlow(false)
}
