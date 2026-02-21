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
    @Json(name = "file_name") val file_name: String,
    @Json(name = "file_url") val file_url: String,
    @Json(name = "file_size") val file_size: Long,
    @Json(name = "original_name") val original_name: String
)

@JsonClass(generateAdapter = true)
data class HealthResponse(
    @Json(name = "status") val status: String,
    @Json(name = "upload_dir") val upload_dir: String,
    @Json(name = "max_file_size") val max_file_size: Int
)