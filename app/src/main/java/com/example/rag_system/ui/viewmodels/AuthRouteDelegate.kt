package com.example.rag_system.ui.viewmodels

import com.example.rag_system.data.api.core.ApiResult
import com.example.rag_system.data.repository.AuthRepository
import com.example.rag_system.ui.models.UserUiModel
import com.example.rag_system.ui.state.UiLoadState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Route Delegate chuyên trách xử lý nghiệp vụ xác thực và thông tin cá nhân (Auth/Profile) của Sinh viên EduRAG.
 */
class AuthRouteDelegate(
    private val scope: CoroutineScope,
    private val authRepository: AuthRepository = AuthRepository()
) {
    private val _loginState = MutableStateFlow<UiLoadState<UserUiModel>>(UiLoadState.Idle)
    val loginState: StateFlow<UiLoadState<UserUiModel>> = _loginState.asStateFlow()

    private val _profileState = MutableStateFlow<UiLoadState<UserUiModel>>(UiLoadState.Idle)
    val profileState: StateFlow<UiLoadState<UserUiModel>> = _profileState.asStateFlow()

    private val _registerState = MutableStateFlow<UiLoadState<UserUiModel>>(UiLoadState.Idle)
    val registerState: StateFlow<UiLoadState<UserUiModel>> = _registerState.asStateFlow()

    private val _forgotPasswordState = MutableStateFlow<UiLoadState<Unit>>(UiLoadState.Idle)
    val forgotPasswordState: StateFlow<UiLoadState<Unit>> = _forgotPasswordState.asStateFlow()

    private val _resetPasswordState = MutableStateFlow<UiLoadState<Unit>>(UiLoadState.Idle)
    val resetPasswordState: StateFlow<UiLoadState<Unit>> = _resetPasswordState.asStateFlow()

    fun login(email: String, pass: String) {
        if (email.isBlank() || pass.isBlank()) {
            _loginState.value = UiLoadState.Error("Vui lòng nhập đầy đủ Email và Mật khẩu.")
            return
        }
        scope.launch {
            _loginState.value = UiLoadState.Loading
            when (val result = authRepository.login(email, pass)) {
                is ApiResult.Success -> {
                    _loginState.value = UiLoadState.Success(result.data)
                }
                is ApiResult.Error -> {
                    _loginState.value = UiLoadState.Error(
                        message = result.error.message,
                        code = result.error.code
                    )
                }
            }
        }
    }

    fun resetLoginState() {
        _loginState.value = UiLoadState.Idle
    }

    fun resetRegisterState() {
        _registerState.value = UiLoadState.Idle
    }

    fun register(request: com.example.rag_system.data.api.model.RegisterRequestDto) {
        scope.launch {
            _registerState.value = UiLoadState.Loading
            when (val result = authRepository.register(request)) {
                is ApiResult.Success -> {
                    _registerState.value = UiLoadState.Success(result.data)
                }
                is ApiResult.Error -> {
                    _registerState.value = UiLoadState.Error(
                        message = result.error.message,
                        code = result.error.code
                    )
                }
            }
        }
    }

    fun forgotPassword(email: String) {
        if (email.isBlank()) {
            _forgotPasswordState.value = UiLoadState.Error("Vui lòng nhập Email.")
            return
        }
        scope.launch {
            _forgotPasswordState.value = UiLoadState.Loading
            when (val result = authRepository.forgotPassword(email)) {
                is ApiResult.Success -> {
                    _forgotPasswordState.value = UiLoadState.Success(Unit)
                }
                is ApiResult.Error -> {
                    _forgotPasswordState.value = UiLoadState.Error(result.error.message, result.error.code)
                }
            }
        }
    }

    fun resetPassword(token: String, newPass: String) {
        if (token.isBlank() || newPass.isBlank()) {
            _resetPasswordState.value = UiLoadState.Error("Vui lòng nhập Token và Mật khẩu mới.")
            return
        }
        scope.launch {
            _resetPasswordState.value = UiLoadState.Loading
            when (val result = authRepository.resetPassword(token, newPass)) {
                is ApiResult.Success -> {
                    _resetPasswordState.value = UiLoadState.Success(Unit)
                }
                is ApiResult.Error -> {
                    _resetPasswordState.value = UiLoadState.Error(result.error.message, result.error.code)
                }
            }
        }
    }
    fun resetForgotPasswordState() {
        _forgotPasswordState.value = UiLoadState.Idle
    }

    fun resetResetPasswordState() {
        _resetPasswordState.value = UiLoadState.Idle
    }

    fun loadProfile() {
        scope.launch {
            _profileState.value = UiLoadState.Loading
            when (val result = authRepository.getProfile()) {
                is ApiResult.Success -> {
                    _profileState.value = UiLoadState.Success(result.data)
                }
                is ApiResult.Error -> {
                    _profileState.value = UiLoadState.Error(
                        message = result.error.message,
                        code = result.error.code
                    )
                }
            }
        }
    }

    fun logout() {
        authRepository.logout()
        _loginState.value = UiLoadState.Idle
        _profileState.value = UiLoadState.Idle
    }

    fun updateProfile(fullName: String, phone: String?, onResult: (ApiResult<UserUiModel>) -> Unit) {
        scope.launch {
            val result = authRepository.updateProfile(fullName, phone)
            if (result is ApiResult.Success) {
                _profileState.value = UiLoadState.Success(result.data)
            }
            onResult(result)
        }
    }

    fun changePassword(currentPass: String, newPass: String, onResult: (ApiResult<Unit>) -> Unit) {
        scope.launch {
            val result = authRepository.changePassword(currentPass, newPass)
            onResult(result)
        }
    }
}
