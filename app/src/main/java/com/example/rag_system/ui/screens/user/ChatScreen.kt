package com.example.rag_system.ui.screens.user

import android.net.Uri
import android.provider.OpenableColumns
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rag_system.ui.components.*
import com.example.rag_system.ui.models.ChatSessionUiModel
import com.example.rag_system.ui.models.MessageUiModel
import com.example.rag_system.ui.models.SourceCitationUiModel
import com.example.rag_system.ui.state.UiLoadState
import com.example.rag_system.ui.theme.*

// Lớp dữ liệu giả lập cho nội dung từng trang tài liệu học tập
data class ReaderMockPageContent(
    val chapterTitle: String,
    val sectionTitle: String,
    val bodyTextBefore: String,
    val highlightedSnippet: String,
    val bodyTextAfter: String
)

private val readerMockPagesData = mapOf(
    1 to ReaderMockPageContent(
        chapterTitle = "Mở đầu giáo trình",
        sectionTitle = "Lời nói đầu",
        bodyTextBefore = "Chào mừng các bạn sinh viên đến với môn học Nhập môn Trí tuệ Nhân tạo thế hệ mới.",
        highlightedSnippet = "Mục tiêu giáo trình là trang bị nền tảng vững chắc về xử lý ngôn ngữ tự nhiên và hệ thống RAG.",
        bodyTextAfter = "Hy vọng tài liệu này sẽ đồng hành hiệu quả cùng bạn trong suốt quá trình nghiên cứu."
    ),
    10 to ReaderMockPageContent(
        chapterTitle = "Chương 1: Tổng quan về AI",
        sectionTitle = "1.1 Trí tuệ Nhân tạo là gì?",
        bodyTextBefore = "Trí tuệ nhân tạo (AI) đã trải qua nhiều thập kỷ phát triển từ các hệ thống dựa trên luật lệ đơn giản.",
        highlightedSnippet = "AI hiện đại tập trung vào các mô hình học sâu có khả năng tự động học hỏi từ lượng dữ liệu khổng lồ.",
        bodyTextAfter = "Các ứng dụng phổ biến bao gồm nhận diện giọng nói, xử lý ảnh và dịch ngôn ngữ."
    ),
    11 to ReaderMockPageContent(
        chapterTitle = "Chương 1: Tổng quan về AI",
        sectionTitle = "1.2 Học máy và Học sâu",
        bodyTextBefore = "Học máy (Machine Learning) là tập con của AI, và Học sâu (Deep Learning) là bước tiến vượt trội nhất của nó.",
        highlightedSnippet = "Các mạng nơ-ron tích chập (CNN) và mạng nơ-ron hồi quy (RNN) đã mở đường cho thị giác máy tính và NLP.",
        bodyTextAfter = "Hiểu rõ các kiến trúc cơ bản này sẽ giúp tiếp thu các chương sau tốt hơn."
    ),
    45 to ReaderMockPageContent(
        chapterTitle = "Chương 3: Các kiến trúc AI tiên tiến",
        sectionTitle = "3.2 Kỹ thuật RAG (Retrieval-Augmented Generation)",
        bodyTextBefore = """
            Kỹ thuật RAG (Retrieval-Augmented Generation) là một phương pháp tiên tiến giúp nâng cao độ tin cậy của các mô hình ngôn ngữ lớn (LLM) bằng cách kết hợp khả năng sinh văn bản mạnh mẽ của chúng với các nguồn dữ liệu bên ngoài đáng tin cậy. Trong các ứng dụng thực tiễn, mặc dù LLM sở hữu lượng tri thức khổng lồ được tích lũy từ quá trình tiền huấn luyện, chúng vẫn thường xuyên gặp phải hiện tượng ảo tưởng (hallucination) - sinh ra các thông tin không chính xác nhưng nghe có vẻ rất thuyết phục. Điều này đặc biệt nguy hiểm trong các lĩnh vực yêu cầu tính chính xác cao như y tế, luật pháp, tài chính và đào tạo đại học.
            
            Để giải quyết vấn đề này, quy trình RAG bắt đầu bằng việc chuyển đổi kho tài liệu tĩnh thành các vector embeddings thông qua các mô hình nhúng (như OpenAI Ada-002 hoặc các mô hình mã nguồn mở thuộc họ Cohere/BGE). Các tài liệu này được chia thành các phân đoạn nhỏ (chunks) có kích thước cố định (ví dụ: 512 tokens) kèm theo một lượng từ chồng lấn (overlap) nhất định (ví dụ: 50 tokens) để bảo toàn ngữ cảnh liền mạch giữa các đoạn. Sau đó, toàn bộ các vector phân đoạn này sẽ được lập chỉ mục và lưu trữ có hệ thống bên trong một cơ sở dữ liệu vector chuyên dụng (Vector Database) như Milvus, Pinecone, Qdrant hoặc FAISS.
            
            Khi người dùng gửi một câu hỏi (query) đến hệ thống, câu hỏi đó cũng lập tức được chuyển đổi thành vector nhúng bằng chính mô hình nhúng đã sử dụng ở bước lập chỉ mục tài liệu. Hệ thống sau đó thực hiện thuật toán tìm kiếm lân cận gần nhất (k-Nearest Neighbors - kNN) hoặc tính toán độ tương đồng Cosine (Cosine Similarity) để tìm ra top-K phân đoạn tài liệu có độ tương quan ngữ nghĩa cao nhất với câu hỏi của người dùng.
        """.trimIndent(),
        highlightedSnippet = """
            "Cốt lõi sức mạnh của RAG nằm ở chỗ nó biến đổi LLM từ một mô hình hoạt động dựa trên trí nhớ tĩnh (Closed-book) thành một hệ thống thông minh có khả năng tra cứu nguồn tài liệu mở (Open-book). Bằng cách chèn trực tiếp các phân đoạn văn bản trích dẫn chính xác vào cửa sổ ngữ cảnh (Context Window) của Prompt đầu vào, LLM được cung cấp đầy đủ chứng cứ dữ liệu thực tế để suy luận và đưa ra câu trả lời chính xác, trực quan, có trích dẫn nguồn rõ ràng."
        """.trimIndent(),
        bodyTextAfter = """
            Sau khi truy xuất được các phân đoạn văn bản liên quan, bước tiếp theo là Tăng cường ngữ cảnh (Context Augmentation). Hệ thống sẽ thiết kế một Prompt Template chuẩn hóa, kết hợp câu hỏi gốc của người dùng cùng với các phân đoạn tài liệu vừa tìm được, kèm theo chỉ thị nghiêm ngặt: "Chỉ được phép trả lời dựa trên thông tin đã cung cấp. Nếu thông tin không có trong tài liệu, hãy thành thật trả lời là tôi không biết." Điều này hạn chế tối đa khả năng suy diễn tự do của LLM.
            
            Cuối cùng là bước Sinh văn bản (Generation). Prompt đã được tăng cường sẽ được gửi tới LLM (ví dụ: GPT-4, Gemini 1.5 Pro hoặc Llama 3) để tạo ra câu trả lời hoàn chỉnh. Câu trả lời lúc này không chỉ chuẩn xác về mặt học thuật mà còn đính kèm các thẻ trích dẫn (citations) trỏ thẳng tới số trang hoặc tên tài liệu nguồn. Nhờ cơ chế này, sinh viên và nghiên cứu sinh có thể dễ dàng bấm vào thẻ nguồn để đối chiếu trực tiếp tài liệu gốc ngay trên ứng dụng, đảm bảo tính khách quan và minh bạch trong học tập.
            
            Mặc dù RAG mang lại nhiều lợi ích lớn, việc vận hành hệ thống này cũng đòi hỏi tối ưu nhiều tham số kỹ thuật như: chiến lược phân mảnh tài liệu (chunking strategy), thuật toán xếp hạng lại (reranking) bằng các mô hình Cross-Encoder để tinh lọc dữ liệu trước khi gửi vào LLM, cũng như các kỹ thuật tối ưu hóa Prompts để LLM khai thác tối đa cửa sổ ngữ cảnh mà không bị quá tải bộ nhớ.
        """.trimIndent()
    ),
    46 to ReaderMockPageContent(
        chapterTitle = "Chương 3: Các kiến trúc AI tiên tiến",
        sectionTitle = "3.3 Đánh giá mô hình RAG",
        bodyTextBefore = "Để đánh giá hiệu quả của một hệ thống RAG, chúng ta cần đo lường 2 khía cạnh chính.",
        highlightedSnippet = "Độ chính xác của việc truy xuất (Retrieval Precision) và độ chân thực của câu trả lời sinh ra (Faithfulness).",
        bodyTextAfter = "Các khung thử nghiệm như Ragas hiện nay đang được ứng dụng rộng rãi cho mục đích này."
    ),
    80 to ReaderMockPageContent(
        chapterTitle = "Chương 5: Các mô hình ngôn ngữ lớn (LLMs)",
        sectionTitle = "5.1 Kiến trúc Transformer",
        bodyTextBefore = "Mô hình Transformer là bước đột phá nền tảng cho sự bùng nổ của các mô hình LLM ngày nay.",
        highlightedSnippet = "Cơ chế Self-Attention cho phép mô hình tập trung vào các mối quan hệ giữa các từ bất kể khoảng cách.",
        bodyTextAfter = "Đây là cốt lõi của các kiến trúc GPT và BERT hiện đại."
    ),
    81 to ReaderMockPageContent(
        chapterTitle = "Chương 5: Các mô hình ngôn ngữ lớn (LLMs)",
        sectionTitle = "5.2 Cơ chế Attention và Self-Attention",
        bodyTextBefore = "Self-Attention tính toán độ tương quan giữa mỗi từ trong câu với tất cả các từ còn lại.",
        highlightedSnippet = "Các vector Query, Key, Value được nhân ma trận để xác định trọng số chú ý cho từng ngữ cảnh cụ thể.",
        bodyTextAfter = "Nhờ tính toán song song, mô hình đạt tốc độ huấn luyện nhanh hơn vượt trội so với LSTM."
    ),
    120 to ReaderMockPageContent(
        chapterTitle = "Tổng kết giáo trình",
        sectionTitle = "Tài liệu tham khảo",
        bodyTextBefore = "Danh mục các tài liệu khoa học và công trình nghiên cứu được trích dẫn trong giáo trình.",
        highlightedSnippet = "Vaswani et al. (2017) 'Attention Is All You Need' - Nguồn gốc ra đời của kiến trúc Transformer.",
        bodyTextAfter = "Lewis et al. (2020) 'Retrieval-Augmented Generation for Knowledge-Intensive NLP Tasks'."
    )
)

