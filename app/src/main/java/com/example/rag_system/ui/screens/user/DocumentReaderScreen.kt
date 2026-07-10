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
import com.example.rag_system.ui.theme.*

// Mock dữ liệu trang sách theo documentId để hiển thị phong phú
private data class BookPageMock(
    val chapter: String,
    val section: String,
    val textBefore: String,
    val highlight: String,
    val textAfter: String
)

private val mockBookPages = listOf(
    BookPageMock(
        chapter = "Chương I: Tổng quan hệ thống",
        section = "1.1 Giới thiệu kiến trúc máy tính",
        textBefore = "Kiến trúc máy tính đề cập đến các thuộc tính hệ thống được lập trình viên nhìn thấy, hoặc nói cách khác, các thuộc tính có tác động trực tiếp đến việc thực thi logic của một chương trình.",
        highlight = "Kiến trúc máy tính bao gồm tập lệnh, số lượng bit dùng biểu diễn dữ liệu, cơ chế vào/ra và kỹ thuật định địa chỉ bộ nhớ.",
        textAfter = "Ngược lại, tổ chức máy tính liên quan đến các đơn vị vận hành phần cứng và sự kết nối giữa chúng để thực hiện các đặc tả kiến trúc đã đề ra."
    ),
    BookPageMock(
        chapter = "Chương I: Tổng quan hệ thống",
        section = "1.2 Mô hình Von Neumann",
        textBefore = "Mô hình Von Neumann là nền tảng của hầu hết các thiết kế máy tính hiện đại. Mô hình này mô tả cấu trúc phần cứng máy tính gồm đơn vị xử lý trung tâm (CPU), bộ nhớ và thiết bị ngoại vi.",
        highlight = "Đặc trưng lớn nhất của kiến trúc Von Neumann là chương trình và dữ liệu được lưu trữ chung trong cùng một không gian bộ nhớ vật lý.",
        textAfter = "Mô hình này giúp máy tính hoạt động linh hoạt, cho phép nạp các chương trình khác nhau vào bộ nhớ để thực thi mà không cần cấu hình lại phần cứng."
    ),
    BookPageMock(
        chapter = "Chương II: Bộ vi xử lý",
        section = "2.1 Chu kỳ lệnh CPU",
        textBefore = "CPU thực hiện các lệnh thông qua một chu kỳ lặp đi lặp lại gồm ba bước cơ bản: Nhận lệnh (Fetch), Giải mã lệnh (Decode) và Thực thi lệnh (Execute).",
        highlight = "Thanh ghi đếm chương trình (PC - Program Counter) luôn lưu trữ địa chỉ của lệnh tiếp theo sẽ được CPU nạp vào thực hiện.",
        textAfter = "Tốc độ thực hiện chu kỳ lệnh này được điều phối bởi xung nhịp đồng hồ hệ thống (System Clock), đo bằng đơn vị Hertz (Hz)."
    )
)

/**
 * Màn hình Đọc Tài liệu chuyên dụng (DocumentReaderScreen).
 * Hoàn toàn Stateless: Nhận callback onBackClick để lùi trang và quản lý hiển thị.
 */
@Composable
fun DocumentReaderScreen(
    documentId: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var currentPage by rememberSaveable { mutableStateOf(1) }
    val totalPages = 10
    val bookmarkedPages = remember { mutableStateListOf<Int>() }

    // Mock tên tài liệu hiển thị trên TopAppBar dựa trên documentId
    val documentTitle = remember(documentId) {
        when (documentId) {
            "doc_1" -> "Kiến trúc máy tính"
            "doc_2" -> "Hệ điều hành cơ bản"
            "doc_3" -> "Lập trình Python nâng cao"
            "doc_4" -> "Cơ sở dữ liệu"
            "doc_5" -> "Hướng dẫn khóa luận"
            else -> "Mạng máy tính"
        }
    }

    // Chọn mock content theo trang hiện tại (lặp vòng nếu trang lớn hơn mock size)
    val pageContent = mockBookPages[(currentPage - 1) % mockBookPages.size]

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
            // Sử dụng lại trình điều khiển lật trang chân trang đã có sẵn
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
        // Sử dụng lại vùng nội dung trang chi tiết tài liệu đã có sẵn
        DocumentContentArea(
            chapterTitle = pageContent.chapter,
            sectionTitle = pageContent.section,
            bodyTextBefore = pageContent.textBefore,
            highlightedSnippet = pageContent.highlight,
            bodyTextAfter = pageContent.textAfter,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        )
    }
}
