package com.chess.game.domain.engine

import com.chess.game.domain.model.*

/**
 * Генератор возможных ходов для шахматных фигур
 * Генерирует все псевдо-легальные ходы (без проверки на шах)
 */
class MoveGenerator {

    /**
     * Сгенерировать все возможные ходы для фигуры на позиции
     */
    fun generateMoves(board: ChessBoard, from: Position): List<Move> {
        val piece = board.getPiece(from) ?: return emptyList()

        return when (piece.type) {
            PieceType.PAWN -> generatePawnMoves(board, from, piece)
            PieceType.KNIGHT -> generateKnightMoves(board, from, piece)
            PieceType.BISHOP -> generateBishopMoves(board, from, piece)
            PieceType.ROOK -> generateRookMoves(board, from, piece)
            PieceType.QUEEN -> generateQueenMoves(board, from, piece)
            PieceType.KING -> generateKingMoves(board, from, piece)
        }
    }

    /**
     * Сгенерировать все возможные ходы для игрока
     */
    fun generateAllMoves(board: ChessBoard, color: PieceColor): List<Move> {
        val moves = mutableListOf<Move>()
        for (row in 0..7) {
            for (col in 0..7) {
                val position = Position(row, col)
                val piece = board.getPiece(position)
                if (piece != null && piece.color == color) {
                    moves.addAll(generateMoves(board, position))
                }
            }
        }
        return moves
    }

    /**
     * Генерация ходов пешки
     */
    private fun generatePawnMoves(board: ChessBoard, from: Position, piece: Piece): List<Move> {
        val moves = mutableListOf<Move>()
        val direction = piece.color.pawnDirection()
        val startRow = piece.color.pawnStartRow()
        val promotionRow = piece.color.pawnPromotionRow()

        // Ход вперед на одну клетку
        val oneForward = Position(from.row + direction, from.col)
        if (oneForward.isValid() && board.getPiece(oneForward) == null) {
            if (oneForward.row == promotionRow) {
                // Превращение пешки
                addPromotionMoves(moves, from, oneForward, piece)
            } else {
                moves.add(Move.regular(from, oneForward, piece))
            }

            // Ход вперед на две клетки с начальной позиции
            if (from.row == startRow) {
                val twoForward = Position(from.row + direction * 2, from.col)
                if (board.getPiece(twoForward) == null) {
                    moves.add(Move.regular(from, twoForward, piece))
                }
            }
        }

        // Захват по диагонали
        val captureCols = listOf(from.col - 1, from.col + 1)
        for (col in captureCols) {
            if (col in 0..7) {
                val capturePos = Position(from.row + direction, col)
                if (capturePos.isValid()) {
                    val targetPiece = board.getPiece(capturePos)
                    if (targetPiece != null && targetPiece.color != piece.color) {
                        if (capturePos.row == promotionRow) {
                            addPromotionMoves(moves, from, capturePos, piece, targetPiece)
                        } else {
                            moves.add(Move.regular(from, capturePos, piece, targetPiece))
                        }
                    }
                }
            }
        }

        return moves
    }

    /**
     * Добавить ходы превращения пешки
     */
    private fun addPromotionMoves(
        moves: MutableList<Move>,
        from: Position,
        to: Position,
        piece: Piece,
        captured: Piece? = null
    ) {
        val promotionTypes = listOf(
            PieceType.QUEEN,
            PieceType.ROOK,
            PieceType.BISHOP,
            PieceType.KNIGHT
        )
        promotionTypes.forEach { type ->
            moves.add(Move.promotion(from, to, piece, type, captured))
        }
    }

    /**
     * Генерация ходов коня
     */
    private fun generateKnightMoves(board: ChessBoard, from: Position, piece: Piece): List<Move> {
        val moves = mutableListOf<Move>()
        val knightMoves = listOf(
            -2 to -1, -2 to 1, -1 to -2, -1 to 2,
            1 to -2, 1 to 2, 2 to -1, 2 to 1
        )

        for ((dr, dc) in knightMoves) {
            val to = Position(from.row + dr, from.col + dc)
            if (to.isValid()) {
                val targetPiece = board.getPiece(to)
                if (targetPiece == null || targetPiece.color != piece.color) {
                    moves.add(Move.regular(from, to, piece, targetPiece))
                }
            }
        }

        return moves
    }

