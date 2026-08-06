package com.example.rag_system.navigation

import android.content.ContentValues
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.rag_system.data.session.SessionEvent
import com.example.rag_system.data.session.SessionEventBus
import com.example.rag_system.data.session.TokenManager
import com.example.rag_system.data.api.core.ApiResult

import com.example.rag_system.ui.components.CustomToast
import com.example.rag_system.ui.components.LocalToastManager
import com.example.rag_system.ui.components.ToastManager
import com.example.rag_system.ui.components.ToastType
import kotlinx.coroutines.delay
import com.example.rag_system.ui.screens.auth.ForgotPasswordScreen
import com.example.rag_system.ui.screens.auth.LoginScreen
import com.example.rag_system.ui.screens.user.DocumentReaderScreen
import com.example.rag_system.ui.screens.user.MainTabScreen
import com.example.rag_system.ui.screens.user.ProfileScreen
import com.example.rag_system.ui.state.UiLoadState
import com.example.rag_system.ui.viewmodels.AuthViewModel
import com.example.rag_system.ui.viewmodels.ChatViewModel
import com.example.rag_system.ui.viewmodels.DocumentViewModel

/**
 * Hệ thống điều phối định tuyến NavHost của dự án EduRAG.
 * Tích hợp đầy đủ logic xác thực động từ [AuthViewModel] cho các màn hình Login và Profile.
 */
