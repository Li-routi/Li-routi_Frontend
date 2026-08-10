package com.li_routi.feature.grouproutine.screen

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.li_routi.core.common.ui.nav.AppBottomNavBar
import com.li_routi.core.common.ui.nav.AppBottomTab
import com.li_routi.core.designsystem.component.CheckBoxState
import com.li_routi.core.designsystem.component.CustomCheckBox
import com.li_routi.core.common.ui.routine.CategoryAddBottomSheet
import com.li_routi.core.common.ui.routine.CategoryColor
import com.li_routi.core.designsystem.component.LiroutiBottomSheet
import com.li_routi.core.designsystem.component.LiroutiDashedAddButton
import com.li_routi.core.designsystem.component.LiroutiDaySelector
import com.li_routi.core.designsystem.component.LiroutiPrimaryButton
import com.li_routi.core.designsystem.component.LiroutiSearchField
import com.li_routi.core.designsystem.component.LiroutiSwitch
import com.li_routi.core.designsystem.R as DesignSystemR
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme
import com.li_routi.feature.grouproutine.R
import com.li_routi.feature.grouproutine.component.ChatEmoticonUiModel
import com.li_routi.feature.grouproutine.navigation.GrouproutineEntryPoint
import com.li_routi.feature.grouproutine.vm.CertificationPostUiModel
import com.li_routi.feature.grouproutine.vm.CreateRoutineOptionUiModel
import com.li_routi.feature.grouproutine.vm.GroupMemberUiModel
import com.li_routi.feature.grouproutine.vm.GroupRoutineScreenMode
import com.li_routi.feature.grouproutine.vm.GroupRoutineUiModel
import com.li_routi.feature.grouproutine.vm.GroupRoutineUiState
import com.li_routi.feature.grouproutine.vm.GroupRoutineViewModel
import com.li_routi.feature.grouproutine.vm.GroupTodoUiModel
import com.li_routi.feature.grouproutine.vm.NewCertificationUiModel
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

private val ScreenBackground = Color(0xFFF4F7FB)
private val FillBackground = Color(0xFFFAFAFA)
private val LabelDefault = Color(0xFF171719)
private val LabelSub = Color(0xFF46474C)
private val LabelInfo = Color(0xFF878A93)
private val BorderDefault = Color(0xFFDBDCDF)
private val BorderAlternative = Color(0xFFF4F4F5)
private val BorderStrong = Color(0xFFAEB0B6)
private val PrimaryNormal = Color(0xFF338AFF)
private val PrimaryActive = Color(0xFF296ECC)
private val SecondaryNormal = Color(0xFF00AAD2)
private val SecondaryBackground = Color(0xFFF4F7FB)
private val CompleteText = Color(0xFF008C51)
private val CompleteBackground = Color(0xFFE0F8E9)
private val DangerBase = Color(0xFFFF6363)

