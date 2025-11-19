package com.chess.game.ui.statistics

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.chess.game.R
import com.chess.game.data.local.PreferencesManager
import com.chess.game.databinding.ActivityStatisticsBinding
import com.chess.game.domain.model.GameDifficulty

/**
 * Экран статистики игр
 * Отображает статистику побед/поражений/ничьих для каждого уровня сложности
 */
class StatisticsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStatisticsBinding
    private lateinit var preferencesManager: PreferencesManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStatisticsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        preferencesManager = PreferencesManager(this)

        setupToolbar()
        loadStatistics()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = getString(R.string.statistics_title)
        }
    }

    private fun loadStatistics() {
        binding.apply {
            // Новичок
            val beginnerStats = getStatsForDifficulty(GameDifficulty.BEGINNER)
            tvBeginnerPlayed.text = getString(R.string.stats_played, beginnerStats.played)
            tvBeginnerWon.text = getString(R.string.stats_won, beginnerStats.won)
            tvBeginnerLost.text = getString(R.string.stats_lost, beginnerStats.lost)
            tvBeginnerDraw.text = getString(R.string.stats_draw, beginnerStats.draw)
            tvBeginnerWinrate.text = getString(R.string.stats_winrate, beginnerStats.winRate)

            // Любитель
            val amateurStats = getStatsForDifficulty(GameDifficulty.AMATEUR)
            tvAmateurPlayed.text = getString(R.string.stats_played, amateurStats.played)
            tvAmateurWon.text = getString(R.string.stats_won, amateurStats.won)
            tvAmateurLost.text = getString(R.string.stats_lost, amateurStats.lost)
            tvAmateurDraw.text = getString(R.string.stats_draw, amateurStats.draw)
            tvAmateurWinrate.text = getString(R.string.stats_winrate, amateurStats.winRate)

            // Опытный
            val experiencedStats = getStatsForDifficulty(GameDifficulty.EXPERIENCED)
            tvExperiencedPlayed.text = getString(R.string.stats_played, experiencedStats.played)
            tvExperiencedWon.text = getString(R.string.stats_won, experiencedStats.won)
            tvExperiencedLost.text = getString(R.string.stats_lost, experiencedStats.lost)
            tvExperiencedDraw.text = getString(R.string.stats_draw, experiencedStats.draw)
            tvExperiencedWinrate.text = getString(R.string.stats_winrate, experiencedStats.winRate)

            // Мастер
            val masterStats = getStatsForDifficulty(GameDifficulty.MASTER)
            tvMasterPlayed.text = getString(R.string.stats_played, masterStats.played)
            tvMasterWon.text = getString(R.string.stats_won, masterStats.won)
            tvMasterLost.text = getString(R.string.stats_lost, masterStats.lost)
            tvMasterDraw.text = getString(R.string.stats_draw, masterStats.draw)
            tvMasterWinrate.text = getString(R.string.stats_winrate, masterStats.winRate)

            // Общая статистика
            val totalStats = getTotalStats()
            tvTotalPlayed.text = getString(R.string.stats_played, totalStats.played)
            tvTotalWon.text = getString(R.string.stats_won, totalStats.won)
            tvTotalLost.text = getString(R.string.stats_lost, totalStats.lost)
            tvTotalDraw.text = getString(R.string.stats_draw, totalStats.draw)
            tvTotalWinrate.text = getString(R.string.stats_winrate, totalStats.winRate)
        }
    }

    private fun getStatsForDifficulty(difficulty: GameDifficulty): DifficultyStats {
        val played = preferencesManager.getGamesPlayed(difficulty)
        val won = preferencesManager.getGamesWon(difficulty)
        val lost = preferencesManager.getGamesLost(difficulty)
        val draw = preferencesManager.getGamesDraw(difficulty)

        val winRate = if (played > 0) {
            (won.toFloat() / played * 100).toInt()
        } else {
            0
        }

        return DifficultyStats(played, won, lost, draw, winRate)
    }

    private fun getTotalStats(): DifficultyStats {
        val difficulties = GameDifficulty.values()

        val totalPlayed = difficulties.sumOf { preferencesManager.getGamesPlayed(it) }
        val totalWon = difficulties.sumOf { preferencesManager.getGamesWon(it) }
        val totalLost = difficulties.sumOf { preferencesManager.getGamesLost(it) }
        val totalDraw = difficulties.sumOf { preferencesManager.getGamesDraw(it) }

        val winRate = if (totalPlayed > 0) {
            (totalWon.toFloat() / totalPlayed * 100).toInt()
        } else {
            0
        }

        return DifficultyStats(totalPlayed, totalWon, totalLost, totalDraw, winRate)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private data class DifficultyStats(
        val played: Int,
        val won: Int,
        val lost: Int,
        val draw: Int,
        val winRate: Int
    )
}
