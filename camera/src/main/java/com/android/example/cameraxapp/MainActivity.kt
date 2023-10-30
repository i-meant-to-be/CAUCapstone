package com.android.example.cameraxapp


import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.example.cameraxapp.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.math.abs

val TYPE_NUM_PROTANOPIA = 1
val TYPE_NUM_DEUTERANOPIA = 2
val TYPE_NUM_TRITANOPIA = 3


class MainActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityMainBinding

    private var imageCapture: ImageCapture? = null

    private lateinit var cameraExecutor: ExecutorService

    private lateinit var coverView: ImageView
    private lateinit var daltonView: ImageView
    private lateinit var stripeView: ImageView

    private var isCovered = false
    private var isDaltoned = false
    private var isStriped = false

    var hueCriteria = 0F
    var blindType = 0

    //----------------------------------------------------------------------------------------------------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        //카메라 권한 요청
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }


        //사진 찍기 버튼 구현
        viewBinding.imageCaptureButton.setOnClickListener { takePhoto() }

        //------------------------------------------------------------------------------------------------
        //Filter 선택

        coverView = findViewById(R.id.coverView)
        daltonView = findViewById(R.id.daltonView)
        stripeView = findViewById(R.id.stripeView)

        coverView.visibility = View.INVISIBLE
        daltonView.visibility = View.INVISIBLE
        stripeView.visibility = View.INVISIBLE

        //click covered Filter
        viewBinding.coveredButton.setOnClickListener{
            isCovered = !isCovered
            isDaltoned = false
            isStriped = false

            daltonView.visibility = View.INVISIBLE
            stripeView.visibility = View.INVISIBLE
            if(isCovered){
                coverView.visibility = View.VISIBLE
            }
            else{
                coverView.visibility = View.INVISIBLE
            }
        }
        //click Daltoned Filter
        viewBinding.daltonButton.setOnClickListener{
            if(blindType != 0) {
                isCovered = false
                isDaltoned = !isDaltoned
                isStriped = false

                coverView.visibility = View.INVISIBLE
                stripeView.visibility = View.INVISIBLE
                if (isDaltoned) {
                    daltonView.visibility = View.VISIBLE
                } else {
                    daltonView.visibility = View.INVISIBLE
                }
            }
            else{
                val msg = "당신의 색각이상 분류를 선택해주세요"
                Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
            }
        }
        //click Striped Filter
        viewBinding.stripedButton.setOnClickListener{
            if(blindType != 0) {
                isCovered = false
                isDaltoned = false
                isStriped = !isStriped

                coverView.visibility = View.INVISIBLE
                daltonView.visibility = View.INVISIBLE
                if (isStriped) {
                    stripeView.visibility = View.VISIBLE
                } else {
                    stripeView.visibility = View.INVISIBLE
                }
            }
            else{
                val msg = "당신의 색각이상 분류를 선택해주세요"
                Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
            }
        }

        //------------------------------------------------------------------------------------------------

        viewBinding.prot.setOnClickListener { blindType = TYPE_NUM_PROTANOPIA }
        viewBinding.deut.setOnClickListener { blindType = TYPE_NUM_DEUTERANOPIA }
        viewBinding.trit.setOnClickListener { blindType = TYPE_NUM_TRITANOPIA }

        //------------------------------------------------------------------------------------------------

        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    //----------------------------------------------------------------------------------------------------

    //사진 촬영
    private fun takePhoto() {
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        // Create time stamped name and MediaStore entry.
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
            }
        }

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(
                contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )
            .build()

        // Set up image capture listener, which is triggered after photo has
        // been taken
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun
                        onImageSaved(output: ImageCapture.OutputFileResults) {
                    val msg = "Photo capture succeeded: ${output.savedUri}"
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, msg)
                }
            }
        )
    }

    //----------------------------------------------------------------------------------------------------

    //카메라 시작
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            //--------------------------------------------------------------------------------------------

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewBinding.viewFinder.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder().build()

            //--------------------------------------------------------------------------------------------
            //가운데 지점의 색상을 받아와 가장 가까운 대표값의 색상명 출력

            val colorTV = findViewById<TextView>(R.id.colorInfo)
            val imageAnalyzerColorName = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)   //가장 최신 것만 받아서 처리
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)  //이미지 받는 값 변경 yuv(기본설정) > rgba
                .build()
            imageAnalyzerColorName.setAnalyzer(
                Executors.newSingleThreadExecutor(),
                ColorNameAnalyzer { colors ->

                    val red = colors.Red
                    val green = colors.Green
                    val blue = colors.Blue

                    //ColorToText 클래스 접근해서 근사 대표 색상 받아오기
                    val representativeColorName = ColorToText.analyzer(red, green, blue)
                    val colorString = "$representativeColorName \nR: $red \nG: $green \nB: $blue"
                    // 추출한 색상 정보를 TextView에 설정
                    runOnUiThread {
                        colorTV.text = "$colorString"
                    }
                })

            //--------------------------------------------------------------------------------------------
            //지정된 색상의 일정 범위 외에는 흑백으로 처리

            val showColorBound = findViewById<SeekBar>(R.id.showColorBoundary)
            showColorBound.max = 360
            showColorBound.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    hueCriteria= progress.toFloat()
                }
                override fun onStartTrackingTouch(p0: SeekBar?) {}
                override fun onStopTrackingTouch(p0: SeekBar?) {}
            })

            //지정된 범위 외 색상들을 흑백 처리
            val imageAnalyzerDeleteAnotherColor = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)  //이미지 받는 값 변경 yuv(기본설정) > rgba
                .build()

