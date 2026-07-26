package com.li_routi.core.data.mapper

import com.li_routi.core.data.network.dto.response.ChallengeListingResponse
import com.li_routi.core.data.network.dto.response.ChallengeSummaryResponse
import com.li_routi.core.domain.challenge.Challenge
import com.li_routi.core.domain.challenge.ChallengeCategory
import com.li_routi.core.domain.challenge.ChallengePage
import com.li_routi.core.domain.challenge.RoutineCycle

fun ChallengeSummaryResponse.toDomain(): Challenge = Challenge(
    id = challengeId,
    name = name,
    description = description,
    imageUrl = imageUrl,
    category = ChallengeCategory.valueOf(category),
    routineCycle = RoutineCycle.valueOf(routineCycle),
    participantCount = participantCount,
    verificationPostCount = verificationPostCount,
)

fun ChallengeListingResponse.toDomain(): ChallengePage = ChallengePage(
    challenges = challenges.map { it.toDomain() },
    nextCursor = nextCursor,
    hasNext = hasNext,
)
