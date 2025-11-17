package com.chess.game.domain.model

/**
 * Шахматная фигура
 * @param type Тип фигуры
 * @param color Цвет фигуры
 * @param hasMoved Флаг, двигалась ли фигура (важно для рокировки и первого хода пешки)
 */
data class Piece(
    val type: PieceType,
    val color: PieceColor,
    val hasMoved: Boolean = false
) {
    /**
     * Получить ценность фигуры для AI
     */
    fun getValue(): Int = type.value

    /**
     * Создать копию фигуры с флагом "двигалась"
     */
    fun moved(): Piece = copy(hasMoved = true)

    /**
     * Получить Unicode символ для отображения
     */
    fun getSymbol(): String {
        val baseSymbol = type.symbol
        // Белые фигуры используют полые символы (увеличиваем код на 6)
        val symbol = if (color == PieceColor.WHITE) {
            (baseSymbol.code - 6).toChar()
        } else {
            baseSymbol
        }
        return symbol.toString()
    }

    companion object {
        /**
         * Создать белую фигуру
         */
        fun white(type: PieceType, hasMoved: Boolean = false) =
            Piece(type, PieceColor.WHITE, hasMoved)

        /**
         * Создать черную фигуру
         */
        fun black(type: PieceType, hasMoved: Boolean = false) =
            Piece(type, PieceColor.BLACK, hasMoved)
    }
}
