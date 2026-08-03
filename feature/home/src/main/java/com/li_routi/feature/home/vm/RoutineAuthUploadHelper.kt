package com.li_routi.feature.home.vm

import android.content.Context
import android.net.Uri
import com.li_routi.core.common.kotlin.util.ResultState
import com.li_routi.core.data.di.RoutineContainer
import com.li_routi.core.domain.routine.GroupRoutineTarget
import com.li_routi.feature.home.component.RoutineChecklistItemUiModel
import com.li_routi.feature.home.component.RoutineChecklistKind
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** 홈 체크리스트 개인 루틴 id 접두사. [HomeUiMapper]와 동일. */
internal const val MemberRoutineIdPrefix = "my_"

/** 홈 체크리스트 그룹 루틴 id: `group_{groupId}_{routineId}`. */
private val GroupRoutineIdPattern = Regex("""^group_(\d+)_(\d+)$""")

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

internal fun List<RoutineChecklistItemUiModel>.toAuthSelectables(): List<RoutineAuthSelectableUiModel> =
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
 * 촬영 URI → 개인/그룹 루틴 인증.
 *
 * 챌린지 인증과 동일하게 사진 바이트 + 코멘트(memo)를 서버에 저장한다.
 * purpose가 달라 개인·그룹을 함께 고르면 미디어 업로드를 각각 1회씩 수행한다.
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
    if (memberIds.isEmpty() && groupTargets.isEmpty()) {
        return@withContext Result.failure(
            IllegalArgumentException("인증할 루틴을 선택해 주세요."),
        )
    }
    val contentType = context.contentResolver.getType(photoUri) ?: "image/jpeg"
    val bytes = context.contentResolver.openInputStream(photoUri)?.use { it.readBytes() }
        ?: return@withContext Result.failure(IllegalArgumentException("사진을 읽을 수 없습니다."))
    // 챌린지 인증과 동일: 빈 코멘트는 null로 보내고, 값이 있으면 content로 저장.
    val content = memo.trim().ifBlank { null }
    when (
        val result = RoutineContainer.submitRoutineAuthUseCase(
            contentType = contentType,
            bytes = bytes,
            content = content,
            memberRoutineIds = memberIds,
            groupTargets = groupTargets,
        )
    ) {
        is ResultState.Success -> Result.success(Unit)
        is ResultState.Error -> Result.failure(IllegalStateException(result.message))
        ResultState.Loading -> Result.failure(IllegalStateException("업로드가 완료되지 않았습니다."))
    }
}
