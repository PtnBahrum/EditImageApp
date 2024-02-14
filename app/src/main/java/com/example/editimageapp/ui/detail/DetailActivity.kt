package com.example.editimageapp.ui.detail

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.dsphotoeditor.sdk.activity.DsPhotoEditorActivity
import com.dsphotoeditor.sdk.utils.DsPhotoEditorConstants
import com.example.editimageapp.R
import com.example.editimageapp.databinding.ActivityDetailBinding
import com.example.editimageapp.helper.ImageSaver
import com.example.editimageapp.helper.ImageUriConverter
import com.example.editimageapp.helper.UriToBitmapConverter


class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private lateinit var imageUri: Uri

    private val requestWriteExternalStoragePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            saveImage()
        } else {
            Toast.makeText(this, "Permission denied to save image", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= DataBindingUtil.setContentView(this,R.layout.activity_detail)

        val memeUrl = intent.getStringExtra(MEME_URL)

        val dsPhotoEditorIntent = Intent(this, DsPhotoEditorActivity::class.java)

        ImageUriConverter.convertImageUrlToUri(
            this,
            memeUrl!!,
            object : ImageUriConverter.ImageUriCallback {
                override fun onImageUriReady(uri: Uri?) {
                    uri?.let {
                        Glide.with(applicationContext).load(uri).into(binding.editedImage)
                        dsPhotoEditorIntent.data = uri
                        dsPhotoEditorIntent.putExtra(DsPhotoEditorConstants.DS_PHOTO_EDITOR_OUTPUT_DIRECTORY, "Edited Image")

                        startActivityForResult(dsPhotoEditorIntent, 100)
                    }
                }

                override fun onImageUriError(error: Exception) {
                    Toast.makeText(this@DetailActivity, "Failed to load image", Toast.LENGTH_SHORT).show()
                }
            })

        binding.saveButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestWriteExternalStoragePermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            } else {
                saveImage()
            }
        }

        binding.back.setOnClickListener {
            onBackPressed()
        }

        binding.share.setOnClickListener{
            val intent = Intent(this@DetailActivity, ShareActivity::class.java)
            intent.putExtra(ShareActivity.MEME_URI, imageUri)
            startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == 100) {
            val outputUri: Uri? = data?.data
            binding.editedImage.setImageURI(outputUri)
            outputUri?.let {
                imageUri = it
            }
        }
    }

    private fun saveImage() {
        UriToBitmapConverter.convertUriToBitmap(this, imageUri, object : UriToBitmapConverter.BitmapCallback {
            override fun onBitmapReady(bitmap: Bitmap?) {
                bitmap?.let {
                    ImageSaver.saveImageToGallery(this@DetailActivity, it, "Edited_Image")
                    Toast.makeText(this@DetailActivity, "Image saved to Gallery", Toast.LENGTH_SHORT).show()
                } ?: run {
                    Toast.makeText(this@DetailActivity, "Failed to save image", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    companion object {
        const val MEME_URL = "meme_url"
    }
}