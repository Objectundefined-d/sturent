package com.example.flat_rent_app.data.repository

import android.content.Context
import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import com.example.flat_rent_app.core.FirebaseIdTokenProvider
import com.example.flat_rent_app.data.remote.api.PhotoApi
import com.example.flat_rent_app.data.remote.api.UploadResponse
import com.example.flat_rent_app.domain.model.ProfilePhoto
import com.example.flat_rent_app.domain.repository.PhotoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PhotoRepositoryImpl @Inject constructor(
    private val api: PhotoApi,
    private val tokenProvider: FirebaseIdTokenProvider,
    private val context: Context
) : PhotoRepository {

    companion object {
        private const val BASE_URL = "http://37.46.17.80"
        private const val MAX_IMAGE_SIZE = 5 * 1024 * 1024
    }

    override suspend fun uploadPhoto(uri: Uri): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val size = context.contentResolver.openFileDescriptor(uri, "r")?.statSize ?: 0
                if (size > MAX_IMAGE_SIZE) {
                    return@withContext Result.failure(Exception("Файл слишком большой. Максимум 5MB"))
                }

                val token = tokenProvider.getIdToken()
                    ?: return@withContext Result.failure(Exception("Пользователь не авторизован"))

                val file = uriToFile(uri)
                    ?: return@withContext Result.failure(Exception("Не удалось обработать изображение"))

                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                val part = MultipartBody.Part.createFormData("file", file.name, requestFile)

                val response = api.uploadPhoto("Bearer $token", part)

                if (response.isSuccessful && response.body()?.success == true) {
                    val imageUrl = response.body()!!.data.file_url
                    val fullUrl = if (imageUrl.startsWith("http")) imageUrl else BASE_URL + imageUrl
                    Result.success(fullUrl)
                } else {
                    Result.failure(Exception("Ошибка загрузки: ${response.code()} - ${response.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Ошибка при загрузке: ${e.message}", e))
            }
        }
    }

    override suspend fun uploadPhoto(photoId: Int, file: File): Result<ProfilePhoto> {
        return withContext(Dispatchers.IO) {
            try {
                if (file.length() > MAX_IMAGE_SIZE) {
                    return@withContext Result.failure(Exception("Файл слишком большой"))
                }

                val token = tokenProvider.getIdToken()
                    ?: return@withContext Result.failure(Exception("Пользователь не авторизован"))

                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                val part = MultipartBody.Part.createFormData("file", file.name, requestFile)

                val response = api.uploadPhoto("Bearer $token", part)

                if (response.isSuccessful && response.body()?.success == true) {
                    val uploadData = response.body()!!.data
                    val photo = ProfilePhoto(
                        fullUrl = if (uploadData.file_url.startsWith("http"))
                            uploadData.file_url
                        else
                            BASE_URL + uploadData.file_url,
                        thumbUrl = null,
                        updatedAt = System.currentTimeMillis()
                    )
                    Result.success(photo)
                } else {
                    Result.failure(Exception("Ошибка загрузки: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Ошибка при загрузке: ${e.message}", e))
            }
        }
    }

    override suspend fun listProfilePhotos(): Result<List<ProfilePhoto>> {
        return withContext(Dispatchers.IO) {
            try {
                val token = tokenProvider.getIdToken()
                    ?: return@withContext Result.failure(Exception("Пользователь не авторизован"))

                // Здесь должен быть запрос к вашему API для получения списка фото
                // Пример: val response = api.getUserPhotos("Bearer $token")

                // Пока возвращаем тестовые данные
                val mockPhotos = listOf(
                    ProfilePhoto(
                        fullUrl = "$BASE_URL/photos/photo1.jpg",
                        thumbUrl = "$BASE_URL/photos/thumb1.jpg",
                        updatedAt = System.currentTimeMillis()
                    ),
                    ProfilePhoto(
                        fullUrl = "$BASE_URL/photos/photo2.jpg",
                        thumbUrl = "$BASE_URL/photos/thumb2.jpg",
                        updatedAt = System.currentTimeMillis()
                    )
                )

                Result.success(mockPhotos)

                // нужно для получения списка фото:
                /*
                val response = api.getUserPhotos("Bearer $token")
                if (response.isSuccessful) {
                    Result.success(response.body()?.map { it.toDomain() } ?: emptyList())
                } else {
                    Result.failure(Exception("Ошибка получения списка: ${response.code()}"))
                }
                */
            } catch (e: Exception) {
                Result.failure(Exception("Ошибка при получении списка: ${e.message}", e))
            }
        }
    }

    override suspend fun deletePhoto(photoId: Int): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val token = tokenProvider.getIdToken()
                    ?: return@withContext Result.failure(Exception("Пользователь не авторизован"))

                // имитация успешного удаления
                Result.success(Unit)

                // Нужно для удаления (пока не трогаем, тк нереализовано):
                /*
                val response = api.deletePhoto("Bearer $token", photoId)
                if (response.isSuccessful) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Ошибка удаления: ${response.code()}"))
                }
                */
            } catch (e: Exception) {
                Result.failure(Exception("Ошибка при удалении: ${e.message}", e))
            }
        }
    }

    override suspend fun testConnection(): Boolean {
        return try {
            val response = api.healthCheck()
            response.isSuccessful && response.body()?.status == "ok"
        } catch (e: Exception) {
            false
        }
    }

    private fun uriToFile(uri: Uri): File? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val file = File.createTempFile("upload_", ".jpg", context.cacheDir)

            FileOutputStream(file).use { output ->
                inputStream?.copyTo(output)
            }

            inputStream?.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}