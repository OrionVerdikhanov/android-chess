package com.chess.game.domain.engine

import com.chess.game.domain.model.*
import kotlin.math.abs

/**
 * Оценщик шахматной позиции
 * Возвращает числовую оценку позиции с точки зрения белых
 * Положительное значение = преимущество белых, отрицательное = преимущество черных
 */
class BoardEvaluator {

    /**
     * Оценить позицию для определенного цвета
     * @return оценка в сантипешках (100 = 1 пешка)
     */
    fun evaluate(board: ChessBoard, forColor: PieceColor): Int {
        var score = 0

        // Оценка материала
        score += evaluateMaterial(board)

        // Позиционные факторы
        score += evaluatePositional(board)

        // Контроль центра
        score += evaluateCenterControl(board)

        // Безопасность короля
        score += evaluateKingSafety(board)

        // Возвращаем оценку с точки зрения forColor
        return if (forColor == PieceColor.WHITE) score else -score
    }

    /**
     * Оценка материала (ценность фигур)
     */
    private fun evaluateMaterial(board: ChessBoard): Int {
        var score = 0

        for (row in 0..7) {
            for (col in 0..7) {
                val piece = board.getPiece(row, col)
                if (piece != null) {
                    val value = piece.type.value
                    score += if (piece.color == PieceColor.WHITE) value else -value
                }
            }
        }

        return score
    }

    /**
     * Позиционная оценка (таблицы piece-square tables)
     */
    private fun evaluatePositional(board: ChessBoard): Int {
        var score = 0

        for (row in 0..7) {
            for (col in 0..7) {
                val piece = board.getPiece(row, col)
                if (piece != null) {
                    val posValue = getPositionalValue(piece, Position(row, col))
                    score += if (piece.color == PieceColor.WHITE) posValue else -posValue
                }
            }
        }

        return score
    }

    /**
     * Получить позиционную ценность фигуры
     */
    private fun getPositionalValue(piece: Piece, position: Position): Int {
        // Упрощенные piece-square tables
        return when (piece.type) {
            PieceType.PAWN -> getPawnPositionalValue(piece, position)
            PieceType.KNIGHT -> getKnightPositionalValue(position)
            PieceType.BISHOP -> getBishopPositionalValue(position)
            PieceType.ROOK -> 0 // Ладьи не имеют сильного позиционного бонуса в упрощенной версии
            PieceType.QUEEN -> 0
            PieceType.KING -> getKingPositionalValue(piece, position)
        }
    }

    /**
     * Позиционная ценность пешки
     */
    private fun getPawnPositionalValue(piece: Piece, position: Position): Int {
        val row = if (piece.color == PieceColor.WHITE) position.row else 7 - position.row

        return when (row) {
            0, 1 -> 0
            2 -> 5
            3 -> 10
            4 -> 20
            5 -> 30
            6 -> 50
            7 -> 100 // Близко к превращению
            else -> 0
        }
    }

    /**
     * Позиционная ценность коня (любит центр)
     */
    private fun getKnightPositionalValue(position: Position): Int {
        val centerDistance = abs(position.row - 3.5) + abs(position.col - 3.5)
        return (7 - centerDistance.toInt()) * 5
    }

    /**
     * Позиционная ценность слона (любит диагонали и центр)
     */
    private fun getBishopPositionalValue(position: Position): Int {
        val centerDistance = abs(position.row - 3.5) + abs(position.col - 3.5)
        return (7 - centerDistance.toInt()) * 3
    }

    /**
     * Позиционная ценность короля (в начале игры - безопасность, в эндшпиле - активность)
     */
    private fun getKingPositionalValue(piece: Piece, position: Position): Int {
        // В начале игры король должен быть в углу (безопасность)
        // Упрощенная версия - просто небольшой бонус за рокировку
        return if (piece.color == PieceColor.WHITE) {
            if (position.col in 5..7 && position.row == 0) 30 else 0
        } else {
            if (position.col in 5..7 && position.row == 7) 30 else 0
        }
    }

    /**
     * Оценка контроля центра (e4, d4, e5, d5)
     */
    private fun evaluateCenterControl(board: ChessBoard): Int {
        val centerSquares = listOf(
            Position(3, 3), Position(3, 4), // d4, e4
            Position(4, 3), Position(4, 4)  // d5, e5
        )

        var score = 0
        for (square in centerSquares) {
            val piece = board.getPiece(square)
            if (piece != null) {
                val bonus = when (piece.type) {
                    PieceType.PAWN -> 10
                    PieceType.KNIGHT -> 15
                    PieceType.BISHOP -> 10
                    else -> 5
                }
                score += if (piece.color == PieceColor.WHITE) bonus else -bonus
            }
        }

        return score
    }

    /**
     * Оценка безопасности короля
     */
    private fun evaluateKingSafety(board: ChessBoard): Int {
        var score = 0

        // Проверка пешечного щита для белых
        board.findKing(PieceColor.WHITE)?.let { kingPos ->
            score += countPawnShield(board, kingPos, PieceColor.WHITE) * 10
        }

        // Проверка пешечного щита для черных
        board.findKing(PieceColor.BLACK)?.let { kingPos ->
            score -= countPawnShield(board, kingPos, PieceColor.BLACK) * 10
        }

        return score
    }

    /**
     * Подсчитать количество пешек перед королем (пешечный щит)
     */
    private fun countPawnShield(board: ChessBoard, kingPos: Position, kingColor: PieceColor): Int {
        var count = 0
        val direction = if (kingColor == PieceColor.WHITE) 1 else -1
        val shieldRow = kingPos.row + direction

        if (shieldRow in 0..7) {
            for (col in (kingPos.col - 1)..(kingPos.col + 1)) {
                if (col in 0..7) {
                    val piece = board.getPiece(shieldRow, col)
                    if (piece?.type == PieceType.PAWN && piece.color == kingColor) {
                        count++
                    }
                }
            }
        }

        return count
    }
}
