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

    // 캐릭터 본체(MemberAppearanceStore.appearance.characterId)도 같은 이유로 같이 기억해둔다 —
    // 안 그러면 캐릭터가 기본값("blue_bird")이 아닌 사용자도 탭을 새로 열 때마다 로컬 하이드레이션이
    // 끝나기 전까지 잠깐 기본 캐릭터가 보이는 깜빡임이 남는다.
    val characterId = MutableStateFlow<String?>(null)

    /** 로그아웃/회원 탈퇴/토큰 갱신 실패 등 세션이 끝날 때 호출한다.
     * 안 그러면 다음 로그인 사용자가 화면을 열자마자 이전 사용자의 닉네임/캐릭터/알림 상태가
     * 잠깐 보일 수 있다. */
    fun clear() {
        nickname.value = null
        hasUnreadNotification.value = false
        characterId.value = null
    }
}
