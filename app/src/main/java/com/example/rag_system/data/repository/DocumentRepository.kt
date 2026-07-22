package com.example.rag_system.data.repository

import com.example.rag_system.data.api.core.ApiClient
import com.example.rag_system.data.api.core.ApiResult
import com.example.rag_system.data.api.service.DocumentApiService
import com.example.rag_system.data.config.AppConfig
import com.example.rag_system.ui.models.DocumentFileFormat
import com.example.rag_system.ui.models.DocumentUiModel
import com.example.rag_system.ui.models.ReaderPageContentUiModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody

/**
 * Repository kết nối với Backend RAG_Be để tra cứu danh sách tài liệu học tập trong thư viện.
 * Hỗ trợ tự động chuyển đổi thông minh giữa Mock và API thực thông qua [AppConfig.USE_MOCK_DOCUMENT].
 */
class DocumentRepository : BaseRepository() {
    private val documentService = ApiClient.createService<DocumentApiService>()

    private val mockDocuments = listOf(
        DocumentUiModel("doc_1", "Kiến trúc máy tính", "Giáo trình", DocumentFileFormat.PDF, 120),
        DocumentUiModel("doc_2", "Hệ điều hành cơ bản", "Bài giảng", DocumentFileFormat.SLIDE, 45),
        DocumentUiModel("doc_3", "Lập trình Python nâng cao", "Tài liệu tham khảo", DocumentFileFormat.WORD, 80),
        DocumentUiModel("doc_4", "Cơ sở dữ liệu SQL", "Giáo trình", DocumentFileFormat.PDF, 210),
        DocumentUiModel("doc_5", "Hướng dẫn khóa luận tốt nghiệp", "Quy định", DocumentFileFormat.WORD, 15)
    )

    /**
     * Lấy danh sách tài liệu đang hiển thị (VISIBLE) từ Backend hoặc Mock.
     */
    suspend fun getLibraryDocuments(): ApiResult<List<DocumentUiModel>> {
        if (AppConfig.USE_MOCK_DOCUMENT) {
            return ApiResult.Success(mockDocuments)
        }
        return safeApiCall {
            val response = documentService.listDocuments(offset = 0, limit = 50, visibilityStatus = "VISIBLE")
            val docDtos = response.data?.documents ?: emptyList()
            docDtos.map { dto ->
                val format = when {
                    dto.originalFilename.endsWith(".pdf", true) || dto.fileType.contains("pdf", true) -> DocumentFileFormat.PDF
                    dto.originalFilename.endsWith(".doc", true) || dto.originalFilename.endsWith(".docx", true) -> DocumentFileFormat.WORD
                    dto.originalFilename.endsWith(".ppt", true) || dto.originalFilename.endsWith(".pptx", true) -> DocumentFileFormat.SLIDE
                    else -> DocumentFileFormat.OTHER
                }
                DocumentUiModel(
                    id = dto.id.toString(),
                    title = dto.title.ifEmpty { dto.originalFilename },
                    category = "Tài liệu học tập",
                    fileFormat = format,
                    pageOrSlideCount = 10
                )
            }
        }
    }

