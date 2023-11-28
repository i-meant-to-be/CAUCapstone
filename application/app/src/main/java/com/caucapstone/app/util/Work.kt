package com.caucapstone.app.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.caucapstone.app.data.room.DatabaseModule
import com.caucapstone.app.data.room.Image
import com.chaquo.python.Python
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.util.UUID

class ImageProcessWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {
        val imagePathInput =
            inputData.getString("imagePath") ?: return Result.failure()

        // Load python module
        val py = Python.getInstance()
        val module = py.getModule("test")

        // Declare variables required to process image
        val byteArrayOutputStream = ByteArrayOutputStream()
        val inputBitmap = withContext(Dispatchers.IO) {
            BitmapFactory.decodeStream(FileInputStream(File(imagePathInput)))
        }
        inputBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)

        // Process image
        val encodedImage = module.callAttr(
            "test",
            byteArrayOutputStream.toByteArray(),
            inputBitmap.height,
            inputBitmap.width
        ).toString().substring(2)
        val imageBytes = Base64.decode(encodedImage, Base64.DEFAULT)
        val resultBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)

        // Save processed image to the device's internal storage
        val outputStream = applicationContext.openFileOutput("${id}.png", Context.MODE_PRIVATE)
        resultBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)

        // Add image's data to the database
        val database = DatabaseModule.provideAppDatabase(applicationContext)
        var uuid = UUID.randomUUID()
        var isExists = true
        while (isExists) {
            val queryResult = database.imageDao().isUUIDExists(uuid.toString())
            if (queryResult.isEmpty()) {
                isExists = false
            } else {
                uuid = UUID.randomUUID()
                continue
            }
        }
        database.imageDao().insert(
            Image(
                id = uuid.toString(),
                caption = "윤곽선 처리",
                originId = imagePathInput,
            )
        )

        withContext(Dispatchers.IO) {
            byteArrayOutputStream.close()
            outputStream.close()
        }

        return Result.success()
    }

}