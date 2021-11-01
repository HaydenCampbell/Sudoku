package com.haydencampbell.sudoku.presentation.activegame.buildlogic

import android.content.Context
import com.haydencampbell.sudoku.util.ProductionDispatcherProvider
import com.haydencampbell.sudoku.data.*
import com.haydencampbell.sudoku.data.repository.GameRepositoryImpl
import com.haydencampbell.sudoku.data.repository.LocalGameStorageImpl
import com.haydencampbell.sudoku.data.repository.LocalSettingsStorageImpl
import com.haydencampbell.sudoku.data.repository.LocalStatisticsStorageImpl
import com.haydencampbell.sudoku.data.settingsDataStore
import com.haydencampbell.sudoku.presentation.activegame.ActiveGameContainer
import com.haydencampbell.sudoku.presentation.activegame.ActiveGameLogic
import com.haydencampbell.sudoku.presentation.activegame.ActiveGameViewModel

internal fun buildActiveGameLogic(
    container: ActiveGameContainer,
    viewModel: ActiveGameViewModel,
    context: Context
): ActiveGameLogic {
    return ActiveGameLogic(
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