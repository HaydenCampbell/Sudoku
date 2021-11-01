package com.haydencampbell.sudoku.util

import android.app.Activity
import android.widget.Toast
import com.haydencampbell.sudoku.domain.model.Difficulty
import com.haydencampbell.sudoku.presentation.STR_DIFF_EASY
import com.haydencampbell.sudoku.presentation.STR_DIFF_HARD
import com.haydencampbell.sudoku.presentation.STR_DIFF_MED

internal fun Activity.makeToast(message: String) {
    Toast.makeText(
        this,
        message,
        Toast.LENGTH_LONG
    ).show()
}

internal fun Long.toTime(): String {
    if (this >= 3600) return "+59:59"
    var minutes = ((this % 3600) / 60).toString()
    if (minutes.length == 1) minutes = "0$minutes"
    var seconds = (this % 60).toString()
    if (seconds.length == 1) seconds = "0$seconds"
    return String.format("$minutes:$seconds")
}

internal val Difficulty.toLocalizedResource: String
    get() {
        return when (this) {
            Difficulty.EASY -> STR_DIFF_EASY
            Difficulty.MEDIUM -> STR_DIFF_MED
            Difficulty.HARD -> STR_DIFF_HARD
        }
    }