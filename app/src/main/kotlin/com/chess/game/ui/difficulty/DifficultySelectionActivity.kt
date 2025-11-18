package com.chess.game.ui.difficulty

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.chess.game.R
import com.chess.game.core.utils.Constants
import com.chess.game.databinding.ActivityDifficultySelectionBinding
import com.chess.game.domain.model.GameDifficulty
import com.chess.game.ui.game.GameActivity

/**
 * Экран выбора сложности игры
 */
class DifficultySelectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDifficultySelectionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDifficultySelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupDifficultyCards()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = getString(R.string.select_difficulty_title)
        }
    }

    private fun setupDifficultyCards() {
        binding.apply {
            // Новичок
            cardBeginner.setOnClickListener {
                startGame(GameDifficulty.BEGINNER)
            }

            // Любитель
            cardAmateur.setOnClickListener {
                startGame(GameDifficulty.AMATEUR)
            }

            // Опытный
            cardExperienced.setOnClickListener {
                startGame(GameDifficulty.EXPERIENCED)
            }

            // Мастер
            cardMaster.setOnClickListener {
                startGame(GameDifficulty.MASTER)
            }
        }
    }

    private fun startGame(difficulty: GameDifficulty) {
        val intent = Intent(this, GameActivity::class.java).apply {
            putExtra(Constants.EXTRA_DIFFICULTY, difficulty.name)
        }
        startActivity(intent)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}
