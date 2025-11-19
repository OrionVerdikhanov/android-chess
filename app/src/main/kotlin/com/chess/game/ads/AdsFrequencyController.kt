package com.chess.game.ads

import com.chess.game.core.utils.Constants

/**
 * Контроллер частоты показа рекламы
 * Предотвращает слишком частый показ interstitial рекламы
 */
class AdsFrequencyController {

    private var lastInterstitialShowTime = 0L

    /**
     * Проверить, можно ли показать interstitial рекламу
     */
    fun canShowInterstitial(): Boolean {
        val currentTime = System.currentTimeMillis()
        val timeSinceLastShow = currentTime - lastInterstitialShowTime

        return timeSinceLastShow >= Constants.INTERSTITIAL_MIN_INTERVAL_MS
    }

    /**
     * Отметить, что interstitial реклама была показана
     */
    fun markInterstitialShown() {
        lastInterstitialShowTime = System.currentTimeMillis()
    }

    /**
     * Получить оставшееся время до следующего показа
     */
    fun getTimeUntilNextShow(): Long {
        val currentTime = System.currentTimeMillis()
        val timeSinceLastShow = currentTime - lastInterstitialShowTime
        val remaining = Constants.INTERSTITIAL_MIN_INTERVAL_MS - timeSinceLastShow

        return if (remaining > 0) remaining else 0
    }
}
