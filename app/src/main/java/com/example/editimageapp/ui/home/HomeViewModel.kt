package com.example.editimageapp.ui.home

import androidx.lifecycle.ViewModel
import com.example.editimageapp.data.repository.MemeRepository

class HomeViewModel(private val repository: MemeRepository) : ViewModel() {
    fun getListMeme(showLimit: Int? = null) = repository.getListMemes(showLimit)
}