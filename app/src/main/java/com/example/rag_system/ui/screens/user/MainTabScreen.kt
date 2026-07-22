package com.example.rag_system.ui.screens.user

import android.widget.Toast
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.rag_system.ui.viewmodels.ChatViewModel
import com.example.rag_system.ui.viewmodels.DocumentViewModel

/**
 * Màn hình chứa Tab chính (MainTabScreen) quản lý việc chuyển đổi giữa Chat, Lịch sử và Thư viện.
 * Tích hợp đầy đủ [ChatViewModel] và [DocumentViewModel] theo chuẩn MVVM Stateless UI.
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
    val sessionMessagesState by chatViewModel.sessionMessagesState.collectAsState()
    val draftInputText = chatViewModel.draftInputText

    val documentViewModel = remember { DocumentViewModel() }
    val libraryState by documentViewModel.libraryState.collectAsState()

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
                    sessionMessagesState = sessionMessagesState,
                    inputText = draftInputText,
                    onInputTextChanged = { chatViewModel.updateDraftInput(it) },
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
                        val sessionId = session.id.toLongOrNull() ?: 0L
                        if (sessionId > 0L) {
                            chatViewModel.loadSessionMessages(sessionId)
                        }
                        currentTab = "chat"
                    },
                    onProfileClick = onProfileClick,
                    modifier = Modifier.fillMaxSize()
                )
            }
            "documents" -> {
                LibraryScreen(
                    libraryState = libraryState,
                    onReloadLibrary = {
                        documentViewModel.loadLibraryDocuments()
                    },
                    onDocumentClick = onDocumentClick,
                    onTabSelected = { selectedTab ->
                        currentTab = selectedTab
                    },
                    onProfileClick = onProfileClick,
                    modifier = Modifier.fillMaxSize()
                )
            }
            else -> {
                currentTab = "chat"
            }
        }
    }
}
