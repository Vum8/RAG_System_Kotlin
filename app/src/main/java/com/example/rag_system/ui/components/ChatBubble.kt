package com.example.rag_system.ui.components

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rag_system.ui.models.SourceCitationUiModel
import com.example.rag_system.ui.theme.*

/**
 * Các thành phần bong bóng tin nhắn (ChatBubble) theo chuẩn Component Modularization.
 */

@Composable
fun EduAiAvatar() {
    Surface(
        shape = CircleShape,
        color = BrandSecondaryContainer,
        modifier = Modifier.size(36.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text("🤖", fontSize = 18.sp)
        }
    }
}

@Composable
fun EduAiWelcomeMessage() {
    Row(modifier = Modifier.fillMaxWidth()) {
        EduAiAvatar()
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Chào bạn, tôi là trợ lý EduRAG. Tôi có thể giúp gì cho bạn?",
                style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
                color = BrandTextPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "TRỢ LÝ EDURAG • 09:00",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                ),
                color = BrandTextSecondary
            )
        }
    }
}

@Composable
fun EduUserMessageBubble(
    content: String
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.End
    ) {
        if (content.isNotBlank()) {
            var isExpanded by remember { mutableStateOf(false) }
            val showTextToggle = content.length > 250
            val displayText = if (showTextToggle && !isExpanded) {
                content.take(220) + "..."
            } else {
                content
            }

            Surface(
                shape = RoundedCornerShape(16.dp, 16.dp, 4.dp, 16.dp),
                color = BrandPrimaryContainer,
                shadowElevation = 0.5.dp,
                modifier = Modifier.widthIn(max = 280.dp) // Giới hạn chiều ngang để bong bóng luôn gọn gàng cân đối
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = displayText,
                        color = BrandOnPrimary,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        modifier = Modifier.align(Alignment.Start)
                    )
                    if (showTextToggle) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isExpanded) "Thu gọn" else "Xem thêm",
                            color = BrandOnPrimary.copy(alpha = 0.8f),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier
                                .clickable { isExpanded = !isExpanded }
                                .align(Alignment.End)
                        )
                    }
                }
            }
        }
    }
}


/**
 * Hiển thị câu trả lời chi tiết từ AI.
 * Tự động phân tích các thẻ [1], [2], ... trong chuỗi văn bản và chèn các huy hiệu số (citation pins) tròn đẹp mắt.
 */
@Composable
fun EduAiDetailedResponse(
    content: String,
    citations: List<SourceCitationUiModel>,
    onSourceClick: (SourceCitationUiModel) -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    val showTextToggle = content.length > 350
    val activeContent = if (showTextToggle && !isExpanded) {
        content.take(300) + "..."
    } else {
        content
    }

    // Sinh bộ InlineTextContent động dựa trên citationOrder thực tế
    val inlineContentMap = remember(citations) {
        citations.associate { citation ->
            "pin_${citation.citationOrder}" to InlineTextContent(
                Placeholder(
                    width = 24.sp,
                    height = 24.sp,
                    placeholderVerticalAlign = PlaceholderVerticalAlign.Center
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE2E8F0)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${citation.citationOrder}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF464555)
                        )
                    }
                }
            }
        }
    }

    // Tự động phân tách và chèn các pin số vào AnnotatedString
    // Định dạng các gạch đầu dòng danh sách (* hoặc -) thành dấu • đẹp mắt
    val formattedContent = remember(activeContent) {
        activeContent.lines().joinToString("\n") { line ->
            val trimmed = line.trimStart()
            if (trimmed.startsWith("* ") || trimmed.startsWith("- ")) {
                val prefix = line.substring(0, line.indexOf(trimmed))
                prefix + "• " + trimmed.substring(2)
            } else {
                line
            }
        }
    }

    // Tự động phân tách, in đậm các chữ nằm giữa ** và chèn các pin số vào AnnotatedString
    val responseText = remember(formattedContent, citations) {
        buildAnnotatedString {
            // Tách theo dấu ** để xác định phần chữ in đậm (bold) và chữ thường
            val boldParts = formattedContent.split("**")
            
            boldParts.forEachIndexed { partIndex, part ->
                val isBold = partIndex % 2 != 0
                val style = if (isBold) SpanStyle(fontWeight = FontWeight.Bold) else SpanStyle()
                
                withStyle(style) {
                    // Regex nâng cấp hỗ trợ gộp, ví dụ: [1], [1, 2], [1,2,3]
                    val citationRegex = Regex("\\[([\\\\d,\\\\s]+)\\]")
                    val matches = citationRegex.findAll(part).toList()
                    val textSegments = part.split(citationRegex)
                    
                    textSegments.forEachIndexed { segmentIndex, segment ->
                        append(segment)
                        if (segmentIndex < matches.size) {
                            val numbersStr = matches[segmentIndex].groupValues[1]
                            val numbers = numbersStr.split(",").mapNotNull { it.trim().toIntOrNull() }
                            
                            if (numbers.isNotEmpty()) {
                                numbers.forEachIndexed { numIdx, num ->
                                    if (citations.any { it.citationOrder == num }) {
                                        appendInlineContent("pin_$num", "[$num]")
                                    } else {
                                        append("[$num]")
                                    }
                                    if (numIdx < numbers.size - 1) append(" ")
                                }
                            } else {
                                append("[$numbersStr]")
                            }
                        }
                    }
                }
            }
        }
    }

    Row(modifier = modifier.fillMaxWidth()) {
        EduAiAvatar()
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            // Text phản hồi
            Text(
                text = responseText,
                inlineContent = inlineContentMap,
                style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
                color = BrandTextPrimary
            )

            if (showTextToggle) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isExpanded) "Thu gọn" else "Xem thêm",
                    color = BrandPrimary,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.clickable { isExpanded = !isExpanded }
                )
            }

            // Danh sách các Card nguồn trích dẫn động
            if (citations.isNotEmpty()) {
                Spacer(modifier = Modifier.height(14.dp))
                citations.forEach { citation ->
                    EduSourceCitationCard(
                        citation = citation,
                        onClick = { onSourceClick(citation) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "TRỢ LÝ EDURAG • 09:01",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                ),
                color = BrandTextSecondary
            )

            if (citations.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "...RAG giúp tối ưu hóa việc truy xuất kiến thức từ các nguồn tài liệu chính thống...",
                    style = MaterialTheme.typography.bodySmall,
                    color = BrandOnSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun BotLoadingBubble() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        EduAiAvatar()
        Spacer(modifier = Modifier.width(12.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(18.dp),
                strokeWidth = 2.dp,
                color = BrandPrimary
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Đang tra cứu tài liệu...",
                style = MaterialTheme.typography.bodyMedium,
                color = BrandTextSecondary
            )
        }
    }
}

@Composable
fun BotErrorCard(
    errorMessage: String,
    onRetry: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(36.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Lỗi",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.errorContainer,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.error)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "Không thể trả lời: $errorMessage",
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(
                    onClick = onRetry,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Thử lại")
                }
            }
        }
    }
}
