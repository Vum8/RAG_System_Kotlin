package com.example.rag_system.ui.screens.user

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rag_system.ui.components.*
import com.example.rag_system.ui.models.ReaderPageContentUiModel
import com.example.rag_system.ui.theme.*

/**
 * Màn hình Đọc Tài liệu chuyên dụng (DocumentReaderScreen).
 * Hoàn toàn Stateless: Nhận [documentTitle] và [pageContentProvider] từ ViewModel bên ngoài.
 */
@Composable
fun DocumentReaderScreen(
    documentId: String,
    documentTitle: String = "Tài liệu EduRAG",
    pageContentProvider: (Int) -> ReaderPageContentUiModel = { page ->
        ReaderPageContentUiModel(
            chapterTitle = "Giáo trình trích xuất",
            sectionTitle = "Trang số $page",
            bodyTextBefore = "Nội dung trang $page của tài liệu EduRAG (#$documentId).",
            highlightedSnippet = "Đoạn kiến thức trọng tâm được hệ thống AI trích xuất và tối ưu hóa cho việc đọc hiểu nhanh.",
            bodyTextAfter = "Sử dụng tính năng hỏi đáp AI để tra cứu hoặc giải đáp thắc mắc liên quan."
        )
    },
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var currentPage by rememberSaveable { mutableStateOf(1) }
    val totalPages = 10
    val bookmarkedPages = remember { mutableStateListOf<Int>() }

    val pageContent = remember(currentPage) { pageContentProvider(currentPage) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = BrandSurface,
        topBar = {
            EduRAGTopAppBar(
                navigationContent = {
                    Text(
                        text = "Quay lại",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = BrandPrimary,
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .clickable { onBackClick() }
                    )
                },
                centerContent = {
                    Text(
                        text = documentTitle,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = BrandTextPrimary
                    )
                }
            )
        },
        bottomBar = {
            DocumentReaderControls(
                currentPage = currentPage,
                totalPages = totalPages,
                isBookmarked = currentPage in bookmarkedPages,
                onPageChanged = { currentPage = it },
                onBookmarkToggled = {
                    if (currentPage in bookmarkedPages) {
                        bookmarkedPages.remove(currentPage)
                        Toast.makeText(context, "Đã bỏ lưu dấu trang $currentPage!", Toast.LENGTH_SHORT).show()
                    } else {
                        bookmarkedPages.add(currentPage)
                        Toast.makeText(context, "Đã lưu dấu trang $currentPage thành công!", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }
    ) { innerPadding ->
        DocumentContentArea(
            chapterTitle = pageContent.chapterTitle,
            sectionTitle = pageContent.sectionTitle,
            bodyTextBefore = pageContent.bodyTextBefore,
            highlightedSnippet = pageContent.highlightedSnippet,
            bodyTextAfter = pageContent.bodyTextAfter,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        )
    }
}
