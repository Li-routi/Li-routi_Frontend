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
            )
        }

/**
 * 촬영 URI → 개인/그룹 루틴 인증.
 * purpose가 달라 개인·그룹을 함께 고르면 미디어 업로드를 각각 1회씩 수행한다.
 */
internal suspend fun submitRoutineAuthUpload(
    photoUri: Uri,
    memo: String,
    selectedRoutineIds: Set<String>,
    context: Context,
): Result<Unit> = withContext(Dispatchers.IO) {
    val memberIds = selectedRoutineIds.mapNotNull(::parseMemberRoutineId)
    val groupTargets = selectedRoutineIds.mapNotNull(::parseGroupRoutineTarget)
    if (memberIds.isEmpty() && groupTargets.isEmpty()) {
        return@withContext Result.failure(
            IllegalArgumentException("인증할 루틴을 선택해 주세요."),
        )
    }
    val contentType = context.contentResolver.getType(photoUri) ?: "image/jpeg"
    val bytes = context.contentResolver.openInputStream(photoUri)?.use { it.readBytes() }
        ?: return@withContext Result.failure(IllegalArgumentException("사진을 읽을 수 없습니다."))
    when (
        val result = RoutineContainer.submitRoutineAuthUseCase(
            contentType = contentType,
            bytes = bytes,
            content = memo,
            memberRoutineIds = memberIds,
            groupTargets = groupTargets,
        )
    ) {
        is ResultState.Success -> Result.success(Unit)
        is ResultState.Error -> Result.failure(IllegalStateException(result.message))
        ResultState.Loading -> Result.failure(IllegalStateException("업로드가 완료되지 않았습니다."))
    }
}
