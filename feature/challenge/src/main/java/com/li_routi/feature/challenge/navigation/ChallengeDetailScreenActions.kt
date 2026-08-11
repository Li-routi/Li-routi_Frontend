package com.li_routi.feature.challenge.navigation

import com.li_routi.core.domain.challenge.ReportType
import com.li_routi.core.domain.challenge.VerificationSort
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

    /** 정렬 바텀시트에서 정렬 기준 선택. "인증"/"내 인증 보기" 두 탭 모두 리셋 후 다시 불러온다. */
    fun onSortSelected(sort: VerificationSort)

    /** 더보기 바텀시트의 "챌린지 나가기" 탭 */
    fun onLeaveChallengeClick()

    /** 내 인증 게시글 더보기 바텀시트의 "수정하기" 완료(=수정 화면의 "완료" 버튼) */
    fun onEditCertificationSubmit(certificationId: Long, content: String)

    /** 인증 수정 화면을 닫을 때(뒤로가기/닫기 또는 수정 성공 후). 이전 실패 시 남은 에러 메시지를 지운다. */
    fun onEditCertificationDismiss()

    /** 내 인증 게시글 더보기 바텀시트의 "삭제하기" 탭 → 확인 다이얼로그 "삭제" 확정. */
    fun onDeleteCertificationClick(certificationId: Long)

    /** 신고 사유 선택 화면의 "완료" 탭. [reportType]은 선택한 신고 사유 분류, [reason]은 ETC일 때만 쓰이는 직접 입력 텍스트. */
    fun onReportCertificationClick(certificationId: Long, reportType: ReportType, reason: String?)

    /** "인증"(전체) 탭 인증 게시글의 좋아요 아이콘 탭 (현재 liked 상태에 따라 좋아요/취소 전환) */
    fun onLikeToggleClick(certificationId: Long)

    /** 새 인증 게시글 업로드(사진+코멘트) 성공 후 호출. 상세/인증 목록을 새로고침한다. */
    fun onVerificationSubmitted()

    /** 목록을 당겨서 새로고침. 상세/현재 탭(들)을 처음부터 다시 불러온다. */
    fun onRefresh()

    /** 삭제/신고 실패 토스트를 닫을 때. */
    fun onActionErrorDismissed()
}
