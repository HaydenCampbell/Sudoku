package com.haydencampbell.sudoku.domain.repository

import com.haydencampbell.sudoku.domain.model.Difficulty
import com.haydencampbell.sudoku.domain.model.UserStatistics

interface IStatisticsRepository {
    suspend fun getStatistics(
        onSuccess: (UserStatistics) -> Unit,
        onError: (Exception) -> Unit
    )

    suspend fun updateStatistic(
        time: Long,
        difficulty: Difficulty,
        boundary: Int,
        onSuccess: (isRecord: Boolean) -> Unit,
        onError: (Exception) -> Unit
    )
}