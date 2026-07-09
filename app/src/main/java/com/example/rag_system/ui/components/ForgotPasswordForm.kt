package com.example.rag_system.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.rag_system.ui.theme.*

/**
 * Thẻ form nhập email để yêu cầu đặt lại mật khẩu (ForgotPasswordForm) theo chuẩn Component Modularization.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordForm(
    onSendLinkSubmitted: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var email by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(BrandSurface, RoundedCornerShape(12.dp))
            .border(1.dp, BrandBorderSubtle, RoundedCornerShape(12.dp))
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Tiêu đề card
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Quên mật khẩu?",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = BrandTextPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Nhập email của bạn để nhận liên kết đặt lại mật khẩu",
                style = MaterialTheme.typography.bodyMedium,
                color = BrandTextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }

        // Trường nhập email
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Email sinh viên",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = BrandOnSurfaceVariant
            )
            EduRAGTextField(
                value = email,
                onValueChange = { email = it },
                placeholderText = "Nhập email sinh viên",
                leadingIcon = Icons.Default.Email,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = {
                    if (email.isNotBlank()) {
                        onSendLinkSubmitted(email)
                    }
                })
            )
        }

        // Nút gửi link đặt lại dùng EduRAGButton chung
        EduRAGButton(
            text = "Gửi link đặt lại",
            onClick = { onSendLinkSubmitted(email) },
            enabled = email.isNotBlank(),
            modifier = Modifier.fillMaxWidth(),
            height = 52.dp,
            trailingContent = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }
        )
    }
}
