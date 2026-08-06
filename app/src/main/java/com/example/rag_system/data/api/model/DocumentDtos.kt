package com.example.rag_system.data.api.model

import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName

// ─────────────────────────────────────────────────────────────
// Library Document DTO  – public allowlist mà BE trả cho STUDENT
// Ref: docs/api/frontend-integration.md (commit 6ee707a+)
// ─────────────────────────────────────────────────────────────

/**
 * DTO tài liệu dùng với /api/library/documents.
 * BE chỉ trả các field public, không có owner/storagePath/checksum.
 * - [previewAvailable]: true nếu có PDF preview (PDF luôn true, DOCX khi READY).
 * - [previewUrl]: URL authenticated (relative), cần gửi Bearer khi fetch.
 * - [originalAvailable]: true nếu file gốc còn READY+VISIBLE.
 * - [originalFileUrl]: URL authenticated (relative) để download.
 * - [pageCount]: PDF = số trang vật lý; DOCX = null cho đến khi preview READY; TXT = null.
 */
data class LibraryDocumentDto(
    @SerializedName("id") val id: Long = 0,
    @SerializedName("title") val title: String = "",
    @SerializedName("description") val description: String? = null,
    @SerializedName("author") val author: String? = null,
    @SerializedName("fileType") val fileType: String = "",
    @SerializedName("fileSize") val fileSize: Long = 0,
    @SerializedName("pageCount") val pageCount: Int? = null,
    @SerializedName("previewStatus") val previewStatus: String = "NOT_APPLICABLE",
    @SerializedName("previewAvailable") val previewAvailable: Boolean = false,
    @SerializedName("previewMimeType") val previewMimeType: String? = null,
    @SerializedName("previewUrl") val previewUrl: String? = null,
    @SerializedName("originalAvailable") val originalAvailable: Boolean = false,
    @SerializedName("originalFileUrl") val originalFileUrl: String? = null,
    /**
     * Canonical download URL — GET /api/library/documents/{id}/download
     * Student dùng endpoint này để tải file:
     * - PDF: file PDF gốc
     * - DOCX: derived PDF (khi preview READY)
     * - TXT: file TXT gốc
     * Student KHÔNG dùng /source (PDF/DOCX trả 403).
     */
    @SerializedName("downloadUrl") val downloadUrl: String? = null,
    @SerializedName("createdAt") val createdAt: String? = null,
    @SerializedName("updatedAt") val updatedAt: String? = null
)

// ─────────────────────────────────────────────────────────────
// List response – thêm page/totalPages canonical theo BE mới
// ─────────────────────────────────────────────────────────────

/**
 * Wrapper danh sách tài liệu từ /api/library/documents.
 * BE trả cả offset/page/limit/total/totalPages; Mobile dùng page-based.
 */
data class DocumentListResponseDto(
    @SerializedName("offset") val offset: Int = 0,
    @SerializedName("page") val page: Int = 1,
    @SerializedName("limit") val limit: Int = 20,
    @SerializedName("total") val total: Long = 0,
    @SerializedName("totalPages") val totalPages: Int = 1,
    @SerializedName("documents") val documents: List<LibraryDocumentDto> = emptyList()
)

// ─────────────────────────────────────────────────────────────
// Citation DTO  – từ GET /api/citations/{id}
// ─────────────────────────────────────────────────────────────

/**
 * Chi tiết một citation (snapshot bất biến).
 * [sourceLocator]: object JSON từ Python – hiện thường null;
 *   FE dùng [sourceText] làm fallback highlight theo docs BE.
 * [originalAvailable]: true nếu file gốc hiện còn có thể mở.
 */
data class CitationDetailDto(
    @SerializedName("id") val id: Long = 0,
    @SerializedName("messageId") val messageId: Long = 0,
    @SerializedName("documentId") val documentId: Long = 0,
    @SerializedName("chunkId") val chunkId: Long = 0,
    @SerializedName("citationOrder") val citationOrder: Int = 0,
    @SerializedName("documentTitle") val documentTitle: String? = null,
    @SerializedName("pageNumber") val pageNumber: Int? = null,
    @SerializedName("sectionTitle") val sectionTitle: String? = null,
    @SerializedName("sourceText") val sourceText: String? = null,
    @SerializedName("sourceLocator") val sourceLocator: JsonElement? = null,
    @SerializedName("retrievalScore") val retrievalScore: Double? = null,
    @SerializedName("rerankScore") val rerankScore: Double? = null,
    @SerializedName("originalAvailable") val originalAvailable: Boolean = false
)

// ─────────────────────────────────────────────────────────────
// Legacy – giữ lại để không phá compile các screen đang dùng
// ─────────────────────────────────────────────────────────────

/** @deprecated Dùng LibraryDocumentDto thay thế. */
@Deprecated("Dùng LibraryDocumentDto", ReplaceWith("LibraryDocumentDto"))
typealias DocumentDto = LibraryDocumentDto
