package com.example.flat_rent_app.data.remote.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface PhotoApi {
    @Multipart
    @POST("api/photos/upload")
    suspend fun uploadPhoto(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part
    ): Response<UploadResponse>

    @GET("api/photos/health")
    suspend fun healthCheck(): Response<HealthResponse>
}

@JsonClass(generateAdapter = true)
data class UploadResponse(
    @Json(name = "success") val success: Boolean,
    @Json(name = "data") val data: PhotoData
)

@JsonClass(generateAdapter = true)
data class PhotoData(
    @Json(name = "file_name") val fileName: String,
    @Json(name = "file_url") val fileUrl: String,
    @Json(name = "file_size") val fileSize: Long,
    @Json(name = "original_name") val originalName: String
)

@JsonClass(generateAdapter = true)
data class HealthResponse(
    @Json(name = "status") val status: String,
    @Json(name = "upload_dir") val uploadDir: String,
    @Json(name = "max_file_size") val maxFileSize: Int
)
