package com.li_routi.core.data.network.dto.response

/**
 * GET /api/achievements 응답. 실제 `summary`는 `{acquiredCount, totalCount, inProgressCount,
 * specialAcquiredCount, specialTotalCount}` 모양으로 내려온다(스웨거 문서의 예시 스키마는 챌린지
 * 응답과 뒤섞인 오류였다) — 그런데도 매핑하지 않는 이유는 화면이 등급 필터(레어/에픽/유니크)를 적용한
 * "필터링된" 개수를 목록에서 직접 세서 보여주는 구조라, 필터와 무관한 서버 전체 합계를 쓰면 오히려
 * 필터링해도 개수가 안 바뀌는 퇴보가 생기기 때문이다. 필터와 무관한 전체 요약을 화면에 추가하게 되면
 * 그때 매핑한다 — Gson은 알 수 없는 필드를 무시하니 지금 안 써도 파싱엔 문제없다.
 */
data class AchievementsResponse(
    val categories: List<AchievementCategoryResponse>,
)
