package com.chess.game.domain.model

/**
 * Шахматный ход
 * @param from Начальная позиция
 * @param to Конечная позиция
 * @param piece Фигура, которая ходит
 * @param capturedPiece Захваченная фигура (если есть)
 * @param isEnPassant Взятие на проходе
 * @param isCastling Рокировка
 * @param isPromotion Превращение пешки
 * @param promotionPiece Фигура, в которую превращается пешка
 */
data class Move(
    val from: Position,
    val to: Position,
    val piece: Piece,
    val capturedPiece: Piece? = null,
    val isEnPassant: Boolean = false,
    val isCastling: Boolean = false,
    val isPromotion: Boolean = false,
    val promotionPiece: PieceType? = null
) {
    /**
     * Является ли ход захватом фигуры
     */
    fun isCapture(): Boolean = capturedPiece != null || isEnPassant

    /**
     * Получить алгебраическую нотацию хода (упрощенная версия)
     */
    fun toAlgebraic(): String {
        return when {
            isCastling -> {
                if (to.col > from.col) "O-O" else "O-O-O"
            }
            isPromotion -> {
                val capture = if (isCapture()) "x" else ""
                "${from.toAlgebraic()}$capture${to.toAlgebraic()}=${promotionPiece?.toNotation()}"
            }
            else -> {
                val pieceSymbol = piece.type.toNotation()
                val capture = if (isCapture()) "x" else ""
                "$pieceSymbol${from.toAlgebraic()}$capture${to.toAlgebraic()}"
            }
        }
    }

    companion object {
        /**
         * Создать обычный ход
         */
        fun regular(from: Position, to: Position, piece: Piece, capturedPiece: Piece? = null) =
            Move(from, to, piece, capturedPiece)

        /**
         * Создать рокировку
         */
        fun castling(from: Position, to: Position, king: Piece) =
            Move(from, to, king, isCastling = true)

        /**
         * Создать взятие на проходе
         */
        fun enPassant(from: Position, to: Position, pawn: Piece, capturedPawn: Piece) =
            Move(from, to, pawn, capturedPawn, isEnPassant = true)

        /**
         * Создать превращение пешки
         */
        fun promotion(from: Position, to: Position, pawn: Piece, promotionType: PieceType, capturedPiece: Piece? = null) =
            Move(from, to, pawn, capturedPiece, isPromotion = true, promotionPiece = promotionType)
    }
}
