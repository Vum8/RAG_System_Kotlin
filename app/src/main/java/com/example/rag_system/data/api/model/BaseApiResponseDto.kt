package com.example.rag_system.data.api.model

import com.google.gson.annotations.SerializedName

/**
 * Cấu trúc phản hồi chung từ Backend EduRAG (RAG_Be).
 * Khi thành công: success = true, message = "...", data = <T>
 * Khi lỗi: success = false, message = "...", errorCode = "..."
 */
data class BaseApiResponseDto<T>(
    @SerializedName("success") val success: Boolean = false,
    @SerializedName("message") val message: String? = null,
    @SerializedName("errorCode") val errorCode: String? = null,
    @SerializedName("data") val data: T? = null
)
