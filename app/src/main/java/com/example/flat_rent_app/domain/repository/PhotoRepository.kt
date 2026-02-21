package com.example.flat_rent_app.domain.repository

import android.net.Uri
import com.example.flat_rent_app.domain.model.ProfilePhoto
import java.io.File

interface PhotoRepository {
    // Загрузка фото из Uri (для выбора из галереи)
    suspend fun uploadPhoto(uri: Uri): Result<String>

    // Загрузка фото с указанием ID (для существующего файла)
    suspend fun uploadPhoto(photoId: Int, file: File): Result<ProfilePhoto>

    // Получение списка всех фото профиля
    suspend fun listProfilePhotos(): Result<List<ProfilePhoto>>

    // Удаление фото по ID
    suspend fun deletePhoto(photoId: Int): Result<Unit>

    // Проверка соединения с сервером
    suspend fun testConnection(): Boolean
}