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

/** What the widget shows. shabbat is null on Sun–Thu. */
data class HebrewDay(
    val dateText: String,     // כ״ז באלול
    val yearText: String,     // תשפ״ו
    val parsha: String,       // נצבים
    val shabbat: String?,     // כניסה 18:32 / יציאה 19:31
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
        val zc = ComplexZmanimCalendar(geo).apply {
            calendar = civil
            candleLightingOffset = CANDLE_OFFSET_MIN
        }

        // Hebrew date flips at sunset
        val afterSunset = zc.sunset != null && now.after(zc.sunset)
        val jc = JewishCalendar(civil).apply { inIsrael = true }
        if (afterSunset) jc.forward(Calendar.DATE, 1)

        val dow = civil.get(Calendar.DAY_OF_WEEK)
        val satZc = if (dow == Calendar.FRIDAY) ComplexZmanimCalendar(geo).apply {
            calendar = (civil.clone() as Calendar).apply { add(Calendar.DATE, 1) }
        } else zc
        val shabbat = when {
            dow == Calendar.FRIDAY && !afterSunset -> "כניסה ${hhmm.format(zc.candleLighting)}"
            dow == Calendar.FRIDAY || (dow == Calendar.SATURDAY && now.before(satZc.tzais)) ->
                "יציאה ${hhmm.format(satZc.tzais)}"    // tzais = 8.5°; use tzais72 for ר״ת
            else -> null
        }

        // parsha (or yom tov) of the coming Shabbat
        val sat = JewishCalendar(civil).apply { inIsrael = true }
        while (sat.dayOfWeek != Calendar.SATURDAY) sat.forward(Calendar.DATE, 1)
        val parsha = fmt.formatParsha(sat).ifEmpty { fmt.formatYomTov(sat) }

        return HebrewDay(
            dateText = "${fmt.formatHebrewNumber(jc.jewishDayOfMonth)} ב${fmt.formatMonth(jc)}",
            yearText = fmt.formatHebrewNumber(jc.jewishYear),
            parsha = parsha,
            shabbat = shabbat,
            nextTick = nextTick(now, zc, dow),
        )
    }

    /** Next moment the display can change: sunset, tzais (Sat), or midnight. */
    private fun nextTick(now: Date, zc: ComplexZmanimCalendar, dow: Int): Date {
        val candidates = mutableListOf<Date>()
        zc.sunset?.let { if (it.after(now)) candidates += it }
        if (dow == Calendar.SATURDAY) zc.tzais?.let { if (it.after(now)) candidates += it }
        val midnight = Calendar.getInstance(geo.timeZone).apply {
            time = now; add(Calendar.DATE, 1)
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 10); set(Calendar.MILLISECOND, 0)
        }.time
        candidates += midnight
        return candidates.minOf { it }.let { Date(it.time + 5_000) } // +5s so we're past the boundary
    }
}
