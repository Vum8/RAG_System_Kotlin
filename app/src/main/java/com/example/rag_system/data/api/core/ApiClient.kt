package com.example.rag_system.data.api.core

import com.example.rag_system.data.session.TokenManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Cung cấp Client mạng (Retrofit & OkHttp) kết nối với Backend EduRAG (RAG_Be).
 * Base URL mặc định là 10.0.2.2:5001 (dành cho Android Emulator kết nối với localhost của máy host).
 */
object ApiClient {
    // Địa chỉ IP 10.0.2.2 là địa chỉ localhost của máy tính host từ góc nhìn của Android Emulator
    private const val BASE_URL = "http://10.0.2.2:5001/"

    private val authInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()
        val token = TokenManager.getToken()

        val requestBuilder = originalRequest.newBuilder()
        if (!token.isNullOrEmpty()) {
            requestBuilder.header("Authorization", "Bearer $token")
        }
        requestBuilder.header("Accept", "application/json")

        chain.proceed(requestBuilder.build())
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /**
     * Tạo Service API Interface từ Retrofit.
     */
    inline fun <reified T> createService(): T {
        return retrofit.create(T::class.java)
    }
}
