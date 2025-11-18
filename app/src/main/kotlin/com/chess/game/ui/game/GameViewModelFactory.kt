package com.chess.game.ui.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.chess.game.data.preferences.PreferencesManager
import com.chess.game.data.sound.SoundManager
import com.chess.game.domain.model.GameDifficulty

/**
 * Factory для создания GameViewModel
 */
class GameViewModelFactory(
    private val difficulty: GameDifficulty,
    private val preferencesManager: PreferencesManager,
    private val soundManager: SoundManager
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
            return GameViewModel(difficulty, preferencesManager, soundManager) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
