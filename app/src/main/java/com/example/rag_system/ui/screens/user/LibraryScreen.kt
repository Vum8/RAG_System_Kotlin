package com.example.rag_system.ui.screens.user

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.sp
import com.example.rag_system.ui.components.*
import com.example.rag_system.ui.theme.*

// Lớp dữ liệu mô phỏng tài liệu thư viện
data class LibraryDocument(
    val id: String,
    val title: String,
    val category: String,
    val info: String,
    val bannerColor: Color,
    val iconEmoji: String
)

private val mockDocuments = listOf(
    LibraryDocument("doc_1", "Kiến trúc máy tính", "Chương 1", "12 slides", Color(0xFFE0F2FE), "📁"),
    LibraryDocument("doc_2", "Hệ điều hành cơ bản", "Chương 2", "24 slides", Color(0xFFF3E8FF), "🧠"),
    LibraryDocument("doc_3", "Lập trình Python nâng cao", "Tài liệu đọc", "PDF • 45 trang", Color(0xFFDCFCE7), "🐍"),
    LibraryDocument("doc_4", "Cơ sở dữ liệu", "Chương 1", "18 slides", Color(0xFFFEF3C7), "🗄️"),
    LibraryDocument("doc_5", "Hướng dẫn khóa luận", "Tài liệu đọc", "Word • 10 trang", Color(0xFFE0F2FE), "📝"),
    LibraryDocument("doc_6", "Mạng máy tính", "Chương 3", "32 slides", Color(0xFFF3E8FF), "🌐")
)

/**
 * Màn hình Thư viện tài liệu (LibraryScreen) hiển thị danh sách tài liệu học tập.
 * Lắp ghép từ các component con độc lập theo nguyên tắc Component Modularization.
 */
@Composable
fun LibraryScreen(
    onDocumentClick: (String) -> Unit,
    onTabSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedFilter by rememberSaveable { mutableStateOf("Tất cả") }

    // Xử lý logic lọc danh sách tài liệu động theo chip đang chọn
    val filteredDocuments = remember(selectedFilter) {
        when (selectedFilter) {
            "Tất cả" -> mockDocuments
            "Slide bài giảng" -> mockDocuments.filter { it.info.contains("slides", ignoreCase = true) }
            "Tài liệu đọc" -> mockDocuments.filter { it.category == "Tài liệu đọc" || it.info.contains("trang", ignoreCase = true) }
            else -> mockDocuments.filter { it.category == selectedFilter }
        }
    }

    Scaffold(
        topBar = {
            // Sử dụng Base Header dùng chung (Phương án 2)
            EduRAGTopAppBar(
                navigationContent = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(BrandPrimary.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🎓", fontSize = 16.sp)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Thư viện",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = BrandPrimary
                            )
                        )
                    }
                },
                actionContent = {
                    // Profile Avatar sinh viên
                    Surface(
                        shape = CircleShape,
                        color = BrandSurfaceContainerLow,
                        border = BorderStroke(1.5.dp, BrandPrimary),
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .size(36.dp)
                            .clickable {
                                Toast.makeText(context, "Mở hồ sơ cá nhân", Toast.LENGTH_SHORT).show()
                            }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("A", fontWeight = FontWeight.Bold, color = BrandPrimary, fontSize = 14.sp)
                        }
                    }
                }
            )
        },
        bottomBar = {
            // Sử dụng lại thanh BottomNavBar dùng chung của hệ thống
            EduRAGBottomNavBar(
                currentTab = "documents", // Tab tài liệu đang được active
                onTabSelected = onTabSelected
            )
        },
        containerColor = BrandAppBackground,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // 1. Thanh lọc ChipFilter (Component con 2)
            LibraryFilterChips(
                selectedFilter = selectedFilter,
                onFilterSelected = { selectedFilter = it }
            )

            // 2. Lưới hiển thị danh sách tài liệu động (Component con 3)
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                items(filteredDocuments, key = { it.id }) { document ->
                    DocumentCard(
                        title = document.title,
                        categoryLabel = document.category,
                        infoText = document.info,
                        bannerColor = document.bannerColor,
                        iconEmoji = document.iconEmoji,
                        onViewClick = {
                            onDocumentClick(document.id)
                        }
                    )
                }
            }
        }
    }
}
