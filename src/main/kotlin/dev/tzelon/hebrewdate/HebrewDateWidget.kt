package dev.tzelon.hebrewdate

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews

class HebrewDateWidget : AppWidgetProvider() {

    override fun onUpdate(ctx: Context, mgr: AppWidgetManager, ids: IntArray) {
        val d = HebrewDayProvider.compute()
        ids.forEach { id ->
            val rv = RemoteViews(ctx.packageName, R.layout.widget_hebrew_date).apply {
                setTextViewText(R.id.day, d.dateText)
                setTextViewText(R.id.year, d.yearText)
                setTextViewText(R.id.parsha, d.parsha)
                setTextViewText(R.id.shabbat, d.shabbat ?: "")
                setViewVisibility(R.id.shabbat, if (d.shabbat != null) View.VISIBLE else View.GONE)
            }
            mgr.updateAppWidget(id, rv)
        }
        val am = ctx.getSystemService(AlarmManager::class.java)
        val pi = PendingIntent.getBroadcast(
            ctx, 0, Intent(ctx, javaClass).setAction(ACTION_TICK),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        am.setAndAllowWhileIdle(AlarmManager.RTC, d.nextTick.time, pi)
    }

    override fun onReceive(ctx: Context, intent: Intent) {
        super.onReceive(ctx, intent)
        if (intent.action in setOf(ACTION_TICK, Intent.ACTION_DATE_CHANGED,
                Intent.ACTION_TIMEZONE_CHANGED, Intent.ACTION_TIME_CHANGED)) {
            val mgr = AppWidgetManager.getInstance(ctx)
            onUpdate(ctx, mgr, mgr.getAppWidgetIds(ComponentName(ctx, javaClass)))
        }
    }

    companion object { const val ACTION_TICK = "dev.tzelon.hebrewdate.TICK" }
}
