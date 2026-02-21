package com.example.flat_rent_app.data.remote.okhttp

import com.example.flat_rent_app.core.FirebaseIdTokenProvider
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthTokenInterceptor @Inject constructor(
    private val tokenProvider: FirebaseIdTokenProvider
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        if (originalRequest.url.host.contains("onrender.com")) {
            val token = runBlocking { tokenProvider.getIdToken() }

            if (token != null) {
                val newRequest = originalRequest.newBuilder()
                    .header("Authorization", "Bearer $token")
                    .build()
                return chain.proceed(newRequest)
            }
        }

        return chain.proceed(originalRequest)
    }
}