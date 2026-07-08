package com.example.rag_system.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rag_system.data.repository.MockDocumentRepository
import com.example.rag_system.ui.models.DocumentUiModel
import com.example.rag_system.ui.state.UiLoadState
import kotlinx.coroutines.flow.StateFlow

/**
 * Facade Coordinator ViewModel cho phân hệ Thư viện tài liệu trong EduRAG.
 * Quản lý vòng đời Coroutine qua `viewModelScope` và chuyển tiếp logic sang [DocumentRouteDelegate].
 */
class DocumentViewModel(
    private val documentRepository: MockDocumentRepository = MockDocumentRepository()
) : ViewModel() {

    private val delegate = DocumentRouteDelegate(
        scope = viewModelScope,
        documentRepository = documentRepository
    )

    val libraryState: StateFlow<UiLoadState<List<DocumentUiModel>>> = delegate.libraryState

    init {
        loadLibraryDocuments()
    }

    fun loadLibraryDocuments() {
        delegate.loadLibraryDocuments()
    }
}
