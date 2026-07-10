package com.example.rag_system.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateListOf
import android.net.Uri
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

    // Lưu nháp nội dung đang gõ dở trong ô chat — được hoist lên ViewModel
    // để không bị reset khi sinh viên chuyển tab và quay lại
    var draftInputText by mutableStateOf("")
        private set

    fun updateDraftInput(text: String) {
        draftInputText = text
    }

    // Lưu nháp danh sách tệp đính kèm — hoist lên ViewModel để không mất khi đổi tab
    val draftAttachedFileNames = mutableStateListOf<String>()
    val draftAttachedFileUris = mutableStateListOf<Uri>()

    fun addAttachment(fileName: String, uri: Uri) {
        if (!draftAttachedFileUris.contains(uri)) {
            draftAttachedFileNames.add(fileName)
            draftAttachedFileUris.add(uri)
        }
    }

    fun removeAttachment(index: Int) {
        if (index in draftAttachedFileNames.indices) {
            draftAttachedFileNames.removeAt(index)
            draftAttachedFileUris.removeAt(index)
        }
    }

    fun clearAttachments() {
        draftAttachedFileNames.clear()
        draftAttachedFileUris.clear()
    }

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

