package com.li_routi.feature.challenge.navigation

import com.li_routi.feature.challenge.vm.CertificationTab

/**
 * `ChallengeDetailScreen`에서 발생하는 사용자 이벤트에 대한 콜백 계약(contract).
 */
interface ChallengeDetailScreenActions {
    /** "참여하기" 버튼 탭 (참여 후에는 "인증하기"로 바뀌며 더 이상 호출되지 않음) */
    fun onJoinClick()

    /** "인증"/"내 인증 보기" 탭 전환 */
    fun onTabSelected(tab: CertificationTab)

    /** 인증 목록 스크롤이 끝에 닿았을 때 다음 페이지 로드 */
    fun onLoadMore()

    /** 더보기 바텀시트의 "챌린지 나가기" 탭 */
    fun onLeaveChallengeClick()
}
