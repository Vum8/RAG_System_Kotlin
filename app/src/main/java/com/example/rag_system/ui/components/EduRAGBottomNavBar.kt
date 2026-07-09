package com.example.rag_system.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rag_system.ui.theme.*

/**
 * Thanh Bottom Navigation Bar của EduRAG (Component Modularization).
 * Hỗ trợ tô đậm làm nổi bật capsule đồng bộ cho tất cả các Tab khi hoạt động.
 */
@Composable
fun EduRAGBottomNavBar(
    currentTab: String,
    onTabSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = BrandSurface,
        border = BorderStroke(1.dp, BrandBorderSubtle),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .navigationBarsPadding()
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Tab 1: Chat
            BottomNavItem(
                label = "Chat",
                iconEmoji = "💬",
                iconVector = null,
                isActive = currentTab == "chat",
                onClick = { onTabSelected("chat") }
            )

            // Tab 2: Lịch sử
            BottomNavItem(
                label = "Lịch sử",
                iconEmoji = null,
                iconVector = Icons.Default.Refresh,
                isActive = currentTab == "history",
                onClick = { onTabSelected("history") }
            )

            // Tab 3: Tài liệu
            BottomNavItem(
                label = "Tài liệu",
                iconEmoji = null,
                iconVector = Icons.AutoMirrored.Filled.List,
                isActive = currentTab == "documents",
                onClick = { onTabSelected("documents") }
            )
        }
    }
}

/**
 * Helper component hiển thị thống nhất cấu trúc cho mỗi Tab dưới dạng Capsule khi active
 */
@Composable
private fun BottomNavItem(
    label: String,
    iconEmoji: String?,
    iconVector: ImageVector?,
    isActive: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = if (isActive) BrandSecondaryContainer else Color.Transparent,
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (iconEmoji != null) {
                Text(
                    text = iconEmoji,
                    fontSize = 16.sp
                )
            } else if (iconVector != null) {
                Icon(
                    imageVector = iconVector,
                    contentDescription = label,
                    tint = if (isActive) BrandOnSecondaryContainer else BrandTextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                color = if (isActive) BrandOnSecondaryContainer else BrandTextSecondary
            )
        }
    }
}
