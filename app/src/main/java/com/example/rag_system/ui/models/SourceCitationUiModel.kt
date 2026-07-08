package com.example.rag_system.ui.models

/**
 * UI Model đại diện cho nguồn trích dẫn từ tài liệu học tập trả lời cho câu hỏi trong EduRAG.
 */
data class SourceCitationUiModel(
    val sourceDocumentName: String = "",
    val pageNumber: Int? = null,
    val chapterSection: String? = null,
    val rawExtractedText: String = ""
)
