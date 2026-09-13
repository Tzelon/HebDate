package dev.tzelon.hebrewdate

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.text.Layout
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.StaticLayout
import android.text.TextDirectionHeuristics
import android.text.TextPaint
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.util.TypedValue

/**
 * Draws the widget text into a bitmap.
 *
 * A TextView's `android:fontFamily` is not honoured reliably inside RemoteViews — the launcher
 * inflates the layout in its own process and may impose its own typeface — so the text is painted
 * here, where the chosen Typeface always applies.
 */
object WidgetRenderer {

    fun render(ctx: Context, day: HebrewDay, font: WidgetFont, widthDp: Int, heightDp: Int): Bitmap {
        val width = px(ctx, widthDp.coerceIn(MIN_DP, MAX_DP))
        val height = px(ctx, heightDp.coerceIn(MIN_DP, MAX_DP))
        val pad = px(ctx, PADDING_DP)
        val lines = listOfNotNull(day.dateText, day.yearText, day.parsha, day.shabbat)

        // Shrink until every line fits on a line of its own: a wrapped "צאת החג 19:26" reads worse
        // than slightly smaller text.
        var scale = 1f
        var layout = layout(ctx, font, lines, width - 2 * pad, scale)
        while (scale > MIN_SCALE &&
            (layout.lineCount > lines.size || layout.height > height - 2 * pad)
        ) {
            scale -= SCALE_STEP
            layout = layout(ctx, font, lines, width - 2 * pad, scale)
        }

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        Canvas(bitmap).apply {
            translate(pad.toFloat(), pad.toFloat())
            layout.draw(this)
        }
        return bitmap
    }

    private fun layout(
        ctx: Context, font: WidgetFont, lines: List<String>, width: Int, scale: Float,
    ): StaticLayout {
        val paint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply {
            typeface = font.typeface(ctx)
            color = ctx.getColor(R.color.widget_text)
            textSize = sp(ctx, BODY_SP * scale)
        }
        val text = SpannableStringBuilder()
        lines.forEachIndexed { index, line ->
            if (index > 0) text.append('\n')
            val start = text.length
            text.append(line)
            val span = when (index) {
                0 -> AbsoluteSizeSpan(sp(ctx, TITLE_SP * scale).toInt(), false)
                lines.lastIndex -> ForegroundColorSpan(ctx.getColor(R.color.widget_text_dim))
                else -> null
            }
            span?.let { text.setSpan(it, start, text.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE) }
        }
        return StaticLayout.Builder
            .obtain(text, 0, text.length, paint, width.coerceAtLeast(1))
            .setAlignment(Layout.Alignment.ALIGN_NORMAL)
            .setTextDirection(TextDirectionHeuristics.RTL)
            .setIncludePad(false)
            .build()
    }

    private fun px(ctx: Context, dp: Int) = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), ctx.resources.displayMetrics,
    ).toInt()

    private fun sp(ctx: Context, sp: Float) = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP, sp, ctx.resources.displayMetrics,
    )

    private const val PADDING_DP = 18
    private const val MIN_DP = 80
    private const val MAX_DP = 480
    private const val TITLE_SP = 20f
    private const val BODY_SP = 16f
    private const val MIN_SCALE = 0.6f
    private const val SCALE_STEP = 0.05f
}
