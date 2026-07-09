package com.example.rag_system.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rag_system.ui.theme.*

/**
 * Định nghĩa các kiểu dáng (variant) nút bấm trong hệ thống.
 */
enum class EduButtonVariant {
    PRIMARY,
    SECONDARY,
    TEXT,
    GOOGLE
}

/**
 * Component Nút bấm hợp nhất duy nhất (EduRAGButton) của dự án.
 * Thiết kế tập trung để dễ dàng cấu hình, tùy biến và mở rộng kiểu dáng toàn cục.
 */
@Composable
fun EduRAGButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: EduButtonVariant = EduButtonVariant.PRIMARY,
    enabled: Boolean = true,
    leadingContent: @Composable (RowScope.() -> Unit)? = null,
    trailingContent: @Composable (RowScope.() -> Unit)? = null,
    height: Dp = 48.dp
) {
    val containerColor = when (variant) {
        EduButtonVariant.PRIMARY -> BrandPrimaryContainer
        EduButtonVariant.SECONDARY -> BrandSurface
        EduButtonVariant.TEXT -> Color.Transparent
        EduButtonVariant.GOOGLE -> BrandSurface
    }

    val contentColor = when (variant) {
        EduButtonVariant.PRIMARY -> Color.White
        EduButtonVariant.SECONDARY -> BrandTextPrimary
        EduButtonVariant.TEXT -> BrandPrimaryContainer
        EduButtonVariant.GOOGLE -> BrandOnSurfaceVariant
    }

    val border = when (variant) {
        EduButtonVariant.SECONDARY -> BorderStroke(1.dp, BrandBorderSubtle)
        EduButtonVariant.GOOGLE -> BorderStroke(1.dp, BrandBorderSubtle)
        else -> null
    }

    val buttonColors = ButtonDefaults.buttonColors(
        containerColor = containerColor,
        contentColor = contentColor,
        disabledContainerColor = if (variant == EduButtonVariant.TEXT) Color.Transparent else BrandOutlineVariant.copy(alpha = 0.2f),
        disabledContentColor = BrandTextSecondary.copy(alpha = 0.5f)
    )

    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(height),
        shape = RoundedCornerShape(8.dp),
        colors = buttonColors,
        border = border,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (leadingContent != null) {
                leadingContent()
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = if (variant == EduButtonVariant.TEXT) 14.sp else 16.sp
                )
            )
            if (trailingContent != null) {
                Spacer(modifier = Modifier.width(8.dp))
                trailingContent()
            }
        }
    }
}
