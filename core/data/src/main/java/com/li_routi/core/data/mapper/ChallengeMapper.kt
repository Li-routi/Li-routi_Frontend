package com.li_routi.core.data.mapper

import com.li_routi.core.data.network.dto.response.ChallengeDetailResponse
import com.li_routi.core.data.network.dto.response.ChallengeListingResponse
import com.li_routi.core.data.network.dto.response.ChallengeSummaryResponse
import com.li_routi.core.data.network.dto.response.CreateVerificationResponse
import com.li_routi.core.data.network.dto.response.LikeResponse
import com.li_routi.core.data.network.dto.response.MyChallengeListingResponse
import com.li_routi.core.data.network.dto.response.MyChallengeSummaryResponse
import com.li_routi.core.data.network.dto.response.MyVerificationFeedResponse
import com.li_routi.core.data.network.dto.response.MyVerificationResponse
import com.li_routi.core.data.network.dto.response.ParticipationResponse
import com.li_routi.core.data.network.dto.response.VerificationFeedResponse
import com.li_routi.core.data.network.dto.response.VerificationResponse
import com.li_routi.core.domain.challenge.Certification
import com.li_routi.core.domain.challenge.CertificationPage
import com.li_routi.core.domain.challenge.Challenge
import com.li_routi.core.domain.challenge.ChallengeCategory
import com.li_routi.core.domain.challenge.ChallengeDetail
import com.li_routi.core.domain.challenge.ChallengePage
import com.li_routi.core.domain.challenge.CreatedVerification
import com.li_routi.core.domain.challenge.LikeResult
import com.li_routi.core.domain.challenge.MyCertification
import com.li_routi.core.domain.challenge.MyCertificationPage
import com.li_routi.core.domain.challenge.MyChallenge
import com.li_routi.core.domain.challenge.Participation
import com.li_routi.core.domain.challenge.RoutineCycle

fun ChallengeSummaryResponse.toDomain(): Challenge = Challenge(
    id = challengeId,
    name = name,
    description = description,
    imageUrl = imageUrl.orEmpty(),
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
    imageUrl = imageUrl.orEmpty(),
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
    imageUrl = imageUrl.orEmpty(),
    verifiedAt = verifiedAt,
    likeCount = likeCount,
    liked = liked,
)

fun VerificationFeedResponse.toDomain(): CertificationPage = CertificationPage(
    certifications = verifications.map { it.toDomain() },
    nextCursor = nextCursor,
    hasNext = hasNext,
)

fun MyVerificationResponse.toDomain(): MyCertification = MyCertification(
    id = verificationId,
    content = content,
    imageUrl = imageUrl.orEmpty(),
    verifiedAt = verifiedAt,
    likeCount = likeCount,
)

fun MyVerificationFeedResponse.toDomain(): MyCertificationPage = MyCertificationPage(
    certifications = verifications.map { it.toDomain() },
    currentStreak = currentStreak,
    nextCursor = nextCursor,
    hasNext = hasNext,
)

fun LikeResponse.toDomain(): LikeResult = LikeResult(
    verificationId = verificationId,
    likeCount = likeCount,
    liked = liked,
)

fun CreateVerificationResponse.toDomain(): CreatedVerification = CreatedVerification(
    verificationId = verificationId,
    imageUrl = imageUrl,
    content = content,
    currentStreak = currentStreak,
    reverified = reverified,
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
    imageUrl = imageUrl.orEmpty(),
    category = ChallengeCategory.valueOf(category),
)

fun MyChallengeListingResponse.toDomain(): List<MyChallenge> = challenges.map { it.toDomain() }
