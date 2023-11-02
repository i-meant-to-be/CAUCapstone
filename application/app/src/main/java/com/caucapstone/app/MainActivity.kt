package com.caucapstone.app

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.caucapstone.app.data.ImageProtoSerializer
import com.caucapstone.app.data.SettingProtoSerializer
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

            AppTheme {
                // A surface container using the 'background' color from the theme
                MainView(MainViewModel())

            }
        }
    }
}