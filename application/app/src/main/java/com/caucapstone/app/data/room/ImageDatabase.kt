package com.caucapstone.app.data.room

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

@Dao
interface ImageDatabaseDao {
    @Query("SELECT * from image_table")
    fun getImages(): Flow<List<Image>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(image: Image)

    @Query("DELETE from image_table")
    suspend fun deleteAll()

    @Delete
    suspend fun delete(image: Image)
}

@TypeConverters(Converters::class)
@Database(entities = [Image::class], version = 1, exportSchema = false)
abstract class ImageDatabase: RoomDatabase() {
    abstract fun imageDao(): ImageDatabaseDao
}

class ImageRepository @Inject constructor(private val imageDatabaseDao: ImageDatabaseDao) {
    fun getImages(): Flow<List<Image>> = imageDatabaseDao.getImages().flowOn(Dispatchers.IO).conflate()

    suspend fun insert(image: Image) = imageDatabaseDao.insert(image)
    suspend fun deleteAll() = imageDatabaseDao.deleteAll()
    suspend fun delete(image: Image) = imageDatabaseDao.delete(image)
}