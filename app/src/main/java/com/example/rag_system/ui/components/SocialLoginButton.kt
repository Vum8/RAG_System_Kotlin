package com.example.rag_system.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.rag_system.ui.theme.*

/**
 * Thành phần phân cách HOẶC và nút Đăng nhập nhanh bằng Google (SocialLoginButton).
 */
@Composable
fun SocialLoginButton(
    onGoogleLoginClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
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

        // Nút Đăng nhập Google
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = BrandSurface,
            border = BorderStroke(1.dp, BrandBorderSubtle),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clickable { onGoogleLoginClick() }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                // Ký tự G màu xanh thương hiệu đại diện cho Google
                Text(
                    text = "G",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = BrandPrimaryContainer,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = "Đăng nhập với Google",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = BrandOnSurfaceVariant
                )
            }
        }
    }
}
