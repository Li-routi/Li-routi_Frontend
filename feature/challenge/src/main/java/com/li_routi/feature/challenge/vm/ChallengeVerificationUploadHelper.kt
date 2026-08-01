package com.li_routi.feature.challenge.vm

import android.content.Context
import android.net.Uri
import com.li_routi.core.common.kotlin.util.ResultState
import com.li_routi.core.data.di.ChallengeContainer
import com.li_routi.core.data.di.MediaContainer
import com.li_routi.core.domain.media.MediaPurpose
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 촬영한 사진 → 챌린지 인증 게시글 작성.
 *
 * 1) [MediaContainer.uploadMediaUseCase]로 presigned 업로드 → mediaKey 발급
 * 2) [ChallengeContainer.createVerificationUseCase]로 mediaKey + 코멘트를 제출
 */
internal suspend fun submitChallengeVerification(
    challengeId: Long,
    photoUri: Uri,
    content: String,
    context: Context,
): Result<Unit> = withContext(Dispatchers.IO) {
    val contentType = context.contentResolver.getType(photoUri) ?: "image/jpeg"
    val bytes = context.contentResolver.openInputStream(photoUri)?.use { it.readBytes() }
        ?: return@withContext Result.failure(IllegalArgumentException("사진을 읽을 수 없습니다."))

    when (
        val uploadResult = MediaContainer.uploadMediaUseCase(
            purpose = MediaPurpose.CHALLENGE_VERIFICATION,
            contentType = contentType,
            bytes = bytes,
        )
    ) {
        is ResultState.Success -> {
            when (
                val createResult = ChallengeContainer.createVerificationUseCase(
                    challengeId = challengeId,
                    mediaKey = uploadResult.data,
                    content = content.ifBlank { null },
                )
            ) {
                is ResultState.Success -> Result.success(Unit)
                is ResultState.Error -> Result.failure(IllegalStateException(createResult.message))
                ResultState.Loading -> Result.failure(IllegalStateException("등록이 완료되지 않았습니다."))
            }
        }
        is ResultState.Error -> Result.failure(IllegalStateException(uploadResult.message))
        ResultState.Loading -> Result.failure(IllegalStateException("업로드가 완료되지 않았습니다."))
    }
}
