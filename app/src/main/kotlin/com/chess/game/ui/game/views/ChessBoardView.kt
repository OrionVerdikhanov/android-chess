package com.chess.game.ui.game.views

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import com.chess.game.domain.model.BoardTheme
import com.chess.game.domain.model.Position
import com.chess.game.domain.session.GameSession

/**
 * Custom View для отрисовки шахматной доски
 * Отображает доску, фигуры, подсветку возможных ходов и обрабатывает Drag & Drop
 */
class ChessBoardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // Рендереры
    private val pieceRenderer = PieceRenderer(context)
    private val highlightRenderer = HighlightRenderer(context)

    // Обработчик drag & drop
    private val dragHandler = DragHandler()

    // Состояние
    private var _gameSession: GameSession? = null

    /**
     * Текущая игровая сессия
     * Публичный сеттер для обновления UI
     */
    var gameSession: GameSession?
        get() = _gameSession
        set(value) {
            _gameSession = value
            invalidate()
        }

    var boardTheme: BoardTheme = BoardTheme.CLASSIC
        set(value) {
            field = value
            highlightRenderer.theme = value
            invalidate()
        }

    var showLegalMoves: Boolean = true

    // Callback'и
    var onMoveAttempt: ((Position, Position) -> Unit)? = null

    // Размеры
    private var squareSize = 0f
    private var boardOffset = 0f

    // Paint для отрисовки доски
    private val lightSquarePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val darkSquarePaint = Paint(Paint.ANTI_ALIAS_FLAG)

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        val size = minOf(w, h).toFloat()
        squareSize = size / 8f
        boardOffset = (w - size) / 2f

        dragHandler.squareSize = squareSize
        dragHandler.boardOffset = boardOffset
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Рисуем доску
        drawBoard(canvas)

        // Рисуем подсветку
        _gameSession?.let { session ->
            highlightRenderer.draw(
                canvas = canvas,
                selectedPosition = dragHandler.selectedPosition,
                legalMoves = if (showLegalMoves) dragHandler.legalMoves else emptyList(),
                lastMove = session.moveHistory.getMoves().lastOrNull(),
                squareSize = squareSize,
                boardOffset = boardOffset
            )
        }

        // Рисуем фигуры
        drawPieces(canvas)

        // Рисуем перетаскиваемую фигуру
        dragHandler.draggedPiece?.let { piece ->
            pieceRenderer.drawPiece(
                canvas = canvas,
                piece = piece,
                x = dragHandler.dragX - squareSize / 2,
                y = dragHandler.dragY - squareSize / 2,
                size = squareSize
            )
        }
    }

    private fun drawBoard(canvas: Canvas) {
        updateBoardColors()

        for (row in 0..7) {
            for (col in 0..7) {
                val x = boardOffset + col * squareSize
                val y = boardOffset + (7 - row) * squareSize

                val paint = if ((row + col) % 2 == 0) lightSquarePaint else darkSquarePaint
                canvas.drawRect(x, y, x + squareSize, y + squareSize, paint)
            }
        }
    }

    private fun updateBoardColors() {
        lightSquarePaint.color = ContextCompat.getColor(context, boardTheme.lightSquareColor)
        darkSquarePaint.color = ContextCompat.getColor(context, boardTheme.darkSquareColor)
    }

    private fun drawPieces(canvas: Canvas) {
        val board = _gameSession?.currentBoard ?: return

        for (row in 0..7) {
            for (col in 0..7) {
                val position = Position(row, col)

                // Не рисуем перетаскиваемую фигуру
                if (position == dragHandler.selectedPosition && dragHandler.isDragging) {
                    continue
                }

                val piece = board.getPiece(position) ?: continue
                val x = boardOffset + col * squareSize
                val y = boardOffset + (7 - row) * squareSize

                pieceRenderer.drawPiece(canvas, piece, x, y, squareSize)
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val session = _gameSession ?: return false

        return dragHandler.handleTouch(event, session) { from, to ->
            onMoveAttempt?.invoke(from, to)
            invalidate()
        }.also {
            if (it) invalidate()
        }
    }

    /**
     * Обновить отображение доски
     */
    fun refresh() {
        invalidate()
    }
}
