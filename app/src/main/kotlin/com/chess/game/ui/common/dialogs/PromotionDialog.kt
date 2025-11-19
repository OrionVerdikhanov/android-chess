package com.chess.game.ui.common.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.ViewGroup
import android.view.Window
import android.widget.LinearLayout
import android.widget.TextView
import com.chess.game.R
import com.chess.game.domain.model.PieceType
import com.google.android.material.button.MaterialButton

/**
 * Диалог выбора фигуры при превращении пешки
 * Показывает 4 кнопки: Ферзь, Ладья, Слон, Конь
 */
class PromotionDialog(
    context: Context,
    private val onPieceSelected: (PieceType) -> Unit
) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        // Создаем layout программно
        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 48, 48, 48)
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        // Заголовок
        val title = TextView(context).apply {
            text = context.getString(R.string.pawn_promotion_title)
            textSize = 20f
            setPadding(0, 0, 0, 32)
        }
        layout.addView(title)

        // Кнопка "Ферзь"
        layout.addView(createButton(R.string.pawn_promotion_queen, PieceType.QUEEN))

        // Кнопка "Ладья"
        layout.addView(createButton(R.string.pawn_promotion_rook, PieceType.ROOK))

        // Кнопка "Слон"
        layout.addView(createButton(R.string.pawn_promotion_bishop, PieceType.BISHOP))

        // Кнопка "Конь"
        layout.addView(createButton(R.string.pawn_promotion_knight, PieceType.KNIGHT))

        setContentView(layout)
        setCancelable(false) // Нельзя закрыть без выбора
    }

    private fun createButton(textResId: Int, pieceType: PieceType): MaterialButton {
        return MaterialButton(context).apply {
            text = context.getString(textResId)
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 0, 16)
            }
            setOnClickListener {
                onPieceSelected(pieceType)
                dismiss()
            }
        }
    }
}
