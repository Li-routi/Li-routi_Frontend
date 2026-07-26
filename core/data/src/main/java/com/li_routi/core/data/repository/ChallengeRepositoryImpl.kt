package com.li_routi.core.data.repository

import com.li_routi.core.common.kotlin.util.ApiException
import com.li_routi.core.common.kotlin.util.ResultState
import com.li_routi.core.common.kotlin.util.safeApiCall
import com.li_routi.core.data.mapper.toDomain
import com.li_routi.core.data.network.dto.response.ApiResponse
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
        api.getChallenges(
            category = category?.name,
            keyword = keyword,
            cursor = cursor,
            size = size,
        ).unwrap().toDomain()
    }

    override suspend fun getChallengeDetail(challengeId: Long): ResultState<ChallengeDetail> = safeApiCall {
        api.getChallenge(challengeId).unwrap().toDomain()
    }

    override suspend fun getVerifications(
        challengeId: Long,
        cursor: Long?,
        size: Int?,
    ): ResultState<CertificationPage> = safeApiCall {
        api.getVerifications(challengeId = challengeId, cursor = cursor, size = size).unwrap().toDomain()
    }

    override suspend fun participate(challengeId: Long): ResultState<Participation> = safeApiCall {
        api.participate(challengeId).unwrap().toDomain()
    }

    override suspend fun leaveChallenge(challengeId: Long): ResultState<Participation> = safeApiCall {
        api.leaveChallenge(challengeId).unwrap().toDomain()
    }

    override suspend fun getMyChallenges(
        category: ChallengeCategory?,
        keyword: String?,
    ): ResultState<List<MyChallenge>> = safeApiCall {
        api.getMyChallenges(category = category?.name, keyword = keyword).unwrap().toDomain()
    }
}

private fun <T> ApiResponse<T>.unwrap(): T {
    val result = result
    if (!isSuccess || result == null) throw ApiException(message)
    return result
}