@Composable
fun GroupRoutineRoute(
    initialEntryPoint: GrouproutineEntryPoint? = null,
    onInitialEntryPointConsumed: () -> Unit = {},
    viewModel: GroupRoutineViewModel = viewModel { GroupRoutineViewModel() },
    onTabSelected: (AppBottomTab) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(initialEntryPoint) {
        when (initialEntryPoint) {
            GrouproutineEntryPoint.CreateRoom -> viewModel.onCreateRoomClick()
            GrouproutineEntryPoint.JoinWithInviteCode -> viewModel.onJoinByCodeClick()
            null -> Unit
        }
        if (initialEntryPoint != null) onInitialEntryPointConsumed()
    }

    GroupRoutineScreen(
        uiState = uiState,
        onTabSelected = onTabSelected,
        onRoutineClick = viewModel::onRoutineClick,
        onBackClick = viewModel::onBackClick,
        onAddClick = viewModel::onAddClick,
        onSearchInputChange = viewModel::onSearchInputChange,
        onDismissActionSheet = viewModel::onDismissActionSheet,
        onCreateRoomClick = viewModel::onCreateRoomClick,
        onJoinByCodeClick = viewModel::onJoinByCodeClick,
        onRoomNameChange = viewModel::onRoomNameChange,
        onCreateRoomNextClick = viewModel::onCreateRoomNextClick,
        onCreateFlowCloseClick = viewModel::onCreateFlowCloseClick,
        onInviteCodeChange = viewModel::onInviteCodeChange,
        onInviteCodeConfirmClick = viewModel::onInviteCodeConfirmClick,
        onCreateRoutineOptionClick = viewModel::onCreateRoutineOptionClick,
        onCreateRoutineSelectAllClick = viewModel::onCreateRoutineSelectAllClick,
        onCategoryClick = viewModel::onCategoryClick,
        onCategoryAddClick = viewModel::onCategoryAddClick,
        onDismissCategorySheet = viewModel::onDismissCategorySheet,
        onCategoryInputChange = viewModel::onCategoryInputChange,
        onCategoryConfirmClick = viewModel::onCategoryConfirmClick,
        onRoutineAddClick = viewModel::onRoutineAddClick,
        onRoutineSettingClick = viewModel::onRoutineSettingClick,
        onDismissRoutineSettingSheet = viewModel::onDismissRoutineSettingSheet,
        onRoutineDraftNameChange = viewModel::onRoutineDraftNameChange,
        onRoutineDraftStartTimeChange = viewModel::onRoutineDraftStartTimeChange,
        onRoutineDraftEndTimeChange = viewModel::onRoutineDraftEndTimeChange,
        onRepeatDayClick = viewModel::onRepeatDayClick,
        onRoutineSettingConfirmClick = viewModel::onRoutineSettingConfirmClick,
        onRoutineDeleteClick = viewModel::onRoutineDeleteClick,
        onDismissDeleteRoutineDialog = viewModel::onDismissDeleteRoutineDialog,
        onConfirmDeleteRoutineClick = viewModel::onConfirmDeleteRoutineClick,
        onLeaveRoomClick = viewModel::onLeaveRoomClick,
        onDismissLeaveRoomDialog = viewModel::onDismissLeaveRoomDialog,
        onLeaveRoomConfirmClick = viewModel::onLeaveRoomConfirmClick,
        onDismissDeleteRoomDialog = viewModel::onDismissDeleteRoomDialog,
        onDeleteRoomConfirmClick = viewModel::onDeleteRoomConfirmClick,
        onMemberKickClick = viewModel::onMemberKickClick,
        onDismissKickMemberDialog = viewModel::onDismissKickMemberDialog,
        onKickMemberConfirmClick = viewModel::onKickMemberConfirmClick,
        onRoutineDraftCategoryClick = viewModel::onRoutineDraftCategoryClick,
        onCategoryColorSelected = viewModel::onCategoryColorSelected,
        onCreateRoomDoneClick = viewModel::onCreateRoomDoneClick,
        onTodoCheckedChange = viewModel::onTodoCheckedChange,
        onCertificationTabClick = viewModel::onCertificationTabClick,
        onCertificationMemberClick = viewModel::onCertificationMemberClick,
        onCertificationSummaryClick = viewModel::onCertificationSummaryClick,
        onCertificationLikeClick = viewModel::onCertificationLikeClick,
        onDismissNewCertificationDialog = viewModel::onDismissNewCertificationDialog,
        onNewCertificationDisappointClick = viewModel::onNewCertificationDisappointClick,
        onNewCertificationLikeClick = viewModel::onNewCertificationLikeClick,
        onMemberClick = viewModel::onMemberClick,
        onDismissMemberDialog = viewModel::onDismissMemberDialog,
        onChatClick = viewModel::onChatClick,
        onChatMessageChange = viewModel::onChatMessageChange,
        onChatSendClick = viewModel::onChatSendClick,
        onChatEmojiSelected = viewModel::onChatEmojiSelected,
        onMessageEditClick = viewModel::onMessageEditClick,
        onMessageDraftChange = viewModel::onMessageDraftChange,
        onDismissMessageEditSheet = viewModel::onDismissMessageEditSheet,
        onMessageEditConfirmClick = viewModel::onMessageEditConfirmClick,
        onSettingsClick = viewModel::onSettingsClick,
        onInviteCodeCopyClick = viewModel::onInviteCodeCopyClick,
        onGroupRoutineManageClick = viewModel::onGroupRoutineManageClick,
        onRoomNameEditClick = viewModel::onRoomNameEditClick,
        onLeaderSettingsClick = viewModel::onLeaderSettingsClick,
        onLeaderMemberClick = viewModel::onLeaderMemberClick,
        onLeaderTransferConfirmClick = viewModel::onLeaderTransferConfirmClick,
        onRoomAlarmSettingsClick = viewModel::onRoomAlarmSettingsClick,
        onRoomLockClick = viewModel::onRoomLockClick,
        onRoomNameEditConfirmClick = viewModel::onRoomNameEditConfirmClick,
        onDismissActionMessage = viewModel::onDismissActionMessage,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GroupRoutineScreen(
    uiState: GroupRoutineUiState,
    onRoutineClick: (Long) -> Unit,
    onBackClick: () -> Unit,
    onAddClick: () -> Unit,
    onSearchInputChange: (String) -> Unit,
    onDismissActionSheet: () -> Unit,
    onCreateRoomClick: () -> Unit,
    onJoinByCodeClick: () -> Unit,
    onRoomNameChange: (String) -> Unit,
    onCreateRoomNextClick: () -> Unit,
    onCreateFlowCloseClick: () -> Unit = onBackClick,
    onInviteCodeChange: (String) -> Unit,
    onInviteCodeConfirmClick: () -> Unit,
    onCreateRoutineOptionClick: (Long) -> Unit,
    onCreateRoutineSelectAllClick: () -> Unit,
    onCategoryClick: (String) -> Unit,
    onCategoryAddClick: () -> Unit,
    onDismissCategorySheet: () -> Unit,
    onCategoryInputChange: (String) -> Unit,
    onCategoryConfirmClick: () -> Unit,
    onRoutineAddClick: () -> Unit,
    onRoutineSettingClick: (Long) -> Unit,
    onDismissRoutineSettingSheet: () -> Unit,
    onRoutineDraftNameChange: (String) -> Unit,
    onRoutineDraftStartTimeChange: (String) -> Unit,
    onRoutineDraftEndTimeChange: (String) -> Unit,
    onRepeatDayClick: (String) -> Unit,
    onRoutineSettingConfirmClick: () -> Unit,
    onRoutineDeleteClick: () -> Unit,
    onDismissDeleteRoutineDialog: () -> Unit,
    onConfirmDeleteRoutineClick: () -> Unit,
    onLeaveRoomClick: () -> Unit,
    onDismissLeaveRoomDialog: () -> Unit,
    onLeaveRoomConfirmClick: () -> Unit,
    onDismissDeleteRoomDialog: () -> Unit,
    onDeleteRoomConfirmClick: () -> Unit,
    onMemberKickClick: () -> Unit,
    onDismissKickMemberDialog: () -> Unit,
    onKickMemberConfirmClick: () -> Unit,
    onRoutineDraftCategoryClick: (String) -> Unit,
    onCategoryColorSelected: (CategoryColor) -> Unit,
    onCreateRoomDoneClick: () -> Unit,
    onTodoCheckedChange: (Long, Boolean) -> Unit,
    onCertificationTabClick: (Boolean) -> Unit,
    onCertificationMemberClick: (Long?) -> Unit,
    onCertificationSummaryClick: () -> Unit,
    onCertificationLikeClick: (Long, Boolean) -> Unit,
    onDismissNewCertificationDialog: () -> Unit,
    onNewCertificationDisappointClick: (Long) -> Unit = {},
    onNewCertificationLikeClick: (Long) -> Unit = {},
    onMemberClick: (Long) -> Unit,
    onDismissMemberDialog: () -> Unit,
    onChatClick: () -> Unit,
    onChatMessageChange: (String) -> Unit,
    onChatSendClick: () -> Unit,
    onChatEmojiSelected: (ChatEmoticonUiModel) -> Unit,
    onMessageEditClick: () -> Unit,
    onMessageDraftChange: (String) -> Unit,
    onDismissMessageEditSheet: () -> Unit,
    onMessageEditConfirmClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onInviteCodeCopyClick: () -> Unit,
    onGroupRoutineManageClick: () -> Unit,
    onRoomNameEditClick: () -> Unit,
    onLeaderSettingsClick: () -> Unit,
    onLeaderMemberClick: (Long) -> Unit,
    onLeaderTransferConfirmClick: () -> Unit,
    onRoomAlarmSettingsClick: () -> Unit,
    onRoomLockClick: () -> Unit,
    onRoomNameEditConfirmClick: () -> Unit,
    onDismissActionMessage: () -> Unit = {},
    onTabSelected: (AppBottomTab) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        when (uiState.screenMode) {
            GroupRoutineScreenMode.List -> GroupRoutineListScreen(
                uiState = uiState,
                onRoutineClick = onRoutineClick,
                onAddClick = onAddClick,
                onSearchInputChange = onSearchInputChange,
                onTabSelected = onTabSelected,
            )

            GroupRoutineScreenMode.Detail -> GroupRoutineDetailScreen(
                uiState = uiState,
                onBackClick = onBackClick,
                onTodoCheckedChange = onTodoCheckedChange,
                onCertificationTabClick = onCertificationTabClick,
                onCertificationSummaryClick = onCertificationSummaryClick,
                onMemberClick = onMemberClick,
                onDismissMemberDialog = onDismissMemberDialog,
                onMemberKickClick = onMemberKickClick,
                onChatClick = onChatClick,
                onMessageEditClick = onMessageEditClick,
                onSettingsClick = onSettingsClick,
                onInviteCodeClick = onInviteCodeCopyClick,
                onTabSelected = onTabSelected,
            )

            GroupRoutineScreenMode.CertificationCollection -> CertificationCollectionScreen(
                uiState = uiState,
                onBackClick = onBackClick,
                onCertificationMemberClick = onCertificationMemberClick,
                onCertificationLikeClick = onCertificationLikeClick,
                onTabSelected = onTabSelected,
            )

            GroupRoutineScreenMode.GroupChat -> uiState.selectedRoutine?.let { routine ->
                RoomDetailScreen(
                    room = GroupRoomUiModel(
                        id = routine.id.toString(),
                        name = routine.title,
                        memberCount = routine.memberCount,
                        routineCount = routine.routineCount,
                    ),
                    messages = uiState.chatMessages,
                    chatDraftText = uiState.chatDraftText,
                    emoticons = uiState.chatEmoticons,
                    onBackClick = onBackClick,
                    onChatMessageChange = onChatMessageChange,
                    onSendClick = onChatSendClick,
                    onEmojiSelected = onChatEmojiSelected,
                )
            }

            GroupRoutineScreenMode.GroupSettings -> GroupSettingsScreen(
                uiState = uiState,
                onBackClick = onBackClick,
                onGroupRoutineManageClick = onGroupRoutineManageClick,
                onRoomNameEditClick = onRoomNameEditClick,
                onLeaderSettingsClick = onLeaderSettingsClick,
                onRoomAlarmSettingsClick = onRoomAlarmSettingsClick,
                onRoomLockClick = onRoomLockClick,
                onInviteCodeCopyClick = onInviteCodeCopyClick,
                onLeaveRoomClick = onLeaveRoomClick,
            )

            GroupRoutineScreenMode.GroupRoutineManage -> GroupRoutineManageScreen(
                uiState = uiState,
                onBackClick = onBackClick,
                onOptionClick = onCreateRoutineOptionClick,
                onSelectAllClick = onCreateRoutineSelectAllClick,
                onCategoryClick = onCategoryClick,
                onCategoryAddClick = onCategoryAddClick,
                onRoutineAddClick = onRoutineAddClick,
                onRoutineSettingClick = onRoutineSettingClick,
                onDoneClick = onBackClick,
            )

            GroupRoutineScreenMode.RoomNameEdit -> RoomNameEditScreen(
                roomName = uiState.roomNameInput,
                onRoomNameChange = onRoomNameChange,
                onBackClick = onBackClick,
                onConfirmClick = onRoomNameEditConfirmClick,
            )

            GroupRoutineScreenMode.LeaderSettings -> LeaderSettingsScreen(
                uiState = uiState,
                onBackClick = onBackClick,
                onMemberClick = onLeaderMemberClick,
                onConfirmClick = onLeaderTransferConfirmClick,
            )

            GroupRoutineScreenMode.RoomAlarmSettings -> RoomAlarmSettingsScreen(
                onBackClick = onBackClick,
            )

            GroupRoutineScreenMode.CreateRoomName -> CreateRoomNameScreen(
                roomName = uiState.roomNameInput,
                onRoomNameChange = onRoomNameChange,
                onBackClick = onBackClick,
                onNextClick = onCreateRoomNextClick,
            )

            GroupRoutineScreenMode.JoinByCode -> JoinByCodeScreen(
                inviteCode = uiState.inviteCodeInput,
                onInviteCodeChange = onInviteCodeChange,
                onBackClick = onBackClick,
                onConfirmClick = onInviteCodeConfirmClick,
            )

            GroupRoutineScreenMode.CreateRoutineSelect -> CreateRoutineSelectScreen(
                uiState = uiState,
                selectedCount = uiState.selectedCreateRoutineCount,
                onBackClick = onBackClick,
                onCloseClick = onCreateFlowCloseClick,
                onOptionClick = onCreateRoutineOptionClick,
                onSelectAllClick = onCreateRoutineSelectAllClick,
                onCategoryClick = onCategoryClick,
                onCategoryAddClick = onCategoryAddClick,
                onRoutineAddClick = onRoutineAddClick,
                onRoutineSettingClick = onRoutineSettingClick,
                onDoneClick = onCreateRoomDoneClick,
            )
        }

        uiState.actionMessage?.let { message ->
            LaunchedEffect(message) {
                delay(4_000)
                onDismissActionMessage()
            }
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(bottom = 92.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xCC171719))
                    .padding(horizontal = 16.dp, vertical = 13.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = message,
                    color = Color.White,
                    style = LiroutiTheme.typography.body3,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = "×",
                    color = Color.White,
                    fontSize = 24.sp,
                    lineHeight = 24.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .sizeIn(minWidth = 48.dp, minHeight = 48.dp)
                        .semantics { contentDescription = "알림 닫기" }
                        .clickable(
                            onClickLabel = "알림 닫기",
                            onClick = onDismissActionMessage,
                        ),
                )
            }
        }
    }

    if (uiState.isActionSheetVisible) {
        GroupRoutineActionSheet(
            onDismissRequest = onDismissActionSheet,
            onCreateRoomClick = onCreateRoomClick,
            onJoinByCodeClick = onJoinByCodeClick,
        )
    }

    if (uiState.isRoutineSettingSheetVisible) {
        RoutineSettingSheet(
            isEditing = uiState.editingRoutineId != null,
            routineName = uiState.routineDraftName,
            startTime = uiState.routineDraftStartTime,
            endTime = uiState.routineDraftEndTime,
            repeatDays = uiState.routineDraftRepeatDays,
            categories = uiState.categories,
            selectedCategory = uiState.routineDraftCategory,
            onCategoryClick = onRoutineDraftCategoryClick,
            onDismissRequest = onDismissRoutineSettingSheet,
            onNameChange = onRoutineDraftNameChange,
            onStartTimeChange = onRoutineDraftStartTimeChange,
            onEndTimeChange = onRoutineDraftEndTimeChange,
            onRepeatDayClick = onRepeatDayClick,
            onDeleteClick = onRoutineDeleteClick,
            onConfirmClick = onRoutineSettingConfirmClick,
        )
    }

    if (uiState.isDeleteRoutineDialogVisible) {
        DeleteRoutineDialog(
            onDismissRequest = onDismissDeleteRoutineDialog,
            onConfirmClick = onConfirmDeleteRoutineClick,
        )
    }

    if (uiState.isLeaveRoomDialogVisible) {
        DangerConfirmDialog(
            title = "방 나가기",
            description = "방을 나가면 진행 중인 그룹 루틴이 사라져요.",
            confirmLabel = "나가기",
            onDismissRequest = onDismissLeaveRoomDialog,
            onConfirmClick = onLeaveRoomConfirmClick,
        )
    }

    if (uiState.isKickMemberDialogVisible) {
        DangerConfirmDialog(
            title = "내보내기",
            description = "이 멤버를 방에서 내보낼까요? 내보낸 뒤에는 초대코드로 다시 들어와야 해요.",
            confirmLabel = "내보내기",
            onDismissRequest = onDismissKickMemberDialog,
            onConfirmClick = onKickMemberConfirmClick,
        )
    }

    if (uiState.isDeleteRoomDialogVisible) {
        DangerConfirmDialog(
            title = "방 삭제하기",
            description = "방장은 방을 나갈 수 없어요. 대신 방을 삭제하면 방의 모든 기록이 함께 사라져요.",
            confirmLabel = "삭제",
            onDismissRequest = onDismissDeleteRoomDialog,
            onConfirmClick = onDeleteRoomConfirmClick,
        )
    }

    if (uiState.isNewCertificationDialogVisible) {
        NewCertificationDialog(
            certifications = uiState.newCertifications,
            onDismissRequest = onDismissNewCertificationDialog,
            onNegativeClick = onNewCertificationDisappointClick,
            onPositiveClick = onNewCertificationLikeClick,
        )
    }

    if (uiState.isCategorySheetVisible) {
        // 개인 루틴 쪽이랑 같은 공용 시트를 씀 — 색 피커가 이미 들어있음
        CategoryAddBottomSheet(
            name = uiState.categoryInput,
            onNameChange = onCategoryInputChange,
            selectedColor = uiState.categoryColorInput,
            onColorSelected = onCategoryColorSelected,
            onConfirm = onCategoryConfirmClick,
            onDismissRequest = onDismissCategorySheet,
        )
    }

    if (uiState.isMessageEditSheetVisible) {
        MessageEditSheet(
            message = uiState.messageDraft,
            onDismissRequest = onDismissMessageEditSheet,
            onMessageChange = onMessageDraftChange,
            onConfirmClick = onMessageEditConfirmClick,
        )
    }
}

@Composable
private fun GroupRoutineListScreen(
    uiState: GroupRoutineUiState,
    onRoutineClick: (Long) -> Unit,
    onAddClick: () -> Unit,
    onSearchInputChange: (String) -> Unit,
    onTabSelected: (AppBottomTab) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    // 헤더를 스크롤 영역과 겹치는 오버레이(Box + align(TopCenter))로 띄우고 contentPadding 매직넘버로
    // 헤더 높이를 흉내 내던 이전 구조는, 기기별 상태바 높이가 그 값과 어긋나면 스크롤 콘텐츠가
    // 헤더 위로 삐져나오는 문제가 있었다. 헤더/GNB를 Column의 고정 영역으로 완전히 분리한다.
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ScreenBackground),
    ) {
        GroupRoutineTopBar(
            title = "그룹 루틴",
            showAdd = true,
            onAddClick = onAddClick,
        )

        // 검색바도 헤더처럼 스크롤 영역 밖에 고정하고, 목록만 그 아래에서 스크롤되게 한다.
        GroupRoutineSearchField(
            value = uiState.searchInput,
            onValueChange = onSearchInputChange,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
        )

        Box(modifier = Modifier.weight(1f)) {
            if (uiState.isEmptyState) {
                GroupRoutineEmptyState(modifier = Modifier.align(Alignment.Center))
            } else if (uiState.visibleRoutines.isEmpty()) {
                GroupRoutineEmptyState(
                    message = "검색 결과가 없어요.",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 180.dp),
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    items(uiState.visibleRoutines) { routine ->
                        GroupRoutineCard(routine = routine, onClick = { onRoutineClick(routine.id) })
                    }
                }
            }
        }

        AppBottomNavBar(selectedTab = AppBottomTab.GroupRoutine, onTabSelected = onTabSelected)
    }
}

@Composable
private fun CreateRoomNameScreen(
    roomName: String,
    onRoomNameChange: (String) -> Unit,
    onBackClick: () -> Unit,
    onNextClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White),
    ) {
        GroupRoutineTopBar(
            title = "방 만들기",
            showBack = true,
            showClose = true,
            onBackClick = onBackClick,
            onCloseClick = onBackClick,
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 24.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                text = "어떤 방을 만들까요?",
                color = LabelDefault,
                style = LiroutiTheme.typography.heading2.copy(fontWeight = FontWeight.Bold),
            )
            Text(
                text = "친구들이 방 목록에서 이 이름으로 보게 돼요.",
                color = LabelInfo,
                style = LiroutiTheme.typography.body3,
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "방이름",
                color = LabelDefault,
                style = LiroutiTheme.typography.body3.copy(fontWeight = FontWeight.Bold),
            )
            BasicInputBox(
                value = roomName,
                onValueChange = onRoomNameChange,
                placeholder = "최대 20자",
                showClear = false,
            )
        }

        BottomFixedButton(
            text = "다음",
            enabled = true,
            onClick = onNextClick,
        )
    }
}

