package com.li_routi.feature.home.screen

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.li_routi.core.common.ui.camera.LiroutiCameraPreview
import com.li_routi.core.common.ui.camera.captureLiroutiCameraPhoto
import com.li_routi.core.designsystem.R
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme
import com.li_routi.feature.home.navigation.RoutineAuthCameraScreenActions

/**
 * "루틴 인증하기" 카메라 화면 (Figma node `2156:32485`).
 *
 * CameraX 프리뷰/촬영을 연동한다. [isCameraActive]가 false이면 카메라를 언바인딩한다.
 */
@Composable
fun RoutineAuthCameraScreen(
    actions: RoutineAuthCameraScreenActions,
    modifier: Modifier = Modifier,
    isCameraActive: Boolean = true,
) {
    // Preview에는 ActivityResultRegistry가 없어 런처 등록 시 크래시 난다.
    if (LocalInspectionMode.current) {
        RoutineAuthCameraLayout(
            actions = actions,
            hasCameraPermission = true,
            isTorchOn = false,
            isCapturing = false,
            onToggleLens = {},
            onToggleFlash = {},
            onShutterClick = {},
            cameraContent = {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black),
                )
            },
            modifier = modifier,
        )
        return
    }

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var hasCameraPermission by remember {
        mutableStateOf(context.isCameraPermissionGranted())
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        hasCameraPermission = granted
    }

    // 설정에서 권한을 바꾼 뒤 복귀해도 상태가 갱신되도록 ON_RESUME마다 다시 확인한다.
    DisposableEffect(lifecycleOwner, context) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                hasCameraPermission = context.isCameraPermissionGranted()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(isCameraActive) {
        if (isCameraActive && !hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    var lensFacing by remember { mutableIntStateOf(CameraSelector.LENS_FACING_BACK) }
    var isTorchOn by remember { mutableStateOf(false) }
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var isCapturing by remember { mutableStateOf(false) }

    RoutineAuthCameraLayout(
        actions = actions,
        hasCameraPermission = hasCameraPermission,
        isTorchOn = isTorchOn,
        isCapturing = isCapturing,
        onToggleLens = {
            lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK) {
                CameraSelector.LENS_FACING_FRONT
            } else {
                CameraSelector.LENS_FACING_BACK
            }
            // 전면 등 플래시 유닛 없는 렌즈로 바꿀 때 손전등 상태 초기화.
            isTorchOn = false
        },
        onToggleFlash = {
            isTorchOn = !isTorchOn
        },
        onShutterClick = {
            val capture = imageCapture
            if (capture == null) return@RoutineAuthCameraLayout
            isCapturing = true
            captureLiroutiCameraPhoto(
                context = context,
                imageCapture = capture,
                executor = ContextCompat.getMainExecutor(context),
                onSuccess = { uri ->
                    isCapturing = false
                    actions.onCaptureSuccess(uri)
                },
                onError = { _: ImageCaptureException ->
                    isCapturing = false
                },
                filePrefix = "routine_auth",
            )
        },
        onRequestPermission = {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        },
        cameraContent = {
            LiroutiCameraPreview(
                lensFacing = lensFacing,
                flashMode = ImageCapture.FLASH_MODE_OFF,
                isActive = isCameraActive,
                torchEnabled = isTorchOn,
                onImageCaptureReady = { capture: ImageCapture? ->
                    imageCapture = capture
                },
                modifier = Modifier.fillMaxSize(),
            )
        },
        modifier = modifier,
    )
}

// Figma node 5562:14552(Subtract): 360x800 기준 프레임에서 상단 0–94dp(94/800), 하단
// 614–800dp(186/800) 구간이 최종 인증 사진 크롭 시 잘려나간다. 실제 화면 높이에 이 비율을
// 곱해 "나중에 잘리는 영역"을 촬영 전에 스크림으로 미리 보여준다.
private const val CropIndicatorTopFraction = 94f / 800f
private const val CropIndicatorBottomFraction = 186f / 800f

// Figma `backdrop-blur(5px)` + rgba(93,93,93,0.6) — minSdk 24라 실제 backdrop blur(API 31+)를
// 못 써서 ChatBox.ChatDateDividerBackground와 동일한 값의 반투명 단색으로 대체한다.
private val HintPillScrimColor = Color(0xFF5D5D5D).copy(alpha = 0.60f)
private val HintPillShape = RoundedCornerShape(percent = 50)

/**
 * Figma node `4734:42024`/`5562:14547`: 프리뷰가 화면 전체(상태바/내비게이션 바 영역까지)를 꽉
 * 채우고, 닫기·안내 문구·하단 컨트롤은 그 위에 얹힌 오버레이다 — 흰 배경의 별도 상단/하단 바가
 * 아니다. 상/하단에는 실제 인증 사진 크롭 시 잘려나갈 영역을 미리 보여주는 스크림을 깔아 오버레이
 * 가독성과 크롭 안내를 함께 담당한다. minSdk 24라 Figma의 backdrop blur는 못 쓰고 반투명 단색으로
 * 대체했다(다른 화면의 blur→scrim 대체 전례와 동일). 아이콘·텍스트는 흰색(labelReverse)을 쓴다.
 */
