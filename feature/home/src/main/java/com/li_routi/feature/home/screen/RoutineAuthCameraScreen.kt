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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
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
            flashMode = ImageCapture.FLASH_MODE_OFF,
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
    var flashMode by remember { mutableIntStateOf(ImageCapture.FLASH_MODE_OFF) }
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var isCapturing by remember { mutableStateOf(false) }

    RoutineAuthCameraLayout(
        actions = actions,
        hasCameraPermission = hasCameraPermission,
        flashMode = flashMode,
        isCapturing = isCapturing,
        onToggleLens = {
            lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK) {
                CameraSelector.LENS_FACING_FRONT
            } else {
                CameraSelector.LENS_FACING_BACK
            }
        },
        onToggleFlash = {
            flashMode = if (flashMode == ImageCapture.FLASH_MODE_OFF) {
                ImageCapture.FLASH_MODE_ON
            } else {
                ImageCapture.FLASH_MODE_OFF
            }
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
                flashMode = flashMode,
                isActive = isCameraActive,
                onImageCaptureReady = { capture: ImageCapture? ->
                    imageCapture = capture
                },
                modifier = Modifier.fillMaxSize(),
            )
        },
        modifier = modifier,
    )
}

@Composable
private fun RoutineAuthCameraLayout(
    actions: RoutineAuthCameraScreenActions,
    hasCameraPermission: Boolean,
    flashMode: Int,
    isCapturing: Boolean,
    onToggleLens: () -> Unit,
    onToggleFlash: () -> Unit,
    onShutterClick: () -> Unit,
    cameraContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    onRequestPermission: () -> Unit = {},
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = LiroutiTheme.colors.backgroundDefault,
        // topBar/bottomBar에서 inset을 직접 처리. 기본 safeDrawing과 중복되면 타이틀이 프리뷰를 침범한다.
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(LiroutiTheme.colors.backgroundDefault)
                    .statusBarsPadding(),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.chevron__left),
                        contentDescription = "뒤로가기",
                        modifier = Modifier
                            .size(20.dp)
                            .clickable(onClick = actions::onBackClick),
                        colorFilter = ColorFilter.tint(LiroutiTheme.colors.labelStrong),
                    )
                    Text(
                        text = "루틴 인증하기",
                        style = LiroutiTheme.typography.heading2,
                        color = LiroutiTheme.colors.labelStrong,
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 20.dp),
                        textAlign = TextAlign.Center,
                    )
                }
            }
        },
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(LiroutiTheme.colors.backgroundDefault)
                    .navigationBarsPadding()
                    .padding(horizontal = 40.dp, vertical = 24.dp),
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
                    label = if (flashMode == ImageCapture.FLASH_MODE_ON) "플래시 켜짐" else "플래시",
                    onClick = onToggleFlash,
                )
            }
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            if (hasCameraPermission) {
                cameraContent()
            } else {
                CameraPermissionDenied(
                    onRequestPermission = onRequestPermission,
                    modifier = Modifier.fillMaxSize(),
                )
            }
            if (hasCameraPermission) {
                Text(
                    text = "가로로 촬영해 주세요",
                    style = LiroutiTheme.typography.body2LongMedium,
                    // 어두운 프리뷰 위에서도 읽히도록 reverse + scrim
                    color = LiroutiTheme.colors.labelReverse,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 16.dp)
                        .background(
                            color = Color.Black.copy(alpha = 0.45f),
                            shape = RoundedCornerShape(8.dp),
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp),
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
        modifier = modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(id = iconResId),
            contentDescription = label,
            modifier = Modifier.size(24.dp),
            colorFilter = ColorFilter.tint(LiroutiTheme.colors.labelStrong),
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            // Figma Body4 13/16
            style = LiroutiTheme.typography.body3Regular.copy(lineHeight = 16.sp),
            color = LiroutiTheme.colors.labelStrong,
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
