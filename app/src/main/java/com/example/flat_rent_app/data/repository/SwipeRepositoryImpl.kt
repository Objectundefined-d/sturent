package com.example.flat_rent_app.data.repository

import com.example.flat_rent_app.domain.model.Match
import com.example.flat_rent_app.domain.repository.AuthRepository
import com.example.flat_rent_app.domain.repository.SwipeRepository
import com.example.flat_rent_app.util.LikeOutCome
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.emptyList

@Singleton
class SwipeRepositoryImpl @Inject constructor(
    private val db: FirebaseFirestore,
    private val authRepo: AuthRepository
) : SwipeRepository {

    private fun pairId(a: String, b: String): String = listOf(a, b).sorted().joinToString("_")

    override suspend fun likeUser(targetId: String): Result<LikeOutCome> =
        runCatching {
            val myUid = authRepo.currentUid() ?: throw IllegalStateException("Нет авторизации")
            if (targetId == myUid) throw IllegalArgumentException("Нельзя лайкнуть себя")

            val likeRef = db.collection("likes").document("${myUid}_$targetId")
            val reverseRef = db.collection("likes").document("${targetId}_$myUid")

            val chatId = pairId(myUid, targetId)
            val matchRef = db.collection("matches").document(chatId)
            val chatRef = db.collection("chats").document(chatId)

            val myChatIndex =
                db.collection("userChats").document(myUid).collection("items").document(chatId)
            val otherChatIndex =
                db.collection("userChats").document(targetId).collection("items").document(chatId)

            db.runTransaction { tx ->
                val now = System.currentTimeMillis()

                val reverse = tx.get(reverseRef)
                val matchSnap = tx.get(matchRef)

                tx.set(
                    likeRef,
                    mapOf("fromUid" to myUid, "toUid" to targetId, "createdAtMillis" to now)
                )

                if (!reverse.exists()) return@runTransaction LikeOutCome.LikedOnly

                if (!matchSnap.exists()) {
                    val uids = listOf(myUid, targetId).sorted()

                    tx.set(
                        matchRef, mapOf(
                            "uids" to uids,
                            "createdAtMillis" to now,
                            "chatId" to chatId,
                            "seenByUids" to emptyList<String>()
                        )
                    )
                    tx.set(
                        chatRef, mapOf(
                            "memberUids" to uids,
                            "createdAtMillis" to now,
                            "lastMessageAtMillis" to now
                        )
                    )
                    tx.set(
                        myChatIndex, mapOf(
                            "chatId" to chatId,
                            "otherUid" to targetId,
                            "lastMessageAtMillis" to now,
                            "unreadCount" to 0L
                        )
                    )
                    tx.set(
                        otherChatIndex, mapOf(
                            "chatId" to chatId,
                            "otherUid" to myUid,
                            "lastMessageAtMillis" to now,
                            "unreadCount" to 0L
                        )
                    )
                }

                LikeOutCome.Match(chatId)
            }.await()
        }.recoverCatching { t ->
            throw RuntimeException(t.message ?: "Ошибка лайка", t)
        }

    override suspend fun passUser(targetId: String): Result<Unit> =
        runCatching {
            val myUid = authRepo.currentUid() ?: throw IllegalStateException("Нет авторизации")
            if (targetId == myUid) throw IllegalArgumentException("Нельзя свайпнуть себя")

            db.collection("passes")
                .document("${myUid}_$targetId")
                .set(
                    mapOf(
                        "fromUid" to myUid,
                        "toUid" to targetId,
                        "createdAtMillis" to System.currentTimeMillis()
                    )
                )
                .await()

            Unit
        }.recoverCatching { t ->
            throw RuntimeException(t.message ?: "Ошибка pass", t)
        }

    override fun observeMatches(): Flow<List<Match>> = callbackFlow {
        val myUid = authRepo.currentUid()
        if (myUid == null) {
            trySend(emptyList()); close(); return@callbackFlow
        }

        val reg = db.collection("matches")
            .whereArrayContains("uids", myUid)
            .addSnapshotListener { snap, err ->
                if (err != null || snap == null) {
                    trySend(emptyList()); return@addSnapshotListener
                }

                val list = snap.documents.mapNotNull { d ->
                    val uids = (d.get("uids") as? List<*>)?.mapNotNull { it as? String }
                        ?: return@mapNotNull null
                    Match(
                        matchId = d.id,
                        uids = uids,
                        updatedAtMillis = d.getLong("updatedAtMillis")
                    )
                }.sortedByDescending { it.updatedAtMillis ?: 0L }

                trySend(list)
            }

        awaitClose { reg.remove() }
    }

    override suspend fun getUnseenMatch(): Match? =
        runCatching {
            val myUid = authRepo.currentUid() ?: return null

            val snap = db.collection("matches")
                .whereArrayContains("uids", myUid)
                .get()
                .await()

            snap.documents.firstOrNull { doc ->
                val seen = (doc.get("seenByUids") as? List<*>) ?: emptyList<String>()
                myUid !in seen
            }?.let { doc ->
                val uids =
                    (doc.get("uids") as? List<*>)?.mapNotNull { it as? String } ?: return null
                Match(
                    matchId = doc.id,
                    uids = uids,
                    updatedAtMillis = doc.getLong("updatedAtMillis")
                )
            }
        }.getOrNull()

    override suspend fun markMatchAsSeen(matchId: String): Result<Unit> =
        runCatching {
            val myUid = authRepo.currentUid() ?: throw IllegalStateException("Нет авторизации")

            db.collection("matches").document(matchId)
                .update("seenByUids", com.google.firebase.firestore.FieldValue.arrayUnion(myUid))
                .await()
        }

    override suspend fun addToFavorites(userId: String): Result<Unit> =
        runCatching {
            val myUid = authRepo.currentUid() ?: throw IllegalStateException("Нет авторизации")

            db.collection("favorites")
                .document(myUid)
                .collection("items")
                .document(userId)
                .set(
                    mapOf(
                        "uid" to userId,
                        "addedAtMillis" to System.currentTimeMillis()
                    )
                )
                .await()
            Unit
        }.recoverCatching { t ->
            throw RuntimeException(t.message ?: "Ошибка добавления в избранное", t)
        }

    override suspend fun addToBlackList(userId: String): Result<Unit> =
        runCatching {
            val myUid = authRepo.currentUid() ?: throw IllegalStateException("Не авторизован")

            db.collection("blackList")
                .document(myUid)
                .collection("items")
                .document(userId)
                .set(
                    mapOf(
                        "uid" to userId,
                        "addedAtMillis" to System.currentTimeMillis()
                    )
                )
                .await()
            Unit

        }.recoverCatching { t ->
            throw RuntimeException(t.message ?: "Ошибка добавления в чёрный список", t)
        }



    override fun observeFavorites(): Flow<List<String>> = callbackFlow {
        val myUid = authRepo.currentUid()
        if (myUid == null) {
            trySend(emptyList()); close(); return@callbackFlow
        }

        val reg = db.collection("favorites")
            .document(myUid)
            .collection("items")
            .addSnapshotListener { snap, err ->
                if (err != null || snap == null) {
                    trySend(emptyList()); return@addSnapshotListener
                }
                val ids = snap.documents.mapNotNull { it.getString("uid") }
                trySend(ids)
            }

        awaitClose { reg.remove() }
    }

    override suspend fun removeFromFavorites(userId: String): Result<Unit> =
        runCatching {
            val myUid = authRepo.currentUid() ?: throw IllegalStateException("Не авторизован")

            db.collection("favorites")
                .document(myUid)
                .collection("items")
                .document(userId)
                .delete()
                .await()

            Unit
        }.recoverCatching { t ->
            throw RuntimeException(t.message ?: "Ошибка удаления из избранного", t)
        }
}


