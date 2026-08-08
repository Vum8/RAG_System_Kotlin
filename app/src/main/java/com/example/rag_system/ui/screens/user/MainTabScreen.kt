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
import com.example.rag_system.ui.state.UiLoadState

/**
 * Màn hình chứa Tab chính (MainTabScreen) quản lý việc chuyển đổi giữa Chat, Lịch sử và Thư viện.
 * Tích hợp đầy đủ [ChatViewModel] và [DocumentViewModel] theo chuẩn MVVM Stateless UI.
 */
@Composable
fun MainTabScreen(
    chatViewModel: ChatViewModel,
    documentViewModel: DocumentViewModel,
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
                    onNewChatClick = {
                        chatViewModel.startNewSession()
                    },
                    onBackClick = onBackClick,
                    onSourceClick = {
                        // Trình đọc overlay tích hợp trực tiếp trong ChatScreen
                    },
                    onTabSelected = { selectedTab ->
                        currentTab = selectedTab
                    },
                    onProfileClick = onProfileClick,
                    onLoadMoreMessages = {
                        val sessionId = chatViewModel.currentSessionId
                        if (sessionId != null && sessionId > 0L) {
                            chatViewModel.loadSessionMessages(sessionId, isLoadMore = true)
                        }
                    },
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
                    onNewChatClick = {
                        chatViewModel.startNewSession()
                    },
                    onProfileClick = onProfileClick,
                    onDeleteSession = { sessionId, callback ->
                        val id = sessionId.toLongOrNull() ?: 0L
                        if (id > 0L) {
                            chatViewModel.deleteSession(id) { success ->
                                callback(success)
                            }
                        } else {
                            callback(false)
                        }
                    },
                    onDeleteAll = { callback ->
                        val historyState = chatHistoryState
                        if (historyState is UiLoadState.Success) {
                            val sessionIds = historyState.data.mapNotNull { it.id.toLongOrNull() }
                            if (sessionIds.isEmpty()) {
                                callback(true)
                                return@HistoryScreen
                            }
                            var completed = 0
                            var overallSuccess = true
                            sessionIds.forEach { id ->
                                chatViewModel.deleteSession(id) { success ->
                                    completed++
                                    if (!success) overallSuccess = false
                                    if (completed == sessionIds.size) {
                                        callback(overallSuccess)
                                    }
                                }
                            }
                        } else {
                            callback(true)
                        }
                    },
                    onLoadMore = {
                        chatViewModel.loadChatHistory(isLoadMore = true)
                    },
                    onRefresh = {
                        chatViewModel.loadChatHistory()
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
            "documents" -> {
                LibraryScreen(
                    libraryState = libraryState,
                    onReloadLibrary = { query, fileType, sort ->
                        documentViewModel.loadLibraryDocuments(query, fileType, sort)
                    },
                    onLoadMoreLibrary = {
                        documentViewModel.loadMoreLibraryDocuments()
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
