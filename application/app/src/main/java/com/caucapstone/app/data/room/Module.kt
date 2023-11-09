package com.caucapstone.app.data.room

import android.content.Context
import androidx.room.Room
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

object DatabaseModule {
    @Singleton
    @Provides
    fun provideImageDao(imageDatabase: ImageDatabase) : ImageDatabaseDao
        = imageDatabase.imageDao()

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context) : ImageDatabase
        = Room.databaseBuilder(
            context,
            ImageDatabase::class.java,
            "image_database"
        )
        .fallbackToDestructiveMigration()
        .build()
}