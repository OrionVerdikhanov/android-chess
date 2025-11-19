package com.chess.game.ui.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.chess.game.R
import com.chess.game.data.local.BoardTheme
import com.chess.game.data.local.PreferencesManager
import com.chess.game.databinding.ActivitySettingsBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * Экран настроек приложения
 * Позволяет настроить звук, вибрацию, анимации, тему доски и тему приложения
 */
class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var preferencesManager: PreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferencesManager = PreferencesManager(this)

        setupToolbar()
        setupSettings()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = getString(R.string.settings_title)
        }
    }

    private fun setupSettings() {
        binding.apply {
            // Звук
            switchSound.isChecked = preferencesManager.soundEnabled
            switchSound.setOnCheckedChangeListener { _, isChecked ->
                preferencesManager.soundEnabled = isChecked
            }

            // Вибрация
            switchVibration.isChecked = preferencesManager.vibrationEnabled
            switchVibration.setOnCheckedChangeListener { _, isChecked ->
                preferencesManager.vibrationEnabled = isChecked
            }

            // Анимации
            switchAnimations.isChecked = preferencesManager.animationsEnabled
            switchAnimations.setOnCheckedChangeListener { _, isChecked ->
                preferencesManager.animationsEnabled = isChecked
            }

            // Подсветка ходов
            switchHighlightMoves.isChecked = preferencesManager.highlightLegalMoves
            switchHighlightMoves.setOnCheckedChangeListener { _, isChecked ->
                preferencesManager.highlightLegalMoves = isChecked
            }

            // Тема доски
            val boardThemes = BoardTheme.values()
            val currentThemeIndex = boardThemes.indexOf(preferencesManager.boardTheme)
            tvBoardThemeValue.text = getBoardThemeName(preferencesManager.boardTheme)

            cardBoardTheme.setOnClickListener {
                showBoardThemeDialog(boardThemes, currentThemeIndex)
            }

            // Тема приложения
            val currentMode = when (preferencesManager.darkMode) {
                AppCompatDelegate.MODE_NIGHT_YES -> getString(R.string.theme_dark)
                AppCompatDelegate.MODE_NIGHT_NO -> getString(R.string.theme_light)
                else -> getString(R.string.theme_system)
            }
            tvAppThemeValue.text = currentMode

            cardAppTheme.setOnClickListener {
                showAppThemeDialog()
            }

            // Сброс статистики
            btnResetStats.setOnClickListener {
                showResetStatsDialog()
            }
        }
    }

    private fun showBoardThemeDialog(themes: Array<BoardTheme>, currentIndex: Int) {
        val themeNames = themes.map { getBoardThemeName(it) }.toTypedArray()

        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.board_theme_title)
            .setSingleChoiceItems(themeNames, currentIndex) { dialog, which ->
                preferencesManager.boardTheme = themes[which]
                binding.tvBoardThemeValue.text = themeNames[which]
                dialog.dismiss()
            }
            .setNegativeButton(R.string.btn_cancel, null)
            .show()
    }

    private fun showAppThemeDialog() {
        val themes = arrayOf(
            getString(R.string.theme_light),
            getString(R.string.theme_dark),
            getString(R.string.theme_system)
        )

        val currentMode = preferencesManager.darkMode
        val currentIndex = when (currentMode) {
            AppCompatDelegate.MODE_NIGHT_NO -> 0
            AppCompatDelegate.MODE_NIGHT_YES -> 1
            else -> 2
        }

        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.app_theme_title)
            .setSingleChoiceItems(themes, currentIndex) { dialog, which ->
                val newMode = when (which) {
                    0 -> AppCompatDelegate.MODE_NIGHT_NO
                    1 -> AppCompatDelegate.MODE_NIGHT_YES
                    else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                }

                preferencesManager.darkMode = newMode
                AppCompatDelegate.setDefaultNightMode(newMode)
                binding.tvAppThemeValue.text = themes[which]
                dialog.dismiss()
            }
            .setNegativeButton(R.string.btn_cancel, null)
            .show()
    }

    private fun showResetStatsDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.reset_stats_title)
            .setMessage(R.string.reset_stats_message)
            .setPositiveButton(R.string.btn_reset) { _, _ ->
                preferencesManager.resetStatistics()
                binding.root.post {
                    MaterialAlertDialogBuilder(this)
                        .setMessage(R.string.stats_reset_success)
                        .setPositiveButton(R.string.btn_ok, null)
                        .show()
                }
            }
            .setNegativeButton(R.string.btn_cancel, null)
            .show()
    }

    private fun getBoardThemeName(theme: BoardTheme): String {
        return when (theme) {
            BoardTheme.CLASSIC -> getString(R.string.theme_classic)
            BoardTheme.GREEN -> getString(R.string.theme_green)
            BoardTheme.BLUE -> getString(R.string.theme_blue)
            BoardTheme.DARK -> getString(R.string.theme_dark_board)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
