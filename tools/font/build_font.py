from fontTools.fontBuilder import FontBuilder
from fontTools.pens.ttGlyphPen import TTGlyphPen
from fontTools.pens.transformPen import TransformPen

# rows top->bottom. Row 0 = ascender row (above x-height), rows 1-7 = body, rows 8-9 descenders.
# Glyph bitmaps below are body-only unless marked; "asc"/"desc" keys shift them.
G = {}
def g(cp, rows, asc=0):
    rows = [r for r in rows.strip("\n").split("\n")]
    w = max(len(r) for r in rows)
    G[cp] = ([r.ljust(w, ".") for r in rows], asc)

# ---------- Hebrew ----------
g(0x05D0, """
#...#
##..#
.##.#
..#..
#.##.
#..##
#...#""")            # א
g(0x05D1, """
.####.
.....#
.....#
.....#
.....#
.....#
######""")           # ב
g(0x05D2, """
.###.
...#.
...#.
...#.
..##.
.#.#.
#..#.""")            # ג
g(0x05D3, """
#####
....#
....#
....#
....#
....#
....#""")            # ד
g(0x05D4, """
#####
....#
....#
#...#
#...#
#...#
#...#""")            # ה
g(0x05D5, """
##
.#
.#
.#
.#
.#
.#""")               # ו
g(0x05D6, """
###
.#.
.#.
.#.
.#.
.#.
.#.""")              # ז
g(0x05D7, """
#####
#...#
#...#
#...#
#...#
#...#
#...#""")            # ח
g(0x05D8, """
#..#.
#...#
#.#.#
#.#.#
#.#.#
#...#
.###.""")            # ט
g(0x05D9, """
##
.#
.#
..
..
..
..""")               # י
g(0x05DA, """
.####
....#
....#
....#
....#
....#
....#
....#
....#""")            # ך
g(0x05DB, """
.####
....#
....#
....#
....#
....#
.####""")            # כ
g(0x05DC, """
#....
.#...
.####
....#
....#
....#
...#.
.##..""", asc=1)     # ל
g(0x05DD, """
#####
#...#
#...#
#...#
#...#
#...#
#####""")            # ם
g(0x05DE, """
.####
#...#
#...#
#...#
#...#
#...#
#.###""")            # מ
g(0x05DF, """
##
.#
.#
.#
.#
.#
.#
.#
.#""")               # ן
g(0x05E0, """
.##
..#
..#
..#
..#
..#
###""")              # נ
g(0x05E1, """
#####
#...#
#...#
#...#
#...#
#...#
.###.""")            # ס
g(0x05E2, """
#...#
#...#
#...#
.#..#
.#.#.
.##..
##...""")            # ע
g(0x05E3, """
#####
....#
.#..#
.##.#
....#
....#
....#
....#
....#""")            # ף
g(0x05E4, """
#####
....#
.#..#
.##.#
....#
....#
#####""")            # פ
g(0x05E5, """
#...#
#...#
.#..#
..#.#
...##
....#
....#
....#
....#""")            # ץ
g(0x05E6, """
#...#
#...#
.#..#
..#.#
...##
....#
#####""")            # צ
g(0x05E7, """
.####
....#
#...#
#...#
#....
#....
#....
#....
#....""")            # ק
g(0x05E8, """
.####
....#
....#
....#
....#
....#
....#""")            # ר
g(0x05E9, """
#.#.#
#.#.#
#.#.#
#.#.#
#.##.
##...
#####""")            # ש
g(0x05EA, """
#####
.#..#
.#..#
.#..#
.#..#
.#..#
##..#""")            # ת
g(0x05F3, """
#
#""")                # ׳ geresh
g(0x05F4, """
#.#
#.#""")              # ״ gershayim
g(0x27, "#\n#")       # '
g(0x22, "#.#\n#.#")   # "

# ---------- digits / punctuation ----------
D = """
.###. ..#.. .###. ##### ...#. ##### ..### ##### .###. .###.
#...# .##.. #...# ...#. ..##. #.... .#... ....# #...# #...#
#..## ..#.. ....# ..#.. .#.#. ####. #.... ...#. #...# #...#
#.#.# ..#.. ...#. ...#. #..#. ....# ####. ..#.. .###. .####
##..# ..#.. ..#.. ....# ##### ....# #...# .#... #...# ....#
#...# ..#.. .#... #...# ...#. #...# #...# .#... #...# ...#.
.###. .###. ##### .###. ...#. .###. .###. .#... .###. ###..""".strip("\n").split("\n")
for i in range(10):
    g(0x30 + i, "\n".join(r.split(" ")[i] for r in D))
g(0x3A, ".\n.\n#\n.\n.\n#\n.")          # :
g(0x2E, ".\n.\n.\n.\n.\n.\n#")          # .
g(0x2D, ".\n.\n.\n###\n.\n.\n.")        # -
g(0x20, "...\n.")                        # space

# ---------- build ----------
CELL, R = 100, 46
BODY_TOP = 700   # y of top of row 1
def circle(pen, cx, cy, r):
    k = r  # quadratic approximation with 8 off-curve points
    pts = [(cx + r, cy), (cx + r, cy + k), (cx, cy + r), (cx - k, cy + r),
           (cx - r, cy), (cx - r, cy - k), (cx, cy - r), (cx + k, cy - r), (cx + r, cy)]
    pen.moveTo(pts[0])
    pen.qCurveTo(pts[1], pts[2]); pen.qCurveTo(pts[3], pts[4])
    pen.qCurveTo(pts[5], pts[6]); pen.qCurveTo(pts[7], pts[8])
    pen.closePath()

names = {".notdef": ".notdef"}
glyphs, widths, cmap = {}, {}, {}
p = TTGlyphPen(None); glyphs[".notdef"] = p.glyph(); widths[".notdef"] = 500
for cp, (rows, asc) in G.items():
    name = f"uni{cp:04X}"
    names[name] = name
    pen = TTGlyphPen(None)
    for ri, row in enumerate(rows):
        y = BODY_TOP + asc * CELL - ri * CELL - CELL / 2
        for ci, ch in enumerate(row):
            if ch == "#":
                circle(pen, ci * CELL + CELL / 2, y, R)
    glyphs[name] = pen.glyph()
    widths[name] = (len(rows[0]) + 1) * CELL
    cmap[cp] = name

order = list(glyphs.keys())
fb = FontBuilder(1000, isTTF=True)
fb.setupGlyphOrder(order)
fb.setupCharacterMap(cmap)
fb.setupGlyf(glyphs)
fb.setupHorizontalMetrics({n: (widths[n], 0) for n in order})
fb.setupHorizontalHeader(ascent=850, descent=-250)
fb.setupOS2(sTypoAscender=850, sTypoDescender=-250, usWinAscent=850, usWinDescent=250)
fb.setupNameTable({"familyName": "HebDot", "styleName": "Regular",
                   "fullName": "HebDot Regular", "psName": "HebDot-Regular",
                   "uniqueFontIdentifier": "HebDot-0.1"})
fb.setupPost()
fb.save("/tmp/font/hebdot.ttf")
print("glyphs:", len(order))
