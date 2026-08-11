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
import com.li_routi.core.data.network.dto.response.UpdateVerificationMemoResponse
import com.li_routi.core.data.network.dto.response.VerificationFeedResponse
import com.li_routi.core.data.network.dto.response.VerificationResponse
import com.li_routi.core.domain.challenge.Certification
import com.li_routi.core.domain.challenge.CertificationPage
import com.li_routi.core.domain.challenge.Challenge
import com.li_routi.core.domain.challenge.ChallengeCategory
import com.li_routi.core.domain.challenge.ChallengeDetail
import com.li_routi.core.domain.challenge.ChallengePage
import com.li_routi.core.domain.challenge.CreatedVerification
import com.li_routi.core.domain.challenge.EditedVerification
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
    category = category.toChallengeCategory(),
    routineCycle = routineCycle.toRoutineCycle(),
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
    category = category.toChallengeCategory(),
    routineCycle = routineCycle.toRoutineCycle(),
    reward = reward,
    participating = participating,
    participantCount = participantCount,
    verificationPostCount = verificationPostCount,
    verifiedInCurrentPeriod = verifiedInCurrentPeriod,
)

fun VerificationResponse.toDomain(): Certification = Certification(
    id = verificationId,
    authorName = nickname,
    content = content.orEmpty(),
    imageUrl = imageUrl.orEmpty(),
    verifiedAt = verifiedAt,
    likeCount = likeCount,
    liked = liked,
    isMine = mine,
)

fun VerificationFeedResponse.toDomain(): CertificationPage = CertificationPage(
    certifications = verifications.map { it.toDomain() },
    nextCursor = nextCursor,
    nextCursorLikeCount = nextCursorLikeCount,
    hasNext = hasNext,
)

fun MyVerificationResponse.toDomain(): MyCertification = MyCertification(
    id = verificationId,
    content = content.orEmpty(),
    imageUrl = imageUrl.orEmpty(),
    verifiedDate = verifiedDate,
    verifiedAt = verifiedAt,
    likeCount = likeCount,
)

fun MyVerificationFeedResponse.toDomain(): MyCertificationPage = MyCertificationPage(
    certifications = verifications.map { it.toDomain() },
    currentStreak = currentStreak,
    nextCursor = nextCursor,
    nextCursorLikeCount = nextCursorLikeCount,
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
    content = content.orEmpty(),
    currentStreak = currentStreak,
    reverified = reverified,
)

fun UpdateVerificationMemoResponse.toDomain(): EditedVerification = EditedVerification(
    verificationId = verificationId,
    content = content.orEmpty(),
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
    category = category.toChallengeCategory(),
)

fun MyChallengeListingResponse.toDomain(): List<MyChallenge> = challenges.map { it.toDomain() }

/**
 * 서버가 새 카테고리를 추가했는데 앱이 아직 대응하지 못한 경우를 대비한 방어 코드.
 * 임의의 기존 값으로 대체하지 않고 [ChallengeCategory.UNKNOWN]으로 명시적으로 남겨서, "정말 그
 * 카테고리"와 "매핑 실패"를 화면에서 구분할 수 있게 한다([ChallengeLabelMapper] 참고).
 */
private fun String.toChallengeCategory(): ChallengeCategory =
    runCatching { ChallengeCategory.valueOf(this) }.getOrDefault(ChallengeCategory.UNKNOWN)

/**
 * 서버가 새 인증 주기를 추가했는데 앱이 아직 대응하지 못한 경우를 대비한 방어 코드.
 * [RoutineCycle.UNKNOWN] 참고 — [toChallengeCategory]와 같은 이유다.
 */
private fun String.toRoutineCycle(): RoutineCycle =
    runCatching { RoutineCycle.valueOf(this) }.getOrDefault(RoutineCycle.UNKNOWN)
