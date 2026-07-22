package com.example.rag_system.data.api.service

import com.example.rag_system.data.api.model.AuthResponseDto
import com.example.rag_system.data.api.model.BaseApiResponseDto
import com.example.rag_system.data.api.model.LoginRequestDto
import com.example.rag_system.data.api.model.RegisterRequestDto
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Interface Retrofit gọi các đầu API xác thực của Backend EduRAG.
 */
interface AuthApiService {
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequestDto): BaseApiResponseDto<AuthResponseDto>

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequestDto): BaseApiResponseDto<AuthResponseDto>
}
