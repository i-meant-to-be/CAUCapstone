package com.caucapstone.app

import android.app.Activity
import android.app.Application
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
            val bytes = module.callAttr("test")

            AppTheme {
                // A surface container using the 'background' color from the theme
                MainView(MainViewModel())

            }
        }
    }
}