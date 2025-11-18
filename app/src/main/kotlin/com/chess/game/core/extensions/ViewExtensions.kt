package com.chess.game.core.extensions

import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.annotation.AnimRes

/**
 * Расширения для View
 */

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun View.setVisibleOrGone(visible: Boolean) {
    visibility = if (visible) View.VISIBLE else View.GONE
}

fun View.setVisibleOrInvisible(visible: Boolean) {
    visibility = if (visible) View.VISIBLE else View.INVISIBLE
}

fun View.animateWithResource(@AnimRes animRes: Int, onEnd: (() -> Unit)? = null) {
    val animation = AnimationUtils.loadAnimation(context, animRes)
    animation.setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationStart(animation: Animation?) {}
        override fun onAnimationRepeat(animation: Animation?) {}
        override fun onAnimationEnd(animation: Animation?) {
            onEnd?.invoke()
        }
    })
    startAnimation(animation)
}

fun View.fadeIn(duration: Long = 300) {
    animate()
        .alpha(1f)
        .setDuration(duration)
        .start()
}

fun View.fadeOut(duration: Long = 300) {
    animate()
        .alpha(0f)
        .setDuration(duration)
        .start()
}
