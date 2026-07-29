package com.example.rag_system.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rag_system.ui.theme.*

enum class ToastType {
    INFO, SUCCESS, ERROR
}

interface ToastManager {
    fun showToast(message: String, type: ToastType = ToastType.INFO)
}

val LocalToastManager = staticCompositionLocalOf<ToastManager> {
    object : ToastManager {
        override fun showToast(message: String, type: ToastType) {}
    }
}

/**
 * Custom Toast layout with premium design.
 */
@Composable
fun CustomToast(
    message: String,
    type: ToastType,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when (type) {
        ToastType.SUCCESS -> Color(0xFFE8F5E9)
        ToastType.ERROR -> Color(0xFFFFEBEE)
        ToastType.INFO -> BrandSurfaceContainerLow
    }

    val borderColor = when (type) {
        ToastType.SUCCESS -> Color(0xFF4CAF50)
        ToastType.ERROR -> BrandErrorDestructive
        ToastType.INFO -> BrandPrimary
    }

    val iconColor = when (type) {
        ToastType.SUCCESS -> Color(0xFF2E7D32)
        ToastType.ERROR -> Color(0xFFC62828)
        ToastType.INFO -> BrandPrimary
    }

    val icon = when (type) {
        ToastType.SUCCESS -> Icons.Default.CheckCircle
        ToastType.ERROR -> Icons.Default.Warning
        ToastType.INFO -> Icons.Default.Info
    }

    Row(
        modifier = modifier
            .shadow(8.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = message,
            color = BrandTextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f, fill = false)
        )
    }
}