@Composable
private fun RoutineAuthCameraLayout(
    actions: RoutineAuthCameraScreenActions,
    hasCameraPermission: Boolean,
    isTorchOn: Boolean,
    isCapturing: Boolean,
    onToggleLens: () -> Unit,
    onToggleFlash: () -> Unit,
    onShutterClick: () -> Unit,
    cameraContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    onRequestPermission: () -> Unit = {},
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black),
    ) {
        val topBandHeight = maxHeight * CropIndicatorTopFraction
        val bottomBandHeight = maxHeight * CropIndicatorBottomFraction

        if (hasCameraPermission) {
            cameraContent()
        } else {
            CameraPermissionDenied(
                onRequestPermission = onRequestPermission,
                modifier = Modifier.fillMaxSize(),
            )
        }

        // 상단 크롭 아웃 스크림 — 닫기 버튼 가독성 확보 + 잘려나갈 영역 안내를 겸한다.
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .height(topBandHeight)
                .background(LiroutiTheme.colors.dimmerDefault),
        )
        // 하단 크롭 아웃 스크림. 아래 Column(컨트롤)보다 먼저 그려 뒤에 깔리게 한다.
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(bottomBandHeight)
                .background(LiroutiTheme.colors.dimmerDefault),
        )
        Image(
            painter = painterResource(id = R.drawable.close),
            contentDescription = "닫기",
            // 터치 영역은 접근성 최소 권장 크기(48dp)로 확보하고, 안쪽 padding으로 시각적 아이콘
            // 크기(28dp)는 그대로 유지한다.
            modifier = Modifier
                .align(Alignment.TopEnd)
                .statusBarsPadding()
                .padding(horizontal = 6.dp)
                .size(48.dp)
                .clickable(onClick = actions::onBackClick)
                .padding(10.dp),
            colorFilter = ColorFilter.tint(LiroutiTheme.colors.labelReverse),
        )

        // 안내 문구 + 컨트롤(전환/셔터/플래시). 배경은 위 하단 스크림 Box가 담당한다.
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(top = 24.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (hasCameraPermission) {
                Text(
                    text = "가로로 촬영해 주세요",
                    style = LiroutiTheme.typography.body2LongMedium,
                    // 어두운 프리뷰 위에서도 읽히도록 reverse + scrim
                    color = LiroutiTheme.colors.labelReverse,
                    modifier = Modifier
                        .background(
                            color = HintPillScrimColor,
                            shape = HintPillShape,
                        )
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                )
                Spacer(modifier = Modifier.height(20.dp))
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CameraControlAction(
                    iconResId = R.drawable.cameraswitch,
                    label = "전환",
                    onClick = onToggleLens,
                )
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clickable(
                            enabled = !isCapturing && hasCameraPermission,
                            onClick = onShutterClick,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.shutter__outer),
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                    )
                    Image(
                        painter = painterResource(id = R.drawable.shutter),
                        contentDescription = "촬영",
                        modifier = Modifier.size(52.dp),
                    )
                }
                CameraControlAction(
                    iconResId = R.drawable.flash,
                    // 폭 고정 + 짧은 라벨로 토글 시 셔터가 밀리지 않게 한다.
                    label = if (isTorchOn) "켜짐" else "플래시",
                    onClick = onToggleFlash,
                )
            }
        }
    }
}

@Composable
private fun CameraPermissionDenied(
    onRequestPermission: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.background(Color.Black),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(24.dp),
        ) {
            Text(
                text = "카메라 권한이 필요해요",
                style = LiroutiTheme.typography.body2,
                color = LiroutiTheme.colors.labelReverse,
                textAlign = TextAlign.Center,
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(LiroutiTheme.colors.primaryNormal)
                    .clickable(onClick = onRequestPermission)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
            ) {
                Text(
                    text = "권한 허용",
                    style = LiroutiTheme.typography.body2,
                    color = LiroutiTheme.colors.labelReverse,
                )
            }
        }
    }
}

@Composable
private fun CameraControlAction(
    @androidx.annotation.DrawableRes iconResId: Int,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        // 좌우 컨트롤 폭을 맞춰 SpaceBetween에서 셔터가 한쪽으로 밀리지 않게 한다.
        modifier = modifier
            .width(64.dp)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(id = iconResId),
            contentDescription = label,
            modifier = Modifier.size(24.dp),
            // 카메라 프리뷰 위에 얹히는 오버레이라 흰색(labelReverse)을 쓴다.
            colorFilter = ColorFilter.tint(LiroutiTheme.colors.labelReverse),
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            // Figma Body4 13/16
            style = LiroutiTheme.typography.body3Regular.copy(lineHeight = 16.sp),
            color = LiroutiTheme.colors.labelReverse,
            textAlign = TextAlign.Center,
            maxLines = 1,
        )
    }
}

private fun Context.isCameraPermissionGranted(): Boolean =
    ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
        PackageManager.PERMISSION_GRANTED

private object PreviewRoutineAuthCameraScreenActions : RoutineAuthCameraScreenActions {
    override fun onBackClick() = Unit
    override fun onCaptureSuccess(photoUri: Uri) = Unit
}

@Preview(showBackground = true)
@Composable
private fun RoutineAuthCameraScreenPreview() {
    LiroutiFrontendTheme {
        RoutineAuthCameraScreen(
            actions = PreviewRoutineAuthCameraScreenActions,
            isCameraActive = false,
        )
    }
}
