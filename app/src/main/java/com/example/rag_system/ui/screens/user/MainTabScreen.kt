package com.example.rag_system.ui.screens.user

import android.widget.Toast
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.rag_system.ui.models.SourceCitationUiModel
import com.example.rag_system.ui.state.UiLoadState
import com.example.rag_system.ui.viewmodels.ChatViewModel

/**
 * Màn hình chứa Tab chính (MainTabScreen) quản lý việc chuyển đổi giữa Chat và Thư viện.
 * Sử dụng Crossfade để chuyển đổi tức thời và mượt mà, không bị lag vẽ giao diện.
 */
@Composable
fun MainTabScreen(
    chatViewModel: ChatViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var currentTab by rememberSaveable { mutableStateOf("chat") }

    val currentChatState by chatViewModel.currentChatState.collectAsState()
    val chatHistoryState by chatViewModel.chatHistoryState.collectAsState()

    Crossfade(
        targetState = currentTab,
        label = "TabCrossfade",
        modifier = modifier.fillMaxSize()
    ) { tab ->
        when (tab) {
            "chat" -> {
                ChatScreen(
                    currentChatState = currentChatState,
                    chatHistoryState = chatHistoryState,
                    onSendMessage = { query ->
                        chatViewModel.sendChatQuery(query)
                    },
                    onBackClick = onBackClick,
                    onSourceClick = {
                        // Trình đọc overlay tích hợp trực tiếp trong ChatScreen
                    },
                    onTabSelected = { selectedTab ->
                        currentTab = selectedTab
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
            "documents" -> {
                LibraryScreen(
                    onDocumentClick = { documentId ->
                        Toast.makeText(context, "Mở tài liệu: $documentId", Toast.LENGTH_SHORT).show()
                    },
                    onTabSelected = { selectedTab ->
                        currentTab = selectedTab
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
            else -> {
                // Tab Lịch sử hoặc các tab khác tạm thời hiển thị Toast hoặc fallback về Chat
                Toast.makeText(context, "Tính năng đang được phát triển!", Toast.LENGTH_SHORT).show()
                currentTab = "chat"
            }
        }
    }
}
