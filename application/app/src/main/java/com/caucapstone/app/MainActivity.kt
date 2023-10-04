package com.caucapstone.app

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FileCopy
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import com.caucapstone.app.ui.theme.AppTheme
import com.chaquo.python.PyException
import com.chaquo.python.PyObject
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            if (!Python.isStarted()) {
                Python.start(AndroidPlatform(LocalContext.current as Activity))
            }

            val py = Python.getInstance()

            val sys = py.getModule("sys")
            val io = py.getModule("io")
            val console = py.getModule("test")
            val textOutputStream = io.callAttr("StringIO")
            sys.put("stdout", textOutputStream)

            var interpreterOutput = "(empty)";

            try {
                console.callAttrThrows("function")
                interpreterOutput = textOutputStream.callAttr("getvalue").toString()
            } catch (e: PyException) {
                interpreterOutput = e.message.toString()
            } catch (throwable: Throwable) {
                throwable.printStackTrace()
            }

            AppTheme {
                // A surface container using the 'background' color from the theme
                Scaffold(bottomBar = { BottomBar() }) {
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(it)) {
                        Text(
                            text = interpreterOutput
                        )
                    }
                }
            }
        }
    }
}

data class MainBottomBarItemData(
    val title: String,
    val icon: ImageVector
)

@Composable
fun BottomBar() {
    var selectedItem by remember { mutableStateOf(0) }
    val items = listOf(
        MainBottomBarItemData("Camera", Icons.Filled.CameraAlt),
        MainBottomBarItemData("File", Icons.Filled.FileCopy)
    )

    NavigationBar {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title, color =  MaterialTheme.colorScheme.onBackground) },
                selected = selectedItem == index,
                onClick = { selectedItem = index }
            )                    }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

/*
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AppTheme {
        Greeting("Android")
    }
}
*/