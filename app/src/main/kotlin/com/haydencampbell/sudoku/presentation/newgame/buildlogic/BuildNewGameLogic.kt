package com.haydencampbell.sudoku.presentation.newgame.buildlogic

import android.content.Context
import com.haydencampbell.sudoku.util.ProductionDispatcherProvider
import com.haydencampbell.sudoku.data.*
import com.haydencampbell.sudoku.data.repository.GameRepositoryImpl
import com.haydencampbell.sudoku.data.repository.LocalGameStorageImpl
import com.haydencampbell.sudoku.data.repository.LocalSettingsStorageImpl
import com.haydencampbell.sudoku.data.repository.LocalStatisticsStorageImpl
import com.haydencampbell.sudoku.presentation.newgame.NewGameContainer
import com.haydencampbell.sudoku.presentation.newgame.NewGameLogic
import com.haydencampbell.sudoku.presentation.newgame.NewGameViewModel

internal fun buildNewGameLogic(
    container: NewGameContainer,
    viewModel: NewGameViewModel,
    context: Context
): NewGameLogic {
    return NewGameLogic(
        container,
        viewModel,
        GameRepositoryImpl(
            LocalGameStorageImpl(context.filesDir.path),
            LocalSettingsStorageImpl(context.settingsDataStore)
        ),
        LocalStatisticsStorageImpl(
            context.statsDataStore
        ),
        ProductionDispatcherProvider
    )
}