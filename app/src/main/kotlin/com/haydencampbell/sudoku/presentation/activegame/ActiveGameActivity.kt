package com.haydencampbell.sudoku.presentation.activegame

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.compose.setContent
import com.haydencampbell.sudoku.R
import com.haydencampbell.sudoku.util.makeToast
import com.haydencampbell.sudoku.presentation.SudokuTheme
import com.haydencampbell.sudoku.presentation.activegame.buildlogic.buildActiveGameLogic
import com.haydencampbell.sudoku.presentation.newgame.NewGameActivity

class ActiveGameActivity : AppCompatActivity(), ActiveGameContainer {
    private lateinit var logic: ActiveGameLogic

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel = ActiveGameViewModel()

        setContent {
            SudokuTheme {
                ActiveGameScreen(
                    onEventHandler = logic::onEvent,
                    viewModel
                )
            }
        }

        logic = buildActiveGameLogic(this, viewModel, applicationContext)
    }

    override fun onStart() {
        super.onStart()
        logic.onEvent(ActiveGameEvent.OnStart)
    }

    override fun onStop() {
        super.onStop()
        logic.onEvent(ActiveGameEvent.OnStop)

        //guarantee that onRestart not called

        finish()
    }

    override fun onNewGameClick() {
        startActivity(
            Intent(
                this,
                NewGameActivity::class.java
            )
        )
    }

    override fun showError() = makeToast(getString(R.string.generic_error))
}
