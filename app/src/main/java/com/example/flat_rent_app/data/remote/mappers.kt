package com.example.flat_rent_app.data.remote

import com.example.flat_rent_app.data.remote.dto.PhotoUrlsDto
import com.example.flat_rent_app.domain.model.ProfilePhoto
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

fun PhotoUrlsDto.toDomain(nowMillis: Long = System.currentTimeMillis()): ProfilePhoto =
    ProfilePhoto(
        fullUrl = fullUrl,
        thumbUrl = thumbUrl,
        updatedAt = nowMillis
    )

fun File.asImage(fieldName: String = "file"): MultipartBody.Part{
    val body = asRequestBody("image/jpeg".toMediaType())
    return MultipartBody.Part.createFormData(fieldName, name, body)
}
