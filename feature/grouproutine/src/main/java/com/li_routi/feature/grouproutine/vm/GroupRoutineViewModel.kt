package com.li_routi.feature.grouproutine.vm

import androidx.lifecycle.viewModelScope
import com.li_routi.core.common.android.architecture.BaseViewModel
import com.li_routi.core.common.kotlin.util.ResultState
import com.li_routi.core.common.ui.routine.CategoryColor
import com.li_routi.core.common.ui.routine.toCategoryColor
import com.li_routi.core.data.di.AuthContainer
import com.li_routi.core.data.di.ChatContainer
import com.li_routi.core.data.di.GroupRoutineContainer
import com.li_routi.core.domain.auth.GetMyInfoUseCase
import com.li_routi.core.domain.chat.ChatMessage
import com.li_routi.core.domain.chat.ChatMessageType
import com.li_routi.core.domain.chat.ConnectChatSocketUseCase
import com.li_routi.core.domain.chat.DisconnectChatSocketUseCase
import com.li_routi.core.domain.chat.GetChatDatesUseCase
import com.li_routi.core.domain.chat.GetChatMessagesUseCase
import com.li_routi.core.domain.chat.GetEmoticonsUseCase
import com.li_routi.core.domain.chat.NewChatMessage
import com.li_routi.core.domain.chat.ObserveChatMessagesUseCase
import com.li_routi.core.domain.chat.SendChatMessageUseCase
import com.li_routi.core.domain.chat.UpdateChatReadPositionUseCase
import com.li_routi.core.domain.grouproutine.CreateGroupRoutineCategoryUseCase
import com.li_routi.core.domain.grouproutine.CreateGroupRoutineUseCase
import com.li_routi.core.domain.grouproutine.CreateGroupUseCase
import com.li_routi.core.domain.grouproutine.DeleteGroupRoutineUseCase
import com.li_routi.core.domain.grouproutine.DeleteGroupUseCase
import com.li_routi.core.domain.grouproutine.DisappointGroupRoutineVerificationUseCase
import com.li_routi.core.domain.grouproutine.GetGroupJoinPreviewUseCase
import com.li_routi.core.domain.grouproutine.GetGroupDetailUseCase
import com.li_routi.core.domain.grouproutine.GetGroupRoutineCategoriesUseCase
import com.li_routi.core.domain.grouproutine.GetGroupRoutinesUseCase
import com.li_routi.core.domain.grouproutine.GetGroupRoutineVerificationsUseCase
import com.li_routi.core.domain.grouproutine.GetGroupInviteCodeUseCase
import com.li_routi.core.domain.grouproutine.GetParticipatingGroupsUseCase
import com.li_routi.core.domain.grouproutine.GetTodayGroupRoutinesUseCase
import com.li_routi.core.domain.grouproutine.GetUnreadGroupRoutineVerificationsUseCase
import com.li_routi.core.domain.grouproutine.GroupRoutineSchedule
import com.li_routi.core.domain.grouproutine.GroupRoutineStatus
import com.li_routi.core.domain.grouproutine.JoinGroupUseCase
import com.li_routi.core.domain.grouproutine.KickGroupMemberUseCase
import com.li_routi.core.domain.grouproutine.LeaveGroupResult
import com.li_routi.core.domain.grouproutine.LeaveGroupUseCase
import com.li_routi.core.domain.grouproutine.LikeGroupRoutineVerificationUseCase
import com.li_routi.core.domain.grouproutine.MarkGroupRoutineVerificationsReadUseCase
import com.li_routi.core.domain.grouproutine.NewGroupCategory
import com.li_routi.core.domain.grouproutine.NewGroupRoutine
import com.li_routi.core.domain.grouproutine.PokeGroupMemberUseCase
import com.li_routi.core.domain.grouproutine.RepeatDay
import com.li_routi.core.domain.grouproutine.SetGroupLockUseCase
import com.li_routi.core.domain.grouproutine.TransferGroupOwnerUseCase
import com.li_routi.core.domain.grouproutine.UndisappointGroupRoutineVerificationUseCase
import com.li_routi.core.domain.grouproutine.UnlikeGroupRoutineVerificationUseCase
import com.li_routi.core.domain.grouproutine.UpdateGroupMemberStatusMessageUseCase
import com.li_routi.core.domain.grouproutine.UpdateGroupNameUseCase
import com.li_routi.core.domain.grouproutine.UpdateGroupRoutineUseCase
import com.li_routi.feature.grouproutine.component.ChatEmoticonUiModel
import com.li_routi.feature.grouproutine.component.ChatMessageUiModel
import com.li_routi.feature.grouproutine.component.ChatReplyPreviewUiModel
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.YearMonth
import java.time.ZoneId
import java.util.UUID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private data class ChatReadTarget(
    val groupId: Long,
    val lastReadMessageId: Long,
)

/** 답장 원본이 이모티콘이라 텍스트 미리보기가 없을 때 대신 보여줄 라벨. */
private const val EmojiReplyPreviewLabel = "이모티콘"

private data class VerifiedRoutineKey(
    val groupId: Long,
    val routineId: Long,
)

private const val MAX_ROOM_NAME_LENGTH = 20
private const val MAX_ROUTINE_NAME_LENGTH = 20
private const val MAX_CATEGORY_NAME_LENGTH = 20
private const val MAX_STATUS_MESSAGE_LENGTH = 8

