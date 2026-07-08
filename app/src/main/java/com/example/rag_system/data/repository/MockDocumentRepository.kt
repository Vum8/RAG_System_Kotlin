package com.example.rag_system.data.repository

import com.example.rag_system.data.api.core.ApiResult
import com.example.rag_system.ui.models.DocumentFileFormat
import com.example.rag_system.ui.models.DocumentUiModel
import kotlinx.coroutines.delay

/**
 * Mock Repository giả lập quản lý danh sách tài liệu học tập trong hệ thống EduRAG.
 * Kế thừa [BaseRepository] và tự động trì hoãn 1.5 giây để mô phỏng độ trễ mạng thực tế.
 */
class MockDocumentRepository : BaseRepository() {

    suspend fun getLibraryDocuments(): ApiResult<List<DocumentUiModel>> = safeApiCall {
        delay(1500L)
        listOf(
            DocumentUiModel(
                id = "doc_01",
                title = "Kiến trúc máy tính",
                category = "Chương 1 - Tổng quan",
                fileFormat = DocumentFileFormat.SLIDE,
                pageOrSlideCount = 12
            ),
            DocumentUiModel(
                id = "doc_02",
                title = "Hệ điều hành cơ bản",
                category = "Chương 2 - Quản lý tiến trình",
                fileFormat = DocumentFileFormat.SLIDE,
                pageOrSlideCount = 24
            ),
            DocumentUiModel(
                id = "doc_03",
                title = "Lập trình Python nâng cao",
                category = "Tài liệu môn học",
                fileFormat = DocumentFileFormat.PDF,
                pageOrSlideCount = 45
            ),
            DocumentUiModel(
                id = "doc_04",
                title = "Cơ sở dữ liệu",
                category = "Chương 3 - Mô hình quan hệ",
                fileFormat = DocumentFileFormat.SLIDE,
                pageOrSlideCount = 18
            ),
            DocumentUiModel(
                id = "doc_05",
                title = "Hướng dẫn khóa luận",
                category = "Đồ án tốt nghiệp",
                fileFormat = DocumentFileFormat.WORD,
                pageOrSlideCount = 10
            ),
            DocumentUiModel(
                id = "doc_06",
                title = "Mạng máy tính",
                category = "Chương 4 - Tầng giao vận",
                fileFormat = DocumentFileFormat.SLIDE,
                pageOrSlideCount = 32
            )
        )
    }
}
