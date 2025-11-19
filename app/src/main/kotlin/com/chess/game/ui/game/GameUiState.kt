package com.chess.game.ui.game

import com.chess.game.domain.model.ChessBoard
import com.chess.game.domain.model.GameState
import com.chess.game.domain.model.Move
import com.chess.game.domain.model.Position

/**
 * UI состояние игрового экрана
 * Содержит всю информацию для отображения UI
 */
data class GameUiState(
    val board: ChessBoard = ChessBoard(),
    val gameState: GameState = GameState.Playing(com.chess.game.domain.model.PieceColor.WHITE),
    val selectedPosition: Position? = null,
    val legalMoves: List<Move> = emptyList(),
    val moveHistory: List<Move> = emptyList(),
    val isAiThinking: Boolean = false,
    val hintsAvailable: Int = 3, // Количество доступных подсказок
    val canUndo: Boolean = false // Можно ли отменить ход
)
