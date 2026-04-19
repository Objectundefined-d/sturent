package com.example.flat_rent_app.di

import android.content.Context
import com.example.flat_rent_app.core.FirebaseIdTokenProvider
import com.example.flat_rent_app.data.remote.api.PhotoApi
import com.example.flat_rent_app.data.repository.*
import com.example.flat_rent_app.domain.repository.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideAuthRepository(
        auth: FirebaseAuth,
        db: FirebaseFirestore
    ): AuthRepository = AuthRepositoryImpl(auth, db)

    @Provides
    @Singleton
    fun provideProfileRepository(
        db: FirebaseFirestore,
        authRepository: AuthRepository
    ): ProfileRepository = ProfileRepositoryImpl(db, authRepository)

    @Provides
    @Singleton
    fun provideSwipeRepository(
        db: FirebaseFirestore,
        authRepository: AuthRepository
    ): SwipeRepository = SwipeRepositoryImpl(db, authRepository)

    @Provides
    @Singleton
    fun provideChatRepository(
        db: FirebaseFirestore,
        authRepository: AuthRepository
    ): ChatRepository = ChatRepositoryImpl(db, authRepository)

    @Provides
    @Singleton
    fun provideStorageRepository(
        storage: FirebaseStorage,
        @ApplicationContext context: Context
    ): StorageRepository = StorageRepositoryImpl(storage, context)

    @Provides
    @Singleton
    fun providePhotoRepository(
        api: PhotoApi,
        tokenProvider: FirebaseIdTokenProvider,
        @ApplicationContext context: Context
    ): PhotoRepository = PhotoRepositoryImpl(api, tokenProvider, context)

    @Provides
    @Singleton
    fun provideBlackListRepository(
        db: FirebaseFirestore,
        authRepository: AuthRepository
    ) : BlackListRepository = BlackListRepositoryImpl(db, authRepository)
}