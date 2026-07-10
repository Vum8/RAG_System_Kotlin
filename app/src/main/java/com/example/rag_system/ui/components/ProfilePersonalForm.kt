package com.example.rag_system.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rag_system.ui.theme.*

/**
 * Form hiển thị và sửa thông tin cá nhân (ProfilePersonalForm).
 * Gồm: Họ và tên (Editable), Email (Disabled + Icon khóa), MSSV (Disabled + Icon khóa), Số điện thoại (Editable).
 */
@Composable
fun ProfilePersonalForm(
    name: String,
    onNameChange: (String) -> Unit,
    email: String,
    studentId: String,
    phone: String,
    onPhoneChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ── Trường Họ và tên (Disabled) ──
        ProfileInputField(
            label = "Họ và tên",
            value = name,
            onValueChange = {},
            enabled = false,
            trailingIcon = "🔒"
        )

        // ── Trường Email (Disabled) ──
        ProfileInputField(
            label = "Email",
            value = email,
            onValueChange = {},
            enabled = false,
            trailingIcon = "🔒"
        )

        // ── Trường Mã số sinh viên (Disabled) ──
        ProfileInputField(
            label = "Mã số sinh viên",
            value = studentId,
            onValueChange = {},
            enabled = false,
            trailingIcon = "🔒"
        )

        // ── Trường Số điện thoại (Editable) ──
        ProfileInputField(
            label = "Số điện thoại",
            value = phone,
            onValueChange = onPhoneChange,
            enabled = true
        )
    }
}

/**
 * Component input field tùy biến cho Profile, có label ngoài tinh tế.
 * Label chuyển sang màu primary khi ô nhập được focus.
 */
@Composable
private fun ProfileInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean,
    trailingIcon: String? = null,
    modifier: Modifier = Modifier
) {
    var isFocused by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth()) {
        // Label căn lề nhẹ ở trên
        Text(
            text = label,
            fontSize = 13.sp,
            color = if (isFocused && enabled) BrandPrimary else BrandTextPrimary,
            modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .onFocusChanged { focusState ->
                    isFocused = focusState.isFocused
                },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color(0xFFF3F4F6), // Nền xám nhạt cho disabled
                focusedBorderColor = BrandPrimary,
                unfocusedBorderColor = BrandBorderSubtle,
                disabledBorderColor = BrandBorderSubtle,
                focusedTextColor = BrandTextPrimary,
                unfocusedTextColor = BrandTextPrimary,
                disabledTextColor = BrandTextPrimary
            ),
            trailingIcon = if (trailingIcon != null) {
                {
                    Text(
                        text = trailingIcon,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                }
            } else null
        )
    }
}
