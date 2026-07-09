package com.example.rag_system.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.rag_system.ui.theme.BrandSecondaryContainer

/**
 * Thành phần trang trí vòng tròn nền (AuthCircleDecoration) dùng chung cho các màn hình xác thực.
 * Tái sử dụng để hiển thị icon hoặc emoji giữa vòng tròn màu thương hiệu.
 */
@Composable
fun AuthCircleDecoration(
    modifier: Modifier = Modifier,
    size: Dp = 80.dp,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(BrandSecondaryContainer),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}
