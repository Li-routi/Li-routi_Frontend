package com.li_routi.core.data.repository

import com.li_routi.core.common.kotlin.util.ResultState
import com.li_routi.core.common.kotlin.util.safeApiCall
import com.li_routi.core.data.mapper.toDomain
import com.li_routi.core.data.network.apiCall
import com.li_routi.core.data.network.service.ChallengeApiService
import com.li_routi.core.domain.challenge.CertificationPage
import com.li_routi.core.domain.challenge.ChallengeCategory
import com.li_routi.core.domain.challenge.ChallengeDetail
import com.li_routi.core.domain.challenge.ChallengePage
import com.li_routi.core.domain.challenge.ChallengeRepository
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
}
