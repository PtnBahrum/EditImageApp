package com.example.editimageapp.data.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.example.editimageapp.data.Result
import com.example.editimageapp.data.MemeDatabase
import com.example.editimageapp.data.model.MemeData
import com.example.editimageapp.data.remote.retrofit.ApiConfig
import com.example.editimageapp.data.remote.retrofit.ApiService

class MemeRepository private constructor(
    private val apiService: ApiService,
    private val database: MemeDatabase,
) {
    fun getListMemes(showLimit: Int? = null) = liveData {
        emit(Result.Loading)
        try {
            val memeResponse = apiService.getMemeList()

            // Save with DAO
            val memes = memeResponse.data.memes
            database.memeDao().deleteAllMeme()
            database.memeDao().insertMemes(memes)
        } catch (e: Exception) {
            emit(Result.Error(e.toString()))
        }

        // Get meme list from local database
        val localData: LiveData<Result<List<MemeData>>> = if (showLimit != null) {
            database.memeDao().getListMemesRandom(showLimit).map { Result.Success(it) }
        } else {
            database.memeDao().getListMemes().map { Result.Success(it) }
        }
        emitSource(localData)
    }

    fun getMeme(id: String) = liveData {
        emit(Result.Loading)
        try {
            // Get meme data from local database
            val memeData = database.memeDao().getMemeById(id).value
            if (memeData != null) {
                emit(Result.Success(memeData))
            } else {
                emit(Result.Error("Meme with id $id not found in the local database"))
            }
        } catch (e: Exception) {
            emit(Result.Error(e.toString()))
        }
    }

    companion object {
        @Volatile
        private var instance: MemeRepository? = null

        fun getInstance(
            apiService: ApiService,
            database: MemeDatabase,
            ): MemeRepository = instance ?: synchronized(this) {
            instance ?: MemeRepository(apiService, database)
        }.also { instance = it }
    }
}