    /**
     * Генерация ходов слона
     */
    private fun generateBishopMoves(board: ChessBoard, from: Position, piece: Piece): List<Move> {
        val directions = listOf(-1 to -1, -1 to 1, 1 to -1, 1 to 1)
        return generateSlidingMoves(board, from, piece, directions)
    }

    /**
     * Генерация ходов ладьи
     */
    private fun generateRookMoves(board: ChessBoard, from: Position, piece: Piece): List<Move> {
        val directions = listOf(-1 to 0, 1 to 0, 0 to -1, 0 to 1)
        return generateSlidingMoves(board, from, piece, directions)
    }

    /**
     * Генерация ходов ферзя
     */
    private fun generateQueenMoves(board: ChessBoard, from: Position, piece: Piece): List<Move> {
        val directions = listOf(
            -1 to -1, -1 to 0, -1 to 1,
            0 to -1, 0 to 1,
            1 to -1, 1 to 0, 1 to 1
        )
        return generateSlidingMoves(board, from, piece, directions)
    }

    /**
     * Генерация скользящих ходов (для слона, ладьи, ферзя)
     */
    private fun generateSlidingMoves(
        board: ChessBoard,
        from: Position,
        piece: Piece,
        directions: List<Pair<Int, Int>>
    ): List<Move> {
        val moves = mutableListOf<Move>()

        for ((dr, dc) in directions) {
            var row = from.row + dr
            var col = from.col + dc

            while (row in 0..7 && col in 0..7) {
                val to = Position(row, col)
                val targetPiece = board.getPiece(to)

                when {
                    targetPiece == null -> {
                        moves.add(Move.regular(from, to, piece))
                    }
                    targetPiece.color != piece.color -> {
                        moves.add(Move.regular(from, to, piece, targetPiece))
                        break
                    }
                    else -> break
                }

                row += dr
                col += dc
            }
        }

        return moves
    }

    /**
     * Генерация ходов короля
     */
    private fun generateKingMoves(board: ChessBoard, from: Position, piece: Piece): List<Move> {
        val moves = mutableListOf<Move>()
        val kingMoves = listOf(
            -1 to -1, -1 to 0, -1 to 1,
            0 to -1, 0 to 1,
            1 to -1, 1 to 0, 1 to 1
        )

        for ((dr, dc) in kingMoves) {
            val to = Position(from.row + dr, from.col + dc)
            if (to.isValid()) {
                val targetPiece = board.getPiece(to)
                if (targetPiece == null || targetPiece.color != piece.color) {
                    moves.add(Move.regular(from, to, piece, targetPiece))
                }
            }
        }

        // Рокировка
        if (!piece.hasMoved) {
            moves.addAll(generateCastlingMoves(board, from, piece))
        }

        return moves
    }

    /**
     * Генерация ходов рокировки
     */
    private fun generateCastlingMoves(board: ChessBoard, from: Position, king: Piece): List<Move> {
        val moves = mutableListOf<Move>()
        val row = from.row

        // Короткая рокировка (O-O)
        val kingRook = board.getPiece(row, 7)
        if (kingRook?.type == PieceType.ROOK && !kingRook.hasMoved &&
            board.getPiece(row, 5) == null &&
            board.getPiece(row, 6) == null
        ) {
            moves.add(Move.castling(from, Position(row, 6), king))
        }

        // Длинная рокировка (O-O-O)
        val queenRook = board.getPiece(row, 0)
        if (queenRook?.type == PieceType.ROOK && !queenRook.hasMoved &&
            board.getPiece(row, 1) == null &&
            board.getPiece(row, 2) == null &&
            board.getPiece(row, 3) == null
        ) {
            moves.add(Move.castling(from, Position(row, 2), king))
        }

        return moves
    }
}
