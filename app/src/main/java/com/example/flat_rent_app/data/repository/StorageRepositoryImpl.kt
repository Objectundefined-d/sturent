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
    private val context: Context  // Внедряем Context вместо ContentResolver
) : StorageRepository {

    companion object {
        private const val MAX_IMAGE_SIZE = 1024 * 1024 // 1 MB
        private const val IMAGE_QUALITY = 80 // 80% качество
    }

    // Получаем ContentResolver из Context
    private val contentResolver: ContentResolver
        get() = context.contentResolver

    override suspend fun uploadProfileImage(userId: String, imageUri: Uri): Result<String> {
        return try {
            // Сжимаем изображение
            val compressedImage = compressImage(imageUri)

            // Создаем путь: profile_images/user_id/photo_123456789.jpg
            val fileName = "photo_${System.currentTimeMillis()}.jpg"
            val path = "profile_images/$userId/$fileName"
            val ref = storage.reference.child(path)

            // Загружаем сжатое изображение
            ref.putBytes(compressedImage).await()

            // Получаем URL
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

            // Путь: property_images/user_id/property_id/photo_123456789.jpg
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

    // Вспомогательный метод для сжатия изображений
    private suspend fun compressImage(uri: Uri): ByteArray {
        val inputStream = contentResolver.openInputStream(uri)
        val originalBitmap = BitmapFactory.decodeStream(inputStream)
        inputStream?.close()

        // Сжимаем bitmap
        val stream = ByteArrayOutputStream()
        originalBitmap.compress(Bitmap.CompressFormat.JPEG, IMAGE_QUALITY, stream)

        // Если все еще больше MAX_IMAGE_SIZE, сжимаем сильнее
        var bytes = stream.toByteArray()
        var quality = IMAGE_QUALITY

        while (bytes.size > MAX_IMAGE_SIZE && quality > 10) {
            stream.reset()
            quality -= 10
            originalBitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream)
            bytes = stream.toByteArray()
        }

        return bytes
    }
}