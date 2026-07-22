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
    onInputTextChanged: (String) -> Unit,
    onSendClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = BrandSurface,
        border = BorderStroke(1.dp, BrandBorderSubtle),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Thanh nhập câu hỏi bo tròn
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BrandAppBackground, RoundedCornerShape(24.dp))
                    .border(1.dp, BrandBorderSubtle, RoundedCornerShape(24.dp))
                    .padding(horizontal = 14.dp, vertical = 2.dp)
            ) {
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


