package com.example.rag_system.data.config

object AppConfig {
    /**
     * Cờ cấu hình Mock cho từng phân hệ (Feature-based Mocking):
     * - USE_MOCK_AUTH = false: Phần Xác thực & Tài khoản (Đăng nhập, đăng ký, profile, đổi mật khẩu...) 
     *   đã hoàn thiện API trên Backend -> SỬ DỤNG API THẬT.
     * - USE_MOCK_DOCUMENT = true: Phần Quản lý tài liệu Backend chưa hoàn thiện API -> SỬ DỤNG MOCK.
     * - USE_MOCK_CHAT = true: Phần Chat RAG Backend chưa hoàn thiện API -> SỬ DỤNG MOCK.
     */
    const val USE_MOCK_AUTH = false
    const val USE_MOCK_DOCUMENT = false
    const val USE_MOCK_CHAT = false
}
