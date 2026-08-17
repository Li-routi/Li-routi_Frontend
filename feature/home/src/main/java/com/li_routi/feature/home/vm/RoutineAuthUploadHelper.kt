package com.li_routi.feature.home.vm

import android.content.Context
import android.net.Uri
import com.li_routi.core.common.kotlin.util.ResultState
import com.li_routi.core.data.di.ChallengeContainer
import com.li_routi.core.data.di.MediaContainer
import com.li_routi.core.data.di.RoutineContainer
import com.li_routi.core.domain.media.MediaPurpose
import com.li_routi.core.domain.routine.GroupRoutineTarget
import com.li_routi.feature.home.component.RoutineChecklistItemUiModel
import com.li_routi.feature.home.component.RoutineChecklistKind
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** 홈 체크리스트 개인 루틴 id 접두사. [HomeUiMapper]와 동일. */
internal const val MemberRoutineIdPrefix = "my_"

/** 홈 체크리스트 그룹 루틴 id: `group_{groupId}_{routineId}`. */
private val GroupRoutineIdPattern = Regex("""^group_(\d+)_(\d+)$""")

/** 챌린지 id 접두사: `challenge_{challengeId}`. `app` 모듈이 프리셀렉트 id를 만들 때도 재사용한다. */
const val ChallengeIdPrefix = "challenge_"

internal fun parseMemberRoutineId(checklistId: String): Long? {
    if (!checklistId.startsWith(MemberRoutineIdPrefix)) return null
    return checklistId.removePrefix(MemberRoutineIdPrefix).toLongOrNull()
}

internal fun parseGroupRoutineTarget(checklistId: String): GroupRoutineTarget? {
    val match = GroupRoutineIdPattern.matchEntire(checklistId) ?: return null
    return GroupRoutineTarget(
        groupId = match.groupValues[1].toLong(),
        routineId = match.groupValues[2].toLong(),
    )
}

internal fun parseChallengeId(checklistId: String): Long? {
    if (!checklistId.startsWith(ChallengeIdPrefix)) return null
    return checklistId.removePrefix(ChallengeIdPrefix).toLongOrNull()
}

/** 다른 모듈(예: app)에서도 홈 체크리스트를 인증 선택 목록으로 변환할 수 있도록 공개한다. */
fun List<RoutineChecklistItemUiModel>.toAuthSelectables(): List<RoutineAuthSelectableUiModel> =
    filter { it.canVerify }
        .map { item ->
            RoutineAuthSelectableUiModel(
                id = item.id,
                title = item.title,
                dueLabel = item.dueLabel.takeIf { it.isNotBlank() },
                subtitle = item.roomLabel,
                categoryLabel = item.categoryLabel,
                badgeTone = when (item.kind) {
                    RoutineChecklistKind.Member -> RoutineAuthBadgeTone.Secondary
                    RoutineChecklistKind.Group -> RoutineAuthBadgeTone.Secondary
                },
                categoryColor = item.categoryColor,
                memberRoutineId = when (item.kind) {
                    RoutineChecklistKind.Member -> item.routineId
                    RoutineChecklistKind.Group -> null
                },
                groupId = item.groupId,
                groupRoutineId = when (item.kind) {
                    RoutineChecklistKind.Group -> item.routineId
                    RoutineChecklistKind.Member -> null
                },
            )
        }

/**
 * 촬영 URI → 개인/그룹 루틴 + 챌린지 인증.
 *
 * purpose가 달라 종류를 섞어 고르면 미디어 업로드를 종류별로 1회씩 수행한다. 챌린지는 서버(AI)가
 * 주제 적합성을 판정해 등록을 거부할 수 있으므로 **항상 먼저** 처리한다: 챌린지가 하나라도 섞여
 * 있으면 미디어를 1회 업로드해 [ChallengeContainer.createVerificationUseCase]를 챌린지별로 반복
 * 호출하고, 그중 하나라도 실패하면 즉시 실패를 반환해 개인/그룹 루틴 업로드는 시도조차 하지 않는다.
 * 선택된 챌린지가 모두 통과했을 때만 개인·그룹 루틴을 [RoutineContainer.submitRoutineAuthUseCase]로
 * 업로드한다(같은 사진 바이트, 같은 메모).
 */