@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val authViewModel = remember { AuthViewModel() }
    val documentViewModel = remember { DocumentViewModel() }

    // Quản lý trạng thái Custom Toast toàn cục
    var toastMessage by remember { mutableStateOf<String?>(null) }
    var toastType by remember { mutableStateOf(ToastType.INFO) }
    var toastTrigger by remember { mutableStateOf(0) }

    val toastManager = remember {
        object : ToastManager {
            override fun showToast(message: String, type: ToastType) {
                toastMessage = message
                toastType = type
                toastTrigger++
            }
        }
    }

    LaunchedEffect(toastTrigger) {
        if (toastMessage != null) {
            delay(3000)
            toastMessage = null
        }
    }

    LaunchedEffect(Unit) {
        SessionEventBus.sessionEvents.collect { event ->
            when (event) {
                is SessionEvent.Unauthorized, is SessionEvent.SessionExpired -> {
                    toastManager.showToast("Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại.", ToastType.ERROR)
                    authViewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }
        }
    }

    CompositionLocalProvider(LocalToastManager provides toastManager) {
        Box(modifier = modifier.fillMaxSize()) {
            NavHost(
                navController = navController,
                startDestination = if (TokenManager.getToken().isNullOrEmpty()) Screen.Login.route else Screen.Chat.route,
                modifier = Modifier.fillMaxSize()
            ) {
                composable(Screen.Login.route) {
                    val loginState by authViewModel.loginState.collectAsState()

                    LaunchedEffect(loginState) {
                        if (loginState is UiLoadState.Success) {
                            toastManager.showToast("Đăng nhập thành công!", ToastType.SUCCESS)
                            authViewModel.resetLoginState()
                            navController.navigate(Screen.Chat.route) {
                                popUpTo(Screen.Login.route) { inclusive = true }
                            }
                        }
                    }

                    LoginScreen(
                        loginState = loginState,
                        onLoginSubmitted = { email, pass, rememberMe ->
                            authViewModel.login(email, pass, rememberMe)
                        },
                        onForgotPasswordClick = {
                            navController.navigate(Screen.ForgotPassword.route)
                        },
                        onRegisterClick = {
                            navController.navigate(Screen.Register.route)
                        }
                    )
                }

                composable(Screen.Register.route) {
                    val registerState by authViewModel.registerState.collectAsState()

                    LaunchedEffect(registerState) {
                        if (registerState is UiLoadState.Success) {
                            toastManager.showToast("Đăng ký thành công!", ToastType.SUCCESS)
                            authViewModel.resetRegisterState()
                            navController.popBackStack()
                        }
                    }

                    com.example.rag_system.ui.screens.auth.RegisterScreen(
                        registerState = registerState,
                        onRegisterSubmitted = { email, pass, name, phone, studentCode, dob ->
                            authViewModel.register(
                                com.example.rag_system.data.api.model.RegisterRequestDto(
                                    email = email,
                                    password = pass,
                                    fullName = name,
                                    phone = phone,
                                    studentCode = studentCode,
                                    dateOfBirth = dob
                                )
                            )
                        },
                        onBackToLoginClick = {
                            navController.popBackStack()
                        }
                    )
                }

                composable(Screen.ForgotPassword.route) {
                    val forgotPasswordState by authViewModel.forgotPasswordState.collectAsState()

                    LaunchedEffect(forgotPasswordState) {
                        if (forgotPasswordState is UiLoadState.Success) {
                            toastManager.showToast("Đã gửi mã xác nhận! Vui lòng kiểm tra email hoặc Log.", ToastType.SUCCESS)
                            authViewModel.resetForgotPasswordState()
                            navController.navigate(Screen.ResetPassword.route) {
                                popUpTo(Screen.Login.route) { inclusive = false }
                            }
                        }
                    }

                    ForgotPasswordScreen(
                        forgotPasswordState = forgotPasswordState,
                        onSendLinkSubmitted = { email ->
                            authViewModel.forgotPassword(email)
                        },
                        onBackToLoginClick = {
                            navController.popBackStack()
                        }
                    )
                }

                composable(Screen.ResetPassword.route) {
                    val resetPasswordState by authViewModel.resetPasswordState.collectAsState()

                    LaunchedEffect(resetPasswordState) {
                        if (resetPasswordState is UiLoadState.Success) {
                            toastManager.showToast("Đặt lại mật khẩu thành công!", ToastType.SUCCESS)
                            authViewModel.resetResetPasswordState()
                            navController.navigate(Screen.Login.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    }

                    com.example.rag_system.ui.screens.auth.ResetPasswordScreen(
                        resetPasswordState = resetPasswordState,
                        onResetPasswordSubmitted = { token, newPassword ->
                            authViewModel.resetPassword(token, newPassword)
                        },
                        onBackToLoginClick = {
                            navController.navigate(Screen.Login.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    )
                }

                composable(Screen.Chat.route) {
                    val chatViewModel = remember { ChatViewModel() }
                    LaunchedEffect(Unit) {
                        documentViewModel.prefetchLastReadDocument(context)
                    }

                    MainTabScreen(
                        chatViewModel = chatViewModel,
                        documentViewModel = documentViewModel,
                        onBackClick = {
                        },
                        onProfileClick = {
                            navController.navigate(Screen.Profile.route)
                        },
                        onDocumentClick = { documentId ->
                            navController.navigate("document_reader_screen/$documentId")
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                composable(Screen.Profile.route) {
                    val profileState by authViewModel.profileState.collectAsState()
                    val avatarState by authViewModel.avatarState.collectAsState()

                    LaunchedEffect(avatarState) {
                        if (avatarState is UiLoadState.Success) {
                            toastManager.showToast("Cập nhật ảnh đại diện thành công", ToastType.SUCCESS)
                            authViewModel.resetAvatarState()
                        } else if (avatarState is UiLoadState.Error) {
                            toastManager.showToast("Lỗi: ${(avatarState as UiLoadState.Error).message}", ToastType.ERROR)
                            authViewModel.resetAvatarState()
                        }
                    }

                    ProfileScreen(
                        profileState = profileState,
                        onReloadProfile = {
                            authViewModel.loadProfile()
                        },
                        onCloseClick = {
                            navController.popBackStack()
                        },
                        onLogoutClick = {
                            authViewModel.logout()
                            navController.navigate(Screen.Login.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        },
                        onUpdateProfile = { name, phone, onComplete ->
                            authViewModel.updateProfile(name, phone) { result ->
                                when (result) {
                                    is ApiResult.Success -> onComplete(true, "Cập nhật thông tin thành công!")
                                    is ApiResult.Error -> onComplete(false, result.error.message)
                                }
                            }
                        },
                        onChangePassword = { current, new, onComplete ->
                            authViewModel.changePassword(current, new) { result ->
                                when (result) {
                                    is ApiResult.Success -> onComplete(true, "Đổi mật khẩu thành công!")
                                    is ApiResult.Error -> onComplete(false, result.error.message)
                                }
                            }
                        },
                        onUploadAvatar = { uri ->
                            val contentResolver = context.contentResolver
                            val mimeType = contentResolver.getType(uri) ?: "image/jpeg"
                            var filename = "avatar.jpg"
                            contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                                val nameIndex = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                                if (cursor.moveToFirst() && nameIndex >= 0) {
                                    filename = cursor.getString(nameIndex)
                                }
                            }
                            val inputStream = contentResolver.openInputStream(uri)
                            if (inputStream != null) {
                                authViewModel.uploadAvatar(inputStream, mimeType, filename, context)
                            } else {
                                toastManager.showToast("Không thể đọc file ảnh", ToastType.ERROR)
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                composable(
                    route = Screen.DocumentReader.route,
                    arguments = listOf(navArgument("documentId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val documentId = backStackEntry.arguments?.getString("documentId") ?: ""
                    DocumentReaderScreen(
                        documentId = documentId,
                        documentTitle = documentViewModel.getDocumentTitleById(documentId),
                        pageContentProvider = { page -> documentViewModel.getPageContent(page) },
                        downloadFileProvider = { ctx, id, progress -> documentViewModel.downloadDocumentFile(ctx, id, progress) },
                        saveHistoryProvider = { ctx, id -> documentViewModel.saveLastReadDocumentId(ctx, id) },
                        onExportClick = { file ->
                            try {
                                val docTitle = documentViewModel.getDocumentTitleById(documentId)
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                                    val contentValues = ContentValues().apply {
                                        put(MediaStore.MediaColumns.DISPLAY_NAME, "EduRAG_${docTitle}.pdf")
                                        put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                                        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                                    }
                                    val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                                    if (uri != null) {
                                        context.contentResolver.openOutputStream(uri)?.use { out ->
                                            file.inputStream().use { input ->
                                                input.copyTo(out)
                                            }
                                        }
                                        toastManager.showToast("Đã lưu vào thư mục Downloads", ToastType.SUCCESS)
                                    } else {
                                        toastManager.showToast("Lỗi tạo file tải xuống", ToastType.ERROR)
                                    }
                                } else {
                                    val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                                    val destFile = java.io.File(downloadsDir, "EduRAG_${docTitle}.pdf")
                                    file.copyTo(destFile, overwrite = true)
                                    toastManager.showToast("Đã lưu vào thư mục Downloads", ToastType.SUCCESS)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                                toastManager.showToast("Không thể lưu tài liệu", ToastType.ERROR)
                            }
                        },
                        onBackClick = {
                            navController.popBackStack()
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            // Overlay Custom Toast
            AnimatedVisibility(
                visible = toastMessage != null,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(bottom = 32.dp)
                    .padding(horizontal = 24.dp)
            ) {
                toastMessage?.let {
                    CustomToast(message = it, type = toastType)
                }
            }
        }
    }
}
