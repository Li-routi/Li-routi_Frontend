package com.li_routi.feature.grouproutine.vm

import com.li_routi.core.common.android.architecture.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class GroupRoutineViewModel : BaseViewModel() {

    private val _uiState = MutableStateFlow(GroupRoutineUiState())
    val uiState: StateFlow<GroupRoutineUiState> = _uiState.asStateFlow()

    fun onRoutineClick(routineId: Long) {
        _uiState.update {
            it.copy(
                screenMode = GroupRoutineScreenMode.Detail,
                selectedRoutineId = routineId,
                selectedMemberId = null,
                isNewCertificationDialogVisible = true,
                actionMessage = null,
            )
        }
    }

    fun onBackClick() {
        _uiState.update { state ->
            when (state.screenMode) {
                GroupRoutineScreenMode.Detail -> state.copy(
                    screenMode = GroupRoutineScreenMode.List,
                    selectedRoutineId = null,
                    selectedMemberId = null,
                    showOnlyMyCertifications = false,
                    isNewCertificationDialogVisible = false,
                    actionMessage = null,
                )

                GroupRoutineScreenMode.CertificationCollection,
                GroupRoutineScreenMode.GroupChat,
                GroupRoutineScreenMode.GroupSettings -> state.copy(
                    screenMode = GroupRoutineScreenMode.Detail,
                    actionMessage = null,
                )

                GroupRoutineScreenMode.GroupRoutineManage,
                GroupRoutineScreenMode.RoomNameEdit,
                GroupRoutineScreenMode.LeaderSettings,
                GroupRoutineScreenMode.RoomAlarmSettings -> state.copy(
                    screenMode = GroupRoutineScreenMode.GroupSettings,
                    pendingLeaderMemberId = null,
                    actionMessage = null,
                )

                GroupRoutineScreenMode.CreateRoutineSelect -> state.copy(
                    screenMode = GroupRoutineScreenMode.CreateRoomName,
                    actionMessage = null,
                )

                GroupRoutineScreenMode.CreateRoomName -> state.copy(
                    screenMode = GroupRoutineScreenMode.List,
                    roomNameInput = "",
                    routineOptions = DefaultCreateRoutineOptions,
                    selectedCategory = "전체",
                    actionMessage = null,
                )

                GroupRoutineScreenMode.JoinByCode -> state.copy(
                    screenMode = GroupRoutineScreenMode.List,
                    inviteCodeInput = "",
                    actionMessage = null,
                )

                GroupRoutineScreenMode.List -> state.copy(actionMessage = null)
            }
        }
    }

    fun onCertificationSummaryClick() {
        _uiState.update {
            it.copy(
                screenMode = GroupRoutineScreenMode.CertificationCollection,
                showOnlyMyCertifications = false,
                actionMessage = null,
            )
        }
    }

    fun onChatClick() {
        _uiState.update {
            it.copy(
                screenMode = GroupRoutineScreenMode.GroupChat,
                actionMessage = null,
            )
        }
    }

    fun onSettingsClick() {
        _uiState.update {
            it.copy(
                screenMode = GroupRoutineScreenMode.GroupSettings,
                selectedMemberId = null,
                actionMessage = null,
            )
        }
    }

    fun onMemberClick(memberId: Long) {
        _uiState.update { it.copy(selectedMemberId = memberId, actionMessage = null) }
    }

    fun onDismissMemberDialog() {
        _uiState.update { it.copy(selectedMemberId = null) }
    }

    fun onDismissNewCertificationDialog() {
        _uiState.update { it.copy(isNewCertificationDialogVisible = false) }
    }

    fun onGroupRoutineManageClick() {
        _uiState.update { it.copy(screenMode = GroupRoutineScreenMode.GroupRoutineManage, actionMessage = null) }
    }

    fun onRoomNameEditClick() {
        _uiState.update { state ->
            state.copy(
                screenMode = GroupRoutineScreenMode.RoomNameEdit,
                roomNameInput = state.selectedRoutine?.title.orEmpty(),
                actionMessage = null,
            )
        }
    }

    fun onLeaderSettingsClick() {
        _uiState.update { state ->
            state.copy(
                screenMode = GroupRoutineScreenMode.LeaderSettings,
                pendingLeaderMemberId = state.members.firstOrNull { it.isMe }?.id,
                actionMessage = null,
            )
        }
    }

    fun onRoomAlarmSettingsClick() {
        _uiState.update { it.copy(screenMode = GroupRoutineScreenMode.RoomAlarmSettings, actionMessage = null) }
    }

    fun onLeaderMemberClick(memberId: Long) {
        _uiState.update { it.copy(pendingLeaderMemberId = memberId, actionMessage = null) }
    }

    fun onLeaderTransferConfirmClick() {
        _uiState.update { state ->
            val myMemberId = state.members.firstOrNull { it.isMe }?.id
            state.copy(
                screenMode = GroupRoutineScreenMode.GroupSettings,
                isCurrentUserLeader = state.pendingLeaderMemberId == myMemberId,
                pendingLeaderMemberId = null,
                actionMessage = if (state.pendingLeaderMemberId == myMemberId) null else "방장이 변경되었습니다.",
            )
        }
    }

    fun onRoomLockClick() {
        _uiState.update { state ->
            val nextLocked = !state.isRoomLocked
            state.copy(
                isRoomLocked = nextLocked,
                actionMessage = if (nextLocked) {
                    "더 이상 다른 사람이 참여할 수 없습니다."
                } else {
                    "다른 사람이 참여할 수 있습니다."
                },
            )
        }
    }

    fun onRoomNameEditConfirmClick() {
        _uiState.update { state ->
            val title = state.roomNameInput.trim()
            if (title.isBlank()) {
                state.copy(actionMessage = "방 이름을 입력해주세요.")
            } else {
                state.copy(
                    screenMode = GroupRoutineScreenMode.GroupSettings,
                    routines = state.routines.map { routine ->
                        if (routine.id == state.selectedRoutine?.id) routine.copy(title = title) else routine
                    },
                    actionMessage = "방 이름이 변경됐어요.",
                )
            }
        }
    }

    fun onAddClick() {
        _uiState.update { it.copy(isActionSheetVisible = true, actionMessage = null) }
    }

    fun onSearchInputChange(value: String) {
        _uiState.update { it.copy(searchInput = value, actionMessage = null) }
    }

    fun onDismissActionSheet() {
        _uiState.update { it.copy(isActionSheetVisible = false) }
    }

    fun onCreateRoomClick() {
        _uiState.update {
            it.copy(
                screenMode = GroupRoutineScreenMode.CreateRoomName,
                isActionSheetVisible = false,
                actionMessage = null,
            )
        }
    }

    fun onJoinByCodeClick() {
        _uiState.update {
            it.copy(
                screenMode = GroupRoutineScreenMode.JoinByCode,
                isActionSheetVisible = false,
                inviteCodeInput = "",
                actionMessage = null,
            )
        }
    }

    fun onInviteCodeChange(value: String) {
        _uiState.update { it.copy(inviteCodeInput = value, actionMessage = null) }
    }

    fun onInviteCodeConfirmClick() {
        _uiState.update { state ->
            if (state.inviteCodeInput.isBlank()) {
                state.copy(actionMessage = "초대코드를 입력해주세요.")
            } else {
                state.copy(
                    screenMode = GroupRoutineScreenMode.Detail,
                    selectedRoutineId = state.routines.firstOrNull()?.id,
                    inviteCodeInput = "",
                    actionMessage = "그룹방에 참여했어요.",
                )
            }
        }
    }

    fun onInviteCodeCopyClick() {
        _uiState.update { it.copy(actionMessage = "초대코드가 복사되었습니다!") }
    }

    fun onRoomNameChange(value: String) {
        _uiState.update { it.copy(roomNameInput = value, actionMessage = null) }
    }

    fun onCreateRoomNextClick() {
        _uiState.update { state ->
            if (state.roomNameInput.isBlank()) {
                state.copy(actionMessage = "방 이름을 입력해주세요.")
            } else {
                state.copy(
                    screenMode = GroupRoutineScreenMode.CreateRoutineSelect,
                    actionMessage = null,
                )
            }
        }
    }

    fun onCreateRoutineOptionClick(optionId: Long) {
        _uiState.update { state ->
            state.copy(
                routineOptions = state.routineOptions.map { option ->
                    if (option.id == optionId) option.copy(isSelected = !option.isSelected) else option
                },
                actionMessage = null,
            )
        }
    }

    fun onCreateRoutineSelectAllClick() {
        _uiState.update { state ->
            val visibleIds = state.visibleRoutineOptions.map { it.id }.toSet()
            val shouldSelect = !state.allVisibleRoutineOptionsSelected
            state.copy(
                routineOptions = state.routineOptions.map { option ->
                    if (option.id in visibleIds) option.copy(isSelected = shouldSelect) else option
                },
                actionMessage = null,
            )
        }
    }

    fun onCategoryClick(category: String) {
        _uiState.update { it.copy(selectedCategory = category, actionMessage = null) }
    }

    fun onCategoryAddClick() {
        _uiState.update {
            it.copy(
                isCategorySheetVisible = true,
                categoryInput = "",
                actionMessage = null,
            )
        }
    }

    fun onDismissCategorySheet() {
        _uiState.update { it.copy(isCategorySheetVisible = false, categoryInput = "") }
    }

    fun onCategoryInputChange(value: String) {
        _uiState.update { it.copy(categoryInput = value, actionMessage = null) }
    }

    fun onCategoryConfirmClick() {
        _uiState.update { state ->
            val name = state.categoryInput.trim()
            if (name.isBlank()) {
                state.copy(actionMessage = "카테고리를 입력해주세요.")
            } else if (name in state.categories) {
                state.copy(actionMessage = "이미 있는 카테고리예요.")
            } else {
                state.copy(
                    categories = state.categories + name,
                    selectedCategory = name,
                    categoryInput = "",
                    isCategorySheetVisible = false,
                    actionMessage = null,
                )
            }
        }
    }

    fun onRoutineAddClick() {
        _uiState.update {
            it.copy(
                isRoutineSettingSheetVisible = true,
                editingRoutineId = null,
                routineDraftName = "",
                routineDraftRepeatDays = emptySet(),
                actionMessage = null,
            )
        }
    }

    fun onRoutineSettingClick(optionId: Long) {
        _uiState.update { state ->
            val option = state.routineOptions.firstOrNull { it.id == optionId } ?: return@update state
            state.copy(
                isRoutineSettingSheetVisible = true,
                editingRoutineId = optionId,
                routineDraftName = option.title,
                routineDraftRepeatDays = option.repeatDays,
                actionMessage = null,
            )
        }
    }

    fun onDismissRoutineSettingSheet() {
        _uiState.update {
            it.copy(
                isRoutineSettingSheetVisible = false,
                editingRoutineId = null,
                routineDraftName = "",
                routineDraftRepeatDays = emptySet(),
                isDeleteRoutineDialogVisible = false,
            )
        }
    }

    fun onRoutineDraftNameChange(value: String) {
        _uiState.update { it.copy(routineDraftName = value, actionMessage = null) }
    }

    fun onRepeatDayClick(day: String) {
        _uiState.update { state ->
            val nextDays = if (day in state.routineDraftRepeatDays) {
                state.routineDraftRepeatDays - day
            } else {
                state.routineDraftRepeatDays + day
            }
            state.copy(routineDraftRepeatDays = nextDays, actionMessage = null)
        }
    }

    fun onRoutineSettingConfirmClick() {
        _uiState.update { state ->
            val title = state.routineDraftName.trim()
            if (title.isBlank()) {
                state.copy(actionMessage = "루틴 이름을 입력해주세요.")
            } else {
                val repeatLabel = repeatDaysLabel(state.routineDraftRepeatDays)
                val editingId = state.editingRoutineId
                val nextOptions = if (editingId == null) {
                    val newId = (state.routineOptions.maxOfOrNull { it.id } ?: 0L) + 1L
                    state.routineOptions + CreateRoutineOptionUiModel(
                        id = newId,
                        title = title,
                        deadline = "23:00",
                        category = state.selectedCategory.takeUnless { it == "전체" } ?: "건강",
                        repeatLabel = repeatLabel,
                        repeatDays = state.routineDraftRepeatDays,
                    )
                } else {
                    state.routineOptions.map { option ->
                        if (option.id == editingId) {
                            option.copy(
                                title = title,
                                repeatLabel = repeatLabel,
                                repeatDays = state.routineDraftRepeatDays,
                            )
                        } else {
                            option
                        }
                    }
                }

                state.copy(
                    routineOptions = nextOptions,
                    isRoutineSettingSheetVisible = false,
                    editingRoutineId = null,
                    routineDraftName = "",
                    routineDraftRepeatDays = emptySet(),
                    actionMessage = null,
                )
            }
        }
    }

    fun onRoutineDeleteClick() {
        _uiState.update { it.copy(isDeleteRoutineDialogVisible = true) }
    }

    fun onDismissDeleteRoutineDialog() {
        _uiState.update { it.copy(isDeleteRoutineDialogVisible = false) }
    }

    fun onConfirmDeleteRoutineClick() {
        _uiState.update { state ->
            val editingId = state.editingRoutineId ?: return@update state.copy(
                isRoutineSettingSheetVisible = false,
                isDeleteRoutineDialogVisible = false,
            )
            state.copy(
                routineOptions = state.routineOptions.filterNot { it.id == editingId },
                isRoutineSettingSheetVisible = false,
                isDeleteRoutineDialogVisible = false,
                editingRoutineId = null,
                routineDraftName = "",
                routineDraftRepeatDays = emptySet(),
                actionMessage = null,
            )
        }
    }

    fun onCreateRoomDoneClick() {
        _uiState.update { state ->
            val selectedOptions = state.routineOptions.filter { it.isSelected }
            if (selectedOptions.isEmpty()) {
                state.copy(actionMessage = "함께할 루틴을 선택해주세요.")
            } else {
                val newId = (state.routines.maxOfOrNull { it.id } ?: 0L) + 1L
                val newRoutine = GroupRoutineUiModel(
                    id = newId,
                    title = state.roomNameInput.trim(),
                    lastActiveLabel = "방금 전 활동",
                    memberCount = 1,
                    routineCount = selectedOptions.size,
                    statusLabel = "진행중",
                    isCompleted = false,
                    todayCompletedCount = 0,
                    todayTotalCount = selectedOptions.size,
                    streakDays = 0,
                    monthlyAchievementRate = 0,
                    todayCertificationCount = 0,
                )
                val selectedTodos = selectedOptions.map { option ->
                    GroupTodoUiModel(
                        id = option.id,
                        title = option.title,
                        deadline = option.deadline,
                        category = option.category,
                        isDone = false,
                    )
                }

                state.copy(
                    screenMode = GroupRoutineScreenMode.Detail,
                    selectedRoutineId = newId,
                    roomNameInput = "",
                    routineOptions = DefaultCreateRoutineOptions,
                    selectedCategory = "전체",
                    routines = listOf(newRoutine) + state.routines,
                    todos = selectedTodos,
                    actionMessage = "방이 만들어졌어요.",
                )
            }
        }
    }

    fun onTodoCheckedChange(todoId: Long, checked: Boolean) {
        _uiState.update { state ->
            state.copy(
                todos = state.todos.map { todo ->
                    if (todo.id == todoId) todo.copy(isDone = checked) else todo
                },
                actionMessage = null,
            )
        }
    }

    fun onCertificationTabClick(showOnlyMine: Boolean) {
        _uiState.update { it.copy(showOnlyMyCertifications = showOnlyMine, actionMessage = null) }
    }

    private fun repeatDaysLabel(days: Set<String>): String {
        return when (days) {
            emptySet<String>() -> "없음"
            setOf("일", "월", "화", "수", "목", "금", "토") -> "매일"
            setOf("월", "화", "수", "목", "금") -> "주중"
            setOf("일", "토") -> "주말"
            else -> days.joinToString(" ")
        }
    }
}
