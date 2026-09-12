package dev.tzelon.hebrewdate

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import org.junit.Test

/** Expectations are for Ashkelon, Asia/Jerusalem, 18-minute candle lighting, tzais 8.5°. */
class HebrewDayProviderTest {

    private val parse = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)
        .apply { timeZone = TimeZone.getTimeZone("Asia/Jerusalem") }

    private fun at(moment: String) = HebrewDayProvider.compute(parse.parse(moment)!!)

    @Test fun `ordinary weekday has no shabbat line`() {
        val d = at("2026-09-16 12:00")
        assertEquals("ה׳ בתשרי", d.dateText)
        assertEquals("תשפ״ז", d.yearText)
        assertEquals("האזינו", d.parsha)
        assertNull(d.shabbat)
    }

    @Test fun `friday shows candle lighting, saturday shows the exit`() {
        assertEquals("כניסת שבת 18:25", at("2026-09-18 16:00").shabbat)
        assertEquals("צאת שבת 19:18", at("2026-09-19 14:00").shabbat)
    }

    @Test fun `date flips at sunset`() {
        assertEquals("א׳ בתשרי", at("2026-09-12 13:56").dateText)
        assertEquals("ב׳ בתשרי", at("2026-09-12 21:00").dateText)
    }

    /** Shabbat running into the second day of Rosh Hashana ends Sunday night, not Saturday night. */
    @Test fun `shabbat into yom tov exits at the end of the whole span`() {
        assertEquals("צאת החג 19:26", at("2026-09-12 13:56").shabbat)
        assertEquals("צאת החג 19:26", at("2026-09-12 21:00").shabbat)
        assertEquals("ראש השנה", at("2026-09-12 13:56").parsha)
    }

    @Test fun `erev yom tov names the festival being entered`() {
        val erevYomKippur = at("2026-09-20 12:00")
        assertEquals("יום כיפור", erevYomKippur.parsha)
        assertEquals("כניסת החג 18:23", erevYomKippur.shabbat)
        assertEquals("כניסת החג 18:16", at("2026-09-25 12:00").shabbat)
        assertEquals("סוכות", at("2026-09-25 12:00").parsha)
    }

    @Test fun `yom tov on a weekday still shows entry and exit`() {
        assertEquals("כניסת החג 18:35", at("2026-09-11 10:00").shabbat)
        assertEquals("צאת החג 19:16", at("2026-09-21 12:00").shabbat)
        assertEquals("שמיני עצרת", at("2026-10-03 12:00").parsha)
    }

    @Test fun `next tick never lands in the past`() {
        val now = parse.parse("2026-09-12 13:56")!!
        assert(at("2026-09-12 13:56").nextTick.after(now))
    }
}
