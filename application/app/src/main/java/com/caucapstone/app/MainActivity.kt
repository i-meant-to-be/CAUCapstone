package com.caucapstone.app

import android.app.Activity
import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.platform.LocalContext
import com.caucapstone.app.ui.theme.AppTheme
import com.caucapstone.app.view.MainView
import com.caucapstone.app.viewmodel.MainViewModel
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import java.io.ByteArrayOutputStream

@HiltAndroidApp
class MainApp : Application() {}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            // Python interpreter initiate
            if (!Python.isStarted()) {
                Python.start(AndroidPlatform(LocalContext.current as Activity))
            }

            val py = Python.getInstance()
            val module = py.getModule("integration_test")

            // Drawable - Bitmap - ByteArray - String
            val bitmap = BitmapFactory.decodeResource(applicationContext.resources, R.drawable.test)
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)

            // String - ByteArray - Bitmap
            val bitmapDestByteArray =
                module.callAttr(
                    "test",
                    outputStream.toByteArray().toString(Charsets.UTF_8),
                    1365, 2048)
                    .toJava(ByteArray::class.java)

            BitmapFactory.decodeByteArray(bitmapDestByteArray, 0, bitmapDestByteArray.size)


            AppTheme {
                MainView(MainViewModel())
            }
        }
    }
}