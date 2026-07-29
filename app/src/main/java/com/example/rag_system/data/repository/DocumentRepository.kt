package com.example.rag_system.data.repository

import com.example.rag_system.data.api.core.ApiClient
import com.example.rag_system.data.api.core.ApiResult
import com.example.rag_system.data.api.service.DocumentApiService
import com.example.rag_system.data.config.AppConfig
import com.example.rag_system.ui.models.DocumentFileFormat
import com.example.rag_system.ui.models.DocumentUiModel
import com.example.rag_system.ui.models.ReaderPageContentUiModel
import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody

/**
 * Repository kết nối với Backend RAG_Be để tra cứu danh sách tài liệu học tập trong thư viện.
 * Hỗ trợ tự động chuyển đổi thông minh giữa Mock và API thực thông qua [AppConfig.USE_MOCK_DOCUMENT].
 */
class DocumentRepository : BaseRepository() {
    private val documentService = ApiClient.createService<DocumentApiService>()



    /**
     * Lấy danh sách tài liệu đang hiển thị (VISIBLE) từ Backend hoặc Mock.
     */
    suspend fun getLibraryDocuments(search: String = ""): ApiResult<List<DocumentUiModel>> {

        return safeApiCall {
            val response = documentService.listDocuments(offset = 0, limit = 50, search = search)
            val docDtos = response.data?.documents ?: emptyList()
            docDtos.map { dto ->
                val format = determineFileFormat(dto.title, dto.originalFilename, dto.fileType)
                
                // Tính toán dung lượng hiển thị
                val sizeInBytes = if (dto.fileSize > 0) dto.fileSize else dto.fileSizeBytes
                val sizeText = when {
                    sizeInBytes >= 1024 * 1024 -> String.format(java.util.Locale.US, "%.1f MB", sizeInBytes.toFloat() / (1024 * 1024))
                    sizeInBytes >= 1024 -> "${sizeInBytes / 1024} KB"
                    else -> "$sizeInBytes B"
                }

                DocumentUiModel(
                    id = dto.id.toString(),
                    title = dto.title.ifEmpty { dto.originalFilename },
                    category = "Tài liệu học tập",
                    fileFormat = format,
                    pageOrSlideCount = dto.pageCount ?: 0,
                    fileSizeText = sizeText,
                    previewAvailable = dto.previewAvailable,
                    previewUrl = dto.previewUrl
                )
            }
        }
    }

