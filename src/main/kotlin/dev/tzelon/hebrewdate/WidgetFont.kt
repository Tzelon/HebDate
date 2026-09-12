package dev.tzelon.hebrewdate

import android.content.Context
import android.graphics.Typeface

/** Typeface the widget draws with. */
enum class WidgetFont(val label: String) {
    DOTS("נקודות"), SYSTEM("רגיל");

    fun typeface(ctx: Context): Typeface =
        if (this == DOTS) ctx.resources.getFont(R.font.hebdot) else Typeface.DEFAULT

    companion object {
        val DEFAULT = DOTS

        fun named(name: String?) = entries.firstOrNull { it.name == name } ?: DEFAULT
    }
}
