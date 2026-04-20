package com.example.flat_rent_app.domain.repository

import com.example.flat_rent_app.domain.model.Match
import com.example.flat_rent_app.util.LikeOutCome
import kotlinx.coroutines.flow.Flow

interface SwipeRepository {
    suspend fun likeUser(targetId: String): Result<LikeOutCome>

    suspend fun passUser(targetId: String): Result<Unit>

    fun observeMatches(): Flow<List<Match>>

    suspend fun getUnseenMatch() : Match?

    suspend fun markMatchAsSeen(matchId: String) : Result<Unit>

    suspend fun addToFavorites(userId: String) : Result<Unit>

    fun observeFavorites() : Flow<List<String>>

    suspend fun removeFromFavorites(userId: String) : Result<Unit>
}
