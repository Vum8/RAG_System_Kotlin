package com.example.rag_system.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.rag_system.ui.components.*
import com.example.rag_system.ui.theme.BrandAppBackground

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
        topBar = {
            LoginHeader()
        },
        containerColor = BrandAppBackground,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 400.dp)
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // 1. Vùng trang trí Avatar Decor
                LoginDecoration()

                // 2. Thẻ Form đăng nhập nhập liệu
                LoginForm(
                    onLoginSubmitted = onLoginSubmitted,
                    onForgotPasswordClick = onForgotPasswordClick
                )

                // 3. Đăng nhập Google
                SocialLoginButton(
                    onGoogleLoginClick = onGoogleLoginClick
                )

                // 4. Footer hành động phụ
                LoginFooterButton(
                    onRegisterNewDocumentClick = onRegisterNewDocumentClick
                )
            }
        }
    }
}
