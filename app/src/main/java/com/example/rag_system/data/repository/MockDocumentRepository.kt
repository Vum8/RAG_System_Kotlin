package com.example.rag_system.data.repository

import com.example.rag_system.data.api.core.ApiResult
import com.example.rag_system.ui.models.DocumentFileFormat
import com.example.rag_system.ui.models.DocumentUiModel
import com.example.rag_system.ui.models.ReaderPageContentUiModel
import kotlinx.coroutines.delay

/**
 * Mock Repository giả lập quản lý danh sách tài liệu học tập trong hệ thống EduRAG.
 * Kế thừa [BaseRepository] và tự động trì hoãn 1.5 giây để mô phỏng độ trễ mạng thực tế.
 */
class MockDocumentRepository : BaseRepository() {

    /**
     * Trả về nội dung trang tài liệu theo số trang.
     * Dữ liệu giả lập — sẽ được thay bằng API thật khi backend sẵn sàng.
     */
    fun getDocumentPageContent(page: Int): ReaderPageContentUiModel {
        return readerMockPages[page] ?: ReaderPageContentUiModel(
            chapterTitle = "Giáo trình Nhập môn AI",
            sectionTitle = "Nội dung trang $page",
            bodyTextBefore = "Bạn đang đọc nội dung học tập của giáo trình Nhập môn AI tại trang số $page.",
            highlightedSnippet = "Hệ thống RAG hỗ trợ sinh viên nhanh chóng đối chiếu kiến thức trực tiếp từ trang $page.",
            bodyTextAfter = "Tiếp tục lật trang hoặc kéo thanh trượt để xem các nội dung kiến thức khác."
        )
    }

    suspend fun getLibraryDocuments(): ApiResult<List<DocumentUiModel>> = safeApiCall {
        delay(1500L)
        listOf(
            DocumentUiModel(
                id = "doc_01",
                title = "Kiến trúc máy tính",
                category = "Chương 1 - Tổng quan",
                fileFormat = DocumentFileFormat.SLIDE,
                pageOrSlideCount = 12
            ),
            DocumentUiModel(
                id = "doc_02",
                title = "Hệ điều hành cơ bản",
                category = "Chương 2 - Quản lý tiến trình",
                fileFormat = DocumentFileFormat.SLIDE,
                pageOrSlideCount = 24
            ),
            DocumentUiModel(
                id = "doc_03",
                title = "Lập trình Python nâng cao",
                category = "Tài liệu môn học",
                fileFormat = DocumentFileFormat.PDF,
                pageOrSlideCount = 45
            ),
            DocumentUiModel(
                id = "doc_04",
                title = "Cơ sở dữ liệu",
                category = "Chương 3 - Mô hình quan hệ",
                fileFormat = DocumentFileFormat.SLIDE,
                pageOrSlideCount = 18
            ),
            DocumentUiModel(
                id = "doc_05",
                title = "Hướng dẫn khóa luận",
                category = "Đồ án tốt nghiệp",
                fileFormat = DocumentFileFormat.WORD,
                pageOrSlideCount = 10
            ),
            DocumentUiModel(
                id = "doc_06",
                title = "Mạng máy tính",
                category = "Chương 4 - Tầng giao vận",
                fileFormat = DocumentFileFormat.SLIDE,
                pageOrSlideCount = 32
            )
        )
    }
}

