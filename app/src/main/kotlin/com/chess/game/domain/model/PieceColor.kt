package com.chess.game.domain.model

/**
 * Цвет шахматной фигуры
 */
enum class PieceColor {
    WHITE,  // Белые
    BLACK;  // Черные

    /**
     * Получить противоположный цвет
     */
    fun opposite(): PieceColor = when (this) {
        WHITE -> BLACK
        BLACK -> WHITE
    }

    /**
     * Направление движения пешки (белые идут вверх, черные вниз)
     */
    fun pawnDirection(): Int = when (this) {
        WHITE -> 1
        BLACK -> -1
    }

    /**
     * Стартовая строка для пешек
     */
    fun pawnStartRow(): Int = when (this) {
        WHITE -> 1
        BLACK -> 6
    }

    /**
     * Строка для превращения пешки
     */
    fun pawnPromotionRow(): Int = when (this) {
        WHITE -> 7
        BLACK -> 0
    }
}
