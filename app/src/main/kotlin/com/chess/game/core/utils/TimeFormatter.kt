package com.chess.game.core.utils

/**
 * Форматирование времени для шахматных часов
 */
object TimeFormatter {

    /**
     * Форматировать миллисекунды в формат ММ:СС
     */
    fun formatTime(milliseconds: Long): String {
        if (milliseconds <= 0) return "00:00"

        val totalSeconds = milliseconds / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60

        return String.format("%02d:%02d", minutes, seconds)
    }

    /**
     * Форматировать миллисекунды в формат ЧЧ:ММ:СС
     */
    fun formatTimeLong(milliseconds: Long): String {
        if (milliseconds <= 0) return "00:00:00"

        val totalSeconds = milliseconds / 1000
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60

        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }
}
