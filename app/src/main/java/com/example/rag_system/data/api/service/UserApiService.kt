package com.example.rag_system.data.api.service

import com.example.rag_system.data.api.model.BaseApiResponseDto
import com.example.rag_system.data.api.model.UserProfileResponseDto
import com.example.rag_system.data.api.model.UpdateProfileRequestDto
import com.example.rag_system.data.api.model.ChangePasswordRequestDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT

/**
 * Interface Retrofit gọi các đầu API profile người dùng.
 */
interface UserApiService {
    @GET("api/profile")
    suspend fun getMyProfile(): BaseApiResponseDto<UserProfileResponseDto>

    @PUT("api/profile")
    suspend fun updateProfile(
        @Body request: UpdateProfileRequestDto
    ): BaseApiResponseDto<UserProfileResponseDto>

    @PUT("api/profile/password")
    suspend fun changePassword(
        @Body request: ChangePasswordRequestDto
    ): BaseApiResponseDto<Unit>
}
