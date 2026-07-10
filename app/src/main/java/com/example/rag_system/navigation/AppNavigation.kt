package com.example.rag_system.navigation

import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.rag_system.data.session.SessionEvent
import com.example.rag_system.data.session.SessionEventBus
import com.example.rag_system.ui.screens.auth.ForgotPasswordScreen
import com.example.rag_system.ui.screens.auth.LoginScreen
import com.example.rag_system.ui.screens.user.MainTabScreen
import com.example.rag_system.ui.screens.user.ProfileScreen
import com.example.rag_system.ui.viewmodels.ChatViewModel

/**
 * Hệ thống điều phối định tuyến NavHost của dự án EduRAG.
 * Quản lý 2 tầng điều hướng:
 *   - Tầng 1 (NavHost): chuyển giữa các màn hình lớn (Login → Chat → Profile...)
 *   - Tầng 2 (MainTabScreen/Crossfade): chuyển giữa các Tab trong màn hình chính
 */
@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // ── Lắng nghe SessionEventBus để tự động đăng xuất khi token hết hạn (401) ──
    LaunchedEffect(Unit) {
        SessionEventBus.sessionEvents.collect { event ->
            when (event) {
                is SessionEvent.Unauthorized, is SessionEvent.SessionExpired -> {
                    Toast.makeText(context, "Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại.", Toast.LENGTH_LONG).show()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true } // Xóa toàn bộ back stack, về Login
                    }
                }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route,
        modifier = modifier
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSubmitted = { email, _, _ ->
                    Toast.makeText(context, "Đăng nhập thành công: $email", Toast.LENGTH_SHORT).show()
                    navController.navigate(Screen.Chat.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onForgotPasswordClick = {
                    navController.navigate(Screen.ForgotPassword.route)
                },
                onGoogleLoginClick = {
                    Toast.makeText(context, "Đăng nhập bằng Google thành công!", Toast.LENGTH_SHORT).show()
                    navController.navigate(Screen.Chat.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onRegisterNewDocumentClick = {
                    Toast.makeText(context, "Mở form đăng ký tài liệu mới", Toast.LENGTH_SHORT).show()
                }
            )
        }

        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(
                onSendLinkSubmitted = { email ->
                    Toast.makeText(context, "Đã gửi link đặt lại mật khẩu vào: $email", Toast.LENGTH_LONG).show()
                    navController.popBackStack()
                },
                onBackToLoginClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Chat.route) {
            // ChatViewModel được tạo tại đây và share cho toàn bộ hệ thống Tab bên dưới
            val chatViewModel = remember { ChatViewModel() }

            // MainTabScreen quản lý nội bộ: tab Chat / Lịch sử / Thư viện (không cần route NavHost riêng)
            MainTabScreen(
                chatViewModel = chatViewModel,
                onBackClick = {
                    Toast.makeText(context, "Quay lại màn hình trước", Toast.LENGTH_SHORT).show()
                },
                onProfileClick = {
                    navController.navigate(Screen.Profile.route)
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                onCloseClick = {
                    navController.popBackStack()
                },
                onLogoutClick = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true } // Xóa toàn bộ stack và chuyển về màn hình đăng nhập
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}


