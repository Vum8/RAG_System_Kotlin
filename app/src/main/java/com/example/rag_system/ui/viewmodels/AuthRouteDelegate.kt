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
import java.io.InputStream

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

    private val _avatarState = MutableStateFlow<UiLoadState<Unit>>(UiLoadState.Idle)
    val avatarState: StateFlow<UiLoadState<Unit>> = _avatarState.asStateFlow()

    /** State chứa byte[] blob avatar — UI dùng để hiển thị Bitmap, cần revoke khi dời sang ảnh khác. */
    private val _avatarBytesState = MutableStateFlow<UiLoadState<ByteArray>>(UiLoadState.Idle)
    val avatarBytesState: StateFlow<UiLoadState<ByteArray>> = _avatarBytesState.asStateFlow()

    fun login(email: String, pass: String, rememberMe: Boolean = true) {
        if (email.isBlank() || pass.isBlank()) {
            _loginState.value = UiLoadState.Error("Vui lòng nhập đầy đủ Email và Mật khẩu.")
            return
        }
        scope.launch {
            _loginState.value = UiLoadState.Loading
            when (val result = authRepository.login(email, pass, rememberMe)) {
                is ApiResult.Success -> {
                    _loginState.value = UiLoadState.Success(result.data)
                    // Reset profile states for the new session so it fetches fresh data when opened
                    _profileState.value = UiLoadState.Idle
                    _avatarState.value = UiLoadState.Idle
                    _avatarBytesState.value = UiLoadState.Idle
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

    fun loadProfile(context: android.content.Context? = null) {
        scope.launch {
            _profileState.value = UiLoadState.Loading
            when (val result = authRepository.getProfile()) {
                is ApiResult.Success -> {
                    _profileState.value = UiLoadState.Success(result.data)
                    // Tự động tải blob avatar nếu profile báo có avatar
                    if (result.data.hasAvatar && context != null) {
                        loadMyAvatarBytes(context)
                    } else if (!result.data.hasAvatar) {
                        // Nếu không có, reset bytes state
                        resetAvatarBytesState()
                    }
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
        // Do NOT reset _profileState to Idle here, otherwise ProfileScreen will automatically 
        // try to reload the profile without a token, triggering a 401 and double navigation.
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

    /**
     * Upload ảnh đại diện mới. Sau khi thành công sẽ tự động refresh profile.
     */
    fun uploadAvatar(inputStream: InputStream, mimeType: String, filename: String, context: android.content.Context) {
        scope.launch {
            _avatarState.value = UiLoadState.Loading
            when (val result = authRepository.uploadAvatar(inputStream, mimeType, filename)) {
                is ApiResult.Success -> {
                    _avatarState.value = UiLoadState.Success(Unit)
                    loadMyAvatarBytes(context) // fetch new avatar directly without reloading entire profile to prevent UI flash
                }
                is ApiResult.Error -> {
                    _avatarState.value = UiLoadState.Error(result.error.message, result.error.code)
                }
            }
        }
    }

    /**
     * Xóa ảnh đại diện hiện tại.
     */
    fun deleteAvatar() {
        scope.launch {
            _avatarState.value = UiLoadState.Loading
            when (val result = authRepository.deleteAvatar()) {
                is ApiResult.Success -> {
                    _avatarState.value = UiLoadState.Success(Unit)
                    loadProfile()
                }
                is ApiResult.Error -> {
                    _avatarState.value = UiLoadState.Error(result.error.message, result.error.code)
                }
            }
        }
    }

    fun resetAvatarState() {
        _avatarState.value = UiLoadState.Idle
    }

    /**
     * Tải blob avatar từ GET /api/profile/avatar (Bearer-authenticated).
     * Kết quả lưu trong [avatarBytesState]; UI dùng BitmapFactory.decodeByteArray() để render.
     * Chỉ gọi khi profile.avatarAvailable == true.
     */
    fun loadMyAvatarBytes(context: android.content.Context) {
        scope.launch {
            _avatarBytesState.value = UiLoadState.Loading
            when (val result = authRepository.loadMyAvatarBytes(context)) {
                is ApiResult.Success -> _avatarBytesState.value = UiLoadState.Success(result.data)
                is ApiResult.Error -> _avatarBytesState.value = UiLoadState.Error(result.error.message)
            }
        }
    }

    fun resetAvatarBytesState() {
        _avatarBytesState.value = UiLoadState.Idle
    }
}
