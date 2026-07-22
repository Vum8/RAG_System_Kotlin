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

    private var sharedPreferences: SharedPreferences? = null

    /**
     * Khởi tạo TokenManager với Application Context.
     */
    fun init(context: Context) {
        if (sharedPreferences == null) {
            sharedPreferences = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        }
    }

    /**
     * Lưu trữ Token JWT sau khi đăng nhập thành công.
     */
    fun saveToken(token: String?) {
        sharedPreferences?.edit()?.apply {
            if (token.isNullOrEmpty()) {
                remove(KEY_JWT_TOKEN)
            } else {
                putString(KEY_JWT_TOKEN, token)
            }
            apply()
        }
    }

    /**
     * Lấy Token JWT hiện tại nếu có.
     */
    fun getToken(): String? {
        return sharedPreferences?.getString(KEY_JWT_TOKEN, null)
    }

    /**
     * Xóa Token khi đăng xuất hoặc khi nhận sự kiện 401 Unauthorized.
     */
    fun clearToken() {
        saveToken(null)
    }
}
