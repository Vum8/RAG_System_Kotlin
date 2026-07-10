package com.example.rag_system.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import com.example.rag_system.ui.theme.BrandErrorDestructive
import com.example.rag_system.ui.theme.BrandOnSurfaceVariant
import com.example.rag_system.ui.theme.BrandPrimary

/**
 * Dialog xác nhận xóa toàn bộ lịch sử chat.
 * Hiển thị cảnh báo không thể hoàn tác và 2 nút Hủy / Xóa tất cả.
 */
@Composable
fun DeleteAllConfirmDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Xóa toàn bộ lịch sử?",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Text(
                text = "Tất cả lịch sử hội thoại sẽ bị xóa vĩnh viễn. Hành động này không thể hoàn tác.",
                style = MaterialTheme.typography.bodyMedium,
                color = BrandOnSurfaceVariant
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = "Xóa tất cả",
                    color = BrandErrorDestructive,
                    fontWeight = FontWeight.SemiBold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Hủy",
                    color = BrandPrimary
                )
            }
        }
    )
}
