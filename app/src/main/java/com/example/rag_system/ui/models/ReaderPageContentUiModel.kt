package com.example.rag_system.ui.models

/**
 * UI Model biểu diễn nội dung chi tiết của một trang tài liệu học tập.
 * Được dùng bởi DocumentReaderBottomSheet để hiển thị bố cục đọc sách.
 */
data class ReaderPageContentUiModel(
    val chapterTitle: String,
    val sectionTitle: String,
    val bodyTextBefore: String,
    val highlightedSnippet: String,
    val bodyTextAfter: String
)
