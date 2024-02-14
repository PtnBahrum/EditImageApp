package com.example.editimageapp.utils

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.editimageapp.data.repository.MemeRepository
import com.example.editimageapp.di.Injection
import com.example.editimageapp.ui.detail.DetailViewModel
import com.example.editimageapp.ui.home.HomeViewModel

class MemeViewModelFactory private constructor(private val memeRepository: MemeRepository) :
    ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(memeRepository) as T
        }else if (modelClass.isAssignableFrom(DetailViewModel::class.java)) {
            return DetailViewModel(memeRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }

    companion object {
        @Volatile
        private var instance: MemeViewModelFactory? = null

        fun getInstance(context: Context): MemeViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: MemeViewModelFactory(Injection.provideRepository(context))
            }.also { instance = it }
    }
}