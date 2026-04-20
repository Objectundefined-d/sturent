package com.example.flat_rent_app.core

interface FirebaseIdTokenProvider {
    suspend fun getIdToken(): String?
}