    /**
     * Trả về nội dung trang tài liệu phục vụ màn hình đọc (Reader) từ Mock hoặc API.
     */
    fun getDocumentPageContent(page: Int): ReaderPageContentUiModel {

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
            
            val format = determineFileFormat(dto.title, dto.originalFilename, dto.fileType)
            
            DocumentUiModel(
                id = dto.id.toString(),
                title = dto.title.ifEmpty { dto.originalFilename },
                category = "Tài liệu học tập",
                fileFormat = format,
                pageOrSlideCount = 1, // Giá trị mặc định
                previewAvailable = dto.previewAvailable,
                previewUrl = dto.previewUrl
            )
        }
    }

    /**
     * Tải file gốc tài liệu từ Backend lưu vào thư mục Cache cục bộ với cơ chế báo cáo tiến trình (Progress).
     * Áp dụng cơ chế Offline-First Cache: Nếu file đã tồn tại cục bộ thì bỏ qua việc tải mạng.
     */
    suspend fun downloadDocumentFile(
        context: Context, 
        docId: String, 
        onProgress: (Int) -> Unit = {}
    ): java.io.File? {

        return withContext(Dispatchers.IO) {
            try {
                val file = java.io.File(context.cacheDir, "doc_${docId}.pdf")
                
                // 1. Kiểm tra chi tiết tài liệu xem có bản PDF xem trước hay không trước khi kiểm tra Cache
                var usePreview = false
                try {
                    val detailResponse = documentService.getDocumentDetail(docId)
                    val dto = detailResponse.data?.document
                    usePreview = dto?.previewAvailable == true && !dto.previewUrl.isNullOrEmpty()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
 
                // 2. Cơ chế Cache Offline-First kết hợp xác thực định dạng
                if (file.exists() && file.length() > 0) {
                    val isCachedPdf = isPdfFile(file)
                    // Nếu ở Server đã có bản PDF Preview nhưng Cache đang chứa file gốc Word (.docx) cũ không phải PDF
                    // -> Xóa file cache cũ để buộc tải lại bản PDF mới.
                    if (usePreview && !isCachedPdf) {
                        file.delete()
                    } else {
                        onProgress(100)
                        return@withContext file
                    }
                }

                // Tải file mới (dùng luồng PDF preview hoặc luồng tệp gốc)
                var responseBody: okhttp3.ResponseBody? = null
                if (usePreview) {
                    try {
                        responseBody = documentService.downloadDocumentPreview(docId)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        // Nếu API /preview bị sập (ví dụ lỗi 500 do BE gặp sự cố Header Unicode), 
                        // tự động fallback về tải file gốc để đọc tạm bằng Text Viewer.
                        responseBody = documentService.downloadDocument(docId)
                    }
                } else {
                    responseBody = documentService.downloadDocument(docId)
                }
                
                val contentLength = responseBody!!.contentLength()
                
                responseBody.byteStream().use { inputStream ->
                    file.outputStream().use { outputStream ->
                        val buffer = ByteArray(8192)
                        var bytesRead = 0L
                        var read = inputStream.read(buffer)
                        while (read != -1) {
                            outputStream.write(buffer, 0, read)
                            bytesRead += read
                            if (contentLength > 0) {
                                val progress = ((bytesRead * 100) / contentLength).toInt()
                                // Cập nhật tiến độ tải về UI
                                onProgress(progress.coerceIn(0, 100))
                            }
                            read = inputStream.read(buffer)
                        }
                    }
                }
                
                // Đảm bảo hoàn thành 100%
                onProgress(100)
                file
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }

    /**
     * Lưu ID tài liệu đọc dở gần nhất vào SharedPreferences.
     */
    fun saveLastReadDocumentId(context: Context, docId: String) {
        val prefs = context.getSharedPreferences("edurag_prefs", Context.MODE_PRIVATE)
        prefs.edit().putString("last_read_doc_id", docId).apply()
    }

    /**
     * Lấy ID tài liệu đọc dở gần nhất từ SharedPreferences.
     */
    fun getLastReadDocumentId(context: Context): String? {
        val prefs = context.getSharedPreferences("edurag_prefs", Context.MODE_PRIVATE)
        return prefs.getString("last_read_doc_id", null)
    }

    private fun determineFileFormat(title: String, originalFilename: String, fileType: String): DocumentFileFormat {
        val nameLower = originalFilename.lowercase()
        val titleLower = title.lowercase()
        val typeLower = fileType.lowercase()
        
        return when {
            nameLower.endsWith(".ppt") || nameLower.endsWith(".pptx") -> DocumentFileFormat.SLIDE
            (nameLower.endsWith(".pdf") || typeLower.contains("pdf")) && 
                (titleLower.contains("slide") || nameLower.contains("slide") ||
                 titleLower.contains("bài giảng") || nameLower.contains("bài giảng") ||
                 titleLower.contains("presentation") || nameLower.contains("presentation")) -> DocumentFileFormat.SLIDE
            nameLower.endsWith(".pdf") || typeLower.contains("pdf") -> DocumentFileFormat.PDF
            nameLower.endsWith(".doc") || nameLower.endsWith(".docx") || nameLower.endsWith(".txt") -> DocumentFileFormat.WORD
            else -> DocumentFileFormat.OTHER
        }
    }

    private fun isPdfFile(file: java.io.File): Boolean {
        if (!file.exists() || file.length() < 4) return false
        return try {
            file.inputStream().use { input ->
                val header = ByteArray(4)
                val read = input.read(header)
                read == 4 && header[0] == 0x25.toByte() && header[1] == 0x50.toByte() && 
                        header[2] == 0x44.toByte() && header[3] == 0x46.toByte()
            }
        } catch (e: Exception) {
            false
        }
    }
}
