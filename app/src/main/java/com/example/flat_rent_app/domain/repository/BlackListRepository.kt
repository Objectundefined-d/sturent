package com.example.flat_rent_app.domain.repository

import kotlinx.coroutines.flow.Flow

interface BlackListRepository {
    suspend fun blockUser(userId: String): Result<Unit>
    suspend fun unblockUser(userId: String): Result<Unit>
    fun observeBlockedUsers(): Flow<List<String>>
    suspend fun isUserBlocked(userId: String): Boolean
}