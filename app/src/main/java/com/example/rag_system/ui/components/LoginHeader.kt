package com.example.rag_system.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rag_system.ui.theme.BrandPrimaryContainer

/**
 * Header chứa logo thương hiệu EduRAG đơn giản tại màn hình đăng nhập.
 */
@Composable
fun LoginHeader(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .statusBarsPadding() // Đẩy nội dung xuống dưới thanh trạng thái hệ thống
            .fillMaxWidth()
            .height(64.dp)
            .padding(horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(BrandPrimaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text("🎓", fontSize = 18.sp)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "EduRAG",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = BrandPrimaryContainer
            )
        )
    }
}
