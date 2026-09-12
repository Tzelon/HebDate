package dev.tzelon.hebrewdate

import android.content.Context

/** Per-widget settings. Each widget instance keeps its own font and city. */
object WidgetPrefs {
    private const val PREFS = "widget_prefs"
    private const val FONT = "font_"
    private const val CITY = "city_"

    fun font(ctx: Context, widgetId: Int) =
        WidgetFont.named(ctx.prefs().getString(FONT + widgetId, null))

    fun city(ctx: Context, widgetId: Int) =
        City.named(ctx.prefs().getString(CITY + widgetId, null))

    fun setFont(ctx: Context, widgetId: Int, font: WidgetFont) =
        ctx.prefs().edit().putString(FONT + widgetId, font.name).apply()

    fun setCity(ctx: Context, widgetId: Int, city: City) =
        ctx.prefs().edit().putString(CITY + widgetId, city.name).apply()

    fun forget(ctx: Context, widgetIds: IntArray) = ctx.prefs().edit().apply {
        widgetIds.forEach { remove(FONT + it); remove(CITY + it) }
    }.apply()

    private fun Context.prefs() = getSharedPreferences(PREFS, Context.MODE_PRIVATE)
}
