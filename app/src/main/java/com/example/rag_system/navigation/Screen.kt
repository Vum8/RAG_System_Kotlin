package com.example.rag_system.navigation

/**
 * Định nghĩa các tuyến đường (routes) và tham số định hướng trong hệ thống định tuyến EduRAG.
 */
sealed class Screen(val route: String) {
    data object Login : Screen("login_screen")
    data object ForgotPassword : Screen("forgot_password_screen")
    data object Chat : Screen("chat_screen")     // MainTabScreen — quản lý cả 3 tab Chat/Lịch sử/Thư viện
    data object Profile : Screen("profile_screen")
}
