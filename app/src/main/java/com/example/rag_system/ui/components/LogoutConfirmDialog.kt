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
 * Dialog xác nhận đăng xuất tài khoản của sinh viên.
 */
@Composable
fun LogoutConfirmDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Đăng xuất tài khoản?",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Text(
                text = "Bạn có chắc chắn muốn đăng xuất khỏi tài khoản học tập EduRAG không?",
                style = MaterialTheme.typography.bodyMedium,
                color = BrandOnSurfaceVariant
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = "Đăng xuất",
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
