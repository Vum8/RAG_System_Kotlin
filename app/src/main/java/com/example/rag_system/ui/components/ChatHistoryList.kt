package com.example.rag_system.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rag_system.ui.models.ChatSessionUiModel
import com.example.rag_system.ui.theme.BrandErrorDestructive
import com.example.rag_system.ui.theme.BrandTextPrimary

/**
 * Danh sách lịch sử chat gồm header "Gần đây" + nút "Xóa tất cả" + LazyColumn.
 * Khi danh sách trống, hiển thị [ChatHistoryEmptyState].
 * Khi có item, mỗi item được bọc trong [ChatHistoryItem] hỗ trợ swipe-to-delete.
 */
@Composable
fun ChatHistoryList(
    sessions: List<ChatSessionUiModel>,
    onSessionClick: (ChatSessionUiModel) -> Unit,
    onDeleteSession: (ChatSessionUiModel) -> Unit,
    onDeleteAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDeleteAllDialog by remember { mutableStateOf(false) }

    if (showDeleteAllDialog) {
        DeleteAllConfirmDialog(
            onDismiss = { showDeleteAllDialog = false },
            onConfirm = {
                showDeleteAllDialog = false
                onDeleteAll()
            }
        )
    }

    Column(modifier = modifier.fillMaxSize()) {
        // ── Header: "Gần đây" + nút "Xóa tất cả" ──
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Gần đây",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = BrandTextPrimary
            )

            if (sessions.isNotEmpty()) {
                TextButton(onClick = { showDeleteAllDialog = true }) {
                    Text(
                        text = "🗑  Xóa tất cả",
                        fontSize = 13.sp,
                        color = BrandErrorDestructive,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // ── Body: Danh sách hoặc Empty State ──
        if (sessions.isEmpty()) {
            ChatHistoryEmptyState()
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                itemsIndexed(sessions) { index, session ->
                    ChatHistoryItem(
                        session = session,
                        isHighlighted = index == 0, // Item đầu tiên có tiêu đề màu primary
                        onSessionClick = { onSessionClick(session) },
                        onDeleteClick = { onDeleteSession(session) }
                    )
                }
            }
        }
    }
}
