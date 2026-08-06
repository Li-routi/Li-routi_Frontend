package com.li_routi.feature.grouproutine.vm

enum class GroupRoutineScreenMode {
    List,
    Detail,
    CertificationCollection,
    GroupChat,
    GroupSettings,
    GroupRoutineManage,
    RoomNameEdit,
    LeaderSettings,
    RoomAlarmSettings,
    CreateRoomName,
    CreateRoutineSelect,
    JoinByCode,
}

data class GroupRoutineUiModel(
    val id: Long,
    val title: String,
    val lastActiveLabel: String,
    val memberCount: Int,
    val routineCount: Int,
    val statusLabel: String,
    val isCompleted: Boolean,
    val todayCompletedCount: Int,
    val todayTotalCount: Int,
    val streakDays: Int,
    val monthlyAchievementRate: Int,
    val todayCertificationCount: Int,
)

data class GroupMemberUiModel(
    val id: Long,
    val name: String,
    val message: String,
    val streak: Int,
    val isMe: Boolean = false,
)

data class GroupTodoUiModel(
    val id: Long,
    val title: String,
    val deadline: String,
    val category: String,
    val isDone: Boolean,
)

data class CertificationPostUiModel(
    val id: Long,
    val userName: String,
    val body: String,
    val likeCount: Int,
    val timeAgo: String,
    val isMine: Boolean,
)

data class NewCertificationUiModel(
    val id: Long,
    val memberName: String,
    val routineName: String,
    val message: String,
)

data class CreateRoutineOptionUiModel(
    val id: Long,
    val title: String,
    val deadline: String,
    val category: String,
    val startTime: String = "08:00",
    val repeatLabel: String = "없음",
    val repeatDays: Set<String> = emptySet(),
    val isSelected: Boolean = false,
)

data class GroupRoutineUiState(
    val screenMode: GroupRoutineScreenMode = GroupRoutineScreenMode.List,
    val selectedRoutineId: Long? = null,
    val selectedMemberId: Long? = null,
    val pendingLeaderMemberId: Long? = null,
    val isActionSheetVisible: Boolean = false,
    val actionMessage: String? = null,
    val isRoomLocked: Boolean = false,
    val isCurrentUserLeader: Boolean = true,
    val showOnlyMyCertifications: Boolean = false,
    val isNewCertificationDialogVisible: Boolean = false,
    val isEmptyState: Boolean = false,
    val searchInput: String = "",
    val roomNameInput: String = "",
    val inviteCodeInput: String = "",
    val groupInviteCode: String? = null,
    val selectedCategory: String = "전체",
    // PR 반영: 백엔드 카테고리와 일치시킴 ("공부" 제거 및 항목 추가)
    val categories: List<String> = listOf("전체", "운동", "건강", "자기계발", "생활정리", "마음관리", "취미"),
    val routineOptions: List<CreateRoutineOptionUiModel> = SampleCreateRoutineOptions,
    val isRoutineSettingSheetVisible: Boolean = false,
    val editingRoutineId: Long? = null,
    val routineDraftName: String = "",
    val routineDraftStartTime: String = "08:00",
    val routineDraftEndTime: String = "20:00",
    val routineDraftRepeatDays: Set<String> = emptySet(),
    val isDeleteRoutineDialogVisible: Boolean = false,
    val isCategorySheetVisible: Boolean = false,
    val categoryInput: String = "",
    val isMessageEditSheetVisible: Boolean = false,
    val messageDraft: String = "",
    val newCertifications: List<NewCertificationUiModel> = SampleNewCertifications,
    val routines: List<GroupRoutineUiModel> = SampleGroupRoutines,
    val members: List<GroupMemberUiModel> = SampleGroupMembers,
    val todos: List<GroupTodoUiModel> = SampleGroupTodos,
    val posts: List<CertificationPostUiModel> = SampleCertificationPosts,
    // PR 반영: 중복 API 제출 방지용 상태 추가
    val isSubmitting: Boolean = false,
) {
    val visibleRoutines: List<GroupRoutineUiModel>
        get() = if (isEmptyState) {
            emptyList()
        } else if (searchInput.isBlank()) {
            routines
        } else {
            routines.filter { it.title.contains(searchInput.trim(), ignoreCase = true) }
        }

    val selectedRoutine: GroupRoutineUiModel?
        get() = routines.firstOrNull { it.id == selectedRoutineId } ?: routines.firstOrNull()

    val selectedMember: GroupMemberUiModel?
        get() = members.firstOrNull { it.id == selectedMemberId }

    val selectedCreateRoutineCount: Int
        get() = routineOptions.count { it.isSelected }

    val visibleRoutineOptions: List<CreateRoutineOptionUiModel>
        get() = if (selectedCategory == "전체") {
            routineOptions
        } else {
            routineOptions.filter { it.category == selectedCategory }
        }

    val allVisibleRoutineOptionsSelected: Boolean
        get() = visibleRoutineOptions.isNotEmpty() && visibleRoutineOptions.all { it.isSelected }

    val todoProgressLabel: String
        get() = "${todos.count { it.isDone }}/${todos.size} 완료"
}

