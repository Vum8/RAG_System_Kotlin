package com.example.rag_system.ui.screens.user

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.clip
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rag_system.ui.components.*
import com.example.rag_system.ui.theme.*

/**
 * Màn hình Hồ sơ cá nhân chính (ProfileScreen).
 * Tuân thủ Stateless: Nhận callback onCloseClick từ bên ngoài điều hướng (AppNavigation).
 * Sử dụng Scaffold bao bọc, tích hợp TopAppBar có nút Đóng trái và tiêu đề ở giữa.
 */
@Composable
fun ProfileScreen(
    onCloseClick: () -> Unit,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    var showLogoutDialog by remember { mutableStateOf(false) }

    // ── Quản lý State cho Form thông tin cá nhân ──
    var name by rememberSaveable { mutableStateOf("Nguyễn Văn A") }
    val email = "vanna.nguyen@student.edu.vn"
    val studentId = "B20DCCN123"
    var phone by rememberSaveable { mutableStateOf("0912 345 678") }

    // ── Quản lý State cho Form mật khẩu ──
    var currentPass by rememberSaveable { mutableStateOf("") }
    var newPass by rememberSaveable { mutableStateOf("") }
    var confirmPass by rememberSaveable { mutableStateOf("") }

    // ── Quản lý Tab hiện tại ("personal" hoặc "password") ──
    var selectedTab by rememberSaveable { mutableStateOf("personal") }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = BrandAppBackground,
        topBar = {
            EduRAGTopAppBar(
                navigationContent = {
                    Text(
                        text = "Đóng",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = BrandTextPrimary,
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .clickableNoRipple { onCloseClick() }
                    )
                },
                centerContent = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(BrandPrimary.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🎓", fontSize = 18.sp)
                        }
                        Text(
                            text = "EduRAG",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = BrandPrimary
                        )
                    }
                },
                actionContent = {
                    // [PHƯƠNG ÁN B] Nút Đăng xuất ở góc trên bên phải thanh TopAppBar
                    // Để xóa phương án này: Xóa toàn bộ block actionContent này.
                    Text(
                        text = "Đăng xuất",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = BrandErrorDestructive,
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .clickableNoRipple { showLogoutDialog = true }
                    )
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Thẻ Profile Header Card
            ProfileHeaderCard(
                userName = name,
                userEmail = email
            )

            // Thanh chuyển đổi Tab dạng capsule
            ProfileTabControl(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it }
            )

            // Hiển thị Form tương ứng với Tab được chọn
            if (selectedTab == "personal") {
                ProfilePersonalForm(
                    name = name,
                    onNameChange = { name = it },
                    email = email,
                    studentId = studentId,
                    phone = phone,
                    onPhoneChange = { phone = it }
                )
            } else {
                ProfilePasswordForm(
                    currentPass = currentPass,
                    onCurrentPassChange = { currentPass = it },
                    newPass = newPass,
                    onNewPassChange = { newPass = it },
                    confirmPass = confirmPass,
                    onConfirmPassChange = { confirmPass = it }
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Nút Lưu thay đổi
            EduRAGButton(
                text = "Lưu thay đổi",
                onClick = {
                    if (selectedTab == "personal") {
                        if (name.isBlank() || phone.isBlank()) {
                            Toast.makeText(context, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Cập nhật thông tin thành công!", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        if (currentPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
                            Toast.makeText(context, "Vui lòng điền đủ các trường mật khẩu", Toast.LENGTH_SHORT).show()
                        } else if (newPass != confirmPass) {
                            Toast.makeText(context, "Mật khẩu xác nhận không khớp", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Thay đổi mật khẩu thành công!", Toast.LENGTH_SHORT).show()
                            currentPass = ""
                            newPass = ""
                            confirmPass = ""
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            )

        }
    }

    // Dialog xác nhận đăng xuất dùng chung cho cả 2 phương án
    if (showLogoutDialog) {
        LogoutConfirmDialog(
            onDismiss = { showLogoutDialog = false },
            onConfirm = {
                showLogoutDialog = false
                onLogoutClick()
            }
        )
    }
}

/**
 * Clickable helper không hiển thị hiệu ứng gợn sóng (để giống nút text phẳng Close).
 */
@Composable
private fun Modifier.clickableNoRipple(onClick: () -> Unit): Modifier {
    return this.clickable(
        interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
        indication = null,
        onClick = onClick
    )
}
