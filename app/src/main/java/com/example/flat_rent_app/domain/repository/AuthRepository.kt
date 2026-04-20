package com.example.flat_rent_app.domain.repository

import com.example.flat_rent_app.domain.model.AuthUser
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: Flow<AuthUser?>
    fun currentUid(): String?
    fun currentUserEmail(): String?
    suspend fun sendPasswordReset(email: String): Result<Unit>
    suspend fun sendEmailVerification(): Result<Unit>
    suspend fun updateEmail(newEmail: String, password: String): Result<Unit>
    suspend fun signIn(email: String, password: String): Result<AuthUser>
    suspend fun signUp(email: String, password: String): Result<AuthUser>
    suspend fun signOut()
    suspend fun deleteAccount(): Result<Unit>
}
