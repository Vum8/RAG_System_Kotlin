package com.example.rag_system.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
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
    attachedFile: String?,
    onInputTextChanged: (String) -> Unit,
    onRemoveAttachedFile: () -> Unit,
    onSendClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = BrandSurface,
        border = BorderStroke(1.dp, BrandBorderSubtle),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Hiển thị tài liệu đính kèm nếu có
            AnimatedVisibility(
                visible = attachedFile != null,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                if (attachedFile != null) {
                    Box(
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                            .size(64.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.DarkGray.copy(alpha = 0.8f))
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("📁", fontSize = 24.sp)
                        }
                        // Nút Xóa ảnh/file đính kèm
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(2.dp)
                                .size(18.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.6f))
                                .clickable { onRemoveAttachedFile() },
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
                        .clickable { /* logic đính kèm */ },
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
