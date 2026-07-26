package com.li_routi.core.data.mapper

import com.li_routi.core.data.network.dto.response.ChallengeDetailResponse
import com.li_routi.core.data.network.dto.response.ChallengeListingResponse
import com.li_routi.core.data.network.dto.response.ChallengeSummaryResponse
import com.li_routi.core.data.network.dto.response.MyChallengeListingResponse
import com.li_routi.core.data.network.dto.response.MyChallengeSummaryResponse
import com.li_routi.core.data.network.dto.response.ParticipationResponse
import com.li_routi.core.data.network.dto.response.VerificationFeedResponse
import com.li_routi.core.data.network.dto.response.VerificationResponse
import com.li_routi.core.domain.challenge.Certification
import com.li_routi.core.domain.challenge.CertificationPage
import com.li_routi.core.domain.challenge.Challenge
import com.li_routi.core.domain.challenge.ChallengeCategory
import com.li_routi.core.domain.challenge.ChallengeDetail
import com.li_routi.core.domain.challenge.ChallengePage
import com.li_routi.core.domain.challenge.MyChallenge
import com.li_routi.core.domain.challenge.Participation
import com.li_routi.core.domain.challenge.RoutineCycle

fun ChallengeSummaryResponse.toDomain(): Challenge = Challenge(
    id = challengeId,
    name = name,
    description = description,
    imageUrl = imageUrl,
    category = ChallengeCategory.valueOf(category),
    routineCycle = RoutineCycle.valueOf(routineCycle),
    reward = reward,
    participantCount = participantCount,
    verificationPostCount = verificationPostCount,
)

fun ChallengeListingResponse.toDomain(): ChallengePage = ChallengePage(
    challenges = challenges.map { it.toDomain() },
    nextCursor = nextCursor,
    hasNext = hasNext,
)

fun ChallengeDetailResponse.toDomain(): ChallengeDetail = ChallengeDetail(
    id = challengeId,
    name = name,
    description = description,
    imageUrl = imageUrl,
    category = ChallengeCategory.valueOf(category),
    routineCycle = RoutineCycle.valueOf(routineCycle),
    reward = reward,
    participating = participating,
    participantCount = participantCount,
    verificationPostCount = verificationPostCount,
    todayCompletionCount = todayCompletionCount,
)

fun VerificationResponse.toDomain(): Certification = Certification(
    id = verificationId,
    authorName = nickname,
    content = content,
    imageUrl = imageUrl,
    verifiedAt = verifiedAt,
)

fun VerificationFeedResponse.toDomain(): CertificationPage = CertificationPage(
    certifications = verifications.map { it.toDomain() },
    nextCursor = nextCursor,
    hasNext = hasNext,
)

fun ParticipationResponse.toDomain(): Participation = Participation(
    challengeId = challengeId,
    participating = participating,
    participationRound = participationRound,
)

fun MyChallengeSummaryResponse.toDomain(): MyChallenge = MyChallenge(
    id = challengeId,
    name = name,
    description = description,
    imageUrl = imageUrl,
    category = ChallengeCategory.valueOf(category),
)

fun MyChallengeListingResponse.toDomain(): List<MyChallenge> = challenges.map { it.toDomain() }
