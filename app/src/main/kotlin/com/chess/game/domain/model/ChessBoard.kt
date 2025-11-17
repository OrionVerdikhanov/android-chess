package com.chess.game.domain.model

/**
 * Шахматная доска с полной логикой игры
 */
class ChessBoard(
    private val board: Array<Array<Piece?>> = createInitialBoard(),
    val currentPlayer: PieceColor = PieceColor.WHITE,
    val moveHistory: List<Move> = emptyList(),
    private var enPassantTarget: Position? = null
) {
    /**
     * Получить фигуру на позиции
     */
    fun getPiece(position: Position): Piece? = board[position.row][position.col]

    /**
     * Получить фигуру на позиции (по координатам)
     */
    fun getPiece(row: Int, col: Int): Piece? =
        if (row in 0..7 && col in 0..7) board[row][col] else null

    /**
     * Установить фигуру на позицию
     */
    private fun setPiece(position: Position, piece: Piece?) {
        board[position.row][position.col] = piece
    }

    /**
     * Получить все фигуры определенного цвета
     */
    fun getPieces(color: PieceColor): List<Pair<Position, Piece>> {
        val pieces = mutableListOf<Pair<Position, Piece>>()
        for (row in 0..7) {
            for (col in 0..7) {
                val piece = board[row][col]
                if (piece != null && piece.color == color) {
                    pieces.add(Position(row, col) to piece)
                }
            }
        }
        return pieces
    }

    /**
     * Найти короля определенного цвета
     */
    fun findKing(color: PieceColor): Position? {
        for (row in 0..7) {
            for (col in 0..7) {
                val piece = board[row][col]
                if (piece?.type == PieceType.KING && piece.color == color) {
                    return Position(row, col)
                }
            }
        }
        return null
    }

    /**
     * Выполнить ход и вернуть новую доску
     */
    fun makeMove(move: Move): ChessBoard {
        val newBoard = copyBoard()
        val newHistory = moveHistory + move

        // Убираем фигуру с начальной позиции
        newBoard[move.from.row][move.from.col] = null

        // Обработка специальных ходов
        when {
            move.isCastling -> {
                // Перемещаем короля
                newBoard[move.to.row][move.to.col] = move.piece.moved()

                // Перемещаем ладью
                val rookFromCol = if (move.to.col > move.from.col) 7 else 0
                val rookToCol = if (move.to.col > move.from.col) move.to.col - 1 else move.to.col + 1
                val rook = newBoard[move.from.row][rookFromCol]
                newBoard[move.from.row][rookFromCol] = null
                newBoard[move.from.row][rookToCol] = rook?.moved()
            }
            move.isEnPassant -> {
                // Убираем захваченную пешку
                val capturedRow = move.from.row
                newBoard[capturedRow][move.to.col] = null
                newBoard[move.to.row][move.to.col] = move.piece.moved()
            }
            move.isPromotion -> {
                // Превращаем пешку
                val promotedPiece = Piece(move.promotionPiece!!, move.piece.color, true)
                newBoard[move.to.row][move.to.col] = promotedPiece
            }
            else -> {
                // Обычный ход
                newBoard[move.to.row][move.to.col] = move.piece.moved()
            }
        }

        // Обновляем цель для взятия на проходе
        val newEnPassantTarget = if (move.piece.type == PieceType.PAWN &&
            kotlin.math.abs(move.to.row - move.from.row) == 2) {
            Position((move.from.row + move.to.row) / 2, move.from.col)
        } else {
            null
        }

        return ChessBoard(
            board = newBoard,
            currentPlayer = currentPlayer.opposite(),
            moveHistory = newHistory,
            enPassantTarget = newEnPassantTarget
        )
    }

    /**
     * Копировать доску
     */
    private fun copyBoard(): Array<Array<Piece?>> {
        return Array(8) { row ->
            Array(8) { col ->
                board[row][col]
            }
        }
    }

    /**
     * Создать копию доски
     */
    fun copy(): ChessBoard {
        return ChessBoard(
            board = copyBoard(),
            currentPlayer = currentPlayer,
            moveHistory = moveHistory.toList(),
            enPassantTarget = enPassantTarget
        )
    }

    companion object {
        /**
         * Создать начальную расстановку фигур
         */
        private fun createInitialBoard(): Array<Array<Piece?>> {
            val board = Array(8) { arrayOfNulls<Piece>(8) }

            // Белые фигуры (нижние ряды)
            board[0][0] = Piece.white(PieceType.ROOK)
            board[0][1] = Piece.white(PieceType.KNIGHT)
            board[0][2] = Piece.white(PieceType.BISHOP)
            board[0][3] = Piece.white(PieceType.QUEEN)
            board[0][4] = Piece.white(PieceType.KING)
            board[0][5] = Piece.white(PieceType.BISHOP)
            board[0][6] = Piece.white(PieceType.KNIGHT)
            board[0][7] = Piece.white(PieceType.ROOK)
            for (col in 0..7) {
                board[1][col] = Piece.white(PieceType.PAWN)
            }

            // Черные фигуры (верхние ряды)
            board[7][0] = Piece.black(PieceType.ROOK)
            board[7][1] = Piece.black(PieceType.KNIGHT)
            board[7][2] = Piece.black(PieceType.BISHOP)
            board[7][3] = Piece.black(PieceType.QUEEN)
            board[7][4] = Piece.black(PieceType.KING)
            board[7][5] = Piece.black(PieceType.BISHOP)
            board[7][6] = Piece.black(PieceType.KNIGHT)
            board[7][7] = Piece.black(PieceType.ROOK)
            for (col in 0..7) {
                board[6][col] = Piece.black(PieceType.PAWN)
            }

            return board
        }
    }
}
