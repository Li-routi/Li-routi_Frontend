package com.li_routi.feature.challenge.vm

import com.li_routi.core.domain.challenge.ChallengeCategory
import com.li_routi.core.domain.challenge.RoutineCycle
import com.li_routi.core.domain.challenge.VerificationSort

/** 챌린지 화면들에서 공통으로 쓰는 분류/주기 표시 라벨 매핑. */
internal fun ChallengeCategory.toDisplayLabel(): String = when (this) {
    ChallengeCategory.HEALTH -> "건강"
    ChallengeCategory.EXERCISE -> "운동"
    ChallengeCategory.STUDY -> "공부"
    ChallengeCategory.LIFE -> "생활"
    ChallengeCategory.HOBBY -> "취미"
    // 서버가 아직 앱이 모르는 카테고리를 내려준 경우(ChallengeMapper.toChallengeCategory 참고) —
    // 크래시하거나 엉뚱한 기존 카테고리로 보이는 대신 "기타"로 명시적으로 표시한다.
    ChallengeCategory.UNKNOWN -> "기타"
}

internal fun RoutineCycle.toBadgeLabel(): String = when (this) {
    RoutineCycle.DAILY -> "매일 루틴"
    RoutineCycle.WEEKLY -> "주간 루틴"
    RoutineCycle.MONTHLY -> "월간 루틴"
    RoutineCycle.UNKNOWN -> "루틴"
}

internal fun VerificationSort.toDisplayLabel(): String = when (this) {
    VerificationSort.LATEST -> "최신순"
    VerificationSort.LIKES -> "인기순"
}
