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

/**
 * Thẻ thông tin cá nhân trên cùng (ProfileHeaderCard).
 * Hiển thị Avatar tròn lớn, nút sửa ảnh đè lên, Tên, Email và nhãn Đã xác thực.
 */
@Composable
fun ProfileHeaderCard(
    userName: String,
    userEmail: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

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
                // Vòng tròn chứa chữ cái đại diện
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(BrandPrimary.copy(alpha = 0.1f))
                        .border(1.5.dp, BrandPrimary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = userName.firstOrNull()?.toString()?.uppercase() ?: "A",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandPrimary
                    )
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
                            // Xử lý mở bộ sưu tập/chụp ảnh
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

            // ── Badge Đã xác thực ──
            Surface(
                shape = RoundedCornerShape(9999.dp),
                color = Color(0xFFE6F4EA), // Nền xanh lá nhạt
                border = BorderStroke(1.dp, Color(0xFF137333)) // Viền xanh lá đậm
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
