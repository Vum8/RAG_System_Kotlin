package com.example.rag_system.ui.models

/**
 * UI Model đại diện cho một phiên hỏi đáp/trò chuyện hiển thị trên danh sách lịch sử hội thoại.
 */
data class ChatSessionUiModel(
    val id: String = "",
    val title: String = "",
    val lastMessagePreview: String = "",
    val displayTime: String = "",
    val subjectLabel: String? = null,
    val citations: List<SourceCitationUiModel> = emptyList()
)
