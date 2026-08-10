package com.li_routi.core.domain.challenge

import com.li_routi.core.common.kotlin.util.ResultState

interface ChallengeRepository {

    /**
     * 챌린지 목록을 최신순으로 조회한다 (무한 스크롤 커서 방식).
     *
     * @param category 분류 필터. null이면 전체.
     * @param keyword 챌린지 이름 부분 검색어.
     * @param cursor 직전 응답의 nextCursor. 첫 요청에는 null.
     * @param size 한 번에 가져올 개수 (서버 기본값 20, 최대 50).
     */
    suspend fun getChallenges(
        category: ChallengeCategory?,
        keyword: String?,
        cursor: Long?,
        size: Int?,
    ): ResultState<ChallengePage>

    /** 챌린지 상세를 조회한다. */
    suspend fun getChallengeDetail(challengeId: Long): ResultState<ChallengeDetail>

    /**
     * 챌린지의 인증 피드를 조회한다 (무한 스크롤 커서 방식, 커서 값 = verificationId, sort=LIKES면 cursorLikeCount도 같이 넘겨야 함).
     */
    suspend fun getVerifications(
        challengeId: Long,
        cursor: Long?,
        cursorLikeCount: Long?,
        size: Int?,
        sort: VerificationSort,
    ): ResultState<CertificationPage>

    /** 로그인한 회원이 챌린지에 참여한다. */
    suspend fun participate(challengeId: Long): ResultState<Participation>

    /** 로그인한 회원이 참여 중인 챌린지를 그만둔다. */
    suspend fun leaveChallenge(challengeId: Long): ResultState<Participation>

    /**
     * 로그인한 회원이 현재 참여 중인 챌린지 목록을 조회한다.
     *
     * @param category 분류 필터. null이면 전체.
     * @param keyword 챌린지 이름 부분 검색어.
     */
    suspend fun getMyChallenges(
        category: ChallengeCategory?,
        keyword: String?,
    ): ResultState<List<MyChallenge>>

    /**
     * 로그인한 회원 본인이 작성한 인증 피드를 조회한다 (무한 스크롤 커서 방식, sort=LIKES면 cursorLikeCount도 같이 넘겨야 함).
     */
    suspend fun getMyVerifications(
        challengeId: Long,
        cursor: Long?,
        cursorLikeCount: Long?,
        size: Int?,
        sort: VerificationSort,
    ): ResultState<MyCertificationPage>

    /** 사진(mediaKey)과 코멘트로 인증 게시글을 작성한다. mediaKey는 미디어 presigned 업로드로 먼저 발급받는다. */
    suspend fun createVerification(challengeId: Long, mediaKey: String, content: String?): ResultState<CreatedVerification>

    /** 인증 게시글의 메모(코멘트)만 수정한다 (사진은 그대로 유지). */
    suspend fun updateVerificationMemo(challengeId: Long, verificationId: Long, content: String): ResultState<EditedVerification>

    /** 인증 게시글을 삭제한다. */
    suspend fun deleteVerification(challengeId: Long, verificationId: Long): ResultState<Unit>

    /** 인증 게시글을 신고한다. */
    suspend fun reportVerification(challengeId: Long, verificationId: Long, reportType: ReportType, reason: String?): ResultState<Unit>

    /** 인증 게시글에 좋아요를 누른다. */
    suspend fun likeVerification(challengeId: Long, verificationId: Long): ResultState<LikeResult>

    /** 인증 게시글의 좋아요를 취소한다. */
    suspend fun unlikeVerification(challengeId: Long, verificationId: Long): ResultState<LikeResult>
}
