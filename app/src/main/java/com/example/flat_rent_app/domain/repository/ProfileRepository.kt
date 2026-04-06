package com.example.flat_rent_app.domain.repository

import com.example.flat_rent_app.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    fun observerProfile(uid: String): Flow<UserProfile?>

    suspend fun upsertMyProfile(profile: UserProfile): Result<Unit>

    suspend fun getFeedProfiles(limit: Int = 50): Result<List<UserProfile>>

    suspend fun saveFcmToken(token: String)
}