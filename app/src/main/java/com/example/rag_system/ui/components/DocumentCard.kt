package com.example.rag_system.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rag_system.ui.theme.*

/**
 * Thẻ hiển thị tài liệu học tập trong danh sách lưới Thư viện (DocumentCard).
 * Stateless UI hoàn toàn, nhận dữ liệu hiển thị và truyền hành động click Xem ra ngoài.
 */
@Composable
fun DocumentCard(
    title: String,
    categoryLabel: String,
    infoText: String,
    bannerColor: Color,
    iconEmoji: String,
    onViewClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, BrandBorderSubtle, RoundedCornerShape(12.dp))
            .clickable { onViewClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = BrandSurface)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Phần trên: Banner màu kèm tag và emoji mờ làm hình nền minh họa
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(96.dp)
                    .background(bannerColor)
                    .padding(12.dp)
            ) {
                // Nhãn phân loại (Ví dụ: "Chương 1")
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.8f))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = categoryLabel,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = BrandPrimary
                    )
                }

                // Biểu tượng mờ bên góc
                Text(
                    text = iconEmoji,
                    fontSize = 44.sp,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 2.dp, end = 2.dp)
                        .alpha(0.2f)
                )
            }

            // Phần dưới: Tiêu đề tài liệu, mô tả số trang và liên kết xem
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    color = BrandTextPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.height(44.dp) // Cố định chiều cao để thẳng hàng lưới
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Dòng mô tả (ví dụ: "12 slides" hoặc "PDF • 45 trang")
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = "📄",
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = infoText,
                        style = MaterialTheme.typography.labelSmall,
                        color = BrandTextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Liên kết Xem hành động
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Xem",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = BrandPrimaryContainer
                        )
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = BrandPrimaryContainer,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}
