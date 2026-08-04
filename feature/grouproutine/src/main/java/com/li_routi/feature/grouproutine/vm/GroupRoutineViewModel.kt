package com.li_routi.feature.grouproutine.vm

import androidx.lifecycle.viewModelScope
import com.li_routi.core.common.android.architecture.BaseViewModel
import com.li_routi.core.common.kotlin.util.ResultState
import com.li_routi.core.data.di.GroupRoutineContainer
import com.li_routi.core.domain.grouproutine.CreateGroupRoutineUseCase
import com.li_routi.core.domain.grouproutine.CreateGroupUseCase
import com.li_routi.core.domain.grouproutine.GetGroupInviteCodeUseCase
import com.li_routi.core.domain.grouproutine.GroupRoutineSchedule
import com.li_routi.core.domain.grouproutine.IssueGroupInviteCodeUseCase
import com.li_routi.core.domain.grouproutine.NewGroupCategory
import com.li_routi.core.domain.grouproutine.NewGroupRoutine
import com.li_routi.core.domain.grouproutine.RepeatDay
import com.li_routi.core.domain.grouproutine.UpdateGroupRoutineUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GroupRoutineViewModel(
    private val createGroupUseCase: CreateGroupUseCase = GroupRoutineContainer.createGroupUseCase,
    private val createGroupRoutineUseCase: CreateGroupRoutineUseCase = GroupRoutineContainer.createGroupRoutineUseCase,
    private val updateGroupRoutineUseCase: UpdateGroupRoutineUseCase = GroupRoutineContainer.updateGroupRoutineUseCase,
    private val getGroupInviteCodeUseCase: GetGroupInviteCodeUseCase = GroupRoutineContainer.getGroupInviteCodeUseCase,
    private val issueGroupInviteCodeUseCase: IssueGroupInviteCodeUseCase = GroupRoutineContainer.issueGroupInviteCodeUseCase,
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(GroupRoutineUiState())
    val uiState: StateFlow<GroupRoutineUiState> = _uiState.asStateFlow()

    // PR 반영: Mock ID와 실제 서버 ID 분리
    // createGroupUseCase 성공 시나 초대코드로 조인했을 때 발급되는 실제 서버 그룹 ID를 저장합니다.
    private var backendGroupId: Long? = null

    // API 호출 시 사용할 currentGroupId()는 이제 실제 backendGroupId를 참조합니다.
    private fun currentGroupId(): Long? = backendGroupId

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

    fun onCreateFlowCloseClick() {
        _uiState.update {
            it.copy(
                screenMode = GroupRoutineScreenMode.List,
                roomNameInput = "",
                routineOptions = DefaultCreateRoutineOptions,
                selectedCategory = "전체",
                isCategorySheetVisible = false,
                isRoutineSettingSheetVisible = false,
                isDeleteRoutineDialogVisible = false,
                actionMessage = null,
            )
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

    fun onMessageEditClick() {
        _uiState.update { state ->
            val myMessage = state.members.firstOrNull { it.isMe }?.message.orEmpty()
            state.copy(isMessageEditSheetVisible = true, messageDraft = myMessage, actionMessage = null)
        }
    }

    fun onMessageDraftChange(value: String) {
        _uiState.update { it.copy(messageDraft = value.take(8)) }
    }

    fun onDismissMessageEditSheet() {
        _uiState.update { it.copy(isMessageEditSheetVisible = false) }
    }

    fun onMessageEditConfirmClick() {
        _uiState.update { state ->
            state.copy(
                members = state.members.map { if (it.isMe) it.copy(message = state.messageDraft) else it },
                isMessageEditSheetVisible = false,
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
        loadInviteCode()
    }

    private fun loadInviteCode() {
        val groupId = currentGroupId()
        if (groupId == null) {
            _uiState.update { it.copy(actionMessage = "그룹 ID를 찾을 수 없습니다.") }
            return
        }
        
        viewModelScope.launch {
            when (val result = getGroupInviteCodeUseCase(groupId)) {
                is ResultState.Success -> _uiState.update { it.copy(groupInviteCode = result.data.inviteCode) }
                // PR 반영: ResultState.Error 시 바로 새로 발급(issue)하지 않고 에러 메시지 띄우기 (기존 초대코드 무효화 방지)
                is ResultState.Error -> _uiState.update { it.copy(actionMessage = result.message) }
                ResultState.Loading -> Unit
            }
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
        val state = _uiState.value
        val title = state.routineDraftName.trim()
        
        if (title.isBlank()) {
            _uiState.update { it.copy(actionMessage = "루틴 이름을 입력해주세요.") }
            return
        }

        // PR 반영: "전체" 카테고리는 조회(필터) 전용이므로 생성/수정 시 선택을 강제
        if (state.selectedCategory == "전체") {
            _uiState.update { it.copy(actionMessage = "카테고리를 선택해주세요.") }
            return
        }

        if (state.screenMode == GroupRoutineScreenMode.GroupRoutineManage) {
            submitGroupRoutine(state, title)
        } else {
            applyLocalRoutineDraft(state, title)
        }
    }

    private fun submitGroupRoutine(state: GroupRoutineUiState, title: String) {
        val groupId = currentGroupId()
        if (groupId == null) {
            _uiState.update { it.copy(actionMessage = "그룹 ID를 찾을 수 없습니다.") }
            return
        }

        // PR 반영: 따닥(중복 클릭)으로 인한 API 다중 호출 방지 가드
        if (state.isSubmitting) return
        _uiState.update { it.copy(isSubmitting = true) }

        val categoryName = state.selectedCategory
        val schedules = state.routineDraftRepeatDays.toGroupRoutineSchedules(endTime = "23:00")
        val editingId = state.editingRoutineId
        val categoryId = DefaultCategoryIds[categoryName]
        
        if (categoryId == null) {
            _uiState.update { it.copy(isSubmitting = false, actionMessage = "\"$categoryName\"은 아직 지원하지 않는 카테고리예요. 기본 카테고리를 선택해주세요.") }
            return
        }
        if (editingId != null && DefaultCreateRoutineOptions.any { it.id == editingId }) {
            _uiState.update { it.copy(isSubmitting = false, actionMessage = "기본 샘플 루틴은 서버에 수정할 수 없습니다.") }
            return
        }

        viewModelScope.launch {
            try {
                val result = if (editingId == null) {
                    createGroupRoutineUseCase(
                        groupId = groupId,
                        categoryId = categoryId,
                        title = title,
                        description = title,
                        schedules = schedules,
                    )
                } else {
                    updateGroupRoutineUseCase(
                        groupId = groupId,
                        routineId = editingId,
                        categoryId = categoryId,
                        title = title,
                        description = title,
                        schedules = schedules,
                    )
                }
                when (result) {
                    is ResultState.Success -> {
                        val routine = result.data
                        val repeatLabel = repeatDaysLabel(state.routineDraftRepeatDays)
                        val savedOption = CreateRoutineOptionUiModel(
                            id = routine.routineId,
                            title = routine.title,
                            deadline = "23:00",
                            category = categoryName,
                            repeatLabel = repeatLabel,
                            repeatDays = state.routineDraftRepeatDays,
                        )
                        _uiState.update {
                            val nextOptions = if (editingId == null) {
                                it.routineOptions + savedOption
                            } else {
                                it.routineOptions.map { option ->
                                    // PR 반영: 수정 시 기존의 isSelected 상태 유지
                                    if (option.id == editingId) savedOption.copy(isSelected = option.isSelected) else option
                                }
                            }
                            it.copy(
                                routineOptions = nextOptions,
                                isRoutineSettingSheetVisible = false,
                                editingRoutineId = null,
                                routineDraftName = "",
                                routineDraftRepeatDays = emptySet(),
                                actionMessage = null,
                            )
                        }
                    }
                    is ResultState.Error -> _uiState.update { it.copy(actionMessage = result.message) }
                    ResultState.Loading -> Unit
                }
            } finally {
                // 성공/실패 무관하게 중복 호출 방지 플래그 초기화
                _uiState.update { it.copy(isSubmitting = false) }
            }
        }
    }

    private fun applyLocalRoutineDraft(state: GroupRoutineUiState, title: String) {
        val repeatLabel = repeatDaysLabel(state.routineDraftRepeatDays)
        val editingId = state.editingRoutineId
        val categoryName = state.selectedCategory
        
        val nextOptions = if (editingId == null) {
            val newId = (state.routineOptions.maxOfOrNull { it.id } ?: 0L) + 1L
            state.routineOptions + CreateRoutineOptionUiModel(
                id = newId,
                title = title,
                deadline = "23:00",
                category = categoryName,
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

        _uiState.update {
            it.copy(
                routineOptions = nextOptions,
                isRoutineSettingSheetVisible = false,
                editingRoutineId = null,
                routineDraftName = "",
                routineDraftRepeatDays = emptySet(),
                actionMessage = null,
            )
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
        val state = _uiState.value
        val selectedOptions = state.routineOptions.filter { it.isSelected }
        val roomName = state.roomNameInput.trim()
        if (selectedOptions.isEmpty()) {
            _uiState.update { it.copy(actionMessage = "함께할 루틴을 선택해주세요.") }
            return
        }
        if (roomName.isBlank()) {
            _uiState.update { it.copy(actionMessage = "방 이름을 입력해주세요.") }
            return
        }
        
        // PR 반영: 따닥(중복 클릭)으로 인한 그룹 2개 생성 방지
        if (state.isSubmitting) return
        _uiState.update { it.copy(isSubmitting = true) }

        val categoryNames = selectedOptions.map { it.category }.distinct()
        val customCategories = categoryNames
            .filter { it !in DefaultCategoryIds }
            .map { NewGroupCategory(clientKey = it, name = it, color = null) }
        val routines = selectedOptions.map { option ->
            NewGroupRoutine(
                categoryId = DefaultCategoryIds[option.category],
                categoryKey = if (option.category in DefaultCategoryIds) null else option.category,
                title = option.title,
                description = option.title,
                schedules = option.repeatDays.toGroupRoutineSchedules(endTime = option.deadline),
            )
        }

        viewModelScope.launch {
            try {
                when (val result = createGroupUseCase(roomName, customCategories, routines)) {
                    is ResultState.Success -> {
                        val groupId = result.data.groupId
                        // PR 반영: 생성 성공한 실제 서버 그룹 ID를 저장
                        backendGroupId = groupId
                        
                        val newRoutine = GroupRoutineUiModel(
                            id = groupId,
                            title = roomName,
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
                        _uiState.update {
                            it.copy(
                                screenMode = GroupRoutineScreenMode.Detail,
                                selectedRoutineId = groupId,
                                roomNameInput = "",
                                routineOptions = DefaultCreateRoutineOptions,
                                selectedCategory = "전체",
                                routines = listOf(newRoutine) + it.routines,
                                todos = selectedTodos,
                                actionMessage = "방이 만들어졌어요.",
                            )
                        }
                    }
                    is ResultState.Error -> _uiState.update { it.copy(actionMessage = result.message) }
                    ResultState.Loading -> Unit
                }
            } finally {
                // 완료 후 제출 상태 해제
                _uiState.update { it.copy(isSubmitting = false) }
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

private val KoreanDayToRepeatDay = mapOf(
    "일" to RepeatDay.SUNDAY,
    "월" to RepeatDay.MONDAY,
    "화" to RepeatDay.TUESDAY,
    "수" to RepeatDay.WEDNESDAY,
    "목" to RepeatDay.THURSDAY,
    "금" to RepeatDay.FRIDAY,
    "토" to RepeatDay.SATURDAY,
)

// 백엔드 기본 카테고리(운동/건강/자기계발/생활정리/마음관리/취미) 이름 → categoryId
private val DefaultCategoryIds = mapOf(
    "운동" to 1L,
    "건강" to 2L,
    "자기계발" to 3L,
    "생활정리" to 4L,
    "마음관리" to 5L,
    "취미" to 6L,
)

private fun Set<String>.toGroupRoutineSchedules(endTime: String): List<GroupRoutineSchedule> {
    val days = ifEmpty { KoreanDayToRepeatDay.keys }
    return days.mapNotNull { KoreanDayToRepeatDay[it] }.map { day ->
        GroupRoutineSchedule(repeatDay = day, startTime = "00:00", endTime = endTime)
    }
}
