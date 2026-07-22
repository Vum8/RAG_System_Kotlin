package com.example.rag_system.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.rag_system.ui.theme.*

/**
 * Thẻ form đăng ký của EduRAG
 */
@Composable
fun RegisterForm(
    onRegisterSubmitted: (String, String, String, String, String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var fullName by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }
    var studentCode by rememberSaveable { mutableStateOf("") }
    var dob by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(BrandSurface, RoundedCornerShape(12.dp))
            .border(1.dp, BrandBorderSubtle, RoundedCornerShape(12.dp))
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Tạo tài khoản",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                color = BrandTextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Điền thông tin để đăng ký tài khoản sinh viên",
                style = MaterialTheme.typography.bodyMedium,
                color = BrandTextSecondary,
                textAlign = TextAlign.Center
            )
        }

        EduRAGTextField(
            value = fullName,
            onValueChange = { fullName = it },
            placeholderText = "Họ và tên",
            leadingIcon = Icons.Default.Person,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
        )
        EduRAGTextField(
            value = email,
            onValueChange = { email = it },
            placeholderText = "Email sinh viên",
            leadingIcon = Icons.Default.Email,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next)
        )
        EduRAGTextField(
            value = password,
            onValueChange = { password = it },
            placeholderText = "Mật khẩu",
            leadingIcon = Icons.Default.Lock,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next)
        )
        EduRAGTextField(
            value = studentCode,
            onValueChange = { studentCode = it },
            placeholderText = "Mã sinh viên",
            leadingIcon = Icons.Default.AccountBox,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
        )
        EduRAGTextField(
            value = phone,
            onValueChange = { phone = it },
            placeholderText = "Số điện thoại",
            leadingIcon = Icons.Default.Phone,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next)
        )
        EduRAGTextField(
            value = dob,
            onValueChange = { dob = it },
            placeholderText = "Ngày sinh (VD: 2000-01-01)",
            leadingIcon = Icons.Default.DateRange,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
        )

        EduRAGButton(
            text = "Đăng ký",
            onClick = { onRegisterSubmitted(email, password, fullName, phone, studentCode, dob) },
            enabled = email.isNotBlank() && password.isNotBlank() && fullName.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        )
    }
}
