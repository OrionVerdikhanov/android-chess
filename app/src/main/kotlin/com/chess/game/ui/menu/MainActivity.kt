package com.chess.game.ui.menu

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.chess.game.R
import com.chess.game.ads.AdsManager
import com.chess.game.core.extensions.toast
import com.chess.game.databinding.ActivityMainBinding
import com.chess.game.ui.difficulty.DifficultySelectionActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * Главное меню приложения
 * Отображает кнопки навигации и рекламный баннер
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var adsManager: AdsManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        setupAds()
    }

    private fun setupUI() {
        binding.apply {
            // Кнопка "Играть"
            btnPlay.setOnClickListener {
                startActivity(Intent(this@MainActivity, DifficultySelectionActivity::class.java))
            }

            // Кнопка "Настройки"
            btnSettings.setOnClickListener {
                toast("Настройки в разработке")
                // TODO: Открыть настройки
            }

            // Кнопка "Статистика"
            btnStatistics.setOnClickListener {
                toast("Статистика в разработке")
                // TODO: Открыть статистику
            }

            // Кнопка "О игре"
            btnAbout.setOnClickListener {
                showAboutDialog()
            }
        }
    }

    private fun setupAds() {
        adsManager = AdsManager(this).apply {
            loadBanner(binding.adContainer)
            loadInterstitial()
        }
    }

    private fun showAboutDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.about_title)
            .setMessage(getString(R.string.about_description))
            .setPositiveButton(R.string.btn_ok, null)
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        adsManager?.destroy()
    }
}
