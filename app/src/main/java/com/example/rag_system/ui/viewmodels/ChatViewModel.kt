package com.example.rag_system.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rag_system.data.repository.MockChatRepository
import com.example.rag_system.ui.models.ChatSessionUiModel
import com.example.rag_system.ui.models.MessageUiModel
import com.example.rag_system.ui.state.UiLoadState
import kotlinx.coroutines.flow.StateFlow

/**
 * Facade Coordinator ViewModel cho phân hệ Chat trong EduRAG.
 * Quản lý vòng đời Coroutine an toàn qua `viewModelScope` và ủy quyền xử lý nghiệp vụ cho [ChatRouteDelegate].
 */
class ChatViewModel(
    private val chatRepository: MockChatRepository = MockChatRepository()
) : ViewModel() {

    private val delegate = ChatRouteDelegate(
        scope = viewModelScope,
        chatRepository = chatRepository
    )

    val chatHistoryState: StateFlow<UiLoadState<List<ChatSessionUiModel>>> = delegate.chatHistoryState
    val currentChatState: StateFlow<UiLoadState<MessageUiModel>> = delegate.currentChatState

    init {
        loadChatHistory()
    }

    fun loadChatHistory() {
        delegate.loadChatHistory()
    }

    fun sendChatQuery(query: String) {
        delegate.sendChatQuery(query)
    }
}
