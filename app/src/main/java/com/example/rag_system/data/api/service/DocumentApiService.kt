package com.example.rag_system.data.api.service

import com.example.rag_system.data.api.model.BaseApiResponseDto
import com.example.rag_system.data.api.model.CitationDetailDto
import com.example.rag_system.data.api.model.DocumentListResponseDto
import com.example.rag_system.data.api.model.LibraryDocumentDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Streaming

/**
 * Interface Retrofit gọi các đầu API tài liệu dành cho STUDENT.
 * - STUDENT chỉ dùng /api/library (read-only public) và /api/citations.
 * - Không có upload document phía Mobile.
 *
 * Param canonical theo BE mới nhất (commit 6ee707a trở đi):
 * - 'q' là canonical search query (thay cho 'search' legacy).
 * - 'page' là canonical page index (thay cho 'offset' legacy).
 */
interface DocumentApiService {

    // ─────────────────────────────────────────────
    // Document Library  (STUDENT/TEACHER/ADMIN – read-only)
    // ─────────────────────────────────────────────

    /**
     * Lấy danh sách tài liệu công khai. Chỉ trả READY + VISIBLE.
     * @param q Tìm kiếm theo title/description/author (canonical, thay cho legacy 'search').
     * @param page Trang (1-based, canonical). Dùng 'page' thay cho 'offset' cũ.
     * @param limit Số lượng mỗi trang (1-100, default 20).
     * @param fileType Lọc theo loại: "PDF", "DOCX", "TXT" (optional).
     * @param sort Sắp xếp: "newest"(default),"oldest","title_asc","title_desc".
     */
    @GET("api/library/documents")
    suspend fun listDocuments(
        @Query("q") q: String? = null,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20,
        @Query("fileType") fileType: String? = null,
        @Query("sort") sort: String? = null
    ): BaseApiResponseDto<DocumentListResponseDto>

    /** Chi tiết một tài liệu trong Library. */
    @GET("api/library/documents/{id}")
    suspend fun getLibraryDocument(
        @Path("id") id: Long
    ): BaseApiResponseDto<LibraryDocumentDto>

    /**
     * Tải file về dạng attachment (canonical download cho STUDENT):
     * - PDF → file PDF gốc
     * - DOCX → derived PDF (khi previewStatus = READY)
     * - TXT → file TXT gốc
     * Dùng downloadUrl từ [LibraryDocumentDto]; fetch với Bearer.
     * STUDENT dùng endpoint này, KHÔNG dùng /source cho PDF/DOCX.
     */
    @GET("api/library/documents/{id}/download")
    @Streaming
    suspend fun downloadDocument(
        @Path("id") id: String
    ): okhttp3.ResponseBody

    /**
     * Xem trước dạng PDF inline.
     * previewAvailable phải là true; DOCX trả PDF preview khi READY.
     * 409 PREVIEW_UNAVAILABLE nếu pending/failed/not applicable.
     */
    @GET("api/library/documents/{id}/preview")
    @Streaming
    suspend fun downloadDocumentPreview(
        @Path("id") id: String
    ): okhttp3.ResponseBody

    /**
     * Tải file gốc (source) — chỉ dùng cho TXT hoặc Teacher/Admin.
     * PDF/DOCX trả 403 với STUDENT — dùng /download thay thế.
     */
    @GET("api/library/documents/{id}/source")
    @Streaming
    suspend fun downloadDocumentSource(
        @Path("id") id: String
    ): okhttp3.ResponseBody


    // ─────────────────────────────────────────────
    // Citations  (session owner only)
    // ─────────────────────────────────────────────

    /**
     * Lấy chi tiết citation kèm originalAvailable.
     * Chỉ session owner mới gọi được, kể cả ADMIN.
     * sourceLocator thường null cho đến khi Python tạo locator.
     */
    @GET("api/citations/{id}")
    suspend fun getCitation(
        @Path("id") citationId: Long
    ): BaseApiResponseDto<CitationDetailDto>
}
