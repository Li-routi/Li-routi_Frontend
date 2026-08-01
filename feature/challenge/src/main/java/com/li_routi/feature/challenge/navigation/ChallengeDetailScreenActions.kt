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

    /** 내 인증 게시글 더보기 바텀시트의 "수정하기" 완료(=수정 화면의 "완료" 버튼) */
    fun onEditCertificationSubmit(certificationId: Long, content: String)

    /**
     * 내 인증 게시글 더보기 바텀시트의 "삭제하기" 탭.
     * 백엔드에 인증 삭제 API가 아직 없어 현재는 버튼 UI만 존재하고 동작은 없다(API 추가는 별도 진행 예정).
     */
    fun onDeleteCertificationClick(certificationId: Long)

    /** 타 사용자 인증 게시글 더보기 바텀시트의 "신고하기" 탭 */
    fun onReportCertificationClick(certificationId: Long)

    /** "인증"(전체) 탭 인증 게시글의 좋아요 아이콘 탭 (현재 liked 상태에 따라 좋아요/취소 전환) */
    fun onLikeToggleClick(certificationId: Long)

    /** 새 인증 게시글 업로드(사진+코멘트) 성공 후 호출. 상세/인증 목록을 새로고침한다. */
    fun onVerificationSubmitted()
}
