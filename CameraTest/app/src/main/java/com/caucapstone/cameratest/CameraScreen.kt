package com.caucapstone.cameratest

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri


enum class FilterType {
    FILTER_NONE,
    FILTER_STRIPE,
    FILTER_SPECIFIC,
    FILTER_DALTONIZED
}

@Composable
fun CameraScreen() {
    val sliderValue = remember { mutableStateOf(0f) }
    val currFilterType = remember { mutableStateOf(FilterType.FILTER_NONE) }
    var imageUri by remember { mutableStateOf(EMPTY_IMAGE_URI) }
    var bitmap : Bitmap? = null
    var blindType by remember { mutableStateOf(TYPE_NUM_NORMAL) }
    val context = LocalContext.current


    PreviewAndFilter(
        currFilterType = currFilterType.value,
        sliderValue = sliderValue.value,

        //일단 deuteranopia로 지정해둠-----------------------------------------------------------------------------------------------------색각 이상 종류 변경시 여기 변경하면 됨
        blindType = TYPE_NUM_PROTANOPIA,
        onImageFile = { file ->
            imageUri = file.toUri()
        }
    )
    //-----------------------------------------------------------------------------------------------------------------------------------capture한 이미지 bitmap 여기서 반환
    bitmap = uriToBitmap(context,imageUri)
    if( bitmap != null) {
        bitmap = Bitmap.createScaledBitmap(bitmap, bitmap.width/2, bitmap.height/2, true)
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
            onSliderValueChange = { newValue -> sliderValue.value = newValue },
            blindType = blindType,
            context = context
        )
        CameraCrosshair()
//----------------------------------------------------------------------------------------- bitmap정상적으로 받아오는지 체크해봤음
//                                                                                          참고로 생성된 모든 이미지는 90도 돌아가 있을것이니 출력을 정상적인 방향으로 하려면 rotation해야함.
//                                                                                          참고 : PreviewAndFilter 101줄 정도쯤
//        if(bitmap != null) {
//            Image(
//                bitmap = bitmap!!.asImageBitmap(),
//                contentDescription = null,
//                modifier = Modifier.size(128.dp)
//            )
//        }
    }
}


private val colors = (1..360).map { hue ->
    Color(android.graphics.Color.HSVToColor(floatArrayOf(hue.toFloat(), 1f, 1f)))
}

@Composable
fun BlackModeSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
) {
    val currWidth = LocalConfiguration.current.screenWidthDp

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width((currWidth * 0.8).dp)
            .clip(RoundedCornerShape(30.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f))
            .padding(
                top = 3.dp,
                start = 15.dp,
                end = 15.dp
            )
    ) {
            Row(
                modifier = Modifier
                    .padding(
                        bottom = 3.dp
                    )
            ){
                Text(text = "빨", modifier = Modifier.weight(17f))
                Text(text = "주", modifier = Modifier.weight(12f))
                Text(text = "노", modifier = Modifier.weight(31f))
                Text(text = "초", modifier = Modifier.weight(33f))
                Text(text = "하", modifier = Modifier.weight(27f))
                Text(text = "파", modifier = Modifier.weight(29f))
                Text(text = "보", modifier = Modifier.weight(12f))
                Text(text = "분", modifier = Modifier.weight(27f))
                Text(text = "빨", modifier = Modifier.weight(12f))
            }
            Spacer(modifier = Modifier.height(3.dp))
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

@Composable
fun CameraShotButtonWithRGBIndicator(
    colorCodes: Triple<Int, Int, Int>,
    approxColorCodes: Triple<Int, Int, Int>,
    approxColorName: String,
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
                    .background(
                        Color(
                            approxColorCodes.first,                               //대표값의 colorcode 받아와서 배경색 처리함.
                            approxColorCodes.second,                                    //대표값으로 하니까 뭔가 좀 오차가 많이 느껴져서 실제값(ColorCodes)으로 처리해도 괜찮을거 같긴한데
                            approxColorCodes.third
                        )
                    )                                    //일단 배경색을 코드값으로 하고싶어서 글자색을 어느정도 보색으로 처리해야할거 같다는 생각이 듦
            ) {
                if((approxColorCodes.first+approxColorCodes.second+approxColorCodes.third)/3<100){
                    Text(
                        "${colorCodes.first}, ${colorCodes.second}, ${colorCodes.third} / $approxColorName",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.White.copy(
                                alpha = 0.8f
                            )
                        )
                    )
                }
                else {
                    Text(
                        "${colorCodes.first}, ${colorCodes.second}, ${colorCodes.third} / $approxColorName",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                alpha = 0.8f
                            )
                        )
                    )
                }
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
    blindType: Int,
    context: Context
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
        modifier = Modifier.fillMaxWidth()
    ) {
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
                onClick = {
                    if(blindType in TYPE_NUM_PROTANOPIA .. TYPE_NUM_TRITANOPIA) {
                        onClick(FilterType.FILTER_STRIPE)
                    }
                    else{
                        Toast.makeText(context, "설정에서 본인의 색각이상을 선택해주세요", Toast.LENGTH_SHORT).show()
                    }
                },
                label = "줄무늬 필터"
            )
            ReducibleRadioButton(
                value = filterType == FilterType.FILTER_DALTONIZED,
                onClick = {
                    if(blindType in TYPE_NUM_PROTANOPIA .. TYPE_NUM_TRITANOPIA){
                        onClick(FilterType.FILTER_DALTONIZED)
                    }
                    else{
                        Toast.makeText(context, "설정에서 본인의 색각이상을 선택해주세요", Toast.LENGTH_SHORT).show()
                    }
                },
                label = "색상 조정 필터"
            )
        }
        BlackModeSlider(sliderValue, onSliderValueChange)
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

fun uriToBitmap(context: Context, imageUri: Uri): Bitmap? {
    val contentResolver: ContentResolver = context.contentResolver
    return try {
        // Uri를 통해 이미지를 읽어옴
        val inputStream = contentResolver.openInputStream(imageUri)

        // BitmapFactory를 사용하여 InputStream에서 Bitmap으로 변환
        BitmapFactory.decodeStream(inputStream)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
val EMPTY_IMAGE_URI: Uri = Uri.parse("file://dev/null")