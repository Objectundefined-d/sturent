package com.example.flat_rent_app.data.repository

import android.util.Log
import com.example.flat_rent_app.domain.model.Chat
import com.example.flat_rent_app.domain.model.Message
import com.example.flat_rent_app.domain.repository.AuthRepository
import com.example.flat_rent_app.domain.repository.ChatRepository
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.jvm.Throws

@Singleton
class ChatRepositoryImpl @Inject constructor(
    private val db: FirebaseFirestore,
    private val authRepo: AuthRepository
) : ChatRepository {

    override fun observeMyChats(): Flow<List<Chat>> = callbackFlow {
        val myUid = authRepo.currentUid()
        if (myUid == null) {
            trySend(emptyList()); close(); return@callbackFlow
        }

        val reg = db.collection("userChats")
            .document(myUid)
            .collection("items")
            .orderBy("lastMessageAtMillis", Query.Direction.DESCENDING)
            .addSnapshotListener { snap, err ->
                if (err != null || snap == null) {
                    trySend(emptyList()); return@addSnapshotListener
                }

                val list = snap.documents.mapNotNull { d ->
                    val otherUid = d.getString("otherUid") ?: return@mapNotNull null
                    Chat(
                        chatId = d.getString("chatId") ?: d.id,
                        otherUid = otherUid,
                        lastMessageText = d.getString("lastMessageText"),
                        lastMessageAt = d.getLong("lastMessageAtMillis"),
                        unreadCount = d.getLong("unreadCount") ?: 0L
                    )
                }
                trySend(list)
            }

        awaitClose { reg.remove() }
    }


    override fun observeMessages(chatId: String, limit: Long): Flow<List<Message>> = callbackFlow {
        val myUid = authRepo.currentUid().orEmpty()

        val reg = db.collection("chats").document(chatId)
            .collection("messages")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(limit)
            .addSnapshotListener { snap, err ->
                if (err != null || snap == null) {
                    trySend(emptyList()); return@addSnapshotListener
                }

                val list = snap.documents.mapNotNull { d ->
                    val senderUid = d.getString("senderUid") ?: return@mapNotNull null

                    val deletedFor = (d.get("deletedFor") as? List<*>) ?: emptyList<String>()
                    if (myUid in deletedFor) return@mapNotNull null

                    Message(
                        messageId = d.id,
                        senderUid = senderUid,
                        text = d.getString("text").orEmpty(),
                        type = d.getString("type") ?: "text",
                        createdAt = d.getLong("createdAt") ?: 0L
                    )
                }.reversed()

                trySend(list)
            }

        awaitClose { reg.remove() }
    }


    override suspend fun sendMessage(chatId: String, otherId: String, text: String): Result<Unit> =
        runCatching {
            val myUid = authRepo.currentUid() ?: throw IllegalStateException("Нет авторизации")
            if (text.isBlank()) throw IllegalArgumentException("Пустое сообщение")

            val now = System.currentTimeMillis()

            val chatRef = db.collection("chats").document(chatId)
            val msgRef = chatRef.collection("messages").document()

            val myIndex =
                db.collection("userChats").document(myUid).collection("items").document(chatId)
            val otherIndex =
                db.collection("userChats").document(otherId).collection("items").document(chatId)

            val batch = db.batch()

            batch.set(
                msgRef, mapOf(
                    "senderUid" to myUid,
                    "text" to text,
                    "type" to "text",
                    "createdAt" to now
                )
            )

            batch.set(
                chatRef, mapOf(
                    "lastMessageText" to text,
                    "lastMessageAt" to now,
                    "lastSenderUid" to myUid
                ), SetOptions.merge()
            )

            batch.set(
                myIndex, mapOf(
                    "lastMessageText" to text,
                    "lastMessageAt" to now
                ), SetOptions.merge()
            )

            batch.set(
                otherIndex, mapOf(
                    "lastMessageText" to text,
                    "lastMessageAt" to now,
                    "unreadCount" to FieldValue.increment(1)
                ), SetOptions.merge()
            )

            batch.commit().await()
            Unit
        }.recoverCatching { t ->
            throw RuntimeException(t.message ?: "Ошибка отправки", t)
        }

    override suspend fun markRead(chatId: String): Result<Unit> =
        runCatching {
            val myUid = authRepo.currentUid() ?: throw IllegalStateException("Не авторизован")

            db.collection("userChats").document(myUid)
                .collection("items").document(chatId)
                .set(mapOf("unreadCount" to 0L), SetOptions.merge())
                .await()

            Unit
        }.recoverCatching { t ->
            throw RuntimeException(t.message ?: "Ошибка markRead", t)
        }

    override suspend fun deleteChat(
        chatId: String,
        otherUid: String,
        forBoth: Boolean
    ): Result<Unit> =
        runCatching {
            val myUid = authRepo.currentUid() ?: throw IllegalStateException("Не авторизован")
            val batch = db.batch()

            if (forBoth) {
                val messages = db.collection("chats").document(chatId)
                    .collection("messages").get().await()

                messages.documents.forEach { doc ->
                    batch.delete(doc.reference)
                }

                batch.delete(db.collection("chats").document(chatId))

                batch.delete(
                    db.collection("userChats")
                        .document(myUid).collection("items").document(chatId)
                )

                batch.delete(
                    db.collection("userChats")
                        .document(otherUid).collection("items").document(chatId)
                )
            } else {

                batch.delete(
                    db.collection("userChats").document(myUid)
                        .collection("items").document(chatId)
                )
            }

            batch.commit().await()
            Unit
        }.recoverCatching { t ->
            throw RuntimeException(t.message ?: "Ошибка удаления чата")
        }


    override suspend fun clearHistory(chatId: String, forBoth: Boolean): Result<Unit> =
        runCatching {
            val myUid = authRepo.currentUid() ?: throw IllegalStateException("Нет авторизации")
            val batch = db.batch()

            val messages = db.collection("chats").document(chatId)
                .collection("messages").get().await()

            if (forBoth) {
                messages.documents.forEach { doc -> batch.delete(doc.reference) }

                batch.set(
                    db.collection("chats").document(chatId),
                    mapOf("lastMessageText" to null, "lastMessageAt" to null),
                    SetOptions.merge()
                )

                val chatSnap = db.collection("chats").document(chatId).get().await()
                val memberUids = (chatSnap.get("memberUids") as? List<*>)
                    ?.mapNotNull { it as? String } ?: emptyList()

                memberUids.forEach { uid ->
                    val indexRef = db.collection("userChats")
                        .document(uid).collection("items").document(chatId)
                    batch.update(indexRef, "lastMessageText", null)
                }
            } else {
                messages.documents.forEach { doc ->
                    batch.update(doc.reference, "deletedFor", FieldValue.arrayUnion(myUid))
                }

                val indexRef = db.collection("userChats")
                    .document(myUid).collection("items").document(chatId)
                batch.update(indexRef, "lastMessageText", null)
            }

            batch.commit().await()
            Unit
        }.recoverCatching { t ->
            throw RuntimeException(t.message ?: "Ошибка очистки истории", t)
        }

    override suspend fun deleteMessage(
        chatId: String,
        messageId: String,
        forBoth: Boolean
    ): Result<Unit> =
        runCatching {
            val myUid = authRepo.currentUid() ?: throw IllegalStateException("Не авторизован")
            val messageRef = db.collection("chats").document(chatId)
                .collection("messages").document(messageId)

            if (forBoth) {
                messageRef.delete().await()
            } else {
                messageRef.update("deletedFor", FieldValue.arrayUnion(myUid)).await()
            }

            val remaining = db.collection("chats").document(chatId)
                .collection("messages")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .await()

            val lastText = remaining.documents.firstOrNull()?.getString("text")

            val chatSnap = db.collection("chats").document(chatId).get().await()
            val memberUids = (chatSnap.get("memberUids") as? List<*>)
                ?.mapNotNull { it as? String } ?: emptyList()

            val batch = db.batch()
            memberUids.forEach { uid ->
                val indexRef = db.collection("userChats")
                    .document(uid).collection("items").document(chatId)
                batch.update(indexRef, "lastMessageText", lastText)
            }
            batch.commit().await()

            Unit
        }.recoverCatching { t ->
            throw RuntimeException(t.message ?: "Ошибка удаления сообщения", t)
        }
}