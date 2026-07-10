package com.example.rag_system.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rag_system.ui.theme.BrandOnSurfaceVariant

/**
 * Giao diện trạng thái trống hiển thị khi danh sách lịch sử chat rỗng.
 * Icon lịch sử + text mô tả nhạt màu, opacity 40%.
 */
@Composable
fun ChatHistoryEmptyState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "🕐",
            fontSize = 60.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Chưa có lịch sử hội thoại nào",
            style = MaterialTheme.typography.bodyLarge,
            color = BrandOnSurfaceVariant.copy(alpha = 0.4f)
        )
    }
}
