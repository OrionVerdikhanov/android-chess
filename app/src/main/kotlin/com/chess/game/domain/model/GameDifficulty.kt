package com.chess.game.domain.model

/**
 * Уровень сложности игры против AI
 * @param displayName Название для отображения пользователю
 * @param description Описание уровня
 * @param searchDepth Глубина поиска для AI (количество полуходов)
 * @param thinkingTimeMs Время "размышления" AI в миллисекундах
 * @param useAlphaBeta Использовать альфа-бета отсечение
 * @param randomFactor Фактор случайности (0.0 - детерминированный, 1.0 - максимальная случайность)
 */
enum class GameDifficulty(
    val displayName: String,
    val description: String,
    val searchDepth: Int,
    val thinkingTimeMs: Long,
    val useAlphaBeta: Boolean,
    val randomFactor: Double
) {
    BEGINNER(
        displayName = "Новичок",
        description = "Идеально для начинающих. AI делает простые ходы.",
        searchDepth = 1,
        thinkingTimeMs = 500,
        useAlphaBeta = false,
        randomFactor = 0.3
    ),

    AMATEUR(
        displayName = "Любитель",
        description = "Подходит для игроков с базовыми знаниями.",
        searchDepth = 2,
        thinkingTimeMs = 1000,
        useAlphaBeta = true,
        randomFactor = 0.15
    ),

    EXPERIENCED(
        displayName = "Опытный",
        description = "Серьезный вызов для продвинутых игроков.",
        searchDepth = 3,
        thinkingTimeMs = 2000,
        useAlphaBeta = true,
        randomFactor = 0.05
    ),

    MASTER(
        displayName = "Мастер",
        description = "Максимальная сложность. AI играет на профессиональном уровне.",
        searchDepth = 4,
        thinkingTimeMs = 3000,
        useAlphaBeta = true,
        randomFactor = 0.0
    );

    /**
     * Получить следующий уровень сложности
     */
    fun next(): GameDifficulty? = values().getOrNull(ordinal + 1)

    /**
     * Получить предыдущий уровень сложности
     */
    fun previous(): GameDifficulty? = values().getOrNull(ordinal - 1)
}
