package com.example.rag_system.data.api.service

import com.example.rag_system.data.api.model.BaseApiResponseDto
import com.example.rag_system.data.api.model.ChatMessageListResponseDto
import com.example.rag_system.data.api.model.ChatSessionDto
import com.example.rag_system.data.api.model.ChatSessionListResponseDto
import com.example.rag_system.data.api.model.CreateSessionRequestDto
import com.example.rag_system.data.api.model.SendMessageRequestDto
import com.example.rag_system.data.api.model.SendMessageResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Interface Retrofit gọi các đầu API quản lý phiên hỏi đáp và hỏi đáp AI RAG.
 */
interface ChatApiService {
    @GET("api/chat/sessions")
    suspend fun listSessions(
        @Query("offset") offset: Int = 0,
        @Query("limit") limit: Int = 20
    ): BaseApiResponseDto<ChatSessionListResponseDto>

    @POST("api/chat/sessions")
    suspend fun createSession(@Body request: CreateSessionRequestDto): BaseApiResponseDto<ChatSessionDto>

    @GET("api/chat/sessions/{id}/messages")
    suspend fun getHistory(
        @Path("id") sessionId: Long,
        @Query("offset") offset: Int = 0,
        @Query("limit") limit: Int = 50
    ): BaseApiResponseDto<ChatMessageListResponseDto>

    @POST("api/chat/sessions/{id}/messages")
    suspend fun sendMessage(
        @Path("id") sessionId: Long,
        @Body request: SendMessageRequestDto
    ): BaseApiResponseDto<SendMessageResponseDto>

    @DELETE("api/chat/sessions/{id}")
    suspend fun deleteSession(@Path("id") sessionId: Long): Response<Unit>
}
