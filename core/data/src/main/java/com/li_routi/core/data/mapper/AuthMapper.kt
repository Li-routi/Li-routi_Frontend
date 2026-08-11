package com.li_routi.core.data.mapper

import com.li_routi.core.data.network.dto.response.GoogleNonceResponse
import com.li_routi.core.data.network.dto.response.MemberVerificationResponse
import com.li_routi.core.data.network.dto.response.MemberVerificationsResponse
import com.li_routi.core.data.network.dto.response.MyInfoResponse
import com.li_routi.core.data.network.dto.response.TokenResponse
import com.li_routi.core.domain.auth.AuthToken
import com.li_routi.core.domain.auth.MyInfo
import com.li_routi.core.domain.auth.MyVerificationDay
import com.li_routi.core.domain.auth.MyVerificationEntry
import com.li_routi.core.domain.auth.SocialProvider
import com.li_routi.core.domain.auth.VerificationReviewStatus
import com.li_routi.core.domain.auth.VerificationSourceType

fun TokenResponse.toDomain(): AuthToken = AuthToken(
    accessToken = accessToken,
    refreshToken = refreshToken,
    accessTokenExpiresIn = accessTokenExpiresIn,
    onboardingCompleted = onboardingCompleted,
)

fun GoogleNonceResponse.toDomain(): String = nonce

fun MyInfoResponse.toDomain(): MyInfo = MyInfo(
    memberId = memberId,
    email = email,
    nickname = nickname,
    profileImageUrl = profileImageUrl,
    socialProvider = socialProvider.toSocialProvider(),
    onboardingCompleted = onboardingCompleted,
)

private fun String.toSocialProvider(): SocialProvider =
    runCatching { SocialProvider.valueOf(this) }.getOrDefault(SocialProvider.Unknown)

fun MemberVerificationsResponse.toDomain(): MyVerificationDay = MyVerificationDay(
    date = date,
    verifications = verifications.map { it.toDomain() },
)

fun MemberVerificationResponse.toDomain(): MyVerificationEntry = MyVerificationEntry(
    verificationId = verificationId,
    sourceType = sourceType.toVerificationSourceType(),
    categoryName = categoryName,
    title = title,
    content = content,
    imageUrl = imageUrl,
    verifiedAt = verifiedAt,
    reviewStatus = reviewStatus?.toVerificationReviewStatusOrNull(),
)

// 화면이 sourceType으로 분기하지 않아 서버가 새 값을 추가해도 그 항목만 조용히 UNKNOWN으로 떨어질 뿐,
// valueOf()처럼 날짜 전체 조회를 실패시키지 않는다.
private fun String.toVerificationSourceType(): VerificationSourceType =
    runCatching { VerificationSourceType.valueOf(this) }.getOrDefault(VerificationSourceType.UNKNOWN)

// 문서상 PENDING/APPROVED만 오는 게 맞지만, 서버가 새 값을 추가해도 그 항목 하나 때문에 날짜 전체
// 조회가 실패하지 않도록 알 수 없는 값은 "심사 상태 없음"(null)으로 취급한다.
private fun String.toVerificationReviewStatusOrNull(): VerificationReviewStatus? =
    runCatching { VerificationReviewStatus.valueOf(this) }.getOrNull()
