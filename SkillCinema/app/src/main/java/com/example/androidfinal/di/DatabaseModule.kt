package com.example.androidfinal.di

import android.content.Context
import androidx.room.Room
import com.example.androidfinal.db.CinemaDao
import com.example.androidfinal.db.CinemaDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {
    @Provides
    fun provideCinemaDatabase(@ApplicationContext context: Context): CinemaDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            CinemaDatabase::class.java,
            "Cinema.db"
        ).build()
    }

    @Provides
    fun provideCinemaDao(database: CinemaDatabase): CinemaDao {
        return database.cinemaDao()
    }
}