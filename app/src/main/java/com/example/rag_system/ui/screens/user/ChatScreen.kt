package com.example.rag_system.ui.screens.user

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
 * Màn hình Chat chính của EduRAG (lắp ghép động 100%, không chứa component tĩnh hardcoded).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    currentChatState: UiLoadState<MessageUiModel>,
    chatHistoryState: UiLoadState<List<ChatSessionUiModel>>,
    onSendMessage: (String) -> Unit,
    onBackClick: () -> Unit,
    onSourceClick: (SourceCitationUiModel) -> Unit,
    modifier: Modifier = Modifier
) {
    var inputText by remember { mutableStateOf("") }
    var lastSentQuery by remember { mutableStateOf<String?>(null) }
    var attachedFile by remember { mutableStateOf<String?>("Giáo trình_AI_Preview.png") }
    var currentTab by remember { mutableStateOf("chat") }

    val listState = rememberLazyListState()
    val context = LocalContext.current

    // Tự động cuộn xuống dưới cùng khi có tin nhắn mới
    LaunchedEffect(currentChatState, lastSentQuery) {
        if (lastSentQuery != null || currentChatState !is UiLoadState.Idle) {
            listState.animateScrollToItem(index = Int.MAX_VALUE)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(start = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(BrandPrimary.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🎓", fontSize = 16.sp)
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
                navigationIcon = {},
                actions = {
                    Surface(
                        shape = CircleShape,
                        color = BrandSurfaceContainerLow,
                        border = BorderStroke(1.5.dp, BrandPrimary),
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .size(36.dp)
                            .clickable {
                                Toast.makeText(context, "Mở hồ sơ cá nhân", Toast.LENGTH_SHORT).show()
                            }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("A", fontWeight = FontWeight.Bold, color = BrandPrimary, fontSize = 14.sp)
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = BrandSurface
                ),
                modifier = Modifier.border(1.dp, BrandBorderSubtle)
            )
        },
        bottomBar = {
            Column {
                ChatInputBar(
                    inputText = inputText,
                    attachedFile = attachedFile,
                    onInputTextChanged = { inputText = it },
                    onRemoveAttachedFile = { attachedFile = null },
                    onSendClick = {
                        val query = inputText.trim()
                        if (query.isNotEmpty()) {
                            lastSentQuery = query
                            onSendMessage(query)
                            inputText = ""
                        }
                    }
                )

                // Gọi Bottom Navigation Bar từ file components riêng biệt
                EduRAGBottomNavBar(
                    currentTab = currentTab,
                    onTabSelected = {
                        currentTab = it
                        Toast.makeText(context, "Chuyển sang tab: $it", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        },
        modifier = modifier.fillMaxSize()
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
                            onSourceClick = onSourceClick
                        )
                    }
                }
            }

            // 3. Tin nhắn tiếp theo từ User đang gửi mới
            if (lastSentQuery != null) {
                item {
                    EduUserMessageBubble(content = lastSentQuery!!)
                }
            }

            // 4. Trạng thái tải của tin nhắn mới
            when (currentChatState) {
                is UiLoadState.Loading -> {
                    item {
                        BotLoadingBubble()
                    }
                }
                is UiLoadState.Success -> {
                    item {
                        val response = currentChatState.data
                        EduAiDetailedResponse(
                            content = response.content,
                            citations = response.citations,
                            onSourceClick = onSourceClick
                        )
                    }
                }
                is UiLoadState.Error -> {
                    item {
                        BotErrorCard(
                            errorMessage = currentChatState.message,
                            onRetry = {
                                if (lastSentQuery != null) {
                                    onSendMessage(lastSentQuery!!)
                                }
                            }
                        )
                    }
                }
                else -> {}
            }
        }
    }
}

// Previews
@Preview(showBackground = true)
@Composable
fun EduRAGPreview() {
    RAG_SystemTheme {
        ChatScreen(
            currentChatState = UiLoadState.Idle,
            chatHistoryState = UiLoadState.Idle,
            onSendMessage = {},
            onBackClick = {},
            onSourceClick = {}
        )
    }
}
