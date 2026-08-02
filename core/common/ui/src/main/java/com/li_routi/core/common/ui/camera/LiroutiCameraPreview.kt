package com.li_routi.core.common.ui.camera

import android.content.Context
import android.net.Uri
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import java.io.File
import java.util.concurrent.Executor
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlinx.coroutines.awaitCancellation

/**
 * CameraX Preview + ImageCapture 바인딩. 루틴/그룹 루틴/챌린지 인증 촬영 화면에서 공용으로 쓴다.
 *
 * [isActive]가 false이면 카메라 언바인딩(페이저 등에서 화면을 벗어났을 때 자원 해제).
 * [torchEnabled]는 재바인딩 없이 [CameraControl.enableTorch]로 상시 손전등을 켠다.
 * [flashMode]는 바인딩된 [ImageCapture]에 setter로만 반영해 토글 시 화면이 밀리지 않게 한다.
 * 셔터는 호출부에서 보관하는 [ImageCapture]로 [captureLiroutiCameraPhoto]를 호출한다.
 */
@Composable
fun LiroutiCameraPreview(
    lensFacing: Int,
    flashMode: Int,
    isActive: Boolean,
    onImageCaptureReady: (ImageCapture?) -> Unit,
    modifier: Modifier = Modifier,
    torchEnabled: Boolean = false,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }
    var boundCamera by remember { mutableStateOf<Camera?>(null) }
    var boundImageCapture by remember { mutableStateOf<ImageCapture?>(null) }

    AndroidView(
        factory = { previewView },
        modifier = modifier.fillMaxSize(),
    )

    // lens/active만 재바인딩. flash·torch 토글은 아래에서 setter로 처리한다.
    LaunchedEffect(lensFacing, isActive, lifecycleOwner) {
        if (!isActive) {
            boundCamera = null
            boundImageCapture = null
            onImageCaptureReady(null)
            runCatching { context.awaitCameraProvider().unbindAll() }
            return@LaunchedEffect
        }

        val cameraProvider = context.awaitCameraProvider()
        val preview = Preview.Builder().build().also {
            it.surfaceProvider = previewView.surfaceProvider
        }
        val imageCapture = ImageCapture.Builder()
            .setFlashMode(flashMode)
            .build()
        val selector = CameraSelector.Builder()
            .requireLensFacing(lensFacing)
            .build()

        try {
            cameraProvider.unbindAll()
            val camera = cameraProvider.bindToLifecycle(
                lifecycleOwner,
                selector,
                preview,
                imageCapture,
            )
            boundCamera = camera
            boundImageCapture = imageCapture
            onImageCaptureReady(imageCapture)
            try {
                awaitCancellation()
            } finally {
                boundCamera = null
                boundImageCapture = null
                onImageCaptureReady(null)
                cameraProvider.unbindAll()
            }
        } catch (_: Exception) {
            boundCamera = null
            boundImageCapture = null
            onImageCaptureReady(null)
        }
    }

    LaunchedEffect(flashMode, boundImageCapture) {
        boundImageCapture?.flashMode = flashMode
    }

    LaunchedEffect(torchEnabled, boundCamera) {
        val camera = boundCamera ?: return@LaunchedEffect
        if (!camera.cameraInfo.hasFlashUnit()) return@LaunchedEffect
        runCatching { camera.cameraControl.enableTorch(torchEnabled) }
    }
}

/** 캐시 디렉터리에 JPEG로 저장하고 [Uri]를 반환한다. [filePrefix]로 호출부별 임시 파일명을 구분한다. */
fun captureLiroutiCameraPhoto(
    context: Context,
    imageCapture: ImageCapture,
    executor: Executor,
    onSuccess: (Uri) -> Unit,
    onError: (ImageCaptureException) -> Unit,
    filePrefix: String = "lirouti_auth",
) {
    val photoFile = File(
        context.cacheDir,
        "${filePrefix}_${System.currentTimeMillis()}.jpg",
    )
    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
    imageCapture.takePicture(
        outputOptions,
        executor,
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                val uri = outputFileResults.savedUri ?: Uri.fromFile(photoFile)
                onSuccess(uri)
            }

            override fun onError(exception: ImageCaptureException) {
                onError(exception)
            }
        },
    )
}

private suspend fun Context.awaitCameraProvider(): ProcessCameraProvider =
    suspendCoroutine { continuation ->
        val future = ProcessCameraProvider.getInstance(this)
        future.addListener(
            { continuation.resume(future.get()) },
            ContextCompat.getMainExecutor(this),
        )
    }
