package com.example.rag_system.ui.components

import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rag_system.data.session.TokenManager
import com.example.rag_system.ui.theme.BrandPrimary
import com.example.rag_system.ui.theme.BrandSurfaceContainerLow

/**
 * Reusable User Avatar Button for App Top Bars.
 * Decodes local persisted avatar Uri offline, showing the custom avatar image or fallback letter "A".
 */
@Composable
fun UserAvatarButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val rawUri by TokenManager.avatarUriFlow.collectAsState()
    val avatarUri = remember(rawUri) { rawUri?.let { Uri.parse(it) } }
    val bitmap = remember(avatarUri) {
        if (avatarUri != null) {
            try {
                if (Build.VERSION.SDK_INT < 28) {
                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(context.contentResolver, avatarUri)
                } else {
                    val source = ImageDecoder.createSource(context.contentResolver, avatarUri)
                    ImageDecoder.decodeBitmap(source)
                }
            } catch (e: Exception) {
                null
            }
        } else {
            null
        }
    }

    Surface(
        shape = CircleShape,
        color = BrandSurfaceContainerLow,
        border = BorderStroke(1.5.dp, BrandPrimary),
        modifier = modifier
            .padding(end = 12.dp)
            .size(36.dp)
            .clickable {
                onClick()
            }
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (bitmap != null) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "User Avatar",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Text(
                    text = "A",
                    fontWeight = FontWeight.Bold,
                    color = BrandPrimary,
                    fontSize = 14.sp
                )
            }
        }
    }
}
