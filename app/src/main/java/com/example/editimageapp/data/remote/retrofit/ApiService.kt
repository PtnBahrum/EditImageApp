package com.example.editimageapp.data.remote.retrofit

import com.example.editimageapp.data.model.MemeResponse
import retrofit2.http.GET

interface ApiService {
    @GET("get_memes")
    suspend fun getMemeList(): MemeResponse
}