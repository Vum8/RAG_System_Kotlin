package com.example.rag_system.data.api.model

import com.google.gson.annotations.SerializedName

data class ChatSessionDto(
    @SerializedName("id") val id: Long = 0,
    @SerializedName("title") val title: String? = null,
    @SerializedName("lastMessageAt") val lastMessageAt: String? = null,
    @SerializedName("createdAt") val createdAt: String? = null,
    @SerializedName("updatedAt") val updatedAt: String? = null
)

data class ChatSessionListResponseDto(
    @SerializedName("offset") val offset: Int = 0,
    @SerializedName("limit") val limit: Int = 20,
    @SerializedName("total") val total: Long = 0,
    @SerializedName("sessions") val sessions: List<ChatSessionDto> = emptyList()
)

data class CreateSessionRequestDto(
    @SerializedName("title") val title: String? = null
)

data class CitationDto(
    @SerializedName("id") val id: Long = 0,
    @SerializedName("messageId") val messageId: Long = 0,
    @SerializedName("documentId") val documentId: Long = 0,
    @SerializedName("chunkId") val chunkId: Long = 0,
    @SerializedName("citationOrder") val citationOrder: Int = 0,
    @SerializedName("documentTitle") val documentTitle: String? = null,
    @SerializedName("pageNumber") val pageNumber: Int? = null,
    @SerializedName("sectionTitle") val sectionTitle: String? = null,
    @SerializedName("sourceText") val sourceText: String? = null,
    @SerializedName("retrievalScore") val retrievalScore: Double? = null,
    @SerializedName("rerankScore") val rerankScore: Double? = null
)

data class ChatMessageDto(
    @SerializedName("id") val id: Long = 0,
    @SerializedName("sessionId") val sessionId: Long = 0,
    @SerializedName("senderType") val senderType: String = "USER",
    @SerializedName("messageOrder") val messageOrder: Int = 0,
    @SerializedName("content") val content: String? = null,
    @SerializedName("status") val status: String = "COMPLETED",
    @SerializedName("noAnswer") val noAnswer: Boolean = false,
    @SerializedName("clientRequestId") val clientRequestId: String? = null,
    @SerializedName("errorCode") val errorCode: String? = null,
    @SerializedName("completedAt") val completedAt: String? = null,
    @SerializedName("createdAt") val createdAt: String? = null,
    @SerializedName("citations") val citations: List<CitationDto> = emptyList()
)

data class ChatMessageListResponseDto(
    @SerializedName("session") val session: ChatSessionDto? = null,
    @SerializedName("offset") val offset: Int = 0,
    @SerializedName("limit") val limit: Int = 50,
    @SerializedName("total") val total: Long = 0,
    @SerializedName("messages") val messages: List<ChatMessageDto> = emptyList()
)

data class SendMessageRequestDto(
    @SerializedName("content") val content: String,
    @SerializedName("clientRequestId") val clientRequestId: String
)

data class SendMessageResponseDto(
    @SerializedName("duplicate") val duplicate: Boolean = false,
    @SerializedName("userMessageId") val userMessageId: Long = 0,
    @SerializedName("assistantMessage") val assistantMessage: ChatMessageDto? = null
)
