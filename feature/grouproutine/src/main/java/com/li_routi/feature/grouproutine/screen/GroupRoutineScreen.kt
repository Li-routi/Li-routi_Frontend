package com.li_routi.feature.grouproutine.screen

import android.view.WindowManager
import androidx.activity.compose.BackHandler
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.li_routi.core.common.ui.nav.AppBottomNavBar
import com.li_routi.core.common.ui.nav.AppBottomTab
import com.li_routi.core.domain.grouproutine.GroupJoinPreview
import com.li_routi.core.domain.shop.AvatarLayer
import com.li_routi.core.domain.shop.hasCharacterLayer
import com.li_routi.core.designsystem.component.CheckBoxState
import com.li_routi.core.designsystem.component.CustomCheckBox
import com.li_routi.core.common.ui.routine.CategoryAddBottomSheet
import com.li_routi.core.common.ui.routine.CategoryColor
import com.li_routi.core.designsystem.foundation.color.ChatSendBackground
import com.li_routi.core.designsystem.foundation.color.RepresentativeBadgeBackground
import com.li_routi.core.designsystem.foundation.color.RepresentativeBadgeText
import com.li_routi.core.designsystem.foundation.color.MemberHeroGradientEnd
import com.li_routi.core.designsystem.foundation.color.Neutral10
import com.li_routi.core.designsystem.component.LiroutiBottomSheet
import com.li_routi.core.designsystem.component.LiroutiBottomSheetCloseButton
import com.li_routi.core.designsystem.component.LiroutiBottomSheetDeleteButton
import com.li_routi.core.designsystem.component.LiroutiBadge
import com.li_routi.core.designsystem.component.LiroutiBadgeColor
import com.li_routi.core.designsystem.component.LiroutiChevronLeftIcon
import com.li_routi.core.designsystem.component.LiroutiChevronRightIcon
import com.li_routi.core.designsystem.component.LiroutiClockTime
import com.li_routi.core.designsystem.component.LiroutiDashedAddButton
import com.li_routi.core.designsystem.component.LiroutiDaySelector
import com.li_routi.core.designsystem.component.LiroutiPrimaryButton
import com.li_routi.core.designsystem.component.LiroutiSearchField
import com.li_routi.core.designsystem.component.LiroutiSwitch
import com.li_routi.core.designsystem.component.LiroutiTimeWheelPicker
import com.li_routi.core.designsystem.component.LiroutiToast
import com.li_routi.core.designsystem.component.LiroutiToastStyle
import com.li_routi.core.designsystem.R as DesignSystemR
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme
import com.li_routi.feature.grouproutine.R
import com.li_routi.feature.grouproutine.component.ChatEmoticonUiModel
import com.li_routi.feature.grouproutine.component.ChatMessageUiModel
import com.li_routi.feature.grouproutine.navigation.GrouproutineEntryPoint
import com.li_routi.feature.grouproutine.navigation.GroupRoutineVerificationTarget
import com.li_routi.feature.grouproutine.vm.CertificationPostUiModel
import com.li_routi.feature.grouproutine.vm.CreateRoutineOptionUiModel
import com.li_routi.feature.grouproutine.vm.GroupMemberUiModel
import com.li_routi.feature.grouproutine.vm.GroupRoutineScreenMode
import com.li_routi.feature.grouproutine.vm.GroupRoutineUiModel
import com.li_routi.feature.grouproutine.vm.GroupRoutineUiState
import com.li_routi.feature.grouproutine.vm.GroupRoutineViewModel
import com.li_routi.feature.grouproutine.vm.GroupTodoUiModel
import com.li_routi.feature.grouproutine.vm.NewCertificationUiModel
import java.time.LocalDate
import java.time.YearMonth
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

