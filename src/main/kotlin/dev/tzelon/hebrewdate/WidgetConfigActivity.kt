package dev.tzelon.hebrewdate

import android.app.Activity
import android.app.AlertDialog
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle

/** Settings for one widget. Runs when it is added, and again when its card is tapped. */
class WidgetConfigActivity : Activity() {

    private var widgetId = AppWidgetManager.INVALID_APPWIDGET_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        widgetId = intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID,
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID
        if (widgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }
        // Backing out leaves the widget as it was; the widget itself is already usable.
        setResult(RESULT_OK, Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId))
        showSettings()
    }

    private fun showSettings() {
        val entries = arrayOf(
            getString(R.string.setting_font, WidgetPrefs.font(this, widgetId).label),
            getString(R.string.setting_city, WidgetPrefs.city(this, widgetId).label),
        )
        dialog()
            .setTitle(getString(R.string.settings_title, version()))
            .setItems(entries) { _, which -> if (which == 0) pickFont() else pickCity() }
            .setOnCancelListener { finish() }
            .show()
    }

    private fun pickFont() = pick(
        R.string.setting_font_title,
        WidgetFont.entries.map { it.label },
        WidgetFont.entries.indexOf(WidgetPrefs.font(this, widgetId)),
    ) { WidgetPrefs.setFont(this, widgetId, WidgetFont.entries[it]) }

    private fun pickCity() = pick(
        R.string.setting_city_title,
        City.entries.map { it.label },
        City.entries.indexOf(WidgetPrefs.city(this, widgetId)),
    ) { WidgetPrefs.setCity(this, widgetId, City.entries[it]) }

    private fun pick(titleRes: Int, labels: List<String>, checked: Int, store: (Int) -> Unit) {
        dialog()
            .setTitle(titleRes)
            .setSingleChoiceItems(labels.toTypedArray(), checked) { d, which ->
                store(which)
                d.dismiss()
                HebrewDateWidget.refresh(this, intArrayOf(widgetId))
                finish()
            }
            .setOnCancelListener { finish() }
            .show()
    }

    private fun dialog() = AlertDialog.Builder(this, android.R.style.Theme_Material_Light_Dialog_Alert)

    /** Shown in the title so it is obvious which build is installed. */
    private fun version() = packageManager.getPackageInfo(packageName, 0).versionName ?: "?"
}
