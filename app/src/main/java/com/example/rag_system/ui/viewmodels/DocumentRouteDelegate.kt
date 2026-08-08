package com.example.rag_system.ui.viewmodels

import com.example.rag_system.data.api.core.ApiResult
import com.example.rag_system.data.repository.DocumentRepository
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
    private val documentRepository: DocumentRepository = DocumentRepository()
) {
    private val _libraryState = MutableStateFlow<UiLoadState<List<DocumentUiModel>>>(UiLoadState.Idle)
    val libraryState: StateFlow<UiLoadState<List<DocumentUiModel>>> = _libraryState.asStateFlow()

    private var currentPage = 1
    private var isLastPage = false
    private var currentQuery = ""
    private var currentFileType: String? = null
    private var currentSort: String = "newest"
    private var isFetching = false

    fun loadLibraryDocuments(search: String = "", fileType: String? = null, sort: String = "newest", isLoadMore: Boolean = false) {
        if (isFetching) return
        if (isLoadMore && isLastPage) return

        scope.launch {
            isFetching = true
            if (!isLoadMore) {
                currentPage = 1
                isLastPage = false
                currentQuery = search
                currentFileType = fileType
                currentSort = sort
                _libraryState.value = UiLoadState.Loading
            }

            when (val result = documentRepository.getLibraryDocuments(q = currentQuery, page = currentPage, fileType = currentFileType, sort = currentSort)) {
                is ApiResult.Success -> {
                    val newData = result.data
                    if (newData.isEmpty()) {
                        if (!isLoadMore) {
                            _libraryState.value = UiLoadState.Empty
                        }
                        isLastPage = true
                    } else {
                        val currentData = if (isLoadMore) {
                            (_libraryState.value as? UiLoadState.Success)?.data ?: emptyList()
                        } else {
                            emptyList()
                        }
                        _libraryState.value = UiLoadState.Success(currentData + newData)
                        currentPage++
                        // API mặc định limit = 50. Nếu nhỏ hơn 50 tức là đã hết data.
                        if (newData.size < 50) {
                            isLastPage = true
                        }
                    }
                }
                is ApiResult.Error -> {
                    if (!isLoadMore) {
                        _libraryState.value = UiLoadState.Error(
                            message = result.error.message,
                            code = result.error.code
                        )
                    }
                }
            }
            isFetching = false
        }
    }

    fun getPageContent(page: Int) = documentRepository.getDocumentPageContent(page)

    fun getDocumentTitleById(docId: String): String {
        val currentDocs = (_libraryState.value as? UiLoadState.Success)?.data ?: emptyList()
        return currentDocs.find { it.id == docId }?.title ?: "Tài liệu EduRAG (#$docId)"
    }
}