@Composable
private fun JoinByCodeScreen(
    inviteCode: String,
    onInviteCodeChange: (String) -> Unit,
    onBackClick: () -> Unit,
    onConfirmClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White),
    ) {
        GroupRoutineTopBar(
            title = "초대코드로 참여",
            showBack = true,
            showClose = true,
            onBackClick = onBackClick,
            onCloseClick = onBackClick,
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 24.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(
                text = "초대코드를 입력해주세요",
                color = LabelDefault,
                style = LiroutiTheme.typography.heading2.copy(fontWeight = FontWeight.Bold),
            )
            Text(
                text = "친구에게 받은 초대코드로 그룹방에 참여할 수 있어요.",
                color = LabelInfo,
                style = LiroutiTheme.typography.body3,
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "초대코드",
                color = LabelDefault,
                style = LiroutiTheme.typography.body3.copy(fontWeight = FontWeight.Bold),
            )
            BasicInputBox(
                value = inviteCode,
                onValueChange = onInviteCodeChange,
                placeholder = "초대코드 입력",
                showClear = inviteCode.isNotBlank(),
            )
        }

        BottomFixedButton(
            text = "참여하기",
            enabled = inviteCode.isNotBlank(),
            onClick = onConfirmClick,
        )
    }
}

@Composable
private fun CreateRoutineSelectScreen(
    uiState: GroupRoutineUiState,
    selectedCount: Int,
    onBackClick: () -> Unit,
    onCloseClick: () -> Unit,
    onOptionClick: (Long) -> Unit,
    onSelectAllClick: () -> Unit,
    onCategoryClick: (String) -> Unit,
    onCategoryAddClick: () -> Unit,
    onRoutineAddClick: () -> Unit,
    onRoutineSettingClick: (Long) -> Unit,
    onDoneClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White),
    ) {
        GroupRoutineTopBar(
            title = "루틴 추가",
            showBack = true,
            showClose = true,
            onBackClick = onBackClick,
            onCloseClick = onCloseClick,
        )

        Box(modifier = Modifier.weight(1f)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "함께할 루틴을 추가해보세요",
                            color = LabelDefault,
                            style = LiroutiTheme.typography.heading2.copy(fontWeight = FontWeight.Bold),
                        )
                        Text(
                            text = "루틴을 누르면 세부 설정을 변경할 수 있어요",
                            color = LabelInfo,
                            style = LiroutiTheme.typography.body3,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        CategoryChipRow(
                            categories = uiState.categories,
                            selectedCategory = uiState.selectedCategory,
                            onCategoryClick = onCategoryClick,
                            onCategoryAddClick = onCategoryAddClick,
                        )
                    }
                }
                item {
                    RoutineOptionsCard(
                        uiState = uiState,
                        maxHeight = 385.dp,
                        onSelectAllClick = onSelectAllClick,
                        onOptionClick = onOptionClick,
                        onRoutineSettingClick = onRoutineSettingClick,
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .navigationBarsPadding()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            DashedRoutineAddButton(onClick = onRoutineAddClick)
            Text(
                text = "총 ${selectedCount}개 선택됨",
                color = LabelSub,
                style = LiroutiTheme.typography.body3,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
            PrimaryButton(text = "방 만들기", enabled = true, onClick = onDoneClick)
        }
    }
}

@Composable
private fun CategoryChipRow(
    categories: List<String>,
    selectedCategory: String,
    onCategoryClick: (String) -> Unit,
    onCategoryAddClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        LazyRow(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(categories) { label ->
                val selected = label == selectedCategory
                Text(
                    text = if (selected) "✓ $label" else label,
                    color = if (selected) Color.White else LabelSub,
                    style = LiroutiTheme.typography.body3.copy(fontWeight = FontWeight.Medium),
                    modifier = Modifier
                        .clip(RoundedCornerShape(100.dp))
                        .background(if (selected) PrimaryNormal else Color.White)
                        .border(1.dp, if (selected) PrimaryNormal else BorderDefault, RoundedCornerShape(100.dp))
                        .clickable { onCategoryClick(label) }
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                )
            }
        }
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color.White)
                .border(1.dp, BorderDefault, CircleShape)
                .clickable(onClick = onCategoryAddClick),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "+",
                color = LabelSub,
                style = LiroutiTheme.typography.body2Long.copy(fontWeight = FontWeight.Medium),
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun SelectAllRoutineRow(
    checked: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(44.dp)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        SmallSquareCheckbox(checked = checked, onClick = onClick)
        Text(
            text = "전체 선택",
            color = LabelDefault,
            style = LiroutiTheme.typography.body3.copy(fontWeight = FontWeight.Bold),
        )
    }
}

@Composable
private fun RoutineOptionsCard(
    uiState: GroupRoutineUiState,
    maxHeight: Dp,
    onSelectAllClick: () -> Unit,
    onOptionClick: (Long) -> Unit,
    onRoutineSettingClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(max = maxHeight),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color.White)
                .border(1.dp, BorderAlternative, RoundedCornerShape(6.dp))
                .padding(horizontal = 16.dp, vertical = 2.dp),
        ) {
            SelectAllRoutineRow(
                checked = uiState.allVisibleRoutineOptionsSelected,
                onClick = onSelectAllClick,
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        HorizontalDivider(color = BorderDefault)
        Spacer(modifier = Modifier.height(12.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(maxHeight - 73.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
                    .padding(end = 10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                uiState.visibleRoutineOptions.forEach { option ->
                    CreateRoutineOptionRow(
                        option = option,
                        onCheckClick = { onOptionClick(option.id) },
                        onSettingClick = { onRoutineSettingClick(option.id) },
                    )
                }
            }
            RoutineScrollIndicator(
                scrollState = scrollState,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(top = 8.dp, end = 4.dp, bottom = 8.dp),
            )
        }
    }
}

@Composable
private fun RoutineScrollIndicator(
    scrollState: ScrollState,
    modifier: Modifier = Modifier,
) {
    if (scrollState.maxValue <= 0) return

    val density = LocalDensity.current

    BoxWithConstraints(
        modifier = modifier
            .width(4.dp)
            .fillMaxHeight(),
    ) {
        val trackHeightPx = with(density) { maxHeight.toPx() }
        val maxScrollPx = scrollState.maxValue.toFloat()
        if (!trackHeightPx.isFinite() || trackHeightPx <= 0f || maxScrollPx <= 0f) {
            return@BoxWithConstraints
        }

        // CodeRabbit 반영: minThumbHeightPx가 trackHeightPx보다 크면 coerceIn이 예외를 던지므로 먼저 가드
        val minThumbHeightPx = with(density) { 42.dp.toPx() }
        if (trackHeightPx < minThumbHeightPx) return@BoxWithConstraints

        val thumbHeightPx = (trackHeightPx * trackHeightPx / (trackHeightPx + maxScrollPx))
            .coerceIn(minThumbHeightPx, trackHeightPx)
        val scrollableTrackPx = trackHeightPx - thumbHeightPx
        if (!scrollableTrackPx.isFinite() || scrollableTrackPx <= 0f) return@BoxWithConstraints

        val thumbOffsetPx = (scrollState.value.toFloat() / maxScrollPx) *
            scrollableTrackPx
        if (!thumbOffsetPx.isFinite()) return@BoxWithConstraints

        Box(
            modifier = Modifier
                .offset(y = with(density) { thumbOffsetPx.roundToInt().toDp() })
                .width(4.dp)
                .height(with(density) { thumbHeightPx.toDp() })
                .clip(RoundedCornerShape(99.dp))
                .background(Color(0xFF9CA3AF)),
        )
    }
}

@Composable
private fun CreateRoutineOptionRow(
    option: CreateRoutineOptionUiModel,
    onCheckClick: () -> Unit,
    onSettingClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(Color.White)
            .border(1.dp, BorderAlternative, RoundedCornerShape(6.dp))
            .clickable(onClick = onSettingClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            SmallSquareCheckbox(
                checked = option.isSelected,
                onClick = onCheckClick,
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = option.title,
                    color = LabelDefault,
                    style = LiroutiTheme.typography.body3.copy(fontWeight = FontWeight.Medium),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "마감 ${option.deadline}",
                        color = LabelInfo,
                        style = LiroutiTheme.typography.caption.copy(fontSize = 11.sp, lineHeight = 14.sp),
                    )
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(10.dp)
                            .background(BorderStrong),
                    )
                    Text(
                        text = option.category,
                        color = LabelInfo,
                        style = LiroutiTheme.typography.caption.copy(fontSize = 11.sp, lineHeight = 14.sp),
                    )
                }
            }
            if (option.repeatLabel.isNotBlank() && option.repeatLabel != "없음") {
                Text(
                    text = option.repeatLabel,
                    color = SecondaryNormal,
                    style = LiroutiTheme.typography.caption.copy(fontSize = 11.sp, lineHeight = 14.sp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(SecondaryBackground)
                        .padding(horizontal = 6.dp, vertical = 3.dp),
                )
            }
        }
    }
}

@Composable
private fun SmallSquareCheckbox(
    checked: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
) {
    CustomCheckBox(
        state = if (checked) CheckBoxState.B else CheckBoxState.A,
        isCircle = false,
        onClick = onClick,
        modifier = modifier,
    )
}

@Composable
private fun DashedRoutineAddButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LiroutiDashedAddButton(
        text = "루틴 추가",
        onClick = onClick,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RoutineSettingSheet(
    isEditing: Boolean,
    routineName: String,
    startTime: String,
    endTime: String,
    repeatDays: Set<String>,
    categories: List<String>,
    selectedCategory: String,
    onCategoryClick: (String) -> Unit,
    onDismissRequest: () -> Unit,
    onNameChange: (String) -> Unit,
    onStartTimeChange: (String) -> Unit,
    onEndTimeChange: (String) -> Unit,
    onRepeatDayClick: (String) -> Unit,
    onDeleteClick: () -> Unit,
    onConfirmClick: () -> Unit,
) {
    var expandedSection by remember { mutableStateOf<RoutineSettingSection?>(null) }

    LiroutiBottomSheet(
        onDismissRequest = onDismissRequest,
        contentPadding = PaddingValues(start = 16.dp, top = 30.dp, end = 16.dp, bottom = 32.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "×",
                    color = LabelDefault,
                    fontSize = 28.sp,
                    modifier = Modifier.clickable(onClick = onDismissRequest),
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "삭제",
                    color = DangerBase,
                    style = LiroutiTheme.typography.body2Long,
                    modifier = Modifier.clickable(onClick = onDeleteClick),
                )
            }
            BasicInputBox(
                value = routineName,
                onValueChange = onNameChange,
                placeholder = "루틴 이름",
                showClear = routineName.isNotEmpty(),
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp)),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                RoutineSettingExpandableRow(
                    label = "카테고리",
                    value = selectedCategory.ifBlank { "선택 안 함" },
                    expanded = expandedSection == RoutineSettingSection.Category,
                    onClick = {
                        expandedSection = if (expandedSection == RoutineSettingSection.Category) null else RoutineSettingSection.Category
                    },
                )
                if (expandedSection == RoutineSettingSection.Category) {
                    RoutineSettingDivider()
                    RoutineCategoryRow(
                        categories = categories,
                        selectedCategory = selectedCategory,
                        onCategoryClick = onCategoryClick,
                    )
                }
                RoutineSettingExpandableRow(
                    label = "시작시간",
                    value = displayRoutineTime(startTime),
                    expanded = expandedSection == RoutineSettingSection.StartTime,
                    onClick = {
                        expandedSection = if (expandedSection == RoutineSettingSection.StartTime) null else RoutineSettingSection.StartTime
                    },
                )
                if (expandedSection == RoutineSettingSection.StartTime) {
                    RoutineSettingDivider()
                    RoutineTimePicker(
                        time = startTime,
                        onTimeChange = onStartTimeChange,
                    )
                }
                RoutineSettingExpandableRow(
                    label = "마감시간",
                    value = displayRoutineTime(endTime),
                    expanded = expandedSection == RoutineSettingSection.EndTime,
                    onClick = {
                        expandedSection = if (expandedSection == RoutineSettingSection.EndTime) null else RoutineSettingSection.EndTime
                    },
                )
                if (expandedSection == RoutineSettingSection.EndTime) {
                    RoutineSettingDivider()
                    RoutineTimePicker(
                        time = endTime,
                        onTimeChange = onEndTimeChange,
                    )
                }
                RoutineSettingExpandableRow(
                    label = "반복",
                    value = repeatDaysLabel(repeatDays),
                    expanded = expandedSection == RoutineSettingSection.Repeat,
                    onClick = {
                        expandedSection = if (expandedSection == RoutineSettingSection.Repeat) null else RoutineSettingSection.Repeat
                    },
                )
                if (expandedSection == RoutineSettingSection.Repeat) {
                    RoutineSettingDivider()
                    RepeatDayRow(selectedDays = repeatDays, onRepeatDayClick = onRepeatDayClick)
                }
            }
            PrimaryButton(text = "확인", enabled = true, onClick = onConfirmClick)
        }
    }
}

