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
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.InputStream
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream

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
    suspend fun login(email: String, password: String, rememberMe: Boolean = true): ApiResult<UserUiModel> {


        return safeApiCall(emitUnauthorizedEvent = false) {
            val response = authService.login(LoginRequestDto(email, password))
            val loginData = response.data
            
            // Chặn ADMIN hoặc các tài khoản không phải STUDENT
            if (loginData?.requireOtp == true || (loginData?.user != null && loginData.user.role != "STUDENT")) {
                throw IllegalArgumentException("Tài khoản này không có quyền truy cập ứng dụng dành cho Sinh viên.")
            }
            
            val token = loginData?.token
            if (!token.isNullOrEmpty()) {
                TokenManager.saveToken(token, rememberMe)
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
                avatarUrl = profileDto?.avatarUrl,
                hasAvatar = profileDto?.avatarAvailable == true,
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
     * Upload hoặc thay thế ảnh đại diện.
     * @param inputStream dữ liệu ảnh từ content URI
     * @param mimeType MIME type của ảnh (image/jpeg, image/png, image/webp)
     * @param filename tên file gốc để gửi lên server
     */
    suspend fun uploadAvatar(inputStream: InputStream, mimeType: String, filename: String): ApiResult<Unit> {
        return safeApiCall {
            val originalBytes = inputStream.readBytes()
            var uploadBytes = originalBytes
            var finalMimeType = mimeType

            // Tự động nén ảnh nếu dung lượng lớn hơn 500KB
            if (originalBytes.size > 500 * 1024) {
                try {
                    val bitmap = BitmapFactory.decodeByteArray(originalBytes, 0, originalBytes.size)
                    if (bitmap != null) {
                        val outputStream = ByteArrayOutputStream()
                        // Tính toán scale giảm kích thước (max dimension = 800px)
                        var scaledBitmap = bitmap
                        val maxDim = 800
                        if (bitmap.width > maxDim || bitmap.height > maxDim) {
                            val ratio = bitmap.width.toFloat() / bitmap.height.toFloat()
                            val width = if (ratio > 1) maxDim else (maxDim * ratio).toInt()
                            val height = if (ratio > 1) (maxDim / ratio).toInt() else maxDim
                            scaledBitmap = Bitmap.createScaledBitmap(bitmap, width, height, true)
                        }
                        
                        // Ép nén chất lượng xuống 70% chuẩn JPEG
                        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
                        val compressed = outputStream.toByteArray()
                        
                        if (compressed.size < originalBytes.size) {
                            uploadBytes = compressed
                            finalMimeType = "image/jpeg"
                        }
                    }
                } catch (e: Exception) {
                    // Bỏ qua lỗi nén, sử dụng file gốc nếu nén thất bại
                }
            }

            val requestBody = uploadBytes.toRequestBody(finalMimeType.toMediaTypeOrNull())
            val part = MultipartBody.Part.createFormData("avatar", filename, requestBody)
            userService.uploadAvatar(part)
            Unit
        }
    }

    /**
     * Xóa ảnh đại diện.
     */
    suspend fun deleteAvatar(): ApiResult<Unit> {
        return safeApiCall {
            userService.deleteAvatar()
            Unit
        }
    }

    /**
     * Tải blob ảnh đại diện của chính mình từ GET /api/profile/avatar.
     * Trả về [ByteArray] để UI tạo Bitmap trực tiếp (không cần file tạm).
     * Chỉ gọi khi [UserProfileResponseDto.avatarAvailable] == true.
     */
    suspend fun loadMyAvatarBytes(context: android.content.Context): ApiResult<ByteArray> {
        return safeApiCall {
            val bytes = userService.streamMyAvatar().bytes()
            // Ghi ra file để persist qua các lần mở app
            val file = java.io.File(context.filesDir, "avatar.jpg")
            file.writeBytes(bytes)
            com.example.rag_system.data.session.TokenManager.saveLocalAvatarUri(android.net.Uri.fromFile(file).toString())
            bytes
        }
    }

    /**
     * Đăng xuất, xóa token khỏi hệ thống.
     */
    fun logout() {
        com.example.rag_system.data.session.TokenManager.clearToken()
    }
}
