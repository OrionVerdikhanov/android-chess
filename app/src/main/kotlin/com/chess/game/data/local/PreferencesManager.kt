package com.chess.game.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.chess.game.domain.model.GameDifficulty

/**
 * Менеджер настроек приложения
 * Управляет сохранением настроек и статистики в SharedPreferences
 */
class PreferencesManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "chess_prefs"

        // Settings keys
        private const val KEY_SOUND_ENABLED = "sound_enabled"
        private const val KEY_VIBRATION_ENABLED = "vibration_enabled"
        private const val KEY_ANIMATIONS_ENABLED = "animations_enabled"
        private const val KEY_HIGHLIGHT_LEGAL_MOVES = "highlight_legal_moves"
        private const val KEY_BOARD_THEME = "board_theme"
        private const val KEY_DARK_MODE = "dark_mode"
        private const val KEY_LAST_DIFFICULTY = "last_difficulty"

        // Statistics keys
        private const val KEY_GAMES_PLAYED = "games_played_"
        private const val KEY_GAMES_WON = "games_won_"
        private const val KEY_GAMES_LOST = "games_lost_"
        private const val KEY_GAMES_DRAW = "games_draw_"
    }

    // ========== Settings ==========

    var soundEnabled: Boolean
        get() = prefs.getBoolean(KEY_SOUND_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_SOUND_ENABLED, value).apply()

    var vibrationEnabled: Boolean
        get() = prefs.getBoolean(KEY_VIBRATION_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_VIBRATION_ENABLED, value).apply()

    var animationsEnabled: Boolean
        get() = prefs.getBoolean(KEY_ANIMATIONS_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_ANIMATIONS_ENABLED, value).apply()

    var highlightLegalMoves: Boolean
        get() = prefs.getBoolean(KEY_HIGHLIGHT_LEGAL_MOVES, true)
        set(value) = prefs.edit().putBoolean(KEY_HIGHLIGHT_LEGAL_MOVES, value).apply()

    var boardTheme: BoardTheme
        get() {
            val themeName = prefs.getString(KEY_BOARD_THEME, BoardTheme.CLASSIC.name)
            return try {
                BoardTheme.valueOf(themeName ?: BoardTheme.CLASSIC.name)
            } catch (e: IllegalArgumentException) {
                BoardTheme.CLASSIC
            }
        }
        set(value) = prefs.edit().putString(KEY_BOARD_THEME, value.name).apply()

    var darkMode: Int
        get() = prefs.getInt(KEY_DARK_MODE, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        set(value) = prefs.edit().putInt(KEY_DARK_MODE, value).apply()

    var lastDifficulty: GameDifficulty
        get() {
            val difficultyName = prefs.getString(KEY_LAST_DIFFICULTY, GameDifficulty.BEGINNER.name)
            return try {
                GameDifficulty.valueOf(difficultyName ?: GameDifficulty.BEGINNER.name)
            } catch (e: IllegalArgumentException) {
                GameDifficulty.BEGINNER
            }
        }
        set(value) = prefs.edit().putString(KEY_LAST_DIFFICULTY, value.name).apply()

    // ========== Statistics ==========

    fun getGamesPlayed(difficulty: GameDifficulty): Int {
        return prefs.getInt(KEY_GAMES_PLAYED + difficulty.name, 0)
    }

    fun getGamesWon(difficulty: GameDifficulty): Int {
        return prefs.getInt(KEY_GAMES_WON + difficulty.name, 0)
    }

    fun getGamesLost(difficulty: GameDifficulty): Int {
        return prefs.getInt(KEY_GAMES_LOST + difficulty.name, 0)
    }

    fun getGamesDraw(difficulty: GameDifficulty): Int {
        return prefs.getInt(KEY_GAMES_DRAW + difficulty.name, 0)
    }

    fun incrementGamesPlayed(difficulty: GameDifficulty) {
        val current = getGamesPlayed(difficulty)
        prefs.edit().putInt(KEY_GAMES_PLAYED + difficulty.name, current + 1).apply()
    }

    fun incrementGamesWon(difficulty: GameDifficulty) {
        incrementGamesPlayed(difficulty)
        val current = getGamesWon(difficulty)
        prefs.edit().putInt(KEY_GAMES_WON + difficulty.name, current + 1).apply()
    }

    fun incrementGamesLost(difficulty: GameDifficulty) {
        incrementGamesPlayed(difficulty)
        val current = getGamesLost(difficulty)
        prefs.edit().putInt(KEY_GAMES_LOST + difficulty.name, current + 1).apply()
    }

    fun incrementGamesDraw(difficulty: GameDifficulty) {
        incrementGamesPlayed(difficulty)
        val current = getGamesDraw(difficulty)
        prefs.edit().putInt(KEY_GAMES_DRAW + difficulty.name, current + 1).apply()
    }

    fun resetStatistics() {
        val editor = prefs.edit()
        GameDifficulty.values().forEach { difficulty ->
            editor.remove(KEY_GAMES_PLAYED + difficulty.name)
            editor.remove(KEY_GAMES_WON + difficulty.name)
            editor.remove(KEY_GAMES_LOST + difficulty.name)
            editor.remove(KEY_GAMES_DRAW + difficulty.name)
        }
        editor.apply()
    }
}
