package com.li_routi.core.domain.notification

/**
 * 알림 탭/푸시 탭 시 이동할 화면. [AppNotification.type]으로 결정한다.
 *
 * 서버 스웨거(`GET /api/notifications`)의 `type` 필드 설명("화면 이동·아이콘 분기에 사용")을
 * 기준으로 매핑한다. `referenceId`가 챌린지/인증 등 어떤 대상을 가리키는지는 타입마다 달라서
 * (예: CHALLENGE_VERIFICATION_LIKED는 인증 게시물 id, CHALLENGE_CYCLE_STARTED는 챌린지 id로 추정),
 * 챌린지 id라고 확신할 수 있는 타입만 [ChallengeDetail]로 보내고 나머지는 [ChallengeHome]으로 보낸다.
 */
sealed interface NotificationNavigationTarget {
    data object PersonalRoutine : NotificationNavigationTarget
    data object GroupRoutine : NotificationNavigationTarget
    data object ChallengeHome : NotificationNavigationTarget
    data class ChallengeDetail(val challengeId: Long) : NotificationNavigationTarget
}

private val PersonalRoutineTypes = setOf(
    "PERSONAL_ROUTINE_REMINDER",
    "PERSONAL_ROUTINE_DEADLINE",
    "PERSONAL_ROUTINE_MISSED",
)

/** referenceId가 챌린지 자체를 가리키는 것으로 확신할 수 있는 타입(회차 시작/참여 제한). */
private val ChallengeLevelTypes = setOf(
    "CHALLENGE_CYCLE_STARTED",
    "CHALLENGE_RESTRICTED",
)

/** referenceId가 인증 게시물 등 챌린지 하위 대상을 가리켜 챌린지 id를 알 수 없는 타입. */
private val ChallengeSubEntityTypes = setOf(
    "CHALLENGE_VERIFICATION_LIKED",
    "CHALLENGE_REVIEW_APPROVED",
    "CHALLENGE_REVIEW_REJECTED",
)

private val GroupRoutineTypes = setOf(
    "GROUP_MEMBER_JOINED",
    "GROUP_VERIFICATION_LIKED",
    "GROUP_VERIFICATION_DISAPPOINTED",
    "GROUP_MEMBER_POKED",
    "GROUP_ROUTINE_UPDATED",
    "GROUP_CHAT_MESSAGE",
    "GROUP_ROUTINE_STARTED",
    "GROUP_ROUTINE_DEADLINE",
    "GROUP_ROUTINE_ENDED",
    "GROUP_MEMBER_VERIFIED",
)

fun resolveNotificationNavigationTarget(
    type: String?,
    referenceId: Long?,
): NotificationNavigationTarget? = when (type) {
    in PersonalRoutineTypes -> NotificationNavigationTarget.PersonalRoutine
    in ChallengeLevelTypes ->
        referenceId?.let { NotificationNavigationTarget.ChallengeDetail(it) }
            ?: NotificationNavigationTarget.ChallengeHome
    in ChallengeSubEntityTypes -> NotificationNavigationTarget.ChallengeHome
    in GroupRoutineTypes -> NotificationNavigationTarget.GroupRoutine
    else -> null
}
