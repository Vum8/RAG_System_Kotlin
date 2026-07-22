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

    private val mockHistory = listOf(
        ChatSessionUiModel("101", "Tìm hiểu kiến trúc Von Neumann", "Đặc trưng lớn nhất của kiến trúc Von Neumann...", "14-07-2026", "Kiến trúc máy tính"),
        ChatSessionUiModel("102", "So sánh hệ điều hành Windows và Linux", "Hệ điều hành mã nguồn mở Linux...", "13-07-2026", "Hệ điều hành"),
        ChatSessionUiModel("103", "Hướng dẫn cấu hình mạng LAN", "Địa chỉ IP tĩnh và cơ chế DHCP...", "12-07-2026", "Mạng máy tính")
    )

    /**
     * Lấy danh sách lịch sử các phiên hội thoại RAG từ Backend hoặc Mock.
     */
    suspend fun getChatHistory(): ApiResult<List<ChatSessionUiModel>> {
        if (AppConfig.USE_MOCK_CHAT) {
            return ApiResult.Success(mockHistory)
        }
        return safeApiCall {
            val response = chatService.listSessions(offset = 0, limit = 50)
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
        if (AppConfig.USE_MOCK_CHAT) {
            // Giả lập AI suy nghĩ một chút
            kotlinx.coroutines.delay(800)
            
            val responseText = when {
                query.contains("Neumann", ignoreCase = true) || query.contains("Von", ignoreCase = true) -> 
                    "Kiến trúc Von Neumann (còn được gọi là mô hình Von Neumann) mô tả một thiết kế máy tính sử dụng chung một không gian bộ nhớ vật lý để lưu trữ cả chương trình và dữ liệu. Mô hình này bao gồm CPU (với ALU và các thanh ghi), bộ điều khiển, bộ nhớ chính và thiết bị ngoại vi vào/ra."
                query.contains("mạng", ignoreCase = true) || query.contains("IP", ignoreCase = true) -> 
                    "Mạng máy tính là một tập hợp các thiết bị máy tính được kết nối với nhau để chia sẻ tài nguyên và thông tin. Địa chỉ IP (Internet Protocol) là một nhãn số được gán cho mỗi thiết bị tham gia vào mạng để định danh và định tuyến dữ liệu."
                else -> 
                    "Tôi đã nhận được câu hỏi: '$query'. Theo tài liệu học tập của hệ thống EduRAG, kiến thức này thuộc phạm vi tài liệu 'Kiến trúc máy tính cơ bản'. Bạn có thể xem thêm chi tiết trong file đính kèm bên dưới hoặc tài liệu tham khảo trong Thư viện."
            }

            val citations = listOf(
                SourceCitationUiModel(
                    sourceDocumentName = "Kiến trúc máy tính",
                    pageNumber = 2,
                    chapterSection = "1.2 Mô hình Von Neumann",
                    rawExtractedText = "Đặc trưng lớn nhất của kiến trúc Von Neumann là chương trình và dữ liệu được lưu trữ chung trong cùng một không gian bộ nhớ vật lý."
                )
            )

            if (currentSessionId == null) {
                currentSessionId = 101L
            }

            return ApiResult.Success(
                MessageUiModel(
                    id = UUID.randomUUID().toString(),
                    content = responseText,
                    isFromUser = false,
                    sendTime = "Vừa xong",
                    citations = citations
                )
            )
        }

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
                citations = citationsList
            )
        }
    }

    /**
     * Lấy toàn bộ danh sách tin nhắn của một phiên chat cụ thể từ Backend hoặc Mock.
     */
    suspend fun getSessionMessages(sessionId: Long): ApiResult<List<MessageUiModel>> {
        if (AppConfig.USE_MOCK_CHAT) {
            currentSessionId = sessionId
            val mockMsgs = when (sessionId) {
                101L -> listOf(
                    MessageUiModel("m1", "Mô hình Von Neumann hoạt động thế nào?", true, "14-07-2026 21:00"),
                    MessageUiModel(
                        id = "m2",
                        content = "Mô hình Von Neumann là nền tảng của hầu hết thiết kế máy tính hiện đại. Đặc trưng lớn nhất là chương trình và dữ liệu được lưu trữ chung trong cùng một không gian bộ nhớ vật lý.",
                        isFromUser = false,
                        sendTime = "14-07-2026 21:01",
                        citations = listOf(
                            SourceCitationUiModel(
                                sourceDocumentName = "Kiến trúc máy tính",
                                pageNumber = 2,
                                chapterSection = "1.2 Mô hình Von Neumann",
                                rawExtractedText = "Đặc trưng lớn nhất của kiến trúc Von Neumann là chương trình và dữ liệu được lưu trữ chung trong cùng một không gian bộ nhớ vật lý."
                            )
                        )
                    )
                )
                102L -> listOf(
                    MessageUiModel("m3", "Windows và Linux khác nhau như thế nào?", true, "13-07-2026 10:00"),
                    MessageUiModel(
                        id = "m4",
                        content = "Hệ điều hành Windows là hệ điều hành mã nguồn đóng của Microsoft, dễ tiếp cận người dùng phổ thông. Linux là hệ điều hành mã nguồn mở, có tính an mật cao và được dùng rộng rãi trên các hệ thống máy chủ RAG.",
                        isFromUser = false,
                        sendTime = "13-07-2026 10:02",
                        citations = listOf(
                            SourceCitationUiModel(
                                sourceDocumentName = "Hệ điều hành cơ bản",
                                pageNumber = 5,
                                chapterSection = "2.1 So sánh nhân hệ điều hành",
                                rawExtractedText = "Hệ điều hành mã nguồn mở Linux được thiết kế cho sự ổn định và bảo mật cao trong các ứng dụng máy chủ RAG."
                            )
                        )
                    )
                )
                else -> listOf(
                    MessageUiModel("m5", "Địa chỉ IP tĩnh là gì?", true, "12-07-2026 09:15"),
                    MessageUiModel(
                        id = "m6",
                        content = "Địa chỉ IP tĩnh là một địa chỉ IP cố định được thiết lập thủ công cho thiết bị, không thay đổi theo thời gian giống như IP động cấp từ DHCP.",
                        isFromUser = false,
                        sendTime = "12-07-2026 09:16",
                        citations = listOf(
                            SourceCitationUiModel(
                                sourceDocumentName = "Mạng máy tính",
                                pageNumber = 12,
                                chapterSection = "3.2 Định cấu hình mạng cục bộ",
                                rawExtractedText = "Địa chỉ IP tĩnh giúp duy trì kết nối ổn định cho các thiết bị cần làm máy chủ dịch vụ cục bộ."
                            )
                        )
                    )
                )
            }
            return ApiResult.Success(mockMsgs)
        }

        return safeApiCall {
            currentSessionId = sessionId
            val response = chatService.getHistory(sessionId = sessionId, offset = 0, limit = 100)
            val msgDtos = response.data?.messages ?: emptyList()
            msgDtos.map { dto ->
                val citationsList = dto.citations.map { c ->
                    SourceCitationUiModel(
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
                    citations = citationsList
                )
            }
        }
    }
}
