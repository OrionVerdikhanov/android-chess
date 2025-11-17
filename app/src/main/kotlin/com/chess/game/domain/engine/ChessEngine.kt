package com.chess.game.domain.engine

import com.chess.game.domain.model.*

/**
 * Шахматный движок
 * Отвечает за валидацию ходов, проверку шахов и определение состояния игры
 */
class ChessEngine {

    private val moveGenerator = MoveGenerator()

    /**
     * Получить все легальные ходы для фигуры на позиции
     */
    fun getLegalMoves(board: ChessBoard, from: Position): List<Move> {
        val pseudoLegalMoves = moveGenerator.generateMoves(board, from)
        return pseudoLegalMoves.filter { isMoveLegal(board, it) }
    }

    /**
     * Получить все легальные ходы для игрока
     */
    fun getAllLegalMoves(board: ChessBoard, color: PieceColor): List<Move> {
        val pseudoLegalMoves = moveGenerator.generateAllMoves(board, color)
        return pseudoLegalMoves.filter { isMoveLegal(board, it) }
    }

    /**
     * Проверить, является ли ход легальным
     * Ход легален, если после его выполнения король не находится под шахом
     */
    fun isMoveLegal(board: ChessBoard, move: Move): Boolean {
        // Выполняем ход на временной доске
        val tempBoard = board.makeMove(move)

        // Проверяем, не осталась ли под шахом сторона, которая сделала ход
        val kingColor = move.piece.color
        return !isKingInCheck(tempBoard, kingColor)
    }

    /**
     * Проверить, находится ли король под шахом
     */
    fun isKingInCheck(board: ChessBoard, kingColor: PieceColor): Boolean {
        val kingPos = board.findKing(kingColor) ?: return false
        val opponentColor = kingColor.opposite()

        // Проверяем, может ли какая-либо фигура противника атаковать короля
        for (row in 0..7) {
            for (col in 0..7) {
                val position = Position(row, col)
                val piece = board.getPiece(position)
                if (piece != null && piece.color == opponentColor) {
                    val attacks = moveGenerator.generateMoves(board, position)
                    if (attacks.any { it.to == kingPos }) {
                        return true
                    }
                }
            }
        }

        return false
    }

    /**
     * Проверить, является ли позиция матом
     */
    fun isCheckmate(board: ChessBoard, color: PieceColor): Boolean {
        // Мат - это шах + нет легальных ходов
        return isKingInCheck(board, color) && getAllLegalMoves(board, color).isEmpty()
    }

    /**
     * Проверить, является ли позиция патом
     */
    fun isStalemate(board: ChessBoard, color: PieceColor): Boolean {
        // Пат - это НЕ шах + нет легальных ходов
        return !isKingInCheck(board, color) && getAllLegalMoves(board, color).isEmpty()
    }

    /**
     * Получить текущее состояние игры
     */
    fun getGameState(board: ChessBoard): GameState {
        val currentPlayer = board.currentPlayer

        return when {
            isCheckmate(board, currentPlayer) -> {
                GameState.Checkmate(currentPlayer.opposite())
            }
            isStalemate(board, currentPlayer) -> {
                GameState.Stalemate
            }
            isKingInCheck(board, currentPlayer) -> {
                GameState.Check(currentPlayer)
            }
            else -> {
                GameState.Playing(currentPlayer)
            }
        }
    }

    /**
     * Проверить, может ли ход быть выполнен
     */
    fun canMove(board: ChessBoard, from: Position, to: Position): Boolean {
        val legalMoves = getLegalMoves(board, from)
        return legalMoves.any { it.to == to }
    }

    /**
     * Найти ход по начальной и конечной позиции
     */
    fun findMove(board: ChessBoard, from: Position, to: Position): Move? {
        val legalMoves = getLegalMoves(board, from)
        return legalMoves.find { it.to == to }
    }

    /**
     * Проверить, находится ли клетка под атакой
     */
    fun isSquareAttacked(board: ChessBoard, position: Position, byColor: PieceColor): Boolean {
        for (row in 0..7) {
            for (col in 0..7) {
                val pos = Position(row, col)
                val piece = board.getPiece(pos)
                if (piece != null && piece.color == byColor) {
                    val attacks = moveGenerator.generateMoves(board, pos)
                    if (attacks.any { it.to == position }) {
                        return true
                    }
                }
            }
        }
        return false
    }
}