private val SampleGroupRoutines = listOf(
    GroupRoutineUiModel(
        // PR 반영: 서버 ID(양수)와의 충돌을 막기 위해 Mock 데이터 ID를 음수로 변경
        id = -1L,
        title = "코딩",
        lastActiveLabel = "1시간 전 활동",
        memberCount = 3,
        routineCount = 6,
        statusLabel = "진행중",
        isCompleted = false,
        todayCompletedCount = 3,
        todayTotalCount = 6,
        streakDays = 5,
        monthlyAchievementRate = 60,
        todayCertificationCount = 3,
    ),
)

private val SampleGroupMembers = listOf(
    GroupMemberUiModel(1L, "민지", "반가워요!", 12, isMe = true),
    GroupMemberUiModel(2L, "서현", "오늘도 화이팅", 12),
    GroupMemberUiModel(3L, "수연", "반가워요!", 12),
    GroupMemberUiModel(4L, "준호", "반가워요!", 12),
    GroupMemberUiModel(5L, "건호", "반가워요!", 12),
    GroupMemberUiModel(6L, "마담", "반가워요!", 12),
)

private val SampleGroupTodos = listOf(
    GroupTodoUiModel(1L, "물 마시기", "22:00", "건강", false),
    GroupTodoUiModel(2L, "스트레칭하기", "23:00", "운동", false),
    GroupTodoUiModel(3L, "물 마시기", "22:00", "건강", false),
)

private val SampleCertificationPosts = listOf(
    CertificationPostUiModel(
        id = 1L,
        userName = "민지",
        body = "물 마시기 1일차 인증! 오늘도 잊지 않고 해냈어요.",
        likeCount = 1,
        timeAgo = "9시간 전",
        isMine = true,
    ),
    CertificationPostUiModel(
        id = 2L,
        userName = "서현",
        body = "스트레칭 완료. 내일도 같이 이어가요.",
        likeCount = 3,
        timeAgo = "11시간 전",
        isMine = false,
    ),
)

private val SampleNewCertifications = listOf(
    NewCertificationUiModel(1L, "민지", "물 마시기", "오늘도 1L 완료!"),
    NewCertificationUiModel(2L, "서현", "스트레칭하기", "몸이 개운해요!"),
    NewCertificationUiModel(3L, "수연", "영양제 먹기", "챙겨 먹었어요!"),
    NewCertificationUiModel(4L, "준호", "명상 하기", "마음이 편안해졌어요!"),
    NewCertificationUiModel(5L, "건호", "산책하기", "상쾌한 하루!"),
)

private val SampleCreateRoutineOptions = listOf(
    CreateRoutineOptionUiModel(1L, "물 마시기", "22:00", "건강", repeatLabel = "\uC8FC\uC911", repeatDays = setOf("\uC6D4", "\uD654", "\uC218", "\uBAA9", "\uAE08")),
    CreateRoutineOptionUiModel(2L, "스트레칭하기", "23:00", "운동", repeatLabel = "\uC6D4,\uC218,\uAE08", repeatDays = setOf("\uC6D4", "\uC218", "\uAE08")),
    CreateRoutineOptionUiModel(3L, "영양제 먹기", "09:00", "건강", repeatLabel = "\uAE08\uC694\uC77C\uB9C8\uB2E4", repeatDays = setOf("\uAE08")),
    // PR 반영: 지원하지 않는 "공부" 카테고리를 "자기계발"로 변경
    CreateRoutineOptionUiModel(4L, "명상 하기", "23:30", "자기계발", repeatLabel = "\uAE08\uC694\uC77C\uB9C8\uB2E4", repeatDays = setOf("\uAE08")),
    CreateRoutineOptionUiModel(5L, "산책하기", "21:00", "운동", repeatLabel = "\uC8FC\uB9D0", repeatDays = setOf("\uD1A0", "\uC77C")),
    CreateRoutineOptionUiModel(6L, "책 읽기", "22:30", "자기계발", repeatLabel = "\uC6D4\uC694\uC77C\uB9C8\uB2E4", repeatDays = setOf("\uC6D4")),
    CreateRoutineOptionUiModel(7L, "일기 쓰기", "23:30", "자기계발", repeatLabel = "\uB9E4\uC77C", repeatDays = setOf("\uC77C", "\uC6D4", "\uD654", "\uC218", "\uBAA9", "\uAE08", "\uD1A0")),
)

val DefaultCreateRoutineOptions = SampleCreateRoutineOptions
