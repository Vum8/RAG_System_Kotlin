package com.example.rag_system.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
 * Màn hình Đăng nhập chính (LoginScreen) lắp ghép toàn bộ các component con độc lập.
 * Stateless UI hoàn toàn, nhận callback sự kiện truyền ra ngoài.
 */
@Composable
fun LoginScreen(
    onLoginSubmitted: (String, String, Boolean) -> Unit,
    onForgotPasswordClick: () -> Unit,
    onGoogleLoginClick: () -> Unit,
    onRegisterNewDocumentClick: () -> Unit,
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
                    .widthIn(max = 400.dp)
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Branding Header trung tâm (Logo + Tên ứng dụng phong cách hiện đại)
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
                        text = "Hệ thống tra cứu tài liệu học tập AI",
                        fontSize = 12.sp,
                        color = BrandTextSecondary
                    )
                }

                // 2. Thẻ Form đăng nhập nhập liệu
                LoginForm(
                    onLoginSubmitted = onLoginSubmitted,
                    onForgotPasswordClick = onForgotPasswordClick
                )

                // Đường phân cách HOẶC
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = BrandBorderSubtle
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "HOẶC",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = BrandOutlineVariant
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = BrandBorderSubtle
                    )
                }

                // 3. Đăng nhập Google dùng trực tiếp EduRAGButton
                EduRAGButton(
                    text = "Đăng nhập với Google",
                    onClick = onGoogleLoginClick,
                    variant = EduButtonVariant.GOOGLE,
                    modifier = Modifier.fillMaxWidth(),
                    leadingContent = {
                        Text(
                            text = "G",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = BrandPrimaryContainer
                        )
                    }
                )

                // 4. Footer hành động phụ dùng chung
                AuthFooterActionRow(
                    icon = Icons.Default.AddCircle,
                    text = "Đăng ký tài liệu mới",
                    onClick = onRegisterNewDocumentClick
                )
            }
        }
    }
}
