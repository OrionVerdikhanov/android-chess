package com.chess.game.domain.engine

import com.chess.game.domain.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.random.Random

/**
 * AI движок для шахмат
 * Использует алгоритм Minimax с Alpha-Beta отсечением
 */
class ChessAI(
    private val difficulty: GameDifficulty,
    private val chessEngine: ChessEngine = ChessEngine(),
    private val evaluator: BoardEvaluator = BoardEvaluator()
) {

    /**
     * Найти лучший ход для AI
     * @param board Текущая позиция
     * @return Лучший ход или null, если ходов нет
     */
    suspend fun findBestMove(board: ChessBoard): Move? = withContext(Dispatchers.Default) {
        // Имитация "размышления" AI
        delay(difficulty.thinkingTimeMs)

        val legalMoves = chessEngine.getAllLegalMoves(board, board.currentPlayer)
        if (legalMoves.isEmpty()) return@withContext null

        return@withContext when (difficulty) {
            GameDifficulty.BEGINNER -> findBeginnerMove(legalMoves)
            GameDifficulty.AMATEUR -> findAmateurMove(board, legalMoves)
            GameDifficulty.EXPERIENCED -> findExperiencedMove(board, legalMoves)
            GameDifficulty.MASTER -> findMasterMove(board, legalMoves)
        }
    }

    /**
     * НОВИЧОК: Случайный валидный ход с небольшим предпочтением захвата фигур
     */
    private fun findBeginnerMove(legalMoves: List<Move>): Move {
        // 70% случайный ход, 30% захват (если есть)
        val captures = legalMoves.filter { it.isCapture() }

        return if (captures.isNotEmpty() && Random.nextDouble() > difficulty.randomFactor) {
            captures.random()
        } else {
            legalMoves.random()
        }
    }

    /**
     * ЛЮБИТЕЛЬ: Minimax глубина 2 с базовой оценкой
     */
    private fun findAmateurMove(board: ChessBoard, legalMoves: List<Move>): Move {
        var bestMove = legalMoves.first()
        var bestScore = Int.MIN_VALUE

        for (move in legalMoves) {
            val newBoard = board.makeMove(move)
            val score = -minimax(newBoard, difficulty.searchDepth - 1, false)

            if (score > bestScore) {
                bestScore = score
                bestMove = move
            }
        }

        // Добавляем случайность
        if (Random.nextDouble() < difficulty.randomFactor) {
            return legalMoves.random()
        }

        return bestMove
    }

    /**
     * ОПЫТНЫЙ: Minimax с alpha-beta отсечением, глубина 3
     */
    private fun findExperiencedMove(board: ChessBoard, legalMoves: List<Move>): Move {
        var bestMove = legalMoves.first()
        var bestScore = Int.MIN_VALUE
        var alpha = Int.MIN_VALUE
        val beta = Int.MAX_VALUE

        for (move in legalMoves) {
            val newBoard = board.makeMove(move)
            val score = -alphaBeta(newBoard, difficulty.searchDepth - 1, -beta, -alpha, false)

            if (score > bestScore) {
                bestScore = score
                bestMove = move
            }

            alpha = maxOf(alpha, score)
        }

        // Минимальная случайность
        if (Random.nextDouble() < difficulty.randomFactor) {
            val topMoves = legalMoves.shuffled().take(3)
            return topMoves.random()
        }

        return bestMove
    }

    /**
     * МАСТЕР: Alpha-beta глубина 4-5, продвинутая оценка
     */
    private fun findMasterMove(board: ChessBoard, legalMoves: List<Move>): Move {
        var bestMove = legalMoves.first()
        var bestScore = Int.MIN_VALUE
        var alpha = Int.MIN_VALUE
        val beta = Int.MAX_VALUE

        // Сортируем ходы для лучшего отсечения (captures first)
        val sortedMoves = legalMoves.sortedByDescending { move ->
            when {
                move.isCapture() -> 1000 + (move.capturedPiece?.type?.value ?: 0)
                move.isCastling -> 500
                else -> 0
            }
        }

        for (move in sortedMoves) {
            val newBoard = board.makeMove(move)
            val score = -alphaBeta(newBoard, difficulty.searchDepth - 1, -beta, -alpha, false)

            if (score > bestScore) {
                bestScore = score
                bestMove = move
            }

            alpha = maxOf(alpha, score)

            // Alpha-beta отсечение
            if (alpha >= beta) break
        }

        return bestMove
    }

    /**
     * Алгоритм Minimax (без отсечения)
     */
    private fun minimax(board: ChessBoard, depth: Int, isMaximizing: Boolean): Int {
        if (depth == 0) {
            return evaluator.evaluate(board, board.currentPlayer.opposite())
        }

        val legalMoves = chessEngine.getAllLegalMoves(board, board.currentPlayer)
        if (legalMoves.isEmpty()) {
            // Конец игры
            return when {
                chessEngine.isCheckmate(board, board.currentPlayer) -> -20000
                else -> 0 // Пат
            }
        }

        if (isMaximizing) {
            var maxScore = Int.MIN_VALUE
            for (move in legalMoves) {
                val newBoard = board.makeMove(move)
                val score = minimax(newBoard, depth - 1, false)
                maxScore = maxOf(maxScore, score)
            }
            return maxScore
        } else {
            var minScore = Int.MAX_VALUE
            for (move in legalMoves) {
                val newBoard = board.makeMove(move)
                val score = minimax(newBoard, depth - 1, true)
                minScore = minOf(minScore, score)
            }
            return minScore
        }
    }

    /**
     * Алгоритм Alpha-Beta отсечение (оптимизированный Minimax)
     */
    private fun alphaBeta(board: ChessBoard, depth: Int, alpha: Int, beta: Int, isMaximizing: Boolean): Int {
        if (depth == 0) {
            return evaluator.evaluate(board, board.currentPlayer.opposite())
        }

        val legalMoves = chessEngine.getAllLegalMoves(board, board.currentPlayer)
        if (legalMoves.isEmpty()) {
            return when {
                chessEngine.isCheckmate(board, board.currentPlayer) -> -20000 + (difficulty.searchDepth - depth)
                else -> 0
            }
        }

        var currentAlpha = alpha

        if (isMaximizing) {
            var maxScore = Int.MIN_VALUE
            for (move in legalMoves) {
                val newBoard = board.makeMove(move)
                val score = alphaBeta(newBoard, depth - 1, currentAlpha, beta, false)
                maxScore = maxOf(maxScore, score)
                currentAlpha = maxOf(currentAlpha, score)

                if (beta <= currentAlpha) break // Beta отсечение
            }
            return maxScore
        } else {
            var minScore = Int.MAX_VALUE
            for (move in legalMoves) {
                val newBoard = board.makeMove(move)
                val score = alphaBeta(newBoard, depth - 1, currentAlpha, beta, true)
                minScore = minOf(minScore, score)

                if (beta <= currentAlpha) break // Alpha отсечение
            }
            return minScore
        }
    }

    /**
     * Получить подсказку для игрока
     */
    suspend fun getHint(board: ChessBoard): Move? {
        return findBestMove(board)
    }
}
