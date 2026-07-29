package com.example.rag_system.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rag_system.data.repository.DocumentRepository
import com.example.rag_system.ui.models.DocumentUiModel
import com.example.rag_system.ui.state.UiLoadState
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Facade Coordinator ViewModel cho phân hệ Thư viện tài liệu trong EduRAG.
 * Quản lý vòng đời Coroutine qua `viewModelScope` và chuyển tiếp logic sang [DocumentRouteDelegate].
 */
class DocumentViewModel(
    private val documentRepository: DocumentRepository = DocumentRepository()
) : ViewModel() {

    private val delegate = DocumentRouteDelegate(
        scope = viewModelScope,
        documentRepository = documentRepository
    )

    val libraryState: StateFlow<UiLoadState<List<DocumentUiModel>>> = delegate.libraryState

    init {
        loadLibraryDocuments()
    }

    fun loadLibraryDocuments(search: String = "") {
        delegate.loadLibraryDocuments(search)
    }

    fun getPageContent(page: Int) = delegate.getPageContent(page)

    fun getDocumentTitleById(docId: String) = delegate.getDocumentTitleById(docId)

    suspend fun downloadDocumentFile(
        context: android.content.Context, 
        docId: String,
        onProgress: (Int) -> Unit = {}
    ): java.io.File? {
        return documentRepository.downloadDocumentFile(context, docId, onProgress)
    }

    /**
     * Lưu ID sách đọc dở cục bộ.
     */
    fun saveLastReadDocumentId(context: android.content.Context, docId: String) {
        documentRepository.saveLastReadDocumentId(context, docId)
    }

    /**
     * Tải ngầm (Prefetch) tài liệu đọc dở gần nhất của sinh viên.
     */
    fun prefetchLastReadDocument(context: android.content.Context) {
        val lastReadId = documentRepository.getLastReadDocumentId(context)
        if (lastReadId != null) {
            viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
                // Tải ngầm, không cần lắng nghe tiến trình UI
                documentRepository.downloadDocumentFile(context, lastReadId)
            }
        }
    }
}
