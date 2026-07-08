package com.example.rag_system.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.rag_system.ui.theme.BrandPrimary
import com.example.rag_system.ui.theme.BrandSecondaryContainer

/**
 * Thành phần trang trí avatar tròn ở đầu thẻ Login.
 */
@Composable
fun LoginDecoration(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(96.dp)
            .clip(CircleShape)
            .background(BrandSecondaryContainer),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = "Avatar Decor",
            tint = BrandPrimary,
            modifier = Modifier.size(64.dp)
        )
    }
}
