package com.example.rag_system.navigation

import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.rag_system.data.session.SessionEvent
import com.example.rag_system.data.session.SessionEventBus
import com.example.rag_system.data.api.core.ApiResult
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

    LaunchedEffect(Unit) {
        SessionEventBus.sessionEvents.collect { event ->
            when (event) {
                is SessionEvent.Unauthorized, is SessionEvent.SessionExpired -> {
                    Toast.makeText(context, "Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại.", Toast.LENGTH_LONG).show()
                    authViewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
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
            val loginState by authViewModel.loginState.collectAsState()

            LaunchedEffect(loginState) {
                if (loginState is UiLoadState.Success) {
                    Toast.makeText(context, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show()
                    authViewModel.resetLoginState()
                    navController.navigate(Screen.Chat.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            }

            LoginScreen(
                loginState = loginState,
                onLoginSubmitted = { email, pass, _ ->
                    authViewModel.login(email, pass)
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
                onRegisterClick = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }

        composable(Screen.Register.route) {
            val registerState by authViewModel.registerState.collectAsState()

            LaunchedEffect(registerState) {
                if (registerState is UiLoadState.Success) {
                    Toast.makeText(context, "Đăng ký thành công!", Toast.LENGTH_SHORT).show()
                    authViewModel.resetRegisterState()
                    navController.popBackStack()
                }
            }

            // RegisterScreen chưa được tạo, nhưng ta sẽ map routing trước
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
                    Toast.makeText(context, "Đã gửi link đặt lại mật khẩu!", Toast.LENGTH_LONG).show()
                    navController.popBackStack()
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

        composable(Screen.Chat.route) {
            val chatViewModel = remember { ChatViewModel() }

            MainTabScreen(
                chatViewModel = chatViewModel,
                onBackClick = {
                    // Không cần hiện Toast khi back
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
                onBackClick = {
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
