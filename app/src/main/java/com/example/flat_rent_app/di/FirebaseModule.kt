package com.example.flat_rent_app.di

import com.example.flat_rent_app.core.FirebaseIdTokenProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.tasks.await
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage = FirebaseStorage.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseIdTokenProvider(auth: FirebaseAuth): FirebaseIdTokenProvider =
        object : FirebaseIdTokenProvider {
            override suspend fun getIdToken(): String? {
                val user = auth.currentUser ?: return null
                return try {
                    user.getIdToken(false).await().token
                } catch (_: Throwable) {
                    null
                }
            }
        }
}
