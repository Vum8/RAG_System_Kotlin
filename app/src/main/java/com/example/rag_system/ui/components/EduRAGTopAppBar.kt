package com.example.rag_system.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.rag_system.ui.theme.BrandBorderSubtle
import com.example.rag_system.ui.theme.BrandSurface

/**
 * Thanh tiêu đề dùng chung (EduRAGTopAppBar) áp dụng Slot-based API.
 * Tự động chèn statusBarsPadding và đường kẻ phân cách dưới.
 */
@Composable
fun EduRAGTopAppBar(
    modifier: Modifier = Modifier,
    applyStatusBarPadding: Boolean = true, // Cờ kiểm soát có áp dụng statusBarsPadding hay không
    navigationContent: @Composable (RowScope.() -> Unit)? = null,
    centerContent: @Composable (RowScope.() -> Unit)? = null,
    actionContent: @Composable (RowScope.() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .let { if (applyStatusBarPadding) it.statusBarsPadding() else it } // Bật tắt động
            .fillMaxWidth()
            .background(BrandSurface)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 16.dp)
        ) {
            // Phần điều hướng bên trái (Navigation)
            if (navigationContent != null) {
                Row(
                    modifier = Modifier.align(Alignment.CenterStart),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    navigationContent()
                }
            }

            // Phần tiêu đề ở chính giữa (Center)
            if (centerContent != null) {
                Row(
                    modifier = Modifier.align(Alignment.Center),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    centerContent()
                }
            }

            // Phần hành động bên phải (Actions)
            if (actionContent != null) {
                Row(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    actionContent()
                }
            }
        }
        HorizontalDivider(color = BrandBorderSubtle)
    }
}
