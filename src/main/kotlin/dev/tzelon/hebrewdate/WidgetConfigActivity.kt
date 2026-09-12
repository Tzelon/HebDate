package dev.tzelon.hebrewdate

import android.app.Activity
import android.app.AlertDialog
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle

/** Font picker. Runs when the widget is added, and again when its card is tapped. */
class WidgetConfigActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val widgetId = intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID,
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID
        if (widgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }
        // Backing out of the picker leaves the widget as it was.
        setResult(RESULT_CANCELED, Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId))

        val fonts = WidgetFont.entries.toTypedArray()
        val labels = fonts.map { getString(if (it == WidgetFont.DOTS) R.string.font_dots else R.string.font_system) }
        AlertDialog.Builder(this, android.R.style.Theme_Material_Light_Dialog_Alert)
            .setTitle(R.string.font_title)
            .setSingleChoiceItems(labels.toTypedArray(), fonts.indexOf(WidgetFont.of(this, widgetId))) { dialog, which ->
                WidgetFont.store(this, widgetId, fonts[which])
                dialog.dismiss()
                HebrewDateWidget.refresh(this, intArrayOf(widgetId))
                setResult(RESULT_OK, Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId))
                finish()
            }
            .setOnCancelListener { finish() }
            .show()
    }
}
