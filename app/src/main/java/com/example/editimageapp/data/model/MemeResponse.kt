package com.example.editimageapp.data.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

data class MemeResponse(
    val success: Boolean,
    val data: MemeDataWrapper
)

data class MemeDataWrapper(
    val memes: List<MemeData>
)

@Parcelize
@Entity(tableName = "meme")
data class MemeData(
    @PrimaryKey
    val id: String,
    val name: String,
    val url: String,
    val width: Int,
    val height: Int,
    val box_count: Int,
    val captions: Int
) : Parcelable