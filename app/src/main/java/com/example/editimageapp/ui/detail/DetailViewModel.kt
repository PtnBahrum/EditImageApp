package com.example.editimageapp.ui.detail

import androidx.lifecycle.ViewModel
import com.example.editimageapp.data.repository.MemeRepository

class DetailViewModel(private val repository: MemeRepository) : ViewModel() {
    fun getMeme(id: String) = repository.getMeme(id)
}