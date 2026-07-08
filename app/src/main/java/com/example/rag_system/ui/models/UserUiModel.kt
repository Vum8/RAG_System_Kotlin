package com.example.rag_system.ui.models

/**
 * UI Model đại diện cho thông tin người dùng (Sinh viên/Giảng viên) hiển thị trên giao diện.
 */
data class UserUiModel(
    val name: String = "",
    val email: String = "",
    val studentId: String = "",
    val phoneNumber: String? = null,
    val avatarUrl: String? = null,
    val isVerified: Boolean = false
)
