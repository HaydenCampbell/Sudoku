package com.haydencampbell.sudoku.presentation.newgame

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.haydencampbell.sudoku.R
import com.haydencampbell.sudoku.util.makeToast
import com.haydencampbell.sudoku.presentation.SudokuTheme
import com.haydencampbell.sudoku.presentation.activegame.ActiveGameActivity
import com.haydencampbell.sudoku.presentation.newgame.buildlogic.buildNewGameLogic

class NewGameActivity : AppCompatActivity(), NewGameContainer {
    private lateinit var logic: NewGameLogic


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel = NewGameViewModel()

        setContent {
            SudokuTheme {
                NewGameScreen(
                    onEventHandler = logic::onEvent,
                    viewModel
                )
            }
        }

        logic = buildNewGameLogic(this, viewModel, applicationContext)

    }

    override fun onStart() {
        super.onStart()
        logic.onEvent(NewGameEvent.OnStart)
    }

    override fun showError() = makeToast(getString(R.string.generic_error))

    override fun onDoneClick() {
        startActiveGameActivity()
    }

    override fun onBackPressed() {
        startActiveGameActivity()
    }

    private fun startActiveGameActivity() {
        startActivity(
            Intent(
                this,
                ActiveGameActivity::class.java
            ).apply {
                this.flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK
            }
        )
    }
}