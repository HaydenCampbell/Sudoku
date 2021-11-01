package com.haydencampbell.sudoku.presentation.newgame

import com.haydencampbell.sudoku.util.BaseLogic
import com.haydencampbell.sudoku.util.DispatcherProvider
import com.haydencampbell.sudoku.domain.repository.IGameRepository
import com.haydencampbell.sudoku.domain.repository.IStatisticsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class NewGameLogic(
    private val container: NewGameContainer?,
    private val viewModel: NewGameViewModel,
    private val gameRepo: IGameRepository,
    private val statsRepo: IStatisticsRepository,
    private val dispatcher: DispatcherProvider
) : BaseLogic<NewGameEvent>(),
    CoroutineScope {

    init {
        jobTracker = Job()
    }

    override val coroutineContext: CoroutineContext
        get() = dispatcher.provideUIContext() + jobTracker

    override fun onEvent(event: NewGameEvent) {
        when (event) {
            is NewGameEvent.OnStart -> onStart()
            is NewGameEvent.OnDonePressed -> {
                viewModel.loadingState = true
                onDonePressed()
            }
            is NewGameEvent.OnSizeChanged -> viewModel.settingsState =
                viewModel.settingsState.copy(boundary = event.boundary)
            is NewGameEvent.OnDifficultyChanged -> viewModel.settingsState =
                viewModel.settingsState.copy(difficulty = event.diff)
        }
    }

    private fun onDonePressed() {
        launch {
            gameRepo.updateSettings(
                viewModel.settingsState,
                {
                    createNewGame(viewModel.settingsState.boundary)
                },
                {
                    container?.showError()
                }
            )
        }
    }

    private fun createNewGame(boundary: Int) = launch {
        gameRepo.createNewGame(viewModel.settingsState,
            {
                jobTracker.cancel()
                container?.onDoneClick()
            },
            {
                container?.showError()
                jobTracker.cancel()
                container?.onDoneClick()
            }
        )
    }

    private fun onStart() {
        launch {
            gameRepo.getSettings(
                {
                    viewModel.settingsState = it
                    getStatistics()
                },
                {
                    jobTracker.cancel()
                    container?.showError()
                    container?.onDoneClick()
                }
            )
        }
    }

    private fun getStatistics() {
        launch {
            statsRepo.getStatistics(
                {
                    viewModel.statisticsState = it
                    viewModel.loadingState = false
                },
                {
                    container?.showError()
                }
            )
        }
    }
}