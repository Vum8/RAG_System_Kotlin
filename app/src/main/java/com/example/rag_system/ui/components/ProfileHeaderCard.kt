package com.example.rag_system.ui.components

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rag_system.ui.theme.*

import android.net.Uri
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import androidx.compose.foundation.Image
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.runtime.remember

/**
 * Thẻ thông tin cá nhân trên cùng (ProfileHeaderCard).
 * Hiển thị Avatar tròn lớn, nút sửa ảnh đè lên, Tên, Email, badge vai trò và trạng thái xác thực.
 */
@Composable
fun ProfileHeaderCard(
    userName: String,
    userEmail: String,
    avatarUri: Uri?,
    role: String = "STUDENT",
    onEditAvatarClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
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
        shape = RoundedCornerShape(16.dp),
        color = BrandSurface,
        border = BorderStroke(1.dp, BrandBorderSubtle),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ── Avatar tròn có nút Edit đè góc dưới ──
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .padding(bottom = 8.dp)
            ) {
                // Vòng tròn chứa ảnh đại diện hoặc chữ cái
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(BrandPrimary.copy(alpha = 0.1f))
                        .border(1.5.dp, BrandPrimary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (bitmap != null) {
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "Avatar",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Text(
                            text = userName.firstOrNull()?.toString()?.uppercase() ?: "A",
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandPrimary
                        )
                    }
                }

                // Nút sửa ảnh tròn nhỏ ở góc dưới bên phải
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .border(1.dp, BrandBorderSubtle, CircleShape)
                        .align(Alignment.BottomEnd)
                        .clickable {
                            onEditAvatarClick()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "✏️", fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // ── Tên người dùng ──
            Text(
                text = userName,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = BrandTextPrimary
            )

            Spacer(modifier = Modifier.height(4.dp))

            // ── Email người dùng ──
            Text(
                text = userEmail,
                style = MaterialTheme.typography.bodyMedium,
                color = BrandTextSecondary
            )

            Spacer(modifier = Modifier.height(12.dp))

            // ── Badge vai trò + trạng thái xác thực ──
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Badge vai trò
                val (roleBg, roleText, roleLabel) = when (role.uppercase()) {
                    "TEACHER" -> Triple(Color(0xFFEEF2FF), Color(0xFF3730A3), "🏫 Giảng viên")
                    "ADMIN"   -> Triple(Color(0xFFFFF7ED), Color(0xFFC2410C), "🛡️ Quản trị")
                    else      -> Triple(Color(0xFFEFF6FF), Color(0xFF1D4ED8), "🏫 Sinh viên")
                }
                Surface(
                    shape = RoundedCornerShape(9999.dp),
                    color = roleBg,
                    border = BorderStroke(1.dp, roleText.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = roleLabel,
                        color = roleText,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                // Badge Đã xác thực
                Surface(
                    shape = RoundedCornerShape(9999.dp),
                    color = Color(0xFFE6F4EA),
                    border = BorderStroke(1.dp, Color(0xFF137333))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "✓",
                            color = Color(0xFF137333),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "Đã xác thực",
                            color = Color(0xFF137333),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }
    }
}
