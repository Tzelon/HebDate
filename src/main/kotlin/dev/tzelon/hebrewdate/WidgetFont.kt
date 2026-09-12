package dev.tzelon.hebrewdate

import android.content.Context
import android.graphics.Typeface

/** Typeface the widget draws with, chosen per widget instance. */
enum class WidgetFont {
    DOTS, SYSTEM;

    fun typeface(ctx: Context): Typeface =
        if (this == DOTS) ctx.resources.getFont(R.font.hebdot) else Typeface.DEFAULT

    companion object {
        private const val PREFS = "widget_prefs"
        private const val KEY = "font_"

        fun of(ctx: Context, widgetId: Int): WidgetFont {
            val name = ctx.prefs().getString(KEY + widgetId, null) ?: return DOTS
            return entries.firstOrNull { it.name == name } ?: DOTS
        }

        fun store(ctx: Context, widgetId: Int, font: WidgetFont) =
            ctx.prefs().edit().putString(KEY + widgetId, font.name).apply()

        fun forget(ctx: Context, widgetIds: IntArray) = ctx.prefs().edit().apply {
            widgetIds.forEach { remove(KEY + it) }
        }.apply()

        private fun Context.prefs() = getSharedPreferences(PREFS, Context.MODE_PRIVATE)
    }
}
