package com.caucapstone.app.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.chaquo.python.Python
import com.caucapstone.app.R
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.ByteArrayOutputStream
import javax.inject.Inject

class PythonModule @Inject constructor(
    @ApplicationContext private val context: Context
)  {
    private val py = Python.getInstance()
    private val module = py.getModule("integration_test")

    fun test(): Bitmap {
        // Drawable - Bitmap - ByteArray - String
        val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.test)
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        val bitmapOriginString = outputStream.toByteArray().toString(Charsets.US_ASCII)

        // String - ByteArray - Bitmap
        val bitmapDestByteArray =
            module.callAttr("test", bitmapOriginString, 1365, 2048)
                .toJava(ByteArray::class.java)

        return BitmapFactory.decodeByteArray(bitmapDestByteArray, 0, bitmapDestByteArray.size)
    }
}