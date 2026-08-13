package com.li_routi.feature.grouproutine.vm

import com.li_routi.core.common.ui.routine.CategoryColor

import com.li_routi.feature.grouproutine.component.ChatEmoticonUiModel
import com.li_routi.feature.grouproutine.component.ChatMessageUiModel

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
    val completedCount: Int = 0,
    val totalCount: Int = 0,
    val totalLikeCount: Int = 0,
    val totalDisappointmentCount: Int = 0,
    val pokeCount: Int = 0,
    val isMe: Boolean = false,
)

data class GroupTodoUiModel(
    val id: Long,
    val title: String,
    val deadline: String,
    val category: String,
    val isDone: Boolean,
    val categoryColor: CategoryColor? = null,
)

data class CertificationPostUiModel(
    val id: Long,
    val memberId: Long,
    val userName: String,
    val body: String,
    val likeCount: Int,
    val timeAgo: String,
    val isMine: Boolean,
    val isLiked: Boolean = false,
    val disappointmentCount: Int = 0,
    val isDisappointed: Boolean = false,
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
    val categoryColor: CategoryColor? = null,
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
    // 서버가 그룹 상세에 OWNER 여부를 안 내려줌. isCurrentUserLeader는 기본값이 true라
    // 실제 권한 판단에 쓸 수 없어서, 방장인 게 증명된 경우에만 켜지는 플래그를 따로 둠
    // (방을 직접 만들었거나 / 나가기가 GROUP409_1로 막혔거나)
    val isConfirmedOwner: Boolean = false,
    val showOnlyMyCertifications: Boolean = false,
    val selectedCertificationMemberId: Long? = null,
    val isNewCertificationDialogVisible: Boolean = false,
    val searchInput: String = "",
    val roomNameInput: String = "",
    val inviteCodeInput: String = "",
    val groupInviteCode: String? = null,
    val unreadChatCount: Int = 0,
    val categoryColors: Map<String, CategoryColor> = emptyMap(),
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
    // 상단 카테고리 칩은 목록 필터 전용이라, 루틴에 붙일 카테고리는 따로 들고 있어야 함
    val routineDraftCategory: String = "",
    val isDeleteRoutineDialogVisible: Boolean = false,
    val isLeaveRoomDialogVisible: Boolean = false,
    // 방장이라 나갈 수 없을 때(GROUP409_1) 삭제로 유도하는 다이얼로그
    val isDeleteRoomDialogVisible: Boolean = false,
    val isKickMemberDialogVisible: Boolean = false,
    val isCategorySheetVisible: Boolean = false,
    val categoryInput: String = "",
    val categoryColorInput: CategoryColor? = null,
    val isRoutineColorSheetVisible: Boolean = false,
    val routineColorTargetId: Long? = null,
    val routineColorInput: CategoryColor? = null,
    val isMessageEditSheetVisible: Boolean = false,
    val messageDraft: String = "",
    val chatMessages: List<ChatMessageUiModel> = emptyList(),
    /** 다음에 과거 채팅을 더 불러올 때 서버에 실어 보낼 커서. null이면 최신 메시지부터 조회한다. */
    val chatNextCursor: Long? = null,
    /** false면 더 이상 불러올 과거 채팅이 없다는 뜻이라 추가 요청을 막는다. 최초 응답의 hasNext로 갱신되기 전까지는 과거 채팅 요청을 막기 위해 false로 시작한다. */
    val hasMoreChatHistory: Boolean = false,
    val chatDraftText: String = "",
    /** 스와이프로 지정된 답장 대상. null이면 채팅바 윗상자를 숨긴다. */
    val replyTarget: ChatMessageUiModel? = null,
    val chatEmoticons: List<ChatEmoticonUiModel> = emptyList(),
    val isChatLoading: Boolean = false,
    /** 채팅방 진입 후 최신 메시지 REST 조회(최초 1회)가 끝났는지. 소켓 구독이 이 조회보다 먼저
     * 시작되므로, 이력이 오기 전에 실시간 메시지가 먼저 도착해도 화면이 "맨 아래로 스크롤"을
     * 섣불리 소모하지 않도록 화면 쪽에서 이 값과 함께 확인한다. */
    val isChatHistoryLoaded: Boolean = false,
    val newCertifications: List<NewCertificationUiModel> = SampleNewCertifications,
    val routines: List<GroupRoutineUiModel> = emptyList(),
    val members: List<GroupMemberUiModel> = SampleGroupMembers,
    val todos: List<GroupTodoUiModel> = SampleGroupTodos,
    val posts: List<CertificationPostUiModel> = SampleCertificationPosts,
    // PR 반영: 중복 API 제출 방지용 상태 추가
    val isSubmitting: Boolean = false,
) {
    val visibleRoutines: List<GroupRoutineUiModel>
        get() = if (searchInput.isBlank()) {
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

    val visibleCertificationPosts: List<CertificationPostUiModel>
        get() = selectedCertificationMemberId?.let { memberId ->
            posts.filter { it.memberId == memberId }
        } ?: posts

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
        memberId = 1L,
        userName = "민지",
        body = "물 마시기 1일차 인증! 오늘도 잊지 않고 해냈어요.",
        likeCount = 1,
        timeAgo = "9시간 전",
        isMine = true,
    ),
    CertificationPostUiModel(
        id = 2L,
        memberId = 2L,
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

// mock 방으로 돌아갈 때 이전 서버 방 데이터를 지우고 되돌릴 기본값
val DefaultGroupMembers = SampleGroupMembers
val DefaultGroupTodos = SampleGroupTodos
