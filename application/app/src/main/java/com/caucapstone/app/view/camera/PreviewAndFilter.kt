package com.caucapstone.app.view.camera

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Matrix
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.core.UseCase
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import com.caucapstone.app.ColorBlindType
import com.caucapstone.app.FilterType
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.math.abs

object GlobalVariables {
    var coverRotation: Int = 0
    var hueCriteria = 0F
    var blindType: ColorBlindType = ColorBlindType.COLOR_BLIND_PROTANOPIA
    var filterType: FilterType = FilterType.FILTER_NONE
    var rgb = Triple(0, 0, 0)
    var colorName = "-"
}

@Composable
fun PreviewAndFilter(
    modifier :Modifier= Modifier, // Modifier에 fillMaxSize()가 붙어야 함
    cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA,
    currFilterType : FilterType,
    sliderValue: Float,
    blindType: ColorBlindType,
    rgb : (Triple<Int,Int,Int>?) -> Unit,
    approxColorName : (String?) -> Unit
) {
    val context = LocalContext.current

    GlobalVariables.filterType = currFilterType
    GlobalVariables.hueCriteria = sliderValue
    GlobalVariables.blindType = blindType

    rgb(GlobalVariables.rgb)
    approxColorName(GlobalVariables.colorName)

    Box(modifier = modifier) {
        val lifecycleOwner = LocalLifecycleOwner.current
        var previewUseCase by remember { mutableStateOf<UseCase>(Preview.Builder().build()) }
        var bitmap by remember { mutableStateOf<ImageBitmap?>(null) }
        var roll by remember {  mutableIntStateOf(0)  }

        val imageCaptureUseCase by remember {                                           //사진 찍으면 어떻게 되는지 구현해야함
            mutableStateOf(
                ImageCapture.Builder()
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                    .build()
            )
        }

        val imageAnalyzerFilters by remember {
            mutableStateOf(
                ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)  //이미지 받는 값 변경 yuv(기본설정) > rgba
                    .build()
            )
        }

        imageAnalyzerFilters.setAnalyzer(
            Executors.newSingleThreadExecutor(),
            FilterAnalyzer { bitMap ->
                val matrix = Matrix()
                matrix.postRotate(GlobalVariables.coverRotation.toFloat()) // 90도 회전
                val rotatedBitmap = Bitmap.createBitmap(bitMap!!, 0, 0, bitMap.width, bitMap.height, matrix, true)

                bitmap = rotatedBitmap.asImageBitmap()
            }
        )

        Box {
            CameraPreview(
                modifier = modifier,
                onUseCase = {
                    previewUseCase = it
                }
            )
            if (currFilterType == FilterType.FILTER_NONE){      //roll - 기본 preview상에서도 runOnUiThread하기 위해 선언해 놓았음
                roll+=1
                roll-=1
            }
            if (currFilterType != FilterType.FILTER_NONE) {
                bitmap?.let {
                    Image(
                        modifier = modifier,
                        bitmap = it,
                        contentDescription = "필터 적용",
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
        LaunchedEffect(previewUseCase) {
            val cameraProvider = context.getCameraProvider()
            try {
                // Must unbind the use-cases before rebinding them.
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    previewUseCase,
                    imageCaptureUseCase,
                    imageAnalyzerFilters
                )
            } catch (ex: Exception) {
                Log.e("CameraCapture", "Failed to bind camera use cases", ex)
            }
        }
    }
}


//-----------------------------------------------------------------------------------------------------

//RGBA_8888형식으로 RGBbitmap 만드는 함수
private fun toRGBBitmap(image: ImageProxy): Bitmap {
    val planes = image.planes

    val buffer = planes[0].buffer
    val pixelStride = planes[0].pixelStride
    val rowStride = planes[0].rowStride

    val rowPadding = rowStride - pixelStride * image.width
    val bitmap = Bitmap.createBitmap(
        image.width + rowPadding / pixelStride, image.height, Bitmap.Config.ARGB_8888
    )
    image.close()
    bitmap.copyPixelsFromBuffer(buffer)
    return bitmap
}

//-----------------------------------------------------------------------------------------------------
//이미지 분석 종합(색상명 출력, 각 필터들 적용)
class FilterAnalyzer(private val callBackBitMap: (Bitmap?) -> Unit) : ImageAnalysis.Analyzer {
    private var lastAnalyzedTimestamp = 0L

    override fun analyze(image: ImageProxy) {
        val rgbBitMap = toRGBBitmap(image)
        GlobalVariables.coverRotation = image.imageInfo.rotationDegrees
        image.close()

        val imageWidth = rgbBitMap.width
        val imageHeight = rgbBitMap.height

        // 가운데 지점의 좌표 계산
        val centerX = imageWidth / 2
        val centerY = imageHeight / 2

        // 가운데 지점의 색상 추출
        val centerColor = rgbBitMap.getPixel(centerX, centerY)

        // 추출된 색상 정보를 분해
        val red = Color.red(centerColor)
        val green = Color.green(centerColor)
        val blue = Color.blue(centerColor)

        //ColorToText 클래스 접근해서 근사 대표 색상 받아오기
        val representativeColorName = ColorToTextConverter.analyzer(red, green, blue)

        val currentTimestamp = System.currentTimeMillis()
        if (currentTimestamp - lastAnalyzedTimestamp >= TimeUnit.MILLISECONDS.toMillis(300)) {

            GlobalVariables.rgb = Triple(red,green,blue)
            if (representativeColorName != null) {
                GlobalVariables.colorName = representativeColorName
            }

            lastAnalyzedTimestamp = currentTimestamp
        }


        //정해진 범위 외의 색상 흑백으로 변환
        when (GlobalVariables.filterType) {
            FilterType.FILTER_SPECIFIC -> {
                callBackBitMap(coverFilter(rgbBitMap, GlobalVariables.hueCriteria))
            }
            //Dalton색상으로 변경하기
            FilterType.FILTER_DALTONIZED -> {
                callBackBitMap(daltonFilter(rgbBitMap, GlobalVariables.blindType))
            }
            //잘 안보이는 색상에 빗금 치기
            FilterType.FILTER_STRIPE -> {
                callBackBitMap(stripeFilter(rgbBitMap, GlobalVariables.blindType))
            }
            else -> {}
        }

    }
}

//특정범위의 색상만 표출하는 filter을 위한 bitmap 처리 과정 (표출 범위 hueCriteria-v ~ hueCriteria+v)
fun coverFilter(inputBitmap: Bitmap, hueCriteria: Float): Bitmap {
    val width = inputBitmap.width  / 2
    val height = inputBitmap.height / 2

    val scaledBitmap: Bitmap = Bitmap.createScaledBitmap(inputBitmap, width, height, true)
    val outputBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val v = 10

    for (x in 0 until width) {
        for (y in 0 until height) {
            val pixel = scaledBitmap.getPixel(x, y)

            val hsv = FloatArray(3)
            Color.RGBToHSV(Color.red(pixel), Color.green(pixel), Color.blue(pixel), hsv)
            val hue = hsv[0]


            // Check if the hue value is within the desired range
            if (abs(hue - hueCriteria) <= v || abs(hue - (hueCriteria + 360)) <= v || abs((hue + 360) - hueCriteria) <= v) {
                outputBitmap.setPixel(x, y, pixel) // Keep the original color
            } else {
                val grayValue =
                    (Color.red(pixel) * 0.299 + Color.green(pixel) * 0.587 + Color.blue(pixel) * 0.114).toInt()
                val grayscaleColor =
                    Color.rgb(grayValue, grayValue, grayValue) // Convert to black and white
                outputBitmap.setPixel(x, y, grayscaleColor)
            }
        }
    }

    return outputBitmap
}

//dalton을 위한 bitmap 처리 과정
fun daltonFilter(
    inputBitmap: Bitmap,
    blindType: ColorBlindType
): Bitmap {
    val CVDMatrix = mapOf(
        ColorBlindType.COLOR_BLIND_PROTANOPIA to doubleArrayOf(
            0.0, 2.02344, -2.52581,
            0.0, 1.0, 0.0,
            0.0, 0.0, 1.0
        ),
        ColorBlindType.COLOR_BLIND_DEUTERANOPIA to doubleArrayOf(
            1.0, 0.0, 0.0,
            0.494207, 0.0, 1.24827,
            0.0, 0.0, 1.0
        ),
        ColorBlindType.COLOR_BLIND_TRITANOPIA to doubleArrayOf(
            1.0, 0.0, 0.0,
            0.0, 1.0, 0.0,
            -0.395913, 0.801109, 0.0
        )
    )
    val cvd = CVDMatrix[blindType]!!
    val cvdA = cvd[0]
    val cvdB = cvd[1]
    val cvdC = cvd[2]
    val cvdD = cvd[3]
    val cvdE = cvd[4]
    val cvdF = cvd[5]
    val cvdG = cvd[6]
    val cvdH = cvd[7]
    val cvdI = cvd[8]

    val width = inputBitmap.width / 2
    val height = inputBitmap.height / 2

    val scaledBitmap: Bitmap = Bitmap.createScaledBitmap(inputBitmap, width, height, true)
    val outputBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

    for (x in 0 until width) {
        for (y in 0 until height) {
            val pixel = scaledBitmap.getPixel(x, y)

            val r = Color.red(pixel)
            val g = Color.green(pixel)
            val b = Color.blue(pixel)

            // RGB to LMS matrix conversion
            val L = (17.8824 * r) + (43.5161 * g) + (4.11935 * b)
            val M = (3.45565 * r) + (27.1554 * g) + (3.86714 * b)
            val S = (0.0299566 * r) + (0.184309 * g) + (1.46709 * b)
            // Simulate color blindness
            val l = (cvdA * L) + (cvdB * M) + (cvdC * S)
            val m = (cvdD * L) + (cvdE * M) + (cvdF * S)
            val s = (cvdG * L) + (cvdH * M) + (cvdI * S)
            // LMS to RGB matrix conversion
            var R = (0.0809444479 * l) + (-0.130504409 * m) + (0.116721066 * s)
            var G = (-0.0102485335 * l) + (0.0540193266 * m) + (-0.113614708 * s)
            var B = (-0.000365296938 * l) + (-0.00412161469 * m) + (0.693511405 * s)
            // Isolate invisible colors to color vision deficiency (calculate error matrix)
            R = r - R
            G = g - G
            B = b - B
            // Shift colors towards visible spectrum (apply error modifications)
            val RR = (0.0 * R) + (0.0 * G) + (0.0 * B)
            val GG = (0.7 * R) + (1.0 * G) + (0.0 * B)
            val BB = (0.7 * R) + (0.0 * G) + (1.0 * B)
            // Add compensation to original values
            R = RR + r
            G = GG + g
            B = BB + b
            // Clamp values
            if (R < 0) R = 0.0
            if (R > 255) R = 255.0
            if (G < 0) G = 0.0
            if (G > 255) G = 255.0
            if (B < 0) B = 0.0
            if (B > 255) B = 255.0

            val bitColor = Color.rgb(R.toInt(), G.toInt(), B.toInt())
            outputBitmap.setPixel(x, y, bitColor)
        }
    }

    return outputBitmap
}

//패턴삽입을 위한 bitmap 처리 과정
fun stripeFilter(
    inputBitmap: Bitmap,
    blindType: ColorBlindType
): Bitmap {
    val width = inputBitmap.width / 2
    val height = inputBitmap.height / 2
    val scaledBitmap: Bitmap = Bitmap.createScaledBitmap(inputBitmap, width, height, true)
    val outputBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val v = 50
    val criteria = when (blindType) {
        ColorBlindType.COLOR_BLIND_PROTANOPIA -> 0
        ColorBlindType.COLOR_BLIND_DEUTERANOPIA -> 120
        ColorBlindType.COLOR_BLIND_TRITANOPIA -> 240
        else -> 0
    }
    var i = 0

    for (x in 0 until width) {
        for (y in 0 until height) {
            val pixel = scaledBitmap.getPixel(x, y)
            val hsv = FloatArray(3)
            Color.RGBToHSV(Color.red(pixel), Color.green(pixel), Color.blue(pixel), hsv)
            val hue = hsv[0]
            val sat = hsv[1]

            // 색각이상별로 잘 안보이는 색상의 hue값에 검정 줄무늬 넣기
            // 색각이상별로 잘 안보이는 색상(적색계열, 녹색계열, 청색계열)의 hue값에 검정무늬 넣기
            if ((abs(hue - criteria) <= v || abs(hue - (criteria + 360)) <= v || abs((hue + 360) - criteria) <= v) && sat > 0.30 && i%17 == 10 ) {
                val stripePoint = Color.rgb(50, 50, 50)
                outputBitmap.setPixel(x, y, stripePoint) //중간중간 빗금을 위한 부분
            } else {
                outputBitmap.setPixel(x, y, pixel)
            }
            i++
        }
    }
    return outputBitmap
}