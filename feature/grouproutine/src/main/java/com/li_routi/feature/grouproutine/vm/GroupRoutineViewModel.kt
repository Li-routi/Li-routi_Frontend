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

private data class ChatReadTarget(
    val groupId: Long,
    val lastReadMessageId: Long,
)

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
    private val updateChatReadPositionUseCase: UpdateChatReadPositionUseCase = ChatContainer.updateChatReadPositionUseCase,
    private val getEmoticonsUseCase: GetEmoticonsUseCase = ChatContainer.getEmoticonsUseCase,
    private val connectChatSocketUseCase: ConnectChatSocketUseCase = ChatContainer.connectChatSocketUseCase,
    private val observeChatMessagesUseCase: ObserveChatMessagesUseCase = ChatContainer.observeChatMessagesUseCase,
    private val sendChatMessageUseCase: SendChatMessageUseCase = ChatContainer.sendChatMessageUseCase,
    private val disconnectChatSocketUseCase: DisconnectChatSocketUseCase = ChatContainer.disconnectChatSocketUseCase,
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(GroupRoutineUiState())
    val uiState: StateFlow<GroupRoutineUiState> = _uiState.asStateFlow()

    // 癲?????쇰궚?癲ル슣????濚욌꼬?댄꺍??곕㎜壤????⑤；?????덉툗 ????????ㅼ뒦??????㎣筌??熬곣뫀?櫻?? ??釉먮뻤????熬곥룊??ViewModel ??????????爾???筌먲퐢??
    private var chatSocketJob: Job? = null
    private var chatReadJob: Job? = null
    private var latestChatReadTarget: ChatReadTarget? = null
    private var unreadRoutineVerificationsJob: Job? = null

    // PR ?袁⑸즵??? Mock ID?? ???源놁졆 ??筌먦끉裕?ID ??됰슣維??
    // createGroupUseCase ?濚밸Þ?볠쾮???筌?猷??縕???熬곣뫀???????釉뚰?????源낃도 ???袁⑸즵獒???嚥▲꺂痢????源놁졆 ??筌먦끉裕???숆강筌?쓣爾?ID?????濚왿몾??????덊렡.
    private var backendGroupId: Long? = null

    // API ?嶺뚮ㅎ??????????currentGroupId()?????⑤챷?????源놁졆 backendGroupId??癲ル슔?蹂?덫???筌뤾퍓???
    private fun currentGroupId(): Long? = backendGroupId

    // CodeRabbit ?袁⑸즵??? ??룸Ŧ爾??????⑤챶援?????ш끽維쀨린??1L????筌먦끉裕?????源놁졆 ID癲ル슪?ｇ몭???怨뚮옖?雅?겦?쇿퐲? ????녳뵣??nullable???怨뚮뼚???
    private fun currentRoutineId(): Long? = _uiState.value.todos.firstOrNull()?.id

    init {
        loadParticipatingGroups()
    }

    // CodeRabbit ?袁⑸즵??? ????궈???獄쏅똻?????筌먦끉裕? ?怨멸텭??沃섅뀙??關履????????-> categoryId. DefaultCategoryIds??????몄툗 ?怨멸텭??沃섅뀙??關履????筌믨퉭????????
    private var serverCategoryIds: Map<String, Long> = emptyMap()

    // ????늄???癲ル슢?꾤땟戮⑤뭄?????"??????좊읈????怨???癲????癲ル슢????????щ뮝??筌??嶺뚮쮳?년봼??????. ?嶺뚮ㅎ??????雅????袁⑸즴???????????類??袁?맪??釉뚰????
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
                isNewCertificationDialogVisible = false,
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
        _uiState.update { it.copy(isConfirmedOwner = false) }
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
                        )
                    }
                }

                is ResultState.Error -> _uiState.update { it.copy(actionMessage = result.message) }
                ResultState.Loading -> Unit
            }
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
                showOnlyMyCertifications = false,
                selectedCertificationMemberId = null,
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

        // ??????????ㅼ굣?????源끹걬癲???????嶺뚮ㅎ??????쑩???繞???嚥▲꺆諭???獄?껫????怨뚮옖?雅??????筌먦끉裕??????볥윞 ??숆강筌?쑜??
        // ???⑥щ뎁??????몄릇??癲ル슢?????????????????????ш끽維곮??DTO ?怨뚮뼚??????⑤챶?????????⑤９苑?嶺뚮ㅎ????筌먦끉??癲ル슪?ｇ몭????좊읈??濚왿몾???
        val replyTarget = _uiState.value.replyTarget
        val content = if (replyTarget != null) {
            "[답장] ${replyTarget.senderName}: ${replyTarget.message.take(30)}\n$text"
        } else {
            text
        }

        val message = NewChatMessage(
            clientMessageId = UUID.randomUUID().toString(),
            type = ChatMessageType.TEXT,
            content = content,
            emoticonCode = null,
        )
        viewModelScope.launch {
            when (val result = sendChatMessageUseCase(groupId, message)) {
                is ResultState.Success -> _uiState.update { it.copy(chatDraftText = "", replyTarget = null) }
                is ResultState.Error -> _uiState.update { it.copy(actionMessage = result.message) }
                ResultState.Loading -> Unit
            }
        }
    }

    /** 癲????癲ル슢?????????????렺轅곗땡??낆쑋???肉??????熬곣뫀????⑤똾留???????????ㅼ굣筌뤿뱶??癲ル슣???嶺뚮쮳?노뭄?????嶺뚮ㅎ????筌먲퐢?? */
    fun onReplyTargetSelected(message: ChatMessageUiModel) {
        _uiState.update { it.copy(replyTarget = message) }
    }

    /** ?????雅?퍔瑗띰㎖???????깅탿(???筌???????爾???類?????????????嶺뚮ㅎ????筌먲퐢?? */
    fun onReplyTargetCleared() {
        _uiState.update { it.copy(replyTarget = null) }
    }

    // ????癲ル슣鍮뽳쭕??癲??????⑥????ш끽維뽬땻??筌먲퐢???????源놁졆 ??釉먮뻤???袁⑸즵???? ?????????㎣筌???⑥??????????몄툗 ??怨쀫뮛繞??筌믨퉭堉????덉쉐??????????勇싲짅援⒴퐲?덉쪎??삵렡.
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

    /** ????????ㅼ뒦??????怨쀫뮛繞??筌믨퉭堉????덉쉐 ????㎣筌???筌믨퀣援???REST ??????釉뚰?????筌?留??癲ル슣???몄춿?? ???ㅼ뒦????熬곣뫖????癲ル슢?????????????袁ㅼ땡?堉온 ????낆툗?? */
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
        viewModelScope.launch { disconnectChatSocketUseCase() }
    }

    /** cursor가 null이면 최신 50개를, 값이 있으면 그 커서보다 오래된 과거 메시지 50개를 불러온다. */
    private suspend fun loadChatMessages(groupId: Long, cursor: Long? = null) {
        _uiState.update { it.copy(isChatLoading = true) }
        when (val result = getChatMessagesUseCase(groupId = groupId, cursor = cursor, size = 50)) {
            is ResultState.Success -> {
                val myMemberId = _uiState.value.members.firstOrNull { it.isMe }?.id
                val historyMessages = result.data.messages.map { it.toUiModel(isMine = it.senderId == myMemberId) }
                _uiState.update { state ->
                    // ?釉뚰??????쑩?젆?????됰씚逾??????????⑥???沃섅굥?? ???怨쀪퐨??癲ル슢????????? ?濡ろ뜐????????怨쀪퐨 id ??れ삀?????⑥????獄?낮萸??
                    val merged = (historyMessages + state.chatMessages).distinctBy { it.id }.sortedBy { it.id }
                    state.copy(
                        chatMessages = merged,
                        chatNextCursor = result.data.nextCursor,
                        hasMoreChatHistory = result.data.hasNext,
                        isChatLoading = false,
                        unreadChatCount = if (historyMessages.isEmpty()) 0 else state.unreadChatCount,
                    )
                }
                // 癲ル슢???癲?癲ル슢???????嚥싲갭큔?? ??? ?濡ろ뜏????肉???筌먦끉裕???袁⑸즵?????釉먮뻤??????怨쀪퐨?? 癲ル슢?꾤땟戮⑤뭄?????덇콬???곸쓸???.
                if (cursor == null) {
                    historyMessages.lastOrNull()?.let { last -> markChatRead(groupId, last.id) }
                }
            }

            is ResultState.Error -> _uiState.update { it.copy(actionMessage = result.message, isChatLoading = false) }
            ResultState.Loading -> _uiState.update { it.copy(isChatLoading = false) }
        }
    }

    /** 채팅 목록을 위로 스크롤해 맨 위 근처에 도달했을 때 호출해 이전 대화를 이어서 불러온다. */
    fun onChatScrolledToTop() {
        val state = _uiState.value
        if (state.isChatLoading || !state.hasMoreChatHistory) return
        val groupId = currentGroupId() ?: return
        viewModelScope.launch { loadChatMessages(groupId, cursor = state.chatNextCursor) }
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
        // viewModelScope??onCleared() ??筌믨퀣??????? ???爾???筌뚯슦苑????怨쀪퐨 ?????????????명렡 ???怨뚮옓????????몄???ш끽維곩ㅇ?????룸뎿異??嶺뚮㉡?섌걡?
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
        val message = _uiState.value.messageDraft.trim()
        val groupId = currentGroupId()
        // mock ????筌먦끉裕???숆강筌?쓣爾???ш끽維筌?????????源놁벁癲ル슪?ｇ몭???棺??짆?쏆춾????ㅺ컼?얜쓣異??袁⑸즴???
        if (groupId == null) {
            applyMyStatusMessage(message)
            return
        }

        if (_uiState.value.isSubmitting) return
        _uiState.update { it.copy(isSubmitting = true) }
        viewModelScope.launch {
            try {
                when (val result = updateGroupMemberStatusMessageUseCase(groupId, message)) {
                    // ??筌먦끉裕??좊읈? ???濚왿몾????좊즴?????숆강筌????袁⑸즵?????⑤；??????렺???????釉먮뻤?????????源낅빖??? ???怨룹쓱
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
                        LeaveGroupResult.Left -> exitRoom(groupId, "그룹방을 나갔어요.")
                        // ?袁⑸젻泳??? 癲?????⑸춪 ????????????????? ???怨뺣빰 ??醫딅땾???돥椰?
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

    /** ????????????살씁????猷??????癲ル슢?꾤땟戮⑤뭄?????????????怨멸텭????ｏ쭗?癲ル슣?????ㅼ뒭??癲ル슢?꾤땟戮⑤뭄???釉먮뻤????⑥???????ъ녃域?濚?*/
    private fun exitRoom(groupId: Long, message: String) {
        backendGroupId = null
        _uiState.update { state ->
            state.copy(
                screenMode = GroupRoutineScreenMode.List,
                routines = state.routines.filterNot { it.id == groupId },
                selectedRoutineId = null,
                selectedMemberId = null,
                groupInviteCode = null,
                // ????깅쐿 ?袁⑸젻泳??癲ル슢???볥뼀?癲ル슪???띿물筌먯옓????嶺? ???源낆쓱 ????釉먮뻤?????? ??熬곣뫗踰?????
                members = emptyList(),
                todos = emptyList(),
                isConfirmedOwner = false,
                isLeaveRoomDialogVisible = false,
                isDeleteRoomDialogVisible = false,
                actionMessage = message,
            )
        }
    }

    /** ??숆강筌?쓣爾?猿뗫궚????ㅳ늾?????됰씭???? ??숆강筌?쓣爾???떻??縕???熬곣뫀???????늄???癲ル슢?꾤땟戮⑤뭄?????源놁졆 ??筌먦끉裕????Β?????ㅻ깹鸚?癲??? */
    private fun loadGroupDetail(groupId: Long) {
        viewModelScope.launch {
            if (myMemberId == null) {
                when (val myInfo = getMyInfoUseCase()) {
                    is ResultState.Success -> myMemberId = myInfo.data.memberId
                    // ??memberId??癲ル슢?꾤땟????곗떵?癲ル슢?꾤땟???????늄?????????쒙쭗?쒖뒙?癲ル슔?蹂?엥??????ㅺ컼??癲ル슢??????? ???쒓낯??癲????
                    // 癲ル슢?????????????깅탿 ????????? ???源낅빖?? ??嚥?援????釉먮뻤?????숆강筌????????????癲ル슢????
                    is ResultState.Error -> {
                        _uiState.update { it.copy(actionMessage = myInfo.message) }
                        return@launch
                    }

                    ResultState.Loading -> return@launch
                }
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
                                    completedCount = member.completedCount.toInt(),
                                    totalCount = member.totalCount.toInt(),
                                    totalLikeCount = member.totalLikeCount.toInt(),
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
                // PR ?袁⑸즵??? ResultState.Error ???袁⑸즴??繞?????궈??袁⑸즵獒??issue)??? ??熬곥걿???????癲ル슢??????? ??ш끽維??堉?(??れ삀????縕???熬곣뫀??????뺤깙????袁⑸젻泳?)
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
            _uiState.update { it.copy(actionMessage = "루틴 ID를 찾을 수 없습니다.") }
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
                                    memberId = item.memberId,
                                    userName = item.nickname,
                                    body = item.content.orEmpty(),
                                    likeCount = item.likeCount.toInt(),
                                    timeAgo = item.verifiedAt.orEmpty(),
                                    isMine = item.memberId == myMemberId,
                                    isLiked = item.liked,
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
        val groupId = currentGroupId()
        val lastReadId = _uiState.value.newCertifications.maxOfOrNull { it.id }
        _uiState.update { it.copy(isNewCertificationDialogVisible = false) }

        if (groupId == null || lastReadId == null) return

        viewModelScope.launch {
            when (val result = markGroupRoutineVerificationsReadUseCase(groupId, lastReadId)) {
                is ResultState.Success -> Unit
                is ResultState.Error -> _uiState.update { it.copy(actionMessage = result.message) }
                ResultState.Loading -> Unit
            }
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
                        )
                    }
                    _uiState.update {
                        it.copy(
                            newCertifications = unreadCertifications,
                            isNewCertificationDialogVisible = unreadCertifications.isNotEmpty(),
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
                }

                is ResultState.Error -> _uiState.update { it.copy(actionMessage = result.message) }
                ResultState.Loading -> Unit
            }
        }
    }

    fun onNewCertificationLikeClick(verificationId: Long) {
        val groupId = currentGroupId() ?: return

        viewModelScope.launch {
            when (val result = likeGroupRoutineVerificationUseCase(groupId = groupId, verificationId = verificationId)) {
                is ResultState.Success -> onDismissNewCertificationDialog()
                is ResultState.Error -> _uiState.update { it.copy(actionMessage = result.message) }
                ResultState.Loading -> Unit
            }
        }
    }

    fun onNewCertificationDisappointClick(verificationId: Long) {
        val groupId = currentGroupId() ?: return

        viewModelScope.launch {
            when (val result = disappointGroupRoutineVerificationUseCase(groupId = groupId, verificationId = verificationId)) {
                is ResultState.Success -> onDismissNewCertificationDialog()
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
                }

                is ResultState.Error -> _uiState.update { it.copy(actionMessage = result.message) }
                ResultState.Loading -> Unit
            }
        }
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
        val state = _uiState.value
        val targetMemberId = state.pendingLeaderMemberId
        val myId = state.members.firstOrNull { it.isMe }?.id
        // ??? ??關履??癲???鍮??嶺뚮Ĳ?됮??嚥????袁⑸즴?????獒??????⑤９苑????筌먦끉裕끾퓴諛매?? ????ш끽維?????⑤챶苡?
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
                        loadGroupDetail(groupId)
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
                // ?袁⑸젻泳???????듬젵??野껊갭???????⑤?彛??袁⑸젻泳?????ш끽維筌?
                isConfirmedOwner = if (isStillLeader) state.isConfirmedOwner else false,
                pendingLeaderMemberId = null,
                actionMessage = message,
            )
        }
    }

    fun onRoomLockClick() {
        val groupId = currentGroupId() ?: run {
            _uiState.update { it.copy(actionMessage = "그룹 ID를 찾을 수 없습니다.") }
            return
        }
        val nextLocked = !_uiState.value.isRoomLocked
        // ?????嚥???lock/unlock????筌?留??怨뚮옖??????⑤챶??????덈빰??????⑸춪
        if (_uiState.value.isSubmitting) return
        _uiState.update { it.copy(isSubmitting = true) }

        viewModelScope.launch {
            try {
                when (val result = setGroupLockUseCase(groupId, nextLocked)) {
                    // ??? ???ㅺ컼?????????ㅼ굣筌뤿뱶???袁⑸즴???癲ル슣?? ??熬곥걿????筌먦끉裕??좊읈? ??????㉨?? 癲ル슔?됭짆?륂렭???좊즴?????
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

    /** ????몄툜????숆강筌?쓣爾???룸Ŧ爾?????됰씭???? ?????ㅳ늾????嚥▲꺂??癲ル슪???띿물筌먯옓????嶺? 癲??? */
    private fun loadTodayRoutines(groupId: Long) {
        todayRoutinesJob?.cancel()
        todayRoutinesJob = viewModelScope.launch {
            when (val result = getTodayGroupRoutinesUseCase()) {
                is ResultState.Success -> {
                    // ???쑩?젆??????????몄툗 ?????袁⑸젻泳???????野껊갭??癲ル슣??????癲ル슪???띿물筌먯옓????嶺? ??????ㅽ떝??????
                    if (backendGroupId != groupId) return@launch
                    // ?釉뚰???API??좊읈? ??? ???⑤８??癲ル슢?꾤땟?????숆강筌?쓣爾?????類???산덩???낆뒩??곷뎨????????ш끽維?????濡ろ뜏?蹂잜맪?癲꾧퀗?э㎖???
                    val groupRoutines = result.data
                        .filter { it.groupId == groupId }
                    val todos = groupRoutines.map { routine ->
                            GroupTodoUiModel(
                                id = routine.routineId,
                                title = routine.title,
                                deadline = routine.scheduledEndTime,
                                category = routine.categoryName,
                                isDone = routine.status == GroupRoutineStatus.COMPLETED,
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
                        state.copy(todos = todos, routineOptions = updatedOptions)
                    }
                }

                is ResultState.Error -> _uiState.update { it.copy(actionMessage = result.message) }
                ResultState.Loading -> Unit
            }
        }
    }

    fun onRoomNameEditConfirmClick() {
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
        _uiState.update { it.copy(inviteCodeInput = value, actionMessage = null) }
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
                // ??좊읈?????ш끽維??雅?퍔瑗띰㎖???????깅탿??癲ル슢??쭕??????(?嶺뚮ㅎ????縕??? ???ル㎣?????????沃섅굥?? ??????㉨?
                val preview = getGroupJoinPreviewUseCase(inviteCode)
                if (preview is ResultState.Error) {
                    _uiState.update { it.copy(actionMessage = preview.message) }
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
                                isConfirmedOwner = false,
                                actionMessage = "그룹에 참여했어요.",
                            )
                        }
                        // ???源놁졆 癲ル슢???볥뼀???룸Ŧ爾????嚥▲꺂痢????ㅳ늾???釉뚰????대퓠?癲???
                        loadGroupDetail(joined.groupId)
                        loadGroupRoutineCategories()
                        loadTodayRoutines(joined.groupId)
                        loadUnreadRoutineVerifications()
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
        _uiState.update { it.copy(actionMessage = "초대코드가 복사되었어요.") }
    }

    fun onRoomNameChange(value: String) {
        _uiState.update { it.copy(roomNameInput = value, actionMessage = null) }
    }

    fun onCreateRoomNextClick() {
        _uiState.update { state ->
            if (state.roomNameInput.isBlank()) {
                state.copy(actionMessage = "방 이름을 입력해 주세요.")
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
                categoryColorInput = null,
                actionMessage = null,
            )
        }
    }

    fun onDismissCategorySheet() {
        _uiState.update { it.copy(isCategorySheetVisible = false, categoryInput = "", categoryColorInput = null) }
    }

    fun onCategoryInputChange(value: String) {
        _uiState.update { it.copy(categoryInput = value, actionMessage = null) }
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
                    // CodeRabbit ?袁⑸즵??? ????κ퓭源욘뤃???ш끽維?????筌먦끉裕?categoryId?????濚왿몾?????룸Ŧ爾????筌믨퉭??????????좊읈???
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
            _uiState.update { it.copy(actionMessage = "루틴 이름을 입력해 주세요.") }
            return
        }

        val routineState = state.copy(routineDraftCategory = state.resolveRoutineDraftCategory())

        if (routineState.routineDraftCategory.isBlank()) {
            _uiState.update { it.copy(actionMessage = "카테고리를 선택해 주세요.") }
            return
        }

        // ??筌먦끉裕??좊읈? schedules??癲ル슔?됭짆??1????釉먮윥????⑤똾留????뽮덫???0????좊읈????????? ???源낆쓱) 雅?퍔瑗띰㎖??癲ル슢??쭕??
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

        // PR ?袁⑸즵??? ???ㅻ쿋??濚욌꼬?댄꺇??????????⑥???嶺뚮ㅏ援앯뵳?API ???湲깅룪 ?嶺뚮ㅎ????袁⑸젻泳? ??좊읈???
        if (state.isSubmitting) return
        _uiState.update { it.copy(isSubmitting = true) }

        val categoryName = state.routineDraftCategory
        val categoryColor = state.categoryColors[categoryName]
        val schedules = state.routineDraftRepeatDays.toGroupRoutineSchedules(
            startTime = state.routineDraftStartTime,
            endTime = state.routineDraftEndTime,
        )
        val editingId = state.editingRoutineId
        // CodeRabbit ?袁⑸즵??? ??れ삀????怨멸텭??沃섅뀙??關履??????⑤챶?뺧┼?????궈???獄쏅똻?????筌먦끉裕??怨멸텭??沃섅뀙??關履??ID??????
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
                                    // PR ?袁⑸즵??? ???쒓낯??????れ삀????isSelected ???ㅺ컼?????
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
                        // ???ㅳ늾????釉먮뻤??癲ル슪???띿물筌먯옓?????todos)??????몄툜????룸Ŧ爾???釉뚰????대퓠?癲??????????????筌먲퐣?????怨뺣빰 ??됰씭????????
                        loadTodayRoutines(groupId)
                    }
                    is ResultState.Error -> _uiState.update { it.copy(actionMessage = result.message) }
                    ResultState.Loading -> Unit
                }
            } finally {
                // ?濚밸Þ?볠쾮?????됰꽡 ???뺣섕?????곕쿊 濚욌꼬?댄꺇???嶺뚮ㅎ????袁⑸젻泳? ?????μ쐺??縕?猿녿뎨??
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
        // ???? ??筌먦끉裕??????덉툗 ??룸Ŧ爾????숆강筌?쓣爾???룸Ŧ爾?????굿????釉먮뻤??癲ル슣??????????????源놁졆????????釉먯뒜???
        // ??癲ル슢?????琉왈?????룸Ŧ爾?????ャ뀕????影?됀????れ삀??????얜?源???룸Ŧ爾???????id)?? ?棺??짆?쏆춾????ㅺ컼?얜쓣異?癲ル슣????
        val shouldCallApi = state.screenMode == GroupRoutineScreenMode.GroupRoutineManage &&
            editingId != null && editingId > 0L && groupId != null

        if (!shouldCallApi) {
            // ?棺??짆?쏆춾???ш끽維????癲ル슢?????琉왈?????影?됀? ???얜?源???룸Ŧ爾????????袁⑸즴??繞?癲ル슢?꾤땟戮⑤뭄?????癲ル슣????
            clearRoutineDraft(editingId)
            return
        }
        if (state.isSubmitting) return
        // ???影?놁씀? ????됰꽡??嚥???routineOptions????嚥▲꺃????袁⑸젻泳?쉬??????⑤９苑?? ????レ쉐癲?????됀?癲ル슢?꾤땟戮⑤뭄?? ?濚밸Þ?볠쾮???ш끽維??癲꾧퀗?????ｉ?
        closeRoutineSheets()
        _uiState.update { it.copy(isSubmitting = true) }

        viewModelScope.launch {
            try {
                when (val result = deleteGroupRoutineUseCase(groupId, editingId)) {
                    is ResultState.Success -> {
                        clearRoutineDraft(editingId)
                        _uiState.update { it.copy(actionMessage = "루틴이 삭제되었어요.") }
                        loadTodayRoutines(groupId)
                    }

                    is ResultState.Error -> _uiState.update { it.copy(actionMessage = result.message) }
                    ResultState.Loading -> Unit
                }
            } finally {
                _uiState.update { it.copy(isSubmitting = false) }
            }
        }
    }

    /** 癲ル슢?꾤땟戮⑤뭄?? ??숆강筌?????????嶺뚮ㅎ?당빊?????レ쉐/???????源낇꼧???⑥???숆강筌?쑚?????甕?*/
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
        
        // PR ?袁⑸즵??? ???ㅻ쿋??濚욌꼬?댄꺇??????????⑥???嶺뚮ㅏ援앯뵳???숆강筌?쓣爾?2????獄쏅똻???袁⑸젻泳?
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
                        // CodeRabbit ?袁⑸즵??? ??筌먦끉裕????쑩?젆????룸Ŧ爾????좊즵獒????筌?留??좊읈? ??釉먯뒜???????렺?異???ш끽維뽳쭛????????⑤９苑??ID??
                        // ??筌먦끉裕?ID???濡ろ뜏?遺삳쐩???????? ????녳뵣????좊즵獒??? ??筌먯룄肄???繹먮봾萸???????沃섅굥?? ?濡ろ떟?癲?
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
                                isConfirmedOwner = true,
                                actionMessage = "\uBC29\uC774 \uB9CC\uB4E4\uC5B4\uC84C\uC5B4\uC694.",
                            )
                        }
                        // ?袁⑸젻泳??癲?癲ル슢???????떵?????늄??????????繹먮냱寃??mock 癲ル슢???볥뼀?筌? ??숆강筌??????쒑린??????ㅳ늾???釉뚰????대퓠??????
                        loadGroupDetail(groupId)
                        loadGroupRoutineCategories()
                        loadTodayRoutines(groupId)
                    }
                    is ResultState.Error -> _uiState.update { it.copy(actionMessage = result.message) }
                    ResultState.Loading -> Unit
                }
            } finally {
                // ??ш끽維??????筌믨퉭?????ㅺ컼?????⑤챷??
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
    )
}

// ???源낆뱼癲????뽮덫???createdAt ???????怨뺣빰??좊읈? ???⑤９苑?ISO-8601(Instant)?????Β?띾쭡 ??좊읈??嶺뚮쮳?년봼?? ??????????됰꽡??嚥???
// (??숆강筌?쓣爾????????獄????????됰꽡??????怨멸땀?쀫씛????ㅼ굣?얠쥉異?堉온 ???怨룔걬雅?퍔源??? ??ш끽維????癰?????⑥????癲ル슪???쀫눀??
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

// ?袁⑸즲??援????れ삀????怨멸텭??沃섅뀙??關履???????癲꾧퀗???????????節뚮쳥?????됰Ŧ苑?嶺뚮㉡?섌걡?癲ル슢?????????????) ???????categoryId
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

/** ??筌먦끉裕??怨멸텭??沃섅뀙??關履????? RED/BLUE ??좊즵?? ?????뽮덫??enum?????Kotlin enum ????????숆강筌????怨뚮옖?雅?겦?쇿퐲?400 ??*/
private fun CategoryColor.serverCode(): String = name.uppercase()
