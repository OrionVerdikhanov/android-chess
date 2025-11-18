package com.chess.game.ui.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chess.game.data.preferences.PreferencesManager
import com.chess.game.data.sound.SoundManager
import com.chess.game.domain.model.*
import com.chess.game.domain.session.GameSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel для управления игровым процессом
 */
class GameViewModel(
    private val difficulty: GameDifficulty,
    private val preferencesManager: PreferencesManager,
    private val soundManager: SoundManager
) : ViewModel() {

    val gameSession = GameSession(difficulty, PieceColor.WHITE)

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState

    init {
        updateUiState()
    }

    fun onSquareClicked(position: Position) {
        if (_uiState.value.isAiThinking) return

        val currentSelected = _uiState.value.selectedPosition

        if (currentSelected == null) {
            // Выбираем фигуру
            val piece = gameSession.currentBoard.getPiece(position)
            if (piece != null && piece.color == gameSession.playerColor) {
                val legalMoves = gameSession.getLegalMoves(position)
                _uiState.update { it.copy(
                    selectedPosition = position,
                    legalMoves = legalMoves
                )}
            }
        } else {
            // Пытаемся сделать ход
            when (val result = gameSession.makePlayerMove(currentSelected, position)) {
                is GameSession.MakeMoveResult.Success -> {
                    soundManager.playSound(
                        if (result.move.isCapture()) SoundManager.SoundType.CAPTURE
                        else SoundManager.SoundType.MOVE
                    )

                    _uiState.update { it.copy(
                        selectedPosition = null,
                        legalMoves = emptyList()
                    )}

                    updateUiState()
                    handleGameState(result.newState)

                    // Ход AI
                    if (result.newState is GameState.Playing) {
                        makeAIMove()
                    }
                }
                is GameSession.MakeMoveResult.Error -> {
                    soundManager.playSound(SoundManager.SoundType.ERROR)
                    _uiState.update { it.copy(
                        selectedPosition = null,
                        legalMoves = emptyList()
                    )}
                }
            }
        }
    }

    private fun makeAIMove() {
        _uiState.update { it.copy(isAiThinking = true) }

        viewModelScope.launch {
            val aiMove = gameSession.getAIMove()
            if (aiMove != null) {
                val result = gameSession.makeAIMove(aiMove)
                if (result is GameSession.MakeMoveResult.Success) {
                    soundManager.playSound(
                        if (result.move.isCapture()) SoundManager.SoundType.CAPTURE
                        else SoundManager.SoundType.MOVE
                    )
                    handleGameState(result.newState)
                }
            }

            _uiState.update { it.copy(isAiThinking = false) }
            updateUiState()
        }
    }

    private fun handleGameState(state: GameState) {
        when (state) {
            is GameState.Check -> {
                soundManager.playSound(SoundManager.SoundType.CHECK)
                soundManager.vibrate(100)
            }
            is GameState.Checkmate -> {
                soundManager.playSound(SoundManager.SoundType.CHECKMATE)
                soundManager.vibrate(200)

                if (state.winner == gameSession.playerColor) {
                    preferencesManager.incrementGamesWon(difficulty)
                } else {
                    preferencesManager.incrementGamesLost(difficulty)
                }
                preferencesManager.incrementGamesPlayed(difficulty)
            }
            is GameState.Stalemate, is GameState.Draw -> {
                preferencesManager.incrementGamesDraw(difficulty)
                preferencesManager.incrementGamesPlayed(difficulty)
            }
            else -> {}
        }
    }

    fun undoMove() {
        if (gameSession.undoMove(undoBoth = true)) {
            soundManager.playSound(SoundManager.SoundType.MOVE)
            _uiState.update { it.copy(
                selectedPosition = null,
                legalMoves = emptyList()
            )}
            updateUiState()
        }
    }

    fun resign() {
        gameSession.resign()
        preferencesManager.incrementGamesLost(difficulty)
        preferencesManager.incrementGamesPlayed(difficulty)
        updateUiState()
    }

    fun newGame() {
        gameSession.newGame()
        _uiState.value = GameUiState()
        updateUiState()
    }

    private fun updateUiState() {
        _uiState.update { it.copy(
            board = gameSession.currentBoard,
            gameState = gameSession.gameState,
            moveHistory = gameSession.moveHistory.getMoves()
        )}
    }
}
