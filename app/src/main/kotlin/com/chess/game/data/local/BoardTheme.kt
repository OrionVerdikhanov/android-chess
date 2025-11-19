package com.chess.game.data.local

import com.chess.game.R

/**
 * Темы оформления шахматной доски
 */
enum class BoardTheme(
    val lightSquareColor: Int,
    val darkSquareColor: Int,
    val selectedSquareColor: Int,
    val legalMoveColor: Int
) {
    CLASSIC(
        lightSquareColor = R.color.board_light,
        darkSquareColor = R.color.board_dark,
        selectedSquareColor = R.color.board_selected,
        legalMoveColor = R.color.board_legal_move
    ),

    GREEN(
        lightSquareColor = R.color.board_light_green,
        darkSquareColor = R.color.board_dark_green,
        selectedSquareColor = R.color.board_selected_green,
        legalMoveColor = R.color.board_legal_move_green
    ),

    BLUE(
        lightSquareColor = R.color.board_light_blue,
        darkSquareColor = R.color.board_dark_blue,
        selectedSquareColor = R.color.board_selected_blue,
        legalMoveColor = R.color.board_legal_move_blue
    ),

    DARK(
        lightSquareColor = R.color.board_light_dark,
        darkSquareColor = R.color.board_dark_dark,
        selectedSquareColor = R.color.board_selected_dark,
        legalMoveColor = R.color.board_legal_move_dark
    )
}
