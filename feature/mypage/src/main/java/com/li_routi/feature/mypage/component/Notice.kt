package com.li_routi.feature.mypage.component

/**
 * 공지사항 한 건. Figma node `6008:16123`/`6008:17900` 기준.
 *
 * 공지 API가 아직 없어(백엔드 범위 밖) [SampleNotices]에 고정 데이터로 둔다 — 나중에 서버가
 * 생기면 이 모델은 그대로 두고 [SampleNotices] 자리만 API 응답 매핑으로 바꾸면 된다.
 */
data class NoticeUiModel(
    val id: String,
    val title: String,
    val date: String,
    val content: String,
)

val SampleNotices: List<NoticeUiModel> = listOf(
    NoticeUiModel(
        id = "notice_open",
        title = "[공지] 리루티 서비스 오픈 안내",
        date = "2026. 08. 21",
        content = """
            안녕하세요, 리루티입니다.
            서로의 하루가 서로의 루틴을 이어주는 리루티가 서비스를 시작합니다.

            리루티에서는 나만의 루틴을 만들거나 모임과 챌린지에 참여해 서로의 꾸준한 실천을 응원할 수 있습니다. 루틴을 하나씩 완료하며 쌓이는 기록과 캐릭터의 변화도 함께 확인해 보세요.

            서비스를 이용하며 불편한 점이나 개선이 필요한 부분이 있다면 건의하기를 통해 의견을 남겨 주세요. 보내주신 의견을 바탕으로 더 편리한 서비스를 만들어가겠습니다.

            앞으로 리루티와 함께 즐거운 루틴을 이어가 주세요.

            감사합니다.
        """.trimIndent(),
    ),
    NoticeUiModel(
        id = "notice_bugfix",
        title = "[업데이트] 서비스 오류 수정 안내",
        date = "2026. 08. 21",
        content = """
            안녕하세요, 리루티입니다.
            더욱 안정적인 서비스 이용을 위해 일부 오류를 수정했습니다.

            수정된 내용

            • 루틴 인증 완료 후 진행 상태가 바로 반영되지 않던 현상 수정
            • 모임방의 새 인증 알림을 눌렀을 때 화면이 정상적으로 열리지 않던 현상 수정
            • 오늘의 루틴을 모두 완료한 후 캐릭터 안내가 갱신되지 않던 현상 수정
            • 일부 기기에서 버튼과 문구가 화면을 벗어나 표시되던 현상 개선

            원활한 서비스 이용을 위해 앱을 최신 상태로 업데이트해 주세요.

            이용에 불편을 드려 죄송합니다. 앞으로도 안정적인 서비스를 제공할 수 있도록 노력하겠습니다.

            감사합니다.
        """.trimIndent(),
    ),
)
