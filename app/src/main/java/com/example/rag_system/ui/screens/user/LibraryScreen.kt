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
import com.example.rag_system.ui.models.DocumentFileFormat
import com.example.rag_system.ui.models.DocumentUiModel
import com.example.rag_system.ui.state.UiLoadState
import com.example.rag_system.ui.theme.*

/**
 * Màn hình Thư viện tài liệu (LibraryScreen) hiển thị danh sách tài liệu học tập từ Backend EduRAG.
 * Tuân thủ tuyệt đối Stateless UI: nhận [libraryState] từ bên ngoài và hiển thị theo trạng thái thực tế.
 */
@Composable
fun LibraryScreen(
    libraryState: UiLoadState<List<DocumentUiModel>>,
    onReloadLibrary: () -> Unit,
    onDocumentClick: (String) -> Unit,
    onTabSelected: (String) -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilter by rememberSaveable { mutableStateOf("Tất cả") }

    LaunchedEffect(libraryState) {
        if (libraryState is UiLoadState.Idle) {
            onReloadLibrary()
        }
    }

    Scaffold(
        topBar = {
            EduRAGTopAppBar(
                navigationContent = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(BrandPrimary.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🎓", fontSize = 18.sp)
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
                    Surface(
                        shape = CircleShape,
                        color = BrandSurfaceContainerLow,
                        border = BorderStroke(1.5.dp, BrandPrimary),
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .size(36.dp)
                            .clickable {
                                onProfileClick()
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
            EduRAGBottomNavBar(
                currentTab = "documents",
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
            LibraryFilterChips(
                selectedFilter = selectedFilter,
                onFilterSelected = { selectedFilter = it }
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                when (libraryState) {
                    is UiLoadState.Loading, is UiLoadState.Idle -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = BrandPrimary
                        )
                    }
                    is UiLoadState.Error -> {
                        Column(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Lỗi tải tài liệu: ${libraryState.message}",
                                color = BrandErrorDestructive,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            EduRAGButton(text = "Thử lại", onClick = onReloadLibrary)
                        }
                    }
                    is UiLoadState.Empty -> {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = "📭", fontSize = 48.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Chưa có tài liệu học tập nào trong hệ thống.",
                                color = BrandTextSecondary,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                    is UiLoadState.Success -> {
                        val allDocs = libraryState.data
                        val filteredDocuments = remember(allDocs, selectedFilter) {
                            when (selectedFilter) {
                                "Tất cả" -> allDocs
                                "Slide bài giảng" -> allDocs.filter { it.fileFormat == DocumentFileFormat.SLIDE }
                                "Tài liệu đọc" -> allDocs.filter { it.fileFormat == DocumentFileFormat.PDF || it.fileFormat == DocumentFileFormat.WORD }
                                else -> allDocs
                            }
                        }

                        if (filteredDocuments.isEmpty()) {
                            Text(
                                text = "Không có tài liệu nào thuộc bộ lọc này.",
                                color = BrandTextSecondary,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        } else {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(filteredDocuments, key = { it.id }) { document ->
                                    val (color, emoji) = when (document.fileFormat) {
                                        DocumentFileFormat.PDF -> Color(0xFFDCFCE7) to "🐍"
                                        DocumentFileFormat.SLIDE -> Color(0xFFE0F2FE) to "📁"
                                        DocumentFileFormat.WORD -> Color(0xFFF3E8FF) to "📝"
                                        else -> Color(0xFFFEF3C7) to "🗄️"
                                    }

                                    DocumentCard(
                                        title = document.title,
                                        categoryLabel = document.category,
                                        infoText = "${document.pageOrSlideCount} trang/slide",
                                        bannerColor = color,
                                        iconEmoji = emoji,
                                        onViewClick = {
                                            onDocumentClick(document.id)
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
