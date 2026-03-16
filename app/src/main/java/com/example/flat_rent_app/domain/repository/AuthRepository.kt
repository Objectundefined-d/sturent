package com.example.flat_rent_app.domain.repository

import com.example.flat_rent_app.domain.model.AuthUser
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: Flow<AuthUser?>
    fun currentUid(): String?

    suspend fun signIn(email: String, password: String): Result<AuthUser>
    suspend fun signUp(email: String, password: String): Result<AuthUser>
    suspend fun signOut()

    suspend fun deleteAccount(): Result<Unit>
}