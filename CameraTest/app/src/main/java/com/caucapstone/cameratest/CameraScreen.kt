package com.caucapstone.cameratest

import android.content.Context
import android.widget.ImageView
import androidx.camera.core.CameraSelector
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView


enum class FilterType {
    FILTER_NONE,
    FILTER_STRIPE,
    FILTER_SPECIFIC,
    FILTER_DALTONIZED
}

@Composable
fun CameraScreen() {
    val sliderValue = remember { mutableStateOf(0f) }
    val colorCodes = remember { mutableStateOf(Triple(1, 2, 3)) }
    val colorName = remember { mutableStateOf("초록") }
    val currFilterType = remember { mutableStateOf(FilterType.FILTER_NONE) }


    val context: Context = LocalContext.current
    val cameraController: LifecycleCameraController = remember { LifecycleCameraController(context) }
    val previewView: PreviewView = remember { PreviewView(context) }
    val filterImageView: ImageView = remember { ImageView(context) }
    val lifecycleOwner = LocalLifecycleOwner.current
//    cameraController.bindToLifecycle(
//        lifecycleOwner
//    )
    cameraController.cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    previewView.controller = cameraController
    val cameraSelector: MutableState<CameraSelector> = remember {
        mutableStateOf(CameraSelector.DEFAULT_BACK_CAMERA)
    }

        LaunchedEffect(previewView) {
            context.createImageAnalyzerUseCase(
                lifecycleOwner = lifecycleOwner,
                cameraSelector = cameraSelector.value,
                previewView = previewView,
                filterImageView = filterImageView,
                colorCodes = colorCodes,
                colorName = colorName,
                selectedFilterType = currFilterType.value
            )
        }



    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = {previewView}
        )
        Canvas(
            modifier = Modifier.fillMaxSize(),
            onDraw = {filterImageView}
        )
    }
    Box(
        contentAlignment = Alignment.TopCenter,
        modifier = Modifier
            .fillMaxSize()
            .padding(15.dp)
    ) {
        TopOptionBar(
            filterType = currFilterType.value,
            onClick = { filterType -> currFilterType.value = filterType },
            sliderValue = sliderValue.value,
            // 슬라이더 값 변경 시의 작업을 여기서 구현 (onSliderValueChange)
            onSliderValueChange = { newValue -> sliderValue.value = newValue }
        )
        CameraCrosshair()
        CameraShotButtonWithRGBIndicator(
            colorCodes = colorCodes.value,
            colorName = colorName.value,
            // 카메라 촬영 시의 작업을 여기서 구현 (onButtonClick)
            onButtonClick = {}
        )
        CameraBody()
    }
}


@Composable
fun BlackModeSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
) {
    Slider(
        value = value,
        onValueChange = onValueChange,
        steps = 0,
        valueRange = 0f..100f,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun CameraShotButtonWithRGBIndicator(
    colorCodes: Triple<Int, Int, Int>,
    colorName: String,
    onButtonClick: () -> Unit
) {
    Box(
        contentAlignment = Alignment.CenterEnd,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(300.dp, 40.dp)
                    .clip(RoundedCornerShape(size = 30.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f))
            ) {
                Text(
                    "${colorCodes.first}, ${colorCodes.second}, ${colorCodes.third} / $colorName",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                            alpha = 0.8f
                        )
                    )
                )
            }
            Box(modifier = Modifier.height(10.dp))
            Button(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                ),
                onClick = onButtonClick
            ) {

            }
        }
    }
}

@Composable
fun CameraCrosshair() {
    val deviceHeight = LocalConfiguration.current.screenHeightDp
    val deviceWidth = LocalConfiguration.current.screenWidthDp

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 25.dp)
    ) {
        Box(
            modifier = Modifier
                .height(30.dp)
                .width(2.dp)
                .background(MaterialTheme.colorScheme.onSurfaceVariant)
                .offset(x = (deviceWidth * 0.5).dp, y = (deviceHeight * 0.5).dp)
        )
        Box(
            modifier = Modifier
                .height(2.dp)
                .width(30.dp)
                .background(MaterialTheme.colorScheme.onSurfaceVariant)
                .offset(x = (deviceWidth * 0.5).dp, y = (deviceHeight * 0.5).dp)
        )
    }
}

@Composable
fun TopOptionBar(
    filterType: FilterType,
    onClick: (FilterType) -> Unit,
    sliderValue: Float,
    onSliderValueChange: (Float) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier.fillMaxWidth()
    ) {
        BlackModeSlider(sliderValue, onSliderValueChange)
        Row() {
            ReducibleRadioButton(
                value = filterType == FilterType.FILTER_NONE,
                onClick = { onClick(FilterType.FILTER_NONE) },
                label = "필터 없음"
            )
            ReducibleRadioButton(
                value = filterType == FilterType.FILTER_SPECIFIC,
                onClick = { onClick(FilterType.FILTER_SPECIFIC) },
                label = "특정 색상 지정 필터"
            )
            ReducibleRadioButton(
                value = filterType == FilterType.FILTER_STRIPE,
                onClick = { onClick(FilterType.FILTER_STRIPE) },
                label = "줄무늬 필터"
            )
            ReducibleRadioButton(
                value = filterType == FilterType.FILTER_DALTONIZED,
                onClick = { onClick(FilterType.FILTER_DALTONIZED) },
                label = "색상 조정 필터"
            )
        }
    }
}

@Composable
fun ReducibleRadioButton(
    value: Boolean,
    onClick: () -> Unit,
    label: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.animateContentSize()
    ) {
        RadioButton(
            selected = value,
            onClick = onClick
        )
        if (value) Text(label)
    }
}

@Composable
fun CameraBody() {

}

@Preview
@Composable
private fun Preview_CameraContent() {
    CameraScreen(
    )
}