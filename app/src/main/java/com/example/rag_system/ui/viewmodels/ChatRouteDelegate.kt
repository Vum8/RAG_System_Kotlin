package com.example.rag_system.ui.viewmodels

import com.example.rag_system.data.api.core.ApiResult
import com.example.rag_system.data.repository.ChatRepository
import com.example.rag_system.ui.models.ChatSessionUiModel
import com.example.rag_system.ui.models.MessageUiModel
import com.example.rag_system.ui.state.UiLoadState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Route Delegate chuyên trách xử lý nghiệp vụ, chuyển đổi trạng thái và tương tác Repository cho phân hệ Chat EduRAG.
 */
class ChatRouteDelegate(
    private val scope: CoroutineScope,
    private val chatRepository: ChatRepository = ChatRepository()
) {
    private val _chatHistoryState = MutableStateFlow<UiLoadState<List<ChatSessionUiModel>>>(UiLoadState.Idle)
    val chatHistoryState: StateFlow<UiLoadState<List<ChatSessionUiModel>>> = _chatHistoryState.asStateFlow()

    private val _currentChatState = MutableStateFlow<UiLoadState<MessageUiModel>>(UiLoadState.Idle)
    val currentChatState: StateFlow<UiLoadState<MessageUiModel>> = _currentChatState.asStateFlow()

    private val _sessionMessagesState = MutableStateFlow<UiLoadState<List<MessageUiModel>>>(UiLoadState.Idle)
    val sessionMessagesState: StateFlow<UiLoadState<List<MessageUiModel>>> = _sessionMessagesState.asStateFlow()

    private var historyOffset = 0
    private var isHistoryEnded = false
    private val historyLimit = 20

    private var messageOffset = 0
    private var isMessageEnded = false
    private val messageLimit = 50
    private var currentSessionId: Long? = null

    fun loadChatHistory(isLoadMore: Boolean = false) {
        if (isLoadMore && isHistoryEnded) return
        if (!isLoadMore) {
            historyOffset = 0
            isHistoryEnded = false
            _chatHistoryState.value = UiLoadState.Loading
        }

        scope.launch {
            when (val result = chatRepository.getChatHistory(offset = historyOffset, limit = historyLimit)) {
                is ApiResult.Success -> {
                    val newItems = result.data
                    isHistoryEnded = newItems.size < historyLimit

                    val currentList = if (isLoadMore) {
                        (_chatHistoryState.value as? UiLoadState.Success)?.data ?: emptyList()
                    } else emptyList()

                    val updatedList = currentList + newItems
                    
                    if (updatedList.isEmpty()) {
                        _chatHistoryState.value = UiLoadState.Empty
                    } else {
                        historyOffset += newItems.size
                        _chatHistoryState.value = UiLoadState.Success(updatedList)
                    }
                }
                is ApiResult.Error -> {
                    if (!isLoadMore) {
                        _chatHistoryState.value = UiLoadState.Error(
                            message = result.error.message,
                            code = result.error.code
                        )
                    }
                }
            }
        }
    }

    private var isLoadingMoreMessages = false

    fun loadSessionMessages(sessionId: Long, isLoadMore: Boolean = false) {
        if (isLoadMore && currentSessionId != sessionId) return
        if (isLoadMore && (isMessageEnded || isLoadingMoreMessages)) return
        if (!isLoadMore || currentSessionId != sessionId) {
            messageOffset = 0
            isMessageEnded = false
            currentSessionId = sessionId
            _sessionMessagesState.value = UiLoadState.Loading
        }

        if (isLoadMore) {
            isLoadingMoreMessages = true
        }

        scope.launch {
            when (val result = chatRepository.getSessionMessages(sessionId = sessionId, offset = messageOffset, limit = messageLimit)) {
                is ApiResult.Success -> {
                    val newItems = result.data
                    isMessageEnded = newItems.size < messageLimit

                    val currentList = if (isLoadMore) {
                        (_sessionMessagesState.value as? UiLoadState.Success)?.data ?: emptyList()
                    } else emptyList()

                    val updatedList = currentList + newItems
                    
                    if (updatedList.isEmpty()) {
                        _sessionMessagesState.value = UiLoadState.Empty
                    } else {
                        messageOffset += newItems.size
                        _sessionMessagesState.value = UiLoadState.Success(updatedList)
                    }
                    if (isLoadMore) isLoadingMoreMessages = false
                }
                is ApiResult.Error -> {
                    if (isLoadMore) isLoadingMoreMessages = false
                    if (!isLoadMore) {
                        _sessionMessagesState.value = UiLoadState.Error(
                            message = result.error.message,
                            code = result.error.code
                        )
                    }
                }
            }
        }
    }

    fun startNewSession() {
        chatRepository.setCurrentSession(null)
        currentSessionId = null
        messageOffset = 0
        isMessageEnded = false
        _sessionMessagesState.value = UiLoadState.Empty
        _currentChatState.value = UiLoadState.Idle
    }

    fun sendChatQuery(query: String) {
        if (query.isBlank()) return
        scope.launch {
            _currentChatState.value = UiLoadState.Loading
            when (val result = chatRepository.sendChatQuery(query)) {
                is ApiResult.Success -> {
                    _currentChatState.value = UiLoadState.Success(result.data)

                    // Cập nhật lại lịch sử khi có tin nhắn mới
                    loadChatHistory(isLoadMore = false)
                }
                is ApiResult.Error -> {
                    _currentChatState.value = UiLoadState.Error(
                        message = result.error.message,
                        code = result.error.code
                    )
                }
            }
        }
    }

    fun deleteSession(sessionId: Long, onResult: (Boolean) -> Unit) {
        scope.launch {
            when (val result = chatRepository.deleteSession(sessionId)) {
                is ApiResult.Success -> {
                    onResult(true)
                    // Tải lại lịch sử sau khi xóa thành công
                    loadChatHistory()
                }
                is ApiResult.Error -> {
                    onResult(false)
                }
            }
        }
    }
}
