package com.example.rag_system.ui.viewmodels

import com.example.rag_system.data.api.core.ApiResult
import com.example.rag_system.data.repository.MockChatRepository
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
    private val chatRepository: MockChatRepository = MockChatRepository()
) {
    private val _chatHistoryState = MutableStateFlow<UiLoadState<List<ChatSessionUiModel>>>(UiLoadState.Idle)
    val chatHistoryState: StateFlow<UiLoadState<List<ChatSessionUiModel>>> = _chatHistoryState.asStateFlow()

    private val _currentChatState = MutableStateFlow<UiLoadState<MessageUiModel>>(UiLoadState.Idle)
    val currentChatState: StateFlow<UiLoadState<MessageUiModel>> = _currentChatState.asStateFlow()

    fun loadChatHistory() {
        scope.launch {
            _chatHistoryState.value = UiLoadState.Loading
            when (val result = chatRepository.getChatHistory()) {
                is ApiResult.Success -> {
                    if (result.data.isEmpty()) {
                        _chatHistoryState.value = UiLoadState.Empty
                    } else {
                        _chatHistoryState.value = UiLoadState.Success(result.data)
                    }
                }
                is ApiResult.Error -> {
                    _chatHistoryState.value = UiLoadState.Error(
                        message = result.error.message,
                        code = result.error.code
                    )
                }
            }
        }
    }

    fun sendChatQuery(query: String) {
        if (query.isBlank()) return
        scope.launch {
            _currentChatState.value = UiLoadState.Loading
            when (val result = chatRepository.sendChatQuery(query)) {
                is ApiResult.Success -> {
                    _currentChatState.value = UiLoadState.Success(result.data)
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
}
