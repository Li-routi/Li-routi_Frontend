package com.li_routi.core.data.repository

import com.li_routi.core.common.kotlin.util.ApiException
import com.li_routi.core.common.kotlin.util.ResultState
import com.li_routi.core.common.kotlin.util.safeApiCall
import com.li_routi.core.data.mapper.toDomain
import com.li_routi.core.data.network.apiCall
import com.li_routi.core.data.network.dto.request.ChallengeVerificationRequest
import com.li_routi.core.data.network.dto.request.ReportRequest
import com.li_routi.core.data.network.service.ChallengeApiService
import com.li_routi.core.domain.challenge.CertificationPage
import com.li_routi.core.domain.challenge.ChallengeCategory
import com.li_routi.core.domain.challenge.ChallengeDetail
import com.li_routi.core.domain.challenge.ChallengePage
import com.li_routi.core.domain.challenge.ChallengeRepository
import com.li_routi.core.domain.challenge.CreatedVerification
import com.li_routi.core.domain.challenge.LikeResult
import com.li_routi.core.domain.challenge.MyCertificationPage
import com.li_routi.core.domain.challenge.MyChallenge
import com.li_routi.core.domain.challenge.Participation

class ChallengeRepositoryImpl(
    private val api: ChallengeApiService,
) : ChallengeRepository {

    override suspend fun getChallenges(
        category: ChallengeCategory?,
        keyword: String?,
        cursor: Long?,
        size: Int?,
    ): ResultState<ChallengePage> = safeApiCall {
        apiCall {
            api.getChallenges(
                category = category?.name,
                keyword = keyword,
                cursor = cursor,
                size = size,
            )
        }.toDomain()
    }

    override suspend fun getChallengeDetail(challengeId: Long): ResultState<ChallengeDetail> = safeApiCall {
        apiCall { api.getChallenge(challengeId) }.toDomain()
    }

    override suspend fun getVerifications(
        challengeId: Long,
        cursor: Long?,
        size: Int?,
    ): ResultState<CertificationPage> = safeApiCall {
        apiCall { api.getVerifications(challengeId = challengeId, cursor = cursor, size = size) }.toDomain()
    }

    override suspend fun participate(challengeId: Long): ResultState<Participation> = safeApiCall {
        apiCall { api.participate(challengeId) }.toDomain()
    }

    override suspend fun leaveChallenge(challengeId: Long): ResultState<Participation> = safeApiCall {
        apiCall { api.leaveChallenge(challengeId) }.toDomain()
    }

    override suspend fun getMyChallenges(
        category: ChallengeCategory?,
        keyword: String?,
    ): ResultState<List<MyChallenge>> = safeApiCall {
        apiCall { api.getMyChallenges(category = category?.name, keyword = keyword) }.toDomain()
    }

    // 참여하지 않은 챌린지의 내 인증을 조회하면 서버가 409("참여 중인 챌린지가 아닙니다")를 내려준다.
    // 이건 에러가 아니라 "인증 기록 없음"과 같은 뜻이므로, 빈 페이지로 흡수해 화면이 에러 없이 뜨게 한다.
    override suspend fun getMyVerifications(
        challengeId: Long,
        cursor: Long?,
        size: Int?,
    ): ResultState<MyCertificationPage> = safeApiCall {
        try {
            apiCall { api.getMyVerifications(challengeId = challengeId, cursor = cursor, size = size) }.toDomain()
        } catch (e: ApiException) {
            if (e.statusCode == 409) {
                MyCertificationPage(certifications = emptyList(), currentStreak = 0, nextCursor = null, hasNext = false)
            } else {
                throw e
            }
        }
    }

    override suspend fun createVerification(
        challengeId: Long,
        mediaKey: String,
        content: String?,
    ): ResultState<CreatedVerification> = safeApiCall {
        apiCall { api.createVerification(challengeId, ChallengeVerificationRequest(mediaKey, content)) }.toDomain()
    }

    override suspend fun reportVerification(
        challengeId: Long,
        verificationId: Long,
        reason: String?,
    ): ResultState<Unit> = safeApiCall {
        apiCall { api.reportVerification(challengeId, verificationId, ReportRequest(reason)) }
        Unit
    }

    override suspend fun likeVerification(challengeId: Long, verificationId: Long): ResultState<LikeResult> =
        safeApiCall {
            apiCall { api.likeVerification(challengeId, verificationId) }.toDomain()
        }

    override suspend fun unlikeVerification(challengeId: Long, verificationId: Long): ResultState<LikeResult> =
        safeApiCall {
            apiCall { api.unlikeVerification(challengeId, verificationId) }.toDomain()
        }
}