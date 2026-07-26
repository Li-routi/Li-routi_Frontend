package com.li_routi.feature.grouproutine.vm

enum class GroupRoutineScreenMode {
    List,
    Detail,
    CertificationCollection,
    GroupChat,
    GroupSettings,
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

data class CreateRoutineOptionUiModel(
    val id: Long,
    val title: String,
    val deadline: String,
    val category: String,
    val repeatLabel: String = "없음",
    val repeatDays: Set<String> = emptySet(),
    val isSelected: Boolean = false,
)

data class GroupRoutineUiState(
    val screenMode: GroupRoutineScreenMode = GroupRoutineScreenMode.List,
    val selectedRoutineId: Long? = null,
    val isActionSheetVisible: Boolean = false,
    val actionMessage: String? = null,
    val showOnlyMyCertifications: Boolean = false,
    val isEmptyState: Boolean = false,
    val searchInput: String = "",
    val roomNameInput: String = "",
    val inviteCodeInput: String = "",
    val selectedCategory: String = "전체",
    val categories: List<String> = listOf("전체", "건강", "운동", "공부"),
    val routineOptions: List<CreateRoutineOptionUiModel> = SampleCreateRoutineOptions,
    val isRoutineSettingSheetVisible: Boolean = false,
    val editingRoutineId: Long? = null,
    val routineDraftName: String = "",
    val routineDraftRepeatDays: Set<String> = emptySet(),
    val isDeleteRoutineDialogVisible: Boolean = false,
    val isCategorySheetVisible: Boolean = false,
    val categoryInput: String = "",
    val routines: List<GroupRoutineUiModel> = SampleGroupRoutines,
    val members: List<GroupMemberUiModel> = SampleGroupMembers,
    val todos: List<GroupTodoUiModel> = SampleGroupTodos,
    val posts: List<CertificationPostUiModel> = SampleCertificationPosts,
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
        id = 1L,
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
    GroupRoutineUiModel(
        id = 2L,
        title = "코딩",
        lastActiveLabel = "1시간 전 활동",
        memberCount = 3,
        routineCount = 6,
        statusLabel = "완료",
        isCompleted = true,
        todayCompletedCount = 6,
        todayTotalCount = 6,
        streakDays = 5,
        monthlyAchievementRate = 60,
        todayCertificationCount = 3,
    ),
    GroupRoutineUiModel(
        id = 3L,
        title = "코딩",
        lastActiveLabel = "1시간 전 활동",
        memberCount = 3,
        routineCount = 6,
        statusLabel = "완료",
        isCompleted = true,
        todayCompletedCount = 6,
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
    GroupTodoUiModel(3L, "물 마시기", "22:00", "건강", true),
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

private val SampleCreateRoutineOptions = listOf(
    CreateRoutineOptionUiModel(1L, "물 마시기", "22:00", "건강"),
    CreateRoutineOptionUiModel(2L, "스트레칭하기", "23:00", "운동"),
    CreateRoutineOptionUiModel(3L, "영양제 먹기", "09:00", "건강"),
    CreateRoutineOptionUiModel(4L, "명상 하기", "23:30", "공부"),
    CreateRoutineOptionUiModel(5L, "산책하기", "21:00", "운동"),
    CreateRoutineOptionUiModel(6L, "책 읽기", "22:30", "공부"),
    CreateRoutineOptionUiModel(7L, "일기 쓰기", "23:30", "공부"),
)

val DefaultCreateRoutineOptions = SampleCreateRoutineOptions
