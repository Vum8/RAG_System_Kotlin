package com.example.rag_system.data.repository

import com.example.rag_system.data.api.core.ApiResult
import com.example.rag_system.ui.models.ChatSessionUiModel
import com.example.rag_system.ui.models.MessageUiModel
import com.example.rag_system.ui.models.SourceCitationUiModel
import kotlinx.coroutines.delay

/**
 * Mock Repository giả lập lịch sử cuộc hội thoại và gửi truy vấn hỏi đáp RAG với trợ lý AI EduRAG.
 * Kế thừa [BaseRepository] và giả lập độ trễ 1.5 giây.
 */
class MockChatRepository : BaseRepository() {

    suspend fun getChatHistory(): ApiResult<List<ChatSessionUiModel>> = safeApiCall {
        delay(1500L)
        listOf(
            ChatSessionUiModel(
                id = "session_01",
                title = "RAG trong AI",
                lastMessagePreview = "Dựa trên tài liệu [1] , khái niệm RAG (Retrieval-Augmented Generation) là một kỹ thuật trong AI giúp kết hợp khả năng tạo văn bản của mô hình ngôn ngữ lớn với việc truy xuất thông tin từ kho dữ liệu học thuật riêng biệt.\n\nTrong giáo dục [2] , RAG giúp sinh viên tra cứu tài liệu nhanh chóng và chính xác hơn bằng cách:\n\n  • Cung cấp câu trả lời có dẫn chứng trực tiếp từ giáo trình.\n  • Giảm thiểu tình trạng AI \"ảo tưởng\" (hallucinations).",
                displayTime = "2 giờ trước",
                subjectLabel = "Trí tuệ nhân tạo",
                citations = listOf(
                    SourceCitationUiModel(
                        sourceDocumentName = "Giáo trình Nhập môn AI.pdf",
                        pageNumber = 45,
                        chapterSection = "Phân chương 2.1",
                        rawExtractedText = "RAG giúp tối ưu hóa việc truy xuất kiến thức từ các nguồn tài liệu chính thống..."
                    )
                )
            ),
            ChatSessionUiModel(
                id = "session_02",
                title = "Ôn tập Triết học Mác-Lênin",
                lastMessagePreview = "Phân tích mối quan hệ biện chứng giữa vật chất và ý thức trong triết học...",
                displayTime = "Hôm qua",
                subjectLabel = "Triết học"
            ),
            ChatSessionUiModel(
                id = "session_03",
                title = "Hỏi đáp đồ án tốt nghiệp",
                lastMessagePreview = "Quy cách đóng quyển và chuẩn bị slide bảo vệ đồ án tốt nghiệp...",
                displayTime = "3 ngày trước",
                subjectLabel = "Khóa luận tốt nghiệp"
            )
        )
    }

    suspend fun sendChatQuery(query: String): ApiResult<MessageUiModel> = safeApiCall {
        delay(1500L)
        MessageUiModel(
            id = "msg_${System.currentTimeMillis()}",
            content = "Trong lĩnh vực giáo dục, công nghệ RAG (Retrieval-Augmented Generation) cho phép hệ thống AI tra cứu trực tiếp vào các giáo trình, bài giảng và tài liệu của trường trước khi sinh câu trả lời. Điều này giúp sinh viên tiếp cận đúng kiến thức chuẩn của môn học, loại bỏ hoàn toàn tình trạng bịa thông tin (hallucination) thường gặp ở các AI thông thường.",
            isFromUser = false,
            sendTime = "Vừa xong",
            citations = listOf(
                SourceCitationUiModel(
                    sourceDocumentName = "Giáo trình Nhập môn AI.pdf",
                    pageNumber = 45,
                    chapterSection = "Phân chương 2.1",
                    rawExtractedText = "Hệ thống Retrieval-Augmented Generation (RAG) kết hợp giữa mô hình ngôn ngữ lớn và cơ sở tri thức nội bộ để cung cấp câu trả lời chính xác, giảm thiểu hiện tượng ảo giác trong môi trường học tập."
                )
            )
        )
    }
}
