package com.example.rag_system.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rag_system.ui.models.ChatSessionUiModel
import com.example.rag_system.ui.theme.*
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * Component hiển thị một thẻ phiên lịch sử chat với khả năng vuốt trái (swipe-to-delete).
 * Cấu trúc Box 2 lớp: layer dưới = nút xóa đỏ, layer trên = card trắng offset theo gesture.
 */
@Composable
fun ChatHistoryItem(
    session: ChatSessionUiModel,
    isHighlighted: Boolean = false,
    onSessionClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val swipeThreshold = -240f // px tương đương ~80.dp ở mật độ màn hình chuẩn
    val offsetX = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
    ) {
        // ── Layer 1: Nút xóa đỏ (nằm dưới cùng, chỉ hiện khi vuốt trái) ──
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight()
                .width(80.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(BrandAppBackground)
                .clickable {
                    scope.launch {
                        offsetX.animateTo(0f, spring())
                    }
                    onDeleteClick()
                },
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(BrandErrorDestructive.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🗑️", fontSize = 20.sp)
            }
        }

        // ── Layer 2: Card nội dung chính (có thể bị offset sang trái) ──
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = BrandSurface,
            shadowElevation = 2.dp,
            modifier = Modifier
                .fillMaxWidth()
                .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                .border(1.dp, BrandBorderSubtle, RoundedCornerShape(12.dp))
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            scope.launch {
                                if (offsetX.value < swipeThreshold / 2) {
                                    offsetX.animateTo(swipeThreshold, spring(dampingRatio = 0.8f))
                                } else {
                                    offsetX.animateTo(0f, spring(dampingRatio = 0.8f))
                                }
                            }
                        },
                        onDragCancel = {
                            scope.launch { offsetX.animateTo(0f, spring()) }
                        },
                        onHorizontalDrag = { _, dragAmount ->
                            scope.launch {
                                val newValue = (offsetX.value + dragAmount)
                                    .coerceIn(swipeThreshold - 30f, 0f)
                                offsetX.snapTo(newValue)
                            }
                        }
                    )
                }
                .clickable {
                    if (offsetX.value < -20f) {
                        scope.launch { offsetX.animateTo(0f, spring()) }
                    } else {
                        onSessionClick()
                    }
                }
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // ── Hàng 1: Tiêu đề + thời gian ──
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = session.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = if (isHighlighted) BrandPrimary else BrandTextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = session.displayTime,
                        style = MaterialTheme.typography.labelSmall,
                        color = BrandOnSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // ── Hàng 2: Preview nội dung, tối đa 2 dòng ──
                Text(
                    text = session.lastMessagePreview,
                    style = MaterialTheme.typography.bodyMedium,
                    color = BrandOnSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(12.dp))

                // ── Hàng 3: Badge môn học ──
                session.subjectLabel?.let { label ->
                    ChatHistorySubjectBadge(label = label)
                }
            }
        }
    }
}

/**
 * Badge nhỏ hiển thị tên môn học / chương, màu nền thay đổi theo loại môn.
 */
@Composable
private fun ChatHistorySubjectBadge(label: String) {
    val (bgColor, iconEmoji, textColor) = when {
        label.contains("Triết") || label.contains("biện chứng") ->
            Triple(BrandTertiaryFixed, "📖", BrandTertiary)
        label.contains("tài liệu") || label.contains("RAG") || label.contains("AI") ->
            Triple(BrandPrimaryFixed, "📄", BrandPrimary)
        label.contains("lập trình") || label.contains("Python") || label.contains("code") ->
            Triple(BrandSecondaryFixed, "💻", BrandOnSurfaceVariant)
        else ->
            Triple(BrandSurfaceContainerHighest, "📊", BrandOutline)
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Icon nhỏ trong hình tròn
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(bgColor)
                .border(2.dp, BrandSurface, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(text = iconEmoji, fontSize = 10.sp)
        }

        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = textColor
        )
    }
}
