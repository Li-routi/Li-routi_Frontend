package com.li_routi.feature.home.screen

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.li_routi.core.designsystem.component.DsPlaceholder
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme
import com.li_routi.feature.home.component.RoutineAuthCameraPreview
import com.li_routi.feature.home.component.takeRoutineAuthPhoto
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
                    DsPlaceholder(
                        componentName = "Icon/chevron--left",
                        modifier = Modifier
                            .size(20.dp)
                            .clickable(onClick = actions::onBackClick),
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
                    iconComponentName = "Icon/refresh",
                    label = "전환",
                    onClick = {
                        lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK) {
                            CameraSelector.LENS_FACING_FRONT
                        } else {
                            CameraSelector.LENS_FACING_BACK
                        }
                    },
                )
                DsPlaceholder(
                    componentName = "Button_Filled/shutter",
                    shape = CircleShape,
                    modifier = Modifier
                        .size(64.dp)
                        .clickable(enabled = !isCapturing && hasCameraPermission) {
                            val capture = imageCapture ?: return@clickable
                            isCapturing = true
                            takeRoutineAuthPhoto(
                                context = context,
                                imageCapture = capture,
                                executor = ContextCompat.getMainExecutor(context),
                                onSuccess = { uri ->
                                    isCapturing = false
                                    actions.onCaptureSuccess(uri)
                                },
                                onError = {
                                    isCapturing = false
                                },
                            )
                        },
                )
                CameraControlAction(
                    iconComponentName = "Icon/flash--filled",
                    label = if (flashMode == ImageCapture.FLASH_MODE_ON) "플래시 켜짐" else "플래시",
                    onClick = {
                        flashMode = if (flashMode == ImageCapture.FLASH_MODE_OFF) {
                            ImageCapture.FLASH_MODE_ON
                        } else {
                            ImageCapture.FLASH_MODE_OFF
                        }
                    },
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
                RoutineAuthCameraPreview(
                    lensFacing = lensFacing,
                    flashMode = flashMode,
                    isActive = isCameraActive,
                    onImageCaptureReady = { imageCapture = it },
                    modifier = Modifier.fillMaxSize(),
                )
            } else {
                CameraPermissionDenied(
                    onRequestPermission = {
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    },
                    modifier = Modifier.fillMaxSize(),
                )
            }
            Text(
                text = "가로로 촬영해 주세요",
                style = LiroutiTheme.typography.body2,
                color = LiroutiTheme.colors.labelReverse,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp),
            )
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
    iconComponentName: String,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        DsPlaceholder(
            componentName = iconComponentName,
            modifier = Modifier.size(24.dp),
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = LiroutiTheme.typography.caption,
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
