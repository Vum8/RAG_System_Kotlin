package com.example.rag_system.ui.state

/**
 * Lớp đại diện cho các trạng thái tải bất đồng bộ của UI trong kiến trúc MVVM.
 * Bao gồm 5 trạng thái theo quy tắc Global Rule: Idle, Loading, Success, Error, Empty.
 */
sealed interface UiLoadState<out T> {
    data object Idle : UiLoadState<Nothing>
    data object Loading : UiLoadState<Nothing>
    data class Success<out T>(val data: T) : UiLoadState<T>
    data class Error(val message: String, val code: Int? = null) : UiLoadState<Nothing>
    data object Empty : UiLoadState<Nothing>

    val isLoading: Boolean
        get() = this is Loading

    val isSuccess: Boolean
        get() = this is Success

    val isError: Boolean
        get() = this is Error

    val isEmpty: Boolean
        get() = this is Empty

    val dataOrNull: T?
        get() = (this as? Success)?.data
}