class GroupRoutineViewModel(
    private val createGroupUseCase: CreateGroupUseCase = GroupRoutineContainer.createGroupUseCase,
    private val getParticipatingGroupsUseCase: GetParticipatingGroupsUseCase = GroupRoutineContainer.getParticipatingGroupsUseCase,
    private val createGroupRoutineUseCase: CreateGroupRoutineUseCase = GroupRoutineContainer.createGroupRoutineUseCase,
    private val updateGroupRoutineUseCase: UpdateGroupRoutineUseCase = GroupRoutineContainer.updateGroupRoutineUseCase,
    private val getGroupDetailUseCase: GetGroupDetailUseCase = GroupRoutineContainer.getGroupDetailUseCase,
    private val deleteGroupUseCase: DeleteGroupUseCase = GroupRoutineContainer.deleteGroupUseCase,
    private val leaveGroupUseCase: LeaveGroupUseCase = GroupRoutineContainer.leaveGroupUseCase,
    private val joinGroupUseCase: JoinGroupUseCase = GroupRoutineContainer.joinGroupUseCase,
    private val getGroupJoinPreviewUseCase: GetGroupJoinPreviewUseCase = GroupRoutineContainer.getGroupJoinPreviewUseCase,
    private val setGroupLockUseCase: SetGroupLockUseCase = GroupRoutineContainer.setGroupLockUseCase,
    private val kickGroupMemberUseCase: KickGroupMemberUseCase = GroupRoutineContainer.kickGroupMemberUseCase,
    private val pokeGroupMemberUseCase: PokeGroupMemberUseCase = GroupRoutineContainer.pokeGroupMemberUseCase,
    private val updateGroupMemberStatusMessageUseCase: UpdateGroupMemberStatusMessageUseCase = GroupRoutineContainer.updateGroupMemberStatusMessageUseCase,
    private val updateGroupNameUseCase: UpdateGroupNameUseCase = GroupRoutineContainer.updateGroupNameUseCase,
    private val transferGroupOwnerUseCase: TransferGroupOwnerUseCase = GroupRoutineContainer.transferGroupOwnerUseCase,
    private val deleteGroupRoutineUseCase: DeleteGroupRoutineUseCase = GroupRoutineContainer.deleteGroupRoutineUseCase,
    private val getTodayGroupRoutinesUseCase: GetTodayGroupRoutinesUseCase = GroupRoutineContainer.getTodayGroupRoutinesUseCase,
    private val getGroupRoutinesUseCase: GetGroupRoutinesUseCase = GroupRoutineContainer.getGroupRoutinesUseCase,
    private val getGroupInviteCodeUseCase: GetGroupInviteCodeUseCase = GroupRoutineContainer.getGroupInviteCodeUseCase,
    private val getMyInfoUseCase: GetMyInfoUseCase = AuthContainer.getMyInfoUseCase,
    private val getGroupRoutineCategoriesUseCase: GetGroupRoutineCategoriesUseCase = GroupRoutineContainer.getGroupRoutineCategoriesUseCase,
    private val createGroupRoutineCategoryUseCase: CreateGroupRoutineCategoryUseCase = GroupRoutineContainer.createGroupRoutineCategoryUseCase,
    private val getGroupRoutineVerificationsUseCase: GetGroupRoutineVerificationsUseCase = GroupRoutineContainer.getGroupRoutineVerificationsUseCase,
    private val getUnreadGroupRoutineVerificationsUseCase: GetUnreadGroupRoutineVerificationsUseCase = GroupRoutineContainer.getUnreadGroupRoutineVerificationsUseCase,
    private val markGroupRoutineVerificationsReadUseCase: MarkGroupRoutineVerificationsReadUseCase = GroupRoutineContainer.markGroupRoutineVerificationsReadUseCase,
    private val likeGroupRoutineVerificationUseCase: LikeGroupRoutineVerificationUseCase = GroupRoutineContainer.likeGroupRoutineVerificationUseCase,
    private val unlikeGroupRoutineVerificationUseCase: UnlikeGroupRoutineVerificationUseCase = GroupRoutineContainer.unlikeGroupRoutineVerificationUseCase,
    private val disappointGroupRoutineVerificationUseCase: DisappointGroupRoutineVerificationUseCase = GroupRoutineContainer.disappointGroupRoutineVerificationUseCase,
    private val undisappointGroupRoutineVerificationUseCase: UndisappointGroupRoutineVerificationUseCase = GroupRoutineContainer.undisappointGroupRoutineVerificationUseCase,
    private val getChatMessagesUseCase: GetChatMessagesUseCase = ChatContainer.getChatMessagesUseCase,
    private val getChatDatesUseCase: GetChatDatesUseCase = ChatContainer.getChatDatesUseCase,
    private val updateChatReadPositionUseCase: UpdateChatReadPositionUseCase = ChatContainer.updateChatReadPositionUseCase,
    private val getEmoticonsUseCase: GetEmoticonsUseCase = ChatContainer.getEmoticonsUseCase,
    private val connectChatSocketUseCase: ConnectChatSocketUseCase = ChatContainer.connectChatSocketUseCase,
    private val observeChatMessagesUseCase: ObserveChatMessagesUseCase = ChatContainer.observeChatMessagesUseCase,
    private val sendChatMessageUseCase: SendChatMessageUseCase = ChatContainer.sendChatMessageUseCase,
    private val disconnectChatSocketUseCase: DisconnectChatSocketUseCase = ChatContainer.disconnectChatSocketUseCase,
) : BaseViewModel() {
    private val locallyCompletedRoutineKeys = mutableSetOf<VerifiedRoutineKey>()

    private val _uiState = MutableStateFlow(GroupRoutineUiState())
    val uiState: StateFlow<GroupRoutineUiState> = _uiState.asStateFlow()

    private var chatSocketJob: Job? = null
    private var chatHistoryJob: Job? = null
    private var chatReadJob: Job? = null
    private var routineVerificationsJob: Job? = null
    private var groupDetailJob: Job? = null
    private var groupRoutineCategoriesJob: Job? = null
    private var latestChatReadTarget: ChatReadTarget? = null
    private var unreadRoutineVerificationsJob: Job? = null

    private var backendGroupId: Long? = null

    private fun currentGroupId(): Long? = backendGroupId

    private fun currentRoutineId(): Long? = _uiState.value.todos.firstOrNull()?.id

    init {
        loadParticipatingGroups()
    }

    private var serverCategoryIds: Map<String, Long> = emptyMap()

    private var myMemberId: Long? = null

    fun onDismissActionMessage() {
        _uiState.update { it.copy(actionMessage = null) }
    }

    fun onRoutineClick(routineId: Long) {
        cancelGroupScopedJobs()
        _uiState.update {
            it.copy(
                screenMode = GroupRoutineScreenMode.Detail,
                isDetailLoading = routineId > 0L,
                selectedRoutineId = routineId,
                selectedMemberId = null,
                isNewCertificationDialogVisible = false,
                members = emptyList(),
                todos = emptyList(),
                actionMessage = null,
            )
        }
        if (routineId > 0L) {
            backendGroupId = routineId
            loadGroupDetail(routineId)
            loadGroupRoutineCategories()
            loadTodayRoutines(routineId)
            loadUnreadRoutineVerifications()
        } else {
            backendGroupId = null
            _uiState.update {
                it.copy(members = DefaultGroupMembers, todos = DefaultGroupTodos, groupInviteCode = null)
            }
        }
        _uiState.update {
            it.copy(
                isCurrentUserLeader = false,
                isConfirmedOwner = false,
            )
        }
    }

    fun refreshSelectedGroup(refreshMemberDetail: Boolean = true) {
        val groupId = currentGroupId() ?: return
        if (refreshMemberDetail) {
            loadGroupDetail(groupId)
        }
        loadGroupRoutineCategories()
        loadTodayRoutines(groupId)
    }

    fun markRoutineVerified(routineId: Long?) {
        if (routineId == null) return
        val groupId = currentGroupId() ?: return
        val state = _uiState.value
        val memberCount = state.members.size.takeIf { it > 0 }
            ?: state.routines.firstOrNull { it.id == groupId }?.memberCount
            ?: 0
        val verificationScore = 1.5
        val completionThreshold = memberCount / 2.0

        if (memberCount > 0 && verificationScore > completionThreshold) {
            locallyCompletedRoutineKeys += VerifiedRoutineKey(groupId, routineId)
            _uiState.update { state ->
                state.copy(
                    todos = state.todos.map { todo ->
                        if (todo.id == routineId) todo.copy(isDone = true) else todo
                    },
                )
            }
        }
        refreshVerificationDecisionState()
    }

    private fun refreshVerificationDecisionState() {
        val groupId = currentGroupId() ?: return
        loadTodayRoutines(groupId)
        loadGroupDetail(groupId)
        loadGroupRoutineCategories()
        loadParticipatingGroups()
    }

    private fun loadParticipatingGroups() {
        viewModelScope.launch {
            when (val result = getParticipatingGroupsUseCase()) {
                is ResultState.Success -> {
                    val routines = result.data.map { group ->
                        GroupRoutineUiModel(
                            id = group.groupId,
                            title = group.groupName,
                            lastActiveLabel = "1\uC2DC\uAC04 \uC804 \uD65C\uB3D9",
                            memberCount = group.activeMemberCount,
                            routineCount = group.activeRoutineCount,
                            statusLabel = if (
                                group.todayAssignedRoutineCount > 0 &&
                                group.todayAssignedRoutineCount == group.todayCompletedRoutineCount
                            ) {
                                "\uC644\uB8CC"
                            } else {
                                "\uC9C4\uD589\uC911"
                            },
                            isCompleted = group.todayAssignedRoutineCount > 0 &&
                                group.todayAssignedRoutineCount == group.todayCompletedRoutineCount,
                            todayCompletedCount = group.todayCompletedRoutineCount,
                            todayTotalCount = group.todayAssignedRoutineCount,
                            streakDays = group.currentStreak,
                            monthlyAchievementRate = group.monthlyAchievementRate,
                            todayCertificationCount = group.todayGroupVerificationCount,
                        )
                    }
                    _uiState.update {
                        it.copy(
                            routines = routines,
                            isListLoading = false,
                        )
                    }
                }

                is ResultState.Error -> _uiState.update {
                    it.copy(actionMessage = result.message, isListLoading = false)
                }
                ResultState.Loading -> Unit
            }
        }
    }

    fun onGroupRoutineTabExit() {
        val leavingChat = _uiState.value.screenMode == GroupRoutineScreenMode.GroupChat
        backendGroupId = null
        cancelGroupScopedJobs()
        _uiState.update {
            it.copy(
                screenMode = GroupRoutineScreenMode.List,
                isDetailLoading = false,
                isCertificationLoading = false,
                selectedRoutineId = null,
                selectedMemberId = null,
                showOnlyMyCertifications = false,
                selectedCertificationMemberId = null,
                isNewCertificationDialogVisible = false,
                actionMessage = null,
            )
        }
        if (leavingChat) leaveChatSocket()
    }

    fun onBackClick() {
        val leavingChat = _uiState.value.screenMode == GroupRoutineScreenMode.GroupChat
        _uiState.update { state ->
            when (state.screenMode) {
                GroupRoutineScreenMode.Detail -> state.copy(
                    screenMode = GroupRoutineScreenMode.List,
                    selectedRoutineId = null,
                    selectedMemberId = null,
                    showOnlyMyCertifications = false,
                    selectedCertificationMemberId = null,
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
                    selectedCategory = "\uC804\uCCB4",
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
        if (leavingChat) leaveChatSocket()
    }

    fun onCreateFlowCloseClick() {
        _uiState.update {
            it.copy(
                screenMode = GroupRoutineScreenMode.List,
                roomNameInput = "",
                routineOptions = DefaultCreateRoutineOptions,
                selectedCategory = "\uC804\uCCB4",
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
                isCertificationLoading = true,
                showOnlyMyCertifications = false,
                selectedCertificationMemberId = null,
                posts = emptyList(),
                actionMessage = null,
            )
        }
        loadRoutineVerifications()
    }

    fun onChatClick() {
        _uiState.update {
            it.copy(
                screenMode = GroupRoutineScreenMode.GroupChat,
                actionMessage = null,
            )
        }
        if (_uiState.value.chatEmoticons.isEmpty()) loadEmoticons()
        enterChatSocket()
    }

    fun onChatMessageChange(value: String) {
        _uiState.update { it.copy(chatDraftText = value) }
    }

    fun onChatSendClick() {
        val text = _uiState.value.chatDraftText.trim()
        if (text.isBlank()) return
        val groupId = currentGroupId() ?: run {
            _uiState.update { it.copy(actionMessage = "그룹 ID를 찾을 수 없습니다.") }
            return
        }

        val replyTarget = _uiState.value.replyTarget

        val message = NewChatMessage(
            clientMessageId = UUID.randomUUID().toString(),
            type = ChatMessageType.TEXT,
            content = text,
            emoticonCode = null,
            replyToMessageId = replyTarget?.id,
        )
        viewModelScope.launch {
            when (val result = sendChatMessageUseCase(groupId, message)) {
                is ResultState.Success -> _uiState.update { it.copy(chatDraftText = "", replyTarget = null) }
                is ResultState.Error -> _uiState.update { it.copy(actionMessage = result.message) }
                ResultState.Loading -> Unit
            }
        }
    }

    fun onReplyTargetSelected(message: ChatMessageUiModel) {
        _uiState.update { it.copy(replyTarget = message) }
    }

    fun onReplyTargetCleared() {
        _uiState.update { it.copy(replyTarget = null) }
    }

    fun onChatEmojiSelected(emoticon: ChatEmoticonUiModel) {
        val groupId = currentGroupId() ?: run {
            _uiState.update { it.copy(actionMessage = "그룹 ID를 찾을 수 없습니다.") }
            return
        }

        val replyTarget = _uiState.value.replyTarget
        val message = NewChatMessage(
            clientMessageId = UUID.randomUUID().toString(),
            type = ChatMessageType.EMOTICON,
            content = null,
            emoticonCode = emoticon.code,
            replyToMessageId = replyTarget?.id,
        )
        viewModelScope.launch {
            when (val result = sendChatMessageUseCase(groupId, message)) {
                is ResultState.Success -> _uiState.update { it.copy(replyTarget = null) }
                is ResultState.Error -> _uiState.update { it.copy(actionMessage = result.message) }
                ResultState.Loading -> Unit
            }
        }
    }

    /**
     * 채팅 소켓에 연결하고 REST로 최근 메시지를 불러온다.
     *
     * 그룹이 바뀌었을 수도 있으므로(목록 → 다른 채팅방으로 재진입 포함), 이전 그룹의 채팅 상태
     * (메시지 목록/페이지 커서/답장 대상/임시 입력 등)를 먼저 비운다 — 안 그러면 새 그룹의 이력과
     * 이전에 보던 그룹의 메시지가 한 리스트에 섞여서 그룹별로 채팅이 분리되지 않는 버그가 있었다.
     */
    private fun enterChatSocket() {
        val groupId = currentGroupId() ?: run {
            _uiState.update { it.copy(actionMessage = "그룹 ID를 찾을 수 없습니다.") }
            return
        }

        chatSocketJob?.cancel()
        _uiState.update {
            it.copy(
                isChatHistoryLoaded = false,
                chatMessages = emptyList(),
                chatNextCursor = null,
                hasMoreChatHistory = false,
                unreadChatCount = 0,
                replyTarget = null,
                chatDraftText = "",
                selectedChatDate = null,
            )
        }
        chatSocketJob = viewModelScope.launch {
            when (val result = connectChatSocketUseCase(groupId)) {
                is ResultState.Error -> {
                    _uiState.update { it.copy(actionMessage = result.message) }
                    return@launch
                }
                is ResultState.Success -> Unit
                ResultState.Loading -> Unit
            }

            launch {
                observeChatMessagesUseCase().collect { incoming ->
                    val myMemberId = _uiState.value.members.firstOrNull { it.isMe }?.id
                    val shouldMarkRead = _uiState.value.screenMode == GroupRoutineScreenMode.GroupChat
                    _uiState.update { state ->
                        if (state.chatMessages.any { it.id == incoming.id }) return@update state
                        val isMine = incoming.senderId == myMemberId
                        state.copy(
                            chatMessages = state.chatMessages + incoming.toUiModel(isMine = isMine),
                            unreadChatCount = if (shouldMarkRead || isMine) {
                                state.unreadChatCount
                            } else {
                                state.unreadChatCount + 1
                            },
                        )
                    }
                    if (shouldMarkRead) {
                        markChatRead(groupId, incoming.id)
                    }
                }
            }

            loadChatMessages(groupId)
        }
    }

    private fun leaveChatSocket() {
        chatSocketJob?.cancel()
        chatSocketJob = null
        // A방에서 시작된 과거 채팅 로딩이 B방 이동 후에도 살아남아 응답으로 B방 상태를 덮어쓰지 않도록 함께 취소한다.
        chatHistoryJob?.cancel()
        chatHistoryJob = null
        viewModelScope.launch { disconnectChatSocketUseCase() }
    }

    /**
     * cursor가 null이면 최신 50개를, 값이 있으면 그 커서보다 오래된 과거 메시지 50개를 불러온다.
     * [date]("yyyy-MM-dd")를 주면 그 날짜부터 과거로 조회한다 — 이후 이어지는 과거 스크롤
     * ([onChatScrolledToTop])도 [GroupRoutineUiState.selectedChatDate]로 같은 date를 계속
     * 함께 실어 보내야 한다.
     */
    private suspend fun loadChatMessages(groupId: Long, cursor: Long? = null, date: String? = null) {
        _uiState.update { it.copy(isChatLoading = true) }
        when (val result = getChatMessagesUseCase(groupId = groupId, cursor = cursor, size = 50, date = date)) {
            is ResultState.Success -> {
                // 응답이 오는 사이 다른 방으로 이동했다면(현재 활성 방 ID != 요청 당시 방 ID) 상태 반영을 건너뛴다.
                if (currentGroupId() != groupId) {
                    _uiState.update { it.copy(isChatLoading = false) }
                    return
                }
                val myMemberId = _uiState.value.members.firstOrNull { it.isMe }?.id
                val historyMessages = result.data.messages.map { it.toUiModel(isMine = it.senderId == myMemberId) }
                _uiState.update { state ->
                    // historyMessages(오래된 페이지)와 state.chatMessages(기존 목록)는 각각
                    // 이미 id 오름차순이고 historyMessages의 모든 id가 더 작으므로 이어붙이기만
                    // 하면 된다. distinctBy는 커서 경계에서 중복 응답이 와도 안전하게 걸러낸다.
                    val merged = (historyMessages.sortedBy { it.id } + state.chatMessages).distinctBy { it.id }
                    state.copy(
                        chatMessages = merged,
                        chatNextCursor = result.data.nextCursor,
                        hasMoreChatHistory = result.data.hasNext,
                        isChatLoading = false,
                        isChatHistoryLoaded = state.isChatHistoryLoaded || cursor == null,
                        unreadChatCount = if (historyMessages.isEmpty()) 0 else state.unreadChatCount,
                    )
                }
                // cursor가 null이면(최신 메시지 조회) 마지막 메시지까지 읽음 처리한다.
                if (cursor == null) {
                    historyMessages.lastOrNull()?.let { last -> markChatRead(groupId, last.id) }
                }
            }

            is ResultState.Error -> _uiState.update {
                it.copy(
                    actionMessage = result.message,
                    isChatLoading = false,
                    isChatHistoryLoaded = it.isChatHistoryLoaded || cursor == null,
                )
            }
            ResultState.Loading -> _uiState.update { it.copy(isChatLoading = false) }
        }
    }

    /** 채팅 목록을 위로 스크롤해 맨 위 근처에 도달했을 때 호출해 이전 대화를 이어서 불러온다. */
    fun onChatScrolledToTop() {
        val state = _uiState.value
        if (state.isChatLoading || !state.hasMoreChatHistory) return
        val groupId = currentGroupId() ?: return
        chatHistoryJob?.cancel()
        chatHistoryJob = viewModelScope.launch {
            loadChatMessages(groupId, cursor = state.chatNextCursor, date = state.selectedChatDate?.toString())
        }
    }

    /**
     * 캘린더에서 날짜를 골라 그 날짜부터 과거 채팅을 조회한다. 기존 목록/커서를 비우고 그 날짜를
     * 새 앵커로 다시 불러온다 — 이후 [onChatScrolledToTop]도 이 [date]를 계속 함께 실어 보낸다.
     */
    fun onChatDateSelected(date: LocalDate) {
        val groupId = currentGroupId() ?: return
        chatHistoryJob?.cancel()
        _uiState.update {
            it.copy(
                selectedChatDate = date,
                chatMessages = emptyList(),
                chatNextCursor = null,
                hasMoreChatHistory = false,
                isChatHistoryLoaded = false,
            )
        }
        chatHistoryJob = viewModelScope.launch { loadChatMessages(groupId, date = date.toString()) }
    }

    /**
     * 캘린더 시트에 표시 중인 달이 바뀔 때마다 그 달의 채팅 존재 날짜를 조회해 누적 캐시한다.
     * 실패해도 조용히 무시한다 — 그 달의 날짜 제한만 못 걸릴 뿐 캘린더 자체는 계속 쓸 수 있다.
     */
    fun onCalendarMonthChange(yearMonth: YearMonth) {
        val groupId = currentGroupId() ?: return
        val from = yearMonth.atDay(1).toString()
        val to = yearMonth.plusMonths(1).atDay(1).toString()
        viewModelScope.launch {
            when (val result = getChatDatesUseCase(groupId, from, to)) {
                is ResultState.Success -> _uiState.update { state ->
                    state.copy(chatDates = state.chatDates + result.data.mapNotNull(::parseIsoDateOrNull))
                }
                is ResultState.Error -> Unit
                ResultState.Loading -> Unit
            }
        }
    }

    private fun markChatRead(groupId: Long, lastReadMessageId: Long) {
        val currentTarget = latestChatReadTarget
        if (
            chatReadJob?.isActive == true &&
            currentTarget?.groupId == groupId &&
            currentTarget.lastReadMessageId >= lastReadMessageId
        ) {
            return
        }

        latestChatReadTarget = ChatReadTarget(groupId, lastReadMessageId)
        if (chatReadJob?.isActive == true) return

        chatReadJob = viewModelScope.launch {
            while (true) {
                val target = latestChatReadTarget ?: return@launch
                when (val result = updateChatReadPositionUseCase(target.groupId, target.lastReadMessageId)) {
                    is ResultState.Success -> {
                        if (latestChatReadTarget == target) {
                            latestChatReadTarget = null
                            _uiState.update { it.copy(unreadChatCount = 0) }
                            return@launch
                        }
                    }

                    is ResultState.Error -> {
                        _uiState.update { it.copy(actionMessage = result.message) }
                        return@launch
                    }

                    ResultState.Loading -> Unit
                }
            }
        }
    }

    private fun loadEmoticons() {
        viewModelScope.launch {
            when (val result = getEmoticonsUseCase()) {
                is ResultState.Success -> {
                    _uiState.update {
                        it.copy(
                            chatEmoticons = result.data.map { emoticon ->
                                ChatEmoticonUiModel(id = emoticon.id, code = emoticon.code, assetUrl = emoticon.assetUrl)
                            },
                        )
                    }
                }

                is ResultState.Error -> _uiState.update { it.copy(actionMessage = result.message) }
                ResultState.Loading -> Unit
            }
        }
    }

    override fun onCleared() {
        chatSocketJob?.cancel()
        chatHistoryJob?.cancel()
        CoroutineScope(Dispatchers.IO).launch { disconnectChatSocketUseCase() }
        super.onCleared()
    }

    fun onMessageEditClick() {
        _uiState.update { state ->
            val myMessage = state.members.firstOrNull { it.isMe }?.message.orEmpty()
            state.copy(isMessageEditSheetVisible = true, messageDraft = myMessage, actionMessage = null)
        }
    }

    fun onMessageDraftChange(value: String) {
        _uiState.update {
            it.copy(
                messageDraft = value.take(MAX_STATUS_MESSAGE_LENGTH),
                actionMessage = null,
            )
        }
    }

    fun onDismissMessageEditSheet() {
        _uiState.update { it.copy(isMessageEditSheetVisible = false) }
    }

    fun onMessageEditConfirmClick() {
        val message = _uiState.value.messageDraft.trim()
        if (message.isBlank()) {
            _uiState.update { it.copy(actionMessage = "상태 메시지를 입력해 주세요.") }
            return
        }
        val groupId = currentGroupId()
        if (groupId == null) {
            applyMyStatusMessage(message)
            return
        }

        if (_uiState.value.isSubmitting) return
        _uiState.update { it.copy(isSubmitting = true) }
        viewModelScope.launch {
            try {
                when (val result = updateGroupMemberStatusMessageUseCase(groupId, message)) {
                    is ResultState.Success -> applyMyStatusMessage(result.data)
                    is ResultState.Error -> _uiState.update {
                        it.copy(isMessageEditSheetVisible = false, actionMessage = result.message)
                    }

                    ResultState.Loading -> Unit
                }
            } finally {
                _uiState.update { it.copy(isSubmitting = false) }
            }
        }
    }

    private fun applyMyStatusMessage(message: String) {
        _uiState.update { state ->
            state.copy(
                members = state.members.map { if (it.isMe) it.copy(message = message) else it },
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

    fun onLeaveRoomClick() {
        _uiState.update { state ->
            if (state.isConfirmedOwner) {
                state.copy(isDeleteRoomDialogVisible = true)
            } else {
                state.copy(isLeaveRoomDialogVisible = true)
            }
        }
    }

    fun onDismissLeaveRoomDialog() {
        _uiState.update { it.copy(isLeaveRoomDialogVisible = false) }
    }

    fun onDismissDeleteRoomDialog() {
        _uiState.update { it.copy(isDeleteRoomDialogVisible = false) }
    }

    fun onLeaveRoomConfirmClick() {
        val groupId = currentGroupId() ?: run {
            _uiState.update { it.copy(isLeaveRoomDialogVisible = false, actionMessage = "그룹 ID를 찾을 수 없습니다.") }
            return
        }
        if (_uiState.value.isSubmitting) return

        _uiState.update { it.copy(isSubmitting = true) }
        viewModelScope.launch {
            try {
                when (val result = leaveGroupUseCase(groupId)) {
                    is ResultState.Success -> when (result.data) {
                        LeaveGroupResult.Left -> exitRoom(groupId, "그룹방을 나갔어요.")
                        LeaveGroupResult.OwnerMustDelete -> _uiState.update {
                            it.copy(
                                isLeaveRoomDialogVisible = false,
                                isDeleteRoomDialogVisible = true,
                                isConfirmedOwner = true,
                            )
                        }
                    }

                    is ResultState.Error -> _uiState.update {
                        it.copy(isLeaveRoomDialogVisible = false, actionMessage = result.message)
                    }

                    ResultState.Loading -> Unit
                }
            } finally {
                _uiState.update { it.copy(isSubmitting = false) }
            }
        }
    }

    fun onDeleteRoomConfirmClick() {
        val groupId = currentGroupId() ?: run {
            _uiState.update { it.copy(isDeleteRoomDialogVisible = false, actionMessage = "그룹 ID를 찾을 수 없습니다.") }
            return
        }
        if (_uiState.value.isSubmitting) return

        _uiState.update { it.copy(isSubmitting = true) }
        viewModelScope.launch {
            try {
                when (val result = deleteGroupUseCase(groupId)) {
                    is ResultState.Success -> exitRoom(groupId, "그룹방을 삭제했어요.")
                    is ResultState.Error -> _uiState.update {
                        it.copy(isDeleteRoomDialogVisible = false, actionMessage = result.message)
                    }

                    ResultState.Loading -> Unit
                }
            } finally {
                _uiState.update { it.copy(isSubmitting = false) }
            }
        }
    }

    private fun exitRoom(groupId: Long, message: String) {
        backendGroupId = null
        _uiState.update { state ->
            state.copy(
                screenMode = GroupRoutineScreenMode.List,
                routines = state.routines.filterNot { it.id == groupId },
                selectedRoutineId = null,
                selectedMemberId = null,
                groupInviteCode = null,
                members = emptyList(),
                todos = emptyList(),
                isConfirmedOwner = false,
                isLeaveRoomDialogVisible = false,
                isDeleteRoomDialogVisible = false,
                actionMessage = message,
            )
        }
    }

    private fun loadGroupDetail(groupId: Long) {
        groupDetailJob?.cancel()
        groupDetailJob = viewModelScope.launch {
            if (myMemberId == null) {
                when (val myInfo = getMyInfoUseCase()) {
                    is ResultState.Success -> myMemberId = myInfo.data.memberId
                    is ResultState.Error -> {
                        if (currentGroupId() == groupId) {
                            _uiState.update {
                                it.copy(
                                    actionMessage = myInfo.message,
                                    isDetailLoading = false,
                                )
                            }
                        }
                        return@launch
                    }

                    ResultState.Loading -> return@launch
                }
            }

            if (currentGroupId() != groupId) return@launch

            when (val result = getGroupDetailUseCase(groupId)) {
                is ResultState.Success -> {
                    if (currentGroupId() != groupId) return@launch
                    val detail = result.data
                    _uiState.update { state ->
                        state.copy(
                            groupInviteCode = detail.inviteCode,
                            members = detail.members.map { member ->
                                GroupMemberUiModel(
                                    id = member.memberId,
                                    name = member.name,
                                    message = member.statusMessage.orEmpty(),
                                    streak = member.currentStreak,
                                    completedCount = member.completedCount.toInt(),
                                    totalCount = member.totalCount.toInt(),
                                    totalLikeCount = member.totalLikeCount.toInt(),
                                    totalDisappointmentCount = state.members
                                        .firstOrNull { it.id == member.memberId }
                                        ?.totalDisappointmentCount
                                        ?: 0,
                                    pokeCount = member.totalPokeCount,
                                    isMe = member.memberId == myMemberId,
                                    equippedImageUrls = member.equippedImageUrls,
                                )
                            },
                            routines = state.routines.map { routine ->
                                if (routine.id != groupId) {
                                    routine
                                } else {
                                    routine.copy(
                                        title = detail.groupName,
                                        memberCount = detail.members.size,
                                    )
                                }
                            },
                            isCurrentUserLeader = detail.isCurrentUserOwner,
                            isConfirmedOwner = detail.isCurrentUserOwner,
                            isDetailLoading = false,
                        )
                    }
                }

                is ResultState.Error -> {
                    if (currentGroupId() != groupId) return@launch
                    _uiState.update {
                        it.copy(actionMessage = result.message, isDetailLoading = false)
                    }
                }
                ResultState.Loading -> Unit
            }
        }
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
                is ResultState.Error -> _uiState.update { it.copy(actionMessage = result.message) }
                ResultState.Loading -> Unit
            }
        }
    }

    private fun loadGroupRoutineCategories() {
        val groupId = currentGroupId() ?: run {
            _uiState.update { it.copy(actionMessage = "그룹 ID를 찾을 수 없습니다.") }
            return
        }

        groupRoutineCategoriesJob?.cancel()
        groupRoutineCategoriesJob = viewModelScope.launch {
            when (val result = getGroupRoutineCategoriesUseCase(groupId)) {
                is ResultState.Success -> {
                    if (currentGroupId() != groupId) return@launch
                    val categoryNames = result.data.categories.map { it.name }
                    serverCategoryIds = result.data.categories.associate { category ->
                        category.name to category.categoryId
                    }
                    val categoryColors = result.data.categories
                        .mapNotNull { category ->
                            category.color.toCategoryColor()?.let { color -> category.name to color }
                        }
                        .toMap()
                    _uiState.update { state ->
                        val allLabel = state.categories.firstOrNull().orEmpty()
                        val mergedColors = state.categoryColors + categoryColors
                        state.copy(
                            categories = listOf(allLabel) + categoryNames,
                            categoryColors = mergedColors,
                            routineOptions = state.routineOptions.map { option ->
                                option.copy(categoryColor = mergedColors[option.category] ?: option.categoryColor)
                            },
                            todos = state.todos.map { todo ->
                                todo.copy(categoryColor = mergedColors[todo.category] ?: todo.categoryColor)
                            },
                        )
                    }
                }

                is ResultState.Error -> {
                    if (currentGroupId() != groupId) return@launch
                    _uiState.update { it.copy(actionMessage = result.message) }
                }
                ResultState.Loading -> Unit
            }
        }
    }

    private fun cancelGroupScopedJobs() {
        groupDetailJob?.cancel()
        groupRoutineCategoriesJob?.cancel()
        unreadRoutineVerificationsJob?.cancel()
        routineVerificationsJob?.cancel()
        todayRoutinesJob?.cancel()
        groupRoutinesJob?.cancel()
    }

    private fun loadRoutineVerifications() {
        val groupId = currentGroupId() ?: run {
            _uiState.update { it.copy(actionMessage = "그룹 ID를 찾을 수 없습니다.") }
            return
        }
        val routineIds = (_uiState.value.todos.map { it.id } + _uiState.value.routineOptions.map { it.id })
            .filter { it > 0L }
            .distinct()

        routineVerificationsJob?.cancel()
        routineVerificationsJob = viewModelScope.launch {
            if (routineIds.isEmpty()) {
                if (currentGroupId() == groupId) {
                    _uiState.update { it.copy(posts = emptyList(), isCertificationLoading = false) }
                }
                return@launch
            }

            val myMemberId = _uiState.value.members.firstOrNull { it.isMe }?.id
            val posts = mutableListOf<CertificationPostUiModel>()
            var lastErrorMessage: String? = null

            routineIds.forEach { routineId ->
                when (
                    val result = getGroupRoutineVerificationsUseCase(
                        groupId = groupId,
                        routineId = routineId,
                        cursor = null,
                        size = 20,
                    )
                ) {
                    is ResultState.Success -> {
                        posts += result.data.verifications.map { item ->
                            CertificationPostUiModel(
                                id = item.verificationId,
                                memberId = item.memberId,
                                userName = item.nickname,
                                body = item.content.orEmpty(),
                                likeCount = item.likeCount.toInt(),
                                timeAgo = item.verifiedAt.toRelativeTimeLabel(),
                                isMine = item.memberId == myMemberId,
                                isLiked = item.liked,
                                imageUrl = item.imageUrl,
                                verifiedAtMillis = item.verifiedAt.toEpochMillisOrNull() ?: 0L,
                            )
                        }
                    }

                    is ResultState.Error -> lastErrorMessage = result.message
                    ResultState.Loading -> Unit
                }
            }

            if (currentGroupId() == groupId) {
                _uiState.update {
                    it.copy(
                        posts = posts
                            .distinctBy(CertificationPostUiModel::id)
                            .sortedByDescending(CertificationPostUiModel::verifiedAtMillis),
                        isCertificationLoading = false,
                        actionMessage = if (posts.isEmpty()) lastErrorMessage else it.actionMessage,
                    )
                }
            }
        }
    }

    fun onMemberClick(memberId: Long) {
        _uiState.update { it.copy(selectedMemberId = memberId, actionMessage = null) }
        currentGroupId()?.let(::loadGroupDetail)
    }

    fun onDismissMemberDialog() {
        _uiState.update { it.copy(selectedMemberId = null) }
    }

    fun onDismissNewCertificationDialog() {
        val groupId = currentGroupId()
        val lastReadId = _uiState.value.newCertificationLastReadId
            ?: _uiState.value.newCertifications.maxOfOrNull { it.id }
        _uiState.update {
            it.copy(
                isNewCertificationDialogVisible = false,
                newCertifications = emptyList(),
                newCertificationLastReadId = null,
            )
        }

        if (groupId == null || lastReadId == null) return

        markNewCertificationsRead(groupId, lastReadId)
    }

    private fun markNewCertificationsRead(groupId: Long, lastReadId: Long) {
        viewModelScope.launch {
            when (val result = markGroupRoutineVerificationsReadUseCase(groupId, lastReadId)) {
                is ResultState.Success -> Unit
                is ResultState.Error -> {
                    if (currentGroupId() == groupId) {
                        _uiState.update { it.copy(actionMessage = result.message) }
                    }
                }
                ResultState.Loading -> Unit
            }
        }
    }

    private fun advanceNewCertification(verificationId: Long) {
        val groupId = currentGroupId()
        val lastReadId = _uiState.value.newCertificationLastReadId
        var finished = false

        _uiState.update { state ->
            val remaining = state.newCertifications.filterNot { it.id == verificationId }
            finished = remaining.isEmpty()
            state.copy(
                newCertifications = remaining,
                isNewCertificationDialogVisible = remaining.isNotEmpty(),
                newCertificationLastReadId = if (remaining.isEmpty()) null else state.newCertificationLastReadId,
            )
        }

        if (finished && groupId != null && lastReadId != null) {
            markNewCertificationsRead(groupId, lastReadId)
        }
    }

    private fun updateMemberReactionCount(
        memberId: Long?,
        likeDelta: Int = 0,
        disappointmentDelta: Int = 0,
    ) {
        if (memberId == null || memberId <= 0L) return

        _uiState.update { state ->
            state.copy(
                members = state.members.map { member ->
                    if (member.id != memberId) {
                        member
                    } else {
                        member.copy(
                            totalLikeCount = (member.totalLikeCount + likeDelta).coerceAtLeast(0),
                            totalDisappointmentCount = (
                                member.totalDisappointmentCount + disappointmentDelta
                            ).coerceAtLeast(0),
                        )
                    }
                },
            )
        }
    }

    private fun loadUnreadRoutineVerifications() {
        val groupId = currentGroupId() ?: return

        unreadRoutineVerificationsJob?.cancel()
        unreadRoutineVerificationsJob = viewModelScope.launch {
            when (
                val result = getUnreadGroupRoutineVerificationsUseCase(
                    groupId = groupId,
                    cursor = null,
                    size = 20,
                )
            ) {
                is ResultState.Success -> {
                    if (currentGroupId() != groupId) return@launch

                    val unreadCertifications = result.data.verifications.map { item ->
                        NewCertificationUiModel(
                            id = item.verificationId,
                            memberName = item.authorName,
                            routineName = item.routineName,
                            message = item.content.orEmpty(),
                            memberId = item.authorMemberId,
                            imageUrl = item.imageUrl,
                        )
                    }
                    _uiState.update {
                        it.copy(
                            newCertifications = unreadCertifications,
                            isNewCertificationDialogVisible = unreadCertifications.isNotEmpty(),
                            newCertificationLastReadId = unreadCertifications.maxOfOrNull { certification -> certification.id },
                        )
                    }
                }

                is ResultState.Error -> {
                    if (currentGroupId() == groupId) {
                        _uiState.update { it.copy(actionMessage = result.message) }
                    }
                }
                ResultState.Loading -> Unit
            }
        }
    }

    fun onCertificationLikeClick(verificationId: Long, currentlyLiked: Boolean) {
        val groupId = currentGroupId() ?: run {
            _uiState.update { it.copy(actionMessage = "그룹 ID를 찾을 수 없습니다.") }
            return
        }
        val memberId = _uiState.value.posts.firstOrNull { it.id == verificationId }?.memberId

        viewModelScope.launch {
            val result = if (currentlyLiked) {
                unlikeGroupRoutineVerificationUseCase(groupId = groupId, verificationId = verificationId)
            } else {
                likeGroupRoutineVerificationUseCase(groupId = groupId, verificationId = verificationId)
            }

            when (result) {
                is ResultState.Success -> {
                    val like = result.data
                    _uiState.update { state ->
                        state.copy(
                            posts = state.posts.map { post ->
                                if (post.id == like.verificationId) {
                                    post.copy(
                                        likeCount = like.likeCount.toInt(),
                                        isLiked = like.liked,
                                    )
                                } else {
                                    post
                                }
                            },
                        )
                    }
                    updateMemberReactionCount(
                        memberId = memberId,
                        likeDelta = if (like.liked) 1 else -1,
                    )
                    refreshVerificationDecisionState()
                }

                is ResultState.Error -> _uiState.update { it.copy(actionMessage = result.message) }
                ResultState.Loading -> Unit
            }
        }
    }

    fun onNewCertificationLikeClick(verificationId: Long) {
        val groupId = currentGroupId() ?: return
        val memberId = _uiState.value.newCertifications
            .firstOrNull { it.id == verificationId }
            ?.memberId

        viewModelScope.launch {
            when (val result = likeGroupRoutineVerificationUseCase(groupId = groupId, verificationId = verificationId)) {
                is ResultState.Success -> {
                    val like = result.data
                    _uiState.update { state ->
                        state.copy(
                            posts = state.posts.map { post ->
                                if (post.id == like.verificationId) {
                                    post.copy(
                                        likeCount = like.likeCount.toInt(),
                                        isLiked = like.liked,
                                    )
                                } else {
                                    post
                                }
                            },
                        )
                    }
                    updateMemberReactionCount(memberId = memberId, likeDelta = 1)
                    refreshVerificationDecisionState()
                    advanceNewCertification(verificationId)
                }
                is ResultState.Error -> _uiState.update { it.copy(actionMessage = result.message) }
                ResultState.Loading -> Unit
            }
        }
    }

    fun onNewCertificationDisappointClick(verificationId: Long) {
        val groupId = currentGroupId() ?: return
        val memberId = _uiState.value.newCertifications
            .firstOrNull { it.id == verificationId }
            ?.memberId

        viewModelScope.launch {
            when (val result = disappointGroupRoutineVerificationUseCase(groupId = groupId, verificationId = verificationId)) {
                is ResultState.Success -> {
                    val disappointment = result.data
                    _uiState.update { state ->
                        state.copy(
                            posts = state.posts.map { post ->
                                if (post.id == disappointment.verificationId) {
                                    post.copy(
                                        disappointmentCount = disappointment.count.toInt(),
                                        isDisappointed = disappointment.disappointed,
                                    )
                                } else {
                                    post
                                }
                            },
                        )
                    }
                    updateMemberReactionCount(memberId = memberId, disappointmentDelta = 1)
                    refreshVerificationDecisionState()
                    advanceNewCertification(verificationId)
                }
                is ResultState.Error -> _uiState.update { it.copy(actionMessage = result.message) }
                ResultState.Loading -> Unit
            }
        }
    }

    fun onCertificationDisappointmentClick(verificationId: Long, currentlyDisappointed: Boolean) {
        val groupId = currentGroupId() ?: run {
            _uiState.update { it.copy(actionMessage = "\uADF8\uB8F9 ID\uB97C \uCC3E\uC744 \uC218 \uC5C6\uC2B5\uB2C8\uB2E4.") }
            return
        }
        val memberId = _uiState.value.posts.firstOrNull { it.id == verificationId }?.memberId

        viewModelScope.launch {
            val result = if (currentlyDisappointed) {
                undisappointGroupRoutineVerificationUseCase(groupId = groupId, verificationId = verificationId)
            } else {
                disappointGroupRoutineVerificationUseCase(groupId = groupId, verificationId = verificationId)
            }

            when (result) {
                is ResultState.Success -> {
                    val disappointment = result.data
                    _uiState.update { state ->
                        state.copy(
                            posts = state.posts.map { post ->
                                if (post.id == disappointment.verificationId) {
                                    post.copy(
                                        disappointmentCount = disappointment.count.toInt(),
                                        isDisappointed = disappointment.disappointed,
                                    )
                                } else {
                                    post
                                }
                            },
                        )
                    }
                    updateMemberReactionCount(
                        memberId = memberId,
                        disappointmentDelta = if (disappointment.disappointed) 1 else -1,
                    )
                    refreshVerificationDecisionState()
                }

                is ResultState.Error -> _uiState.update { it.copy(actionMessage = result.message) }
                ResultState.Loading -> Unit
            }
        }
    }

    fun onGroupRoutineManageClick() {
        todayRoutinesJob?.cancel()
        _uiState.update {
            it.copy(
                screenMode = GroupRoutineScreenMode.GroupRoutineManage,
                routineOptions = emptyList(),
                selectedCategory = "전체",
                actionMessage = null,
            )
        }
        loadGroupRoutineCategories()
        loadGroupRoutines()
    }

    fun onRoomNameEditClick() {
        if (!requireConfirmedOwner()) return
        _uiState.update { state ->
            state.copy(
                screenMode = GroupRoutineScreenMode.RoomNameEdit,
                roomNameInput = state.selectedRoutine?.title.orEmpty(),
                actionMessage = null,
            )
        }
    }

    fun onLeaderSettingsClick() {
        if (!requireConfirmedOwner()) return
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
        val state = _uiState.value
        if (!requireConfirmedOwner()) return
        val targetMemberId = state.pendingLeaderMemberId
        val myId = state.members.firstOrNull { it.isMe }?.id
        if (targetMemberId == null || targetMemberId == myId) {
            applyLeaderTransfer(isStillLeader = true, message = null)
            return
        }
        val groupId = currentGroupId()
        if (groupId == null) {
            applyLeaderTransfer(isStillLeader = false, message = "방장 권한을 넘겼어요.")
            return
        }

        if (state.isSubmitting) return
        _uiState.update { it.copy(isSubmitting = true) }
        viewModelScope.launch {
            try {
                when (val result = transferGroupOwnerUseCase(groupId, targetMemberId)) {
                    is ResultState.Success -> {
                        applyLeaderTransfer(isStillLeader = false, message = "방장 권한을 넘겼어요.")
                    }

                    is ResultState.Error -> _uiState.update {
                        it.copy(pendingLeaderMemberId = null, actionMessage = result.message)
                    }

                    ResultState.Loading -> Unit
                }
            } finally {
                _uiState.update { it.copy(isSubmitting = false) }
            }
        }
    }

    private fun applyLeaderTransfer(isStillLeader: Boolean, message: String?) {
        _uiState.update { state ->
            state.copy(
                screenMode = GroupRoutineScreenMode.GroupSettings,
                isCurrentUserLeader = isStillLeader,
                isConfirmedOwner = if (isStillLeader) state.isConfirmedOwner else false,
                pendingLeaderMemberId = null,
                actionMessage = message,
            )
        }
    }

    fun onRoomLockClick() {
        if (!requireConfirmedOwner()) return
        val groupId = currentGroupId() ?: run {
            _uiState.update { it.copy(actionMessage = "그룹 ID를 찾을 수 없습니다.") }
            return
        }
        val nextLocked = !_uiState.value.isRoomLocked
        if (_uiState.value.isSubmitting) return
        _uiState.update { it.copy(isSubmitting = true) }

        viewModelScope.launch {
            try {
                when (val result = setGroupLockUseCase(groupId, nextLocked)) {
                    is ResultState.Success -> _uiState.update {
                        it.copy(
                            isRoomLocked = result.data,
                            actionMessage = if (result.data) {
                                "방이 잠겼어요."
                            } else {
                                "방 잠금이 해제되었어요."
                            },
                        )
                    }

                    is ResultState.Error -> _uiState.update { it.copy(actionMessage = result.message) }
                    ResultState.Loading -> Unit
                }
            } finally {
                _uiState.update { it.copy(isSubmitting = false) }
            }
        }
    }

    fun onMemberKickClick() {
        _uiState.update { it.copy(isKickMemberDialogVisible = true) }
    }

    fun onDismissKickMemberDialog() {
        _uiState.update { it.copy(isKickMemberDialogVisible = false) }
    }

    fun onPokeMemberClick() {
        val groupId = currentGroupId() ?: run {
            _uiState.update { it.copy(actionMessage = "그룹 ID를 찾을 수 없습니다.") }
            return
        }
        val targetMemberId = _uiState.value.selectedMemberId ?: return
        if (_uiState.value.isSubmitting) return
        _uiState.update { it.copy(isSubmitting = true) }

        viewModelScope.launch {
            try {
                when (val result = pokeGroupMemberUseCase(groupId, targetMemberId)) {
                    is ResultState.Success -> {
                        _uiState.update {
                            it.copy(
                                actionMessage = "콕콕 찔렀어요.",
                                actionMessageId = it.actionMessageId + 1,
                            )
                        }
                        loadGroupDetail(groupId)
                    }

                    is ResultState.Error -> _uiState.update { it.copy(actionMessage = result.message) }
                    ResultState.Loading -> Unit
                }
            } finally {
                _uiState.update { it.copy(isSubmitting = false) }
            }
        }
    }

    fun onKickMemberConfirmClick() {
        val groupId = currentGroupId() ?: run {
            _uiState.update { it.copy(actionMessage = "그룹 ID를 찾을 수 없습니다.") }
            return
        }
        val targetMemberId = _uiState.value.selectedMemberId ?: run {
            _uiState.update { it.copy(isKickMemberDialogVisible = false) }
            return
        }
        if (_uiState.value.isSubmitting) return
        _uiState.update { it.copy(isSubmitting = true) }

        viewModelScope.launch {
            try {
                when (val result = kickGroupMemberUseCase(groupId, targetMemberId)) {
                    is ResultState.Success -> {
                        _uiState.update { state ->
                            state.copy(
                                    members = state.members.filterNot { it.id == targetMemberId },
                                selectedMemberId = null,
                                isKickMemberDialogVisible = false,
                                actionMessage = "멤버를 내보냈어요.",
                            )
                        }
                        loadGroupDetail(groupId)
                    }

                    is ResultState.Error -> _uiState.update {
                        it.copy(
                            selectedMemberId = null,
                            isKickMemberDialogVisible = false,
                            actionMessage = result.message,
                        )
                    }

                    ResultState.Loading -> Unit
                }
            } finally {
                _uiState.update { it.copy(isSubmitting = false) }
            }
        }
    }

    private var todayRoutinesJob: Job? = null

    private var groupRoutinesJob: Job? = null

    private fun loadGroupRoutines() {
        val groupId = currentGroupId() ?: run {
            _uiState.update { it.copy(actionMessage = "그룹 ID를 찾을 수 없습니다.") }
            return
        }

        groupRoutinesJob?.cancel()
        groupRoutinesJob = viewModelScope.launch {
            when (val result = getGroupRoutinesUseCase(groupId)) {
                is ResultState.Success -> {
                    if (currentGroupId() != groupId) return@launch

                    _uiState.update { state ->
                        val previousOptions = state.routineOptions.associateBy { it.id }
                        val options = result.data.map { routine ->
                            val schedules = routine.schedules
                            val repeatDays = schedules
                                .mapNotNull { schedule -> RepeatDayToKoreanDay[schedule.repeatDay] }
                                .toSet()
                            CreateRoutineOptionUiModel(
                                id = routine.routineId,
                                title = routine.title,
                                deadline = schedules.firstOrNull()?.endTime.orEmpty(),
                                category = routine.categoryName,
                                startTime = schedules.firstOrNull()?.startTime.orEmpty(),
                                repeatLabel = repeatDaysLabel(repeatDays),
                                repeatDays = repeatDays,
                                isSelected = previousOptions[routine.routineId]?.isSelected == true,
                                categoryColor = state.categoryColors[routine.categoryName],
                            )
                        }
                        state.copy(routineOptions = options)
                    }
                }

                is ResultState.Error -> _uiState.update { state ->
                    if (currentGroupId() == groupId) {
                        state.copy(
                            routineOptions = emptyList(),
                            actionMessage = result.message,
                        )
                    } else {
                        state
                    }
                }
                ResultState.Loading -> Unit
            }
        }
    }

    private fun loadTodayRoutines(groupId: Long) {
        todayRoutinesJob?.cancel()
        todayRoutinesJob = viewModelScope.launch {
            when (val result = getTodayGroupRoutinesUseCase()) {
                is ResultState.Success -> {
                    if (backendGroupId != groupId) return@launch
                    val groupRoutines = result.data
                        .filter { it.groupId == groupId }
                    val todos = groupRoutines.map { routine ->
                            val key = VerifiedRoutineKey(groupId, routine.routineId)
                            val isCompletedOnServer = routine.status == GroupRoutineStatus.COMPLETED
                            if (isCompletedOnServer) {
                                locallyCompletedRoutineKeys.remove(key)
                            }
                            GroupTodoUiModel(
                                id = routine.routineId,
                                title = routine.title,
                                deadline = routine.scheduledEndTime,
                                category = routine.categoryName,
                                isDone = isCompletedOnServer || key in locallyCompletedRoutineKeys,
                                categoryColor = _uiState.value.categoryColors[routine.categoryName],
                            )
                        }
                    _uiState.update { state ->
                        val routineOptionsById = state.routineOptions.associateBy { it.id }
                        val updatedOptions = groupRoutines.map { routine ->
                            routineOptionsById[routine.routineId]?.copy(
                                title = routine.title,
                                deadline = routine.scheduledEndTime,
                                startTime = routine.scheduledStartTime,
                                category = routine.categoryName,
                                categoryColor = state.categoryColors[routine.categoryName],
                            ) ?: CreateRoutineOptionUiModel(
                                id = routine.routineId,
                                title = routine.title,
                                deadline = routine.scheduledEndTime,
                                startTime = routine.scheduledStartTime,
                                category = routine.categoryName,
                                categoryColor = state.categoryColors[routine.categoryName],
                            )
                        }
                        state.copy(
                            todos = todos,
                            routineOptions = if (
                                state.screenMode == GroupRoutineScreenMode.GroupRoutineManage
                            ) {
                                state.routineOptions
                            } else {
                                updatedOptions
                            },
                        )
                    }
                }

                is ResultState.Error -> _uiState.update { it.copy(actionMessage = result.message) }
                ResultState.Loading -> Unit
            }
        }
    }

    fun onRoomNameEditConfirmClick() {
        if (!requireConfirmedOwner()) return
        val title = _uiState.value.roomNameInput.trim()
        if (title.isBlank()) {
            _uiState.update { it.copy(actionMessage = "방 이름을 입력해 주세요.") }
            return
        }
        val groupId = currentGroupId()
        if (groupId == null) {
            applyRoomName(title)
            return
        }

        if (_uiState.value.isSubmitting) return
        _uiState.update { it.copy(isSubmitting = true) }
        viewModelScope.launch {
            try {
                when (val result = updateGroupNameUseCase(groupId, title)) {
                    is ResultState.Success -> applyRoomName(title)
                    is ResultState.Error -> _uiState.update { it.copy(actionMessage = result.message) }
                    ResultState.Loading -> Unit
                }
            } finally {
                _uiState.update { it.copy(isSubmitting = false) }
            }
        }
    }

    private fun applyRoomName(title: String) {
        _uiState.update { state ->
            state.copy(
                screenMode = GroupRoutineScreenMode.GroupSettings,
                routines = state.routines.map { routine ->
                    if (routine.id == state.selectedRoutine?.id) routine.copy(title = title) else routine
                },
                actionMessage = "방 이름이 변경되었어요.",
            )
        }
    }

    private fun requireConfirmedOwner(): Boolean {
        if (_uiState.value.isConfirmedOwner) return true
        _uiState.update { it.copy(actionMessage = "방장만 사용할 수 있는 기능입니다.") }
        return false
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
                roomNameInput = "",
                routineOptions = DefaultCreateRoutineOptions,
                selectedCategory = "\uC804\uCCB4",
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
        _uiState.update {
            it.copy(
                inviteCodeInput = value.filterNot(Char::isWhitespace),
                actionMessage = null,
            )
        }
    }

    fun onInviteCodeConfirmClick() {
        val inviteCode = _uiState.value.inviteCodeInput.trim()
        if (inviteCode.isBlank()) {
            _uiState.update { it.copy(actionMessage = "초대코드를 입력해 주세요.") }
            return
        }
        if (_uiState.value.isSubmitting) return

        _uiState.update { it.copy(isSubmitting = true) }
        viewModelScope.launch {
            try {
                val preview = getGroupJoinPreviewUseCase(inviteCode)
                if (preview is ResultState.Error) {
                    _uiState.update { it.copy(actionMessage = preview.message.toGroupJoinMessage()) }
                    return@launch
                }
                if (preview is ResultState.Success && !preview.data.joinable) {
                    _uiState.update {
                        it.copy(actionMessage = preview.data.unavailableReason ?: "참여할 수 없는 그룹이에요.")
                    }
                    return@launch
                }

                val previewData = (preview as? ResultState.Success)?.data
                when (val result = joinGroupUseCase(inviteCode)) {
                    is ResultState.Success -> {
                        val joined = result.data
                        backendGroupId = joined.groupId
                        val joinedRoutine = GroupRoutineUiModel(
                            id = joined.groupId,
                            title = joined.name,
                            lastActiveLabel = "\uBC29\uAE08 \uC804 \uD65C\uB3D9",
                            memberCount = previewData?.activeMemberCount ?: 1,
                            routineCount = previewData?.totalRoutineCount ?: 0,
                            statusLabel = "\uC9C4\uD589\uC911",
                            isCompleted = false,
                            todayCompletedCount = 0,
                            todayTotalCount = 0,
                            streakDays = 0,
                            monthlyAchievementRate = 0,
                            todayCertificationCount = 0,
                        )
                        _uiState.update { state ->
                            val others = state.routines.filterNot { it.id == joined.groupId }
                            state.copy(
                                screenMode = GroupRoutineScreenMode.Detail,
                                selectedRoutineId = joined.groupId,
                                routines = listOf(joinedRoutine) + others,
                                inviteCodeInput = "",
                                isCurrentUserLeader = false,
                                isConfirmedOwner = false,
                                actionMessage = "그룹에 참여했어요.",
                            )
                        }
                        loadGroupDetail(joined.groupId)
                        loadGroupRoutineCategories()
                        loadTodayRoutines(joined.groupId)
                        loadUnreadRoutineVerifications()
                    }

                    is ResultState.Error -> _uiState.update {
                        it.copy(actionMessage = result.message.toGroupJoinMessage())
                    }
                    ResultState.Loading -> Unit
                }
            } finally {
                _uiState.update { it.copy(isSubmitting = false) }
            }
        }
    }

    fun onInviteCodeCopyClick() {
        _uiState.update {
            it.copy(
                actionMessage = "초대코드가 복사되었어요.",
                actionMessageId = it.actionMessageId + 1,
            )
        }
    }

    fun onRoomNameChange(value: String) {
        _uiState.update {
            it.copy(
                roomNameInput = value.take(MAX_ROOM_NAME_LENGTH),
                actionMessage = null,
            )
        }
    }

    fun onCreateRoomNextClick() {
        _uiState.update { state ->
            val roomName = state.roomNameInput.trim()
            if (roomName.isBlank()) {
                state.copy(actionMessage = "방 이름을 입력해 주세요.")
            } else {
                state.copy(
                    screenMode = GroupRoutineScreenMode.CreateRoutineSelect,
                    roomNameInput = roomName,
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
                categoryColorInput = null,
                actionMessage = null,
            )
        }
    }

    fun onDismissCategorySheet() {
        _uiState.update { it.copy(isCategorySheetVisible = false, categoryInput = "", categoryColorInput = null) }
    }

    fun onCategoryInputChange(value: String) {
        _uiState.update {
            it.copy(
                categoryInput = value.take(MAX_CATEGORY_NAME_LENGTH),
                actionMessage = null,
            )
        }
    }

    fun onCategoryColorSelected(color: CategoryColor) {
        _uiState.update { it.copy(categoryColorInput = color, actionMessage = null) }
    }

    fun onCategoryConfirmClick() {
        val state = _uiState.value
        val name = state.categoryInput.trim()
        if (name.isBlank()) {
            _uiState.update { it.copy(actionMessage = "카테고리 이름을 입력해 주세요.") }
            return
        }
        if (name in state.categories) {
            _uiState.update { it.copy(actionMessage = "이미 있는 카테고리예요.") }
            return
        }

        if (state.screenMode == GroupRoutineScreenMode.CreateRoutineSelect) {
            val selectedColor = state.categoryColorInput
            _uiState.update {
                it.copy(
                    categories = it.categories + name,
                    categoryColors = if (selectedColor == null) it.categoryColors else it.categoryColors + (name to selectedColor),
                    selectedCategory = name,
                    categoryInput = "",
                    categoryColorInput = null,
                    isCategorySheetVisible = false,
                    actionMessage = null,
                )
            }
            return
        }

        val groupId = currentGroupId() ?: run {
            _uiState.update { it.copy(actionMessage = "그룹 ID를 찾을 수 없습니다.") }
            return
        }

        viewModelScope.launch {
            when (val result = createGroupRoutineCategoryUseCase(groupId, name, color = state.categoryColorInput?.serverCode())) {
                is ResultState.Success -> {
                    serverCategoryIds = serverCategoryIds + (result.data.name to result.data.categoryId)
                    val categoryColor = result.data.color.toCategoryColor() ?: state.categoryColorInput
                    _uiState.update {
                        it.copy(
                            categories = it.categories + result.data.name,
                            categoryColors = if (categoryColor == null) it.categoryColors else it.categoryColors + (result.data.name to categoryColor),
                            selectedCategory = result.data.name,
                            categoryInput = "",
                            categoryColorInput = null,
                            isCategorySheetVisible = false,
                            actionMessage = null,
                        )
                    }
                }

                is ResultState.Error -> _uiState.update { it.copy(actionMessage = result.message) }
                ResultState.Loading -> Unit
            }
        }
    }

    fun onRoutineAddClick() {
        _uiState.update { state ->
            state.copy(
                isRoutineSettingSheetVisible = true,
                editingRoutineId = null,
                routineDraftName = "",
                routineDraftStartTime = "08:00",
                routineDraftEndTime = "20:00",
                routineDraftRepeatDays = emptySet(),
                routineDraftCategory = state.resolveRoutineDraftCategory(),
                actionMessage = null,
            )
        }
    }

    fun onRoutineDraftCategoryClick(category: String) {
        _uiState.update { it.copy(routineDraftCategory = category, actionMessage = null) }
    }

    fun onRoutineSettingClick(optionId: Long) {
        _uiState.update { state ->
            val option = state.routineOptions.firstOrNull { it.id == optionId } ?: return@update state
            state.copy(
                isRoutineSettingSheetVisible = true,
                editingRoutineId = optionId,
                routineDraftName = option.title,
                routineDraftStartTime = option.startTime,
                routineDraftEndTime = option.deadline,
                routineDraftRepeatDays = option.repeatDays,
                routineDraftCategory = option.category,
                actionMessage = null,
            )
        }
    }

    fun onRoutineColorLongClick(optionId: Long) {
        _uiState.update { state ->
            val option = state.routineOptions.firstOrNull { it.id == optionId }
                ?: state.todos.firstOrNull { it.id == optionId }?.let { todo ->
                    CreateRoutineOptionUiModel(
                        id = todo.id,
                        title = todo.title,
                        deadline = todo.deadline,
                        category = todo.category,
                        categoryColor = todo.categoryColor,
                    )
                }
                ?: return@update state
            state.copy(
                isRoutineColorSheetVisible = true,
                routineColorTargetId = optionId,
                routineColorInput = option.categoryColor ?: state.categoryColors[option.category],
                actionMessage = null,
            )
        }
    }

    fun onDismissRoutineColorSheet() {
        _uiState.update {
            it.copy(
                isRoutineColorSheetVisible = false,
                routineColorTargetId = null,
                routineColorInput = null,
            )
        }
    }

    fun onRoutineColorSelected(color: CategoryColor) {
        _uiState.update { state ->
            val targetId = state.routineColorTargetId ?: return@update state
            val categoryName = state.routineOptions.firstOrNull { it.id == targetId }?.category
                ?: state.todos.firstOrNull { it.id == targetId }?.category
                ?: return@update state
            val nextColors = state.categoryColors + (categoryName to color)
            state.copy(
                categoryColors = nextColors,
                routineOptions = state.routineOptions.map { option ->
                    if (option.category == categoryName) option.copy(categoryColor = color) else option
                },
                todos = state.todos.map { todo ->
                    if (todo.category == categoryName) todo.copy(categoryColor = color) else todo
                },
                isRoutineColorSheetVisible = false,
                routineColorTargetId = null,
                routineColorInput = color,
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
                routineDraftStartTime = "08:00",
                routineDraftEndTime = "20:00",
                routineDraftRepeatDays = emptySet(),
                routineDraftCategory = "",
                isDeleteRoutineDialogVisible = false,
            )
        }
    }

    fun onRoutineDraftNameChange(value: String) {
        _uiState.update {
            it.copy(
                routineDraftName = value.take(MAX_ROUTINE_NAME_LENGTH),
                actionMessage = null,
            )
        }
    }

    fun onRoutineDraftStartTimeChange(value: String) {
        _uiState.update { it.copy(routineDraftStartTime = value, actionMessage = null) }
    }

    fun onRoutineDraftEndTimeChange(value: String) {
        _uiState.update { it.copy(routineDraftEndTime = value, actionMessage = null) }
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
            _uiState.update { it.copy(actionMessage = "루틴 이름을 입력해 주세요.") }
            return
        }

        val routineState = state.copy(routineDraftCategory = state.resolveRoutineDraftCategory())

        if (routineState.routineDraftCategory.isBlank()) {
            _uiState.update { it.copy(actionMessage = "카테고리를 선택해 주세요.") }
            return
        }

        if (routineState.routineDraftRepeatDays.isEmpty()) {
            _uiState.update { it.copy(actionMessage = "반복 요일을 선택해 주세요.") }
            return
        }

        if (routineState.screenMode == GroupRoutineScreenMode.GroupRoutineManage) {
            submitGroupRoutine(routineState, title)
        } else {
            applyLocalRoutineDraft(routineState, title)
        }
    }

    private fun GroupRoutineUiState.resolveRoutineDraftCategory(): String {
        return routineDraftCategory.takeIf { it.isNotBlank() && it != "\uC804\uCCB4" }
            ?: selectedCategory.takeIf { it.isNotBlank() && it != "\uC804\uCCB4" }
            ?: categories.firstOrNull { it != "\uC804\uCCB4" }
            .orEmpty()
    }

    private fun submitGroupRoutine(state: GroupRoutineUiState, title: String) {
        val groupId = currentGroupId()
        if (groupId == null) {
            _uiState.update { it.copy(actionMessage = "그룹 ID를 찾을 수 없습니다.") }
            return
        }

        if (state.isSubmitting) return
        _uiState.update { it.copy(isSubmitting = true) }

        val categoryName = state.routineDraftCategory
        val categoryColor = state.categoryColors[categoryName]
        val schedules = state.routineDraftRepeatDays.toGroupRoutineSchedules(
            startTime = state.routineDraftStartTime,
            endTime = state.routineDraftEndTime,
        )
        val editingId = state.editingRoutineId
        val categoryId = DefaultCategoryIds[categoryName] ?: serverCategoryIds[categoryName]

        if (categoryId == null) {
            _uiState.update { it.copy(isSubmitting = false, actionMessage = "\"$categoryName\" 카테고리 ID를 찾을 수 없습니다. 카테고리를 다시 선택해 주세요.") }
            return
        }
        if (editingId != null && DefaultCreateRoutineOptions.any { it.id == editingId }) {
            _uiState.update { it.copy(isSubmitting = false, actionMessage = "기본 제공 루틴은 서버에 수정할 수 없습니다.") }
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
                            deadline = state.routineDraftEndTime,
                            category = categoryName,
                            startTime = state.routineDraftStartTime,
                            repeatLabel = repeatLabel,
                            repeatDays = state.routineDraftRepeatDays,
                            categoryColor = categoryColor,
                        )
                        _uiState.update {
                            val nextOptions = if (editingId == null) {
                                it.routineOptions + savedOption
                            } else {
                                it.routineOptions.map { option ->
                                    if (option.id == editingId) savedOption.copy(isSelected = option.isSelected) else option
                                }
                            }
                            it.copy(
                                routineOptions = nextOptions,
                                isRoutineSettingSheetVisible = false,
                                editingRoutineId = null,
                                routineDraftName = "",
                                routineDraftStartTime = "08:00",
                                routineDraftEndTime = "20:00",
                                routineDraftRepeatDays = emptySet(),
                                routineDraftCategory = "",
                                actionMessage = null,
                            )
                        }
                        loadGroupRoutines()
                    }
                    is ResultState.Error -> _uiState.update { it.copy(actionMessage = result.message) }
                    ResultState.Loading -> Unit
                }
            } finally {
                _uiState.update { it.copy(isSubmitting = false) }
            }
        }
    }

    private fun applyLocalRoutineDraft(state: GroupRoutineUiState, title: String) {
        val repeatLabel = repeatDaysLabel(state.routineDraftRepeatDays)
        val editingId = state.editingRoutineId
        val categoryName = state.routineDraftCategory
        val categoryColor = state.categoryColors[categoryName]
        
        val nextOptions = if (editingId == null) {
            val newId = (state.routineOptions.maxOfOrNull { it.id } ?: 0L) + 1L
            state.routineOptions + CreateRoutineOptionUiModel(
                id = newId,
                title = title,
                deadline = state.routineDraftEndTime,
                category = categoryName,
                startTime = state.routineDraftStartTime,
                repeatLabel = repeatLabel,
                repeatDays = state.routineDraftRepeatDays,
                categoryColor = categoryColor,
            )
        } else {
            state.routineOptions.map { option ->
                if (option.id == editingId) {
                    option.copy(
                        title = title,
                        deadline = state.routineDraftEndTime,
                        startTime = state.routineDraftStartTime,
                        repeatLabel = repeatLabel,
                        repeatDays = state.routineDraftRepeatDays,
                        categoryColor = categoryColor,
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
                routineDraftStartTime = "08:00",
                routineDraftEndTime = "20:00",
                routineDraftRepeatDays = emptySet(),
                routineDraftCategory = "",
                actionMessage = null,
            )
        }
    }

    fun onRoutineDeleteClick() {
        _uiState.update {
            it.copy(
                editingRoutineId = it.editingRoutineId ?: it.routineColorTargetId,
                isRoutineColorSheetVisible = false,
                isDeleteRoutineDialogVisible = true,
            )
        }
    }

    fun onDismissDeleteRoutineDialog() {
        _uiState.update { it.copy(isDeleteRoutineDialogVisible = false) }
    }

    fun onConfirmDeleteRoutineClick() {
        val state = _uiState.value
        val editingId = state.editingRoutineId
        val groupId = currentGroupId()
        val shouldCallApi = state.screenMode == GroupRoutineScreenMode.GroupRoutineManage &&
            editingId != null && editingId > 0L && groupId != null

        if (!shouldCallApi) {
            clearRoutineDraft(editingId)
            return
        }
        if (state.isSubmitting) return
        closeRoutineSheets()
        _uiState.update { it.copy(isSubmitting = true) }

        viewModelScope.launch {
            try {
                when (val result = deleteGroupRoutineUseCase(groupId, editingId)) {
                    is ResultState.Success -> {
                        clearRoutineDraft(editingId)
                        _uiState.update { it.copy(actionMessage = "루틴이 삭제되었어요.") }
                        loadGroupRoutines()
                    }

                    is ResultState.Error -> _uiState.update { it.copy(actionMessage = result.message) }
                    ResultState.Loading -> Unit
                }
            } finally {
                _uiState.update { it.copy(isSubmitting = false) }
            }
        }
    }

    private fun closeRoutineSheets() {
        _uiState.update {
            it.copy(isRoutineSettingSheetVisible = false, isDeleteRoutineDialogVisible = false)
        }
    }

    private fun clearRoutineDraft(editingId: Long?) {
        _uiState.update { state ->
            state.copy(
                routineOptions = if (editingId == null) {
                    state.routineOptions
                } else {
                    state.routineOptions.filterNot { it.id == editingId }
                },
                todos = if (editingId == null) state.todos else state.todos.filterNot { it.id == editingId },
                isRoutineSettingSheetVisible = false,
                isDeleteRoutineDialogVisible = false,
                editingRoutineId = null,
                routineDraftName = "",
                routineDraftStartTime = "08:00",
                routineDraftEndTime = "20:00",
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
            _uiState.update { it.copy(actionMessage = "함께할 루틴을 선택해 주세요.") }
            return
        }
        if (roomName.isBlank()) {
            _uiState.update { it.copy(actionMessage = "방 이름을 입력해 주세요.") }
            return
        }
        
        if (state.isSubmitting) return
        _uiState.update { it.copy(isSubmitting = true) }

        val categoryNames = selectedOptions.map { it.category }.distinct()
        val customCategories = categoryNames
            .filter { it !in DefaultCategoryIds }
            .map { categoryName ->
                NewGroupCategory(
                    clientKey = categoryName,
                    name = categoryName,
                    color = state.categoryColors[categoryName]?.serverCode(),
                )
            }
        val routines = selectedOptions.map { option ->
            NewGroupRoutine(
                categoryId = DefaultCategoryIds[option.category],
                categoryKey = if (option.category in DefaultCategoryIds) null else option.category,
                title = option.title,
                description = option.title,
                schedules = option.repeatDays.toGroupRoutineSchedules(startTime = option.startTime, endTime = option.deadline),
            )
        }

        viewModelScope.launch {
            try {
                when (val result = createGroupUseCase(roomName, customCategories, routines)) {
                    is ResultState.Success -> {
                        val createdRoutines = result.data.routines
                        val responseIsValid = createdRoutines.size == selectedOptions.size &&
                            createdRoutines.indices.all { index -> createdRoutines[index].title == selectedOptions[index].title }
                        if (!responseIsValid) {
                            _uiState.update {
                                it.copy(actionMessage = "그룹 생성 응답이 선택한 루틴과 일치하지 않습니다. 다시 시도해 주세요.")
                            }
                            return@launch
                        }

                        val groupId = result.data.groupId
                        backendGroupId = groupId

                        val newRoutine = GroupRoutineUiModel(
                            id = groupId,
                            title = roomName,
                            lastActiveLabel = "\uBC29\uAE08 \uC804 \uD65C\uB3D9",
                            memberCount = 1,
                            routineCount = selectedOptions.size,
                            statusLabel = "\uC9C4\uD589\uC911",
                            isCompleted = false,
                            todayCompletedCount = 0,
                            todayTotalCount = selectedOptions.size,
                            streakDays = 0,
                            monthlyAchievementRate = 0,
                            todayCertificationCount = 0,
                        )
                        val createdOptions = selectedOptions.mapIndexed { index, option ->
                            val createdRoutine = createdRoutines[index]
                            option.copy(
                                id = createdRoutine.routineId,
                                title = createdRoutine.title,
                                category = createdRoutine.categoryName ?: option.category,
                            )
                        }
                        val selectedTodos = createdOptions.map { option ->
                            GroupTodoUiModel(
                                id = option.id,
                                title = option.title,
                                deadline = option.deadline,
                                category = option.category,
                                isDone = false,
                                categoryColor = option.categoryColor ?: _uiState.value.categoryColors[option.category],
                            )
                        }
                        _uiState.update {
                            val existingServerRoutines = it.routines.filter { routine ->
                                routine.id > 0L && routine.id != newRoutine.id
                            }
                            it.copy(
                                screenMode = GroupRoutineScreenMode.Detail,
                                selectedRoutineId = groupId,
                                roomNameInput = "",
                                routineOptions = createdOptions,
                                selectedCategory = "\uC804\uCCB4",
                                routines = listOf(newRoutine) + existingServerRoutines,
                                todos = selectedTodos,
                                isCurrentUserLeader = true,
                                isConfirmedOwner = true,
                                actionMessage = "\uBC29\uC774 \uB9CC\uB4E4\uC5B4\uC84C\uC5B4\uC694.",
                            )
                        }
                        loadGroupDetail(groupId)
                        loadGroupRoutineCategories()
                    }
                    is ResultState.Error -> _uiState.update { it.copy(actionMessage = result.message) }
                    ResultState.Loading -> Unit
                }
            } finally {
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

    fun onCertificationMemberClick(memberId: Long?) {
        _uiState.update { state ->
            state.copy(
                selectedCertificationMemberId = memberId,
                showOnlyMyCertifications = memberId?.let { id ->
                    state.members.firstOrNull { member -> member.id == id }?.isMe == true
                } ?: false,
                actionMessage = null,
            )
        }
    }

    private fun repeatDaysLabel(days: Set<String>): String {
        if (days.isEmpty()) return "\uC5C6\uC74C"

        val orderedDays = listOf("\uC77C", "\uC6D4", "\uD654", "\uC218", "\uBAA9", "\uAE08", "\uD1A0")
        val weekdays = setOf("\uC6D4", "\uD654", "\uC218", "\uBAA9", "\uAE08")
        val weekend = setOf("\uD1A0", "\uC77C")
        val allDays = orderedDays.toSet()

        return when (days) {
            allDays -> "\uB9E4\uC77C"
            weekdays -> "\uC8FC\uC911"
            weekend -> "\uC8FC\uB9D0"
            else -> {
                val sortedDays = orderedDays.filter { it in days }
                if (sortedDays.size == 1) "${sortedDays.first()}\uC694\uC77C\uB9C8\uB2E4" else sortedDays.joinToString(",")
            }
        }
    }

    private fun ChatMessage.toUiModel(isMine: Boolean): ChatMessageUiModel = ChatMessageUiModel(
        id = id,
        senderName = senderNickname,
        message = if (type == ChatMessageType.TEXT) content else "",
        sentAtMillis = createdAt.toEpochMillisOrNow(),
        isMine = isMine,
        emojiUrl = if (type == ChatMessageType.EMOTICON) emoticon?.assetUrl else null,
        replyPreview = reply?.let { original ->
            ChatReplyPreviewUiModel(
                originalMessageId = original.id,
                senderName = original.senderNickname,
                previewText = if (original.type == ChatMessageType.EMOTICON) {
                    EmojiReplyPreviewLabel
                } else {
                    original.content
                },
            )
        },
    )
}

/** 서버가 주는 "yyyy-MM-dd" 채팅 날짜 문자열을 [LocalDate]로 변환한다. 파싱 실패 시 그 날짜만 건너뛴다. */
private fun parseIsoDateOrNull(isoDate: String): LocalDate? = runCatching { LocalDate.parse(isoDate) }.getOrNull()

/**
 * 서버가 주는 createdAt 문자열을 epoch millis로 변환한다.
 *
 * 정확한 포맷을 하나로 확신할 수 없어(오프셋 포함 Instant, 오프셋 없는 LocalDateTime 등) 순서대로
 * 시도한다 - 예전에는 Instant.parse만 시도하고 실패하면 "지금"으로 대체했는데, 서버가 오프셋 없는
 * 로컬 시각(KST)을 내려줄 때마다 매번 실패해서 모든 메시지의 시각이 화면에 그려지는 순간의
 * 현재 시각으로 표시되는 버그가 있었다(새 메시지가 올 때마다 이전 메시지들도 방금 시각으로
 * 보이는 것처럼 보임).
 */
private fun String.toEpochMillisOrNow(): Long {
    runCatching { return Instant.parse(this).toEpochMilli() }
    runCatching { return OffsetDateTime.parse(this).toInstant().toEpochMilli() }
    runCatching {
        return LocalDateTime.parse(this).atZone(ZoneId.of("Asia/Seoul")).toInstant().toEpochMilli()
    }
    return System.currentTimeMillis()
}

private fun String?.toEpochMillisOrNull(): Long? {
    if (isNullOrBlank()) return null
    runCatching { return Instant.parse(this).toEpochMilli() }
    runCatching { return OffsetDateTime.parse(this).toInstant().toEpochMilli() }
    runCatching {
        return LocalDateTime.parse(this).atZone(ZoneId.of("Asia/Seoul")).toInstant().toEpochMilli()
    }
    return null
}

private fun String?.toRelativeTimeLabel(nowMillis: Long = System.currentTimeMillis()): String {
    val verifiedAtMillis = toEpochMillisOrNull() ?: return orEmpty()
    val elapsedMinutes = ((nowMillis - verifiedAtMillis).coerceAtLeast(0L) / 60_000L)
    return when {
        elapsedMinutes < 1 -> "방금 전"
        elapsedMinutes < 60 -> "${elapsedMinutes}분 전"
        elapsedMinutes < 1_440 -> "${elapsedMinutes / 60}시간 전"
        else -> "${elapsedMinutes / 1_440}일 전"
    }
}

private fun String.toGroupJoinMessage(): String {
    return if (
        equals("HTTP 403", ignoreCase = true) ||
        contains("잠금") ||
        contains("잠긴")
    ) {
        "잠겨 있어 참여할 수 없는 그룹방이에요."
    } else {
        this
    }
}

private val KoreanDayToRepeatDay = mapOf(
    "\uC77C" to RepeatDay.SUNDAY,
    "\uC6D4" to RepeatDay.MONDAY,
    "\uD654" to RepeatDay.TUESDAY,
    "\uC218" to RepeatDay.WEDNESDAY,
    "\uBAA9" to RepeatDay.THURSDAY,
    "\uAE08" to RepeatDay.FRIDAY,
    "\uD1A0" to RepeatDay.SATURDAY,
)

private val RepeatDayToKoreanDay = KoreanDayToRepeatDay.entries.associate { (day, repeatDay) ->
    repeatDay to day
}

private val DefaultCategoryIds = mapOf(
    "\uC6B4\uB3D9" to 1L,
    "\uAC74\uAC15" to 2L,
    "\uC790\uAE30\uACC4\uBC1C" to 3L,
    "\uC0DD\uD65C\uC815\uB9AC" to 4L,
    "\uB9C8\uC74C\uAD00\uB9AC" to 5L,
    "\uCDE8\uBBF8" to 6L,
)

private fun Set<String>.toGroupRoutineSchedules(startTime: String, endTime: String): List<GroupRoutineSchedule> {
    return mapNotNull { KoreanDayToRepeatDay[it] }.map { day ->
        GroupRoutineSchedule(repeatDay = day, startTime = startTime, endTime = endTime)
    }
}

private fun CategoryColor.serverCode(): String = name.uppercase()
