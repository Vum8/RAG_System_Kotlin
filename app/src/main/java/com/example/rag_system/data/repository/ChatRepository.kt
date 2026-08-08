package com.example.rag_system.data.repository

import com.example.rag_system.data.api.core.ApiClient
import com.example.rag_system.data.api.core.ApiResult
import com.example.rag_system.data.api.model.CreateSessionRequestDto
import com.example.rag_system.data.api.model.SendMessageRequestDto
import com.example.rag_system.data.api.service.ChatApiService
import com.example.rag_system.data.config.AppConfig
import com.example.rag_system.ui.models.ChatSessionUiModel
import com.example.rag_system.ui.models.MessageUiModel
import com.example.rag_system.ui.models.SourceCitationUiModel
import java.util.UUID

/**
 * Repository kết nối với Backend RAG_Be cho nghiệp vụ hỏi đáp AI và quản lý phiên hội thoại.
 * Hỗ trợ tự động chuyển đổi thông minh giữa Mock và API thực thông qua [AppConfig.USE_MOCK_CHAT].
 */
class ChatRepository : BaseRepository() {
    private val chatService = ApiClient.createService<ChatApiService>()

    var currentSessionId: Long? = null
        private set

    fun setCurrentSession(sessionId: Long?) {
        currentSessionId = sessionId
    }



    /**
     * Lấy danh sách lịch sử các phiên hội thoại RAG từ Backend hoặc Mock.
     */
    suspend fun getChatHistory(offset: Int = 0, limit: Int = 20): ApiResult<List<ChatSessionUiModel>> {

        return safeApiCall {
            val response = chatService.listSessions(offset = offset, limit = limit)
            val sessionDtos = response.data?.sessions ?: emptyList()
            sessionDtos.map { dto ->
                ChatSessionUiModel(
                    id = dto.id.toString(),
                    title = dto.title ?: "Cuộc hội thoại EduRAG",
                    lastMessagePreview = "Phiên hỏi đáp RAG học tập",
                    displayTime = dto.lastMessageAt?.take(10) ?: dto.createdAt?.take(10) ?: "Gần đây",
                    subjectLabel = "Học tập"
                )
            }
        }
    }

    /**
     * Gửi truy vấn hỏi đáp RAG tới Backend hoặc xử lý Mock.
     */
    suspend fun sendChatQuery(query: String): ApiResult<MessageUiModel> {


        return safeApiCall {
            var sessionId = currentSessionId
            if (sessionId == null || sessionId <= 0L) {
                val titleText = if (query.length > 35) query.take(35) + "..." else query
                val createResponse = chatService.createSession(CreateSessionRequestDto(title = titleText))
                sessionId = createResponse.data?.id
                if (sessionId == null || sessionId <= 0L) {
                    throw IllegalStateException("Không thể khởi tạo phiên hội thoại mới trên máy chủ.")
                }
                currentSessionId = sessionId
            }

            val clientRequestId = UUID.randomUUID().toString()
            val sendResponse = chatService.sendMessage(
                sessionId = sessionId,
                request = SendMessageRequestDto(
                    content = query,
                    clientRequestId = clientRequestId
                )
            )

            val assistantDto = sendResponse.data?.assistantMessage
                ?: throw IllegalStateException("Không nhận được câu trả lời từ trợ lý AI EduRAG.")

            val citationsList = assistantDto.citations.map { citationDto ->
                SourceCitationUiModel(
                    citationOrder = citationDto.citationOrder,
                    documentId = citationDto.documentId.toString(),
                    sourceDocumentName = citationDto.documentTitle ?: "Tài liệu hệ thống",
                    pageNumber = citationDto.pageNumber ?: 1,
                    chapterSection = citationDto.sectionTitle ?: "Nội dung trích xuất",
                    rawExtractedText = citationDto.sourceText ?: ""
                )
            }

            MessageUiModel(
                id = "msg_${assistantDto.id}",
                content = assistantDto.content ?: (if (assistantDto.noAnswer) "Xin lỗi, tôi không tìm thấy câu trả lời liên quan trong kho tài liệu hiện tại." else ""),
                isFromUser = false,
                sendTime = assistantDto.completedAt?.take(16)?.replace("T", " ") ?: "Vừa xong",
                citations = citationsList,
                noAnswer = assistantDto.noAnswer
            )
        }
    }

    /**
     * Lấy toàn bộ danh sách tin nhắn của một phiên chat cụ thể từ Backend hoặc Mock.
     */
    suspend fun getSessionMessages(sessionId: Long, offset: Int = 0, limit: Int = 50): ApiResult<List<MessageUiModel>> {


        return safeApiCall {
            currentSessionId = sessionId
            val response = chatService.getHistory(sessionId = sessionId, offset = offset, limit = limit)
            val msgDtos = response.data?.messages ?: emptyList()
            msgDtos.map { dto ->
                val citationsList = dto.citations.map { c ->
                    SourceCitationUiModel(
                        citationOrder = c.citationOrder,
                        documentId = c.documentId.toString(),
                        sourceDocumentName = c.documentTitle ?: "Tài liệu hệ thống",
                        pageNumber = c.pageNumber ?: 1,
                        chapterSection = c.sectionTitle ?: "Nội dung trích xuất",
                        rawExtractedText = c.sourceText ?: ""
                    )
                }
                MessageUiModel(
                    id = "msg_${dto.id}",
                    content = dto.content ?: "",
                    isFromUser = dto.senderType.equals("USER", ignoreCase = true),
                    sendTime = dto.completedAt?.take(16)?.replace("T", " ") ?: dto.createdAt?.take(16)?.replace("T", " ") ?: "Vừa xong",
                    citations = citationsList,
                    noAnswer = dto.noAnswer
                )
            }
        }
    }

    /**
     * Xóa một phiên hội thoại cụ thể khỏi Backend.
     */
    suspend fun deleteSession(sessionId: Long): ApiResult<Unit> {
        return safeApiCall {
            chatService.deleteSession(sessionId)
            Unit
        }
    }
}
