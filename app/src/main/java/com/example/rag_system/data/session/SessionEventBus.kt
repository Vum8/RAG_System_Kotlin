package com.example.rag_system.data.session

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * EventBus quản lý các sự kiện liên quan đến phiên làm việc (Session),
 * ví dụ: thông báo lỗi Unauthorized (401) để tự động điều hướng đăng xuất tại AppNavigation.
 */
object SessionEventBus {
    private val _sessionEvents = MutableSharedFlow<SessionEvent>(extraBufferCapacity = 1)
    val sessionEvents: SharedFlow<SessionEvent> = _sessionEvents.asSharedFlow()

    suspend fun emitEvent(event: SessionEvent) {
        _sessionEvents.emit(event)
    }

    fun tryEmitEvent(event: SessionEvent): Boolean {
        return _sessionEvents.tryEmit(event)
    }
}

sealed interface SessionEvent {
    data object Unauthorized : SessionEvent
    data object SessionExpired : SessionEvent
}