@Composable
fun GroupRoutineRoute(
    initialEntryPoint: GrouproutineEntryPoint? = null,
    onInitialEntryPointConsumed: () -> Unit = {},
    viewModel: GroupRoutineViewModel = viewModel { GroupRoutineViewModel() },
    onTabSelected: (AppBottomTab) -> Unit = {},
    onStartVerification: (GroupRoutineVerificationTarget) -> Unit = {},
    verificationRefreshSignal: Int = 0,
    verifiedRoutineId: Long? = null,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val clipboardManager = LocalClipboardManager.current

    // 홈의 "방 만들기"/"초대코드로 참여" 바로가기로 들어온 경우(=initialEntryPoint), 그 첫
    // 화면(CreateRoomName/JoinByCode)에서 뒤로가기를 누르면 홈으로 돌아가야 한다. 하지만
    // viewModel.onBackClick()의 상태 전이는 항상 "그룹 루틴 목록"으로 돌아가도록만 되어 있어
    // (List 탭 안에서 그 버튼을 눌러 들어온 일반적인 경우를 위한 것), 홈에서 바로 들어온
    // 경우에도 똑같이 목록 화면에 떨어져 버리는 문제가 있었다. 그 첫 화면에 있는 동안만 이
    // 플래그를 켜 두고 뒤로가기를 가로채 홈으로 보낸다.
    var enteredViaExternalEntryPoint by remember { mutableStateOf(false) }
    LaunchedEffect(uiState.screenMode) {
        // 목록으로 돌아오면(정상 완료/다른 경로 등 어떤 식으로든) 더 이상 "홈에서 막 들어온
        // 첫 화면"이 아니므로 플래그를 내려서, 이후 목록 화면 안에서 같은 버튼을 다시 눌러
        // 들어갔을 때는 원래대로 뒤로가기가 목록으로 돌아가게 한다.
        if (uiState.screenMode == GroupRoutineScreenMode.List) {
            enteredViaExternalEntryPoint = false
        }
    }

    // 화면 안의 뒤로가기 버튼(CreateRoomNameScreen/JoinByCodeScreen 등)과 시스템/제스처 뒤로가기가
    // 서로 다른 동작을 하면 안 되므로 하나의 핸들러로 합쳐서 BackHandler와 onBackClick 파라미터
    // 양쪽에 똑같이 전달한다 — 전엔 BackHandler에만 이 분기가 있어서 화면 안 뒤로가기 버튼을 누르면
    // "홈에서 막 들어온 첫 화면" 판정을 건너뛰고 늘 그룹 루틴 목록으로 떨어졌다.
    val handleGroupRoutineBackClick: () -> Unit = handle@{
        val isEntryScreen = uiState.screenMode == GroupRoutineScreenMode.CreateRoomName ||
            uiState.screenMode == GroupRoutineScreenMode.JoinByCode
        if (enteredViaExternalEntryPoint && isEntryScreen) {
            enteredViaExternalEntryPoint = false
            // 탭 전환용 onTabSelected 분기(217번째 줄 근처)와 동일하게, 홈으로 나가기 전에
            // 그룹 루틴 탭 상태를 정리한다.
            viewModel.onGroupRoutineTabExit()
            onTabSelected(AppBottomTab.Home)
            return@handle
        }
        viewModel.onBackClick()
    }

    // 목록이 아닌 화면(상세/채팅/설정 등)에서는 시스템/제스처 뒤로가기도 화면 자체의 뒤로가기와
    // 똑같이 동작해야 한다. 이게 없으면 AppNavHost의 탭 전환용 BackHandler가 대신 받아서
    // 곧장 홈 탭으로 나가버린다(뒤로가기를 눌렀는데 이전 화면이 아니라 홈으로 튕기는 버그).
    BackHandler(enabled = uiState.screenMode != GroupRoutineScreenMode.List) {
        handleGroupRoutineBackClick()
    }

    LaunchedEffect(initialEntryPoint) {
        when (initialEntryPoint) {
            GrouproutineEntryPoint.CreateRoom -> {
                enteredViaExternalEntryPoint = true
                viewModel.onCreateRoomClick()
            }
            GrouproutineEntryPoint.JoinWithInviteCode -> {
                enteredViaExternalEntryPoint = true
                viewModel.onJoinByCodeClick()
            }
            null -> Unit
        }
        if (initialEntryPoint != null) onInitialEntryPointConsumed()
    }

    LaunchedEffect(verificationRefreshSignal) {
        if (verificationRefreshSignal > 0) {
            viewModel.markRoutineVerified(verifiedRoutineId)
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner, viewModel) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            viewModel.refreshMemberAvatarsOnResume()
        }
    }

    GroupRoutineScreen(
        uiState = uiState,
        onTabSelected = { tab ->
            if (tab != AppBottomTab.GroupRoutine) viewModel.onGroupRoutineTabExit()
            onTabSelected(tab)
        },
        onRoutineClick = viewModel::onRoutineClick,
        onBackClick = handleGroupRoutineBackClick,
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
        onDismissJoinPreviewDialog = viewModel::onDismissJoinPreviewDialog,
        onJoinPreviewConfirmClick = viewModel::onJoinPreviewConfirmClick,
        onCreateRoutineOptionClick = viewModel::onCreateRoutineOptionClick,
        onCreateRoutineSelectAllClick = viewModel::onCreateRoutineSelectAllClick,
        onCategoryClick = viewModel::onCategoryClick,
        onCategoryAddClick = viewModel::onCategoryAddClick,
        onDismissCategorySheet = viewModel::onDismissCategorySheet,
        onCategoryInputChange = viewModel::onCategoryInputChange,
        onCategoryConfirmClick = viewModel::onCategoryConfirmClick,
        onRoutineAddClick = viewModel::onRoutineAddClick,
        onRoutineSettingClick = viewModel::onRoutineSettingClick,
        onRoutineColorLongClick = viewModel::onRoutineColorLongClick,
        onDismissRoutineColorSheet = viewModel::onDismissRoutineColorSheet,
        onRoutineColorSelected = viewModel::onRoutineColorSelected,
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
        onRoutineVerificationClick = { groupId, routineId ->
            val todo = uiState.todos.firstOrNull { it.id == routineId } ?: return@GroupRoutineScreen
            onStartVerification(
                GroupRoutineVerificationTarget(
                    groupId = groupId,
                    routineId = routineId,
                    roomName = uiState.selectedRoutine?.title.orEmpty(),
                    title = todo.title,
                    category = todo.category,
                    deadline = todo.deadline,
                    categoryColor = todo.categoryColor,
                ),
            )
        },
        onCertificationReverifyClick = { post ->
            val groupId = uiState.selectedRoutine?.id ?: return@GroupRoutineScreen
            val todo = uiState.todos.firstOrNull { it.id == post.routineId }
                ?: return@GroupRoutineScreen
            onStartVerification(
                GroupRoutineVerificationTarget(
                    groupId = groupId,
                    routineId = post.routineId,
                    verificationId = post.id,
                    roomName = uiState.selectedRoutine?.title.orEmpty(),
                    title = todo.title,
                    category = todo.category,
                    deadline = todo.deadline,
                    categoryColor = todo.categoryColor,
                ),
            )
        },
        onCertificationTabClick = viewModel::onCertificationTabClick,
        onCertificationMemberClick = viewModel::onCertificationMemberClick,
        onCertificationSummaryClick = viewModel::onCertificationSummaryClick,
        onCertificationLikeClick = viewModel::onCertificationLikeClick,
        onCertificationDisappointmentClick = viewModel::onCertificationDisappointmentClick,
        onDismissNewCertificationDialog = viewModel::onDismissNewCertificationDialog,
        onNewCertificationDisappointClick = viewModel::onNewCertificationDisappointClick,
        onNewCertificationLikeClick = viewModel::onNewCertificationLikeClick,
        onMemberClick = viewModel::onMemberClick,
        onDismissMemberDialog = viewModel::onDismissMemberDialog,
        onPokeMemberClick = viewModel::onPokeMemberClick,
        onChatClick = viewModel::onChatClick,
        onChatMessageChange = viewModel::onChatMessageChange,
        onChatSendClick = viewModel::onChatSendClick,
        onChatEmojiSelected = viewModel::onChatEmojiSelected,
        onChatLoadMore = viewModel::onChatScrolledToTop,
        onReplySwipe = viewModel::onReplyTargetSelected,
        onReplyCancelClick = viewModel::onReplyTargetCleared,
        onCalendarMonthChange = viewModel::onCalendarMonthChange,
        onChatDateSelected = viewModel::onChatDateSelected,
        onMessageEditClick = viewModel::onMessageEditClick,
        onMessageDraftChange = viewModel::onMessageDraftChange,
        onDismissMessageEditSheet = viewModel::onDismissMessageEditSheet,
        onMessageEditConfirmClick = viewModel::onMessageEditConfirmClick,
        onSettingsClick = viewModel::onSettingsClick,
        onInviteCodeCopyClick = {
            uiState.groupInviteCode
                ?.takeIf(String::isNotBlank)
                ?.let { inviteCode ->
                    clipboardManager.setText(AnnotatedString(inviteCode))
                    viewModel.onInviteCodeCopyClick()
                }
        },
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
    onDismissJoinPreviewDialog: () -> Unit,
    onJoinPreviewConfirmClick: () -> Unit,
    onCreateRoutineOptionClick: (Long) -> Unit,
    onCreateRoutineSelectAllClick: () -> Unit,
    onCategoryClick: (String) -> Unit,
    onCategoryAddClick: () -> Unit,
    onDismissCategorySheet: () -> Unit,
    onCategoryInputChange: (String) -> Unit,
    onCategoryConfirmClick: () -> Unit,
    onRoutineAddClick: () -> Unit,
    onRoutineSettingClick: (Long) -> Unit,
    onRoutineColorLongClick: (Long) -> Unit,
    onDismissRoutineColorSheet: () -> Unit,
    onRoutineColorSelected: (CategoryColor) -> Unit,
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
    onRoutineVerificationClick: (Long, Long) -> Unit = { _, _ -> },
    onCertificationReverifyClick: (CertificationPostUiModel) -> Unit = {},
    onCertificationTabClick: (Boolean) -> Unit,
    onCertificationMemberClick: (Long?) -> Unit,
    onCertificationSummaryClick: () -> Unit,
    onCertificationLikeClick: (Long, Boolean) -> Unit,
    onCertificationDisappointmentClick: (Long, Boolean) -> Unit,
    onDismissNewCertificationDialog: () -> Unit,
    onNewCertificationDisappointClick: (Long) -> Unit = {},
    onNewCertificationLikeClick: (Long) -> Unit = {},
    onMemberClick: (Long) -> Unit,
    onDismissMemberDialog: () -> Unit,
    onPokeMemberClick: () -> Unit = {},
    onChatClick: () -> Unit,
    onChatMessageChange: (String) -> Unit,
    onChatSendClick: () -> Unit,
    onChatEmojiSelected: (ChatEmoticonUiModel) -> Unit,
    onChatLoadMore: () -> Unit = {},
    onReplySwipe: (ChatMessageUiModel) -> Unit,
    onReplyCancelClick: () -> Unit,
    onCalendarMonthChange: (YearMonth) -> Unit = {},
    onChatDateSelected: (LocalDate) -> Unit = {},
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
            GroupRoutineScreenMode.List -> if (uiState.isListLoading) {
                GroupRoutineLoadingState()
            } else {
                GroupRoutineListScreen(
                    uiState = uiState,
                    onRoutineClick = onRoutineClick,
                    onAddClick = onAddClick,
                    onSearchInputChange = onSearchInputChange,
                    onTabSelected = onTabSelected,
                )
            }

            GroupRoutineScreenMode.Detail -> if (uiState.isDetailLoading) {
                GroupRoutineLoadingState()
            } else GroupRoutineDetailScreen(
                uiState = uiState,
                onBackClick = onBackClick,
                onRoutineVerificationClick = onRoutineVerificationClick,
                onCertificationTabClick = onCertificationTabClick,
                onCertificationSummaryClick = onCertificationSummaryClick,
                onMemberClick = onMemberClick,
                onDismissMemberDialog = onDismissMemberDialog,
                onPokeMemberClick = onPokeMemberClick,
                onMemberKickClick = onMemberKickClick,
                onChatClick = onChatClick,
                onMessageEditClick = onMessageEditClick,
                onSettingsClick = onSettingsClick,
                onInviteCodeClick = onInviteCodeCopyClick,
                onRoutineColorLongClick = onRoutineColorLongClick,
                onCategoryClick = onCategoryClick,
                onTabSelected = onTabSelected,
            )

            GroupRoutineScreenMode.CertificationCollection -> if (uiState.isCertificationLoading) {
                GroupRoutineLoadingState()
            } else CertificationCollectionScreen(
                uiState = uiState,
                onBackClick = onBackClick,
                onCertificationMemberClick = onCertificationMemberClick,
                onCertificationLikeClick = onCertificationLikeClick,
                onCertificationDisappointmentClick = onCertificationDisappointmentClick,
                onCertificationReverifyClick = onCertificationReverifyClick,
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
                    onLoadMore = onChatLoadMore,
                    replyTarget = uiState.replyTarget,
                    onReplySwipe = onReplySwipe,
                    onReplyCancelClick = onReplyCancelClick,
                    isInitialHistoryLoaded = uiState.isChatHistoryLoaded,
                    chatDates = uiState.chatDates,
                    onCalendarMonthChange = onCalendarMonthChange,
                    onChatDateSelected = onChatDateSelected,
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
                onRoutineColorLongClick = onRoutineColorLongClick,
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
                onRoutineColorLongClick = onRoutineColorLongClick,
                onDoneClick = onCreateRoomDoneClick,
            )
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

    if (uiState.isJoinPreviewDialogVisible) {
        uiState.joinPreview?.let { preview ->
            GroupJoinPreviewDialog(
                preview = preview,
                onDismissRequest = onDismissJoinPreviewDialog,
                onConfirmClick = onJoinPreviewConfirmClick,
            )
        }
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
            placeholder = "카테고리 이름을 입력해 주세요",
        )
    }

    if (uiState.isRoutineColorSheetVisible) {
        val routineColorTargetName = remember(
            uiState.routineColorTargetId,
            uiState.routineOptions,
            uiState.todos,
        ) {
            val targetId = uiState.routineColorTargetId
            uiState.routineOptions.firstOrNull { it.id == targetId }?.title
                ?: uiState.todos.firstOrNull { it.id == targetId }?.title
                ?: ""
        }
        RoutineColorBottomSheet(
            routineName = routineColorTargetName,
            selectedColor = uiState.routineColorInput,
            onColorSelected = onRoutineColorSelected,
            onDeleteClick = onRoutineDeleteClick,
            onDismissRequest = onDismissRoutineColorSheet,
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

    uiState.actionMessage?.let { message ->
        LaunchedEffect(message, uiState.actionMessageId) {
            delay(4_000)
            onDismissActionMessage()
        }
        ActionMessageToastDialog(
            message = message,
            onDismiss = onDismissActionMessage,
        )
    }
}
@Composable
private fun ActionMessageToastDialog(
    message: String,
    onDismiss: () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter,
    ) {
        LiroutiToast(
            message = message,
            style = LiroutiToastStyle.Dimmer,
            onCloseClick = onDismiss,
            modifier = Modifier
                .navigationBarsPadding()
                .padding(bottom = 92.dp)
                .widthIn(max = 332.dp)
                .fillMaxWidth()
                .height(54.dp),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
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
            .background(LiroutiTheme.colors.backgroundSecondary),
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

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            if (uiState.routines.isEmpty()) {
                GroupRoutineEmptyState()
            } else if (uiState.visibleRoutines.isEmpty()) {
                GroupRoutineEmptyState(
                    message = "검색 결과가 없어요.",
                    modifier = Modifier.fillMaxWidth(),
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
            .background(LiroutiTheme.colors.backgroundDefault),
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
                color = LiroutiTheme.colors.labelDefault,
                style = LiroutiTheme.typography.heading2.copy(fontWeight = FontWeight.Bold),
            )
            Text(
                text = "친구들이 방 목록에서 이 이름으로 보게 돼요.",
                color = LiroutiTheme.colors.labelInfo,
                style = LiroutiTheme.typography.body3,
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "방이름",
                color = LiroutiTheme.colors.labelDefault,
                style = LiroutiTheme.typography.body3.copy(fontWeight = FontWeight.Bold),
            )
            BasicInputBox(
                value = roomName,
                onValueChange = onRoomNameChange,
                placeholder = "방 이름을 입력해 주세요",
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
            .background(LiroutiTheme.colors.backgroundDefault),
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
                color = LiroutiTheme.colors.labelDefault,
                style = LiroutiTheme.typography.heading2.copy(fontWeight = FontWeight.Bold),
            )
            Text(
                text = "친구에게 받은 초대코드로 그룹방에 참여할 수 있어요.",
                color = LiroutiTheme.colors.labelInfo,
                style = LiroutiTheme.typography.body3,
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "초대코드",
                color = LiroutiTheme.colors.labelDefault,
                style = LiroutiTheme.typography.body3.copy(fontWeight = FontWeight.Bold),
            )
            BasicInputBox(
                value = inviteCode,
                onValueChange = onInviteCodeChange,
                placeholder = "초대코드를 입력해 주세요",
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
    onRoutineColorLongClick: (Long) -> Unit,
    onDoneClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(LiroutiTheme.colors.backgroundDefault),
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
                            color = LiroutiTheme.colors.labelDefault,
                            style = LiroutiTheme.typography.heading2.copy(fontWeight = FontWeight.Bold),
                        )
                        Text(
                            text = "루틴을 누르면 세부 설정을 변경할 수 있어요",
                            color = LiroutiTheme.colors.labelInfo,
                            style = LiroutiTheme.typography.body3,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        CategoryChipRow(
                            categories = uiState.categories,
                            selectedCategory = uiState.selectedCategory,
                            categoryColors = uiState.categoryColors,
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
                        onRoutineColorLongClick = onRoutineColorLongClick,
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(LiroutiTheme.colors.backgroundDefault)
                .navigationBarsPadding()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            DashedRoutineAddButton(onClick = onRoutineAddClick)
            Text(
                text = "총 ${selectedCount}개 선택됨",
                color = LiroutiTheme.colors.labelSub,
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
    categoryColors: Map<String, CategoryColor>,
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
                val selectedColor = categoryColors[label]?.swatch ?: LiroutiTheme.colors.primaryNormal
                GroupRoutineCategoryChip(
                    label = label,
                    selected = selected,
                    selectedColor = selectedColor,
                    onClick = { onCategoryClick(label) },
                )
            }
        }
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(LiroutiTheme.colors.backgroundDefault.copy(alpha = 0.9f))
                .border(1.dp, LiroutiTheme.colors.borderDefault, CircleShape)
                .clickable(onClick = onCategoryAddClick),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = "+",
                color = LiroutiTheme.colors.labelSub,
                style = LiroutiTheme.typography.body2Long.copy(fontWeight = FontWeight.Medium),
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
private fun GroupRoutineCategoryChip(
    label: String,
    selected: Boolean,
    selectedColor: Color,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(100.dp)
    Text(
        text = if (selected) "✓ $label" else label,
        color = if (selected) selectedColor.readableContentColor() else LiroutiTheme.colors.labelSub,
        style = LiroutiTheme.typography.body3.copy(fontWeight = FontWeight.Medium),
        modifier = Modifier
            .clip(shape)
            .background(if (selected) selectedColor else LiroutiTheme.colors.backgroundDefault)
            .border(1.dp, if (selected) selectedColor else LiroutiTheme.colors.borderDefault, shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RoutineColorBottomSheet(
    routineName: String,
    selectedColor: CategoryColor?,
    onColorSelected: (CategoryColor) -> Unit,
    onDeleteClick: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    val currentColor = selectedColor?.swatch ?: LiroutiTheme.colors.primaryNormal

    LiroutiBottomSheet(
        onDismissRequest = onDismissRequest,
        contentPadding = PaddingValues(start = 16.dp, top = 30.dp, end = 16.dp, bottom = 32.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                LiroutiBottomSheetCloseButton(onClick = onDismissRequest)
                LiroutiBottomSheetDeleteButton(onClick = onDeleteClick)
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(LiroutiTheme.colors.backgroundDefault)
                    .border(1.dp, LiroutiTheme.colors.borderDefault, RoundedCornerShape(6.dp))
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterStart,
            ) {
                Text(
                    text = routineName.ifBlank { "루틴 이름" },
                    color = if (routineName.isBlank()) LiroutiTheme.colors.labelInfo else LiroutiTheme.colors.labelDefault,
                    style = LiroutiTheme.typography.body3,
                )
            }

             Column(
                 modifier = Modifier
                     .fillMaxWidth()
                     .height(110.dp)
                     .clip(RoundedCornerShape(4.dp))
                     .background(LiroutiTheme.colors.backgroundFill)
                     .padding(start = 12.dp, top = 5.dp, end = 12.dp, bottom = 14.dp),
             ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "카테고리 색",
                        color = LiroutiTheme.colors.labelDefault,
                        style = LiroutiTheme.typography.body3.copy(fontWeight = FontWeight.Bold),
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(currentColor),
                    )
                }

                HorizontalDivider(color = LiroutiTheme.colors.borderSub)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    CategoryColor.values().forEach { color ->
                        RoutineColorSwatch(
                            color = color,
                            selected = color == selectedColor,
                            onClick = { onColorSelected(color) },
                        )
                    }
                }
            }

            Button(
                onClick = onDismissRequest,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                shape = RoundedCornerShape(6.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = LiroutiTheme.colors.primaryNormal,
                    contentColor = LiroutiTheme.colors.labelReverse,
                ),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
            ) {
                Text(
                    text = "확인",
                    color = LiroutiTheme.colors.labelReverse,
                    style = LiroutiTheme.typography.body3.copy(fontWeight = FontWeight.Medium),
                )
            }
        }
    }
}

@Composable
private fun RoutineColorSwatch(
    color: CategoryColor,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(30.dp)
            .clip(CircleShape)
            .background(color.swatch)
            .border(
                width = 1.dp,
                color = color.border,
                shape = CircleShape,
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        if (selected) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = null,
                tint = LiroutiTheme.colors.labelReverse,
                modifier = Modifier.size(18.dp),
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
            color = LiroutiTheme.colors.labelDefault,
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
    onRoutineColorLongClick: (Long) -> Unit,
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
                .background(LiroutiTheme.colors.backgroundDefault)
                .border(1.dp, LiroutiTheme.colors.borderAlternative, RoundedCornerShape(6.dp))
                .padding(horizontal = 16.dp, vertical = 2.dp),
        ) {
            SelectAllRoutineRow(
                checked = uiState.allVisibleRoutineOptionsSelected,
                onClick = onSelectAllClick,
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        HorizontalDivider(color = LiroutiTheme.colors.borderDefault)
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
                        onLongClick = { onRoutineColorLongClick(option.id) },
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
                .background(LiroutiTheme.colors.borderStrong),
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CreateRoutineOptionRow(
    option: CreateRoutineOptionUiModel,
    onCheckClick: () -> Unit,
    onSettingClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(4.dp)
    val categoryColor = option.categoryColor?.swatch ?: LiroutiTheme.colors.primaryNormal

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(shape)
            .background(LiroutiTheme.colors.backgroundDefault)
            .border(1.dp, LiroutiTheme.colors.borderAlternative, shape)
            .combinedClickable(
                onClick = onSettingClick,
                onLongClick = onLongClick,
            ),
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .width(4.dp)
                .fillMaxHeight()
                .background(categoryColor),
        )
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 14.dp, end = 14.dp, top = 10.dp, bottom = 10.dp),
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
                    color = LiroutiTheme.colors.labelDefault,
                    style = LiroutiTheme.typography.body2LongMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "마감 ${option.deadline}",
                        color = LiroutiTheme.colors.labelInfo,
                        style = LiroutiTheme.typography.caption.copy(fontSize = 11.sp, lineHeight = 14.sp),
                    )
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(10.dp)
                            .background(LiroutiTheme.colors.borderStrong),
                    )
                    Text(
                        text = option.category,
                        color = LiroutiTheme.colors.labelInfo,
                        style = LiroutiTheme.typography.caption.copy(fontSize = 11.sp, lineHeight = 14.sp),
                    )
                }
            }
            if (option.repeatLabel.isNotBlank() && option.repeatLabel != "없음") {
                Text(
                    text = option.repeatLabel,
                    color = LiroutiTheme.colors.pendingText,
                    style = LiroutiTheme.typography.caption.copy(fontSize = 11.sp, lineHeight = 14.sp),
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(LiroutiTheme.colors.backgroundSecondary)
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
    enabled: Boolean = true,
    onClick: () -> Unit = {},
) {
    CustomCheckBox(
        state = if (checked) CheckBoxState.B else CheckBoxState.A,
        isCircle = false,
        enabled = enabled,
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                LiroutiBottomSheetCloseButton(onClick = onDismissRequest)
                LiroutiBottomSheetDeleteButton(onClick = onDeleteClick)
            }
            BasicInputBox(
                value = routineName,
                onValueChange = onNameChange,
                placeholder = "루틴 이름을 입력해 주세요",
                showClear = routineName.isNotEmpty(),
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(4.dp)),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
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
        textStyle = LiroutiTheme.typography.body2Long.copy(color = LiroutiTheme.colors.labelDefault),
        placeholder = {
            Text(text = placeholder, color = LiroutiTheme.colors.labelInfo, style = LiroutiTheme.typography.body2Long)
        },
        trailingIcon = {
            if (showClear) {
                GroupRoutineCloseButton(
                    onClick = { onValueChange("") },
                    tint = LiroutiTheme.colors.labelInfo,
                    modifier = Modifier.size(18.dp),
                )
            }
        },
        shape = RoundedCornerShape(6.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = LiroutiTheme.colors.backgroundDefault,
            unfocusedContainerColor = LiroutiTheme.colors.backgroundDefault,
            disabledContainerColor = LiroutiTheme.colors.backgroundDefault,
            focusedIndicatorColor = LiroutiTheme.colors.borderDefault,
            unfocusedIndicatorColor = LiroutiTheme.colors.borderDefault,
            cursorColor = LiroutiTheme.colors.primaryNormal,
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
            .background(LiroutiTheme.colors.backgroundFill)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = label, color = LiroutiTheme.colors.labelSub, style = LiroutiTheme.typography.body3)
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = value,
            color = LiroutiTheme.colors.labelSub,
            style = LiroutiTheme.typography.body3.copy(fontWeight = FontWeight.Bold),
        )
        Image(
            painter = painterResource(id = DesignSystemR.drawable.chevron__right),
            contentDescription = if (expanded) "접기" else "펼치기",
            modifier = Modifier
                .padding(start = 4.dp)
                .size(16.dp)
                .rotate(if (expanded) -90f else 90f),
            colorFilter = ColorFilter.tint(LiroutiTheme.colors.labelSub),
        )
    }
}

@Composable
private fun RoutineSettingDivider() {
    HorizontalDivider(
        color = LiroutiTheme.colors.borderSub,
        modifier = Modifier.padding(horizontal = 12.dp),
    )
}

@Composable
private fun RoutineTimePicker(
    time: String,
    onTimeChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LiroutiTimeWheelPicker(
        value = LiroutiClockTime.fromApiHHmm(time),
        onValueChange = { selectedTime -> onTimeChange(selectedTime.toApiHHmm()) },
        modifier = modifier,
    )
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
                .background(LiroutiTheme.colors.borderAlternative),
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
                        color = if (index == selectedIndex) LiroutiTheme.colors.labelDefault else LiroutiTheme.colors.labelInfo,
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
            .background(if (selected) LiroutiTheme.colors.borderAlternative else LiroutiTheme.colors.backgroundFill)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = if (hour < 12) "오전" else "오후",
            color = if (selected) LiroutiTheme.colors.labelDefault else LiroutiTheme.colors.labelSub,
            style = LiroutiTheme.typography.body3.copy(fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium),
            modifier = Modifier.weight(1f),
        )
        Text(
            text = displayHour12(hour).toString(),
            color = if (selected) LiroutiTheme.colors.labelDefault else LiroutiTheme.colors.labelSub,
            style = LiroutiTheme.typography.body3.copy(fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium),
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = minute.toString().padStart(2, '0'),
            color = if (selected) LiroutiTheme.colors.labelDefault else LiroutiTheme.colors.labelSub,
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
                color = if (selected) LiroutiTheme.colors.primaryNormal.readableContentColor() else LiroutiTheme.colors.labelSub,
                style = LiroutiTheme.typography.body3.copy(fontWeight = FontWeight.Medium),
                modifier = Modifier
                    .clip(RoundedCornerShape(100.dp))
                    .background(if (selected) LiroutiTheme.colors.primaryNormal else LiroutiTheme.colors.backgroundDefault)
                    .border(1.dp, if (selected) LiroutiTheme.colors.primaryNormal else LiroutiTheme.colors.borderDefault, RoundedCornerShape(100.dp))
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
            .background(LiroutiTheme.colors.backgroundFill)
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

private fun Color.readableContentColor(): Color =
    if (luminance() < 0.5f) Color.White else Neutral10

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
                .background(LiroutiTheme.colors.backgroundDefault)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = title,
                    color = LiroutiTheme.colors.labelDefault,
                    style = LiroutiTheme.typography.heading2.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.weight(1f),
                )
                GroupRoutineCloseButton(
                    onClick = onDismissRequest,
                    modifier = Modifier.size(28.dp),
                )
            }
            Text(
                text = description,
                color = LiroutiTheme.colors.labelSub,
                style = LiroutiTheme.typography.body2Long,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = onDismissRequest,
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = LiroutiTheme.colors.backgroundFill, contentColor = LiroutiTheme.colors.labelDefault),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                ) {
                    Text(text = "취소")
                }
                Button(
                    onClick = onConfirmClick,
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = LiroutiTheme.colors.dangerBase, contentColor = LiroutiTheme.colors.labelReverse),
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

