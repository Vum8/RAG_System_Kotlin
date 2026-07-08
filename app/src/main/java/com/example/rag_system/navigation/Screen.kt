package com.example.rag_system.navigation

/**
 * Định nghĩa các tuyến đường (routes) và tham số định hướng trong hệ thống định tuyến EduRAG.
 */
sealed class Screen(val route: String) {
    data object Login : Screen("login_screen")
    data object Chat : Screen("chat_screen")
    data object Library : Screen("library_screen")
    data object Profile : Screen("profile_screen")
}
