package com.chess.game.ads

import android.app.Activity
import android.util.Log
import android.view.ViewGroup
import com.yandex.mobile.ads.banner.AdSize
import com.yandex.mobile.ads.banner.BannerAdView
import com.yandex.mobile.ads.common.AdRequest
import com.yandex.mobile.ads.common.AdRequestError
import com.yandex.mobile.ads.common.ImpressionData
import com.yandex.mobile.ads.interstitial.InterstitialAd
import com.yandex.mobile.ads.interstitial.InterstitialAdEventListener
import com.yandex.mobile.ads.rewarded.Reward
import com.yandex.mobile.ads.rewarded.RewardedAd
import com.yandex.mobile.ads.rewarded.RewardedAdEventListener

/**
 * Менеджер рекламы Yandex Mobile Ads SDK
 * Управляет загрузкой и показом баннеров, межстраничной и rewarded рекламы
 *
 * ВАЖНО: Замените тестовые ID на реальные из кабинета РСЯ/Adfox!
 */
class AdsManager(private val activity: Activity) {

    // ========== ТЕСТОВЫЕ ID РЕКЛАМНЫХ БЛОКОВ ==========
    // ЗАМЕНИТЕ ИХ НА РЕАЛЬНЫЕ ИЗ КАБИНЕТА РСЯ/ADFOX!
    companion object {
        private const val TAG = "AdsManager"

        // Тестовый ID баннера (замените на реальный)
        private const val BANNER_AD_UNIT_ID = "R-M-DEMO-320x50"

        // Тестовый ID межстраничной рекламы (замените на реальный)
        private const val INTERSTITIAL_AD_UNIT_ID = "R-M-DEMO-interstitial"

        // Тестовый ID rewarded рекламы (замените на реальный)
        private const val REWARDED_AD_UNIT_ID = "R-M-DEMO-rewarded-client-side-rtb"
    }

    // Текущие объекты рекламы
    private var bannerAdView: BannerAdView? = null
    private var interstitialAd: InterstitialAd? = null
    private var rewardedAd: RewardedAd? = null

    // Флаги состояния
    private var isInterstitialLoaded = false
    private var isRewardedLoaded = false

    // Контроллер частоты показов
    private val frequencyController = AdsFrequencyController()

    /**
     * Загрузить и показать адаптивный баннер
     * @param container ViewGroup контейнер для баннера
     */
    fun loadBanner(container: ViewGroup) {
        try {
            Log.d(TAG, "Загрузка баннера...")

            // Создаем BannerAdView
            bannerAdView = BannerAdView(activity).apply {
                setAdUnitId(BANNER_AD_UNIT_ID)
                setAdSize(AdSize.stickySize(activity))

                // Слушатель событий (опционально)
                setBannerAdEventListener(object : com.yandex.mobile.ads.banner.BannerAdEventListener {
                    override fun onAdLoaded() {
                        Log.d(TAG, "Баннер загружен успешно")
                    }

                    override fun onAdFailedToLoad(error: AdRequestError) {
                        Log.e(TAG, "Ошибка загрузки баннера: ${error.description}")
                    }

                    override fun onAdClicked() {
                        Log.d(TAG, "Клик по баннеру")
                    }

                    override fun onImpression(impressionData: ImpressionData?) {
                        Log.d(TAG, "Показ баннера")
                    }

                    override fun onLeftApplication() {
                        Log.d(TAG, "Выход из приложения через баннер")
                    }

                    override fun onReturnedToApplication() {
                        Log.d(TAG, "Возврат в приложение")
                    }
                })
            }

            // Добавляем в контейнер
            container.addView(bannerAdView)

            // Загружаем рекламу
            bannerAdView?.loadAd(AdRequest.Builder().build())

        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при создании баннера", e)
        }
    }

    /**
     * Уничтожить баннер
     */
    fun destroyBanner() {
        bannerAdView?.destroy()
        bannerAdView = null
        Log.d(TAG, "Баннер уничтожен")
    }