private enum class RoutineSettingSection {
    Category,
    StartTime,
    EndTime,
    Repeat,
}

@Composable
private fun BasicInputBox(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    showClear: Boolean,
    modifier: Modifier = Modifier,
    height: Dp = 56.dp,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        textStyle = LiroutiTheme.typography.body2Long.copy(color = LabelDefault),
        placeholder = {
            Text(text = placeholder, color = LabelInfo, style = LiroutiTheme.typography.body2Long)
        },
        trailingIcon = {
            if (showClear) {
                Text(
                    text = "×",
                    color = LabelInfo,
                    fontSize = 18.sp,
                    modifier = Modifier.clickable { onValueChange("") },
                )
            }
        },
        shape = RoundedCornerShape(6.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            disabledContainerColor = Color.White,
            focusedIndicatorColor = BorderDefault,
            unfocusedIndicatorColor = BorderDefault,
            cursorColor = PrimaryNormal,
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(height),
    )
}

@Composable
private fun RoutineSettingExpandableRow(
    label: String,
    value: String,
    expanded: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(FillBackground)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = label, color = LabelSub, style = LiroutiTheme.typography.body3)
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = value,
            color = LabelSub,
            style = LiroutiTheme.typography.body3.copy(fontWeight = FontWeight.Bold),
        )
        Text(
            text = if (expanded) "⌃" else "⌄",
            color = LabelSub,
            style = LiroutiTheme.typography.body3.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(start = 4.dp),
        )
    }
}

@Composable
private fun RoutineSettingDivider() {
    HorizontalDivider(
        color = Color(0xFFEDEEF0),
        modifier = Modifier.padding(horizontal = 12.dp),
    )
}

@Composable
private fun RoutineTimePicker(
    time: String,
    onTimeChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val hour = time.substringBefore(":").toIntOrNull()?.coerceIn(0, 23) ?: 8
    val minute = time.substringAfter(":", "00").toIntOrNull()?.coerceIn(0, 59) ?: 0
    val hour12 = displayHour12(hour)
    val isAm = hour < 12

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(FillBackground)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        WheelPickerColumn(
            items = listOf("오전", "오후"),
            selectedIndex = if (isAm) 0 else 1,
            onSelected = { index ->
                val newHour = if (index == 0) hour12.to24Hour(isAm = true) else hour12.to24Hour(isAm = false)
                onTimeChange(formatRoutineTime(newHour, minute))
            },
            modifier = Modifier.weight(1f),
        )
        WheelPickerColumn(
            items = (1..12).map { it.toString() },
            selectedIndex = hour12 - 1,
            onSelected = { index ->
                val newHour = (index + 1).to24Hour(isAm = isAm)
                onTimeChange(formatRoutineTime(newHour, minute))
            },
            modifier = Modifier.weight(1f),
        )
        WheelPickerColumn(
            items = (0..59).map { it.toString().padStart(2, '0') },
            selectedIndex = minute,
            onSelected = { index -> onTimeChange(formatRoutineTime(hour, index)) },
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun WheelPickerColumn(
    items: List<String>,
    selectedIndex: Int,
    onSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val rowHeight = 32.dp
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = selectedIndex.coerceIn(items.indices))
    val density = LocalDensity.current

    LaunchedEffect(selectedIndex, items.size) {
        val safeIndex = selectedIndex.coerceIn(items.indices)
        if (!listState.isScrollInProgress && listState.firstVisibleItemIndex != safeIndex) {
            listState.scrollToItem(safeIndex)
        }
    }

    LaunchedEffect(listState.isScrollInProgress) {
        if (!listState.isScrollInProgress && items.isNotEmpty()) {
            val rowHeightPx = with(density) { rowHeight.toPx() }
            val shouldMoveNext = listState.firstVisibleItemScrollOffset > rowHeightPx / 2f
            val targetIndex = (listState.firstVisibleItemIndex + if (shouldMoveNext) 1 else 0).coerceIn(items.indices)
            if (targetIndex != selectedIndex) {
                onSelected(targetIndex)
            }
            listState.animateScrollToItem(targetIndex)
        }
    }

    Box(
        modifier = modifier
            .height(rowHeight * 3)
            .clip(RoundedCornerShape(6.dp)),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(rowHeight)
                .clip(RoundedCornerShape(6.dp))
                .background(Color(0xFFF4F4F5)),
        )
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = rowHeight),
        ) {
            itemsIndexed(items) { index, item ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(rowHeight)
                        .clickable { onSelected(index) },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = item,
                        color = if (index == selectedIndex) LabelDefault else LabelInfo,
                        style = LiroutiTheme.typography.body3.copy(
                            fontWeight = if (index == selectedIndex) FontWeight.Bold else FontWeight.Medium,
                        ),
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

private fun Int.to24Hour(isAm: Boolean): Int {
    val hour = this.coerceIn(1, 12)
    return when {
        isAm && hour == 12 -> 0
        isAm -> hour
        hour == 12 -> 12
        else -> hour + 12
    }
}

@Composable
private fun TimePickerPreviewRow(
    hour: Int,
    minute: Int,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(28.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(if (selected) Color(0xFFF4F4F5) else FillBackground)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = if (hour < 12) "오전" else "오후",
            color = if (selected) LabelDefault else LabelSub,
            style = LiroutiTheme.typography.body3.copy(fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium),
            modifier = Modifier.weight(1f),
        )
        Text(
            text = displayHour12(hour).toString(),
            color = if (selected) LabelDefault else LabelSub,
            style = LiroutiTheme.typography.body3.copy(fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium),
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = minute.toString().padStart(2, '0'),
            color = if (selected) LabelDefault else LabelSub,
            style = LiroutiTheme.typography.body3.copy(fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium),
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun RoutineCategoryRow(
    categories: List<String>,
    selectedCategory: String,
    onCategoryClick: (String) -> Unit,
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        // "전체"는 목록 필터용이라 루틴에 붙일 수 없어서 뺌
        items(categories.filterNot { it == "전체" }) { category ->
            val selected = category == selectedCategory
            Text(
                text = category,
                color = if (selected) Color.White else LabelSub,
                style = LiroutiTheme.typography.body3.copy(fontWeight = FontWeight.Medium),
                modifier = Modifier
                    .clip(RoundedCornerShape(100.dp))
                    .background(if (selected) PrimaryNormal else Color.White)
                    .border(1.dp, if (selected) PrimaryNormal else BorderDefault, RoundedCornerShape(100.dp))
                    .clickable { onCategoryClick(category) }
                    .padding(horizontal = 16.dp, vertical = 9.dp),
            )
        }
    }
}

@Composable
private fun RepeatDayRow(
    selectedDays: Set<String>,
    onRepeatDayClick: (String) -> Unit,
) {
    val dayLabels = listOf("\uC77C", "\uC6D4", "\uD654", "\uC218", "\uBAA9", "\uAE08", "\uD1A0")
    val selectedIndexes = dayLabels.mapIndexedNotNull { index, day ->
        if (day in selectedDays) index else null
    }.toSet()

    LiroutiDaySelector(
        selectedDays = selectedIndexes,
        onDayClick = { index -> onRepeatDayClick(dayLabels[index]) },
        modifier = Modifier
            .fillMaxWidth()
            .background(FillBackground)
            .padding(horizontal = 24.dp, vertical = 12.dp),
    )
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
private fun displayRoutineTime(time: String): String {
    val hour = time.substringBefore(":").toIntOrNull()?.coerceIn(0, 23) ?: 8
    val minute = time.substringAfter(":", "00").toIntOrNull()?.coerceIn(0, 59) ?: 0
    return "${if (hour < 12) "오전" else "오후"} ${displayHour12(hour)}:${minute.toString().padStart(2, '0')}"
}

private fun displayHour12(hour: Int): Int {
    val display = hour % 12
    return if (display == 0) 12 else display
}

private fun formatRoutineTime(hour: Int, minute: Int): String {
    return "${hour.coerceIn(0, 23).toString().padStart(2, '0')}:${minute.coerceIn(0, 59).toString().padStart(2, '0')}"
}

@Composable
private fun DeleteRoutineDialog(
    onDismissRequest: () -> Unit,
    onConfirmClick: () -> Unit,
) {
    DangerConfirmDialog(
        title = "삭제하기",
        description = "작성 중이던 루틴이 삭제됩니다.",
        confirmLabel = "삭제",
        onDismissRequest = onDismissRequest,
        onConfirmClick = onConfirmClick,
    )
}

/** 되돌릴 수 없는 동작(루틴 삭제, 방 나가기/삭제)을 한 번 더 확인받는 다이얼로그 */
@Composable
private fun DangerConfirmDialog(
    title: String,
    description: String,
    confirmLabel: String,
    onDismissRequest: () -> Unit,
    onConfirmClick: () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 38.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color.White)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = title,
                    color = LabelDefault,
                    style = LiroutiTheme.typography.heading2.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.weight(1f),
                )
                Text(text = "×", color = LabelDefault, fontSize = 28.sp, modifier = Modifier.clickable(onClick = onDismissRequest))
            }
            Text(
                text = description,
                color = LabelSub,
                style = LiroutiTheme.typography.body2Long,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = onDismissRequest,
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FillBackground, contentColor = LabelDefault),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                ) {
                    Text(text = "취소")
                }
                Button(
                    onClick = onConfirmClick,
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE55454), contentColor = Color.White),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                ) {
                    Text(text = confirmLabel)
                }
            }
        }
    }
}

@Composable
private fun NewCertificationDialog(
    certifications: List<NewCertificationUiModel>,
    onDismissRequest: () -> Unit,
    onNegativeClick: (Long) -> Unit,
    onPositiveClick: (Long) -> Unit,
) {
    if (certifications.isEmpty()) return
    val pagerState = rememberPagerState(pageCount = { certifications.size })

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 36.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color.White)
                .padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "새 인증",
                    color = LabelDefault,
                    style = LiroutiTheme.typography.heading2.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = "×",
                    color = LabelDefault,
                    fontSize = 28.sp,
                    modifier = Modifier.clickable(onClick = onDismissRequest),
                )
            }
            HorizontalPager(state = pagerState) {
                Image(
                    painter = painterResource(id = R.drawable.img_group_routine_cert_water),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(146.dp)
                        .clip(RoundedCornerShape(4.dp)),
                )
            }
            Row(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                certifications.indices.forEach { index ->
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(if (index == pagerState.currentPage) LabelDefault else BorderDefault),
                    )
                }
            }
            val current = certifications[pagerState.currentPage]
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "${current.memberName} | ${current.routineName}",
                    color = LabelDefault,
                    style = LiroutiTheme.typography.body2Long.copy(fontWeight = FontWeight.Bold),
                )
                Text(
                    text = current.message,
                    color = LabelSub,
                    style = LiroutiTheme.typography.body3,
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                  Button(
                      onClick = { onNegativeClick(current.id) },
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FillBackground, contentColor = LabelDefault),
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp),
                ) {
                    Text(text = "아쉬워요", style = LiroutiTheme.typography.body3)
                }
                  Button(
                      onClick = { onPositiveClick(current.id) },
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryNormal, contentColor = Color.White),
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp),
                ) {
                    Text(text = "좋아요", style = LiroutiTheme.typography.body3)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MessageEditSheet(
    message: String,
    onDismissRequest: () -> Unit,
    onMessageChange: (String) -> Unit,
    onConfirmClick: () -> Unit,
) {
    LiroutiBottomSheet(
        onDismissRequest = onDismissRequest,
        contentPadding = PaddingValues(start = 20.dp, top = 30.dp, end = 20.dp, bottom = 28.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Spacer(modifier = Modifier.weight(1f))
                Text(text = "×", color = LabelDefault, fontSize = 28.sp, modifier = Modifier.clickable(onClick = onDismissRequest))
            }
            Text(
                text = "상태 메시지",
                color = LabelDefault,
                style = LiroutiTheme.typography.body2Long.copy(fontWeight = FontWeight.Bold),
            )
            BasicInputBox(
                value = message,
                onValueChange = onMessageChange,
                placeholder = "최대 8자",
                showClear = false,
            )
            PrimaryButton(text = "확인", enabled = message.isNotBlank(), onClick = onConfirmClick)
        }
    }
}

