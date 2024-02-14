package com.example.editimageapp.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.editimageapp.data.model.MemeData
import com.example.editimageapp.databinding.ItemMemeBinding
import com.example.editimageapp.ui.detail.DetailActivity

class MemeListAdapter(
    private val listMemeItem: List<MemeData>
) : RecyclerView.Adapter<MemeListAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemMemeBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMemeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = listMemeItem.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = listMemeItem[position]

        holder.apply {
            Glide.with(itemView.context)
                .load(currentItem.url)
                .into(binding.ivMeme)

            itemView.setOnClickListener {
                val intent = Intent(it.context, DetailActivity::class.java)
                intent.putExtra(DetailActivity.MEME_URL, currentItem.url)
                it.context.startActivity(intent)
            }
        }
    }
}