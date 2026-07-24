package com.example.rag_system.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
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
import com.example.rag_system.ui.models.UserUiModel
import com.example.rag_system.ui.state.UiLoadState
import com.example.rag_system.ui.theme.*

/**
 * Màn hình Đăng nhập (LoginScreen) lắp ghép các component con độc lập.
 * Stateless UI hoàn toàn, quan sát [loginState] và phát sự kiện ra bên ngoài.
 */
@Composable
fun LoginScreen(
    loginState: UiLoadState<UserUiModel>,
    onLoginSubmitted: (String, String, Boolean) -> Unit,
    onForgotPasswordClick: () -> Unit,
    onRegisterClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Scaffold(
        containerColor = BrandAppBackground,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 400.dp)
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(bottom = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(BrandPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🎓", fontSize = 32.sp)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "EduRAG",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Hệ thống tra cứu tài liệu học tập AI",
                        fontSize = 12.sp,
                        color = BrandTextSecondary
                    )
                }

                LoginForm(
                    onLoginSubmitted = onLoginSubmitted,
                    onForgotPasswordClick = onForgotPasswordClick
                )

                if (loginState is UiLoadState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(32.dp),
                        color = BrandPrimary
                    )
                } else if (loginState is UiLoadState.Error) {
                    Text(
                        text = loginState.message,
                        color = BrandErrorDestructive,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                AuthFooterActionRow(
                    icon = Icons.Default.AddCircle,
                    text = "Đăng ký tài khoản mới",
                    onClick = onRegisterClick
                )
            }
        }
    }
}