@Composable
private fun BottomFixedButton(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .navigationBarsPadding()
            .padding(16.dp),
    ) {
        PrimaryButton(text = text, enabled = enabled, onClick = onClick)
    }
}

@Composable
private fun PrimaryButton(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LiroutiPrimaryButton(
        text = text,
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
    )
}

@Composable
private fun GroupRoutineSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LiroutiSearchField(
        value = value,
        onValueChange = onValueChange,
        placeholder = "그룹방 검색",
        modifier = modifier.fillMaxWidth(),
    )
}

@Composable
private fun GroupRoutineEmptyState(
    modifier: Modifier = Modifier,
    message: String = "아직 만들어진 방이 없어요!",
) {
    Column(
        modifier = modifier.padding(bottom = 76.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(text = "!", color = LabelInfo, fontSize = 26.sp)
        Text(
            text = message,
            color = LabelInfo,
            style = LiroutiTheme.typography.body3,
        )
    }
}

@Composable
private fun GroupRoutineCard(
    routine: GroupRoutineUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(Color.White)
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            RoutineIconBox(size = 44)
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = routine.title,
                        color = LabelDefault,
                        style = LiroutiTheme.typography.body1.copy(fontWeight = FontWeight.Bold),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = routine.lastActiveLabel,
                        color = PrimaryNormal,
                        style = LiroutiTheme.typography.caption,
                        maxLines = 1,
                    )
                }
                Text(
                    text = "멤버 ${routine.memberCount}명  |  루틴 ${routine.routineCount}개",
                    color = Color.Black,
                    style = LiroutiTheme.typography.caption.copy(fontSize = 11.sp, lineHeight = 14.sp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            StatusBadge(label = routine.statusLabel, completed = routine.isCompleted)
        }

        HorizontalDivider(color = BorderDefault)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            AvatarStack()
            Text(
                text = "오늘 ${routine.todayCompletedCount}/${routine.todayTotalCount} 완료",
                color = LabelInfo,
                style = LiroutiTheme.typography.caption,
            )
        }
        RoutineStatsRow(routine = routine)
    }
}

@Composable
private fun StatusBadge(
    label: String,
    completed: Boolean,
    modifier: Modifier = Modifier,
) {
    Text(
        text = label,
        color = if (completed) CompleteText else SecondaryNormal,
        style = LiroutiTheme.typography.caption.copy(fontSize = 11.sp, lineHeight = 14.sp),
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(if (completed) CompleteBackground else ScreenBackground)
            .padding(horizontal = 7.dp, vertical = 3.dp),
    )
}

@Composable
private fun AvatarStack(modifier: Modifier = Modifier) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy((-8).dp)) {
        repeat(3) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE8EAEB))
                    .border(0.6.dp, BorderDefault, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_group_routine_member),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                )
            }
        }
    }
}

@Composable
private fun RoutineStatsRow(
    routine: GroupRoutineUiModel,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(66.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(FillBackground)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        StatItem(value = "${routine.streakDays}일", label = "연속 달성")
        VerticalStatDivider()
        StatItem(value = "${routine.monthlyAchievementRate}%", label = "이번 달성률")
        VerticalStatDivider()
        StatItem(value = "${routine.todayCertificationCount}건", label = "오늘 인증")
    }
}

@Composable
private fun StatItem(value: String, label: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.width(76.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = value,
            color = LabelSub,
            style = LiroutiTheme.typography.body1.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            color = LabelSub,
            style = LiroutiTheme.typography.body3,
            textAlign = TextAlign.Center,
            maxLines = 1,
        )
    }
}

@Composable
private fun VerticalStatDivider() {
    Box(
        modifier = Modifier
            .height(50.dp)
            .width(1.dp)
            .background(BorderDefault),
    )
}

@Composable
private fun GroupRoutineDetailScreen(
    uiState: GroupRoutineUiState,
    onBackClick: () -> Unit,
    onTodoCheckedChange: (Long, Boolean) -> Unit,
    onCertificationTabClick: (Boolean) -> Unit,
    onCertificationSummaryClick: () -> Unit,
    onMemberClick: (Long) -> Unit,
    onDismissMemberDialog: () -> Unit,
    onMemberKickClick: () -> Unit,
    onChatClick: () -> Unit,
    onMessageEditClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onInviteCodeClick: () -> Unit,
    onTabSelected: (AppBottomTab) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val routine = uiState.selectedRoutine ?: return
    var isRoutineSheetExpanded by remember(routine.id) { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ScreenBackground),
    ) {
        GroupRoutineTopBar(
            title = routine.title,
            showBack = true,
            showActions = true,
            chatBadgeCount = uiState.unreadChatCount,
            onBackClick = onBackClick,
            onChatClick = onChatClick,
            onSettingsClick = onSettingsClick,
        )

        Column(modifier = Modifier.weight(1f)) {
            if (!isRoutineSheetExpanded) {
                DetailMemberSection(
                    title = routine.title,
                    members = uiState.members,
                    onCertificationClick = onCertificationSummaryClick,
                    onMessageEditClick = onMessageEditClick,
                    onInviteCodeClick = onInviteCodeClick,
                    onMemberClick = onMemberClick,
                )
            }
            DetailRoutineTabSheet(
                title = "${routine.title}의 루틴",
                todos = uiState.todos,
                progressLabel = uiState.todoProgressLabel,
                onTodoCheckedChange = onTodoCheckedChange,
                expanded = isRoutineSheetExpanded,
                onExpandedChange = { isRoutineSheetExpanded = it },
                modifier = Modifier.weight(1f),
            )
        }

        AppBottomNavBar(selectedTab = AppBottomTab.GroupRoutine, onTabSelected = onTabSelected)
    }

    uiState.selectedMember?.let { member ->
        MemberProfileDialog(
            member = member,
            // 방장만 내보낼 수 있고, 자기 자신은 못 내보냄
            canKick = uiState.isConfirmedOwner && !member.isMe,
            onDismissRequest = onDismissMemberDialog,
            onPokeClick = onDismissMemberDialog,
            onKickClick = onMemberKickClick,
        )
    }
}

@Composable
private fun DetailMemberSection(
    title: String,
    members: List<GroupMemberUiModel>,
    onCertificationClick: () -> Unit,
    onMessageEditClick: () -> Unit,
    onInviteCodeClick: () -> Unit,
    onMemberClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val visibleMembers = members.take(6)
    val seatLayoutHeight = if (visibleMembers.size <= 3) 120.dp else 252.dp

    Column(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 360.dp)
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color.White, Color(0x33CFE4FF)),
                ),
            )
            .padding(start = 20.dp, top = 20.dp, end = 20.dp, bottom = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(28.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = title,
                color = LabelDefault,
                style = LiroutiTheme.typography.heading2.copy(fontWeight = FontWeight.Bold),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            CertificationCollectAction(onClick = onCertificationClick)
        }

        MemberSeatLayout(
            members = visibleMembers,
            onMemberClick = onMemberClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(seatLayoutHeight),
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextAction(label = "메시지 수정", onClick = onMessageEditClick)
            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .width(1.dp)
                    .height(12.dp)
                    .background(BorderDefault),
            )
            TextAction(label = "초대코드", onClick = onInviteCodeClick)
        }
    }
}

@Composable
private fun CertificationCollectAction(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 4.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_group_routine_flame),
            contentDescription = null,
            modifier = Modifier.size(18.dp),
        )
        Text(
            text = "인증 모아보기",
            color = LabelDefault,
            style = LiroutiTheme.typography.caption.copy(fontWeight = FontWeight.Bold),
            maxLines = 1,
        )
    }
}

@Composable
private fun DetailRoutineTabSheet(
    title: String,
    todos: List<GroupTodoUiModel>,
    progressLabel: String,
    onTodoCheckedChange: (Long, Boolean) -> Unit,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val dragThresholdPx = with(LocalDensity.current) { 24.dp.toPx() }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .clip(if (expanded) RoundedCornerShape(0.dp) else RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp)
                .pointerInput(expanded) {
                    var dragAmountSum = 0f
                    detectVerticalDragGestures(
                        onDragStart = { dragAmountSum = 0f },
                        onVerticalDrag = { _, dragAmount ->
                            dragAmountSum += dragAmount
                        },
                        onDragEnd = {
                            when {
                                dragAmountSum < -dragThresholdPx -> onExpandedChange(true)
                                dragAmountSum > dragThresholdPx -> onExpandedChange(false)
                            }
                        },
                    )
                },
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .width(44.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(100.dp))
                    .background(Color(0xFFDEDEDE)),
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp, end = 16.dp, bottom = if (expanded) 12.dp else 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = title,
                color = LabelDefault,
                style = LiroutiTheme.typography.body2Long.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.fillMaxWidth(),
            )
            DetailRoutineCategoryTabs()
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(todos, key = { it.id }) { todo ->
                    DetailRoutineTodoRow(
                        todo = todo,
                        onCheckedChange = { checked -> onTodoCheckedChange(todo.id, checked) },
                    )
                }
                item {
                    Text(
                        text = progressLabel,
                        color = LabelSub,
                        style = LiroutiTheme.typography.caption,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 2.dp),
                        textAlign = TextAlign.End,
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailRoutineCategoryTabs(
    modifier: Modifier = Modifier,
) {
    val categories = listOf("전체", "건강", "운동", "공부", "코딩")

    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(categories) { category ->
            val selected = category == "전체"
            Text(
                text = if (selected) "✓ $category" else category,
                color = if (selected) Color.White else LabelSub,
                style = LiroutiTheme.typography.body3.copy(fontWeight = FontWeight.Medium),
                modifier = Modifier
                    .height(38.dp)
                    .clip(RoundedCornerShape(100.dp))
                    .background(if (selected) PrimaryNormal else Color.White)
                    .border(1.dp, if (selected) PrimaryNormal else BorderDefault, RoundedCornerShape(100.dp))
                    .padding(horizontal = 16.dp, vertical = 9.dp),
            )
        }
    }
}

@Composable
private fun DetailRoutineTodoRow(
    todo: GroupTodoUiModel,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(Color.White)
            .border(1.dp, BorderAlternative, RoundedCornerShape(6.dp))
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        SmallSquareCheckbox(
            checked = todo.isDone,
            modifier = Modifier.size(16.dp),
            onClick = { onCheckedChange(!todo.isDone) },
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = todo.title,
                color = if (todo.isDone) LabelInfo else LabelDefault,
                style = LiroutiTheme.typography.body3.copy(fontWeight = FontWeight.Bold),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = "마감 ${todo.deadline}",
                    color = LabelInfo,
                    style = LiroutiTheme.typography.caption,
                    maxLines = 1,
                )
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(10.dp)
                        .background(BorderDefault),
                )
                Text(
                    text = todo.category,
                    color = LabelInfo,
                    style = LiroutiTheme.typography.caption,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        if (todo.isDone) {
            Text(
                text = "완료",
                color = LabelInfo,
                style = LiroutiTheme.typography.caption,
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFFEAEBEC))
                    .padding(horizontal = 7.dp, vertical = 3.dp),
            )
        } else {
            Image(
                painter = painterResource(id = DesignSystemR.drawable.camera),
                contentDescription = null,
                modifier = Modifier.size(18.dp),
            )
        }
    }
}

