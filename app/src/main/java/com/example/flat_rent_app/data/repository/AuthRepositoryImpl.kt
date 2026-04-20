package com.example.flat_rent_app.data.repository

import com.example.flat_rent_app.domain.model.AuthUser
import com.example.flat_rent_app.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
): AuthRepository {
    override val currentUser: Flow<AuthUser?> =  callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { fa ->
            val u = fa.currentUser
            trySend(u?.let { AuthUser(uid = it.uid, email = it.email) })
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    override fun currentUid(): String? {
        return auth.currentUser?.uid
    }

    override fun currentUserEmail(): String? {
        return auth.currentUser?.email
    }

    override suspend fun sendPasswordReset(email: String): Result<Unit> = runCatching {
        auth.sendPasswordResetEmail(email).await()
    }

    override suspend fun sendEmailVerification(): Result<Unit> = runCatching {
        auth.currentUser?.sendEmailVerification()?.await()
            ?: throw IllegalStateException("Не авторизован")
    }

    override suspend fun updateEmail(newEmail: String, password: String): Result<Unit> = runCatching {
        val user = auth.currentUser ?: throw IllegalStateException("Не авторизован")
        val email = user.email ?: throw IllegalStateException("Нет email")

        val credential = com.google.firebase.auth.EmailAuthProvider
            .getCredential(email, password)
        user.reauthenticate(credential).await()

        user.verifyBeforeUpdateEmail(newEmail.trim()).await()
    }

    override suspend fun signIn(
        email: String,
        password: String
    ): Result<AuthUser> =  runCatching {
        val res = auth.signInWithEmailAndPassword(email.trim(), password).await()
        val u = res.user ?: throw IllegalStateException("Нет пользователя")
        AuthUser(u.uid, u.email)
    }.recoverCatching { t ->
        throw RuntimeException(t.message ?: "Ошибка входа", t)
    }

    override suspend fun signUp(
        email: String,
        password: String
    ): Result<AuthUser> = runCatching {
        val res = auth.createUserWithEmailAndPassword(email.trim(), password).await()
        val u = res.user ?: throw IllegalStateException("Нет пользователя")
        AuthUser(u.uid, u.email)
    }.recoverCatching { t ->
        throw RuntimeException(t.message ?: "Ошибка регистрации", t)
    }

    override suspend fun signOut() {
        val uid = auth.currentUser?.uid ?: throw IllegalStateException("Не авторизован")

        runCatching {
            db.collection("users").document(uid)
                .update("fcmToken", null)
                .await()
        }
        auth.signOut()
    }

    override suspend fun deleteAccount(): Result<Unit> = runCatching {
        auth.currentUser?.delete()?.await()
            ?: throw IllegalStateException("Не авторизован")
    }
}
