package com.example.androidfinal.di

import com.example.androidfinal.api.CinemaApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
class ApiModule {

    @Provides
    fun provideCinemaAPI(): CinemaApi {
        return CinemaApi.getInstance()
    }
}