@Composable
private fun CertificationCollectionScreen(
    uiState: GroupRoutineUiState,
    onBackClick: () -> Unit,
    onCertificationMemberClick: (Long?) -> Unit,
    onCertificationLikeClick: (Long, Boolean) -> Unit,
    onTabSelected: (AppBottomTab) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ScreenBackground),
    ) {
        GroupRoutineTopBar(
            title = "인증 모아보기",
            showBack = true,
            onBackClick = onBackClick,
        )

        Box(modifier = Modifier.weight(1f)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                item {
                    CertificationFeedCard(
                        posts = uiState.visibleCertificationPosts,
                        members = uiState.members,
                        selectedMemberId = uiState.selectedCertificationMemberId,
                        onMemberClick = onCertificationMemberClick,
                        onLikeClick = onCertificationLikeClick,
                        modifier = Modifier.padding(horizontal = 16.dp),
                    )
                }
            }
        }

        AppBottomNavBar(selectedTab = AppBottomTab.GroupRoutine, onTabSelected = onTabSelected)
    }
}

@Composable
private fun GroupChatScreen(
    uiState: GroupRoutineUiState,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val routine = uiState.selectedRoutine ?: return
    val messages = listOf(
        Triple("", "오늘 너무 힘들다", false),
        Triple("", "루틴 못할 것 같은데", false),
        Triple("오후 1:00", "나는 포기", false),
        Triple("오후 1:02", "ㅃㄹ 하라고", true),
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ScreenBackground),
    ) {
        GroupRoutineTopBar(
            title = routine.title,
            showBack = true,
            onBackClick = onBackClick,
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items(messages) { (time, message, isMine) ->
                ChatMessageBubble(time = time, message = message, isMine = isMine)
            }
        }

        ChatInputBar(
            modifier = Modifier.padding(bottom = if (messages.isEmpty()) 20.dp else 0.dp),
        )
    }
}

@Composable
private fun ChatMessageBubble(
    time: String,
    message: String,
    isMine: Boolean,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = if (isMine) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom,
    ) {
        if (!isMine && time.isBlank()) {
            Image(
                painter = painterResource(id = R.drawable.img_group_routine_character),
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.White),
            )
            Spacer(modifier = Modifier.width(8.dp))
        } else if (!isMine) {
            Spacer(modifier = Modifier.width(48.dp))
        }
        if (isMine && time.isNotBlank()) {
            Text(
                text = time,
                color = LabelDefault,
                style = LiroutiTheme.typography.caption,
                modifier = Modifier.padding(end = 4.dp, bottom = 2.dp),
            )
        }
        Text(
            text = message,
            color = if (isMine) Color.White else LabelDefault,
            style = LiroutiTheme.typography.body3,
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(if (isMine) PrimaryNormal else Color.White)
                .padding(horizontal = 16.dp, vertical = 8.dp),
        )
        if (!isMine && time.isNotBlank()) {
            Text(
                text = time,
                color = LabelDefault,
                style = LiroutiTheme.typography.caption,
                modifier = Modifier.padding(start = 4.dp, bottom = 2.dp),
            )
        }
    }
}

@Composable
private fun ChatInputBar(
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .border(1.dp, BorderDefault)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(44.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color.White)
                .border(1.dp, BorderDefault, RoundedCornerShape(6.dp))
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart,
        ) {
            Text(text = "메세지 보내기", color = LabelInfo, style = LiroutiTheme.typography.body3)
        }
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(Color(0xFFD6E8FF)),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = "➤", color = PrimaryNormal, fontSize = 28.sp)
        }
    }
}

@Composable
private fun GroupSettingsScreen(
    uiState: GroupRoutineUiState,
    onBackClick: () -> Unit,
    onGroupRoutineManageClick: () -> Unit,
    onRoomNameEditClick: () -> Unit,
    onLeaderSettingsClick: () -> Unit,
    onRoomAlarmSettingsClick: () -> Unit,
    onRoomLockClick: () -> Unit,
    onInviteCodeCopyClick: () -> Unit,
    onLeaveRoomClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isLeader = uiState.isCurrentUserLeader
    val clipboardManager = LocalClipboardManager.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White),
    ) {
        GroupRoutineTopBar(
            title = "방 설정",
            showBack = true,
            showClose = true,
            onBackClick = onBackClick,
            onCloseClick = onBackClick,
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(vertical = 16.dp),
        ) {
            if (isLeader) {
                item {
                    SettingsSectionHeader(text = "그룹 루틴 설정")
                    SettingsNavigationRow(
                        iconRes = DesignSystemR.drawable.calendar__add,
                        label = "그룹 루틴 관리",
                        onClick = onGroupRoutineManageClick,
                    )
                    SettingsSectionDivider()
                }
                item {
                    SettingsSectionHeader(text = "방 정보 설정")
                    SettingsNavigationRow(
                        iconRes = DesignSystemR.drawable.edit,
                        label = "방 이름 변경",
                        onClick = onRoomNameEditClick,
                    )
                    SettingsNavigationRow(
                        iconRes = DesignSystemR.drawable.group,
                        label = "방장 설정",
                        onClick = onLeaderSettingsClick,
                    )
                    SettingsSectionDivider()
                }
            }
            item {
                SettingsSectionHeader(text = "방 알림 설정")
                SettingsNavigationRow(
                    iconRes = DesignSystemR.drawable.alarm,
                    label = "방 알림 설정",
                    onClick = onRoomAlarmSettingsClick,
                )
                SettingsSectionDivider()
            }
            item {
                SettingsSectionHeader(text = "초대 설정")
                if (isLeader) {
                    InviteLockRow(
                        locked = uiState.isRoomLocked,
                        onClick = onRoomLockClick,
                    )
                }
                InviteCodeRow(
                    code = uiState.groupInviteCode.orEmpty(),
                    onClick = {
                        val code = uiState.groupInviteCode
                        //코드가 실제로 존재할때만 클립보드에 복사
                        if(!code.isNullOrBlank()) {
                            clipboardManager.setText(AnnotatedString(code))
                            onInviteCodeCopyClick()
                        }
                    },
                )
            }
        }

        Button(
            onClick = onLeaveRoomClick,
            shape = RoundedCornerShape(6.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = DangerBase,
                contentColor = Color.White,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(start = 16.dp, end = 16.dp, bottom = 32.dp)
                .height(44.dp),
        ) {
            Text(text = "방 나가기", style = LiroutiTheme.typography.body3.copy(fontWeight = FontWeight.Medium))
        }
    }
}

@Composable
private fun GroupRoutineManageScreen(
    uiState: GroupRoutineUiState,
    onBackClick: () -> Unit,
    onOptionClick: (Long) -> Unit,
    onSelectAllClick: () -> Unit,
    onCategoryClick: (String) -> Unit,
    onCategoryAddClick: () -> Unit,
    onRoutineAddClick: () -> Unit,
    onRoutineSettingClick: (Long) -> Unit,
    onDoneClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ScreenBackground),
    ) {
        GroupRoutineTopBar(
            title = "루틴 관리",
            showBack = true,
            showClose = true,
            onBackClick = onBackClick,
            onCloseClick = onBackClick,
        )

        Box(modifier = Modifier.weight(1f)) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                item {
                    CategoryChipRow(
                        categories = uiState.categories,
                        selectedCategory = uiState.selectedCategory,
                        onCategoryClick = onCategoryClick,
                        onCategoryAddClick = onCategoryAddClick,
                    )
                }
                item {
                    RoutineOptionsCard(
                        uiState = uiState,
                        maxHeight = 455.dp,
                        onSelectAllClick = onSelectAllClick,
                        onOptionClick = onOptionClick,
                        onRoutineSettingClick = onRoutineSettingClick,
                    )
                }
                item {
                    DashedRoutineAddButton(onClick = onRoutineAddClick)
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .navigationBarsPadding()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = "총 ${uiState.selectedCreateRoutineCount}개 선택됨",
                color = LabelSub,
                style = LiroutiTheme.typography.body3,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
            PrimaryButton(text = "완료", enabled = true, onClick = onDoneClick)
        }
    }
}

@Composable
private fun RoomNameEditScreen(
    roomName: String,
    onRoomNameChange: (String) -> Unit,
    onBackClick: () -> Unit,
    onConfirmClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White),
    ) {
        GroupRoutineTopBar(
            title = "방 이름 변경",
            showBack = true,
            showClose = true,
            onBackClick = onBackClick,
            onCloseClick = onBackClick,
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "방이름",
                color = LabelDefault,
                style = LiroutiTheme.typography.body3.copy(fontWeight = FontWeight.Bold),
            )
            BasicInputBox(
                value = roomName,
                onValueChange = onRoomNameChange,
                placeholder = "최대 20자",
                showClear = roomName.isNotBlank(),
            )
        }

        TwoButtonBottomBar(
            leftText = "취소",
            rightText = "확인",
            onLeftClick = onBackClick,
            onRightClick = onConfirmClick,
        )
    }
}

@Composable
private fun LeaderSettingsScreen(
    uiState: GroupRoutineUiState,
    onBackClick: () -> Unit,
    onMemberClick: (Long) -> Unit,
    onConfirmClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White),
    ) {
        GroupRoutineTopBar(
            title = "방장 넘기기",
            showBack = true,
            showClose = true,
            onBackClick = onBackClick,
            onCloseClick = onBackClick,
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
        ) {
            items(uiState.members) { member ->
                LeaderMemberRow(
                    member = member,
                    selected = member.id == uiState.pendingLeaderMemberId,
                    onClick = { onMemberClick(member.id) },
                )
            }
        }

        TwoButtonBottomBar(
            leftText = "취소",
            rightText = "확인",
            onLeftClick = onBackClick,
            onRightClick = onConfirmClick,
        )
    }
}

@Composable
private fun LeaderMemberRow(
    member: GroupMemberUiModel,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(58.dp)
            .background(Color.White)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Image(
            painter = painterResource(id = R.drawable.img_group_routine_character),
            contentDescription = null,
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(ScreenBackground),
        )
        Text(
            text = "팀원 이름",
            color = LabelDefault,
            style = LiroutiTheme.typography.body3.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.weight(1f),
        )
        Box(
            modifier = Modifier
                .size(18.dp)
                .clip(CircleShape)
                .background(if (selected) PrimaryNormal else Color.White)
                .border(1.dp, if (selected) PrimaryNormal else BorderStrong, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            if (selected) {
                Text(text = "✓", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun RoomAlarmSettingsScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White),
    ) {
        GroupRoutineTopBar(
            title = "방 알림 설정",
            showBack = true,
            showClose = true,
            onBackClick = onBackClick,
            onCloseClick = onBackClick,
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            item { RoomAlarmToggleRow(title = "루틴 마감 알림", description = "마감 알림과 다시 알림을 받아요") }
            item { RoomAlarmToggleRow(title = "새 인증 알림", description = "친구가 인증을 올리면 알려드려요") }
            item { RoomAlarmToggleRow(title = "내 인증 반응 알림", description = "내 인증에 좋아요·아쉬워요가 오면 알려드려요") }
            item { RoomAlarmToggleRow(title = "콕콕 알림", description = "친구가 콕콕 찌르면 알려드려요") }
            item { RoomAlarmToggleRow(title = "새 채팅 알림", description = "새 채팅이 오면 알려드려요") }
        }
    }
}

@Composable
private fun SettingsSectionHeader(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        color = LabelSub,
        style = LiroutiTheme.typography.body3.copy(fontWeight = FontWeight.Bold),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(22.dp),
    )
}

@Composable
private fun SettingsSectionDivider(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 12.dp, bottom = 25.dp)
            .height(8.dp)
            .background(Color(0xFFF4F4F5)),
    )
}

@Composable
private fun SettingsNavigationRow(
    @DrawableRes iconRes: Int,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp)
            .background(Color.White)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            modifier = Modifier.size(24.dp),
        )
        Text(
            text = label,
            color = LabelDefault,
            style = LiroutiTheme.typography.body3.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.weight(1f),
        )
        Text(text = ">", color = LabelDefault, fontSize = 22.sp)
    }
}

@Composable
private fun InviteLockRow(
    locked: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(54.dp)
            .background(Color.White)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Image(
            painter = painterResource(id = DesignSystemR.drawable.locked),
            contentDescription = null,
            modifier = Modifier.size(24.dp),
        )
        Text(
            text = "방 잠금",
            color = LabelDefault,
            style = LiroutiTheme.typography.body3.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.weight(1f),
        )
        StaticSwitch(
            checked = locked,
            onCheckedChange = { onClick() },
        )
    }
}

