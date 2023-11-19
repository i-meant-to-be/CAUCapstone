package com.caucapstone.app.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.caucapstone.app.R
import com.caucapstone.app.data.room.DatabaseModule
import com.caucapstone.app.data.room.Image
import com.chaquo.python.PyException
import com.chaquo.python.Python
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import java.io.ByteArrayOutputStream
import javax.inject.Inject

@HiltViewModel
class FileViewModel @Inject constructor(
    application: Application
) : ViewModel() {
    private val _py = Python.getInstance()
    private val _output = mutableStateOf("")
    private val _bitmap: Bitmap = BitmapFactory.decodeResource(application.applicationContext.resources, R.drawable.test)
    private val _databaseDao = DatabaseModule
        .provideAppDatabase(application.applicationContext)
        .imageDao()
    val output: State<String> = _output
    val bitmap: Bitmap = _bitmap

    fun getImages(): Flow<List<Image>> {
        return _databaseDao.getImages()
    }

    fun testFunc() {
        val module = this._py.getModule("integration_test")
        val sys = this._py.getModule("sys")
        val io = this._py.getModule("io")
        val textOutputStream = io.callAttr("StringIO")
        val byteArrayOutputStream = ByteArrayOutputStream()

        try {
            sys.put("stdout", textOutputStream)

            // Drawable - Bitmap - ByteArray - String
            _bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)

            // String - ByteArray - Bitmap
            val bitmapDestByteArray =
                module.callAttrThrows(
                    "test",
                    byteArrayOutputStream.toByteArray().toString(Charsets.UTF_8),
                    1365, 2048)
                    .toJava(ByteArray::class.java)
            BitmapFactory.decodeByteArray(bitmapDestByteArray, 0, bitmapDestByteArray.size)
            _output.value = "Success"
        } catch (e: PyException) {
            _output.value = e.message.toString()
        } catch (throwable: Throwable) {
            throwable.printStackTrace()
        }
    }
}