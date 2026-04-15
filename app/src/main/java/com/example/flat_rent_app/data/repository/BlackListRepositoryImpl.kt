package com.example.flat_rent_app.data.repository

import com.example.flat_rent_app.domain.repository.AuthRepository
import com.example.flat_rent_app.domain.repository.BlackListRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.emptyList

@Singleton
class BlackListRepositoryImpl @Inject constructor(
    private val db: FirebaseFirestore,
    private val authRepo: AuthRepository
) : BlackListRepository {

    override suspend fun blockUser(userId: String): Result<Unit> =
        runCatching {
            val myUid = authRepo.currentUid() ?: throw IllegalStateException("Не авторизован")

            db.collection("blackList")
                .document(myUid)
                .collection("items")
                .document(userId)
                .set(
                    mapOf(
                        "uid" to userId,
                        "blockedAtMillis" to System.currentTimeMillis()
                    )
                ).await()
        }

    override suspend fun unblockUser(userId: String): Result<Unit> =
        runCatching {
            val myUid = authRepo.currentUid() ?: throw IllegalStateException("Не авторизован")

            db.collection("blackList")
                .document(myUid)
                .collection("items")
                .document(userId)
                .delete().await()

            val chatId = listOf(myUid, userId).sorted().joinToString("_")
            val messages = db.collection("chats").document(chatId)
                .collection("messages").whereEqualTo("senderUid", userId)
                .whereEqualTo("blockedMessage", true).get().await()

            val batch = db.batch()
            messages.documents.forEach { batch.delete(it.reference) }
            batch.commit().await()
        }

    override fun observeBlockedUsers(): Flow<List<String>> = callbackFlow {
        val myUid = authRepo.currentUid() ?: run { trySend(emptyList()); close(); return@callbackFlow }

        val reg = db.collection("blackList").document(myUid)
            .collection("items")
            .addSnapshotListener { snap, _ ->
                val list = snap?.documents?.mapNotNull { it.getString("uid")  } ?: emptyList()
                trySend(list)
            }
        awaitClose { reg.remove() }
    }

    override suspend fun isUserBlocked(userId: String): Boolean {
        val myUid = authRepo.currentUid() ?: throw IllegalStateException("Не авторизован")

        val meBlockUser = db.collection("blackList").document(myUid)
            .collection("items").document(userId).get().await().exists()

        val userBlockMe = db.collection("blackList").document(userId)
            .collection("items").document(myUid).get().await().exists()

        return meBlockUser || userBlockMe
    }
}