package com.example.rag_system.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rag_system.ui.theme.*

/**
 * Thanh chuyển đổi Tab (ProfileTabControl) dạng capsule xám đồng bộ.
 * Hỗ trợ chuyển qua lại giữa "Thông tin cá nhân" (personal) và "Đổi mật khẩu" (password).
 */
@Composable
fun ProfileTabControl(
    selectedTab: String,
    onTabSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(99.dp),
        color = Color(0xFFE2E8F0), // Nền xám nhạt capsule
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Tab 1: Thông tin cá nhân
            TabItem(
                label = "Thông tin cá nhân",
                isActive = selectedTab == "personal",
                onClick = { onTabSelected("personal") },
                modifier = Modifier.weight(1f)
            )

            // Tab 2: Đổi mật khẩu
            TabItem(
                label = "Đổi mật khẩu",
                isActive = selectedTab == "password",
                onClick = { onTabSelected("password") },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun TabItem(
    label: String,
    isActive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(99.dp),
        color = if (isActive) Color.White else Color.Transparent,
        border = if (isActive) BorderStroke(1.dp, BrandBorderSubtle) else null,
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(99.dp))
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                color = if (isActive) BrandPrimary else BrandTextPrimary,
                textAlign = TextAlign.Center
            )
        }
    }
}
