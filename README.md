# Hebrew Date widget (Nothing Phone)

Shows the Hebrew date (flips at sunset), the year, the parsha or festival, and
Shabbat/Yom Tov entry (candle lighting) and exit (tzais 8.5°).

- `HebrewDay.kt` — all logic, built on KosherJava Zmanim
- `HebrewDateWidget.kt` — AppWidgetProvider; refreshes at the next boundary (sunset / tzais / midnight)
- `WidgetRenderer.kt` — paints the text into a bitmap, because a TextView's `fontFamily` is not
  honoured reliably inside RemoteViews (the launcher inflates the layout and can impose its own font)
- `WidgetConfigActivity.kt` — settings: font (dot-matrix or system) and city. Shown when the widget
  is added, and again whenever the card is tapped; both are stored per widget instance
- No launcher activity → appears only in the widget picker

- `City.kt` — the cities offered in the picker, each with its own candle-lighting custom
  (Jerusalem 40 minutes, Haifa 30, the rest 18). Everything assumes Israel (`inIsrael = true`).

Tweaks: add a city to `City.kt`; swap `zc.tzais` for `zc.tzais72` for Rabbeinu Tam.
Styling lives in `res/layout/widget_hebrew_date.xml` + `res/values/colors.xml`: a cream rounded
card (`res/drawable/widget_card.xml`) with near-black dot-matrix text, matching the launcher's
weather/clock widgets.

## Font
`res/font/hebdot.ttf` is a generated dot-matrix Hebrew font (letters, gershayim, digits, colon).
ב and ד carry a heel: their bar runs one dot past the stem, which is what tells them apart from
the flush כ and ר — measured off a text face, not guessed.
Edit the bitmaps in `tools/font/build_font.py` and run it (`pip install fonttools`); it writes
straight back to `res/font/hebdot.ttf`.

## Installing
`keystore/debug.keystore` signs every debug build, including CI, so each new APK
installs over the previous one. It is a debug key only — never ship a release with it.
Coming from a differently-signed build, uninstall first:
`adb uninstall dev.tzelon.hebrewdate`.
