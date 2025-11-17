package com.chess.game.domain.model

/**
 * Тип шахматной фигуры
 * @param value Базовая ценность фигуры для AI оценки
 * @param symbol Символ для отображения (Unicode)
 */
enum class PieceType(val value: Int, val symbol: Char) {
    PAWN(100, '♟'),      // Пешка
    KNIGHT(320, '♞'),    // Конь
    BISHOP(330, '♝'),    // Слон
    ROOK(500, '♜'),      // Ладья
    QUEEN(900, '♛'),     // Ферзь
    KING(20000, '♚');    // Король (наивысшая ценность)

    /**
     * Буквенное обозначение для записи ходов
     */
    fun toNotation(): String = when (this) {
        KING -> "K"
        QUEEN -> "Q"
        ROOK -> "R"
        BISHOP -> "B"
        KNIGHT -> "N"
        PAWN -> ""
    }

    /**
     * Русское название фигуры
     */
    fun toRussianName(): String = when (this) {
        PAWN -> "Пешка"
        KNIGHT -> "Конь"
        BISHOP -> "Слон"
        ROOK -> "Ладья"
        QUEEN -> "Ферзь"
        KING -> "Король"
    }
}