    /**
     * Trả về nội dung trang tài liệu phục vụ màn hình đọc (Reader) từ Mock hoặc API.
     */
    fun getDocumentPageContent(page: Int): ReaderPageContentUiModel {
        if (AppConfig.USE_MOCK_DOCUMENT) {
            return when (page % 3) {
                1 -> ReaderPageContentUiModel(
                    chapterTitle = "Chương I: Tổng quan hệ thống",
                    sectionTitle = "1.1 Giới thiệu kiến trúc máy tính",
                    bodyTextBefore = "Kiến trúc máy tính đề cập đến các thuộc tính hệ thống được lập trình viên nhìn thấy, hoặc nói cách khác, các thuộc tính có tác động trực tiếp đến việc thực thi logic của một chương trình.",
                    highlightedSnippet = "Kiến trúc máy tính bao gồm tập lệnh, số lượng bit dùng biểu diễn dữ liệu, cơ chế vào/ra và kỹ thuật định địa chỉ bộ nhớ.",
                    bodyTextAfter = "Ngược lại, tổ chức máy tính liên quan đến các đơn vị vận hành phần cứng và sự kết nối giữa chúng để thực hiện các đặc tả kiến trúc đã đề ra."
                )
                2 -> ReaderPageContentUiModel(
                    chapterTitle = "Chương I: Tổng quan hệ thống",
                    sectionTitle = "1.2 Mô hình Von Neumann",
                    bodyTextBefore = "Mô hình Von Neumann là nền tảng của hầu hết các thiết kế máy tính hiện đại. Mô hình này mô tả cấu trúc phần cứng máy tính gồm đơn vị xử lý trung tâm (CPU), bộ nhớ và thiết bị ngoại vi.",
                    highlightedSnippet = "Đặc trưng lớn nhất của kiến trúc Von Neumann là chương trình và dữ liệu được lưu trữ chung trong cùng một không gian bộ nhớ vật lý.",
                    bodyTextAfter = "Mô hình này giúp máy tính hoạt động linh hoạt, cho phép nạp các chương trình khác nhau vào bộ nhớ để thực thi mà không cần cấu hình lại phần cứng."
                )
                else -> ReaderPageContentUiModel(
                    chapterTitle = "Chương II: Bộ vi xử lý",
                    sectionTitle = "2.1 Chu kỳ lệnh CPU",
                    bodyTextBefore = "CPU thực hiện các lệnh thông qua một chu kỳ lặp đi lặp lại gồm ba bước cơ bản: Nhận lệnh (Fetch), Giải mã lệnh (Decode) và Thực thi lệnh (Execute).",
                    highlightedSnippet = "Thanh ghi đếm chương trình (PC - Program Counter) luôn lưu trữ địa chỉ của lệnh tiếp theo sẽ được CPU nạp vào thực hiện.",
                    bodyTextAfter = "Tốc độ thực hiện chu kỳ lệnh này được điều phối bởi xung nhịp đồng hồ hệ thống (System Clock), đo bằng đơn vị Hertz (Hz)."
                )
            }
        }
        return ReaderPageContentUiModel(
            chapterTitle = "Giáo trình trích xuất từ EduRAG",
            sectionTitle = "Trang số $page",
            bodyTextBefore = "Bạn đang xem nội dung trang số $page của tài liệu được lưu trữ trên hệ thống EduRAG.",
            highlightedSnippet = "Hệ thống RAG hỗ trợ truy cập nhanh chóng và chính xác các đoạn kiến thức quan trọng.",
            bodyTextAfter = "Sử dụng tính năng hỏi đáp AI bên dưới để tra cứu chi tiết hơn về phần nội dung này."
        )
    }

    /**
     * Upload tài liệu lên hệ thống Backend (giới hạn 20MB, định dạng PDF/DOCX).
     */
    suspend fun uploadDocument(file: java.io.File): ApiResult<DocumentUiModel> {
        if (AppConfig.USE_MOCK_DOCUMENT) {
            kotlinx.coroutines.delay(1500)
            return ApiResult.Success(
                DocumentUiModel(
                    id = "doc_mock_upload",
                    title = file.name,
                    category = "Tài liệu mới",
                    fileFormat = if (file.name.endsWith(".pdf", true)) DocumentFileFormat.PDF else DocumentFileFormat.WORD,
                    pageOrSlideCount = 1
                )
            )
        }
        return safeApiCall {
            val mediaType = if (file.name.endsWith(".pdf", true)) {
                "application/pdf".toMediaTypeOrNull()
            } else if (file.name.endsWith(".docx", true)) {
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document".toMediaTypeOrNull()
            } else {
                "application/octet-stream".toMediaTypeOrNull()
            }
            val requestBody = file.asRequestBody(mediaType)
            val multipartBody = okhttp3.MultipartBody.Part.createFormData("file", file.name, requestBody)
            
            val response = documentService.uploadDocument(multipartBody)
            val dto = response.data ?: throw IllegalStateException("Không nhận được dữ liệu tài liệu sau khi upload.")
            
            val format = when {
                dto.originalFilename.endsWith(".pdf", true) || dto.fileType.contains("pdf", true) -> DocumentFileFormat.PDF
                dto.originalFilename.endsWith(".doc", true) || dto.originalFilename.endsWith(".docx", true) -> DocumentFileFormat.WORD
                dto.originalFilename.endsWith(".ppt", true) || dto.originalFilename.endsWith(".pptx", true) -> DocumentFileFormat.SLIDE
                else -> DocumentFileFormat.OTHER
            }
            
            DocumentUiModel(
                id = dto.id.toString(),
                title = dto.title.ifEmpty { dto.originalFilename },
                category = "Tài liệu học tập",
                fileFormat = format,
                pageOrSlideCount = 1 // Giá trị mặc định
            )
        }
    }
}
