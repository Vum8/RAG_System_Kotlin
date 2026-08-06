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
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.text.input.ImeAction
import com.example.rag_system.ui.components.*
import com.example.rag_system.ui.models.DocumentFileFormat
import com.example.rag_system.ui.models.DocumentUiModel
import com.example.rag_system.ui.state.UiLoadState
import com.example.rag_system.ui.theme.*
import android.net.Uri
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import com.example.rag_system.data.session.TokenManager

/**
 * Màn hình Thư viện tài liệu (LibraryScreen) hiển thị danh sách tài liệu học tập từ Backend EduRAG.
 * Tuân thủ tuyệt đối Stateless UI: nhận [libraryState] từ bên ngoài và hiển thị theo trạng thái thực tế.
 */
@Composable
fun LibraryScreen(
    libraryState: UiLoadState<List<DocumentUiModel>>,
    onReloadLibrary: (String) -> Unit,
    onLoadMoreLibrary: () -> Unit = {},
    onDocumentClick: (String) -> Unit,
    onTabSelected: (String) -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current


    var searchQuery by rememberSaveable { mutableStateOf("") }
    var isFirstLoad by remember { mutableStateOf(true) }

    LaunchedEffect(searchQuery) {
        if (isFirstLoad) {
            isFirstLoad = false
            onReloadLibrary(searchQuery)
        } else {
            // Debounce 500ms để tránh gọi API liên tục khi gõ
            kotlinx.coroutines.delay(500)
            onReloadLibrary(searchQuery)
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
                    UserAvatarButton(onClick = onProfileClick)
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
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Tìm kiếm tài liệu học tập...", color = BrandTextSecondary) },
                leadingIcon = { Text("🔍", fontSize = 16.sp, modifier = Modifier.padding(start = 8.dp)) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Text("❌", fontSize = 12.sp)
                        }
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                    }
                ),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BrandPrimary,
                    unfocusedBorderColor = BrandBorderSubtle,
                    focusedContainerColor = BrandSurface,
                    unfocusedContainerColor = BrandSurface
                )
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
                            EduRAGButton(text = "Thử lại", onClick = { onReloadLibrary(searchQuery) })
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

                        if (allDocs.isEmpty()) {
                            Text(
                                text = "Không tìm thấy tài liệu phù hợp.",
                                color = BrandTextSecondary,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        } else {
                            val gridState = rememberLazyGridState()

                            LaunchedEffect(gridState) {
                                snapshotFlow { gridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
                                    .collect { lastIndex ->
                                        if (lastIndex != null && lastIndex >= allDocs.size - 4) {
                                            onLoadMoreLibrary()
                                        }
                                    }
                            }

                            LazyVerticalGrid(
                                state = gridState,
                                columns = GridCells.Fixed(2),
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(allDocs, key = { it.id }) { document ->
                                    val (color, emoji) = when (document.fileFormat) {
                                        DocumentFileFormat.PDF -> Color(0xFFDCFCE7) to "🐍"
                                        DocumentFileFormat.SLIDE -> Color(0xFFE0F2FE) to "📁"
                                        DocumentFileFormat.WORD -> Color(0xFFF3E8FF) to "📝"
                                        else -> Color(0xFFFEF3C7) to "🗄️"
                                    }

                                    val pageText = if (document.pageOrSlideCount > 0) {
                                        "${document.pageOrSlideCount} trang"
                                    } else {
                                        ""
                                    }
                                    val sizeText = document.fileSizeText
                                    val detailText = if (pageText.isNotEmpty() && sizeText.isNotEmpty()) {
                                        "$pageText • $sizeText"
                                    } else {
                                        pageText.ifEmpty { sizeText }
                                    }

                                    DocumentCard(
                                        title = document.title,
                                        categoryLabel = document.category,
                                        infoText = detailText,
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
