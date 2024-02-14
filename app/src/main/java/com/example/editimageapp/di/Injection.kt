package com.example.editimageapp.di

import android.content.Context
import com.example.editimageapp.data.MemeDatabase
import com.example.editimageapp.data.remote.retrofit.ApiConfig
import com.example.editimageapp.data.repository.MemeRepository

object Injection {
    fun provideRepository(context: Context): MemeRepository {
        val apiService = ApiConfig.getApiService()
        val database = MemeDatabase.getDatabase(context)

        return MemeRepository.getInstance(apiService, database)
    }
}