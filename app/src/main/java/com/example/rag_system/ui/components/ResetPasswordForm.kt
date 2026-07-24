package com.example.rag_system.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.rag_system.ui.theme.*

/**
 * Thẻ form nhập token và mật khẩu mới để đặt lại mật khẩu
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResetPasswordForm(
    onResetPasswordSubmitted: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var token by rememberSaveable { mutableStateOf("") }
    var newPassword by rememberSaveable { mutableStateOf("") }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }

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
                text = "Tạo mật khẩu mới",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = BrandTextPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Nhập mã xác nhận (Token) bạn nhận được và mật khẩu mới",
                style = MaterialTheme.typography.bodyMedium,
                color = BrandTextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }

        // Token
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Mã xác nhận (Token)",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = BrandOnSurfaceVariant
            )
            EduRAGTextField(
                value = token,
                onValueChange = { token = it },
                placeholderText = "VD: user1.abcxyz...",
                leadingIcon = Icons.Default.Edit,
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                )
            )
        }

        // New Password
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Mật khẩu mới",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = BrandOnSurfaceVariant
            )
            EduRAGTextField(
                value = newPassword,
                onValueChange = { newPassword = it },
                placeholderText = "Tối thiểu 6 ký tự",
                leadingIcon = Icons.Default.Lock,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = {
                    if (token.isNotBlank() && newPassword.isNotBlank()) {
                        onResetPasswordSubmitted(token, newPassword)
                    }
                }),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Text(if (passwordVisible) "Ẩn" else "Hiện", style = MaterialTheme.typography.labelSmall)
                    }
                }
            )
        }

        EduRAGButton(
            text = "Xác nhận đặt lại",
            onClick = { onResetPasswordSubmitted(token, newPassword) },
            enabled = token.isNotBlank() && newPassword.isNotBlank(),
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
