package com.example.rag_system.data.api.core

/**
 * Lớp biểu diễn lỗi từ API hoặc mạng theo chuẩn MVVM của dự án.
 */
data class ApiError(
    val code: Int? = null,
    val message: String = "Đã có lỗi xảy ra, vui lòng thử lại sau.",
    val throwable: Throwable? = null
) {
    companion object {
        const val NETWORK_ERROR_CODE = -1
        const val TIMEOUT_ERROR_CODE = -2
        const val UNKNOWN_ERROR_CODE = -3
        const val UNAUTHORIZED_CODE = 401
    }

    val isUnauthorized: Boolean
        get() = code == UNAUTHORIZED_CODE

    val isNetworkError: Boolean
        get() = code == NETWORK_ERROR_CODE || code == TIMEOUT_ERROR_CODE
}
