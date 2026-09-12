package dev.tzelon.hebrewdate

import android.app.AlarmManager
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.RemoteViews

class HebrewDateWidget : AppWidgetProvider() {

    override fun onUpdate(ctx: Context, mgr: AppWidgetManager, ids: IntArray) {
        val d = HebrewDayProvider.compute()
        ids.forEach { id -> mgr.updateAppWidget(id, views(ctx, mgr, id, d)) }

        val am = ctx.getSystemService(AlarmManager::class.java)
        val pi = PendingIntent.getBroadcast(
            ctx, 0, Intent(ctx, javaClass).setAction(ACTION_TICK),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        am.setAndAllowWhileIdle(AlarmManager.RTC, d.nextTick.time, pi)
    }

    /** Re-render at the new size: the text is a bitmap, so it does not reflow on its own. */
    override fun onAppWidgetOptionsChanged(
        ctx: Context, mgr: AppWidgetManager, id: Int, newOptions: Bundle,
    ) = mgr.updateAppWidget(id, views(ctx, mgr, id, HebrewDayProvider.compute()))

    override fun onDeleted(ctx: Context, ids: IntArray) = WidgetFont.forget(ctx, ids)

    override fun onReceive(ctx: Context, intent: Intent) {
        super.onReceive(ctx, intent)
        if (intent.action in setOf(ACTION_TICK, Intent.ACTION_DATE_CHANGED,
                Intent.ACTION_TIMEZONE_CHANGED, Intent.ACTION_TIME_CHANGED)) {
            val mgr = AppWidgetManager.getInstance(ctx)
            onUpdate(ctx, mgr, mgr.getAppWidgetIds(ComponentName(ctx, javaClass)))
        }
    }

    private fun views(ctx: Context, mgr: AppWidgetManager, id: Int, d: HebrewDay): RemoteViews {
        val options = mgr.getAppWidgetOptions(id)
        val width = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, DEFAULT_DP)
        val height = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT, DEFAULT_DP)
        val bitmap = WidgetRenderer.render(ctx, d, WidgetFont.of(ctx, id), width, height)

        return RemoteViews(ctx.packageName, R.layout.widget_hebrew_date).apply {
            setImageViewBitmap(R.id.canvas, bitmap)
            setContentDescription(R.id.canvas, listOfNotNull(d.dateText, d.yearText, d.parsha, d.shabbat).joinToString(", "))
            setOnClickPendingIntent(R.id.canvas, configIntent(ctx, id))
        }
    }

    /** Tapping the widget reopens the font picker. */
    private fun configIntent(ctx: Context, id: Int) = PendingIntent.getActivity(
        ctx, id,
        Intent(ctx, WidgetConfigActivity::class.java)
            .putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id)
            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK),
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
    )

    companion object {
        const val ACTION_TICK = "dev.tzelon.hebrewdate.TICK"
        private const val DEFAULT_DP = 110

        fun refresh(ctx: Context, ids: IntArray) = ctx.sendBroadcast(
            Intent(ctx, HebrewDateWidget::class.java)
                .setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE)
                .putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids),
        )
    }
}
