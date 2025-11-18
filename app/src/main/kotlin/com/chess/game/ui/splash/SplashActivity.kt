package com.chess.game.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.chess.game.R
import com.chess.game.ui.menu.MainActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Splash экран
 * Отображается при запуске приложения, загружает SDK и переходит в главное меню
 */
@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Задержка для отображения splash и инициализации SDK
        lifecycleScope.launch {
            delay(2000) // 2 секунды

            // Переход в главное меню
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            finish()
        }
    }
}
