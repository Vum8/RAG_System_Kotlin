package com.example.rag_system.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rag_system.ui.theme.*

/**
 * Vùng nội dung chi tiết của trang tài liệu (DocumentContentArea) hiển thị chương, bài học và phần trích dẫn nổi bật.
 */
@Composable
fun DocumentContentArea(
    chapterTitle: String,
    sectionTitle: String,
    bodyTextBefore: String,
    highlightedSnippet: String,
    bodyTextAfter: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Tag tiêu đề chương ở đầu trang PDF
        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .background(BrandOutlineVariant.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                .padding(horizontal = 16.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = chapterTitle.uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                ),
                color = BrandOnSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Tiêu đề chương mục
        Text(
            text = sectionTitle,
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = BrandPrimary
            )
        )

        // Văn bản thường trước phần trích dẫn
        Text(
            text = bodyTextBefore,
            style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
            color = BrandTextPrimary
        )

        // Hộp trích dẫn nổi bật viền trái khớp Figma
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Max) // Cho phép vạch màu bên trái chiếm hết chiều cao của văn bản
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFEEF2FF))
        ) {
            // Vạch viền màu xanh tím đậm
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(BrandPrimary)
            )
            // Nội dung văn bản nổi bật
            Text(
                text = highlightedSnippet,
                style = MaterialTheme.typography.bodyMedium.copy(
                    lineHeight = 22.sp,
                    fontWeight = FontWeight.Medium
                ),
                color = Color(0xFF2E2BC2),
                modifier = Modifier.padding(16.dp)
            )
        }

        // Văn bản tiếp theo sau trích dẫn
        Text(
            text = bodyTextAfter,
            style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
            color = BrandTextPrimary
        )
    }
}
