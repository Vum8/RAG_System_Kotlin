package com.example.rag_system.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rag_system.ui.components.*
import com.example.rag_system.ui.theme.*

/**
 * Màn hình Quên mật khẩu chính (ForgotPasswordScreen) lắp ghép các component con độc lập.
 * Stateless UI hoàn toàn, nhận callback sự kiện truyền ra ngoài.
 */
@Composable
fun ForgotPasswordScreen(
    onSendLinkSubmitted: (String) -> Unit,
    onBackToLoginClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        containerColor = BrandAppBackground,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 420.dp)
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Branding Header trung tâm (Đồng bộ màn đăng nhập)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(bottom = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(BrandPrimaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🎓", fontSize = 28.sp)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "EduRAG",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Đặt lại mật khẩu tài khoản học tập",
                        fontSize = 12.sp,
                        color = BrandTextSecondary
                    )
                }

                // 2. Thẻ Form quên mật khẩu nhập email
                ForgotPasswordForm(onSendLinkSubmitted = onSendLinkSubmitted)

                // 3. Chân trang quay lại màn hình đăng nhập dùng chung
                AuthFooterActionRow(
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    text = "Quay lại Đăng nhập",
                    onClick = onBackToLoginClick,
                    showTopDivider = true
                )
            }
        }
    }
}
