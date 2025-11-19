package com.chess.game.data.repository

import android.content.Context
import com.chess.game.domain.model.ChessBoard
import com.chess.game.domain.model.GameDifficulty
import com.chess.game.domain.model.Move
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * Репозиторий для сохранения и загрузки игр
 * Использует JSON файлы для хранения
 */
class GameRepository(private val context: Context) {

    private val savedGamesDir: File
        get() = File(context.filesDir, "saved_games").apply {
            if (!exists()) mkdirs()
        }

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    /**
     * Сохранить текущую игру
     */
    suspend fun saveGame(
        board: ChessBoard,
        difficulty: GameDifficulty,
        moveHistory: List<Move>
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val gameId = "game_${System.currentTimeMillis()}"
            val file = File(savedGamesDir, "$gameId.json")

            val jsonObject = JSONObject().apply {
                put("id", gameId)
                put("difficulty", difficulty.name)
                put("savedAt", dateFormat.format(Date()))
                put("currentPlayer", board.currentPlayer.name)
                put("moveCount", moveHistory.size)
                put("moves", serializeMoves(moveHistory))
            }

            file.writeText(jsonObject.toString())
            Result.success(gameId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Загрузить игру
     */
    suspend fun loadGame(gameId: String): Result<SavedGameData> = withContext(Dispatchers.IO) {
        try {
            val file = File(savedGamesDir, "$gameId.json")
            if (!file.exists()) {
                return@withContext Result.failure(Exception("Игра не найдена"))
            }

            val jsonObject = JSONObject(file.readText())
            val difficulty = GameDifficulty.valueOf(jsonObject.getString("difficulty"))
            val moves = deserializeMoves(jsonObject.getJSONArray("moves"))

            Result.success(SavedGameData(
                id = jsonObject.getString("id"),
                difficulty = difficulty,
                savedAt = jsonObject.getString("savedAt"),
                moves = moves
            ))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Получить список сохраненных игр
     */
    suspend fun getSavedGames(): List<SavedGameInfo> = withContext(Dispatchers.IO) {
        savedGamesDir.listFiles()
            ?.filter { it.extension == "json" }
            ?.mapNotNull { file ->
                try {
                    val jsonObject = JSONObject(file.readText())
                    SavedGameInfo(
                        id = jsonObject.getString("id"),
                        difficulty = GameDifficulty.valueOf(jsonObject.getString("difficulty")),
                        savedAt = jsonObject.getString("savedAt"),
                        moveCount = jsonObject.getInt("moveCount")
                    )
                } catch (e: Exception) {
                    null
                }
            }
            ?.sortedByDescending { it.savedAt }
            ?: emptyList()
    }

    /**
     * Удалить сохраненную игру
     */
    suspend fun deleteGame(gameId: String): Boolean = withContext(Dispatchers.IO) {
        val file = File(savedGamesDir, "$gameId.json")
        file.delete()
    }

    /**
     * Сериализация ходов в JSON
     */
    private fun serializeMoves(moves: List<Move>): JSONArray {
        val jsonArray = JSONArray()
        moves.forEach { move ->
            jsonArray.put(JSONObject().apply {
                put("from", move.from.toAlgebraic())
                put("to", move.to.toAlgebraic())
                put("piece", move.piece.type.name)
                put("color", move.piece.color.name)
                if (move.isCapture()) put("capture", true)
                if (move.isCastling) put("castling", true)
                if (move.isEnPassant) put("enPassant", true)
                if (move.isPromotion) put("promotion", move.promotionPiece?.name)
            })
        }
        return jsonArray
    }

    /**
     * Десериализация ходов из JSON
     */
    private fun deserializeMoves(jsonArray: JSONArray): List<String> {
        val moves = mutableListOf<String>()
        for (i in 0 until jsonArray.length()) {
            val moveObj = jsonArray.getJSONObject(i)
            moves.add(moveObj.getString("from") + moveObj.getString("to"))
        }
        return moves
    }

    /**
     * Информация о сохраненной игре (краткая)
     */
    data class SavedGameInfo(
        val id: String,
        val difficulty: GameDifficulty,
        val savedAt: String,
        val moveCount: Int
    )

    /**
     * Полные данные сохраненной игры
     */
    data class SavedGameData(
        val id: String,
        val difficulty: GameDifficulty,
        val savedAt: String,
        val moves: List<String>
    )
}
