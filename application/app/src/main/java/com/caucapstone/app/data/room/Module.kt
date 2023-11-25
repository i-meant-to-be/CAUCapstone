package com.caucapstone.app.data.room

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
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
        .allowMainThreadQueries()
        .fallbackToDestructiveMigration()
        .build()
}