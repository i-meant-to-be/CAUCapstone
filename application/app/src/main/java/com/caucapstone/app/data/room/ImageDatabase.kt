package com.caucapstone.app.data.room

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.Update
import javax.inject.Inject

@Dao
interface ImageDatabaseDao {
    @Query("SELECT * from image_table")
    fun getImages(): List<Image>

    @Query("SELECT * from image_table WHERE image_id = :id")
    fun getImageById(id: String): Image

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(image: Image)

    @Query("DELETE from image_table")
    suspend fun deleteAll()

    @Query("DELETE from image_table WHERE image_id = :id")
    suspend fun deleteById(id: String)

    @Update
    suspend fun update(image: Image)

    @Query("SELECT * from image_table WHERE image_id = :id")
    fun isUUIDExists(id: String): List<Image>
}

@TypeConverters(Converters::class)
@Database(entities = [Image::class], version = 1, exportSchema = false)
abstract class ImageDatabase: RoomDatabase() {
    abstract fun imageDao(): ImageDatabaseDao
}

class ImageRepository @Inject constructor(private val imageDatabaseDao: ImageDatabaseDao) {
    fun allImages(): List<Image> = imageDatabaseDao.getImages()
    fun getImageById(id: String): Image = imageDatabaseDao.getImageById(id)

    suspend fun insert(image: Image) = imageDatabaseDao.insert(image)
    suspend fun deleteAll() = imageDatabaseDao.deleteAll()
    suspend fun deleteById(id: String) = imageDatabaseDao.deleteById(id)
    suspend fun update(image: Image) = imageDatabaseDao.update(image)
    fun isUUIDExists(id: String) = imageDatabaseDao.isUUIDExists(id)
}