/** 초대코드 확인 후 실제 가입 전에 그룹 미리보기를 보여주고 참여 여부를 확인받는 팝업 */
@Composable
private fun GroupJoinPreviewDialog(
    preview: GroupJoinPreview,
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
                .background(LiroutiTheme.colors.backgroundDefault)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            Text(
                text = "이 방에 참여할까요?",
                color = LiroutiTheme.colors.labelDefault,
                style = LiroutiTheme.typography.heading2Bold,
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(6.dp))
                    .background(LiroutiTheme.colors.backgroundAlternative)
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                if (preview.memberAvatars.isNotEmpty()) {
                    Row {
                        preview.memberAvatars.take(3).forEachIndexed { index, layers ->
                            Box(
                                modifier = Modifier
                                    .offset(x = (-8).dp * index)
                                    .size(40.dp)
                                    .border(1.5.dp, LiroutiTheme.colors.borderAlternative, CircleShape)
                                    .clip(CircleShape)
                                    .background(LiroutiTheme.colors.labelReverse),
                                contentAlignment = Alignment.Center,
                            ) {
                                MemberAvatarImage(layers = layers, modifier = Modifier.size(40.dp))
                            }
                        }
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = preview.name,
                        color = LiroutiTheme.colors.labelDefault,
                        style = LiroutiTheme.typography.body1SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        GroupJoinPreviewInfoRow(label = "멤버", value = "${preview.activeMemberCount}명")
                        GroupJoinPreviewInfoRow(label = "전체 루틴", value = "${preview.totalRoutineCount}개")
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Button(
                    onClick = onDismissRequest,
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LiroutiTheme.colors.backgroundAlternative,
                        contentColor = LiroutiTheme.colors.labelDefault,
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp),
                ) {
                    Text(text = "취소")
                }
                Button(
                    onClick = onConfirmClick,
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LiroutiTheme.colors.primaryNormal,
                        contentColor = LiroutiTheme.colors.backgroundAlternative,
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp),
                ) {
                    Text(text = "참여하기")
                }
            }
        }
    }
}

