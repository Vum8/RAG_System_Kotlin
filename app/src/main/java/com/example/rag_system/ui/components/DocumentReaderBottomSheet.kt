package com.example.rag_system.ui.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.rag_system.data.repository.DocumentRepository
import com.example.rag_system.ui.models.SourceCitationUiModel
import com.example.rag_system.ui.theme.*

/**
 * Component hiển thị tài liệu học tập dạng Bottom Sheet đè lên màn hình Chat.
 * Stateless hoàn toàn — nhận dữ liệu và callback từ bên ngoài.
 * Tự bao gồm cả lớp phủ Backdrop và Surface trượt lên phía dưới.
 *
 * @param citation  Thông tin trích dẫn xác định tài liệu và trang cần mở.
 * @param onDismiss Callback khi người dùng đóng Bottom Sheet.
 */
@Composable
fun DocumentReaderBottomSheet(
    citation: SourceCitationUiModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val repository = remember { DocumentRepository() }

    var currentPage by rememberSaveable { mutableIntStateOf(citation.pageNumber ?: 45) }
    var bookmarkedPages by rememberSaveable { mutableStateOf(listOf<Int>()) }

    Box(modifier = Modifier.fillMaxSize()) {

        // Lớp phủ đen trong suốt (Backdrop) — bấm vào để đóng
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable { onDismiss() }
        )

        // Bottom Sheet tài liệu trượt lên (Cao 85% màn hình)
        Surface(
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            color = BrandSurface,
            shadowElevation = 16.dp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {

                // Thanh kéo Drag Handle
                Box(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(vertical = 12.dp)
                        .width(48.dp)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(BrandOutlineVariant)
                )

                // Top App Bar của Bottom Sheet (không áp dụng status bar padding)
                EduRAGTopAppBar(
                    applyStatusBarPadding = false,
                    navigationContent = {
                        Column {
                            Text(
                                text = citation.sourceDocumentName,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = BrandTextPrimary,
                                maxLines = 1
                            )
                            Text(
                                text = "Trang $currentPage • Kiến thức cơ bản",
                                style = MaterialTheme.typography.labelSmall,
                                color = BrandTextSecondary
                            )
                        }
                    },
                    actionContent = {
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(BrandSurfaceContainerLow)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Đóng",
                                tint = BrandOnSurfaceVariant,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                )

                // Lấy nội dung trang động theo số trang hiện tại từ Repository
                val pageData = remember(currentPage) {
                    repository.getDocumentPageContent(currentPage)
                }

                // Vùng đọc tài liệu có thể cuộn
                DocumentContentArea(
                    chapterTitle = pageData.chapterTitle,
                    sectionTitle = pageData.sectionTitle,
                    bodyTextBefore = pageData.bodyTextBefore,
                    highlightedSnippet = pageData.highlightedSnippet,
                    bodyTextAfter = pageData.bodyTextAfter,
                    modifier = Modifier.weight(1f)
                )

                // Thanh điều khiển trang và bookmark
                DocumentReaderControls(
                    currentPage = currentPage,
                    totalPages = 120,
                    isBookmarked = currentPage in bookmarkedPages,
                    onPageChanged = { page -> currentPage = page },
                    onBookmarkToggled = {
                        val isNowBookmarked = currentPage !in bookmarkedPages
                        bookmarkedPages = if (isNowBookmarked) {
                            bookmarkedPages + currentPage
                        } else {
                            bookmarkedPages - currentPage
                        }
                        Toast.makeText(
                            context,
                            if (isNowBookmarked) "Đã lưu dấu trang $currentPage!"
                            else "Đã bỏ lưu dấu trang $currentPage!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
            }
        }
    }
}
