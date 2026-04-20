package com.example.flat_rent_app.data.repository

import android.util.Log
import com.example.flat_rent_app.domain.model.Gender
import com.example.flat_rent_app.domain.model.ProfilePhoto
import com.example.flat_rent_app.domain.model.UserProfile
import com.example.flat_rent_app.domain.repository.AuthRepository
import com.example.flat_rent_app.domain.repository.ProfileRepository
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProfileRepositoryImpl @Inject constructor(
    private val db: FirebaseFirestore,
    private val authRepo: AuthRepository
) : ProfileRepository {

    companion object {
        private const val PROFILE_PHOTO_SLOT_COUNT = 3
        private const val MAIN_PHOTO_INDEX_MAX = 2
    }

    override fun observerProfile(uid: String): Flow<UserProfile?> = callbackFlow {
        val reg = db.collection("users").document(uid)
            .addSnapshotListener { snap, err ->
                if (err != null || snap == null || !snap.exists()) {
                    trySend(null)
                    return@addSnapshotListener
                }
                trySend(snap.toUserProfile())
            }
        awaitClose { reg.remove() }
    }

    override suspend fun upsertMyProfile(profile: UserProfile): Result<Unit> =
        runCatching {
            val uid = authRepo.currentUid() ?: throw IllegalStateException("Нет авторизации")
            val now = System.currentTimeMillis()

            val slots = profile.photoSlots.map { slot ->
                slot?.let {
                    mapOf(
                        "fullUrl" to it.fullUrl,
                        "thumbUrl" to it.thumbUrl,
                        "updatedAtMillis" to it.updatedAt
                    )
                }
            }

            db.collection("users").document(uid)
                .set(
                    mapOf(
                        "name" to profile.name,
                        "age" to profile.age,
                        "gender" to profile.gender?.name,
                        "city" to profile.city,
                        "eduPlace" to profile.eduPlace,
                        "description" to profile.description,

                        "preferences" to profile.preferences,
                        "mainPhotoIndex" to profile.mainPhotoIndex,
                        "photoSlots" to slots,

                        "updatedAtMillis" to now,
                        "createdAtMillis" to (profile.createdAtMillis ?: now)
                    ),
                    SetOptions.merge()
                )
                .await()

            Unit
        }.recoverCatching { t ->
            throw RuntimeException(t.message ?: "Ошибка сохранения профиля", t)
        }

    override suspend fun getFeedProfiles(limit: Int): Result<List<UserProfile>> =
        runCatching {
            val myUid = authRepo.currentUid() ?: throw IllegalStateException("Нет авторизации")

            Log.d("FIRESTORE", "getFeedProfiles uid=$myUid")

            val snap = db.collection("users")
                .orderBy("updatedAtMillis", Query.Direction.DESCENDING)
                .limit(limit.toLong())
                .get()
                .await()

            Log.d("FIRESTORE", "Всего документов: ${snap.documents.size}")

            snap.documents.mapNotNull { d ->
                if (d.id == myUid) return@mapNotNull null
                try {
                    d.toUserProfile()
                } catch (e: Exception) {
                    Log.e("FIRESTORE", "Ошибка маппинга документа ${d.id}: ${e.message}")
                    null
                }
            }
        }.recoverCatching { t ->
            throw RuntimeException(t.message ?: "Ошибка загрузки кандидатов", t)
        }

    private fun DocumentSnapshot.toUserProfile(): UserProfile {
        val slots = (get("photoSlots") as? List<*>)?.map { item ->
            val m = item as? Map<*, *> ?: return@map null
            ProfilePhoto(
                fullUrl = m["fullUrl"] as? String,
                thumbUrl = m["thumbUrl"] as? String,
                updatedAt = (m["updatedAtMillis"] as? Number)?.toLong()
            )
        } ?: listOf(null, null, null)
        val genderRaw = getString("gender")
        val gender = try {
            genderRaw?.let { Gender.valueOf(it) }
        } catch (e: IllegalArgumentException) {
            null
        }

        return UserProfile(
            uid = id,
            name = getString("name").orEmpty(),
            age = getLong("age")?.toInt(),
            gender = gender,
            city = getString("city").orEmpty(),
            eduPlace = getString("eduPlace").orEmpty(),
            description = getString("description").orEmpty(),
            preferences = (get("preferences") as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
            mainPhotoIndex = ((getLong("mainPhotoIndex") ?: 0L).toInt()).coerceIn(
                0,
                MAIN_PHOTO_INDEX_MAX
            ),
            photoSlots = slots.take(PROFILE_PHOTO_SLOT_COUNT).let {
                it + List(maxOf(0, PROFILE_PHOTO_SLOT_COUNT - it.size)) { null }
            },
            createdAtMillis = getLong("createdAtMillis"),
            updatedAtMillis = getLong("updatedAtMillis")
        )
    }

    override suspend fun saveFcmToken(token: String) {
        val uid = authRepo.currentUid() ?: throw IllegalStateException("Не авторизован")
        db.collection("users")
            .document(uid)
            .update("fcmToken", token)
            .await()
    }
}
