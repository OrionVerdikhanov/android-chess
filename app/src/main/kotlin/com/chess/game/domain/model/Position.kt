package com.chess.game.domain.model

/**
 * Позиция на шахматной доске
 * @param row Строка (0-7, где 0 = 1-я горизонталь, 7 = 8-я горизонталь)
 * @param col Колонка (0-7, где 0 = A, 7 = H)
 */
data class Position(
    val row: Int,
    val col: Int
) {
    init {
        require(row in 0..7) { "Строка должна быть в диапазоне 0..7" }
        require(col in 0..7) { "Колонка должна быть в диапазоне 0..7" }
    }

    /**
     * Проверка валидности позиции
     */
    fun isValid(): Boolean = row in 0..7 && col in 0..7

    /**
     * Алгебраическая нотация (например, e4, a1)
     */
    fun toAlgebraic(): String {
        val file = ('a' + col).toString()
        val rank = (row + 1).toString()
        return "$file$rank"
    }

    companion object {
        /**
         * Создание позиции из алгебраической нотации
         * @param notation Например, "e4", "a1"
         */
        fun fromAlgebraic(notation: String): Position? {
            if (notation.length != 2) return null
            val col = notation[0].lowercaseChar() - 'a'
            val row = notation[1].digitToIntOrNull()?.minus(1) ?: return null
            return if (row in 0..7 && col in 0..7) Position(row, col) else null
        }
    }
}