// ---------------------------------------------------------------------------
// Mock data nội dung trang tài liệu — private, chỉ dùng trong Repository này
// ---------------------------------------------------------------------------
private val readerMockPages = mapOf(
    1 to ReaderPageContentUiModel(
        chapterTitle = "Mở đầu giáo trình",
        sectionTitle = "Lời nói đầu",
        bodyTextBefore = "Chào mừng các bạn sinh viên đến với môn học Nhập môn Trí tuệ Nhân tạo thế hệ mới.",
        highlightedSnippet = "Mục tiêu giáo trình là trang bị nền tảng vững chắc về xử lý ngôn ngữ tự nhiên và hệ thống RAG.",
        bodyTextAfter = "Hy vọng tài liệu này sẽ đồng hành hiệu quả cùng bạn trong suốt quá trình nghiên cứu."
    ),
    10 to ReaderPageContentUiModel(
        chapterTitle = "Chương 1: Tổng quan về AI",
        sectionTitle = "1.1 Trí tuệ Nhân tạo là gì?",
        bodyTextBefore = "Trí tuệ nhân tạo (AI) đã trải qua nhiều thập kỷ phát triển từ các hệ thống dựa trên luật lệ đơn giản.",
        highlightedSnippet = "AI hiện đại tập trung vào các mô hình học sâu có khả năng tự động học hỏi từ lượng dữ liệu khổng lồ.",
        bodyTextAfter = "Các ứng dụng phổ biến bao gồm nhận diện giọng nói, xử lý ảnh và dịch ngôn ngữ."
    ),
    11 to ReaderPageContentUiModel(
        chapterTitle = "Chương 1: Tổng quan về AI",
        sectionTitle = "1.2 Học máy và Học sâu",
        bodyTextBefore = "Học máy (Machine Learning) là tập con của AI, và Học sâu (Deep Learning) là bước tiến vượt trội nhất của nó.",
        highlightedSnippet = "Các mạng nơ-ron tích chập (CNN) và mạng nơ-ron hồi quy (RNN) đã mở đường cho thị giác máy tính và NLP.",
        bodyTextAfter = "Hiểu rõ các kiến trúc cơ bản này sẽ giúp tiếp thu các chương sau tốt hơn."
    ),
    45 to ReaderPageContentUiModel(
        chapterTitle = "Chương 3: Các kiến trúc AI tiên tiến",
        sectionTitle = "3.2 Kỹ thuật RAG (Retrieval-Augmented Generation)",
        bodyTextBefore = """
            Kỹ thuật RAG (Retrieval-Augmented Generation) là một phương pháp tiên tiến giúp nâng cao độ tin cậy của các mô hình ngôn ngữ lớn (LLM) bằng cách kết hợp khả năng sinh văn bản mạnh mẽ của chúng với các nguồn dữ liệu bên ngoài đáng tin cậy. Trong các ứng dụng thực tiễn, mặc dù LLM sở hữu lượng tri thức khổng lồ được tích lũy từ quá trình tiền huấn luyện, chúng vẫn thường xuyên gặp phải hiện tượng ảo tưởng (hallucination) - sinh ra các thông tin không chính xác nhưng nghe có vẻ rất thuyết phục.

            Để giải quyết vấn đề này, quy trình RAG bắt đầu bằng việc chuyển đổi kho tài liệu tĩnh thành các vector embeddings thông qua các mô hình nhúng (như OpenAI Ada-002 hoặc các mô hình mã nguồn mở thuộc họ Cohere/BGE). Các tài liệu này được chia thành các phân đoạn nhỏ (chunks) có kích thước cố định kèm theo một lượng từ chồng lấn (overlap) nhất định để bảo toàn ngữ cảnh liền mạch giữa các đoạn.
        """.trimIndent(),
        highlightedSnippet = "\"Cốt lõi sức mạnh của RAG nằm ở chỗ nó biến đổi LLM từ một mô hình hoạt động dựa trên trí nhớ tĩnh (Closed-book) thành một hệ thống thông minh có khả năng tra cứu nguồn tài liệu mở (Open-book).\"",
        bodyTextAfter = """
            Sau khi truy xuất được các phân đoạn văn bản liên quan, bước tiếp theo là Tăng cường ngữ cảnh (Context Augmentation). Hệ thống sẽ thiết kế một Prompt Template chuẩn hóa, kết hợp câu hỏi gốc của người dùng cùng với các phân đoạn tài liệu vừa tìm được.

            Cuối cùng là bước Sinh văn bản (Generation). Prompt đã được tăng cường sẽ được gửi tới LLM để tạo ra câu trả lời hoàn chỉnh, đính kèm các thẻ trích dẫn (citations) trỏ thẳng tới số trang hoặc tên tài liệu nguồn.
        """.trimIndent()
    ),
    46 to ReaderPageContentUiModel(
        chapterTitle = "Chương 3: Các kiến trúc AI tiên tiến",
        sectionTitle = "3.3 Đánh giá mô hình RAG",
        bodyTextBefore = "Để đánh giá hiệu quả của một hệ thống RAG, chúng ta cần đo lường 2 khía cạnh chính.",
        highlightedSnippet = "Độ chính xác của việc truy xuất (Retrieval Precision) và độ chân thực của câu trả lời sinh ra (Faithfulness).",
        bodyTextAfter = "Các khung thử nghiệm như Ragas hiện nay đang được ứng dụng rộng rãi cho mục đích này."
    ),
    80 to ReaderPageContentUiModel(
        chapterTitle = "Chương 5: Các mô hình ngôn ngữ lớn (LLMs)",
        sectionTitle = "5.1 Kiến trúc Transformer",
        bodyTextBefore = "Mô hình Transformer là bước đột phá nền tảng cho sự bùng nổ của các mô hình LLM ngày nay.",
        highlightedSnippet = "Cơ chế Self-Attention cho phép mô hình tập trung vào các mối quan hệ giữa các từ bất kể khoảng cách.",
        bodyTextAfter = "Đây là cốt lõi của các kiến trúc GPT và BERT hiện đại."
    ),
    81 to ReaderPageContentUiModel(
        chapterTitle = "Chương 5: Các mô hình ngôn ngữ lớn (LLMs)",
        sectionTitle = "5.2 Cơ chế Attention và Self-Attention",
        bodyTextBefore = "Self-Attention tính toán độ tương quan giữa mỗi từ trong câu với tất cả các từ còn lại.",
        highlightedSnippet = "Các vector Query, Key, Value được nhân ma trận để xác định trọng số chú ý cho từng ngữ cảnh cụ thể.",
        bodyTextAfter = "Nhờ tính toán song song, mô hình đạt tốc độ huấn luyện nhanh hơn vượt trội so với LSTM."
    ),
    120 to ReaderPageContentUiModel(
        chapterTitle = "Tổng kết giáo trình",
        sectionTitle = "Tài liệu tham khảo",
        bodyTextBefore = "Danh mục các tài liệu khoa học và công trình nghiên cứu được trích dẫn trong giáo trình.",
        highlightedSnippet = "Vaswani et al. (2017) 'Attention Is All You Need' - Nguồn gốc ra đời của kiến trúc Transformer.",
        bodyTextAfter = "Lewis et al. (2020) 'Retrieval-Augmented Generation for Knowledge-Intensive NLP Tasks'."
    )
)
