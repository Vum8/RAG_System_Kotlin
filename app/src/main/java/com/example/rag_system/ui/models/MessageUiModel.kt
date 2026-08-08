package com.example.rag_system.ui.models

/**
 * UI Model đại diện cho một tin nhắn trong khung trò chuyện của EduRAG (từ người dùng hoặc từ trợ lý AI).
 * [noAnswer]: true khi BE xác nhận AI không tìm thấy tài liệu liên quan (field noAnswer từ ChatMessageDto).
 */
data class MessageUiModel(
    val id: String = "",
    val content: String = "",
    val isFromUser: Boolean = true,
    val sendTime: String = "",
    val citations: List<SourceCitationUiModel> = emptyList(),
    val noAnswer: Boolean = false
)
