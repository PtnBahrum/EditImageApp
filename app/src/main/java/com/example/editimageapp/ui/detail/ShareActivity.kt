package com.example.editimageapp.ui.detail

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.facebook.share.model.SharePhoto
import com.facebook.share.model.SharePhotoContent
import com.facebook.share.widget.ShareDialog
import com.example.editimageapp.R
import com.example.editimageapp.databinding.ActivityShareBinding
import com.example.editimageapp.helper.UriToBitmapConverter


class ShareActivity : AppCompatActivity() {

    private lateinit var binding: ActivityShareBinding
    private lateinit var imageUri: Uri
    private lateinit var shareDialog: ShareDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_share)

        val memeUri = intent.getStringExtra(MEME_URI)
        imageUri = Uri.parse(memeUri)

        binding.back.setOnClickListener {
            onBackPressed()
        }

        shareDialog = ShareDialog(this)

        binding.shareToFacebook.setOnClickListener {
            shareToFacebook()
        }

        binding.shareToTwitter.setOnClickListener {
        }

        loadImage()
    }

    private fun loadImage() {
        Glide.with(this)
            .load(imageUri)
            .into(binding.editedImage)
    }

    private fun shareToFacebook() {
        UriToBitmapConverter.convertUriToBitmap(this@ShareActivity, imageUri, object : UriToBitmapConverter.BitmapCallback {
            override fun onBitmapReady(bitmap: Bitmap?) {
                bitmap?.let {
                    val photo = SharePhoto.Builder()
                        .setBitmap(it)
                        .build()

                    val photoContent = SharePhotoContent.Builder()
                        .addPhoto(photo)
                        .build()

                    shareDialog.show(photoContent)
                } ?: run {
                    Toast.makeText(this@ShareActivity, "Failed to retrieve image", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    companion object {
        const val MEME_URI = "meme_uri"
    }
}