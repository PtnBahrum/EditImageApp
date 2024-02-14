package com.example.editimageapp.helper

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.FutureTarget
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.OutputStream

object ImageUriConverter {

    interface ImageUriCallback {
        fun onImageUriReady(uri: Uri?)
        fun onImageUriError(error: Exception)
    }

    fun convertImageUrlToUri(context: Context, imageUrl: String, callback: ImageUriCallback) {
        val mainHandler = Handler(Looper.getMainLooper())
        Thread {
            try {
                val futureTarget: FutureTarget<Bitmap> = Glide.with(context)
                    .asBitmap()
                    .load(imageUrl)
                    .submit()

                val bitmap = futureTarget.get()

                // Save bitmap to a temporary file
                val file = File.createTempFile("temp_image", ".jpg", context.cacheDir)
                file.deleteOnExit()
                file.outputStream().use { outputStream ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                }

                mainHandler.post {
                    callback.onImageUriReady(Uri.fromFile(file))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                mainHandler.post {
                    callback.onImageUriError(e)
                }
            }
        }.start()
    }
}

object ImageSaver {

    fun saveImageToGallery(context: Context, bitmap: Bitmap, displayName: String) {
        GlobalScope.launch(Dispatchers.IO) {
            // Tentukan direktori penyimpanan
            val imagesDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val imageFile = File(imagesDirectory, "$displayName.jpg")

            // Simpan bitmap ke penyimpanan eksternal
            var outputStream: OutputStream? = null
            try {
                outputStream = imageFile.outputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                outputStream.flush()

                // Tambahkan gambar ke galeri
                val values = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, displayName)
                    put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                    put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }
                context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

                // Beritahu pengguna bahwa gambar telah disimpan
                GlobalScope.launch(Dispatchers.Main) {
                    Toast.makeText(context, "Gambar disimpan di galeri", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                GlobalScope.launch(Dispatchers.Main) {
                    Toast.makeText(context, "Gagal menyimpan gambar", Toast.LENGTH_SHORT).show()
                }
            } finally {
                outputStream?.close()
            }
        }
    }
}

object UriToBitmapConverter {

    fun convertUriToBitmap(context: Context, uri: Uri, callback: BitmapCallback) {
        GlobalScope.launch(Dispatchers.IO) {
            var bitmap: Bitmap? = null
            try {
                val contentResolver: ContentResolver = context.contentResolver
                val inputStream = contentResolver.openInputStream(uri)
                bitmap = BitmapFactory.decodeStream(inputStream)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            GlobalScope.launch(Dispatchers.Main) {
                callback.onBitmapReady(bitmap)
            }
        }
    }

    interface BitmapCallback {
        fun onBitmapReady(bitmap: Bitmap?)
    }
}