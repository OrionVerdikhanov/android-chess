package com.chess.game.data.local

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.chess.game.R

/**
 * Менеджер звуковых эффектов и вибрации
 */
class SoundManager(private val context: Context) {

    private val soundPool: SoundPool
    private val vibrator: Vibrator
    private val soundIds = mutableMapOf<SoundType, Int>()

    init {
        // Инициализация SoundPool
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(5)
            .setAudioAttributes(audioAttributes)
            .build()

        // Получение Vibrator
        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }

        // Загрузка звуков (закомментировано, так как файлов звуков нет)
        // soundIds[SoundType.MOVE] = soundPool.load(context, R.raw.move, 1)
        // soundIds[SoundType.CAPTURE] = soundPool.load(context, R.raw.capture, 1)
        // soundIds[SoundType.CHECK] = soundPool.load(context, R.raw.check, 1)
        // soundIds[SoundType.CHECKMATE] = soundPool.load(context, R.raw.checkmate, 1)
        // soundIds[SoundType.ERROR] = soundPool.load(context, R.raw.error, 1)
    }

    /**
     * Воспроизвести звук
     */
    fun playSound(type: SoundType) {
        soundIds[type]?.let { soundId ->
            soundPool.play(soundId, 1f, 1f, 1, 0, 1f)
        }
    }

    /**
     * Вибрация
     */
    fun vibrate(duration: Long = 50) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(duration)
        }
    }

    /**
     * Освободить ресурсы
     */
    fun release() {
        soundPool.release()
    }

    /**
     * Типы звуков
     */
    enum class SoundType {
        MOVE,
        CAPTURE,
        CHECK,
        CHECKMATE,
        ERROR
    }
}
