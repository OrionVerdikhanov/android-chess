package com.chess.game.core.utils

/**
 * Константы приложения
 */
object Constants {

    // Intent extras
    const val EXTRA_DIFFICULTY = "extra_difficulty"
    const val EXTRA_PLAYER_COLOR = "extra_player_color"

    // Ключи сохраненного состояния
    const val KEY_SAVED_GAME = "saved_game"

    // Частота показа рекламы
    const val INTERSTITIAL_MIN_INTERVAL_MS = 180_000L // 3 минуты

    // Анимация
    const val ANIMATION_DURATION_SHORT = 150L
    const val ANIMATION_DURATION_MEDIUM = 300L
    const val ANIMATION_DURATION_LONG = 500L

    // Вибрация
    const val VIBRATION_SHORT = 50L
    const val VIBRATION_MEDIUM = 100L
    const val VIBRATION_LONG = 200L
}
