package com.li_routi.feature.grouproutine.vm

/**
 * 서버가 "답장" 전용 필드를 지원하지 않아, 원본 메시지 id를 실제 본문 앞에 특수 구분자로 실어
 * 보내는 방식으로 흉내낸다. 일반 채팅에 나올 일이 거의 없는 문자(§, section sign)를 마커/구분자로
 * 써서 본문 내용과 충돌할 가능성을 낮춘다.
 *
 * 보낸 사람 닉네임/미리보기 텍스트는 여기 함께 싣지 않는다 — 그 값은 "보내는" 클라이언트가
 * 자유롭게 채워 넣는 값이라, 받는 쪽이 그대로 믿고 보여주면 변조된 클라이언트가 가짜 인용문(예:
 * 실제로 하지 않은 말을 한 것처럼 보이게)을 위조해 보낼 수 있다(CodeRabbit 리뷰 지적). 대신
 * [replyToMessageId]만 신뢰하고, 실제 보낸 사람/미리보기는 수신 측이 이미 받은 메시지 목록에서
 * 그 id를 찾아 진짜 내용으로 채운다
 * (com.li_routi.feature.grouproutine.component.resolveReplyPreview 참고) — id를 찾지 못하면
 * (아직 안 불러온 과거 메시지 등) 검증할 수 없으므로 인용 없이 일반 텍스트로 보여준다.
 */
private const val ReplyContentPrefix = "§R§"
private const val ReplyContentDelimiter = "§"

/** [String.parseReplyContent]의 결과. 답장이 아니면 [replyToMessageId]가 null이다. */
internal data class ParsedChatContent(
    val body: String,
    val replyToMessageId: Long?,
)

/** 답장으로 보낼 [body] 앞에 원본 메시지 id를 인코딩해 붙인다. */
internal fun encodeReplyContent(replyToMessageId: Long, body: String): String =
    "$ReplyContentPrefix$replyToMessageId$ReplyContentDelimiter$body"

/**
 * [encodeReplyContent]로 인코딩된 문자열을 되돌린다. 그 형식이 아니면(옛날 방식으로 보낸
 * "[답장] 이름: 미리보기\n본문" 포함) 일반 텍스트로 취급한다 — 옛날 방식은 원본 id가 없어
 * 검증할 방법이 없으므로, 보낸 사람/미리보기를 파싱해 인용처럼 보여주지 않는다(같은 문제로
 * CodeRabbit이 지적한 것과 동일한 이유).
 */
internal fun String.parseReplyContent(): ParsedChatContent {
    if (startsWith(ReplyContentPrefix)) {
        val parts = removePrefix(ReplyContentPrefix).split(ReplyContentDelimiter, limit = 2)
        if (parts.size == 2) {
            val (idPart, body) = parts
            val id = idPart.toLongOrNull()
            if (id != null) {
                return ParsedChatContent(body = body, replyToMessageId = id)
            }
        }
    }
    return ParsedChatContent(body = this, replyToMessageId = null)
}
