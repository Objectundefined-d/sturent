package com.example.flat_rent_app.domain.repository

import com.example.flat_rent_app.domain.model.Chat
import com.example.flat_rent_app.domain.model.Message
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun observeMyChats(): Flow<List<Chat>>
    fun observeMessages(chatId: String, limit: Long = 50L): Flow<List<Message>>

    suspend fun sendMessage(chatId: String, otherId: String, text: String): Result<Unit>
    suspend fun markRead(chatId: String): Result<Unit>
    suspend fun deleteChat(chatId: String, otherUid: String, forBoth: Boolean): Result<Unit>
    suspend fun clearHistory(chatId: String, forBoth: Boolean): Result<Unit>
    suspend fun deleteMessage(chatId: String, messageId: String, forBoth: Boolean): Result<Unit>
    suspend fun editMessage(chatId: String, messageId: String, newText: String): Result<Unit>
}