@Composable
private fun GroupJoinPreviewInfoRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = label,
            color = LiroutiTheme.colors.labelSub,
            style = LiroutiTheme.typography.body3Bold,
            modifier = Modifier.width(80.dp),
        )
        Text(
            text = value,
            color = LiroutiTheme.colors.labelDefault,
            style = LiroutiTheme.typography.body2LongRegular,
        )
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
    val currentPage = pagerState.currentPage.coerceIn(certifications.indices)

    LaunchedEffect(certifications.size) {
        if (pagerState.currentPage > certifications.lastIndex) {
            pagerState.scrollToPage(certifications.lastIndex)
        }
    }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 36.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(LiroutiTheme.colors.backgroundDefault)
                .padding(22.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "새 인증",
                    color = LiroutiTheme.colors.labelDefault,
                    style = LiroutiTheme.typography.heading2.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.weight(1f),
                )
                GroupRoutineCloseButton(
                    onClick = onDismissRequest,
                    modifier = Modifier.size(28.dp),
                )
            }
            HorizontalPager(state = pagerState) { page ->
                AsyncImage(
                    model = certifications[page].imageUrl,
                    error = painterResource(id = R.drawable.img_group_routine_cert_water),
                    fallback = painterResource(id = R.drawable.img_group_routine_cert_water),
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
                            .background(if (index == currentPage) LiroutiTheme.colors.labelDefault else LiroutiTheme.colors.borderDefault),
                    )
                }
            }
            val current = certifications[currentPage]
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "${current.memberName} | ${current.routineName}",
                    color = LiroutiTheme.colors.labelDefault,
                    style = LiroutiTheme.typography.body2Long.copy(fontWeight = FontWeight.Bold),
                )
                Text(
                    text = current.message,
                    color = LiroutiTheme.colors.labelSub,
                    style = LiroutiTheme.typography.body3,
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                  Button(
                      onClick = { onNegativeClick(current.id) },
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = LiroutiTheme.colors.backgroundFill, contentColor = LiroutiTheme.colors.labelDefault),
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp),
                ) {
                    Text(text = "아쉬워요", style = LiroutiTheme.typography.body3)
                }
                  Button(
                      onClick = { onPositiveClick(current.id) },
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = LiroutiTheme.colors.primaryNormal, contentColor = LiroutiTheme.colors.labelReverse),
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
                GroupRoutineCloseButton(
                    onClick = onDismissRequest,
                    modifier = Modifier.size(28.dp),
                )
            }
            Text(
                text = "상태 메시지",
                color = LiroutiTheme.colors.labelDefault,
                style = LiroutiTheme.typography.body2Long.copy(fontWeight = FontWeight.Bold),
            )
            BasicInputBox(
                value = message,
                onValueChange = onMessageChange,
                placeholder = "상태 메시지를 입력해 주세요",
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
            .background(LiroutiTheme.colors.backgroundDefault)
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
private fun GroupRoutineLoadingState() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LiroutiTheme.colors.backgroundDefault),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator(color = LiroutiTheme.colors.primaryNormal)
    }
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
        Icon(
            painter = painterResource(id = DesignSystemR.drawable.warning),
            contentDescription = null,
            tint = LiroutiTheme.colors.labelInfo,
            modifier = Modifier.size(28.dp),
        )
        Text(
            text = message,
            color = LiroutiTheme.colors.labelInfo,
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
            .background(LiroutiTheme.colors.backgroundDefault)
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = routine.title,
                        color = LiroutiTheme.colors.labelDefault,
                        style = LiroutiTheme.typography.body1.copy(fontWeight = FontWeight.Bold),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = routine.lastActiveLabel,
                        color = LiroutiTheme.colors.primaryNormal,
                        style = LiroutiTheme.typography.caption,
                        maxLines = 1,
                    )
                }
                Text(
                    text = "멤버 ${routine.memberCount}명  |  루틴 ${routine.routineCount}개",
                    color = LiroutiTheme.colors.labelDefault,
                    style = LiroutiTheme.typography.caption.copy(fontSize = 11.sp, lineHeight = 14.sp),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            StatusBadge(label = routine.statusLabel, completed = routine.isCompleted)
        }

        HorizontalDivider(color = LiroutiTheme.colors.borderDefault)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            AvatarStack(memberCount = routine.memberCount)
            Text(
                text = "오늘 ${routine.todayCompletedCount}/${routine.todayTotalCount} 완료",
                color = LiroutiTheme.colors.labelInfo,
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
        color = if (completed) LiroutiTheme.colors.completeText else LiroutiTheme.colors.pendingText,
        style = LiroutiTheme.typography.caption.copy(fontSize = 11.sp, lineHeight = 14.sp),
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(if (completed) LiroutiTheme.colors.completeBackground else LiroutiTheme.colors.backgroundSecondary)
            .padding(horizontal = 7.dp, vertical = 3.dp),
    )
}

