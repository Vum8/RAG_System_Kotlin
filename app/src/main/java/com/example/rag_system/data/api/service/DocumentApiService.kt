package com.example.rag_system.data.api.service

import com.example.rag_system.data.api.model.BaseApiResponseDto
import com.example.rag_system.data.api.model.DocumentDetailResponseDto
import com.example.rag_system.data.api.model.DocumentListResponseDto
import com.example.rag_system.data.api.model.DocumentDto
import okhttp3.MultipartBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Streaming

/**
 * Interface Retrofit gọi các đầu API xem danh sách và chi tiết tài liệu RAG.
 */
interface DocumentApiService {
    @Multipart
    @POST("api/documents")
    suspend fun uploadDocument(
        @Part file: MultipartBody.Part
    ): BaseApiResponseDto<DocumentDto>

    @GET("api/library/documents")
    suspend fun listDocuments(
        @Query("offset") offset: Int = 0,
        @Query("limit") limit: Int = 20,
        @Query("search") search: String = ""
    ): BaseApiResponseDto<DocumentListResponseDto>

    @GET("api/documents/{id}")
    suspend fun getDocumentDetail(
        @Path("id") id: String
    ): BaseApiResponseDto<DocumentDetailResponseDto>

    @GET("api/library/documents/{id}/source")
    @Streaming
    suspend fun downloadDocument(
        @Path("id") id: String
    ): okhttp3.ResponseBody

    @GET("api/library/documents/{id}/preview")
    @Streaming
    suspend fun downloadDocumentPreview(
        @Path("id") id: String
    ): okhttp3.ResponseBody
}
