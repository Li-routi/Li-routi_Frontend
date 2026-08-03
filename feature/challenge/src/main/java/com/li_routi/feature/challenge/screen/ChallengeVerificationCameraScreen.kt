package com.li_routi.feature.challenge.screen

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.li_routi.core.common.ui.camera.LiroutiCameraPreview
import com.li_routi.core.common.ui.camera.captureLiroutiCameraPhoto
import com.li_routi.core.designsystem.R
import com.li_routi.core.designsystem.theme.LiroutiFrontendTheme
import com.li_routi.core.designsystem.theme.LiroutiTheme

/**
 * 챌린지 "인증하기" 카메라 화면. [feature.home]의 RoutineAuthCameraScreen과 같은 구조(CameraX
 * 프리뷰/촬영)를 챌린지 인증 흐름에 맞춰 단순화한 버전 — 루틴 선택 없이 사진 한 장만 찍는다.
 */
@Composable
fun ChallengeVerificationCameraScreen(
    onBackClick: () -> Unit,
    onCaptureSuccess: (Uri) -> Unit,
    modifier: Modifier = Modifier,
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

    DisposableEffect(lifecycleOwner, context) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                hasCameraPermission = context.isCameraPermissionGranted()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    var lensFacing by remember { mutableIntStateOf(CameraSelector.LENS_FACING_BACK) }
    var isTorchOn by remember { mutableStateOf(false) }
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var isCapturing by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = LiroutiTheme.colors.backgroundDefault,
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
                            .clickable(onClick = onBackClick),
                        colorFilter = ColorFilter.tint(LiroutiTheme.colors.labelStrong),
                    )
                    Text(
                        text = "인증하기",
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
                    onClick = {
                        lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK) {
                            CameraSelector.LENS_FACING_FRONT
                        } else {
                            CameraSelector.LENS_FACING_BACK
                        }
                        isTorchOn = false
                    },
                )
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clickable(enabled = !isCapturing && hasCameraPermission) {
                            val capture = imageCapture ?: return@clickable
                            isCapturing = true
                            captureLiroutiCameraPhoto(
                                context = context,
                                imageCapture = capture,
                                executor = ContextCompat.getMainExecutor(context),
                                onSuccess = { uri ->
                                    isCapturing = false
                                    onCaptureSuccess(uri)
                                },
                                onError = {
                                    isCapturing = false
                                },
                                filePrefix = "challenge_verification",
                            )
                        },
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
                    label = if (isTorchOn) "켜짐" else "플래시",
                    onClick = { isTorchOn = !isTorchOn },
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
                LiroutiCameraPreview(
                    lensFacing = lensFacing,
                    flashMode = ImageCapture.FLASH_MODE_OFF,
                    isActive = true,
                    torchEnabled = isTorchOn,
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
            style = LiroutiTheme.typography.caption,
            color = LiroutiTheme.colors.labelStrong,
        )
    }
}

private fun Context.isCameraPermissionGranted(): Boolean =
    ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
        PackageManager.PERMISSION_GRANTED

@Preview(showBackground = true)
@Composable
private fun ChallengeVerificationCameraScreenPreview() {
    LiroutiFrontendTheme {
        ChallengeVerificationCameraScreen(
            onBackClick = {},
            onCaptureSuccess = {},
        )
    }
}