@Composable
private fun AvatarStack(
    memberCount: Int,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy((-8).dp)) {
        repeat(memberCount.coerceIn(1, 3)) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(LiroutiTheme.colors.borderSub)
                    .border(0.6.dp, LiroutiTheme.colors.borderDefault, CircleShape),
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
            .background(LiroutiTheme.colors.backgroundFill)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        StatItem(value = "${routine.streakDays}일", label = "연속 달성")
        VerticalStatDivider()
        StatItem(value = "${routine.monthlyAchievementRate}%", label = "이번 달 달성률")
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
            color = LiroutiTheme.colors.labelSub,
            style = LiroutiTheme.typography.body1.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            color = LiroutiTheme.colors.labelSub,
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
            .background(LiroutiTheme.colors.borderDefault),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GroupRoutineDetailScreen(
    uiState: GroupRoutineUiState,
    onBackClick: () -> Unit,
    onRoutineVerificationClick: (Long, Long) -> Unit,
    onCertificationTabClick: (Boolean) -> Unit,
    onCertificationSummaryClick: () -> Unit,
    onMemberClick: (Long) -> Unit,
    onDismissMemberDialog: () -> Unit,
    onPokeMemberClick: () -> Unit,
    onMemberKickClick: () -> Unit,
    onChatClick: () -> Unit,
    onMessageEditClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onInviteCodeClick: () -> Unit,
    onRoutineColorLongClick: (Long) -> Unit,
    onCategoryClick: (String) -> Unit,
    onTabSelected: (AppBottomTab) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val routine = uiState.selectedRoutine ?: return
    val sheetScaffoldState = rememberBottomSheetScaffoldState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(LiroutiTheme.colors.backgroundSecondary),
    ) {
        BottomSheetScaffold(
            modifier = Modifier.fillMaxSize(),
            scaffoldState = sheetScaffoldState,
            topBar = {
                GroupRoutineTopBar(
                    title = routine.title,
                    showBack = true,
                    showActions = true,
                    chatBadgeCount = uiState.unreadChatCount,
                    onBackClick = onBackClick,
                    onChatClick = onChatClick,
                    onSettingsClick = onSettingsClick,
                )
            },
            sheetPeekHeight = if (uiState.members.size in 1..3) 480.dp else 400.dp,
            sheetShape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            sheetContainerColor = LiroutiTheme.colors.backgroundDefault,
            containerColor = LiroutiTheme.colors.backgroundSecondary,
            sheetDragHandle = { GroupRoutineSheetDragHandle() },
            sheetContent = {
                DetailRoutineTabSheet(
                    title = "오늘의 루틴",
                    todos = uiState.todos,
                    categories = uiState.categories,
                    selectedCategory = uiState.selectedCategory,
                    categoryColors = uiState.categoryColors,
                    progressLabel = uiState.todoProgressLabel,
                    onRoutineVerificationClick = { routineId ->
                        onRoutineVerificationClick(routine.id, routineId)
                    },
                    onRoutineColorLongClick = onRoutineColorLongClick,
                    onCategoryClick = onCategoryClick,
                )
            },
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            ) {
                DetailMemberSection(
                    title = routine.title,
                    members = uiState.members,
                    onCertificationClick = onCertificationSummaryClick,
                    onMessageEditClick = onMessageEditClick,
                    onInviteCodeClick = onInviteCodeClick,
                    onMemberClick = onMemberClick,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }

        AppBottomNavBar(
            selectedTab = AppBottomTab.GroupRoutine,
            onTabSelected = onTabSelected,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }

    uiState.selectedMember?.let { member ->
        MemberProfileDialog(
            member = member,
            // 방장만 내보낼 수 있고, 자기 자신은 못 내보냄
            canKick = uiState.isConfirmedOwner && !member.isMe,
            onDismissRequest = onDismissMemberDialog,
            onPokeClick = onPokeMemberClick,
            onKickClick = onMemberKickClick,
        )
    }
}

@Composable
private fun GroupRoutineSheetDragHandle(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(20.dp),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .width(44.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(100.dp))
                .background(LiroutiTheme.colors.borderDefault),
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
                    colors = listOf(LiroutiTheme.colors.backgroundDefault, MemberHeroGradientEnd),
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
                color = LiroutiTheme.colors.labelDefault,
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
            TextAction(
                label = "메시지 수정",
                iconRes = DesignSystemR.drawable.edit,
                onClick = onMessageEditClick,
            )
            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .width(1.dp)
                    .height(12.dp)
                    .background(LiroutiTheme.colors.borderDefault),
            )
            TextAction(
                label = "초대코드",
                iconRes = DesignSystemR.drawable.copy,
                onClick = onInviteCodeClick,
            )
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
            color = LiroutiTheme.colors.labelDefault,
            style = LiroutiTheme.typography.caption.copy(fontWeight = FontWeight.Bold),
            maxLines = 1,
        )
    }
}

