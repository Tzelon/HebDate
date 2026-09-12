# Hebrew Date widget (Nothing Phone)

Shows Hebrew date (flips at sunset), year, this week's parsha, and on Fri/Sat
Shabbat entry (sunset−18) / exit (tzais 8.5°) for Ashkelon.

- `HebrewDay.kt` — all logic, built on KosherJava Zmanim
- `HebrewDateWidget.kt` — AppWidgetProvider; refreshes at the next boundary (sunset / tzais / midnight)
- No launcher activity → appears only in the widget picker

Tweaks: `geo` and `CANDLE_OFFSET_MIN` in HebrewDay.kt; swap `zc.tzais` for `zc.tzais72` for Rabbeinu Tam.
Font: add `android:fontFamily` on the TextViews once you have a Hebrew dot-matrix TTF.

## Font
`res/font/hebdot.ttf` is a generated dot-matrix Hebrew font (letters, gershayim, digits, colon).
Edit the bitmaps in `tools/font/build_font.py` and run it (`pip install fonttools`) to regenerate.
