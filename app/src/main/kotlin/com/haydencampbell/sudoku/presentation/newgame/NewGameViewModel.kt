package com.haydencampbell.sudoku.presentation.newgame

import com.haydencampbell.sudoku.domain.model.Settings
import com.haydencampbell.sudoku.domain.model.UserStatistics

class NewGameViewModel {
    internal lateinit var settingsState: Settings
    internal lateinit var statisticsState: UserStatistics
    internal var loadingState: Boolean = true
        set(value) {
            field = value
            subLoadingState?.invoke(field)
        }

    internal var subLoadingState: ((Boolean) -> Unit)? = null
}