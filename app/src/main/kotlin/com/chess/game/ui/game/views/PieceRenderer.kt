package com.chess.game.ui.game.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import androidx.core.content.res.ResourcesCompat
import com.chess.game.domain.model.Piece

/**
 * Рендерер шахматных фигур
 * Отрисовывает фигуры используя Unicode символы
 */
class PieceRenderer(private val context: Context) {

    private val piecePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        typeface = Typeface.DEFAULT_BOLD
    }

    /**
     * Отрисовать фигуру
     */
    fun drawPiece(canvas: Canvas, piece: Piece, x: Float, y: Float, size: Float) {
        piecePaint.textSize = size * 0.75f

        val symbol = piece.getSymbol()
        val centerX = x + size / 2
        val centerY = y + size / 2 - (piecePaint.descent() + piecePaint.ascent()) / 2

        canvas.drawText(symbol, centerX, centerY, piecePaint)
    }
}
