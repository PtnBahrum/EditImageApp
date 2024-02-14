package com.example.editimageapp.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.editimageapp.data.model.MemeData

@Dao
interface MemeDao {
    @Query("SELECT * FROM meme")
    fun getListMemes(): LiveData<List<MemeData>>

    @Query("SELECT * FROM meme ORDER BY RANDOM() LIMIT :limit")
    fun getListMemesRandom(limit: Int): LiveData<List<MemeData>>

    @Query("SELECT * FROM meme WHERE id = :id")
    fun getMemeById(id: String): LiveData<MemeData>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemes(memes: List<MemeData>)

    @Query("DELETE FROM meme")
    suspend fun deleteAllMeme()
}