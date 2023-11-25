package com.caucapstone.cameratest

import android.graphics.Bitmap
import android.graphics.Matrix
import android.widget.ImageView
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.core.app.ComponentActivity
import kotlinx.coroutines.CoroutineScope
import java.util.concurrent.Executors



fun CameraWithImageAnalysis(
    activity: ComponentActivity,
    coroutineScope: CoroutineScope,
    cameraSelector: CameraSelector,
    previewView: PreviewView,
    filterImageView: ImageView
) {
    val cameraProviderFuture = ProcessCameraProvider.getInstance(activity)

    val imageAnalyzerFilters = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)  //이미지 받는 값 변경 yuv(기본설정) > rgba
            .build()

    imageAnalyzerFilters.setAnalyzer(
        Executors.newSingleThreadExecutor(),
        filterAnalyzer { bitMap ->

            val matrix = Matrix()
            matrix.postRotate(coverRotation.toFloat()) // 90도 회전
            val rotatedBitmap = Bitmap.createBitmap(bitMap!!, 0, 0, bitMap.width, bitMap.height, matrix, true)

            filterImageView.setImageBitmap(rotatedBitmap)

        })


    val cameraProvider = cameraProviderFuture.get()

    val preview = Preview.Builder()
        .build()
        .apply { setSurfaceProvider(previewView.surfaceProvider) }


    cameraProvider.bindToLifecycle(
        activity,
        cameraSelector,
        preview,
        imageAnalyzerFilters
    )


}
