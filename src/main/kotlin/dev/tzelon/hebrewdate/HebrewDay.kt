package dev.tzelon.hebrewdate

import com.kosherjava.zmanim.ComplexZmanimCalendar
import com.kosherjava.zmanim.hebrewcalendar.HebrewDateFormatter
import com.kosherjava.zmanim.hebrewcalendar.JewishCalendar
import com.kosherjava.zmanim.util.GeoLocation
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/** What the widget shows. shabbat is null outside Shabbat/Yom Tov and their eves. */
data class HebrewDay(
    val dateText: String,     // כ״ז באלול
    val yearText: String,     // תשפ״ו
    val parsha: String,       // נצבים
    val shabbat: String?,     // כניסת שבת 18:32 / צאת החג 19:26
    val nextTick: Date,       // when to refresh next
)

object HebrewDayProvider {
    // Ashkelon. Change or replace with FusedLocation if you want it dynamic.
    private val geo = GeoLocation("Ashkelon", 31.669, 34.571, 30.0, TimeZone.getTimeZone("Asia/Jerusalem"))
    private const val CANDLE_OFFSET_MIN = 18.0   // 40 for Jerusalem

    private val fmt = HebrewDateFormatter().apply { isHebrewFormat = true; isUseGershGershayim = true }
    private val hhmm = SimpleDateFormat("HH:mm", Locale.US).apply { timeZone = geo.timeZone }

    fun compute(now: Date = Date()): HebrewDay {
        val civil = Calendar.getInstance(geo.timeZone).apply { time = now }
        val sunset = zmanim(civil).sunset

        // The Hebrew day flips at sunset, so from sunset on, the civil day that *carries* it is
        // tomorrow. Anchoring on that one civil date keeps every lookup below on the same day.
        val anchor = (civil.clone() as Calendar).apply {
            if (sunset != null && now.after(sunset)) add(Calendar.DATE, 1)
        }
        val today = hebrew(anchor)

        val shabbat: String?
        val boundary: Date?
        when {
            // Inside Shabbat / Yom Tov: it ends at tzais of the last of any consecutive such days,
            // so a Shabbat running into a festival (or the second day of Rosh Hashana) is one span.
            today.isAssurBemelacha -> {
                val lastCivil = anchor.clone() as Calendar
                var last = today
                while (last.isTomorrowShabbosOrYomTov) {
                    lastCivil.add(Calendar.DATE, 1)
                    last = hebrew(lastCivil)
                }
                boundary = zmanim(lastCivil).tzais       // tzais = 8.5°; use tzais72 for ר״ת
                shabbat = "${if (last.isYomTov) "צאת החג" else "צאת שבת"} ${hhmm.format(boundary)}"
            }
            // Its eve: candle lighting tonight.
            today.isTomorrowShabbosOrYomTov -> {
                boundary = zmanim(anchor).candleLighting
                shabbat = "${if (today.isErevYomTov) "כניסת החג" else "כניסת שבת"} ${hhmm.format(boundary)}"
            }
            else -> {
                shabbat = null
                boundary = null
            }
        }

        return HebrewDay(
            dateText = "${fmt.formatHebrewNumber(today.jewishDayOfMonth)} ב${fmt.formatMonth(today)}",
            yearText = fmt.formatHebrewNumber(today.jewishYear),
            parsha = label(anchor, today),
            shabbat = shabbat,
            nextTick = nextTick(now, sunset, boundary),
        )
    }

    /** The festival we are in or about to enter, else the parsha of the coming Shabbat. */
    private fun label(anchor: Calendar, today: JewishCalendar): String = when {
        today.isYomTov || today.isCholHamoed -> fmt.formatYomTov(today)
        today.isErevYomTov -> fmt.formatYomTov(hebrew((anchor.clone() as Calendar).apply { add(Calendar.DATE, 1) }))
        else -> {
            val satCal = anchor.clone() as Calendar
            while (satCal.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY) satCal.add(Calendar.DATE, 1)
            val sat = hebrew(satCal)
            fmt.formatParsha(sat).ifEmpty { fmt.formatYomTov(sat) }
        }
    }

    /** Next moment the display can change: sunset, the Shabbat/Yom Tov boundary, or midnight. */
    private fun nextTick(now: Date, sunset: Date?, boundary: Date?): Date {
        val candidates = mutableListOf<Date>()
        listOfNotNull(sunset, boundary).forEach { if (it.after(now)) candidates += it }
        candidates += Calendar.getInstance(geo.timeZone).apply {
            time = now; add(Calendar.DATE, 1)
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 10); set(Calendar.MILLISECOND, 0)
        }.time
        return candidates.min().let { Date(it.time + 5_000) } // +5s so we're past the boundary
    }

    private fun zmanim(day: Calendar) = ComplexZmanimCalendar(geo).apply {
        calendar = day.clone() as Calendar
        candleLightingOffset = CANDLE_OFFSET_MIN
    }

    private fun hebrew(day: Calendar) = JewishCalendar(day.clone() as Calendar).apply { inIsrael = true }
}
