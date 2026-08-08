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
import com.example.rag_system.ui.components.LocalToastManager
import com.example.rag_system.ui.components.ToastType
import com.example.rag_system.ui.components.UserAvatarButton
import com.example.rag_system.ui.models.ChatSessionUiModel
import com.example.rag_system.ui.state.UiLoadState
import com.example.rag_system.ui.theme.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf

/**
 * Màn hình Lịch sử Chat - Scaffold khung lắp ghép các component theo MVVM Stateless.
 * Nhận toàn bộ dữ liệu từ bên ngoài, không chứa logic nghiệp vụ.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    chatHistoryState: UiLoadState<List<ChatSessionUiModel>>,
    onTabSelected: (String) -> Unit,
    onSessionClick: (ChatSessionUiModel) -> Unit,
    onNewChatClick: () -> Unit,
    onProfileClick: () -> Unit,
    onDeleteSession: (String, (Boolean) -> Unit) -> Unit,
    onDeleteAll: ((Boolean) -> Unit) -> Unit,
    onLoadMore: () -> Unit = {},
    onRefresh: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    var isRefreshing by remember { mutableStateOf(false) }
    val pullRefreshState = rememberPullToRefreshState()

    val context = LocalContext.current
    val toastManager = LocalToastManager.current

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
                    UserAvatarButton(onClick = onProfileClick)
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
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                coroutineScope.launch {
                    isRefreshing = true
                    onRefresh()
                    delay(600)
                    isRefreshing = false
                }
            },
            state = pullRefreshState,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
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
                        onDeleteSession(session.id) { success ->
                            if (success) {
                                sessions.remove(session)
                                toastManager.showToast("Đã xóa phiên chat", ToastType.SUCCESS)
                            } else {
                                toastManager.showToast("Lỗi: Không thể xóa phiên chat", ToastType.ERROR)
                            }
                        }
                    },
                    onDeleteAll = {
                        onDeleteAll { success ->
                            if (success) {
                                sessions.clear()
                                toastManager.showToast("Đã xóa toàn bộ lịch sử", ToastType.SUCCESS)
                            } else {
                                toastManager.showToast("Lỗi: Không thể xóa toàn bộ lịch sử", ToastType.ERROR)
                            }
                        }
                    },
                    onNewChatClick = {
                        onNewChatClick()
                        onTabSelected("chat")
                    },
                    onLoadMore = onLoadMore,
                    modifier = Modifier
                        .fillMaxSize()
                )
            }
        } // Đóng when
        } // Đóng PullToRefreshBox
    } // Đóng Scaffold
}