@Composable
private fun DetailRoutineTabSheet(
    title: String,
    todos: List<GroupTodoUiModel>,
    categories: List<String>,
    selectedCategory: String,
    categoryColors: Map<String, CategoryColor>,
    progressLabel: String,
    onRoutineVerificationClick: (Long) -> Unit,
    onRoutineColorLongClick: (Long) -> Unit,
    onCategoryClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val visibleTodos = remember(todos, selectedCategory) {
        if (selectedCategory == "전체") {
            todos
        } else {
            todos.filter { it.category == selectedCategory }
        }
    }
    val visibleProgressLabel = if (selectedCategory == "전체") {
        progressLabel
    } else {
        "${visibleTodos.count { it.isDone }}/${visibleTodos.size} 완료"
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(LiroutiTheme.colors.backgroundDefault),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp, end = 16.dp, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = title,
                color = LiroutiTheme.colors.labelDefault,
                style = LiroutiTheme.typography.body2Long.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.fillMaxWidth(),
            )
            DetailRoutineCategoryTabs(
                categories = categories,
                selectedCategory = selectedCategory,
                categoryColors = categoryColors,
                onCategoryClick = onCategoryClick,
            )
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(visibleTodos, key = { it.id }) { todo ->
                    DetailRoutineTodoRow(
                        todo = todo,
                        categoryColor = categoryColors[todo.category],
                        onCameraClick = { onRoutineVerificationClick(todo.id) },
                        onLongClick = { onRoutineColorLongClick(todo.id) },
                    )
                }
                item {
                    Text(
                        text = visibleProgressLabel,
                        color = LiroutiTheme.colors.labelSub,
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
    categories: List<String>,
    selectedCategory: String,
    categoryColors: Map<String, CategoryColor>,
    onCategoryClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(categories) { category ->
            val selected = category == selectedCategory
            val selectedColor = categoryColors[category]?.swatch ?: LiroutiTheme.colors.primaryNormal
            GroupRoutineCategoryChip(
                label = category,
                selected = selected,
                selectedColor = selectedColor,
                onClick = { onCategoryClick(category) },
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun DetailRoutineTodoRow(
    todo: GroupTodoUiModel,
    categoryColor: CategoryColor?,
    onCameraClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(4.dp)
    val routineCategoryColor = (todo.categoryColor ?: categoryColor)?.swatch ?: LiroutiTheme.colors.primaryNormal

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(shape)
            .background(LiroutiTheme.colors.backgroundDefault)
            .border(1.dp, LiroutiTheme.colors.borderAlternative, shape)
            .pointerInput(onLongClick) {
                detectTapGestures(
                    onLongPress = { onLongClick() },
                )
            },
    ) {
        if (!todo.isDone) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(routineCategoryColor),
            )
        }
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = todo.title,
                    color = if (todo.isDone) LiroutiTheme.colors.labelInfo else LiroutiTheme.colors.labelDefault,
                    style = LiroutiTheme.typography.body2LongMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (!todo.isDone) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Text(
                            text = todo.category,
                            color = LiroutiTheme.colors.labelDefault,
                            style = LiroutiTheme.typography.caption,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(10.dp)
                                .background(LiroutiTheme.colors.borderStrong),
                        )
                        Text(
                            text = "마감 ${todo.deadline}",
                            color = LiroutiTheme.colors.labelInfo,
                            style = LiroutiTheme.typography.caption,
                            maxLines = 1,
                        )
                    }
                }
            }
            if (todo.isDone) {
                LiroutiBadge(text = "완료", color = LiroutiBadgeColor.Neutral)
            } else {
                Image(
                    painter = painterResource(id = DesignSystemR.drawable.camera),
                    contentDescription = "루틴 인증하기",
                    modifier = Modifier
                        .size(20.dp)
                        .clickable(onClick = onCameraClick),
                    colorFilter = ColorFilter.tint(LiroutiTheme.colors.labelInfo),
                )
            }
        }
    }
}

