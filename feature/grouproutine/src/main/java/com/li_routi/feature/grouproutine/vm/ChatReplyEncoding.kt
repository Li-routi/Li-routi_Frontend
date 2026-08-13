package com.li_routi.feature.grouproutine.vm

/**
 * 서버가 "답장" 전용 필드를 지원하지 않아, 원본 메시지 정보(id/보낸 사람/미리보기)를 실제 본문
 * 앞에 특수 구분자로 실어 보내는 방식으로 흉내낸다. 일반 텍스트에 나올 일이 거의 없는 제어
 * 문자(U+0001)를 구분자로 써서 본문 내용과 충돌하지 않게 한다.
 */
private const val ReplyContentPrefix = "R"
private const val ReplyContentDelimiter = ""

/** 원본 메시지가 이모티콘이라 텍스트 미리보기가 없을 때 대신 보여줄 라벨. */
internal const val EmojiReplyPreviewLabel = "이모티콘"

/** [String.parseReplyContent]의 결과. 답장이 아니면 replyTo* 필드가 전부 null이다. */
internal data class ParsedChatContent(
    val body: String,
    val replyToMessageId: Long?,
    val replySenderName: String?,
    val replyPreview: String?,
)

/** 답장으로 보낼 [body] 앞에 원본 메시지 정보를 인코딩해 붙인다. */
internal fun encodeReplyContent(
    replyToMessageId: Long,
    senderName: String,
    preview: String,
    body: String,
): String = buildString {
    append(ReplyContentPrefix)
    append(replyToMessageId)
    append(ReplyContentDelimiter)
    append(senderName)
    append(ReplyContentDelimiter)
    append(preview)
    append(ReplyContentDelimiter)
    append(body)
}

/**
 * [encodeReplyContent]로 인코딩된 문자열을 되돌린다. 그 형식이 아니면 예전 방식
 * ("[답장] 이름: 미리보기\n본문")으로 보낸 메시지인지 최선으로 해석하고, 그것도 아니면
 * 일반 텍스트로 취급한다(replyTo* 필드는 전부 null).
 */
internal fun String.parseReplyContent(): ParsedChatContent {
    if (startsWith(ReplyContentPrefix)) {
        val parts = removePrefix(ReplyContentPrefix).split(ReplyContentDelimiter, limit = 4)
        if (parts.size == 4) {
            val (idPart, sender, preview, body) = parts
            return ParsedChatContent(
                body = body,
                replyToMessageId = idPart.toLongOrNull(),
                replySenderName = sender,
                replyPreview = preview,
            )
        }
    }
    val legacyMatch = LegacyReplyPattern.matchEntire(this)
    if (legacyMatch != null) {
        val (sender, preview, body) = legacyMatch.destructured
        return ParsedChatContent(body = body, replyToMessageId = null, replySenderName = sender, replyPreview = preview)
    }
    return ParsedChatContent(body = this, replyToMessageId = null, replySenderName = null, replyPreview = null)
}

private val LegacyReplyPattern = Regex("^\\[답장] (.+?): (.*?)\\n([\\s\\S]*)$")
