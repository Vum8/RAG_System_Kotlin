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
            name = name!!.substring(cut + 1)
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
    inputText: String,
    onInputTextChanged: (String) -> Unit,
    attachedFileNames: List<String>,
    attachedFileUris: List<Uri>,
    onAddAttachment: (String, Uri) -> Unit,
    onRemoveAttachment: (Int) -> Unit,
    onClearAttachments: () -> Unit,
    onSendMessage: (String) -> Unit,
    onBackClick: () -> Unit,
    onSourceClick: (SourceCitationUiModel) -> Unit,
    onTabSelected: (String) -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
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
                val fileName = getFileNameFromUri(context, uri) ?: "Tài_liệu_Đính_Kèm.pdf"
                onAddAttachment(fileName, uri)
            }
            Toast.makeText(context, "Đã thêm ${uris.size} tệp đính kèm", Toast.LENGTH_SHORT).show()
        }
    }

    // Trạng thái Bottom Sheet xem tài liệu — citation active khi người dùng bấm vào nguồn
    var activeDocumentCitation by rememberSaveable { mutableStateOf<SourceCitationUiModel?>(null) }

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
                        attachedFileNames = attachedFileNames,
                        attachedFileUris = attachedFileUris,
                        onInputTextChanged = onInputTextChanged,
                        onRemoveAttachedFile = { index -> onRemoveAttachment(index) },
                        onSendClick = {
                            val query = inputText.trim()
                            if (query.isNotEmpty() || attachedFileNames.isNotEmpty()) {
                                val fileCount = attachedFileNames.size
                                val effectiveQuery = if (query.isNotEmpty()) query
                                    else if (fileCount == 1) "Tôi vừa đính kèm 1 tệp: ${attachedFileNames[0]}. Hãy phân tích giúp tôi."
                                    else "Tôi vừa đính kèm $fileCount tệp. Hãy phân tích giúp tôi."

                                val userMsg = MessageUiModel(
                                    id = "user_${System.currentTimeMillis()}",
                                    content = effectiveQuery,
                                    isFromUser = true,
                                    sendTime = "Vừa xong",
                                    attachedFileNames = attachedFileNames.toList(),
                                    attachedFileUris = attachedFileUris.toList()
                                )
                                activeMessages.add(userMsg)
                                onSendMessage(effectiveQuery)

                                // Reset thanh nhập liệu
                                onInputTextChanged("")
                                onClearAttachments()
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

        // --- 2. Lớp phủ tối mờ Backdrop và Bottom Sheet Xem tài liệu ---
        if (activeDocumentCitation != null) {
            DocumentReaderBottomSheet(
                citation = activeDocumentCitation!!,
                onDismiss = { activeDocumentCitation = null }
            )
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
            inputText = "",
            onInputTextChanged = {},
            attachedFileNames = emptyList(),
            attachedFileUris = emptyList(),
            onAddAttachment = { _, _ -> },
            onRemoveAttachment = {},
            onClearAttachments = {},
            onSendMessage = {},
            onBackClick = {},
            onSourceClick = {},
            onTabSelected = {},
            onProfileClick = {}
        )
    }
}
