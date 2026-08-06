package com.example.rag_system.ui.screens.user

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.rag_system.ui.components.*
import com.example.rag_system.ui.models.UserUiModel
import com.example.rag_system.ui.state.UiLoadState
import com.example.rag_system.ui.theme.*
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.PickVisualMediaRequest

import com.example.rag_system.data.session.TokenManager

/**
 * Màn hình Hồ sơ cá nhân (ProfileScreen).
 * Tuân thủ tuyệt đối Stateless UI: Nhận [profileState] và các callback từ bên ngoài (AppNavigation).
 */
@Composable
fun ProfileScreen(
    profileState: UiLoadState<UserUiModel>,
    onReloadProfile: () -> Unit,
    onCloseClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onUpdateProfile: (String, String?, (Boolean, String) -> Unit) -> Unit = { _, _, _ -> },
    onChangePassword: (String, String, (Boolean, String) -> Unit) -> Unit = { _, _, _ -> },
    onUploadAvatar: (Uri) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val toastManager = LocalToastManager.current
    val scrollState = rememberScrollState()
    var showLogoutDialog by remember { mutableStateOf(false) }

    val rawAvatarUri by TokenManager.avatarUriFlow.collectAsState()
    val localAvatarUri = remember(rawAvatarUri) { rawAvatarUri?.let { Uri.parse(it) } }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            onUploadAvatar(uri)
        }
    }

    LaunchedEffect(profileState) {
        if (profileState is UiLoadState.Idle) {
            onReloadProfile()
        }
    }

    var selectedTab by rememberSaveable { mutableStateOf("personal") }
    var currentPass by rememberSaveable { mutableStateOf("") }
    var newPass by rememberSaveable { mutableStateOf("") }
    var confirmPass by rememberSaveable { mutableStateOf("") }

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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (profileState) {
                is UiLoadState.Loading, is UiLoadState.Idle -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = BrandPrimary
                    )
                }
                is UiLoadState.Error -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Lỗi tải thông tin: ${profileState.message}",
                            color = BrandErrorDestructive,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        EduRAGButton(text = "Thử lại", onClick = onReloadProfile)
                    }
                }
                is UiLoadState.Success -> {
                    val user = profileState.data
                    var editableName by rememberSaveable(user) { mutableStateOf(user.name) }
                    var editablePhone by rememberSaveable(user) { mutableStateOf(user.phoneNumber ?: "") }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        ProfileHeaderCard(
                            userName = user.name,
                            userEmail = user.email,
                            avatarUri = localAvatarUri,
                            onEditAvatarClick = {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            }
                        )

                        ProfileTabControl(
                            selectedTab = selectedTab,
                            onTabSelected = { selectedTab = it }
                        )

                        if (selectedTab == "personal") {
                            ProfilePersonalForm(
                                name = editableName,
                                onNameChange = { editableName = it },
                                email = user.email,
                                studentId = user.studentId,
                                phone = editablePhone,
                                onPhoneChange = { editablePhone = it }
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

                        EduRAGButton(
                            text = "Lưu thay đổi",
                            onClick = {
                                if (selectedTab == "personal") {
                                    if (editableName.isBlank()) {
                                        toastManager.showToast("Họ tên không được để trống", ToastType.ERROR)
                                    } else {
                                        onUpdateProfile(editableName, editablePhone.ifBlank { null }) { success, msg ->
                                            toastManager.showToast(msg, if (success) ToastType.SUCCESS else ToastType.ERROR)
                                        }
                                    }
                                } else {
                                    if (currentPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
                                        toastManager.showToast("Vui lòng điền đủ các trường mật khẩu", ToastType.ERROR)
                                    } else if (newPass != confirmPass) {
                                        toastManager.showToast("Mật khẩu xác nhận không khớp", ToastType.ERROR)
                                    } else {
                                        onChangePassword(currentPass, newPass) { success, msg ->
                                            toastManager.showToast(msg, if (success) ToastType.SUCCESS else ToastType.ERROR)
                                            if (success) {
                                                currentPass = ""
                                                newPass = ""
                                                confirmPass = ""
                                            }
                                        }
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp)
                        )
                    }
                }
                else -> {}
            }
        }
    }

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

@Composable
private fun Modifier.clickableNoRipple(onClick: () -> Unit): Modifier {
    return this.clickable(
        interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
        indication = null,
        onClick = onClick
    )
}
