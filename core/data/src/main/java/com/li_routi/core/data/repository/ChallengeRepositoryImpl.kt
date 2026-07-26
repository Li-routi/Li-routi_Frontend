package com.li_routi.core.data.repository

import com.li_routi.core.common.kotlin.util.ApiException
import com.li_routi.core.common.kotlin.util.ResultState
import com.li_routi.core.common.kotlin.util.safeApiCall
import com.li_routi.core.data.mapper.toDomain
import com.li_routi.core.data.network.service.ChallengeApiService
import com.li_routi.core.domain.challenge.ChallengeCategory
import com.li_routi.core.domain.challenge.ChallengePage
import com.li_routi.core.domain.challenge.ChallengeRepository

class ChallengeRepositoryImpl(
    private val api: ChallengeApiService,
) : ChallengeRepository {

    override suspend fun getChallenges(
        category: ChallengeCategory?,
        keyword: String?,
        cursor: Long?,
        size: Int?,
    ): ResultState<ChallengePage> = safeApiCall {
        val response = api.getChallenges(
            category = category?.name,
            keyword = keyword,
            cursor = cursor,
            size = size,
        )
        val result = response.result
        if (!response.isSuccess || result == null) {
            throw ApiException(response.message)
        }
        result.toDomain()
    }
}