@Composable
private fun CertificationCollectionScreen(
    uiState: GroupRoutineUiState,
    onBackClick: () -> Unit,
    onCertificationMemberClick: (Long?) -> Unit,
    onCertificationLikeClick: (Long, Boolean) -> Unit,
    onCertificationDisappointmentClick: (Long, Boolean) -> Unit,
    onCertificationReverifyClick: (CertificationPostUiModel) -> Unit,
    onTabSelected: (AppBottomTab) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(LiroutiTheme.colors.backgroundSecondary),
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
        onDisappointmentClick = onCertificationDisappointmentClick,
        onReverifyClick = onCertificationReverifyClick,
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
            .background(LiroutiTheme.colors.backgroundSecondary),
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
                painter = painterResource(id = DesignSystemR.drawable.default_character),
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(LiroutiTheme.colors.backgroundDefault),
            )
            Spacer(modifier = Modifier.width(8.dp))
        } else if (!isMine) {
            Spacer(modifier = Modifier.width(48.dp))
        }
        if (isMine && time.isNotBlank()) {
            Text(
                text = time,
                color = LiroutiTheme.colors.labelDefault,
                style = LiroutiTheme.typography.caption,
                modifier = Modifier.padding(end = 4.dp, bottom = 2.dp),
            )
        }
        Text(
            text = message,
            color = if (isMine) LiroutiTheme.colors.labelReverse else LiroutiTheme.colors.labelDefault,
            style = LiroutiTheme.typography.body3,
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(if (isMine) LiroutiTheme.colors.primaryNormal else LiroutiTheme.colors.backgroundDefault)
                .padding(horizontal = 16.dp, vertical = 8.dp),
        )
        if (!isMine && time.isNotBlank()) {
            Text(
                text = time,
                color = LiroutiTheme.colors.labelDefault,
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
            .background(LiroutiTheme.colors.backgroundDefault)
            .border(1.dp, LiroutiTheme.colors.borderDefault)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(44.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(LiroutiTheme.colors.backgroundDefault)
                .border(1.dp, LiroutiTheme.colors.borderDefault, RoundedCornerShape(6.dp))
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart,
        ) {
            Text(text = "메세지 보내기", color = LiroutiTheme.colors.labelInfo, style = LiroutiTheme.typography.body3)
        }
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(ChatSendBackground),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = "➤", color = LiroutiTheme.colors.primaryNormal, fontSize = 28.sp)
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
    val isLeader = uiState.isConfirmedOwner

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(LiroutiTheme.colors.backgroundDefault),
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
                    onClick = onInviteCodeCopyClick,
                )
            }
        }

        Button(
            onClick = onLeaveRoomClick,
            shape = RoundedCornerShape(6.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = LiroutiTheme.colors.dangerBase,
                contentColor = LiroutiTheme.colors.labelReverse,
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
    onRoutineColorLongClick: (Long) -> Unit,
    onDoneClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(LiroutiTheme.colors.backgroundDefault),
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
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "\uD568\uAED8\uD560 \uB8E8\uD2F4\uC744 \uCD94\uAC00\uD574 \uBCF4\uC138\uC694",
                            color = LiroutiTheme.colors.labelDefault,
                            style = LiroutiTheme.typography.heading2.copy(fontWeight = FontWeight.Bold),
                        )
                        Text(
                            text = "\uB8E8\uD2F4\uC744 \uB204\uB974\uBA74 \uC138\uBD80 \uC124\uC815\uC744 \uBCC0\uACBD\uD560 \uC218 \uC788\uC5B4\uC694",
                            color = LiroutiTheme.colors.labelInfo,
                            style = LiroutiTheme.typography.body3,
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        CategoryChipRow(
                            categories = uiState.categories,
                            selectedCategory = uiState.selectedCategory,
                            categoryColors = uiState.categoryColors,
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
                        onRoutineColorLongClick = onRoutineColorLongClick,
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(LiroutiTheme.colors.backgroundDefault)
                .navigationBarsPadding()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            DashedRoutineAddButton(onClick = onRoutineAddClick)
            Text(
                text = "총 ${uiState.selectedCreateRoutineCount}개 선택됨",
                color = LiroutiTheme.colors.labelSub,
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
            .background(LiroutiTheme.colors.backgroundDefault),
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
                color = LiroutiTheme.colors.labelDefault,
                style = LiroutiTheme.typography.body3.copy(fontWeight = FontWeight.Bold),
            )
            BasicInputBox(
                value = roomName,
                onValueChange = onRoomNameChange,
                placeholder = "방 이름을 입력해 주세요",
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
            .background(LiroutiTheme.colors.backgroundDefault),
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

/**
 * 그룹 구성원 아바타. [layers]를 받은 순서 그대로 겹쳐 그린다(`GET /api/groups/{groupId}` 응답의
 * avatar.layers) — 서버가 캐릭터·둥지·착장을 이미 겹칠 순서로 계산해서 내려준다.
 * 캐릭터를 하나도 못 열었으면 `CHARACTER`·둥지 레이어가 빠져서 오는데, 그때만 기본 캐릭터 실루엣
 * (design-system의 default_character)으로 대체한다 — feature/home의 AvatarCharacter.kt와 같은
 * 방식이지만, feature 모듈 간 의존을 새로 만들지 않으려고 여기 따로 둔다.
 */
@Composable
private fun MemberAvatarImage(
    layers: List<AvatarLayer>,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        if (!layers.hasCharacterLayer()) {
            Image(
                painter = painterResource(id = DesignSystemR.drawable.default_character),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize(),
            )
        }
        layers.forEach { layer ->
            key(layer.layer) {
                AsyncImage(
                    model = layer.imageUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
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
            .background(LiroutiTheme.colors.backgroundDefault)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        MemberAvatarImage(
            layers = member.layers,
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(LiroutiTheme.colors.backgroundSecondary),
        )
        Text(
            text = member.name,
            color = LiroutiTheme.colors.labelDefault,
            style = LiroutiTheme.typography.body3.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.weight(1f),
        )
        CustomCheckBox(
            state = if (selected) CheckBoxState.B else CheckBoxState.A,
            isCircle = true,
            onClick = onClick,
        )
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
            .background(LiroutiTheme.colors.backgroundDefault),
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
        color = LiroutiTheme.colors.labelSub,
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
            .background(LiroutiTheme.colors.borderAlternative),
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
            .background(LiroutiTheme.colors.backgroundDefault)
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
            color = LiroutiTheme.colors.labelDefault,
            style = LiroutiTheme.typography.body3.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.weight(1f),
        )
        LiroutiChevronRightIcon(
            modifier = Modifier.size(24.dp),
            color = LiroutiTheme.colors.labelDefault,
        )
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
            .background(LiroutiTheme.colors.backgroundDefault)
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
            color = LiroutiTheme.colors.labelDefault,
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
            .background(LiroutiTheme.colors.backgroundDefault)
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
            color = LiroutiTheme.colors.labelDefault,
            style = LiroutiTheme.typography.body3.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.weight(1f),
        )
        Text(
            text = code.ifBlank { "발급 중..." },
            color = LiroutiTheme.colors.labelSub,
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
            .background(LiroutiTheme.colors.backgroundDefault)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = title,
                color = LiroutiTheme.colors.labelDefault,
                style = LiroutiTheme.typography.body3.copy(fontWeight = FontWeight.Bold),
            )
            Text(
                text = description,
                color = LiroutiTheme.colors.labelInfo,
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
            .background(LiroutiTheme.colors.backgroundDefault)
            .navigationBarsPadding()
            .padding(start = 16.dp, end = 16.dp, bottom = 32.dp, top = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Button(
            onClick = onLeftClick,
            shape = RoundedCornerShape(6.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = LiroutiTheme.colors.borderAlternative,
                contentColor = LiroutiTheme.colors.labelDefault,
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
                containerColor = LiroutiTheme.colors.primaryNormal,
                contentColor = LiroutiTheme.colors.labelReverse,
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
            .background(LiroutiTheme.colors.backgroundDefault)
            .clickable(onClick = onClick)
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(LiroutiTheme.colors.borderAlternative),
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
                color = LiroutiTheme.colors.labelDefault,
                style = LiroutiTheme.typography.body2Long.copy(fontWeight = FontWeight.Bold),
            )
            Text(text = streakLabel, color = LiroutiTheme.colors.labelInfo, style = LiroutiTheme.typography.caption)
        }
        LiroutiChevronRightIcon(
            modifier = Modifier.size(24.dp),
            color = LiroutiTheme.colors.labelDefault,
        )
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
            .background(LiroutiTheme.colors.backgroundDefault)
            .padding(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.Top),
        horizontalAlignment = Alignment.Start,
    ) {
        Text(
            text = title,
            color = LiroutiTheme.colors.labelDefault,
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
            TextAction(
                label = "메시지 수정",
                iconRes = DesignSystemR.drawable.edit,
                onClick = onMessageEditClick,
            )
            Box(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .width(1.dp)
                    .height(12.dp)
                    .background(LiroutiTheme.colors.borderDefault),
            )
            TextAction(
                label = "초대코드",
                iconRes = DesignSystemR.drawable.copy,
                onClick = onInviteCodeClick,
            )
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
                    .background(LiroutiTheme.colors.labelInfo)
                    .padding(horizontal = 8.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = member.message.take(8),
                    color = LiroutiTheme.colors.labelReverse,
                    fontSize = 11.sp,
                    maxLines = 1,
                )
            }
            Box(
                modifier = Modifier
                    .requiredSize(56.dp)
                    .clip(CircleShape)
                    .background(LiroutiTheme.colors.backgroundDefault),
                contentAlignment = Alignment.Center,
            ) {
                MemberAvatarImage(
                    layers = member.layers,
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
                color = LiroutiTheme.colors.labelDefault,
                style = LiroutiTheme.typography.body3.copy(fontWeight = FontWeight.Medium),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f, fill = false)
                    .padding(start = if (member.isMe) 4.dp else 0.dp),
            )
            Text(
                text = "🔥${member.streak}",
                color = LiroutiTheme.colors.dangerBase,
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
            color = LiroutiTheme.colors.backgroundDefault,
            modifier = Modifier
                .width(320.dp)
                .then(if (member.isMe) Modifier.height(272.dp) else Modifier),
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    Text(
                        text = "\u00D7",
                        color = LiroutiTheme.colors.labelDefault,
                        fontSize = 24.sp,
                        lineHeight = 24.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .size(24.dp)
                            .semantics { contentDescription = "\uD504\uB85C\uD544 \uB2EB\uAE30" }
                            .clickable(
                                onClickLabel = "\uD504\uB85C\uD544 \uB2EB\uAE30",
                                onClick = onDismissRequest,
                            ),
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(LiroutiTheme.colors.backgroundSecondary),
                        contentAlignment = Alignment.Center,
                    ) {
                        MemberAvatarImage(
                            layers = member.layers,
                            modifier = Modifier.size(60.dp),
                        )
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(5.dp),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Text(
                                text = member.name,
                                color = LiroutiTheme.colors.labelDefault,
                                style = LiroutiTheme.typography.body2Long.copy(fontWeight = FontWeight.Bold),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            if (member.isMe) {
                                StatusBadge(label = "\uB098", completed = false)
                            }
                        }
                        if (member.representativeBadgeName != null || !member.isMe) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                            ) {
                                if (member.representativeBadgeName != null) {
                                    if (member.representativeBadgeImageUrl != null) {
                                        AsyncImage(
                                            model = member.representativeBadgeImageUrl,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp),
                                        )
                                    }
                                    Text(
                                        text = member.representativeBadgeName,
                                        color = RepresentativeBadgeText,
                                        style = LiroutiTheme.typography.caption,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(RepresentativeBadgeBackground)
                                            .padding(horizontal = 8.dp, vertical = 4.dp),
                                    )
                                }
                                if (!member.isMe) {
                                    Text(text = "\uD83D\uDD25${member.streak}", color = LiroutiTheme.colors.dangerBase, fontSize = 10.sp)
                                }
                            }
                        }
                    }
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(LiroutiTheme.colors.backgroundFill)
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    MemberProfileInfoRow(
                        label = "\uC624\uB298 \uB8E8\uD2F4 \uC9C4\uD589\uB3C4",
                        value = "${member.completedCount}/${member.totalCount} \uC644\uB8CC",
                    )
                    MemberProfileInfoRow(label = "\uC5F0\uC18D \uB2EC\uC131", value = "${member.streak}\uC77C \uC9F8")
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        ReactionBadge(text = "\uC88B\uC544\uC694 ${member.totalLikeCount}")
                        ReactionBadge(text = "\uC544\uC26C\uC6CC\uC694 ${member.totalDisappointmentCount}")
                        ReactionBadge(text = "\uCFE1\uCFE1 ${member.pokeCount}")
                    }
                }
                if (!member.isMe) {
                    PrimaryButton(
                        text = "\uCFE1\uCFE1 \uCC14\uB7EC\uBCF4\uAE30",
                        enabled = true,
                        onClick = onPokeClick,
                    )
                }
                if (!member.isMe && canKick) {
                    Text(
                        text = "\uB0B4\uBCF4\uB0B4\uAE30",
                        color = LiroutiTheme.colors.dangerBase,
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
            color = LiroutiTheme.colors.labelSub,
            style = LiroutiTheme.typography.caption.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.width(93.dp),
        )
        Text(
            text = value,
            color = LiroutiTheme.colors.labelDefault,
            style = LiroutiTheme.typography.body3,
        )
    }
}

@Composable
private fun ReactionBadge(text: String) {
    Text(
        text = text,
        color = LiroutiTheme.colors.labelInfo,
        style = LiroutiTheme.typography.caption,
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(LiroutiTheme.colors.borderSub)
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
            .background(LiroutiTheme.colors.backgroundDefault)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = "그룹 루틴",
            color = LiroutiTheme.colors.labelDefault,
            style = LiroutiTheme.typography.body2Long.copy(fontWeight = FontWeight.Bold),
        )
        todos.forEach { todo ->
            TodoRow(todo = todo, onCheckedChange = { checked -> onTodoCheckedChange(todo.id, checked) })
        }
        HorizontalDivider(color = LiroutiTheme.colors.borderDefault)
        Text(
            text = progressLabel,
            color = LiroutiTheme.colors.labelSub,
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
            enabled = false,
        )
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = todo.title,
                color = if (todo.isDone) LiroutiTheme.colors.labelInfo else LiroutiTheme.colors.labelDefault,
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
                        color = LiroutiTheme.colors.labelInfo,
                        style = LiroutiTheme.typography.caption,
                        maxLines = 1,
                    )
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(10.dp)
                            .background(LiroutiTheme.colors.borderDefault),
                    )
                    Text(
                        text = todo.category,
                        color = LiroutiTheme.colors.labelInfo,
                        style = LiroutiTheme.typography.caption,
                        maxLines = 1,
                    )
                }
            }
        }
        if (todo.isDone) {
            Text(
                text = "완료",
                color = LiroutiTheme.colors.completeText,
                style = LiroutiTheme.typography.caption,
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(LiroutiTheme.colors.completeBackground)
                    .padding(horizontal = 7.dp, vertical = 3.dp),
            )
        } else {
            Text(text = "▣", color = LiroutiTheme.colors.labelInfo, fontSize = 18.sp)
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
    onDisappointmentClick: (Long, Boolean) -> Unit,
    onReverifyClick: (CertificationPostUiModel) -> Unit,
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
                onReverifyClick = { onReverifyClick(post) },
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
        color = if (selected) LiroutiTheme.colors.labelReverse else LiroutiTheme.colors.labelDefault,
        style = LiroutiTheme.typography.body3.copy(fontWeight = FontWeight.Medium),
        modifier = Modifier
            .height(32.dp)
            .clip(RoundedCornerShape(40.dp))
            .background(if (selected) LiroutiTheme.colors.primaryNormal else LiroutiTheme.colors.backgroundDefault)
            .border(
                width = 1.dp,
                color = if (selected) LiroutiTheme.colors.primaryNormal else LiroutiTheme.colors.borderDefault,
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
        color = if (selected) LiroutiTheme.colors.labelReverse else LiroutiTheme.colors.labelDefault,
        style = LiroutiTheme.typography.body3.copy(fontWeight = FontWeight.Medium),
        modifier = Modifier
            .clip(RoundedCornerShape(40.dp))
            .background(if (selected) LiroutiTheme.colors.primaryNormal else LiroutiTheme.colors.backgroundSecondary)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 7.dp),
    )
}

