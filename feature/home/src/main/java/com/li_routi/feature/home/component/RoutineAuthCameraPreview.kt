package com.li_routi.feature.home.component

import android.content.Context
import android.net.Uri
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.core.CameraSelector
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
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
 * CameraX Preview + ImageCapture 바인딩.
 *
 * [isActive]가 false이면 카메라 언바인딩(페이저에서 홈으로 돌아갔을 때 자원 해제).
 * 셔터는 부모에서 보관하는 [ImageCapture]로 [takeRoutineAuthPhoto]를 호출한다.
 */
@Composable
fun RoutineAuthCameraPreview(
    lensFacing: Int,
    flashMode: Int,
    isActive: Boolean,
    onImageCaptureReady: (ImageCapture?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }

    AndroidView(
        factory = { previewView },
        modifier = modifier.fillMaxSize(),
    )

    LaunchedEffect(lensFacing, flashMode, isActive, lifecycleOwner) {
        if (!isActive) {
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
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                selector,
                preview,
                imageCapture,
            )
            onImageCaptureReady(imageCapture)
            try {
                awaitCancellation()
            } finally {
                onImageCaptureReady(null)
                cameraProvider.unbindAll()
            }
        } catch (_: Exception) {
            onImageCaptureReady(null)
        }
    }
}

/** 캐시 디렉터리에 JPEG로 저장하고 [Uri]를 반환한다. */
fun takeRoutineAuthPhoto(
    context: Context,
    imageCapture: ImageCapture,
    executor: Executor,
    onSuccess: (Uri) -> Unit,
    onError: (ImageCaptureException) -> Unit,
) {
    val photoFile = File(
        context.cacheDir,
        "routine_auth_${System.currentTimeMillis()}.jpg",
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
