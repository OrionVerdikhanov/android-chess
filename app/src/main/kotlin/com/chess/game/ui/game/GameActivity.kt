package com.chess.game.ui.game

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.chess.game.R
import com.chess.game.core.extensions.gone
import com.chess.game.core.extensions.toast
import com.chess.game.core.extensions.visible
import com.chess.game.core.utils.Constants
import com.chess.game.data.preferences.PreferencesManager
import com.chess.game.data.sound.SoundManager
import com.chess.game.databinding.ActivityGameBinding
import com.chess.game.domain.model.GameDifficulty
import com.chess.game.domain.model.GameState
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

/**
 * Игровой экран
 * Отображает шахматную доску и управление игрой
 */
class GameActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameBinding
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var soundManager: SoundManager

    private val viewModel: GameViewModel by viewModels {
        val difficultyName = intent.getStringExtra(Constants.EXTRA_DIFFICULTY)
        val difficulty = GameDifficulty.valueOf(difficultyName ?: GameDifficulty.AMATEUR.name)

        GameViewModelFactory(
            difficulty = difficulty,
            preferencesManager = PreferencesManager(this),
            soundManager = SoundManager(this)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferencesManager = PreferencesManager(this)
        soundManager = SoundManager(this).apply {
            soundEnabled = preferencesManager.soundEnabled
            vibrationEnabled = preferencesManager.vibrationEnabled
        }

        setupToolbar()
        setupBoard()
        setupButtons()
        observeGameState()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setupBoard() {
        binding.chessBoardView.apply {
            showLegalMoves = preferencesManager.showLegalMoves
            boardTheme = preferencesManager.boardTheme

            onMoveAttempt = { from, to ->
                viewModel.onSquareClicked(from)
                viewModel.onSquareClicked(to)
            }
        }
    }

    private fun setupButtons() {
        binding.apply {
            // Подсказка
            btnHint.setOnClickListener {
                toast("Функция подсказки в разработке")
                // TODO: Показать rewarded рекламу и подсказку
            }

            // Отменить ход
            btnUndo.setOnClickListener {
                viewModel.undoMove()
            }

            // Сдаться
            btnResign.setOnClickListener {
                showResignDialog()
            }

            // Новая игра
            btnNewGame.setOnClickListener {
                showNewGameDialog()
            }
        }
    }

    private fun observeGameState() {
        lifecycleScope.launch {
            viewModel.uiState.collect { uiState ->
                updateUI(uiState)
            }
        }
    }

    private fun updateUI(uiState: GameUiState) {
        binding.apply {
            // Обновляем доску
            chessBoardView.gameSession = viewModel.gameSession

            // Обновляем статус игры
            tvGameStatus.text = when (uiState.gameState) {
                is GameState.Playing -> {
                    val color = (uiState.gameState as GameState.Playing).currentPlayer
                    if (color.name == "WHITE") getString(R.string.white_turn)
                    else getString(R.string.black_turn)
                }
                is GameState.Check -> getString(R.string.check)
                is GameState.Checkmate -> getString(R.string.checkmate)
                is GameState.Stalemate -> getString(R.string.stalemate)
                is GameState.Draw -> getString(R.string.draw)
                is GameState.Resigned -> getString(R.string.result_you_lose)
            }

            // Показываем индикатор "AI думает"
            if (uiState.isAiThinking) {
                progressAiThinking.visible()
                tvAiThinking.visible()
            } else {
                progressAiThinking.gone()
                tvAiThinking.gone()
            }

            // Проверяем окончание игры
            if (uiState.gameState.isGameOver()) {
                showGameOverDialog(uiState.gameState)
            }
        }
    }

    private fun showResignDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.dialog_resign_title)
            .setMessage(R.string.dialog_resign_message)
            .setPositiveButton(R.string.yes) { _, _ ->
                viewModel.resign()
            }
            .setNegativeButton(R.string.no, null)
            .show()
    }

    private fun showNewGameDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.dialog_new_game_title)
            .setMessage(R.string.dialog_new_game_message)
            .setPositiveButton(R.string.yes) { _, _ ->
                viewModel.newGame()
            }
            .setNegativeButton(R.string.no, null)
            .show()
    }

    private fun showGameOverDialog(gameState: GameState) {
        val message = when (gameState) {
            is GameState.Checkmate -> {
                if (gameState.winner.name == "WHITE") {
                    getString(R.string.result_you_win)
                } else {
                    getString(R.string.result_you_lose)
                }
            }
            is GameState.Stalemate -> getString(R.string.result_draw)
            is GameState.Draw -> getString(R.string.result_draw)
            is GameState.Resigned -> getString(R.string.result_you_lose)
            else -> ""
        }

        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.app_name)
            .setMessage(message)
            .setPositiveButton(R.string.btn_new_game) { _, _ ->
                viewModel.newGame()
            }
            .setNegativeButton(R.string.btn_main_menu) { _, _ ->
                finish()
            }
            .setCancelable(false)
            .show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        soundManager.release()
    }
}
