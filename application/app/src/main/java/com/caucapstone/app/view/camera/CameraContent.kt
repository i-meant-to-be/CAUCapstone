package com.caucapstone.app.view.camera

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.caucapstone.app.ColorBlindType
import com.caucapstone.app.FilterType
import com.caucapstone.app.R
import com.caucapstone.app.SettingProto
import com.caucapstone.app.data.globalPaddingValue
import com.caucapstone.app.viewmodel.CameraViewModel
import kotlin.math.roundToInt

private val colors = (1..360).map { hue ->
    Color(android.graphics.Color.HSVToColor(floatArrayOf(hue.toFloat(), 1f, 1f)))
}

@Composable
fun CameraContent(
    paddingValues: PaddingValues,
    viewModel: CameraViewModel = hiltViewModel()
) {
    val data = viewModel.settingFlow.collectAsState(SettingProto.getDefaultInstance()).value

    PreviewAndFilter(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    )
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                vertical = globalPaddingValue * 2,
                horizontal = globalPaddingValue
            )
    ) {
        TopOptionBar(
            filterType = viewModel.currFilterType.value,
            onClick = { filterType -> viewModel.setCurrFilterType(filterType) },
            sliderValue = viewModel.sliderValue.value,
            onSliderValueChange = { newValue -> viewModel.setSliderValue(newValue) },
            visible = viewModel.currFilterType.value == FilterType.FILTER_SPECIFIC,
            colorBlindType = data.colorBlindType
        )
        CameraCrosshair()
    }
}

@Composable
fun BlackModeSlider(
    value: Float,
    visible: Boolean,
    onValueChange: (Float) -> Unit
) {
    val density = LocalDensity.current
    val currWidth = LocalConfiguration.current.screenWidthDp

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically {
            with (density) { -40.dp.roundToPx() }
        } + fadeIn(initialAlpha = 0.0f),
        exit = slideOutVertically {
            with (density) { -40.dp.roundToPx() }
        } + fadeOut(targetAlpha = 0.0f)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .width((currWidth * 0.8).dp)
                .clip(RoundedCornerShape(30.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f))
                .padding(
                    top = 20.dp,
                    start = 15.dp,
                    end = 15.dp
                )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(
                        brush = Brush.horizontalGradient(colors)
                    )
            )
            Slider(
                value = value,
                onValueChange = onValueChange,
                steps = 0,
                valueRange = 0f..360f,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun CameraShotButtonWithRGBIndicator(
    colorCodes: Triple<Int, Int, Int>,
    approxColorCodes: Triple<Int, Int, Int>,
    approxColorName: String,
    onButtonClick: () -> Unit
) {
    Box(
        contentAlignment = Alignment.CenterEnd,
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 30.dp)
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
                    .background(
                        Color(
                            colorCodes.first,                               //대표값의 colorcode 받아와서 배경색 처리함.
                            colorCodes.second,                                    //대표값으로 하니까 뭔가 좀 오차가 많이 느껴져서 실제값(ColorCodes)으로 처리해도 괜찮을거 같긴한데
                            colorCodes.third,
                            (255 * 0.8).roundToInt()
                        )
                    )
            ) {
                val isGray = (approxColorCodes.first + approxColorCodes.second + approxColorCodes.third) / 3 < 80
                Text(
                    "${colorCodes.first}, ${colorCodes.second}, ${colorCodes.third} / $approxColorName",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = if (isGray) Color.White.copy(alpha = 0.8f) else Color.Black.copy(alpha = 0.8f)
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
        modifier = Modifier.fillMaxSize()
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
    onSliderValueChange: (Float) -> Unit,
    visible: Boolean,
    colorBlindType: ColorBlindType
) {
    val context = LocalContext.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(size = 30.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f))
                .padding(horizontal = 15.dp)
        ) {
            ReducibleRadioButton(
                value = filterType == FilterType.FILTER_NONE,
                onClick = { onClick(FilterType.FILTER_NONE) },
                label = stringResource(R.string.filter_name_none)
            )
            ReducibleRadioButton(
                value = filterType == FilterType.FILTER_SPECIFIC,
                onClick = { onClick(FilterType.FILTER_SPECIFIC) },
                label = stringResource(R.string.filter_name_specific)
            )
            ReducibleRadioButton(
                value = filterType == FilterType.FILTER_STRIPE,
                onClick = {
                    if (colorBlindType in listOf(
                            ColorBlindType.COLOR_BLIND_TRITANOPIA,
                            ColorBlindType.COLOR_BLIND_DEUTERANOPIA,
                            ColorBlindType.COLOR_BLIND_PROTANOPIA
                    )) {
                        onClick(FilterType.FILTER_STRIPE)
                    } else Toast.makeText(context, "설정에서 본인의 색각 이상을 선택해주세요.", Toast.LENGTH_SHORT).show()
                },
                label = stringResource(R.string.filter_name_stripe)
            )
            ReducibleRadioButton(
                value = filterType == FilterType.FILTER_DALTONIZED,
                onClick = {
                    if (colorBlindType in listOf(
                            ColorBlindType.COLOR_BLIND_TRITANOPIA,
                            ColorBlindType.COLOR_BLIND_DEUTERANOPIA,
                            ColorBlindType.COLOR_BLIND_PROTANOPIA
                    )) {
                        onClick(FilterType.FILTER_DALTONIZED)
                    } else Toast.makeText(context, "설정에서 본인의 색각 이상을 선택해주세요.", Toast.LENGTH_SHORT).show()
                },
                label = stringResource(R.string.filter_name_daltonized)
            )
        }
        Box(modifier = Modifier.height(5.dp))
        BlackModeSlider(sliderValue, visible, onSliderValueChange)
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