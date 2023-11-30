package com.caucapstone.cameratest

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Matrix
import android.graphics.Paint
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.core.UseCase
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import com.example.composephoto.camera.CameraPreview
import kotlinx.coroutines.launch
import java.io.File
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.math.abs

const val TYPE_NUM_NORMAL = 0
const val TYPE_NUM_PROTANOPIA = 1
const val TYPE_NUM_DEUTERANOPIA = 2
const val TYPE_NUM_TRITANOPIA = 3


object GlobalVariables {
    var coverRotation: Int = 0
    var hueCriteria = 0F
    var blindType = 1
    var filterType: FilterType = FilterType.FILTER_NONE
    var rgb = Triple<Int, Int, Int>(0, 0, 0)
    var aprxRgb = Triple<Int, Int, Int>(0, 0, 0)
    var aprxColorName = "-"
}

@Composable
fun PreviewAndFilter(
    modifier :Modifier= Modifier.fillMaxSize(),
    cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA,
    currFilterType : FilterType,
    sliderValue: Float,
    blindType: Int,
    onImageFile: (File) -> Unit = { }
) {
    val context = LocalContext.current

    GlobalVariables.filterType = currFilterType
    GlobalVariables.hueCriteria = sliderValue
    GlobalVariables.blindType = blindType

    Box(modifier = modifier) {
        val lifecycleOwner = LocalLifecycleOwner.current
        val coroutineScope = rememberCoroutineScope()
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
            filterAnalyzer { bitMap ->

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
            CameraShotButtonWithRGBIndicator(   //-------------------------------------------------클릭시 캡처하려면 여기서 처리해야할거 같아서 옮겼음
                colorCodes = GlobalVariables.rgb,
                approxColorCodes = GlobalVariables.aprxRgb,
                approxColorName = GlobalVariables.aprxColorName,
                // 카메라 촬영 시의 작업을 여기서 구현 (onButtonClick)
                onButtonClick = {coroutineScope.launch {
                    imageCaptureUseCase.takePicture(context.executor).let {
                        onImageFile(it)
                    }
                }}
            )
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
private fun toRGB_Bitmap(image: ImageProxy): Bitmap? {
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
class filterAnalyzer(private val callBackBitMap: (Bitmap?) -> Unit) : ImageAnalysis.Analyzer {
    private var  lastAnalyzedTimestamp = 0L

    override fun analyze(image: ImageProxy) {
        val rgbBitMap = toRGB_Bitmap(image) ?: return
        GlobalVariables.coverRotation = image.imageInfo.rotationDegrees
        image.close()

        val imageWidth = rgbBitMap!!.width
        val imageHeight = rgbBitMap!!.height

        // 가운데 지점의 좌표 계산
        val centerX = imageWidth / 2
        val centerY = imageHeight / 2

        // 가운데 지점의 색상 추출
        val centerColor = rgbBitMap.getPixel(centerX, centerY)

        // 추출된 색상 정보를 분해
        val red = Color.red(centerColor)
        val green = Color.green(centerColor)
        val blue = Color.blue(centerColor)

        val currentTimestamp = System.currentTimeMillis()
        if (currentTimestamp - lastAnalyzedTimestamp >= TimeUnit.MILLISECONDS.toMillis(300)) {

            //ColorToText 클래스 접근해서 근사 대표 색상 받아오기
            val representativeColor = ColorToText.analyzer(red, green, blue)

            GlobalVariables.rgb = Triple(red,green,blue)
            GlobalVariables.aprxColorName = representativeColor[0].toString()
            GlobalVariables.aprxRgb =   Triple(representativeColor[1].toInt(16),representativeColor[2].toInt(16),representativeColor[3].toInt(16))


            lastAnalyzedTimestamp = currentTimestamp
        }


        //정해진 범위 외의 색상 흑백으로 변환
        if (GlobalVariables.filterType == FilterType.FILTER_SPECIFIC) {
            callBackBitMap(coverFilter(rgbBitMap, GlobalVariables.hueCriteria))
        }
        //Dalton색상으로 변경하기
        else if(GlobalVariables.filterType == FilterType.FILTER_DALTONIZED){
            callBackBitMap(daltonFilter(rgbBitMap,GlobalVariables.blindType))
        }
        //잘 안보이는 색상에 빗금 치기
        else { //GlobalVariables.filterType == FilterType.FILTER_STRIPE)
            callBackBitMap(stripeFilter(rgbBitMap,GlobalVariables.blindType))
        }

    }
}

//특정범위의 색상만 표출하는 filter을 위한 bitmap 처리 과정 (표출 범위 hueCriteria-v ~ hueCriteria+v)
fun coverFilter(inputBitmap: Bitmap, hueCriteria: Float): Bitmap {
    val width = inputBitmap.width
    val height = inputBitmap.height

    val scaledBitmap: Bitmap = Bitmap.createScaledBitmap(inputBitmap, width, height, true)
    val outputBitmap = toGrayscale(scaledBitmap)
    val v = 15

    for (x in 0 until width step 2) {
        for (y in 0 until height step 2) {
            val pixel = scaledBitmap.getPixel(x, y)

            val hsv = FloatArray(3)
            Color.RGBToHSV(Color.red(pixel), Color.green(pixel), Color.blue(pixel), hsv)
            val hue = hsv[0]


            // Check if the hue value is within the desired range
            if (abs(hue - hueCriteria) <= v || hueCriteria + 360 - hue <= v || hue + 360 - hueCriteria <= v) {
                try {
                    outputBitmap.setPixel(x, y, pixel) // Keep the original color
                    outputBitmap.setPixel(x+1, y, scaledBitmap.getPixel(x+1,y))
                    outputBitmap.setPixel(x, y+1, scaledBitmap.getPixel(x,y+1))
                    outputBitmap.setPixel(x+1, y+1, scaledBitmap.getPixel(x+1,y+1))
                } catch (e : ArrayIndexOutOfBoundsException){}
            }
        }
    }

    return outputBitmap
}
fun toGrayscale(bmpOriginal: Bitmap): Bitmap {
    val width: Int
    val height: Int
    height = bmpOriginal.height
    width = bmpOriginal.width
    val bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val c = Canvas(bmpGrayscale)
    val paint = Paint()
    val cm = ColorMatrix()
    cm.setSaturation(0f)
    val f = ColorMatrixColorFilter(cm)
    paint.colorFilter = f
    c.drawBitmap(bmpOriginal, 0f, 0f, paint)
    return bmpGrayscale
}


//dalton을 위한 bitmap 처리 과정
fun daltonFilter(inputBitmap: Bitmap, blindType : Int): Bitmap {

    val CVDMatrix = mapOf(
        TYPE_NUM_PROTANOPIA to doubleArrayOf(
            0.0, 2.02344, -2.52581,
            0.0, 1.0, 0.0,
            0.0, 0.0, 1.0
        ),
        TYPE_NUM_DEUTERANOPIA to doubleArrayOf(
            1.0, 0.0, 0.0,
            0.494207, 0.0, 1.24827,
            0.0, 0.0, 1.0
        ),
        TYPE_NUM_TRITANOPIA to doubleArrayOf(
            1.0, 0.0, 0.0,
            0.0, 1.0, 0.0,
            -0.395913, 0.801109, 0.0
        )
    )
    val cvd = CVDMatrix[blindType]!!
    val cvd_a = cvd[0]
    val cvd_b = cvd[1]
    val cvd_c = cvd[2]
    val cvd_d = cvd[3]
    val cvd_e = cvd[4]
    val cvd_f = cvd[5]
    val cvd_g = cvd[6]
    val cvd_h = cvd[7]
    val cvd_i = cvd[8]

    val width = inputBitmap.width/2
    val height = inputBitmap.height/2

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
            val l = (cvd_a * L) + (cvd_b * M) + (cvd_c * S)
            val m = (cvd_d * L) + (cvd_e * M) + (cvd_f * S)
            val s = (cvd_g * L) + (cvd_h * M) + (cvd_i * S)
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
fun stripeFilter(inputBitmap: Bitmap, blindType : Int): Bitmap {
    val width = inputBitmap.width
    val height = inputBitmap.height
    val outputBitmap = Bitmap.createScaledBitmap(inputBitmap, width, height, true)
    val v = 50
    val criteria = when (blindType){
        TYPE_NUM_PROTANOPIA -> 0
        TYPE_NUM_DEUTERANOPIA -> 120
        TYPE_NUM_TRITANOPIA -> 240
        else -> 0
    }

    for (x in 0 until width step 2) {
        for (y in 0 until height step 2) {
            val pixel = outputBitmap.getPixel(x, y)
            val hsv = FloatArray(3)
            Color.RGBToHSV(Color.red(pixel), Color.green(pixel), Color.blue(pixel), hsv)
            val hue = hsv[0]
            val sat = hsv[1]

            val diffHue = minOf(abs(hue - criteria), hue + 360 - criteria,criteria + 360 - hue)
            // 색각이상별로 잘 안보이는 색상(적색계열, 녹색계열, 청색계열)의 hue값에 검정무늬 넣기
            if ((diffHue <= v) && sat > 0.30 && (x+y)%19 == 1 ) {
                val pointColor =Color.argb((255*(1-diffHue/v)).toInt(),0,0,0)
                try {
                    outputBitmap.setPixel(x+1, y, pointColor)
                    outputBitmap.setPixel(x, y+1, pointColor)
                } catch (e : ArrayIndexOutOfBoundsException){}
            }
        }
    }
    return outputBitmap
}