@Composable
private fun CertificationPostItem(
    post: CertificationPostUiModel,
    onLikeClick: () -> Unit,
    onReverifyClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var isReverifyMenuVisible by remember(post.id) { mutableStateOf(false) }
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = DesignSystemR.drawable.default_character),
                contentDescription = null,
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = post.userName,
                    color = LiroutiTheme.colors.labelDefault,
                    style = LiroutiTheme.typography.body2Long.copy(fontWeight = FontWeight.Bold),
                )
                Text(
                    text = post.timeAgo,
                    color = LiroutiTheme.colors.labelInfo,
                    style = LiroutiTheme.typography.caption,
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            if (post.isMine) {
                Box {
                    Text(
                        text = "...",
                        color = LiroutiTheme.colors.labelInfo,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .size(40.dp)
                            .clickable(
                                onClickLabel = "인증 메뉴 열기",
                                onClick = { isReverifyMenuVisible = true },
                            )
                            .padding(bottom = 8.dp),
                    )
                    DropdownMenu(
                        expanded = isReverifyMenuVisible,
                        onDismissRequest = { isReverifyMenuVisible = false },
                    ) {
                        DropdownMenuItem(
                            text = { Text("재인증") },
                            onClick = {
                                isReverifyMenuVisible = false
                                onReverifyClick()
                            },
                        )
                    }
                }
            } else {
                Text(text = "...", color = LiroutiTheme.colors.labelInfo, fontSize = 18.sp)
            }
        }
        Text(
            text = post.body,
            color = LiroutiTheme.colors.labelDefault,
            style = LiroutiTheme.typography.body2Long,
        )
        AsyncImage(
            model = post.imageUrl,
            error = painterResource(id = R.drawable.img_group_routine_cert_water),
            fallback = painterResource(id = R.drawable.img_group_routine_cert_water),
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
                Icon(
                    painter = painterResource(
                        id = if (post.isLiked) {
                            DesignSystemR.drawable.favorite__filled
                        } else {
                            DesignSystemR.drawable.favorite
                        },
                    ),
                    contentDescription = if (post.isLiked) "좋아요 취소" else "좋아요",
                    tint = LiroutiTheme.colors.labelDefault,
                    modifier = Modifier.size(16.dp),
                )
                Text(
                    text = post.likeCount.toString(),
                    color = LiroutiTheme.colors.labelDefault,
                    style = LiroutiTheme.typography.caption.copy(fontWeight = FontWeight.Medium),
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = if (post.isMine) "내 인증" else "멤버 인증",
                color = LiroutiTheme.colors.labelInfo,
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
            .background(LiroutiTheme.colors.backgroundSecondary),
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
    @DrawableRes iconRes: Int? = null,
    onClick: () -> Unit = {},
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .clickable(onClick = onClick)
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        if (iconRes != null) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                tint = LiroutiTheme.colors.labelSub,
                modifier = Modifier.size(18.dp),
            )
        }
        Text(
            text = label,
            color = LiroutiTheme.colors.labelSub,
            style = LiroutiTheme.typography.body2Long,
        )
    }
}

@Composable
private fun GroupRoutineCloseButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = LiroutiTheme.colors.labelDefault,
    contentDescription: String = "닫기",
) {
    Icon(
        painter = painterResource(id = R.drawable.ic_group_routine_close),
        contentDescription = contentDescription,
        tint = tint,
        modifier = modifier.clickable(
            onClickLabel = contentDescription,
            onClick = onClick,
        ),
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
            .background(LiroutiTheme.colors.backgroundDefault)
            .statusBarsPadding(),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .padding(horizontal = 16.dp),
        ) {
            if (showBack) {
                LiroutiChevronLeftIcon(
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .size(24.dp)
                        .clickable(onClick = onBackClick),
                    color = LiroutiTheme.colors.labelDefault,
                )
            }
            Text(
                text = title,
                color = LiroutiTheme.colors.labelDefault,
                style = LiroutiTheme.typography.heading2.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.align(Alignment.Center),
            )
            if (showAdd) {
                Image(
                    painter = painterResource(id = DesignSystemR.drawable.add__alt),
                      contentDescription = "방 만들기",
                      modifier = Modifier
                          .align(Alignment.CenterEnd)
                          .size(20.dp)
                          .clickable(onClick = onAddClick),
                    colorFilter = ColorFilter.tint(LiroutiTheme.colors.labelStrong),
                )
            }
            if (showClose) {
                GroupRoutineCloseButton(
                    onClick = onCloseClick,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .size(22.dp),
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
                    .background(LiroutiTheme.colors.dangerBase)
                    .padding(horizontal = if (badgeCount >= 10) 4.dp else 0.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = badgeCount.toString(),
                    color = LiroutiTheme.colors.labelReverse,
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
                    .background(LiroutiTheme.colors.borderDefault),
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
            onDismissJoinPreviewDialog = {},
            onJoinPreviewConfirmClick = {},
            onCreateRoutineOptionClick = {},
            onCreateRoutineSelectAllClick = {},
            onCategoryClick = {},
            onCategoryAddClick = {},
            onDismissCategorySheet = {},
            onCategoryInputChange = {},
            onCategoryConfirmClick = {},
            onRoutineAddClick = {},
            onRoutineSettingClick = {},
            onRoutineColorLongClick = {},
            onDismissRoutineColorSheet = {},
            onRoutineColorSelected = {},
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
            onCertificationTabClick = {},
            onCertificationMemberClick = {},
            onCertificationSummaryClick = {},
            onCertificationLikeClick = { _, _ -> },
            onCertificationDisappointmentClick = { _, _ -> },
            onDismissNewCertificationDialog = {},
            onMemberClick = {},
            onDismissMemberDialog = {},
            onChatClick = {},
            onChatMessageChange = {},
            onChatSendClick = {},
            onChatEmojiSelected = {},
            onReplySwipe = {},
            onReplyCancelClick = {},
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
            onDismissJoinPreviewDialog = {},
            onJoinPreviewConfirmClick = {},
            onCreateRoutineOptionClick = {},
            onCreateRoutineSelectAllClick = {},
            onCategoryClick = {},
            onCategoryAddClick = {},
            onDismissCategorySheet = {},
            onCategoryInputChange = {},
            onCategoryConfirmClick = {},
            onRoutineAddClick = {},
            onRoutineSettingClick = {},
            onRoutineColorLongClick = {},
            onDismissRoutineColorSheet = {},
            onRoutineColorSelected = {},
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
            onCertificationTabClick = {},
            onCertificationMemberClick = {},
            onCertificationSummaryClick = {},
            onCertificationLikeClick = { _, _ -> },
            onCertificationDisappointmentClick = { _, _ -> },
            onDismissNewCertificationDialog = {},
            onMemberClick = {},
            onDismissMemberDialog = {},
            onChatClick = {},
            onChatMessageChange = {},
            onChatSendClick = {},
            onChatEmojiSelected = {},
            onReplySwipe = {},
            onReplyCancelClick = {},
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

