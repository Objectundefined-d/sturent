package com.example.flat_rent_app.domain.repository

import android.net.Uri
import kotlinx.coroutines.flow.Flow

interface StorageRepository {
    suspend fun uploadProfileImage(userId: String, imageUri: Uri): Result<String>
    suspend fun uploadPropertyImage(userId: String, propertyId: String, imageUri: Uri): Result<String>
    suspend fun deleteImage(imageUrl: String): Result<Unit>
    suspend fun getDownloadUrl(path: String): Result<String>
    fun getImageUrl(path: String): String
}
