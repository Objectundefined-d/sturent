package com.example.flat_rent_app.data.repository

import android.content.Context
import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.example.flat_rent_app.domain.repository.StorageRepository
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StorageRepositoryImpl @Inject constructor(
    private val storage: FirebaseStorage,
    private val context: Context
) : StorageRepository {

    companion object {
        private const val MAX_IMAGE_SIZE = 1024 * 1024
        private const val IMAGE_QUALITY = 80
        private const val MIN_JPEG_QUALITY = 10
        private const val JPEG_QUALITY_STEP = 10
    }

    private val contentResolver: ContentResolver
        get() = context.contentResolver

    override suspend fun uploadProfileImage(userId: String, imageUri: Uri): Result<String> {
        return try {
            val compressedImage = compressImage(imageUri)

            val fileName = "photo_${System.currentTimeMillis()}.jpg"
            val path = "profile_images/$userId/$fileName"
            val ref = storage.reference.child(path)

            ref.putBytes(compressedImage).await()

            val downloadUrl = ref.downloadUrl.await()

            Result.success(downloadUrl.toString())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun uploadPropertyImage(
        userId: String,
        propertyId: String,
        imageUri: Uri
    ): Result<String> {
        return try {
            val compressedImage = compressImage(imageUri)

            val fileName = "photo_${System.currentTimeMillis()}.jpg"
            val path = "property_images/$userId/$propertyId/$fileName"
            val ref = storage.reference.child(path)

            ref.putBytes(compressedImage).await()
            val downloadUrl = ref.downloadUrl.await()

            Result.success(downloadUrl.toString())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteImage(imageUrl: String): Result<Unit> {
        return try {
            val ref = storage.getReferenceFromUrl(imageUrl)
            ref.delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getDownloadUrl(path: String): Result<String> {
        return try {
            val ref = storage.reference.child(path)
            val url = ref.downloadUrl.await()
            Result.success(url.toString())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getImageUrl(path: String): String {
        return storage.reference.child(path).toString()
    }

    private suspend fun compressImage(uri: Uri): ByteArray {
        val inputStream = contentResolver.openInputStream(uri)
        val originalBitmap = BitmapFactory.decodeStream(inputStream)
        inputStream?.close()

        val stream = ByteArrayOutputStream()
        originalBitmap.compress(Bitmap.CompressFormat.JPEG, IMAGE_QUALITY, stream)

        var bytes = stream.toByteArray()
        var quality = IMAGE_QUALITY

        while (bytes.size > MAX_IMAGE_SIZE && quality > MIN_JPEG_QUALITY) {
            stream.reset()
            quality -= JPEG_QUALITY_STEP
            originalBitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream)
            bytes = stream.toByteArray()
        }

        return bytes
    }
}
