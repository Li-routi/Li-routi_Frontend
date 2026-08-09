package com.li_routi.feature.grouproutine.vm

import androidx.lifecycle.viewModelScope
import com.li_routi.core.common.android.architecture.BaseViewModel
import com.li_routi.core.common.kotlin.util.ResultState
import com.li_routi.core.data.di.AuthContainer
import com.li_routi.core.data.di.ChatContainer
import com.li_routi.core.data.di.GroupRoutineContainer
import com.li_routi.core.domain.auth.GetMyInfoUseCase
import com.li_routi.core.domain.chat.ChatMessage
import com.li_routi.core.domain.chat.ChatMessageType
import com.li_routi.core.domain.chat.ConnectChatSocketUseCase
import com.li_routi.core.domain.chat.DisconnectChatSocketUseCase
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
import com.li_routi.core.domain.grouproutine.GetGroupJoinPreviewUseCase
import com.li_routi.core.domain.grouproutine.GetGroupDetailUseCase
import com.li_routi.core.domain.grouproutine.GetGroupRoutineCategoriesUseCase
import com.li_routi.core.domain.grouproutine.GetGroupRoutineVerificationsUseCase
import com.li_routi.core.domain.grouproutine.GetGroupInviteCodeUseCase
import com.li_routi.core.domain.grouproutine.GetTodayGroupRoutinesUseCase
import com.li_routi.core.domain.grouproutine.GroupRoutineSchedule
import com.li_routi.core.domain.grouproutine.GroupRoutineStatus
import com.li_routi.core.domain.grouproutine.JoinGroupUseCase
import com.li_routi.core.domain.grouproutine.KickGroupMemberUseCase
import com.li_routi.core.domain.grouproutine.LeaveGroupResult
import com.li_routi.core.domain.grouproutine.LeaveGroupUseCase
import com.li_routi.core.domain.grouproutine.NewGroupCategory
import com.li_routi.core.domain.grouproutine.NewGroupRoutine
import com.li_routi.core.domain.grouproutine.RepeatDay
import com.li_routi.core.domain.grouproutine.SetGroupLockUseCase
import com.li_routi.core.domain.grouproutine.UpdateGroupRoutineUseCase
import com.li_routi.feature.grouproutine.component.ChatEmoticonUiModel
import com.li_routi.feature.grouproutine.component.ChatMessageUiModel
import java.time.Instant
import java.util.UUID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GroupRoutineViewModel(
    private val createGroupUseCase: CreateGroupUseCase = GroupRoutineContainer.createGroupUseCase,
    private val createGroupRoutineUseCase: CreateGroupRoutineUseCase = GroupRoutineContainer.createGroupRoutineUseCase,
    private val updateGroupRoutineUseCase: UpdateGroupRoutineUseCase = GroupRoutineContainer.updateGroupRoutineUseCase,
    private val getGroupDetailUseCase: GetGroupDetailUseCase = GroupRoutineContainer.getGroupDetailUseCase,
    private val deleteGroupUseCase: DeleteGroupUseCase = GroupRoutineContainer.deleteGroupUseCase,
    private val leaveGroupUseCase: LeaveGroupUseCase = GroupRoutineContainer.leaveGroupUseCase,
    private val joinGroupUseCase: JoinGroupUseCase = GroupRoutineContainer.joinGroupUseCase,
    private val getGroupJoinPreviewUseCase: GetGroupJoinPreviewUseCase = GroupRoutineContainer.getGroupJoinPreviewUseCase,
    private val setGroupLockUseCase: SetGroupLockUseCase = GroupRoutineContainer.setGroupLockUseCase,
    private val kickGroupMemberUseCase: KickGroupMemberUseCase = GroupRoutineContainer.kickGroupMemberUseCase,
    private val deleteGroupRoutineUseCase: DeleteGroupRoutineUseCase = GroupRoutineContainer.deleteGroupRoutineUseCase,
    private val getTodayGroupRoutinesUseCase: GetTodayGroupRoutinesUseCase = GroupRoutineContainer.getTodayGroupRoutinesUseCase,
    private val getGroupInviteCodeUseCase: GetGroupInviteCodeUseCase = GroupRoutineContainer.getGroupInviteCodeUseCase,
    private val getMyInfoUseCase: GetMyInfoUseCase = AuthContainer.getMyInfoUseCase,
    private val getGroupRoutineCategoriesUseCase: GetGroupRoutineCategoriesUseCase = GroupRoutineContainer.getGroupRoutineCategoriesUseCase,
    private val createGroupRoutineCategoryUseCase: CreateGroupRoutineCategoryUseCase = GroupRoutineContainer.createGroupRoutineCategoryUseCase,
    private val getGroupRoutineVerificationsUseCase: GetGroupRoutineVerificationsUseCase = GroupRoutineContainer.getGroupRoutineVerificationsUseCase,
    private val getChatMessagesUseCase: GetChatMessagesUseCase = ChatContainer.getChatMessagesUseCase,
    private val updateChatReadPositionUseCase: UpdateChatReadPositionUseCase = ChatContainer.updateChatReadPositionUseCase,
    private val getEmoticonsUseCase: GetEmoticonsUseCase = ChatContainer.getEmoticonsUseCase,
    private val connectChatSocketUseCase: ConnectChatSocketUseCase = ChatContainer.connectChatSocketUseCase,
    private val observeChatMessagesUseCase: ObserveChatMessagesUseCase = ChatContainer.observeChatMessagesUseCase,
    private val sendChatMessageUseCase: SendChatMessageUseCase = ChatContainer.sendChatMessageUseCase,
    private val disconnectChatSocketUseCase: DisconnectChatSocketUseCase = ChatContainer.disconnectChatSocketUseCase,
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(GroupRoutineUiState())
    val uiState: StateFlow<GroupRoutineUiState> = _uiState.asStateFlow()

    // 채팅방 진입 중에만 살아있는 소켓 연결+구독 코루틴. 화면 이탈/ViewModel 소멸 시 취소한다.
    private var chatSocketJob: Job? = null

    // PR 반영: Mock ID와 실제 서버 ID 분리
    // createGroupUseCase 성공 시나 초대코드로 조인했을 때 발급되는 실제 서버 그룹 ID를 저장합니다.
    private var backendGroupId: Long? = null

    // API 호출 시 사용할 currentGroupId()는 이제 실제 backendGroupId를 참조합니다.
    private fun currentGroupId(): Long? = backendGroupId

    // CodeRabbit 반영: 루틴이 없을 때 임의의 1L을 서버에 실제 ID처럼 보내지 않도록 nullable로 변경
    private fun currentRoutineId(): Long? = _uiState.value.todos.firstOrNull()?.id

    // CodeRabbit 반영: 새로 생성된(서버) 카테고리의 이름 -> categoryId. DefaultCategoryIds에 없는 카테고리 제출 시 사용
    private var serverCategoryIds: Map<String, Long> = emptyMap()

    // 구성원 목록에서 "나"를 가려내고 채팅 말풍선 좌우를 정하는 데 씀. 세션 내내 안 바뀌어서 한 번만 조회함
    private var myMemberId: Long? = null

    fun onDismissActionMessage() {
        _uiState.update { it.copy(actionMessage = null) }
    }

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
        // 목록에 아직 mock 카드(음수 id)가 섞여 있어서, 실제 서버 그룹일 때만 상세를 불러옴
        if (routineId > 0L) {
            backendGroupId = routineId
            loadGroupDetail(routineId)
            loadTodayRoutines(routineId)
        }
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
        if (leavingChat) leaveChatSocket()
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

        val message = NewChatMessage(
            clientMessageId = UUID.randomUUID().toString(),
            type = ChatMessageType.TEXT,
            content = text,
            emoticonCode = null,
        )
        viewModelScope.launch {
            when (val result = sendChatMessageUseCase(groupId, message)) {
                is ResultState.Success -> _uiState.update { it.copy(chatDraftText = "") }
                is ResultState.Error -> _uiState.update { it.copy(actionMessage = result.message) }
                ResultState.Loading -> Unit
            }
        }
    }

    // 탭한 즉시 채팅으로 전송된다 — 실제 화면 반영은 소켓 구독으로 돌아오는 브로드캐스트를 통해 이뤄진다.
    fun onChatEmojiSelected(emoticon: ChatEmoticonUiModel) {
        val groupId = currentGroupId() ?: run {
            _uiState.update { it.copy(actionMessage = "그룹 ID를 찾을 수 없습니다.") }
            return
        }

        val message = NewChatMessage(
            clientMessageId = UUID.randomUUID().toString(),
            type = ChatMessageType.EMOTICON,
            content = null,
            emoticonCode = emoticon.code,
        )
        viewModelScope.launch {
            when (val result = sendChatMessageUseCase(groupId, message)) {
                is ResultState.Error -> _uiState.update { it.copy(actionMessage = result.message) }
                else -> Unit
            }
        }
    }

    /** 소켓 연결 → 브로드캐스트 구독 시작 → REST 이력 조회 순서로 진행해, 연결 이후 온 메시지를 놓치지 않는다. */
    private fun enterChatSocket() {
        val groupId = currentGroupId() ?: run {
            _uiState.update { it.copy(actionMessage = "그룹 ID를 찾을 수 없습니다.") }
            return
        }

        chatSocketJob?.cancel()
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
                    _uiState.update { state ->
                        if (state.chatMessages.any { it.id == incoming.id }) return@update state
                        state.copy(chatMessages = state.chatMessages + incoming.toUiModel(isMine = incoming.senderId == myMemberId))
                    }
                    markChatRead(groupId, incoming.id)
                }
            }

            loadChatMessages(groupId)
        }
    }

    private fun leaveChatSocket() {
        chatSocketJob?.cancel()
        chatSocketJob = null
        viewModelScope.launch { disconnectChatSocketUseCase() }
    }

    private suspend fun loadChatMessages(groupId: Long) {
        _uiState.update { it.copy(isChatLoading = true) }
        when (val result = getChatMessagesUseCase(groupId = groupId, size = 50)) {
            is ResultState.Success -> {
                val myMemberId = _uiState.value.members.firstOrNull { it.isMe }?.id
                val historyMessages = result.data.messages.map { it.toUiModel(isMine = it.senderId == myMemberId) }
                _uiState.update { state ->
                    // 조회 응답이 오기 전 소켓으로 먼저 들어온 메시지와 겹칠 수 있어 id 기준으로 합친다.
                    val merged = (historyMessages + state.chatMessages).distinctBy { it.id }.sortedBy { it.id }
                    state.copy(chatMessages = merged, isChatLoading = false)
                }
                // 마지막 메시지까지 읽은 것으로 서버에 반영(화면에 들어와 목록을 봤으므로).
                historyMessages.lastOrNull()?.let { last -> markChatRead(groupId, last.id) }
            }

            is ResultState.Error -> _uiState.update { it.copy(actionMessage = result.message, isChatLoading = false) }
            ResultState.Loading -> _uiState.update { it.copy(isChatLoading = false) }
        }
    }

    private fun markChatRead(groupId: Long, lastReadMessageId: Long) {
        viewModelScope.launch {
            updateChatReadPositionUseCase(groupId, lastReadMessageId)
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
        // viewModelScope는 onCleared() 시점엔 이미 취소되어 있어 여기서 쓸 수 없다 — 별도 스코프로 소켓만 정리.
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

    fun onLeaveRoomClick() {
        _uiState.update { it.copy(isLeaveRoomDialogVisible = true) }
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
                        LeaveGroupResult.Left -> exitRoom(groupId, "방에서 나왔어요.")
                        // 방장은 못 나감 — 나가기 대신 삭제할지 다시 물어봄
                        LeaveGroupResult.OwnerMustDelete -> _uiState.update {
                            it.copy(isLeaveRoomDialogVisible = false, isDeleteRoomDialogVisible = true)
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
                    is ResultState.Success -> exitRoom(groupId, "방을 삭제했어요.")
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

    /** 나가기/삭제 공통 뒷정리 — 목록에서 해당 방 카드를 지우고 목록 화면으로 돌려보냄 */
    private fun exitRoom(groupId: Long, message: String) {
        backendGroupId = null
        _uiState.update { state ->
            state.copy(
                screenMode = GroupRoutineScreenMode.List,
                routines = state.routines.filterNot { it.id == groupId },
                selectedRoutineId = null,
                selectedMemberId = null,
                groupInviteCode = null,
                isLeaveRoomDialogVisible = false,
                isDeleteRoomDialogVisible = false,
                actionMessage = message,
            )
        }
    }

    /** 그룹방 상세를 불러와 그룹명/초대코드/구성원 목록을 실제 서버 데이터로 채움 */
    private fun loadGroupDetail(groupId: Long) {
        viewModelScope.launch {
            if (myMemberId == null) {
                val myInfo = getMyInfoUseCase()
                if (myInfo is ResultState.Success) myMemberId = myInfo.data.memberId
            }

            when (val result = getGroupDetailUseCase(groupId)) {
                is ResultState.Success -> {
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
                                    isMe = member.memberId == myMemberId,
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
                        )
                    }
                }

                is ResultState.Error -> _uiState.update { it.copy(actionMessage = result.message) }
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
                // PR 반영: ResultState.Error 시 바로 새로 발급(issue)하지 않고 에러 메시지 띄우기 (기존 초대코드 무효화 방지)
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

        viewModelScope.launch {
            when (val result = getGroupRoutineCategoriesUseCase(groupId)) {
                is ResultState.Success -> {
                    val categoryNames = result.data.categories.map { it.name }
                    _uiState.update { state ->
                        val allLabel = state.categories.firstOrNull().orEmpty()
                        state.copy(categories = listOf(allLabel) + categoryNames)
                    }
                }

                is ResultState.Error -> _uiState.update { it.copy(actionMessage = result.message) }
                ResultState.Loading -> Unit
            }
        }
    }

    private fun loadRoutineVerifications() {
        val groupId = currentGroupId() ?: run {
            _uiState.update { it.copy(actionMessage = "그룹 ID를 찾을 수 없습니다.") }
            return
        }
        val routineId = currentRoutineId() ?: run {
            _uiState.update { it.copy(actionMessage = "루틴을 찾을 수 없습니다.") }
            return
        }

        viewModelScope.launch {
            when (
                val result = getGroupRoutineVerificationsUseCase(
                    groupId = groupId,
                    routineId = routineId,
                    cursor = null,
                    size = 20,
                )
            ) {
                is ResultState.Success -> {
                    val myMemberId = _uiState.value.members.firstOrNull { it.isMe }?.id
                    _uiState.update {
                        it.copy(
                            posts = result.data.verifications.map { item ->
                                CertificationPostUiModel(
                                    id = item.verificationId,
                                    userName = item.nickname,
                                    body = item.content.orEmpty(),
                                    likeCount = 0,
                                    timeAgo = item.verifiedAt.orEmpty(),
                                    isMine = item.memberId == myMemberId,
                                )
                            },
                        )
                    }
                }

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
        loadGroupRoutineCategories()
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
        val groupId = currentGroupId() ?: run {
            _uiState.update { it.copy(actionMessage = "그룹 ID를 찾을 수 없습니다.") }
            return
        }
        val nextLocked = !_uiState.value.isRoomLocked

        viewModelScope.launch {
            when (val result = setGroupLockUseCase(groupId, nextLocked)) {
                // 토글 상태는 낙관적으로 바꾸지 않고 서버가 알려준 최종 값을 씀
                is ResultState.Success -> _uiState.update {
                    it.copy(
                        isRoomLocked = result.data,
                        actionMessage = if (result.data) {
                            "더 이상 다른 사람이 참여할 수 없습니다."
                        } else {
                            "다른 사람이 참여할 수 있습니다."
                        },
                    )
                }

                is ResultState.Error -> _uiState.update { it.copy(actionMessage = result.message) }
                ResultState.Loading -> Unit
            }
        }
    }

    fun onMemberKickClick() {
        val groupId = currentGroupId() ?: run {
            _uiState.update { it.copy(actionMessage = "그룹 ID를 찾을 수 없습니다.") }
            return
        }
        val targetMemberId = _uiState.value.selectedMemberId ?: return

        viewModelScope.launch {
            when (val result = kickGroupMemberUseCase(groupId, targetMemberId)) {
                is ResultState.Success -> {
                    _uiState.update { state ->
                        state.copy(
                            members = state.members.filterNot { it.id == targetMemberId },
                            selectedMemberId = null,
                            actionMessage = "멤버를 내보냈어요.",
                        )
                    }
                    loadGroupDetail(groupId)
                }

                is ResultState.Error -> _uiState.update {
                    it.copy(selectedMemberId = null, actionMessage = result.message)
                }

                ResultState.Loading -> Unit
            }
        }
    }

    /** 오늘자 그룹 루틴을 불러와 방 상세 하단 체크리스트를 채움 */
    private fun loadTodayRoutines(groupId: Long) {
        viewModelScope.launch {
            when (val result = getTodayGroupRoutinesUseCase()) {
                is ResultState.Success -> {
                    // 조회 API가 내가 속한 모든 그룹을 한 번에 주기 때문에 현재 방 것만 걸러냄
                    val todos = result.data
                        .filter { it.groupId == groupId }
                        .map { routine ->
                            GroupTodoUiModel(
                                id = routine.routineId,
                                title = routine.title,
                                deadline = routine.scheduledEndTime,
                                category = routine.categoryName,
                                isDone = routine.status == GroupRoutineStatus.COMPLETED,
                            )
                        }
                    _uiState.update { it.copy(todos = todos) }
                }

                is ResultState.Error -> _uiState.update { it.copy(actionMessage = result.message) }
                ResultState.Loading -> Unit
            }
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
                roomNameInput = "",
                routineOptions = DefaultCreateRoutineOptions,
                selectedCategory = "전체",
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
        val inviteCode = _uiState.value.inviteCodeInput.trim()
        if (inviteCode.isBlank()) {
            _uiState.update { it.copy(actionMessage = "초대코드를 입력해주세요.") }
            return
        }
        if (_uiState.value.isSubmitting) return

        _uiState.update { it.copy(isSubmitting = true) }
        viewModelScope.launch {
            try {
                // 가입 전에 미리보기로 막힌 이유(인원 초과, 잠긴 방 등)를 먼저 알려줌
                val preview = getGroupJoinPreviewUseCase(inviteCode)
                if (preview is ResultState.Error) {
                    _uiState.update { it.copy(actionMessage = preview.message) }
                    return@launch
                }
                if (preview is ResultState.Success && !preview.data.joinable) {
                    _uiState.update {
                        it.copy(actionMessage = preview.data.unavailableReason ?: "지금은 참여할 수 없는 방이에요.")
                    }
                    return@launch
                }

                when (val result = joinGroupUseCase(inviteCode)) {
                    is ResultState.Success -> {
                        val joined = result.data
                        backendGroupId = joined.groupId
                        val joinedRoutine = GroupRoutineUiModel(
                            id = joined.groupId,
                            title = joined.name,
                            lastActiveLabel = "방금 전 활동",
                            memberCount = 1,
                            routineCount = 0,
                            statusLabel = "진행중",
                            isCompleted = false,
                            todayCompletedCount = 0,
                            todayTotalCount = 0,
                            streakDays = 0,
                            monthlyAchievementRate = 0,
                            todayCertificationCount = 0,
                        )
                        _uiState.update { state ->
                            val others = state.routines.filter { it.id > 0L && it.id != joined.groupId }
                            state.copy(
                                screenMode = GroupRoutineScreenMode.Detail,
                                selectedRoutineId = joined.groupId,
                                routines = listOf(joinedRoutine) + others,
                                inviteCodeInput = "",
                                actionMessage = "그룹방에 참여했어요.",
                            )
                        }
                        // 실제 멤버/루틴 수는 상세 조회로 채움
                        loadGroupDetail(joined.groupId)
                        loadTodayRoutines(joined.groupId)
                    }

                    is ResultState.Error -> _uiState.update { it.copy(actionMessage = result.message) }
                    ResultState.Loading -> Unit
                }
            } finally {
                _uiState.update { it.copy(isSubmitting = false) }
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
        val state = _uiState.value
        val name = state.categoryInput.trim()
        if (name.isBlank()) {
            _uiState.update { it.copy(actionMessage = "카테고리를 입력해주세요.") }
            return
        }
        if (name in state.categories) {
            _uiState.update { it.copy(actionMessage = "이미 있는 카테고리예요.") }
            return
        }

        if (state.screenMode == GroupRoutineScreenMode.CreateRoutineSelect) {
            _uiState.update {
                it.copy(
                    categories = it.categories + name,
                    selectedCategory = name,
                    categoryInput = "",
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
            when (val result = createGroupRoutineCategoryUseCase(groupId, name, color = null)) {
                is ResultState.Success -> {
                    // CodeRabbit 반영: 이름뿐 아니라 서버 categoryId도 저장해야 루틴 제출 시 사용 가능
                    serverCategoryIds = serverCategoryIds + (result.data.name to result.data.categoryId)
                    _uiState.update {
                        it.copy(
                            categories = it.categories + result.data.name,
                            selectedCategory = result.data.name,
                            categoryInput = "",
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
        _uiState.update {
            it.copy(
                isRoutineSettingSheetVisible = true,
                editingRoutineId = null,
                routineDraftName = "",
                routineDraftStartTime = "08:00",
                routineDraftEndTime = "20:00",
                routineDraftRepeatDays = emptySet(),
                // 필터로 특정 카테고리를 보고 있었으면 그걸 기본값으로 깔아줌
                routineDraftCategory = it.selectedCategory.takeIf { name -> name != "전체" }.orEmpty(),
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

    fun onDismissRoutineSettingSheet() {
        _uiState.update {
            it.copy(
                isRoutineSettingSheetVisible = false,
                editingRoutineId = null,
                routineDraftName = "",
                routineDraftStartTime = "08:00",
                routineDraftEndTime = "20:00",
                routineDraftRepeatDays = emptySet(),
                isDeleteRoutineDialogVisible = false,
            )
        }
    }

    fun onRoutineDraftNameChange(value: String) {
        _uiState.update { it.copy(routineDraftName = value, actionMessage = null) }
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
            _uiState.update { it.copy(actionMessage = "루틴 이름을 입력해주세요.") }
            return
        }

        // 상단 칩은 목록 필터라서 루틴 카테고리로 쓰면 안 됨 — 시트에서 고른 값을 씀
        if (state.routineDraftCategory.isBlank()) {
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

        val categoryName = state.routineDraftCategory
        val schedules = state.routineDraftRepeatDays.toGroupRoutineSchedules(
            startTime = state.routineDraftStartTime,
            endTime = state.routineDraftEndTime,
        )
        val editingId = state.editingRoutineId
        // CodeRabbit 반영: 기본 카테고리에 없으면 새로 생성한 서버 카테고리 ID를 사용
        val categoryId = DefaultCategoryIds[categoryName] ?: serverCategoryIds[categoryName]

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
                            deadline = state.routineDraftEndTime,
                            category = categoryName,
                            startTime = state.routineDraftStartTime,
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
                                routineDraftStartTime = "08:00",
                                routineDraftEndTime = "20:00",
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
        val categoryName = state.routineDraftCategory
        
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
        val state = _uiState.value
        val editingId = state.editingRoutineId
        val groupId = currentGroupId()
        // 이미 서버에 있는 루틴(그룹 루틴 관리 화면 진입)일 때만 실제로 삭제 요청함.
        // 방 만들기 전 루틴 선택 단계랑 기본 샘플 루틴(음수 id)은 로컬 상태만 지움
        val shouldCallApi = state.screenMode == GroupRoutineScreenMode.GroupRoutineManage &&
            editingId != null && editingId > 0L && groupId != null

        clearRoutineDraft(editingId)
        if (!shouldCallApi) return

        viewModelScope.launch {
            when (val result = deleteGroupRoutineUseCase(groupId, editingId)) {
                is ResultState.Success -> {
                    _uiState.update { it.copy(actionMessage = "루틴을 삭제했어요.") }
                    loadTodayRoutines(groupId)
                }

                // 서버에서 못 지웠으면 목록에서 지운 걸 되돌려야 해서 다시 불러옴
                is ResultState.Error -> {
                    _uiState.update { it.copy(actionMessage = result.message) }
                    loadTodayRoutines(groupId)
                }

                ResultState.Loading -> Unit
            }
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
                schedules = option.repeatDays.toGroupRoutineSchedules(startTime = option.startTime, endTime = option.deadline),
            )
        }

        viewModelScope.launch {
            try {
                when (val result = createGroupUseCase(roomName, customCategories, routines)) {
                    is ResultState.Success -> {
                        // CodeRabbit 반영: 서버 응답의 루틴 개수/순서가 요청과 다르면 임시 클라이언트 ID를
                        // 서버 ID인 것처럼 사용하지 않도록 개수와 제목 일치 여부를 먼저 검증
                        val createdRoutines = result.data.routines
                        val responseIsValid = createdRoutines.size == selectedOptions.size &&
                            createdRoutines.indices.all { index -> createdRoutines[index].title == selectedOptions[index].title }
                        if (!responseIsValid) {
                            _uiState.update {
                                it.copy(actionMessage = "그룹은 만들어졌지만 루틴 정보를 받아오지 못했어요. 다시 시도해주세요.")
                            }
                            return@launch
                        }

                        val groupId = result.data.groupId
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
                                selectedCategory = "전체",
                                routines = listOf(newRoutine) + existingServerRoutines,
                                todos = selectedTodos,
                                actionMessage = "방이 만들어졌어요.",
                            )
                        }
                        // 방을 막 만들면 구성원이 나 혼자라 mock 멤버가 그대로 남음 — 상세 조회로 덮어씀
                        loadGroupDetail(groupId)
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
    )
}

// 스웨거 문서에 createdAt 포맷 예시가 없어 ISO-8601(Instant)로 우선 가정하고, 파싱에 실패하면
// (그룹핑 판단용이라 실패해도 치명적이지 않으므로) 현재 시각으로 대체한다.
private fun String.toEpochMillisOrNow(): Long =
    runCatching { Instant.parse(this).toEpochMilli() }.getOrDefault(System.currentTimeMillis())

private val KoreanDayToRepeatDay = mapOf(
    "\uC77C" to RepeatDay.SUNDAY,
    "\uC6D4" to RepeatDay.MONDAY,
    "\uD654" to RepeatDay.TUESDAY,
    "\uC218" to RepeatDay.WEDNESDAY,
    "\uBAA9" to RepeatDay.THURSDAY,
    "\uAE08" to RepeatDay.FRIDAY,
    "\uD1A0" to RepeatDay.SATURDAY,
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

private fun Set<String>.toGroupRoutineSchedules(startTime: String, endTime: String): List<GroupRoutineSchedule> {
    return mapNotNull { KoreanDayToRepeatDay[it] }.map { day ->
        GroupRoutineSchedule(repeatDay = day, startTime = startTime, endTime = endTime)
    }
}
