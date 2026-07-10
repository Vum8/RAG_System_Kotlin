package com.example.rag_system.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rag_system.ui.theme.*

/**
 * Form đổi mật khẩu (ProfilePasswordForm).
 * Gồm: Mật khẩu hiện tại, Mật khẩu mới, Xác nhận mật khẩu mới.
 * Hỗ trợ nút ẩn/hiện mật khẩu động.
 */
@Composable
fun ProfilePasswordForm(
    currentPass: String,
    onCurrentPassChange: (String) -> Unit,
    newPass: String,
    onNewPassChange: (String) -> Unit,
    confirmPass: String,
    onConfirmPassChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ── Mật khẩu hiện tại ──
        ProfilePasswordInputField(
            label = "Mật khẩu hiện tại",
            value = currentPass,
            onValueChange = onCurrentPassChange
        )

        // ── Mật khẩu mới ──
        ProfilePasswordInputField(
            label = "Mật khẩu mới",
            value = newPass,
            onValueChange = onNewPassChange
        )

        // ── Xác nhận mật khẩu mới ──
        ProfilePasswordInputField(
            label = "Xác nhận mật khẩu mới",
            value = confirmPass,
            onValueChange = onConfirmPassChange
        )
    }
}

/**
 * Component input field cho mật khẩu trong Profile, có nút ẩn/hiện mắt.
 */
@Composable
private fun ProfilePasswordInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isFocused by remember { mutableStateOf(false) }
    var isVisible by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = if (isFocused) BrandPrimary else BrandTextPrimary,
            modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .onFocusChanged { focusState ->
                    isFocused = focusState.isFocused
                },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = BrandPrimary,
                unfocusedBorderColor = BrandBorderSubtle,
                focusedTextColor = BrandTextPrimary,
                unfocusedTextColor = BrandTextPrimary
            ),
            trailingIcon = {
                Text(
                    text = if (isVisible) "👁️" else "🙈",
                    fontSize = 16.sp,
                    modifier = Modifier
                        .padding(end = 4.dp)
                        .clickable { isVisible = !isVisible }
                )
            }
        )
    }
}
