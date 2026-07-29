package com.example.rag_system.data.repository

import com.example.rag_system.data.api.core.ApiClient
import com.example.rag_system.data.api.core.ApiResult
import com.example.rag_system.data.api.model.LoginRequestDto
import com.example.rag_system.data.api.model.RegisterRequestDto
import com.example.rag_system.data.api.model.UpdateProfileRequestDto
import com.example.rag_system.data.api.model.ChangePasswordRequestDto
import com.example.rag_system.data.api.service.AuthApiService
import com.example.rag_system.data.api.service.UserApiService
import com.example.rag_system.data.config.AppConfig
import com.example.rag_system.data.session.TokenManager
import com.example.rag_system.ui.models.UserUiModel

/**
 * Repository kết nối với Backend EduRAG (RAG_Be) cho nghiệp vụ Xác thực và Hồ sơ Sinh viên.
 * Hỗ trợ tự động chuyển đổi thông minh giữa Mock và API thực thông qua [AppConfig.USE_MOCK_AUTH].
 */
class AuthRepository : BaseRepository() {
    private val authService = ApiClient.createService<AuthApiService>()
    private val userService = ApiClient.createService<UserApiService>()

    /**
     * Đăng nhập tài khoản Sinh viên. Tự động lưu JWT Token.
     */
    suspend fun login(email: String, password: String): ApiResult<UserUiModel> {


        return safeApiCall(emitUnauthorizedEvent = false) {
            val response = authService.login(LoginRequestDto(email, password))
            val loginData = response.data
            
            // Chặn ADMIN hoặc các tài khoản không phải STUDENT
            if (loginData?.requireOtp == true || (loginData?.user != null && loginData.user.role != "STUDENT")) {
                throw IllegalArgumentException("Tài khoản này không có quyền truy cập ứng dụng dành cho Sinh viên.")
            }
            
            val token = loginData?.token
            if (!token.isNullOrEmpty()) {
                TokenManager.saveToken(token)
            }
            val userDto = loginData?.user
            UserUiModel(
                name = userDto?.fullName ?: "Sinh viên EduRAG",
                email = userDto?.email ?: email,
                studentId = "",
                phoneNumber = "",
                avatarUrl = null,
                isVerified = true
            )
        }
    }

    /**
     * Đăng ký tài khoản Sinh viên mới.
     */
    suspend fun register(request: RegisterRequestDto): ApiResult<UserUiModel> {

        return safeApiCall(emitUnauthorizedEvent = false) {
            val response = authService.register(request)
            val token = response.data?.token
            if (!token.isNullOrEmpty()) {
                TokenManager.saveToken(token)
            }
            UserUiModel(
                name = request.fullName,
                email = request.email,
                studentId = request.studentCode,
                phoneNumber = request.phone ?: "",
                avatarUrl = null,
                isVerified = true
            )
        }
    }

    /**
     * Lấy thông tin hồ sơ chi tiết.
     */
    suspend fun getProfile(): ApiResult<UserUiModel> {

        return safeApiCall {
            val response = userService.getMyProfile()
            val profileDto = response.data
            UserUiModel(
                name = profileDto?.fullName ?: "Sinh viên EduRAG",
                email = profileDto?.email ?: "",
                studentId = profileDto?.studentCode ?: "",
                phoneNumber = profileDto?.phone ?: "",
                avatarUrl = null,
                isVerified = profileDto?.status == "ACTIVE"
            )
        }
    }

    /**
     * Cập nhật thông tin hồ sơ cá nhân.
     */
    suspend fun updateProfile(fullName: String, phone: String?): ApiResult<UserUiModel> {

        return safeApiCall {
            val response = userService.updateProfile(UpdateProfileRequestDto(fullName, phone))
            val profileDto = response.data
            UserUiModel(
                name = profileDto?.fullName ?: "Sinh viên EduRAG",
                email = profileDto?.email ?: "",
                studentId = profileDto?.studentCode ?: "",
                phoneNumber = profileDto?.phone ?: "",
                avatarUrl = null,
                isVerified = profileDto?.status == "ACTIVE"
            )
        }
    }

    /**
     * Thay đổi mật khẩu.
     */
    suspend fun changePassword(currentPass: String, newPass: String): ApiResult<Unit> {

        return safeApiCall {
            userService.changePassword(ChangePasswordRequestDto(currentPass, newPass))
            Unit
        }
    }

    /**
     * Yêu cầu đặt lại mật khẩu
     */
    suspend fun forgotPassword(email: String): ApiResult<Unit> {

        return safeApiCall {
            authService.forgotPassword(com.example.rag_system.data.api.model.ForgotPasswordRequestDto(email))
            Unit
        }
    }

    /**
     * Đặt lại mật khẩu với token
     */
    suspend fun resetPassword(token: String, newPass: String): ApiResult<Unit> {

        return safeApiCall {
            authService.resetPassword(com.example.rag_system.data.api.model.ResetPasswordRequestDto(token, newPass))
            Unit
        }
    }

    /**
     * Đăng xuất, xóa token khỏi hệ thống.
     */
    fun logout() {
        TokenManager.clearToken()
    }
}
