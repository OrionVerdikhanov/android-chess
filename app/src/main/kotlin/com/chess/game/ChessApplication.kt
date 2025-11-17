package com.chess.game

import android.app.Application
import android.util.Log
import com.yandex.mobile.ads.common.MobileAds

/**
 * Главный класс приложения
 * Инициализирует Yandex Mobile Ads SDK и глобальные компоненты
 */
class ChessApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Инициализация Yandex Mobile Ads SDK
        initializeYandexAds()
    }

    /**
     * Инициализация Yandex Mobile Ads SDK
     * ВАЖНО: Выполняется асинхронно для оптимизации запуска приложения
     */
    private fun initializeYandexAds() {
        try {
            // Настройка политики использования данных (опционально)
            // Если нужно установить возрастные ограничения или геолокацию,
            // используйте соответствующие методы перед инициализацией

            // Инициализация SDK
            MobileAds.initialize(this) {
                Log.d(TAG, "Yandex Mobile Ads SDK успешно инициализирован")
                Log.d(TAG, "Проверьте logcat по фразе 'Yandex Ads' для деталей")
            }

            // Отключение индикатора устаревшей версии SDK (для релиза установите false)
            // По умолчанию лучше оставлять включённым для отслеживания обновлений
            MobileAds.enableDebugErrorIndicator(true)

            Log.i(TAG, "Инициализация Yandex Mobile Ads SDK запущена...")

        } catch (e: Exception) {
            Log.e(TAG, "Ошибка инициализации Yandex Mobile Ads SDK", e)
        }
    }

    companion object {
        private const val TAG = "ChessApplication"
    }
}
