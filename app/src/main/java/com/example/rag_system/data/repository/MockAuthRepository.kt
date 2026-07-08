package com.example.rag_system.data.repository

import com.example.rag_system.data.api.core.ApiResult
import com.example.rag_system.ui.models.UserUiModel
import kotlinx.coroutines.delay

/**
 * Mock Repository giả lập các yêu cầu xác thực và thông tin người dùng từ Backend EduRAG.
 * Kế thừa [BaseRepository] để bọc kết quả an toàn bằng [safeApiCall] và giả lập độ trễ 1.5s.
 */
class MockAuthRepository : BaseRepository() {

    suspend fun getProfile(): ApiResult<UserUiModel> = safeApiCall {
        delay(1500L)
        UserUiModel(
            name = "Nguyễn Văn A",
            email = "vanna.nguyen@student.edu.vn",
            studentId = "B20DCCN123",
            phoneNumber = "0987654321",
            avatarUrl = "https://example.com/avatars/student_a.png",
            isVerified = true
        )
    }
}
