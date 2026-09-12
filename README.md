# Hebrew Date widget (Nothing Phone)

Shows Hebrew date (flips at sunset), year, this week's parsha, and on Fri/Sat
Shabbat entry (sunset−18) / exit (tzais 8.5°) for Ashkelon.

- `HebrewDay.kt` — all logic, built on KosherJava Zmanim
- `HebrewDateWidget.kt` — AppWidgetProvider; refreshes at the next boundary (sunset / tzais / midnight)
- No launcher activity → appears only in the widget picker

Tweaks: `geo` and `CANDLE_OFFSET_MIN` in HebrewDay.kt; swap `zc.tzais` for `zc.tzais72` for Rabbeinu Tam.
Styling lives in `res/layout/widget_hebrew_date.xml` + `res/values/colors.xml`: a cream rounded
card (`res/drawable/widget_card.xml`) with near-black dot-matrix text, matching the launcher's
weather/clock widgets.

## Font
`res/font/hebdot.ttf` is a generated dot-matrix Hebrew font (letters, gershayim, digits, colon).
Edit the bitmaps in `tools/font/build_font.py` and run it (`pip install fonttools`) to regenerate.
