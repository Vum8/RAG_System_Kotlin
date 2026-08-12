package com.example.rag_system.data.repository

import com.example.rag_system.data.api.core.ApiClient
import com.example.rag_system.data.api.core.ApiResult
import com.example.rag_system.data.api.service.DocumentApiService
import com.example.rag_system.data.config.AppConfig
import com.example.rag_system.ui.models.DocumentFileFormat
import com.example.rag_system.ui.models.DocumentUiModel
import com.example.rag_system.ui.models.ReaderPageContentUiModel
import com.example.rag_system.ui.models.SourceCitationUiModel
import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Repository kết nối với Backend RAG_Be để tra cứu danh sách tài liệu học tập trong thư viện.
 * Hỗ trợ tự động chuyển đổi thông minh giữa Mock và API thực thông qua [AppConfig.USE_MOCK_DOCUMENT].
 */
class DocumentRepository : BaseRepository() {
    private val documentService = ApiClient.createService<DocumentApiService>()



    /**
     * Lấy danh sách tài liệu thư viện công khai (chỉ READY + VISIBLE).
     * @param q Tìm kiếm theo title/description/author (canonical param mới).
     * @param page Trang 1-based (mặc định 1).
     * @param limit Số phần tử mỗi trang (mặc định 20).
     * @param fileType Lọc theo loại (PDF/DOCX/TXT), null để lấy hết.
     * @param sort Sắp xếp (newest/oldest/title_asc/title_desc).
     */
    suspend fun getLibraryDocuments(
        q: String? = null,
        page: Int = 1,
        limit: Int = 50,
        fileType: String? = null,
        sort: String? = null
    ): ApiResult<List<DocumentUiModel>> {
        return safeApiCall {
            val response = documentService.listDocuments(
                q = q?.takeIf { it.isNotBlank() },
                page = page,
                limit = limit,
                fileType = fileType,
                sort = sort
            )
            val docDtos = response.data?.documents ?: emptyList()
            docDtos.map { dto ->
                val format = determineFileFormat(dto.title, dto.fileType)

                // Tính toán dung lượng hiển thị
                val sizeText = when {
                    dto.fileSize >= 1024 * 1024 -> String.format(java.util.Locale.US, "%.1f MB", dto.fileSize.toFloat() / (1024 * 1024))
                    dto.fileSize >= 1024 -> "${dto.fileSize / 1024} KB"
                    else -> "${dto.fileSize} B"
                }

                DocumentUiModel(
                    id = dto.id.toString(),
                    title = dto.title,
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

    fun getDocumentPageContent(page: Int): ReaderPageContentUiModel {
        return ReaderPageContentUiModel(
            chapterTitle = "Chi tiết tài liệu học tập",
            sectionTitle = "Trang số $page",
            bodyTextBefore = "Để xem và tương tác toàn văn trang số $page một cách chính xác nhất với định dạng PDF đầy đủ...",
            highlightedSnippet = "Vui lòng truy cập phân hệ 'Thư viện' và mở tài liệu này trực tiếp từ danh sách.",
            bodyTextAfter = "Tính năng xem nhanh từ ô Chat chỉ hỗ trợ hiển thị bối cảnh của đoạn trích dẫn được AI sử dụng để trả lời."
        )
    }


    /**
     * Lấy chi tiết citation theo ID (session owner only).
     * Dùng khi user tap vào citation của tin nhắn AI.
     */
    suspend fun getCitationDetail(citationId: Long): ApiResult<SourceCitationUiModel> {
        return safeApiCall {
            val dto = documentService.getCitation(citationId).data
                ?: throw IllegalStateException("Citation not found")
            SourceCitationUiModel(
                citationOrder = dto.citationOrder,
                documentId = dto.documentId.toString(),
                sourceDocumentName = dto.documentTitle ?: "",
                pageNumber = dto.pageNumber,
                chapterSection = dto.sectionTitle,
                rawExtractedText = dto.sourceText ?: "",
                retrievalScore = dto.retrievalScore,
                rerankScore = dto.rerankScore
                // sourceLocator: JsonObject – bỏ qua cho đến khi Python tạo locator
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
                // 1. Kiểm tra Library document để xem có preview không và lấy chữ ký cache
                var usePreview = false
                var fileExtension = ".pdf"
                var cacheKeySuffix = ""
                try {
                    val detail = documentService.getLibraryDocument(docId.toLong())
                    val dto = detail.data
                    usePreview = dto?.previewAvailable == true && !dto.previewUrl.isNullOrEmpty()
                    val rawSuffix = dto?.updatedAt ?: ""
                    cacheKeySuffix = rawSuffix.replace(Regex("[^a-zA-Z0-9]"), "")
                    
                    // Tôn trọng quy chuẩn Backend: Nếu có Preview (DOCX -> PDF), CHẮC CHẮN phải lưu là .pdf
                    // Nếu không có Preview (như TXT), mới lưu theo định dạng gốc
                    if (!usePreview && dto?.fileType?.lowercase() == "txt") {
                        fileExtension = ".txt"
                    } else {
                        fileExtension = ".pdf"
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                // Chặn đứng nguy cơ lấy nhầm file cũ, sử dụng extension chuẩn theo quy tắc của Backend
                val fileName = if (cacheKeySuffix.isNotEmpty()) "doc_${docId}_${cacheKeySuffix}$fileExtension" else "doc_${docId}_fresh$fileExtension"
                val file = java.io.File(context.cacheDir, fileName)
 
                // 2. Cơ chế Cache Offline-First kết hợp xác thực định dạng
                if (file.exists() && file.length() > 0) {
                    val isCachedPdf = isPdfFile(file)
                    if (usePreview && !isCachedPdf) {
                        // Nếu server báo có PDF nhưng máy đang lưu bản gốc (không phải PDF) -> Xóa để tải lại PDF
                        file.delete()
                    } else {
                        onProgress(100)
                        return@withContext file
                    }
                }

                // Tải file mới theo ưu tiên quy định bởi Backend:
                // 1. /preview — PDF inline khi previewAvailable (DOCX đã có derived PDF)
                // 2. /download — canonical attachment, Student dùng được cho mọi fileType (kể cả txt)
                var responseBody: okhttp3.ResponseBody? = null
                if (usePreview) {
                    try {
                        responseBody = documentService.downloadDocumentPreview(docId)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        // Fallback về /download nếu /preview lỗi (409 PREVIEW_UNAVAILABLE)
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

    /**
     * Xóa ID tài liệu đọc dở khỏi SharedPreferences (dùng khi tài liệu không còn tồn tại trên server).
     */
    fun clearLastReadDocumentId(context: Context) {
        val prefs = context.getSharedPreferences("edurag_prefs", Context.MODE_PRIVATE)
        prefs.edit().remove("last_read_doc_id").apply()
    }

    private fun determineFileFormat(title: String, fileType: String): DocumentFileFormat {
        val titleLower = title.lowercase()
        val typeLower = fileType.lowercase()
        return when {
            typeLower == "pdf" &&
                (titleLower.contains("slide") || titleLower.contains("bài giảng") ||
                    titleLower.contains("presentation")) -> DocumentFileFormat.SLIDE
            typeLower == "pdf" -> DocumentFileFormat.PDF
            typeLower == "docx" || typeLower == "doc" -> DocumentFileFormat.WORD
            typeLower == "txt" -> DocumentFileFormat.TXT
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