    /**
     * Загрузить межстраничную рекламу
     */
    fun loadInterstitial() {
        try {
            Log.d(TAG, "Загрузка межстраничной рекламы...")

            interstitialAd = InterstitialAd(activity).apply {
                setAdUnitId(INTERSTITIAL_AD_UNIT_ID)

                setInterstitialAdEventListener(object : InterstitialAdEventListener {
                    override fun onAdLoaded() {
                        isInterstitialLoaded = true
                        Log.d(TAG, "Межстраничная реклама загружена")
                    }

                    override fun onAdFailedToLoad(error: AdRequestError) {
                        isInterstitialLoaded = false
                        Log.e(TAG, "Ошибка загрузки межстраничной рекламы: ${error.description}")
                    }

                    override fun onAdShown() {
                        Log.d(TAG, "Межстраничная реклама показана")
                    }

                    override fun onAdDismissed() {
                        isInterstitialLoaded = false
                        Log.d(TAG, "Межстраничная реклама закрыта")
                        // Предзагружаем следующую
                        loadInterstitial()
                    }

                    override fun onAdClicked() {
                        Log.d(TAG, "Клик по межстраничной рекламе")
                    }

                    override fun onImpression(impressionData: ImpressionData?) {
                        Log.d(TAG, "Показ межстраничной рекламы")
                    }

                    override fun onLeftApplication() {
                        Log.d(TAG, "Выход из приложения через interstitial")
                    }

                    override fun onReturnedToApplication() {
                        Log.d(TAG, "Возврат в приложение")
                    }
                })

                loadAd(AdRequest.Builder().build())
            }

        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при загрузке межстраничной рекламы", e)
        }
    }

    /**
     * Показать межстраничную рекламу (если загружена)
     */
    fun showInterstitial() {
        if (isInterstitialLoaded && frequencyController.canShowInterstitial()) {
            interstitialAd?.show()
            frequencyController.markInterstitialShown()
        } else if (!frequencyController.canShowInterstitial()) {
            val timeLeft = frequencyController.getTimeUntilNextShow() / 1000
            Log.w(TAG, "Межстраничная реклама заблокирована контроллером частоты (осталось ${timeLeft}с)")
        } else {
            Log.w(TAG, "Межстраничная реклама еще не загружена")
            loadInterstitial()
        }
    }

    /**
     * Загрузить rewarded рекламу
     */
    fun loadRewarded() {
        try {
            Log.d(TAG, "Загрузка rewarded рекламы...")

            rewardedAd = RewardedAd(activity).apply {
                setAdUnitId(REWARDED_AD_UNIT_ID)

                setRewardedAdEventListener(object : RewardedAdEventListener {
                    override fun onAdLoaded() {
                        isRewardedLoaded = true
                        Log.d(TAG, "Rewarded реклама загружена")
                    }

                    override fun onAdFailedToLoad(error: AdRequestError) {
                        isRewardedLoaded = false
                        Log.e(TAG, "Ошибка загрузки rewarded рекламы: ${error.description}")
                    }

                    override fun onAdShown() {
                        Log.d(TAG, "Rewarded реклама показана")
                    }

                    override fun onAdDismissed() {
                        isRewardedLoaded = false
                        Log.d(TAG, "Rewarded реклама закрыта")
                        // Предзагружаем следующую
                        loadRewarded()
                    }

                    override fun onRewarded(reward: Reward) {
                        Log.d(TAG, "Вознаграждение получено: ${reward.amount} ${reward.type}")
                    }

                    override fun onAdClicked() {
                        Log.d(TAG, "Клик по rewarded рекламе")
                    }

                    override fun onImpression(impressionData: ImpressionData?) {
                        Log.d(TAG, "Показ rewarded рекламы")
                    }

                    override fun onLeftApplication() {
                        Log.d(TAG, "Выход из приложения через rewarded")
                    }

                    override fun onReturnedToApplication() {
                        Log.d(TAG, "Возврат в приложение")
                    }
                })

                loadAd(AdRequest.Builder().build())
            }

        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при загрузке rewarded рекламы", e)
        }
    }

    /**
     * Показать rewarded рекламу с callback'ом вознаграждения
     */
    fun showRewarded(onRewardEarned: () -> Unit) {
        if (isRewardedLoaded) {
            rewardedAd?.setRewardedAdEventListener(object : RewardedAdEventListener {
                override fun onAdLoaded() {}
                override fun onAdFailedToLoad(error: AdRequestError) {}
                override fun onAdShown() {}

                override fun onAdDismissed() {
                    isRewardedLoaded = false
                    loadRewarded()
                }

                override fun onRewarded(reward: Reward) {
                    Log.d(TAG, "Пользователь получил вознаграждение!")
                    onRewardEarned()
                }

                override fun onAdClicked() {}
                override fun onImpression(impressionData: ImpressionData?) {}
                override fun onLeftApplication() {}
                override fun onReturnedToApplication() {}
            })

            rewardedAd?.show()
        } else {
            Log.w(TAG, "Rewarded реклама еще не загружена")
            loadRewarded()
        }
    }

    /**
     * Освободить ресурсы
     */
    fun destroy() {
        destroyBanner()
        interstitialAd?.setInterstitialAdEventListener(null)
        interstitialAd = null
        rewardedAd?.setRewardedAdEventListener(null)
        rewardedAd = null
        Log.d(TAG, "AdsManager уничтожен")
    }
}
