package com.chess.game.ui.game.views

import android.view.MotionEvent
import com.chess.game.domain.model.Move
import com.chess.game.domain.model.Piece
import com.chess.game.domain.model.Position
import com.chess.game.domain.session.GameSession

/**
 * Обработчик Drag & Drop для шахматной доски
 * Управляет перетаскиванием фигур и выполнением ходов
 */
class DragHandler {

    var squareSize = 0f
    var boardOffset = 0f

    var selectedPosition: Position? = null
    var legalMoves: List<Move> = emptyList()
    var isDragging = false
    var draggedPiece: Piece? = null
    var dragX = 0f
    var dragY = 0f

    /**
     * Обработать касание
     */
    fun handleTouch(
        event: MotionEvent,
        session: GameSession,
        onMoveAttempt: (Position, Position) -> Unit
    ): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                val position = getTouchedPosition(event.x, event.y) ?: return false
                val piece = session.currentBoard.getPiece(position)

                if (piece != null && piece.color == session.playerColor) {
                    selectedPosition = position
                    legalMoves = session.getLegalMoves(position)
                    isDragging = true
                    draggedPiece = piece
                    dragX = event.x
                    dragY = event.y
                    return true
                }
            }

            MotionEvent.ACTION_MOVE -> {
                if (isDragging) {
                    dragX = event.x
                    dragY = event.y
                    return true
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (isDragging) {
                    val from = selectedPosition
                    val to = getTouchedPosition(event.x, event.y)

                    if (from != null && to != null && from != to) {
                        onMoveAttempt(from, to)
                    }

                    // Сбрасываем состояние
                    selectedPosition = null
                    legalMoves = emptyList()
                    isDragging = false
                    draggedPiece = null
                    return true
                }
            }
        }

        return false
    }

    /**
     * Получить позицию клетки по координатам касания
     */
    private fun getTouchedPosition(x: Float, y: Float): Position? {
        val col = ((x - boardOffset) / squareSize).toInt()
        val row = 7 - ((y - boardOffset) / squareSize).toInt()

        return if (row in 0..7 && col in 0..7) {
            Position(row, col)
        } else {
            null
        }
    }
}
