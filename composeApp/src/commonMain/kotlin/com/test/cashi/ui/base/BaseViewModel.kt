package com.test.cashi.ui.base

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

/**
 * Base ViewModel with UIAction/UIState pattern
 *
 * This pattern provides:
 * - Clear separation between UI actions and state
 * - Single source of truth for UI state
 * - Type-safe event handling
 * - Easier testing and debugging
 *
 * @param UIAction Sealed class representing all possible UI actions
 * @param UIState Data class representing the complete UI state
 * @param defaultState Initial state of the UI
 */
abstract class BaseViewModel<UIAction, UIState>(defaultState: UIState) : ViewModel() {

    /**
     * Flow of UI actions (one-time events like navigation, showing snackbar)
     */
    val actions = MutableSharedFlow<UIAction>()

    /**
     * Current UI state (survives configuration changes)
     */
    var state by mutableStateOf(defaultState)
        protected set

    /**
     * Dispatch a one-time UI action
     * Use for events that should only be handled once (navigation, toasts, etc.)
     */
    protected fun dispatchAction(action: UIAction) {
        viewModelScope.launch { actions.emit(action) }
    }
}