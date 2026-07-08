package com.example.rag_system.ui.models

/**
 * Định dạng file tài liệu học tập trong hệ thống EduRAG.
 */
enum class DocumentFileFormat {
    PDF,
    WORD,
    SLIDE,
    OTHER
}

/**
 * UI Model đại diện cho tài liệu học tập (chương sách, đồ án, bài giảng) hiển thị trên giao diện.
 */
data class DocumentUiModel(
    val id: String = "",
    val title: String = "",
    val category: String = "",
    val fileFormat: DocumentFileFormat = DocumentFileFormat.PDF,
    val pageOrSlideCount: Int = 0
)
