package com.example.rag_system.navigation

import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.rag_system.ui.screens.auth.LoginScreen
import com.example.rag_system.ui.screens.user.ChatScreen
import com.example.rag_system.ui.viewmodels.ChatViewModel

/**
 * Hệ thống điều phối định tuyến NavHost của dự án EduRAG (Global Rules - Bước 6).
 */
@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route, // Khởi chạy từ màn hình đăng nhập trước tiên
        modifier = modifier
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSubmitted = { email, password, rememberMe ->
                    Toast.makeText(context, "Đăng nhập thành công: $email", Toast.LENGTH_SHORT).show()
                    navController.navigate(Screen.Chat.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onForgotPasswordClick = {
                    Toast.makeText(context, "Chức năng quên mật khẩu", Toast.LENGTH_SHORT).show()
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

        composable(Screen.Chat.route) {
            // Khởi tạo ViewModel và quan sát các trạng thái bằng collectAsState
            val chatViewModel = remember { ChatViewModel() }
            val currentChatState by chatViewModel.currentChatState.collectAsState()
            val chatHistoryState by chatViewModel.chatHistoryState.collectAsState()

            ChatScreen(
                currentChatState = currentChatState,
                chatHistoryState = chatHistoryState,
                onSendMessage = { query ->
                    chatViewModel.sendChatQuery(query)
                },
                onBackClick = {
                    Toast.makeText(context, "Quay lại màn hình trước", Toast.LENGTH_SHORT).show()
                },
                onSourceClick = { citation ->
                    Toast.makeText(
                        context,
                        "Mở nguồn trích dẫn:\n${citation.sourceDocumentName} (Trang ${citation.pageNumber})",
                        Toast.LENGTH_LONG
                    ).show()
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        composable(Screen.Library.route) {
            // Hốc hiển thị thư viện tài liệu
        }

        composable(Screen.Profile.route) {
            // Hốc hiển thị hồ sơ cá nhân
        }
    }
}
