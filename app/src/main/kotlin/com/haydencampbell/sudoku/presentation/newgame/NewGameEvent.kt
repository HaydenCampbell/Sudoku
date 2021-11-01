package com.haydencampbell.sudoku.presentation.newgame

import com.haydencampbell.sudoku.domain.model.Difficulty

sealed class NewGameEvent {
    object OnStart: NewGameEvent()
    data class OnSizeChanged(val boundary: Int): NewGameEvent()
    data class OnDifficultyChanged(val diff: Difficulty): NewGameEvent()
    object OnDonePressed: NewGameEvent()
}