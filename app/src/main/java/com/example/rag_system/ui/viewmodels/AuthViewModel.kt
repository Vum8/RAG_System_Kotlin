package com.example.rag_system.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.rag_system.data.api.core.ApiResult
import com.example.rag_system.data.repository.AuthRepository
import com.example.rag_system.ui.models.UserUiModel
import com.example.rag_system.ui.state.UiLoadState
import kotlinx.coroutines.flow.StateFlow

/**
 * Facade Coordinator ViewModel cho phân hệ Xác thực và Hồ sơ cá nhân trong EduRAG.
 * Quản lý vòng đời Coroutine an toàn qua `viewModelScope` và ủy quyền xử lý cho [AuthRouteDelegate].
 */
class AuthViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val delegate = AuthRouteDelegate(
        scope = viewModelScope,
        authRepository = authRepository
    )

    val loginState: StateFlow<UiLoadState<UserUiModel>> = delegate.loginState
    val profileState: StateFlow<UiLoadState<UserUiModel>> = delegate.profileState
    val registerState: StateFlow<UiLoadState<UserUiModel>> = delegate.registerState
    val forgotPasswordState: StateFlow<UiLoadState<Unit>> = delegate.forgotPasswordState
    val resetPasswordState: StateFlow<UiLoadState<Unit>> = delegate.resetPasswordState

    fun login(email: String, pass: String) {
        delegate.login(email, pass)
    }

    fun resetLoginState() {
        delegate.resetLoginState()
    }

    fun resetRegisterState() {
        delegate.resetRegisterState()
    }

    fun register(request: com.example.rag_system.data.api.model.RegisterRequestDto) {
        delegate.register(request)
    }

    fun forgotPassword(email: String) {
        delegate.forgotPassword(email)
    }

    fun resetPassword(token: String, newPass: String) {
        delegate.resetPassword(token, newPass)
    }

    fun resetForgotPasswordState() {
        delegate.resetForgotPasswordState()
    }

    fun resetResetPasswordState() {
        delegate.resetResetPasswordState()
    }

    fun loadProfile() {
        delegate.loadProfile()
    }

    fun logout() {
        delegate.logout()
    }

    fun updateProfile(fullName: String, phone: String?, onResult: (ApiResult<UserUiModel>) -> Unit) {
        delegate.updateProfile(fullName, phone, onResult)
    }

    fun changePassword(currentPass: String, newPass: String, onResult: (ApiResult<Unit>) -> Unit) {
        delegate.changePassword(currentPass, newPass, onResult)
    }
}
