package com.example.rag_system.ui.models

/**
 * UI Model đại diện cho nguồn trích dẫn từ tài liệu học tập trả lời cho câu hỏi trong EduRAG.
 * - [sourceLocator]: JSON snapshot từ Python pipeline (page, section, bbox...) – nullable vì legacy data chưa có.
 * - [retrievalScore] / [rerankScore]: điểm liên quan từ RAG engine; dùng để sort/display confidence.
 */
data class SourceCitationUiModel(
    val citationOrder: Int = 0,
    val documentId: String = "",
    val sourceDocumentName: String = "",
    val pageNumber: Int? = null,
    val chapterSection: String? = null,
    val rawExtractedText: String = "",
    val sourceLocator: Map<String, Any?>? = null,
    val retrievalScore: Double? = null,
    val rerankScore: Double? = null
)
