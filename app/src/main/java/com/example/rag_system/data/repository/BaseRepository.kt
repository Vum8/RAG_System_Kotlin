package com.example.rag_system.data.repository

import com.example.rag_system.data.api.core.ApiError
import com.example.rag_system.data.api.core.ApiResult
import com.example.rag_system.data.api.model.BaseApiResponseDto
import com.example.rag_system.data.session.SessionEvent
import com.example.rag_system.data.session.SessionEventBus
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Lớp cơ sở (BaseRepository) cho tất cả các Repository trong hệ thống theo kiến trúc MVVM.
 * Cung cấp hàm `safeApiCall` để bọc các lời gọi API, xử lý ngoại lệ mạng,
 * bóc tách thông báo lỗi JSON từ Backend và tự động phát sự kiện 401 Unauthorized.
 */
abstract class BaseRepository {
    private val gson = Gson()

    /**
     * Bọc lời gọi API bất đồng bộ và trả về [ApiResult].
     * Tự động bắt lỗi và chuyển đổi sang [ApiError].
     */
    protected suspend fun <T> safeApiCall(
        emitUnauthorizedEvent: Boolean = true,
        apiCall: suspend () -> T
    ): ApiResult<T> = withContext(Dispatchers.IO) {
        try {
            val response = apiCall()
            ApiResult.Success(response)
        } catch (throwable: Throwable) {
            val apiError = parseThrowable(throwable)
            
            // Xử lý tự động khi gặp lỗi 401 Unauthorized theo quy tắc Global Rule
            // Chỉ phát sự kiện Unauthorized nếu cờ emitUnauthorizedEvent được bật (tránh xóa form khi đăng nhập sai)
            if (apiError.isUnauthorized && emitUnauthorizedEvent) {
                SessionEventBus.tryEmitEvent(SessionEvent.Unauthorized)
            }
            
            ApiResult.Error(apiError)
        }
    }

    private fun parseThrowable(throwable: Throwable): ApiError {
        if (throwable is HttpException) {
            val code = throwable.code()
            var errorMsg = throwable.message() ?: "Lỗi máy chủ ($code)"
            
            try {
                val errorBodyString = throwable.response()?.errorBody()?.string()
                if (!errorBodyString.isNullOrEmpty()) {
                    val errorDto = gson.fromJson(errorBodyString, BaseApiResponseDto::class.java)
                    if (!errorDto.message.isNullOrEmpty()) {
                        errorMsg = errorDto.message
                    }
                }
            } catch (_: Exception) {
                // Sử dụng errorMsg mặc định nếu parse JSON thất bại
            }

            return ApiError(
                code = code,
                message = errorMsg,
                throwable = throwable
            )
        }

        // Nhận diện mã lỗi từ reflection cho các trường hợp ngoại lệ HTTP khác
        val className = throwable::class.java.simpleName
        if (className.contains("ResponseException")) {
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
