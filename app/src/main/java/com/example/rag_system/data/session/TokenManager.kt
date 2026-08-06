package com.example.rag_system.data.session

import android.content.Context
import android.content.SharedPreferences

/**
 * Quản lý lưu trữ cục bộ (SharedPreferences) cho JWT Auth Token của hệ thống EduRAG.
 * Giúp đính kèm tự động token vào các lời gọi API thông qua Retrofit Interceptor.
 */
object TokenManager {
    private const val PREFS_NAME = "edurag_auth_prefs"
    private const val KEY_JWT_TOKEN = "jwt_token"
    private const val KEY_LOCAL_AVATAR_URI = "local_avatar_uri"

    private val _avatarUriFlow = kotlinx.coroutines.flow.MutableStateFlow<String?>(null)
    val avatarUriFlow: kotlinx.coroutines.flow.StateFlow<String?> = _avatarUriFlow

    private var sharedPreferences: SharedPreferences? = null

    /**
     * Khởi tạo TokenManager với Application Context.
     */
    fun init(context: Context) {
        if (sharedPreferences == null) {
            sharedPreferences = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            _avatarUriFlow.value = getLocalAvatarUri()
        }
    }

    private var memoryToken: String? = null

    /**
     * Lưu trữ Token JWT sau khi đăng nhập thành công.
     */
    fun saveToken(token: String?, rememberMe: Boolean = true) {
        if (rememberMe) {
            sharedPreferences?.edit()?.apply {
                if (token.isNullOrEmpty()) {
                    remove(KEY_JWT_TOKEN)
                } else {
                    putString(KEY_JWT_TOKEN, token)
                }
                apply()
            }
        } else {
            memoryToken = token
            // Ensure it's removed from persistent storage if rememberMe is false
            sharedPreferences?.edit()?.remove(KEY_JWT_TOKEN)?.apply()
        }
    }

    /**
     * Lấy Token JWT hiện tại nếu có.
     */
    fun getToken(): String? {
        return memoryToken ?: sharedPreferences?.getString(KEY_JWT_TOKEN, null)
    }

    /**
     * Lưu trữ đường dẫn ảnh đại diện cục bộ.
     */
    fun saveLocalAvatarUri(uri: String?) {
        sharedPreferences?.edit()?.apply {
            if (uri.isNullOrEmpty()) {
                remove(KEY_LOCAL_AVATAR_URI)
                _avatarUriFlow.value = null
            } else {
                putString(KEY_LOCAL_AVATAR_URI, uri)
                // Appending timestamp ensures Compose sees it as a new URI state if the file content changed but path is same.
                _avatarUriFlow.value = uri + "?t=" + System.currentTimeMillis()
            }
            apply()
        }
    }

    /**
     * Lấy đường dẫn ảnh đại diện cục bộ hiện tại nếu có.
     */
    fun getLocalAvatarUri(): String? {
        return sharedPreferences?.getString(KEY_LOCAL_AVATAR_URI, null)
    }

    /**
     * Xóa Token khi đăng xuất hoặc khi nhận sự kiện 401 Unauthorized.
     */
    fun clearToken() {
        memoryToken = null
        saveToken(null, true)
        saveLocalAvatarUri(null)
    }
}
