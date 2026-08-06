package com.example.rag_system.data.api.service

import com.example.rag_system.data.api.model.BaseApiResponseDto
import com.example.rag_system.data.api.model.UserProfileResponseDto
import com.example.rag_system.data.api.model.UpdateProfileRequestDto
import com.example.rag_system.data.api.model.ChangePasswordRequestDto
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Streaming

/**
 * Interface Retrofit gọi các đầu API profile người dùng.
 * Avatar dùng Bearer-authenticated Blob; không gắn avatarUrl trực tiếp vào ImageView
 * vì Node không mount /uploads thành static route.
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

    // ─────────────────────────────────────────────
    // Avatar  (POST / GET / DELETE /api/profile/avatar)
    // ─────────────────────────────────────────────

    /**
     * Upload hoặc thay thế ảnh đại diện.
     * Field name phải là "avatar" theo avatar-upload-middleware phía Backend.
     * Chỉ nhận JPEG/PNG/WebP một frame, tối đa 5 MiB.
     */
    @Multipart
    @POST("api/profile/avatar")
    suspend fun uploadAvatar(
        @Part avatar: MultipartBody.Part
    ): BaseApiResponseDto<Any>

    /**
     * Stream blob ảnh đại diện của chính mình.
     * Dùng @Streaming để tránh load toàn bộ ảnh vào RAM.
     * Tạo object URL từ Blob trong UI, revoke khi component unmount / ảnh thay đổi.
     */
    @GET("api/profile/avatar")
    @Streaming
    suspend fun streamMyAvatar(): ResponseBody

    /**
     * Xóa ảnh đại diện hiện tại (idempotent).
     */
    @DELETE("api/profile/avatar")
    suspend fun deleteAvatar(): BaseApiResponseDto<Unit>
}
