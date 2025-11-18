package com.chess.game.ui.game.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import androidx.core.content.ContextCompat
import com.chess.game.domain.model.BoardTheme
import com.chess.game.domain.model.Move
import com.chess.game.domain.model.Position

/**
 * Рендерер подсветки клеток доски
 * Подсвечивает выбранную фигуру, возможные ходы, последний ход
 */
class HighlightRenderer(private val context: Context) {

    var theme: BoardTheme = BoardTheme.CLASSIC

    private val selectedPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        alpha = 120
    }

    private val legalMovePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        alpha = 100
    }

    private val lastMovePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 8f
        alpha = 180
    }

    /**
     * Отрисовать подсветки
     */
    fun draw(
        canvas: Canvas,
        selectedPosition: Position?,
        legalMoves: List<Move>,
        lastMove: Move?,
        squareSize: Float,
        boardOffset: Float
    ) {
        // Обновляем цвета под тему
        updateColors()

        // Подсветка последнего хода
        lastMove?.let {
            drawSquareHighlight(canvas, it.from, lastMovePaint, squareSize, boardOffset)
            drawSquareHighlight(canvas, it.to, lastMovePaint, squareSize, boardOffset)
        }

        // Подсветка выбранной фигуры
        selectedPosition?.let {
            drawSquareHighlight(canvas, it, selectedPaint, squareSize, boardOffset)
        }

        // Подсветка возможных ходов
        legalMoves.forEach { move ->
            drawLegalMoveIndicator(canvas, move.to, squareSize, boardOffset)
        }
    }

    private fun drawSquareHighlight(
        canvas: Canvas,
        position: Position,
        paint: Paint,
        squareSize: Float,
        boardOffset: Float
    ) {
        val x = boardOffset + position.col * squareSize
        val y = boardOffset + (7 - position.row) * squareSize

        canvas.drawRect(x, y, x + squareSize, y + squareSize, paint)
    }

    private fun drawLegalMoveIndicator(
        canvas: Canvas,
        position: Position,
        squareSize: Float,
        boardOffset: Float
    ) {
        val x = boardOffset + position.col * squareSize + squareSize / 2
        val y = boardOffset + (7 - position.row) * squareSize + squareSize / 2
        val radius = squareSize * 0.15f

        canvas.drawCircle(x, y, radius, legalMovePaint)
    }

    private fun updateColors() {
        selectedPaint.color = ContextCompat.getColor(context, theme.selectedColor)
        legalMovePaint.color = ContextCompat.getColor(context, theme.legalMoveColor)
        lastMovePaint.color = ContextCompat.getColor(context, theme.selectedColor)
    }
}