@Composable
private fun InviteCodeRow(
    code: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(42.dp)
            .background(Color.White)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Image(
            painter = painterResource(id = DesignSystemR.drawable.mail),
            contentDescription = null,
            modifier = Modifier.size(24.dp),
        )
        Text(
            text = "초대코드",
            color = LabelDefault,
            style = LiroutiTheme.typography.body3.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.weight(1f),
        )
        Text(
            text = code.ifBlank { "발급 중..." },
            color = LabelSub,
            style = LiroutiTheme.typography.caption.copy(fontWeight = FontWeight.Medium),
        )
    }
}

@Composable
private fun RoomAlarmToggleRow(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = title,
                color = LabelDefault,
                style = LiroutiTheme.typography.body3.copy(fontWeight = FontWeight.Bold),
            )
            Text(
                text = description,
                color = LabelInfo,
                style = LiroutiTheme.typography.caption,
            )
        }
        StaticSwitch(checked = true)
    }
}

@Composable
private fun StaticSwitch(
    checked: Boolean,
    modifier: Modifier = Modifier,
    onCheckedChange: (Boolean) -> Unit = {},
) {
    LiroutiSwitch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier
            .size(width = 44.dp, height = 26.dp),
    )
}

@Composable
private fun TwoButtonBottomBar(
    leftText: String,
    rightText: String,
    onLeftClick: () -> Unit,
    onRightClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .navigationBarsPadding()
            .padding(start = 16.dp, end = 16.dp, bottom = 32.dp, top = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Button(
            onClick = onLeftClick,
            shape = RoundedCornerShape(6.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFF4F4F5),
                contentColor = LabelDefault,
            ),
            modifier = Modifier
                .weight(1f)
                .height(44.dp),
        ) {
            Text(text = leftText, style = LiroutiTheme.typography.body3.copy(fontWeight = FontWeight.Medium))
        }
        Button(
            onClick = onRightClick,
            shape = RoundedCornerShape(6.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF6688F4),
                contentColor = Color.White,
            ),
            modifier = Modifier
                .weight(1f)
                .height(44.dp),
        ) {
            Text(text = rightText, style = LiroutiTheme.typography.body3.copy(fontWeight = FontWeight.Medium))
        }
    }
}

@Composable
private fun CertificationSummaryCard(
    streakLabel: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(Color.White)
            .clickable(onClick = onClick)
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFF3F4F5)),
            contentAlignment = Alignment.Center,
          ) {
              Image(
                  painter = painterResource(id = R.drawable.ic_group_routine_flame),
                  contentDescription = null,
                  modifier = Modifier.size(24.dp),
              )
          }
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(1.dp)) {
            Text(
                text = "인증 모아보기",
                color = LabelDefault,
                style = LiroutiTheme.typography.body2Long.copy(fontWeight = FontWeight.Bold),
            )
            Text(text = streakLabel, color = LabelInfo, style = LiroutiTheme.typography.caption)
        }
        Text(text = ">", color = LabelDefault, fontSize = 20.sp)
    }
}

@Composable
private fun GroupMemberCard(
    title: String,
    members: List<GroupMemberUiModel>,
    onMessageEditClick: () -> Unit,
    onInviteCodeClick: () -> Unit,
    onMemberClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val visibleMembers = members.take(6)
    val seatLayoutHeight = if (visibleMembers.size <= 3) 120.dp else 252.dp

    Column(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 360.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(Color.White)
            .padding(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.Top),
        horizontalAlignment = Alignment.Start,
    ) {
        Text(
            text = title,
            color = LabelDefault,
            style = LiroutiTheme.typography.heading2.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.fillMaxWidth(),
        )
        MemberSeatLayout(
            members = visibleMembers,
            onMemberClick = onMemberClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(seatLayoutHeight),
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextAction(label = "메시지 수정", onClick = onMessageEditClick)
            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .width(1.dp)
                    .height(12.dp)
                    .background(BorderDefault),
            )
            TextAction(label = "초대코드", onClick = onInviteCodeClick)
        }
    }
}

@Composable
private fun MemberSeatLayout(
    members: List<GroupMemberUiModel>,
    onMemberClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val rows = when (members.size) {
        0 -> emptyList()
        1, 2, 3 -> listOf(members)
        4 -> members.chunked(2)
        5 -> listOf(members.take(3), members.drop(3))
        else -> members.chunked(3)
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        rows.forEach { rowMembers ->
            val itemWidth = when (rowMembers.size) {
                1 -> 104.dp
                2 -> 124.dp
                else -> 104.dp
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
            ) {
                rowMembers.forEach { member ->
                    MemberSeat(
                        member = member,
                        onClick = { onMemberClick(member.id) },
                        modifier = Modifier.width(itemWidth),
                    )
                }
            }
        }
    }
}

@Composable
private fun MemberSeat(
    member: GroupMemberUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.Top),
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .widthIn(min = 57.dp)
                    .height(30.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF878A93))
                    .padding(horizontal = 8.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = member.message.take(8),
                    color = Color.White,
                    fontSize = 11.sp,
                    maxLines = 1,
                )
            }
            Box(
                modifier = Modifier
                    .requiredSize(48.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center,
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_group_routine_character),
                    contentDescription = null,
                    modifier = Modifier.requiredSize(48.dp),
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            if (member.isMe) StatusBadge(label = "나", completed = false)
            Text(
                text = member.name,
                color = LabelDefault,
                style = LiroutiTheme.typography.body3.copy(fontWeight = FontWeight.Medium),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f, fill = false)
                    .padding(start = if (member.isMe) 4.dp else 0.dp),
            )
            Text(
                text = "🔥${member.streak}",
                color = DangerBase,
                fontSize = 10.sp,
                maxLines = 1,
                softWrap = false,
                modifier = Modifier.padding(start = 4.dp),
            )
        }
    }
}

@Composable
private fun MemberProfileDialog(
    member: GroupMemberUiModel,
    canKick: Boolean,
    onDismissRequest: () -> Unit,
    onPokeClick: () -> Unit,
    onKickClick: () -> Unit,
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Surface(
            shape = RoundedCornerShape(6.dp),
            color = Color.White,
            modifier = Modifier.width(320.dp),
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    Text(
                        text = "×",
                        color = LabelDefault,
                        fontSize = 24.sp,
                        modifier = Modifier.clickable(onClick = onDismissRequest),
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(ScreenBackground),
                        contentAlignment = Alignment.Center,
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_group_routine_character),
                            contentDescription = null,
                            modifier = Modifier.size(60.dp),
                        )
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(5.dp),
                    ) {
                        Text(
                            text = member.name,
                            color = LabelDefault,
                            style = LiroutiTheme.typography.body2Long.copy(fontWeight = FontWeight.Bold),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "얼리버드",
                                color = Color(0xFFD26D00),
                                style = LiroutiTheme.typography.caption,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFFFFDDB8))
                                    .padding(horizontal = 6.dp, vertical = 3.dp),
                            )
                            Text(text = "🔥${member.streak}", color = DangerBase, fontSize = 10.sp)
                        }
                    }
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(FillBackground)
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    MemberProfileInfoRow(label = "오늘 루틴 진행", value = "${member.completedCount}/${member.totalCount} 완료")
                    MemberProfileInfoRow(label = "연속 달성", value = "${member.streak}일 째")
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        ReactionBadge(text = "좋아요 ${member.totalLikeCount}")
                        ReactionBadge(text = "아쉬워요 3")
                        ReactionBadge(text = "쿡쿡 7")
                    }
                }
                PrimaryButton(text = "쿡쿡 찔러보기", enabled = true, onClick = onPokeClick)
                // 디자인이 아직 없어서 임시로 붙여둔 내보내기 액션 — 시안 나오면 교체 필요함
                if (canKick) {
                    Text(
                        text = "내보내기",
                        color = DangerBase,
                        style = LiroutiTheme.typography.body3,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 48.dp)
                            .clickable(onClick = onKickClick)
                            .padding(vertical = 14.dp),
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

@Composable
private fun MemberProfileInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            color = LabelSub,
            style = LiroutiTheme.typography.caption.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.width(93.dp),
        )
        Text(
            text = value,
            color = LabelDefault,
            style = LiroutiTheme.typography.body3,
        )
    }
}

@Composable
private fun ReactionBadge(text: String) {
    Text(
        text = text,
        color = LabelInfo,
        style = LiroutiTheme.typography.caption,
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(Color(0xFFEAEBEC))
            .padding(horizontal = 6.dp, vertical = 3.dp),
    )
}

@Composable
private fun GroupTodoCard(
    todos: List<GroupTodoUiModel>,
    progressLabel: String,
    onTodoCheckedChange: (Long, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(Color.White)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = "그룹 루틴",
            color = LabelDefault,
            style = LiroutiTheme.typography.body2Long.copy(fontWeight = FontWeight.Bold),
        )
        todos.forEach { todo ->
            TodoRow(todo = todo, onCheckedChange = { checked -> onTodoCheckedChange(todo.id, checked) })
        }
        HorizontalDivider(color = BorderDefault)
        Text(
            text = progressLabel,
            color = LabelSub,
            style = LiroutiTheme.typography.caption,
            modifier = Modifier.align(Alignment.End),
        )
    }
}

@Composable
private fun TodoRow(
    todo: GroupTodoUiModel,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        SmallSquareCheckbox(
            checked = todo.isDone,
            modifier = Modifier.size(16.dp),
            onClick = { onCheckedChange(!todo.isDone) },
        )
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = todo.title,
                color = if (todo.isDone) LabelInfo else LabelDefault,
                style = LiroutiTheme.typography.body2Long.copy(fontWeight = FontWeight.Medium),
                maxLines = 1,
            )
              if (!todo.isDone) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(
                        text = "마감 ${todo.deadline}",
                        color = LabelInfo,
                        style = LiroutiTheme.typography.caption,
                        maxLines = 1,
                    )
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(10.dp)
                            .background(BorderDefault),
                    )
                    Text(
                        text = todo.category,
                        color = LabelInfo,
                        style = LiroutiTheme.typography.caption,
                        maxLines = 1,
                    )
                }
            }
        }
        if (todo.isDone) {
            Text(
                text = "완료",
                color = LabelInfo,
                style = LiroutiTheme.typography.caption,
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFFEAEBEC))
                    .padding(horizontal = 7.dp, vertical = 3.dp),
            )
        } else {
            Text(text = "▣", color = LabelInfo, fontSize = 18.sp)
        }
    }
}

@Composable
private fun CertificationFeedCard(
    posts: List<CertificationPostUiModel>,
    members: List<GroupMemberUiModel>,
    selectedMemberId: Long?,
    onMemberClick: (Long?) -> Unit,
    onLikeClick: (Long, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            item {
                CertificationMemberChip(
                    text = "전체",
                    selected = selectedMemberId == null,
                    onClick = { onMemberClick(null) },
                )
            }
            items(members, key = { member -> member.id }) { member ->
                CertificationMemberChip(
                    text = member.name,
                    selected = selectedMemberId == member.id,
                    onClick = { onMemberClick(member.id) },
                )
            }
        }
        posts.forEach { post ->
            CertificationPostItem(
                post = post,
                onLikeClick = { onLikeClick(post.id, post.isLiked) },
            )
        }
    }
}

@Composable
private fun CertificationMemberChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Text(
        text = if (selected) "✓ $text" else text,
        color = if (selected) Color.White else LabelDefault,
        style = LiroutiTheme.typography.body3.copy(fontWeight = FontWeight.Medium),
        modifier = Modifier
            .height(32.dp)
            .clip(RoundedCornerShape(40.dp))
            .background(if (selected) PrimaryNormal else Color.White)
            .border(
                width = 1.dp,
                color = if (selected) PrimaryNormal else BorderDefault,
                shape = RoundedCornerShape(40.dp),
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 7.dp),
    )
}

@Composable
private fun FeedTab(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Text(
        text = text,
        color = if (selected) Color.White else LabelDefault,
        style = LiroutiTheme.typography.body3.copy(fontWeight = FontWeight.Medium),
        modifier = Modifier
            .clip(RoundedCornerShape(40.dp))
            .background(if (selected) PrimaryNormal else ScreenBackground)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 7.dp),
    )
}