internal suspend fun submitRoutineAuthUpload(
    photoUri: Uri,
    memo: String,
    selectedRoutineIds: Set<String>,
    routines: List<RoutineAuthSelectableUiModel>,
    context: Context,
): Result<Unit> = withContext(Dispatchers.IO) {
    val selected = routines.filter { it.id in selectedRoutineIds }
    val memberIds = selected.mapNotNull { item ->
        item.memberRoutineId ?: parseMemberRoutineId(item.id)
    }.distinct()
    val groupTargets = selected.mapNotNull { item ->
        val groupId = item.groupId
        val routineId = item.groupRoutineId
        if (groupId != null && routineId != null) {
            GroupRoutineTarget(groupId = groupId, routineId = routineId)
        } else {
            parseGroupRoutineTarget(item.id)
        }
    }.distinct()
    val challengeIds = selected.mapNotNull { item ->
        item.challengeId ?: parseChallengeId(item.id)
    }.distinct()
    if (memberIds.isEmpty() && groupTargets.isEmpty() && challengeIds.isEmpty()) {
        return@withContext Result.failure(
            IllegalArgumentException("인증할 항목을 선택해 주세요."),
        )
    }
    // Figma `촬영 후 메모/선택`(3610:26875) 328:184 비율에 맞춰 중앙 크롭한 뒤 JPEG로 재인코딩해서
    // 업로드한다 — 미리보기(CapturedPhotoPreview)와 서버로 나가는 실제 바이트가 항상 같은 크롭 결과를 갖는다.
    // decode/crop/encode는 전부 네이티브 비트맵 메모리를 다뤄 OutOfMemoryError 등을 던질 수 있어
    // runCatching으로 감싼다 — 미리보기 쪽(RoutineAuthUploadScreen)과 동일하게 예외가 앱을 죽이지 않고
    // 업로드 실패로 처리되게 한다.
    val bytes = runCatching {
        val bitmap = decodeBitmapWithExif(context, photoUri, VerificationPhotoUploadTargetSizePx)
            ?: error("사진을 디코딩할 수 없습니다.")
        bitmap.rotateToLandscapeIfPortrait()
            .centerCropToRatio(VerificationPhotoAspectRatio, VerificationPhotoTopCropBias)
            .toJpegBytes()
    }.getOrElse {
        return@withContext Result.failure(IllegalArgumentException("사진을 읽을 수 없습니다.", it))
    }
    val contentType = "image/jpeg"
    // 챌린지 인증과 동일: 빈 코멘트는 null로 보내고, 값이 있으면 content로 저장.
    val content = memo.trim().ifBlank { null }

    if (challengeIds.isNotEmpty()) {
        val challengeMediaKey = when (
            val uploaded = MediaContainer.uploadMediaUseCase(
                purpose = MediaPurpose.CHALLENGE_VERIFICATION,
                contentType = contentType,
                bytes = bytes,
            )
        ) {
            is ResultState.Success -> uploaded.data
            is ResultState.Error -> return@withContext Result.failure(IllegalStateException(uploaded.message))
            ResultState.Loading ->
                return@withContext Result.failure(IllegalStateException("업로드가 완료되지 않았습니다."))
        }
        for (challengeId in challengeIds) {
            when (
                val created = ChallengeContainer.createVerificationUseCase(
                    challengeId = challengeId,
                    mediaKey = challengeMediaKey,
                    content = content,
                )
            ) {
                is ResultState.Success -> Unit
                is ResultState.Error -> return@withContext Result.failure(IllegalStateException(created.message))
                ResultState.Loading ->
                    return@withContext Result.failure(IllegalStateException("등록이 완료되지 않았습니다."))
            }
        }
    }

    if (memberIds.isNotEmpty() || groupTargets.isNotEmpty()) {
        when (
            val result = RoutineContainer.submitRoutineAuthUseCase(
                contentType = contentType,
                bytes = bytes,
                content = content,
                memberRoutineIds = memberIds,
                groupTargets = groupTargets,
            )
        ) {
            is ResultState.Success -> Unit
            is ResultState.Error -> return@withContext Result.failure(IllegalStateException(result.message))
            ResultState.Loading ->
                return@withContext Result.failure(IllegalStateException("업로드가 완료되지 않았습니다."))
        }
    }

    Result.success(Unit)
}
