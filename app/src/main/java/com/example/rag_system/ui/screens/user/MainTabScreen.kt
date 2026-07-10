package com.example.rag_system.ui.screens.user

import android.widget.Toast
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.rag_system.ui.viewmodels.ChatViewModel

/**
 * Màn hình chứa Tab chính (MainTabScreen) quản lý việc chuyển đổi giữa Chat, Lịch sử và Thư viện.
 * Sử dụng Crossfade để chuyển đổi tức thời và mượt mà, không bị lag vẽ giao diện.
 */
@Composable
fun MainTabScreen(
    chatViewModel: ChatViewModel,
    onBackClick: () -> Unit,
    onProfileClick: () -> Unit,
    onDocumentClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var currentTab by rememberSaveable { mutableStateOf("chat") }

    val currentChatState by chatViewModel.currentChatState.collectAsState()
    val chatHistoryState by chatViewModel.chatHistoryState.collectAsState()
    val draftInputText = chatViewModel.draftInputText

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
                    inputText = draftInputText,
                    onInputTextChanged = { chatViewModel.updateDraftInput(it) },
                    attachedFileNames = chatViewModel.draftAttachedFileNames,
                    attachedFileUris = chatViewModel.draftAttachedFileUris,
                    onAddAttachment = { name, uri -> chatViewModel.addAttachment(name, uri) },
                    onRemoveAttachment = { index -> chatViewModel.removeAttachment(index) },
                    onClearAttachments = { chatViewModel.clearAttachments() },
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
                    onProfileClick = onProfileClick,
                    modifier = Modifier.fillMaxSize()
                )
            }
            "history" -> {
                HistoryScreen(
                    chatHistoryState = chatHistoryState,
                    onTabSelected = { selectedTab ->
                        currentTab = selectedTab
                    },
                    onSessionClick = { session ->
                        Toast.makeText(context, "Mở phiên: ${session.title}", Toast.LENGTH_SHORT).show()
                        currentTab = "chat"
                    },
                    onProfileClick = onProfileClick,
                    modifier = Modifier.fillMaxSize()
                )
            }
            "documents" -> {
                LibraryScreen(
                    onDocumentClick = onDocumentClick,
                    onTabSelected = { selectedTab ->
                        currentTab = selectedTab
                    },
                    onProfileClick = onProfileClick,
                    modifier = Modifier.fillMaxSize()
                )
            }
            else -> {
                Toast.makeText(context, "Tính năng đang được phát triển!", Toast.LENGTH_SHORT).show()
                currentTab = "chat"
            }
        }
    }
}

