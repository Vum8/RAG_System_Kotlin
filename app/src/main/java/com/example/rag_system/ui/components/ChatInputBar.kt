package com.example.rag_system.ui.components

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rag_system.ui.theme.*

/**
 * Thanh nhập liệu hội thoại (ChatInputBar) theo chuẩn Component Modularization.
 */
@Composable
fun ChatInputBar(
    inputText: String,
    attachedFileNames: List<String>,
    attachedFileUris: List<Uri>,
    onInputTextChanged: (String) -> Unit,
    onRemoveAttachedFile: (Int) -> Unit,
    onSendClick: () -> Unit,
    onAttachClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = BrandSurface,
        border = BorderStroke(1.dp, BrandBorderSubtle),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Danh sách tài liệu/hình ảnh đính kèm dạng hàng ngang LazyRow cuộn
            if (attachedFileNames.isNotEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        itemsIndexed(attachedFileNames) { index, fileName ->
                            val uri = attachedFileUris.getOrNull(index)
                            AttachmentPreviewItem(
                                fileName = fileName,
                                fileUri = uri,
                                onRemove = { onRemoveAttachedFile(index) }
                            )
                        }
                    }

                    // Nếu có nhiều hơn 2 tệp, hiển thị chỉ báo vuốt ngang ở góc
                    if (attachedFileNames.size > 2) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color.Black.copy(alpha = 0.6f),
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "↔",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "${attachedFileNames.size} tệp",
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    fontSize = 9.sp
                                )
                            }
                        }
                    }
                }
            }

            // Thanh nhập câu hỏi bo tròn
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BrandAppBackground, RoundedCornerShape(24.dp))
                    .border(1.dp, BrandBorderSubtle, RoundedCornerShape(24.dp))
                    .padding(horizontal = 14.dp, vertical = 2.dp)
            ) {
                // Nút đính kèm dạng kẹp giấy
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { onAttachClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Text("📎", fontSize = 18.sp)
                }
                Spacer(modifier = Modifier.width(8.dp))
                TextField(
                    value = inputText,
                    onValueChange = onInputTextChanged,
                    placeholder = {
                        Text(
                            text = "Nhập câu hỏi của bạn...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = BrandTextSecondary
                        )
                    },
                    modifier = Modifier.weight(1f),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(onSend = { onSendClick() }),
                    singleLine = true
                )
                Spacer(modifier = Modifier.width(6.dp))

                // Nút Send màu xanh tím dạng tròn
                Surface(
                    shape = CircleShape,
                    color = BrandPrimary,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .clickable { onSendClick() }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Gửi",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Ô vuông hiển thị xem trước tệp đính kèm (72.dp) ở thanh nhập liệu
 */
@Composable
private fun AttachmentPreviewItem(
    fileName: String,
    fileUri: Uri?,
    onRemove: () -> Unit
) {
    val context = LocalContext.current
    val bitmap = remember(fileUri) {
        if (fileUri != null) {
            try {
                context.contentResolver.openInputStream(fileUri)?.use {
                    BitmapFactory.decodeStream(it)?.asImageBitmap()
                }
            } catch (e: Exception) {
                null
            }
        } else {
            null
        }
    }

    Box(
        modifier = Modifier
            .size(72.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(BrandSurfaceContainerLow)
            .border(1.dp, BrandBorderSubtle, RoundedCornerShape(8.dp))
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap,
                contentDescription = "Preview",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = if (fileName.endsWith(".pdf", ignoreCase = true)) "📄" else "📁",
                    fontSize = 22.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = fileName,
                    style = MaterialTheme.typography.labelSmall,
                    color = BrandTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        // Nút Xóa nhỏ ở góc trên bên phải
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(4.dp)
                .size(16.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.6f))
                .clickable { onRemove() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Xóa",
                tint = Color.White,
                modifier = Modifier.size(10.dp)
            )
        }
    }
}

