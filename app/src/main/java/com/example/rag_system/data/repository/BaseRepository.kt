package com.example.rag_system.data.repository

import com.example.rag_system.data.api.core.ApiError
import com.example.rag_system.data.api.core.ApiResult
import com.example.rag_system.data.session.SessionEvent
import com.example.rag_system.data.session.SessionEventBus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Lớp cơ sở (BaseRepository) cho tất cả các Repository trong hệ thống theo kiến trúc MVVM.
 * Cung cấp hàm `safeApiCall` để bọc các lời gọi API, xử lý ngoại lệ mạng,
 * và tự động thông báo cho `SessionEventBus` khi gặp lỗi 401 Unauthorized.
 */
abstract class BaseRepository {

    /**
     * Bọc lời gọi API bất đồng bộ và trả về [ApiResult].
     * Tự động bắt lỗi và chuyển đổi sang [ApiError].
     */
    protected suspend fun <T> safeApiCall(
        apiCall: suspend () -> T
    ): ApiResult<T> = withContext(Dispatchers.IO) {
        try {
            val response = apiCall()
            ApiResult.Success(response)
        } catch (throwable: Throwable) {
            val apiError = parseThrowable(throwable)
            
            // Xử lý tự động khi gặp lỗi 401 Unauthorized theo quy tắc Global Rule
            if (apiError.isUnauthorized) {
                SessionEventBus.tryEmitEvent(SessionEvent.Unauthorized)
            }
            
            ApiResult.Error(apiError)
        }
    }

    private fun parseThrowable(throwable: Throwable): ApiError {
        // Có thể mở rộng để parse HttpException từ Retrofit khi thêm dependency:
        // if (throwable is retrofit2.HttpException) {
        //     val code = throwable.code()
        //     return ApiError(code = code, message = throwable.message(), throwable = throwable)
        // }
        
        // Nhận diện mã lỗi từ reflection (cho phép hoạt động với HttpException của Retrofit/Ktor nếu có)
        val className = throwable::class.java.simpleName
        if (className == "HttpException" || className.contains("ResponseException")) {
            try {
                val codeMethod = throwable::class.java.getMethod("code")
                val code = codeMethod.invoke(throwable) as? Int
                val messageMethod = throwable::class.java.getMethod("message")
                val msg = messageMethod.invoke(throwable) as? String ?: throwable.message ?: "HTTP Error"
                return ApiError(
                    code = code ?: ApiError.UNKNOWN_ERROR_CODE,
                    message = msg,
                    throwable = throwable
                )
            } catch (_: Exception) {
                // Ignore fallback to generic error
            }
        }

        return when (throwable) {
            is SocketTimeoutException -> ApiError(
                code = ApiError.TIMEOUT_ERROR_CODE,
                message = "Kết nối hết hạn, vui lòng kiểm tra lại mạng.",
                throwable = throwable
            )
            is UnknownHostException, is IOException -> ApiError(
                code = ApiError.NETWORK_ERROR_CODE,
                message = "Không thể kết nối đến máy chủ. Vui lòng kiểm tra kết nối internet.",
                throwable = throwable
            )
            else -> ApiError(
                code = ApiError.UNKNOWN_ERROR_CODE,
                message = throwable.message ?: "Đã có lỗi không xác định xảy ra.",
                throwable = throwable
            )
        }
    }
}