private fun getReaderPageContent(page: Int): ReaderMockPageContent {
    return readerMockPagesData[page] ?: ReaderMockPageContent(
        chapterTitle = "Giáo trình Nhập môn AI",
        sectionTitle = "Nội dung trang $page",
        bodyTextBefore = "Bạn đang đọc nội dung học tập của giáo trình Nhập môn AI tại trang số $page.",
        highlightedSnippet = "Hệ thống RAG hỗ trợ sinh viên nhanh chóng đối chiếu kiến thức trực tiếp từ trang $page.",
        bodyTextAfter = "Tiếp tục lật trang hoặc kéo thanh trượt để xem các nội dung kiến thức khác."
    )
}

/**
 * Trích xuất tên hiển thị thực tế của tệp tin từ Uri hệ thống
 */
private fun getFileNameFromUri(context: Context, uri: Uri): String? {
    var name: String? = null
    if (uri.scheme == "content") {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val index = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (index != -1) {
                    name = it.getString(index)
                }
            }
        }
    }
    if (name == null) {
        name = uri.path
        val cut = name?.lastIndexOf('/')
        if (cut != null && cut != -1) {
            name = name.substring(cut + 1)
        }
    }
    return name
}

/**
 * Màn hình Chat chính của EduRAG (lắp ghép động 100%, không chứa component tĩnh hardcoded).
 * Tích hợp BottomSheet đọc giáo trình trực tiếp đè lên nền chat thật được làm mờ.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    currentChatState: UiLoadState<MessageUiModel>,
    chatHistoryState: UiLoadState<List<ChatSessionUiModel>>,
    onSendMessage: (String) -> Unit,
    onBackClick: () -> Unit,
    onSourceClick: (SourceCitationUiModel) -> Unit,
    onTabSelected: (String) -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var inputText by rememberSaveable { mutableStateOf("") }

    // Quản lý danh sách nhiều tệp đính kèm bằng mutableStateListOf
    val attachedFiles = remember { mutableStateListOf<String>() }
    val attachedFileUris = remember { mutableStateListOf<Uri>() }
    var showAppInfo by rememberSaveable { mutableStateOf(false) }

    // Danh sách toàn bộ tin nhắn (User và AI) trong phiên chat hiện tại
    val activeMessages = remember { mutableStateListOf<MessageUiModel>() }

    // Tự động nhận diện và cập nhật tin nhắn AI mới vào danh sách khi có Success state
    LaunchedEffect(currentChatState) {
        if (currentChatState is UiLoadState.Success) {
            val aiResponse = currentChatState.data
            if (activeMessages.none { it.id == aiResponse.id }) {
                activeMessages.add(aiResponse)
            }
        }
    }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            uris.forEach { uri ->
                val fileName = getFileNameFromUri(context, uri) ?: "Tài liệu_Đính_Kèm.pdf"
                if (!attachedFileUris.contains(uri)) {
                    attachedFiles.add(fileName)
                    attachedFileUris.add(uri)
                }
            }
            Toast.makeText(context, "Đã thêm ${uris.size} tệp đính kèm", Toast.LENGTH_SHORT).show()
        }
    }

    // Trạng thái của Bottom Sheet xem tài liệu trực tiếp
    var activeDocumentCitation by rememberSaveable { mutableStateOf<SourceCitationUiModel?>(null) }
    var readerCurrentPage by rememberSaveable { mutableStateOf(45) }
    var bookmarkedPages by rememberSaveable { mutableStateOf(listOf<Int>()) }

    val listState = rememberLazyListState()

    // Tự động cuộn xuống dưới cùng khi có tin nhắn mới hoặc khi hệ thống đang load câu trả lời
    LaunchedEffect(activeMessages.size, currentChatState) {
        if (activeMessages.isNotEmpty() || currentChatState is UiLoadState.Loading) {
            listState.animateScrollToItem(index = Int.MAX_VALUE)
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        // --- 1. Giao diện Chat chính (sẽ bị mờ nếu activeDocumentCitation != null) ---
        Scaffold(
            topBar = {
                EduRAGTopAppBar(
                    navigationContent = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(BrandPrimary.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("🎓", fontSize = 18.sp)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "EduRAG",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = BrandPrimary
                                )
                            )
                        }
                    },
                    actionContent = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Nút thông tin ℹ️ mở AppInfoBottomSheet
                            Surface(
                                shape = CircleShape,
                                color = BrandSurfaceContainerLow,
                                border = BorderStroke(1.dp, BrandBorderSubtle),
                                modifier = Modifier
                                    .size(32.dp)
                                    .clickable {
                                        showAppInfo = true
                                    }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("ℹ️", fontSize = 14.sp)
                                }
                            }

                            // Avatar người dùng
                            Surface(
                                shape = CircleShape,
                                color = BrandSurfaceContainerLow,
                                border = BorderStroke(1.5.dp, BrandPrimary),
                                modifier = Modifier
                                    .padding(end = 12.dp)
                                    .size(36.dp)
                                    .clickable {
                                        onProfileClick()
                                    }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("A", fontWeight = FontWeight.Bold, color = BrandPrimary, fontSize = 14.sp)
                                }
                            }
                        }
                    }
                )
            },
            bottomBar = {
                Column {
                    ChatInputBar(
                        inputText = inputText,
                        attachedFileNames = attachedFiles,
                        attachedFileUris = attachedFileUris,
                        onInputTextChanged = { inputText = it },
                        onRemoveAttachedFile = { index ->
                            if (index in attachedFiles.indices) {
                                attachedFiles.removeAt(index)
                                attachedFileUris.removeAt(index)
                            }
                        },
                        onSendClick = {
                            val query = inputText.trim()
                            if (query.isNotEmpty() || attachedFiles.isNotEmpty()) {
                                // Nếu chỉ gửi file mà không có text, tạo query fallback mô tả số lượng file
                                val fileCount = attachedFiles.size
                                val effectiveQuery = if (query.isNotEmpty()) query
                                    else if (fileCount == 1) "Tôi vừa đính kèm 1 tệp: ${attachedFiles[0]}. Hãy phân tích giúp tôi."
                                    else "Tôi vừa đính kèm $fileCount tệp. Hãy phân tích giúp tôi."

                                // 1. Tạo tin nhắn người dùng và thêm trực tiếp vào danh sách hiển thị
                                val userMsg = MessageUiModel(
                                    id = "user_${System.currentTimeMillis()}",
                                    content = effectiveQuery,
                                    isFromUser = true,
                                    sendTime = "Vừa xong",
                                    attachedFileNames = attachedFiles.toList(),
                                    attachedFileUris = attachedFileUris.toList()
                                )
                                activeMessages.add(userMsg)

                                // 2. Kích hoạt sự kiện gửi lên ViewModel với query hiệu quả
                                onSendMessage(effectiveQuery)

                                // 3. Reset thanh nhập liệu
                                inputText = ""
                                attachedFiles.clear()
                                attachedFileUris.clear()
                            }
                        },
                        onAttachClick = {
                            filePickerLauncher.launch("*/*")
                        }
                    )

                    // Gọi Bottom Navigation Bar từ file components riêng biệt
                    EduRAGBottomNavBar(
                        currentTab = "chat",
                        onTabSelected = {
                            onTabSelected(it)
                        }
                    )
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .blur(if (activeDocumentCitation != null) 4.dp else 0.dp) // Làm mờ khi mở tài liệu
        ) { innerPadding ->
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(BrandSurface)
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // 1. AI Welcome Message
                item {
                    EduAiWelcomeMessage()
                }

                // 2. Duyệt danh sách hội thoại cũ một cách động hoàn toàn từ chatHistoryState
                if (chatHistoryState is UiLoadState.Success) {
                    chatHistoryState.data.forEach { session ->
                        // Bong bóng tin nhắn động từ người dùng hỏi về chủ đề phiên
                        item {
                            EduUserMessageBubble(content = "Cho tôi hỏi về môn ${session.subjectLabel ?: "học"}: ${session.title}")
                        }
                        // Bong bóng tin nhắn AI phản hồi động hoàn toàn (tự động vẽ pins và citation cards nếu có)
                        item {
                            EduAiDetailedResponse(
                                content = session.lastMessagePreview,
                                citations = session.citations,
                                onSourceClick = { citation ->
                                    // Gán tài liệu active để mở ngay tại màn hình này
                                    activeDocumentCitation = citation
                                    readerCurrentPage = citation.pageNumber ?: 45
                                    onSourceClick(citation)
                                }
                            )
                        }
                    }
                }

                // 3. Danh sách toàn bộ tin nhắn (User và AI) trong phiên chat hiện tại
                items(activeMessages) { message ->
                    if (message.isFromUser) {
                        EduUserMessageBubble(
                            content = message.content,
                            attachedFileNames = message.attachedFileNames,
                            attachedFileUris = message.attachedFileUris
                        )
                    } else {
                        EduAiDetailedResponse(
                            content = message.content,
                            citations = message.citations,
                            onSourceClick = { citation ->
                                activeDocumentCitation = citation
                                readerCurrentPage = citation.pageNumber ?: 45
                                onSourceClick(citation)
                            }
                        )
                    }
                }

                // 4. Hiển thị loading bubble khi đang chờ AI phản hồi
                if (currentChatState is UiLoadState.Loading) {
                    item { BotLoadingBubble() }
                }

                // 5. Hiển thị lỗi nếu request thất bại, cho phép thử lại
                if (currentChatState is UiLoadState.Error) {
                    item {
                        BotErrorCard(
                            errorMessage = currentChatState.message,
                            onRetry = {
                                val lastUserMsg = activeMessages.lastOrNull { it.isFromUser }?.content ?: ""
                                if (lastUserMsg.isNotEmpty()) onSendMessage(lastUserMsg)
                            }
                        )
                    }
                }
            }
        }

        // --- 2. Lớp phủ tối mờ Backdrop và Bottom Sheet Xem tài liệu (Khớp 100% nền thật) ---
        if (activeDocumentCitation != null) {
            // Lớp phủ đen trong suốt (Backdrop)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable { activeDocumentCitation = null }
            )

            // Bottom Sheet tài liệu trượt lên (Cao 85vh)
            Surface(
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                color = BrandSurface,
                shadowElevation = 16.dp,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .fillMaxHeight(0.85f)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Thanh kéo Drag Handle
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(vertical = 12.dp)
                            .width(48.dp)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .background(BrandOutlineVariant)
                    )

                    // Gọi trực tiếp EduRAGTopAppBar tắt status bar padding
                    EduRAGTopAppBar(
                        applyStatusBarPadding = false,
                        navigationContent = {
                            Column {
                                Text(
                                    text = activeDocumentCitation!!.sourceDocumentName,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = BrandTextPrimary,
                                    maxLines = 1
                                )
                                Text(
                                    text = "Trang $readerCurrentPage • Kiến thức cơ bản",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = BrandTextSecondary
                                )
                            }
                        },
                        actionContent = {
                            IconButton(
                                onClick = { activeDocumentCitation = null },
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(BrandSurfaceContainerLow)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Đóng",
                                    tint = BrandOnSurfaceVariant,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    )

                    // Lấy nội dung trang động theo số trang hiện tại
                    val pageData = getReaderPageContent(readerCurrentPage)

                    // Nội dung đọc tài liệu dạng scrollable
                    DocumentContentArea(
                        chapterTitle = pageData.chapterTitle,
                        sectionTitle = pageData.sectionTitle,
                        bodyTextBefore = pageData.bodyTextBefore,
                        highlightedSnippet = pageData.highlightedSnippet,
                        bodyTextAfter = pageData.bodyTextAfter,
                        modifier = Modifier.weight(1f)
                    )

                    // Thanh điều khiển trang và lưu dấu trang (Bookmark) trang cụ thể
                    DocumentReaderControls(
                        currentPage = readerCurrentPage,
                        totalPages = 120,
                        isBookmarked = readerCurrentPage in bookmarkedPages,
                        onPageChanged = { page ->
                            readerCurrentPage = page
                        },
                        onBookmarkToggled = {
                            bookmarkedPages = if (readerCurrentPage in bookmarkedPages) {
                                bookmarkedPages - readerCurrentPage
                            } else {
                                bookmarkedPages + readerCurrentPage
                            }
                            Toast.makeText(
                                context,
                                if (readerCurrentPage in bookmarkedPages) "Đã lưu dấu trang $readerCurrentPage!" else "Đã bỏ lưu dấu trang $readerCurrentPage!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )
                }
            }
        }

        // BottomSheet giới thiệu trường/app và đánh giá
        if (showAppInfo) {
            AppInfoBottomSheet(
                onDismissRequest = { showAppInfo = false },
                onSendFeedback = { rating, comment ->
                    Toast.makeText(context, "Cảm ơn bạn đã đánh giá $rating sao cho EduRAG!", Toast.LENGTH_LONG).show()
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EduRAGPreview() {
    RAG_SystemTheme {
        ChatScreen(
            currentChatState = UiLoadState.Idle,
            chatHistoryState = UiLoadState.Idle,
            onSendMessage = {},
            onBackClick = {},
            onSourceClick = {},
            onTabSelected = {},
            onProfileClick = {}
        )
    }
}
