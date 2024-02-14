package com.example.editimageapp.ui.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.example.editimageapp.R
import com.example.editimageapp.adapter.MemeListAdapter
import com.example.editimageapp.data.Result
import com.example.editimageapp.databinding.ActivityHomeBinding
import com.example.editimageapp.utils.MemeViewModelFactory

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding

    private val viewModel: HomeViewModel by viewModels {
        MemeViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        binding= DataBindingUtil.setContentView(this,R.layout.activity_home)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupSwipeRefreshLayout()
        observeMemeList()
        refreshData()
    }

    private fun setupSwipeRefreshLayout() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            refreshData()
        }
    }

    private fun refreshData() {
        viewModel.getListMeme().observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.swipeRefreshLayout.isRefreshing = true
                }

                is Result.Success -> {
                    val memeListAdapter = MemeListAdapter(result.data)
                    binding.recyclerView.adapter = memeListAdapter
                    binding.swipeRefreshLayout.isRefreshing = false
                }

                is Result.Error -> {
                    binding.swipeRefreshLayout.isRefreshing = false
                    Log.d(TAG, result.toString())
                }
            }
        }
    }

    private fun observeMemeList() {
        binding.recyclerView.layoutManager = GridLayoutManager(this, 3)
    }

    companion object {
        private const val TAG = "HomeActivity"
    }
}