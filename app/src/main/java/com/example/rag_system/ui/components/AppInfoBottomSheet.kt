package com.example.rag_system.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rag_system.ui.theme.*

/**
 * BottomSheet hiển thị thông tin giới thiệu Học viện (PTIT) & ứng dụng EduRAG (AppInfoBottomSheet).
 * Tích hợp phần đánh giá 5 sao tương tác (InteractiveRatingBar) và nhập ý kiến phản hồi đóng góp.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppInfoBottomSheet(
    onDismissRequest: () -> Unit,
    onSendFeedback: (Int, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var rating by rememberSaveable { mutableStateOf(5) }
    var feedbackText by rememberSaveable { mutableStateOf("") }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = BrandSurface,
        dragHandle = { BottomSheetDefaults.DragHandle() },
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp)
                .padding(bottom = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // ── 1. Phần giới thiệu Trường & App ──
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Logo trường học bo góc tròn
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(BrandPrimary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🎓", fontSize = 24.sp)
                }

                Text(
                    text = "Học viện Công nghệ Bưu chính Viễn thông",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 15.sp),
                    color = BrandTextPrimary,
                    textAlign = TextAlign.Center
                )

                Text(
                    text = "EduRAG — Trợ lý học tập AI thông minh, hỗ trợ sinh viên tra cứu tài liệu học trình và giải đáp mọi thắc mắc học tập tức thì.",
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                    color = BrandTextSecondary,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )
            }

            HorizontalDivider(color = BrandBorderSubtle, thickness = 1.dp)

            // ── 2. Phần đánh giá ứng dụng ──
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Đánh giá ứng dụng",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = BrandTextPrimary
                )

                // Gọi component con đánh giá 5 sao
                InteractiveRatingBar(
                    rating = rating,
                    onRatingChanged = { rating = it }
                )

                Spacer(modifier = Modifier.height(2.dp))

                // Ô nhập ý kiến đóng góp ngắn
                OutlinedTextField(
                    value = feedbackText,
                    onValueChange = { feedbackText = it },
                    placeholder = { Text("Hãy chia sẻ ý kiến đóng góp của bạn...", color = BrandOutlineVariant) },
                    singleLine = false,
                    maxLines = 2,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(72.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BrandPrimary,
                        unfocusedBorderColor = BrandBorderSubtle,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedTextColor = BrandTextPrimary,
                        unfocusedTextColor = BrandTextPrimary
                    )
                )
            }

            // ── 3. Nút Gửi đánh giá ──
            EduRAGButton(
                text = "Gửi đánh giá",
                onClick = {
                    onSendFeedback(rating, feedbackText)
                    onDismissRequest()
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
