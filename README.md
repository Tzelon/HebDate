# Hebrew Date widget (Nothing Phone)

Shows Hebrew date (flips at sunset), year, this week's parsha, and on Fri/Sat
Shabbat entry (sunset−18) / exit (tzais 8.5°) for Ashkelon.

- `HebrewDay.kt` — all logic, built on KosherJava Zmanim
- `HebrewDateWidget.kt` — AppWidgetProvider; refreshes at the next boundary (sunset / tzais / midnight)
- `WidgetRenderer.kt` — paints the text into a bitmap, because a TextView's `fontFamily` is not
  honoured reliably inside RemoteViews (the launcher inflates the layout and can impose its own font)
- `WidgetConfigActivity.kt` — font picker: dot-matrix (default) or the system font. Shown when the
  widget is added, and again whenever the card is tapped; the choice is stored per widget instance
- No launcher activity → appears only in the widget picker

Tweaks: `geo` and `CANDLE_OFFSET_MIN` in HebrewDay.kt; swap `zc.tzais` for `zc.tzais72` for Rabbeinu Tam.
Styling lives in `res/layout/widget_hebrew_date.xml` + `res/values/colors.xml`: a cream rounded
card (`res/drawable/widget_card.xml`) with near-black dot-matrix text, matching the launcher's
weather/clock widgets.

## Font
`res/font/hebdot.ttf` is a generated dot-matrix Hebrew font (letters, gershayim, digits, colon).
Edit the bitmaps in `tools/font/build_font.py` and run it (`pip install fonttools`) to regenerate.

## Installing
`keystore/debug.keystore` signs every debug build, including CI, so each new APK
installs over the previous one. It is a debug key only — never ship a release with it.
Coming from a differently-signed build, uninstall first:
`adb uninstall dev.tzelon.hebrewdate`.