@Composable
private fun CertificationPostItem(
    post: CertificationPostUiModel,
    onLikeClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.img_group_routine_character),
                contentDescription = null,
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = post.userName,
                    color = LabelDefault,
                    style = LiroutiTheme.typography.body2Long.copy(fontWeight = FontWeight.Bold),
                )
                Text(
                    text = post.timeAgo,
                    color = LabelInfo,
                    style = LiroutiTheme.typography.caption,
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(text = "...", color = LabelInfo, fontSize = 18.sp)
        }
        Text(
            text = post.body,
            color = LabelDefault,
            style = LiroutiTheme.typography.body2Long,
        )
        Image(
            painter = painterResource(id = R.drawable.img_group_routine_cert_water),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(144.dp)
                .clip(RoundedCornerShape(6.dp)),
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .clickable(onClick = onLikeClick)
                    .padding(end = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(text = if (post.isLiked) "♥" else "♡", color = LabelDefault, fontSize = 15.sp)
                Text(
                    text = "좋아요 ${post.likeCount}",
                    color = LabelDefault,
                    style = LiroutiTheme.typography.caption.copy(fontWeight = FontWeight.Medium),
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = if (post.isMine) "내 인증" else "멤버 인증",
                color = LabelInfo,
                style = LiroutiTheme.typography.caption,
            )
        }
    }
}

@Composable
private fun RoutineIconBox(
    size: Int,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(size.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(ScreenBackground),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(id = R.drawable.img_group_routine_character),
            contentDescription = null,
            modifier = Modifier.size((size - 8).dp),
        )
    }
}

@Composable
private fun TextAction(
    label: String,
    onClick: () -> Unit = {},
) {
    Text(
        text = label,
        color = LabelSub,
        style = LiroutiTheme.typography.body2Long,
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .clickable(onClick = onClick)
            .padding(4.dp),
    )
}

@Composable
private fun GroupRoutineTopBar(
    title: String,
    modifier: Modifier = Modifier,
    showBack: Boolean = false,
    showAdd: Boolean = false,
    showClose: Boolean = false,
    showActions: Boolean = false,
    chatBadgeCount: Int = 0,
    onBackClick: () -> Unit = {},
    onAddClick: () -> Unit = {},
    onCloseClick: () -> Unit = {},
    onChatClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
) {
    // Surface(color=White)를 쓰면 내부적으로 .background()가 이 modifier 체인 뒤에 붙어서
    // statusBarsPadding()으로 생긴 상태바 여백 영역엔 흰색이 칠해지지 않았다(상태바만 배경색이 어긋나 보이던 원인).
    // 다른 화면들과 동일하게 background()를 statusBarsPadding()보다 먼저 적용해 상태바 아래까지 흰 배경이 이어지게 한다.
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .statusBarsPadding(),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .padding(horizontal = 16.dp),
        ) {
            if (showBack) {
                Text(
                    text = "<",
                    color = LabelDefault,
                    fontSize = 24.sp,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .clickable(onClick = onBackClick),
                )
            }
            Text(
                text = title,
                color = LabelDefault,
                style = LiroutiTheme.typography.heading2.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.align(Alignment.Center),
            )
            if (showAdd) {
                Image(
                    painter = painterResource(id = DesignSystemR.drawable.add__alt),
                    contentDescription = "방 만들기",
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .size(24.dp)
                        .clickable(onClick = onAddClick),
                )
            }
            if (showClose) {
                Text(
                    text = "×",
                    color = LabelDefault,
                    fontSize = 22.sp,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .clickable(onClick = onCloseClick),
                )
            }
              if (showActions) {
                  Row(
                      modifier = Modifier.align(Alignment.CenterEnd),
                      horizontalArrangement = Arrangement.spacedBy(14.dp),
                      verticalAlignment = Alignment.CenterVertically,
                  ) {
                      TopBarActionButton(
                          iconRes = DesignSystemR.drawable.chat,
                          badgeCount = chatBadgeCount,
                          onClick = onChatClick,
                      )
                      TopBarActionButton(
                          iconRes = DesignSystemR.drawable.settings,
                          badgeCount = 0,
                          onClick = onSettingsClick,
                      )
                  }
              }
        }
    }
}

@Composable
private fun TopBarActionButton(
    @DrawableRes iconRes: Int,
    badgeCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(24.dp)
            .clickable(onClick = onClick),
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.Center)
                .size(20.dp),
        )
        if (badgeCount > 0) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 8.dp, y = (-4).dp)
                    .height(16.dp)
                    .widthIn(min = 16.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(DangerBase)
                    .padding(horizontal = if (badgeCount >= 10) 4.dp else 0.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = badgeCount.toString(),
                    color = Color.White,
                    fontSize = 10.sp,
                    lineHeight = 10.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GroupRoutineActionSheetContent(
    onDismissRequest: () -> Unit,
    onCreateRoomClick: () -> Unit,
    onJoinByCodeClick: () -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        containerColor = LiroutiTheme.colors.backgroundDefault,
        contentWindowInsets = { WindowInsets(0, 0, 0, 0) },
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .width(44.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFFDEDEDE)),
            )
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(start = 24.dp, end = 24.dp, top = 14.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            GroupRoutineActionSheetItem(
                label = "방 만들기",
                description = "친구들과 함께할 방을 새로 만들어요",
                onClick = {
                    onCreateRoomClick()
                    onDismissRequest()
                },
            )
            GroupRoutineActionSheetItem(
                label = "초대코드로 참여",
                description = "받은 코드로 방에 바로 들어가요",
                onClick = {
                    onJoinByCodeClick()
                    onDismissRequest()
                },
            )
        }
    }
}

@Composable
private fun GroupRoutineActionSheetItem(
    label: String,
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(LiroutiTheme.colors.backgroundFill)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = LiroutiTheme.typography.body2LongSemiBold.copy(fontWeight = FontWeight.Bold),
                color = LiroutiTheme.colors.labelDefault,
            )
            Text(
                text = description,
                style = LiroutiTheme.typography.body3Regular.copy(lineHeight = 16.sp),
                color = LiroutiTheme.colors.labelInfo,
            )
        }
        Image(
            painter = painterResource(id = DesignSystemR.drawable.chevron__right),
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            colorFilter = ColorFilter.tint(LiroutiTheme.colors.labelDefault),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GroupRoutineActionSheet(
    onDismissRequest: () -> Unit,
    onCreateRoomClick: () -> Unit,
    onJoinByCodeClick: () -> Unit,
) {
    GroupRoutineActionSheetContent(
        onDismissRequest = onDismissRequest,
        onCreateRoomClick = onCreateRoomClick,
        onJoinByCodeClick = onJoinByCodeClick,
    )
}


@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun GroupRoutineListPreview() {
    LiroutiFrontendTheme {
        GroupRoutineScreen(
            uiState = GroupRoutineUiState(),
            onRoutineClick = {},
            onBackClick = {},
            onAddClick = {},
            onSearchInputChange = {},
            onDismissActionSheet = {},
            onCreateRoomClick = {},
            onJoinByCodeClick = {},
            onRoomNameChange = {},
            onCreateRoomNextClick = {},
            onInviteCodeChange = {},
            onInviteCodeConfirmClick = {},
            onCreateRoutineOptionClick = {},
            onCreateRoutineSelectAllClick = {},
            onCategoryClick = {},
            onCategoryAddClick = {},
            onDismissCategorySheet = {},
            onCategoryInputChange = {},
            onCategoryConfirmClick = {},
            onRoutineAddClick = {},
            onRoutineSettingClick = {},
            onDismissRoutineSettingSheet = {},
            onRoutineDraftNameChange = {},
            onRoutineDraftStartTimeChange = {},
            onRoutineDraftEndTimeChange = {},
            onRepeatDayClick = {},
            onRoutineSettingConfirmClick = {},
            onRoutineDeleteClick = {},
            onDismissDeleteRoutineDialog = {},
            onConfirmDeleteRoutineClick = {},
            onLeaveRoomClick = {},
            onDismissLeaveRoomDialog = {},
            onLeaveRoomConfirmClick = {},
            onDismissDeleteRoomDialog = {},
            onDeleteRoomConfirmClick = {},
            onMemberKickClick = {},
            onDismissKickMemberDialog = {},
            onKickMemberConfirmClick = {},
            onRoutineDraftCategoryClick = {},
            onCategoryColorSelected = {},
              onCreateRoomDoneClick = {},
            onTodoCheckedChange = { _, _ -> },
            onCertificationTabClick = {},
            onCertificationMemberClick = {},
            onCertificationSummaryClick = {},
            onCertificationLikeClick = { _, _ -> },
            onDismissNewCertificationDialog = {},
            onMemberClick = {},
            onDismissMemberDialog = {},
            onChatClick = {},
            onChatMessageChange = {},
            onChatSendClick = {},
            onChatEmojiSelected = {},
            onMessageEditClick = {},
            onMessageDraftChange = {},
            onDismissMessageEditSheet = {},
            onMessageEditConfirmClick = {},
            onSettingsClick = {},
            onInviteCodeCopyClick = {},
            onGroupRoutineManageClick = {},
            onRoomNameEditClick = {},
            onLeaderSettingsClick = {},
            onLeaderMemberClick = {},
            onLeaderTransferConfirmClick = {},
            onRoomAlarmSettingsClick = {},
            onRoomLockClick = {},
            onRoomNameEditConfirmClick = {},
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
private fun CreateRoomNamePreview() {
    LiroutiFrontendTheme {
        GroupRoutineScreen(
            uiState = GroupRoutineUiState(screenMode = GroupRoutineScreenMode.CreateRoomName, roomNameInput = "코딩"),
            onRoutineClick = {},
            onBackClick = {},
            onAddClick = {},
            onSearchInputChange = {},
            onDismissActionSheet = {},
            onCreateRoomClick = {},
            onJoinByCodeClick = {},
            onRoomNameChange = {},
            onCreateRoomNextClick = {},
            onInviteCodeChange = {},
            onInviteCodeConfirmClick = {},
            onCreateRoutineOptionClick = {},
            onCreateRoutineSelectAllClick = {},
            onCategoryClick = {},
            onCategoryAddClick = {},
            onDismissCategorySheet = {},
            onCategoryInputChange = {},
            onCategoryConfirmClick = {},
            onRoutineAddClick = {},
            onRoutineSettingClick = {},
            onDismissRoutineSettingSheet = {},
            onRoutineDraftNameChange = {},
            onRoutineDraftStartTimeChange = {},
            onRoutineDraftEndTimeChange = {},
            onRepeatDayClick = {},
            onRoutineSettingConfirmClick = {},
            onRoutineDeleteClick = {},
            onDismissDeleteRoutineDialog = {},
            onConfirmDeleteRoutineClick = {},
            onLeaveRoomClick = {},
            onDismissLeaveRoomDialog = {},
            onLeaveRoomConfirmClick = {},
            onDismissDeleteRoomDialog = {},
            onDeleteRoomConfirmClick = {},
            onMemberKickClick = {},
            onDismissKickMemberDialog = {},
            onKickMemberConfirmClick = {},
            onRoutineDraftCategoryClick = {},
            onCategoryColorSelected = {},
              onCreateRoomDoneClick = {},
            onTodoCheckedChange = { _, _ -> },
            onCertificationTabClick = {},
            onCertificationMemberClick = {},
            onCertificationSummaryClick = {},
            onCertificationLikeClick = { _, _ -> },
            onDismissNewCertificationDialog = {},
            onMemberClick = {},
            onDismissMemberDialog = {},
            onChatClick = {},
            onChatMessageChange = {},
            onChatSendClick = {},
            onChatEmojiSelected = {},
            onMessageEditClick = {},
            onMessageDraftChange = {},
            onDismissMessageEditSheet = {},
            onMessageEditConfirmClick = {},
            onSettingsClick = {},
            onInviteCodeCopyClick = {},
            onGroupRoutineManageClick = {},
            onRoomNameEditClick = {},
            onLeaderSettingsClick = {},
            onLeaderMemberClick = {},
            onLeaderTransferConfirmClick = {},
            onRoomAlarmSettingsClick = {},
            onRoomLockClick = {},
            onRoomNameEditConfirmClick = {},
        )
    }
}
