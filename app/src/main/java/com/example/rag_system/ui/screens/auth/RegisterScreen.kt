package com.example.rag_system.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
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

@Composable
fun RegisterScreen(
    registerState: UiLoadState<UserUiModel>,
    onRegisterSubmitted: (String, String, String, String, String, String) -> Unit,
    onBackToLoginClick: () -> Unit,
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
                    .widthIn(max = 420.dp)
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
                        text = "Đăng ký tài khoản học tập",
                        fontSize = 12.sp,
                        color = BrandTextSecondary
                    )
                }

                RegisterForm(onRegisterSubmitted = onRegisterSubmitted)

                if (registerState is UiLoadState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(32.dp),
                        color = BrandPrimary
                    )
                } else if (registerState is UiLoadState.Error) {
                    Text(
                        text = registerState.message,
                        color = BrandErrorDestructive,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

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
