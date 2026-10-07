#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
يبني app/src/main/assets/databases/athar.db من data/athar_corpus.json.

المخطط يُقرأ حرفياً من مخطط Room المصدَّر (app/schemas/.../<VERSION>.json)،
فالجداول والفهارس والقيود الافتراضية وجدول FTS4 تطابق ما يتحقق منه Room عند
createFromAsset تطابقاً تاماً، ويُكتب room_master_table بالبصمة نفسها.

التشغيل (بعد أي تغيير في الكيانات: شغّل kspDebugKotlin أولاً ليُحدَّث المخطط):
    python tools/build_athar_db.py
"""

import json
import os
import re
import sqlite3
import sys

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
CORPUS = os.path.join(ROOT, "data", "athar_corpus.json")
SCHEMA_DIR = os.path.join(ROOT, "app", "schemas", "com.example.data.local.AtharDatabase")
OUT = os.path.join(ROOT, "app", "src", "main", "assets", "databases", "athar.db")
DB_KT = os.path.join(ROOT, "app", "src", "main", "java", "com", "example", "data", "local", "AtharDatabase.kt")

# ألقاب شائعة تُعرض بجانب الاسم الكامل (لا تراجم مختلقة: الملاحظات تبقى فارغة)
POPULAR_NAMES = {
    15: "ابن عمر", 16: "نافع", 17: "الإمام مالك", 18: "ابن المسيب", 19: "عروة",
    20: "سالم", 21: "القاسم", 26: "الزهري", 27: "الأوزاعي", 29: "مكحول",
    37: "أبو الزناد", 38: "يحيى بن سعيد", 39: "ربيعة الرأي", 40: "ابن القاسم المدني",
}

# ------------------------------------------------------------------------------
# توحيد نص البحث — يجب أن يطابق ArabicText.normalizeForSearch في Kotlin حرفياً
# ------------------------------------------------------------------------------
_STRIP = re.compile(r"[ؐ-ًؚ-ٰٟۖ-ۭـ]")
_MAP = str.maketrans({"أ": "ا", "إ": "ا", "آ": "ا", "ٱ": "ا", "ؤ": "و", "ئ": "ي", "ة": "ه", "ى": "ي"})
_NON_WORD = re.compile(r"[^ء-ي0-9]+")


def normalize_for_search(text):
    text = _STRIP.sub("", text or "").translate(_MAP)
    return " ".join(_NON_WORD.sub(" ", text).split())


def index_text(text):
    """النص الموحّد مع صيغ بلا «و/ال» السابقة، حتى يطابق «صلاه*» كلمة «والصلاه»."""
    words = normalize_for_search(text).split()
    extra = []
    for w in words:
        v = w
        if v.startswith("و") and len(v) > 3:
            v = v[1:]
            extra.append(v)
        if v.startswith("ال") and len(v) > 4:
            extra.append(v[2:])
    return " ".join(words + extra)


def db_version():
    m = re.search(r"const val VERSION\s*=\s*(\d+)", open(DB_KT, encoding="utf-8").read())
    if not m:
        sys.exit("لم أجد AtharDatabase.VERSION")
    return int(m.group(1))


def main():
    version = db_version()
    schema_path = os.path.join(SCHEMA_DIR, f"{version}.json")
    if not os.path.exists(schema_path):
        sys.exit(f"المخطط {schema_path} غير موجود — شغّل :app:kspDebugKotlin أولاً")
    schema = json.load(open(schema_path, encoding="utf-8"))["database"]
    corpus = json.load(open(CORPUS, encoding="utf-8"))

    os.makedirs(os.path.dirname(OUT), exist_ok=True)
    if os.path.exists(OUT):
        os.remove(OUT)
    db = sqlite3.connect(OUT)
    db.execute("PRAGMA foreign_keys = ON")

    # 1. المخطط حرفياً من Room
    for e in schema["entities"]:
        db.execute(e["createSql"].replace("${TABLE_NAME}", e["tableName"]))
        for idx in e.get("indices", []):
            db.execute(idx["createSql"].replace("${TABLE_NAME}", e["tableName"]))
    for q in schema.get("setupQueries", []):
        db.execute(q)

    # 2. البيانات
    db.executemany(
        "INSERT INTO books (id, title, author, era) VALUES (?, ?, ?, ?)",
        [(b["id"], b["title"], b["author"], b["era"]) for b in corpus["books"]],
    )
    db.executemany(
        "INSERT INTO narrators (id, name, popular_name, region, era, is_trusted, sect_affiliation, notes) "
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)",
        [(n["id"], n["name"], POPULAR_NAMES.get(n["id"]), n["region"], n["era"],
          int(n.get("is_trusted", 1)), n.get("sect_affiliation"), n.get("notes"))
         for n in corpus["narrators"]],
    )
    hadith_rows, chain_rows, fts_rows = [], [], []
    for h in corpus["hadiths"]:
        hadith_rows.append((
            h["id"], h["book_id"], h["chapter"], h["path_badge"], h["region_tag"],
            h["raw_sanad"], h["matn"], int(bool(h.get("is_mursal_or_balagh"))),
            h.get("transmission_note"), (h.get("source_ref") or {}).get("idInBook"),
        ))
        for order, nid in enumerate(h["narrator_ids"], start=1):
            chain_rows.append((h["id"], nid, order))
        fts_rows.append((h["id"], index_text(h["matn"]), index_text(h["raw_sanad"])))
    db.executemany(
        "INSERT INTO hadiths (id, book_id, chapter, path_badge, region_tag, raw_sanad, matn, "
        "is_mursal_or_balagh, transmission_note, source_number) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
        hadith_rows,
    )
    db.executemany(
        "INSERT INTO hadith_narrator_chain (hadith_id, narrator_id, chain_order) VALUES (?, ?, ?)",
        chain_rows,
    )
    db.executemany("INSERT INTO hadith_fts (rowid, matn, sanad) VALUES (?, ?, ?)", fts_rows)
    db.execute("INSERT INTO hadith_fts(hadith_fts) VALUES ('optimize')")
    db.commit()

    # 3. تحقق ثم ضغط
    problems = db.execute("PRAGMA foreign_key_check").fetchall()
    if problems:
        sys.exit(f"مخالفات مفاتيح خارجية: {problems[:5]}")
    assert db.execute("PRAGMA integrity_check").fetchone()[0] == "ok"
    db.execute(f"PRAGMA user_version = {version}")
    db.commit()
    db.execute("VACUUM")
    db.close()

    size = os.path.getsize(OUT) / 1024 / 1024
    print(f"✅ {OUT}")
    print(f"   الإصدار {version} · {len(corpus['books'])} كتب · {len(corpus['narrators'])} راوياً · "
          f"{len(hadith_rows)} رواية · {len(chain_rows)} حلقة سند · {size:.1f} MB")


if __name__ == "__main__":
    if hasattr(sys.stdout, "reconfigure"):
        sys.stdout.reconfigure(encoding="utf-8")
    main()