//            imageAnalyzerDeleteAnotherColor.targetRotation = Surface.ROTATION_0
            imageAnalyzerDeleteAnotherColor.setAnalyzer(
                Executors.newSingleThreadExecutor(),
                CoverAnalyzer { bitMap ->
//                val matrix = Matrix()
//                matrix.postRotate(90f) // 90도 회전
//                val rotatedBitmap = Bitmap.createBitmap(bitMap!!, 0, 0, bitMap.width, bitMap.height, matrix, true)
                    runOnUiThread {
                        coverView.setImageBitmap(bitMap)
                    }
                })

            //--------------------------------------------------------------------------------------------
            //설정된 색각 이상에 따라 daltonize 처리

            val imageAnalyzerDaltonizer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)  //이미지 받는 값 변경 yuv(기본설정) > rgba
                .build()

            imageAnalyzerDaltonizer.setAnalyzer(
                Executors.newSingleThreadExecutor(),
                DaltonAnalyzer { bitMap ->

                    runOnUiThread {
                        daltonView.setImageBitmap(bitMap)
                    }
                })

            //--------------------------------------------------------------------------------------------
            //설정된 색각 이상에 따라 stripe 처리

            val imageAnalyzerStripe = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)  //이미지 받는 값 변경 yuv(기본설정) > rgba
                .build()

            imageAnalyzerStripe.setAnalyzer(
                Executors.newSingleThreadExecutor(),
                StripeAnalyzer { bitMap ->

                    runOnUiThread {
                        stripeView.setImageBitmap(bitMap)
                    }
                })


            //--------------------------------------------------------------------------------------------

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview,
//                    imageCapture,
//                    imageAnalyzerColorName,
//                    imageAnalyzerDeleteAnotherColor,
                    imageAnalyzerDaltonizer,
                    imageAnalyzerStripe
                )

            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    //----------------------------------------------------------------------------------------------------

    //접근 권한 확인
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(
                    this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    //객체 기본설정
    companion object {
        private const val TAG = "CameraXApp"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS =
            mutableListOf(
                Manifest.permission.CAMERA,
                Manifest.permission.RECORD_AUDIO
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
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
            image.width + rowPadding / pixelStride,
            image.height, Bitmap.Config.ARGB_8888
        )
        bitmap.copyPixelsFromBuffer(buffer)
        return bitmap
    }

    //-----------------------------------------------------------------------------------------------------

    //카메라 프리뷰 센터의 색상을 받아서 RGB값을 반환하기
    inner class ColorNameAnalyzer(private val colorCallback: (RGBandY<Int, Int, Int, Int>) -> Unit) :
        ImageAnalysis.Analyzer {

        override fun analyze(image: ImageProxy) {
            if (image.format == PixelFormat.RGBA_8888) {
                val bitmap = toRGB_Bitmap(image)
                image.close()

                // 이미지의 너비와 높이 가져오기
                val imageWidth = bitmap!!.width
                val imageHeight = bitmap!!.height

                // 가운데 지점의 좌표 계산
                val centerX = imageWidth / 2
                val centerY = imageHeight / 2

                // 가운데 지점의 색상 추출
                val centerColor = bitmap.getPixel(centerX, centerY)

                // 추출된 색상 정보를 분해
                val R = Color.red(centerColor)
                val G = Color.green(centerColor)
                val B = Color.blue(centerColor)
                val A = Color.alpha(centerColor)

                colorCallback(RGBandY(R, G, B, A))
            }
        }

    }

    //-----------------------------------------------------------------------------------------------------

    //지정된 범위외 색상 흑백으로 만들기
    inner class CoverAnalyzer(private val deleteAnotherColorCallback: (Bitmap?) -> Unit) :
        ImageAnalysis.Analyzer {

        override fun analyze(image: ImageProxy) {
            val rgbBitMap = toRGB_Bitmap(image) ?: return
            image.close()

            deleteAnotherColorCallback(applyHueFilter(rgbBitMap!!, hueCriteria))


        }
    }

    //from RGB bitmap to downgraded and covered bitmap, 표출 범위 hueCriteria-v ~ hueCriteria+v;
    fun applyHueFilter(inputBitmap: Bitmap, hueCriteria: Float): Bitmap {
        val width = inputBitmap.width / 3
        val height = inputBitmap.height / 3

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

    //-----------------------------------------------------------------------------------------------------

    //Dalton색상으로 변경하기
    inner class DaltonAnalyzer(private val daltonBitmapCallback: (Bitmap?) -> Unit) :
        ImageAnalysis.Analyzer{
        override fun analyze(image: ImageProxy) {
            val rgbBitMap = toRGB_Bitmap(image) ?: return
            image.close()

            daltonBitmapCallback(daltonFilter(rgbBitMap,blindType))
        }
    }

    fun daltonFilter(inputBitmap: Bitmap, blindType : Int): Bitmap {
        val width = inputBitmap.width / 3
        val height = inputBitmap.height / 3

        val scaledBitmap: Bitmap = Bitmap.createScaledBitmap(inputBitmap, width, height, true)
        val outputBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        for (x in 0 until width) {
            for (y in 0 until height) {
                val pixel = scaledBitmap.getPixel(x, y)

                var r = Color.red(pixel)
                var g = Color.green(pixel)
                var b = Color.blue(pixel)

                if(blindType == TYPE_NUM_PROTANOPIA) {    //Protanopia

                }
                else if(blindType == TYPE_NUM_DEUTERANOPIA){    //Deuteranopia
                    g = (Color.red(pixel)*0.43 + Color.green(pixel)*0.57).toInt()
                }
                else if(blindType == TYPE_NUM_TRITANOPIA){    //Tritanopia

                }
                else{ //normal
                }
                val grayscaleColor =Color.rgb(r, g, b)
                outputBitmap.setPixel(x, y, grayscaleColor)

            }
        }

        return outputBitmap
    }

    //-----------------------------------------------------------------------------------------------------

    //잘 안보이는 색상에 빗금 치기
    inner class StripeAnalyzer(private val daltonBitmapCallback: (Bitmap?) -> Unit) :
        ImageAnalysis.Analyzer{
        override fun analyze(image: ImageProxy) {
            val rgbBitMap = toRGB_Bitmap(image) ?: return
            image.close()

            daltonBitmapCallback(stripeFilter(rgbBitMap,blindType),)
        }
    }

    fun stripeFilter(inputBitmap: Bitmap, blindType : Int): Bitmap {
        val width = inputBitmap.width / 2
        val height = inputBitmap.height / 2

        val scaledBitmap: Bitmap = Bitmap.createScaledBitmap(inputBitmap, width, height, true)
        val outputBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val v = 40
        val criteria = when (blindType){
            TYPE_NUM_PROTANOPIA -> 0
            TYPE_NUM_DEUTERANOPIA -> 120
            TYPE_NUM_TRITANOPIA -> 240
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
                if ((abs(hue - criteria) <= v || abs(hue - (criteria + 360)) <= v || abs((hue + 360) - criteria) <= v) && sat > 0.30 && i%15 > 13 ) {
                    val stripePoint = Color.rgb(10, 10, 10)
                    outputBitmap.setPixel(x, y, stripePoint) //중간중간 빗금을 위한 부분
                } else {
                    outputBitmap.setPixel(x, y, pixel)
                }
                i++
            }
        }
        return outputBitmap
    }

}