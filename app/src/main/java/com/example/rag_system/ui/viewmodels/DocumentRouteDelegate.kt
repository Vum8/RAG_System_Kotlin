package com.example.rag_system.ui.viewmodels

import com.example.rag_system.data.api.core.ApiResult
import com.example.rag_system.data.repository.MockDocumentRepository
import com.example.rag_system.ui.models.DocumentUiModel
import com.example.rag_system.ui.state.UiLoadState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Route Delegate chuyên trách quản lý luồng trạng thái thư viện tài liệu học tập EduRAG.
 */
class DocumentRouteDelegate(
    private val scope: CoroutineScope,
    private val documentRepository: MockDocumentRepository = MockDocumentRepository()
) {
    private val _libraryState = MutableStateFlow<UiLoadState<List<DocumentUiModel>>>(UiLoadState.Idle)
    val libraryState: StateFlow<UiLoadState<List<DocumentUiModel>>> = _libraryState.asStateFlow()

    fun loadLibraryDocuments() {
        scope.launch {
            _libraryState.value = UiLoadState.Loading
            when (val result = documentRepository.getLibraryDocuments()) {
                is ApiResult.Success -> {
                    if (result.data.isEmpty()) {
                        _libraryState.value = UiLoadState.Empty
                    } else {
                        _libraryState.value = UiLoadState.Success(result.data)
                    }
                }
                is ApiResult.Error -> {
                    _libraryState.value = UiLoadState.Error(
                        message = result.error.message,
                        code = result.error.code
                    )
                }
            }
        }
    }
}
