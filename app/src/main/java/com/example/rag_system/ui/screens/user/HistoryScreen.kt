package com.example.rag_system.ui.screens.user

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rag_system.ui.components.ChatHistoryList
import com.example.rag_system.ui.components.EduRAGBottomNavBar
import com.example.rag_system.ui.components.EduRAGTopAppBar
import com.example.rag_system.ui.models.ChatSessionUiModel
import com.example.rag_system.ui.state.UiLoadState
import com.example.rag_system.ui.theme.*

/**
 * Màn hình Lịch sử Chat - Scaffold khung lắp ghép các component theo MVVM Stateless.
 * Nhận toàn bộ dữ liệu từ bên ngoài, không chứa logic nghiệp vụ.
 */
@Composable
fun HistoryScreen(
    chatHistoryState: UiLoadState<List<ChatSessionUiModel>>,
    onTabSelected: (String) -> Unit,
    onSessionClick: (ChatSessionUiModel) -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Quản lý danh sách sessions cục bộ để xử lý xóa tại UI (mock UX)
    val sessions = remember(chatHistoryState) {
        if (chatHistoryState is UiLoadState.Success) {
            chatHistoryState.data.toMutableStateList()
        } else {
            mutableStateListOf()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = BrandAppBackground,
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
                            text = "Lịch sử Chat",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = BrandPrimary
                            )
                        )
                    }
                },
                actionContent = {
                    // Avatar người dùng
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
                            Text(
                                text = "A",
                                fontWeight = FontWeight.Bold,
                                color = BrandPrimary,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            EduRAGBottomNavBar(
                currentTab = "history",
                onTabSelected = onTabSelected
            )
        }
    ) { innerPadding ->
        when (chatHistoryState) {
            is UiLoadState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = BrandPrimary)
                }
            }
            else -> {
                ChatHistoryList(
                    sessions = sessions,
                    onSessionClick = { session ->
                        onSessionClick(session)
                        // Điều hướng sang tab Chat mở phiên đã chọn
                        onTabSelected("chat")
                    },
                    onDeleteSession = { session ->
                        sessions.remove(session)
                        Toast.makeText(context, "Đã xóa phiên chat", Toast.LENGTH_SHORT).show()
                    },
                    onDeleteAll = {
                        sessions.clear()
                        Toast.makeText(context, "Đã xóa toàn bộ lịch sử", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }
        }
    }
}
