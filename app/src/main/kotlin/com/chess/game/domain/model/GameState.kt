package com.chess.game.domain.model

/**
 * Состояние шахматной игры
 */
sealed class GameState {
    /** Игра в процессе */
    data class Playing(val currentPlayer: PieceColor) : GameState()

    /** Шах */
    data class Check(val checkedKing: PieceColor) : GameState()

    /** Мат (игра окончена) */
    data class Checkmate(val winner: PieceColor) : GameState()

    /** Пат (ничья) */
    object Stalemate : GameState()

    /** Ничья по согласию или другим правилам */
    object Draw : GameState()

    /** Игрок сдался */
    data class Resigned(val resignedPlayer: PieceColor) : GameState()

    /**
     * Проверка, окончена ли игра
     */
    fun isGameOver(): Boolean = when (this) {
        is Playing, is Check -> false
        is Checkmate, is Stalemate, is Draw, is Resigned -> true
    }

    /**
     * Получить описание состояния на русском
     */
    fun getDescription(): String = when (this) {
        is Playing -> "Ход ${currentPlayer.name.lowercase()}"
        is Check -> "Шах ${if (checkedKing == PieceColor.WHITE) "белым" else "черным"}!"
        is Checkmate -> "Мат! Победили ${if (winner == PieceColor.WHITE) "белые" else "черные"}"
        is Stalemate -> "Пат! Ничья"
        is Draw -> "Ничья"
        is Resigned -> "${if (resignedPlayer == PieceColor.WHITE) "Белые" else "Черные"} сдались"
    }
}
