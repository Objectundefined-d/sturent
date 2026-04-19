package com.example.flat_rent_app.presentation.viewmodel.blacklistviewmodel

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BlackListEvent @Inject constructor() {
    private val _events = MutableSharedFlow<Unit>()
    val events: SharedFlow<Unit> = _events

    suspend fun notifyChanged() {
        _events.emit(Unit)
    }
}