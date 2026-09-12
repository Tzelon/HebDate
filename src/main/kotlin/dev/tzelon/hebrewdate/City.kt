package dev.tzelon.hebrewdate

import com.kosherjava.zmanim.util.GeoLocation
import java.util.TimeZone

/**
 * Where the zmanim are computed for. [candleOffsetMinutes] is the local custom for candle
 * lighting before sunset, which varies far more between cities than the coordinates do.
 */
enum class City(
    val label: String,
    private val latitude: Double,
    private val longitude: Double,
    private val elevation: Double,
    val candleOffsetMinutes: Double,
) {
    JERUSALEM("ירושלים", 31.778, 35.235, 754.0, 40.0),
    TEL_AVIV("תל אביב", 32.085, 34.781, 20.0, 18.0),
    HAIFA("חיפה", 32.794, 34.990, 60.0, 30.0),
    BEER_SHEVA("באר שבע", 31.252, 34.791, 260.0, 18.0),
    ASHKELON("אשקלון", 31.669, 34.571, 30.0, 18.0),
    EILAT("אילת", 29.557, 34.952, 12.0, 18.0);

    fun geo(): GeoLocation = GeoLocation(name, latitude, longitude, elevation, TZ)

    companion object {
        private val TZ: TimeZone = TimeZone.getTimeZone("Asia/Jerusalem")
        val DEFAULT = ASHKELON

        fun named(name: String?) = entries.firstOrNull { it.name == name } ?: DEFAULT
    }
}
