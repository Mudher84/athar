#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
مشروع «أثر» — بناء مدونة المرويات من AhmedBaset/hadith-json.

القوائم (الرواة، القائمة السوداء، الكتب، شرط الراويين) كما حدّدها صاحب المشروع.
التصحيحات: توحيد النص قبل الفصل والمطابقة، فصل السند عن المتن، حدود الكلمات،
منع تداخل الأسماء، أسماء الأبواب، ترتيب الرواة حسب موقعهم في السند.

تنبيه: «مقبول حسب معايير المسار» يعني أن أسماء رواة المسار وردت في السند
ولم يرد فيه أحد من القائمة السوداء. هو ليس حكماً حديثياً على صحة الإسناد.

المخرجات:
  athar_corpus.json   — بصيغة initial_hadiths.json (books / narrators / hadiths)
  athar_report.json   — إحصاءات لكل كتاب وعينات من المقبول والمرفوض
"""

import json
import os
import random
import re
import sys
import urllib.request

_ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
# نسخ المصادر الخام تُنزَّل مرة واحدة وقت البناء فقط (التطبيق نفسه لا يتصل بالشبكة)
CACHE_DIR = os.path.join(_ROOT, "data", "source_cache")
OUTPUT_CORPUS = os.path.join(_ROOT, "data", "athar_corpus.json")
OUTPUT_REPORT = os.path.join(_ROOT, "data", "athar_report.json")
ACCEPTED_LABEL = "مقبول حسب معايير المسار"
SAMPLE_SIZE = 15
RANDOM_SEED = 7

# ==============================================================================
# 1. معجم الرواة (كما حدّده صاحب المشروع، مع حقل era لأن Room يشترطه)
# ==============================================================================

TARGET_NARRATORS = [
    {"id": 1, "name": "عثمان بن عفان", "patterns": ["عثمان بن عفان", "عثمان"], "region": "المدينة المنورة", "era": "عصر الصحابة"},
    {"id": 2, "name": "معاوية بن أبي سفيان", "patterns": ["معاوية بن أبي سفيان", "معاوية"], "region": "الشام", "era": "عصر الصحابة"},
    {"id": 3, "name": "مروان بن الحكم", "patterns": ["مروان بن الحكم", "مروان"], "region": "المدينة المنورة / الشام", "era": "عصر الصحابة / التابعين"},
    {"id": 4, "name": "عبد الملك بن مروان", "patterns": ["عبد الملك بن مروان"], "region": "الشام", "era": "عصر التابعين"},
    {"id": 5, "name": "عمر بن عبد العزيز", "patterns": ["عمر بن عبد العزيز"], "region": "المدينة المنورة / الشام", "era": "عصر التابعين"},
    {"id": 6, "name": "الوليد بن عبد الملك", "patterns": ["الوليد بن عبد الملك"], "region": "الشام", "era": "عصر التابعين"},
    {"id": 7, "name": "يزيد بن أبي سفيان", "patterns": ["يزيد بن أبي سفيان"], "region": "الشام", "era": "عصر الصحابة"},
    {"id": 8, "name": "أبو سفيان بن حرب", "patterns": ["أبو سفيان بن حرب", "أبو سفيان", "أبي سفيان", "أبا سفيان"], "region": "مكة / الشام", "era": "عصر الصحابة"},
    {"id": 9, "name": "أبو أمامة الباهلي", "patterns": ["أبو أمامة الباهلي", "أبي أمامة الباهلي", "أبو أمامة", "أبي أمامة", "أبا أمامة"], "region": "الشام", "era": "عصر الصحابة"},
    {"id": 10, "name": "عوف بن مالك الأشجعي", "patterns": ["عوف بن مالك الأشجعي", "عوف بن مالك"], "region": "الشام", "era": "عصر الصحابة"},
    {"id": 11, "name": "النعمان بن بشير", "patterns": ["النعمان بن بشير"], "region": "الشام", "era": "عصر الصحابة"},
    {"id": 12, "name": "عبادة بن الصامت", "patterns": ["عبادة بن الصامت"], "region": "الشام / القدس", "era": "عصر الصحابة"},
    {"id": 13, "name": "شداد بن أوس", "patterns": ["شداد بن أوس"], "region": "الشام / القدس", "era": "عصر الصحابة"},
    {"id": 14, "name": "واثلة بن الأسقع", "patterns": ["واثلة بن الأسقع"], "region": "الشام / دمشق", "era": "عصر الصحابة"},
    {"id": 15, "name": "عبد الله بن عمر", "patterns": ["عبد الله بن عمر", "ابن عمر"], "region": "المدينة المنورة", "era": "عصر الصحابة"},
    {"id": 16, "name": "نافع مولى ابن عمر", "patterns": ["نافع مولى عبد الله", "نافع مولى ابن عمر", "نافع"], "region": "المدينة المنورة", "era": "عصر التابعين"},
    {"id": 17, "name": "مالك بن أنس", "patterns": ["مالك بن أنس", "مالك"], "region": "المدينة المنورة", "era": "أتباع التابعين"},
    {"id": 18, "name": "سعيد بن المسيب", "patterns": ["سعيد بن المسيب", "ابن المسيب"], "region": "المدينة المنورة", "era": "عصر التابعين"},
    {"id": 19, "name": "عروة بن الزبير", "patterns": ["عروة بن الزبير", "عروة"], "region": "المدينة المنورة", "era": "عصر التابعين"},
    {"id": 20, "name": "سالم بن عبد الله بن عمر", "patterns": ["سالم بن عبد الله بن عمر", "سالم بن عبد الله"], "region": "المدينة المنورة", "era": "عصر التابعين"},
    {"id": 21, "name": "القاسم بن محمد بن أبي بكر", "patterns": ["القاسم بن محمد بن أبي بكر", "القاسم بن محمد"], "region": "المدينة المنورة", "era": "عصر التابعين"},
    {"id": 22, "name": "سليمان بن يسار", "patterns": ["سليمان بن يسار"], "region": "المدينة المنورة", "era": "عصر التابعين"},
    {"id": 23, "name": "خارجة بن زيد", "patterns": ["خارجة بن زيد"], "region": "المدينة المنورة", "era": "عصر التابعين"},
    {"id": 24, "name": "عبيد الله بن عبد الله بن عتبة", "patterns": ["عبيد الله بن عبد الله بن عتبة", "عبيد الله بن عبد الله"], "region": "المدينة المنورة", "era": "عصر التابعين"},
    {"id": 25, "name": "أبو بكر بن عبد الرحمن بن الحارث", "patterns": ["أبو بكر بن عبد الرحمن بن الحارث", "أبي بكر بن عبد الرحمن بن الحارث", "أبو بكر بن عبد الرحمن", "أبي بكر بن عبد الرحمن"], "region": "المدينة المنورة", "era": "عصر التابعين"},
    {"id": 26, "name": "محمد بن مسلم بن شهاب الزهري", "patterns": ["محمد بن مسلم بن عبيد الله بن شهاب", "محمد بن مسلم بن شهاب", "ابن شهاب الزهري", "ابن شهاب", "الزهري"], "region": "المدينة المنورة / الشام", "era": "عصر التابعين"},
    {"id": 27, "name": "عبد الرحمن بن عمرو الأوزاعي", "patterns": ["عبد الرحمن بن عمرو الأوزاعي", "الأوزاعي"], "region": "الشام", "era": "أتباع التابعين"},
    {"id": 28, "name": "حريز بن عثمان الرحبي الحمصي", "patterns": ["حريز بن عثمان الرحبي", "حريز بن عثمان", "حريز"], "region": "الشام", "era": "صغار التابعين"},
    {"id": 29, "name": "مكحول الشامي", "patterns": ["مكحول الدمشقي", "مكحول الشامي", "مكحول"], "region": "الشام", "era": "عصر التابعين"},
    {"id": 30, "name": "رجاء بن حيوة الكندي", "patterns": ["رجاء بن حيوة الكندي", "رجاء بن حيوة"], "region": "الشام", "era": "عصر التابعين"},
    {"id": 31, "name": "حسان بن عطية المحاربي", "patterns": ["حسان بن عطية المحاربي", "حسان بن عطية"], "region": "الشام", "era": "عصر التابعين"},
    {"id": 32, "name": "ثور بن يزيد الكلاعي الحمصي", "patterns": ["ثور بن يزيد الكلاعي", "ثور بن يزيد"], "region": "الشام", "era": "أتباع التابعين"},
    {"id": 33, "name": "الوليد بن مسلم القرشي", "patterns": ["الوليد بن مسلم القرشي", "الوليد بن مسلم"], "region": "الشام", "era": "أتباع التابعين"},
    {"id": 34, "name": "إسماعيل بن عياش العنسي", "patterns": ["إسماعيل بن عياش"], "region": "الشام", "era": "أتباع التابعين"},
    {"id": 35, "name": "يحيى بن حمزة الحضرمي الدمشقي", "patterns": ["يحيى بن حمزة الحضرمي", "يحيى بن حمزة"], "region": "الشام", "era": "أتباع التابعين"},
    {"id": 36, "name": "عروة بن رويم اللخمي", "patterns": ["عروة بن رويم اللخمي", "عروة بن رويم"], "region": "الشام", "era": "عصر التابعين"},
    # --- أئمة الحجاز (إضافة المرحلة الثانية) ---
    {"id": 37, "name": "أبو الزناد عبد الله بن ذكوان", "patterns": ["عبد الله بن ذكوان", "أبو الزناد", "أبي الزناد", "أبا الزناد"], "region": "المدينة المنورة", "era": "صغار التابعين"},
    {"id": 38, "name": "يحيى بن سعيد الأنصاري", "patterns": ["يحيى بن سعيد الأنصاري", "يحيى بن سعيد"], "region": "المدينة المنورة", "era": "صغار التابعين"},
    {"id": 39, "name": "ربيعة بن أبي عبد الرحمن (ربيعة الرأي)", "patterns": ["ربيعة بن أبي عبد الرحمن", "ربيعة الرأي", "ربيعة"], "region": "المدينة المنورة", "era": "صغار التابعين"},
    {"id": 40, "name": "عبد الرحمن بن القاسم بن محمد بن أبي بكر", "patterns": ["عبد الرحمن بن القاسم بن محمد", "عبد الرحمن بن القاسم"], "region": "المدينة المنورة", "era": "صغار التابعين"},
]

# ==============================================================================
# 2. القائمة السوداء (كما حدّدها صاحب المشروع)
# ==============================================================================

STRICT_BLACKLIST = [
    "أبو مخنف", "لوط بن يحيى", "محمد بن السائب الكلبي", "هشام بن محمد بن السائب",
    "نصر بن مزاحم", "جابر الجعفي", "أصبغ بن نباتة", "سيف بن عمر",
    "محمد بن عمر الواقدي", "الواقدي", "الحارث الأعور", "عطية العوفي",
    "حبة العرني", "عبد الله بن لهيعة", "علي بن غراب", "تليد بن سليمان", "يحيى بن الجزار",
    "إسماعيل بن عبد الرحمن السدي", "أبو الجحاف", "ثوير بن أبي فاختة",
]

# ==============================================================================
# 3. الكتب (أسماء الملفات مصححة)
# ==============================================================================

BASE_URL = "https://raw.githubusercontent.com/AhmedBaset/hadith-json/main/db/by_book/the_9_books/"

BOOKS_PIPELINE = [
    {"id": 1, "file": "malik.json", "title": "موطأ الإمام مالك", "author": "مالك بن أنس الأصبحي", "era": "القرن الثاني الهجري"},
    {"id": 2, "file": "bukhari.json", "title": "صحيح البخاري", "author": "محمد بن إسماعيل البخاري", "era": "القرن الثالث الهجري"},
    {"id": 3, "file": "muslim.json", "title": "صحيح مسلم", "author": "مسلم بن الحجاج النيسابوري", "era": "القرن الثالث الهجري"},
    {"id": 4, "file": "abudawud.json", "title": "سنن أبي داود", "author": "أبو داود السجستاني", "era": "القرن الثالث الهجري"},
    {"id": 5, "file": "nasai.json", "title": "سنن النسائي", "author": "أحمد بن شعيب النسائي", "era": "القرن الثالث الهجري"},
    {"id": 6, "file": "tirmidhi.json", "title": "جامع الترمذي", "author": "محمد بن عيسى الترمذي", "era": "القرن الثالث الهجري"},
    {"id": 7, "file": "ibnmajah.json", "title": "سنن ابن ماجه", "author": "محمد بن يزيد بن ماجه", "era": "القرن الثالث الهجري"},
]

# ==============================================================================
# 4. التوحيد مع الاحتفاظ بخريطة المواقع إلى النص الأصلي
# ==============================================================================

# التشكيل، التطويل، وعلامات الاتجاه الخفية
_STRIP_CHARS = re.compile(r"[ؐ-ًؚ-ٰٟۖ-ۭـ‌-‏‪-‮]")
_CHAR_MAP = {"أ": "ا", "إ": "ا", "آ": "ا", "ٱ": "ا", "ة": "ه", "ى": "ي"}


# فاصلة مقحمة داخل الاسم في بعض النسخ: «ابن، شهاب»، «ابي، هريره»
_STRAY_COMMA_AFTER = ("بن", "ابن", "ابي", "ابو", "ابا", "ام", "بنت")


def normalize(text):
    """يعيد (النص الموحّد، قائمة: موقع كل حرف موحّد في النص الأصلي)."""
    out, index_map = [], []
    for i, ch in enumerate(text):
        # «ـ رضى الله عنها ـ»: التطويل المنفرد يُستعمل شرطةً في بعض النسخ
        if ch == "ـ" and (i == 0 or text[i - 1] == " " or i + 1 == len(text) or text[i + 1] == " "):
            ch = "-"
        elif _STRIP_CHARS.match(ch):
            continue
        ch = _CHAR_MAP.get(ch, ch)
        if ch == "،":
            tail = "".join(out[-5:]).split()
            word = tail[-1] if tail else ""
            if word and (word in _STRAY_COMMA_AFTER or (word[:1] == "و" and word[1:] in _STRAY_COMMA_AFTER)) \
                    and "".join(out[-len(word):]) == word:
                continue
        out.append(ch)
        index_map.append(i)
    return "".join(out), index_map


def norm(text):
    return normalize(text)[0]


# ==============================================================================
# 5. فصل السند عن المتن (على النص الموحّد)
# ==============================================================================

# السند سلسلة حلقات: «صيغة أداء + اسم راوٍ» تفصلها فواصل. نمشي عليها حلقةً حلقة
# من أول النص، ونقف عند أول جملة ليست حلقة إسناد (وهناك يبدأ المتن).

_W = r"(?=[\s،,:؛.\-]|$)"
# صيغ الأداء
_LINK = re.compile(r"(?:و|ف)?(?:حدثن[اي]ه?|حدثتن[اي]|حدثته|حدثه|حدثها|حدثهم|اخبرن[اي]ه?|اخبرتن[اي]|اخبرته|اخبره|اخبرها|اخبرهم|انبان[اي]|قرات علي|قري علي|عن|سمعت|سمع|ان)" + _W)
# الكنية ثم الاسم: «حدثنا ابو موسي، محمد بن المثني حدثنا» — بدلٌ من الراوي نفسه
_APPOSITION = re.compile(r"[\s،]+((?:[^\s،\-]+\s+){0,5}?[^\s،\-]+)\s+(?=(?:و|ف)?(?:حدثن|اخبرن|انبان|حدثتن|اخبرتن|قال\s+(?:حدثن|اخبرن)|عن\s))")
_LINK_PREFIX = r"(?=\s*(?:و|ف)?(?:حدثن|اخبرن|انبان|قرات|عن\s))"
# ما يجوز تخطّيه بين الحلقات: «قال/قالا» قبل الصيغة، «قال ابن المثني حدثنا»، «ح» التحويل،
# «انه/انها» قبل «قال/سمع»
_GLUE = re.compile(
    r"(?:[\s،,:؛.]+"
    r"|(?:و|ف)?قال(?:ا|وا|ت)?\s+(?:[^\s،]+\s+){1,3}?" + _LINK_PREFIX +
    r"|(?:و|ف)?قال(?:ا|وا|ت)?" + _W +
    r"|ح" + _W +
    r"|(?:كلاهما|جميعا|كلهم|جميعهم)" + _W +
    r"|و(?=\s)"
    r"|انه(?=\s+(?:قال|سمع|حدث|اخبر|بلغه))|انها(?=\s+(?:قالت|سمعت|حدث|اخبر|بلغها))"
    r"|بلغ(?:ه|ها|ني|نا)(?=\s+(?:عن|ان)\s))"
)
# توضيح بين شرطتين أو ترضٍّ: «- يعني ابن صالح -»، «- رضي الله عنه -»
_ASIDE = re.compile(r"[\s،,]*-[^-\n]{0,70}-|[\s،,]*\(?رضي الله عن(?:ه|ها|هما|هم)\)?")
# نهاية اسم الراوي داخل الحلقة
_NAME_END = re.compile(r"[،,:؛.\-\n{«]|\s(?:يقول|تقول|يحدث|قال|قالا|قالوا|قالت|يرفعه|رفعه|انه|انها|انهم|انهما|ان|عن|حدث\S*|اخبر\S*|كان|كانوا|كانت|زوج|وهو)" + _W)
# الحلقة التي اسمها النبي ﷺ: نقف قبلها (المتن يبدأ منها، مثل initial_hadiths.json)
_PROPHET_NAME = re.compile(r"\s*(?:رسول الله|النبي|نبي الله)" + _W)
# ليس اسم راوٍ: «عن قول الله»، «عن ذلك»...
_NOT_A_NAME = re.compile(r"\s*(?:قول|ذلك|هذا|هذه|شيء|الصلاه|امر|سال|سالت|فيه|فيها|نفسه|كان|كانت|راي|رات)" + _W)
# اسم فيه ذكر النبي ﷺ فهو جملة من المتن، لا راوٍ
_PROPHET_INSIDE = re.compile(r"رسول الله|النبي|صلي الله")
# راوٍ معطوف بعد فاصلة: «حدثنا زهير بن حرب، ومحمد بن المثني، قالا»
_COORD = re.compile(r"و(?!(?:قال|كان|لا|لم|ما|هو|هي|ان)" + _W + r")(?=[ء-ي])")

MAX_NAME_LEN = 80
MAX_NAME_WORDS = 12


def _skip(n, p):
    while True:
        m = _GLUE.match(n, p) or _ASIDE.match(n, p)
        if not m or m.end() == p:
            return p
        p = m.end()


def split_sanad_matn(original):
    """يعيد (السند، المتن، نوع الفصل، الحلقات) بالنص الأصلي المشكول.

    الحلقات: لكل راوٍ بترتيب وروده {name: اسمه موحّداً، after: ما يليه،
    matn_head: أول المتن}، ليُطابَق كل اسم في حلقته مع معرفة من قبله ومن بعده."""
    n, idx = normalize(original)
    if not n.strip():
        return "", original.strip(), "empty", []

    p, cut, links = 0, 0, 0
    segments = []
    while True:
        q = _skip(n, p)
        link = _LINK.match(n, q)
        if link:
            name_start = link.end()
            # «اخبره ان المسور...»: صيغة تتلوها صيغة، فالاسم في الحلقة التالية
            nxt = _LINK.match(n, _skip(n, name_start))
            if nxt and n[name_start:nxt.start()].strip(" ،") == "":
                p = name_start
                continue
        elif links and _COORD.match(n, q) and n[p:q].strip() in ("", "،"):
            name_start = q + 1  # راوٍ معطوف
        elif links and (ap := _APPOSITION.match(n, p)) and not _PROPHET_INSIDE.search(ap.group(1)) \
                and not re.search(r"(?:^|\s)(?:قال|كان|يقول|في|من|الي|علي)(?:\s|$)", ap.group(1))                 and re.search(r"(?:^|\s)(?:بن|ابن|ابو|ابي|ام|بنت)(?:\s|$)", ap.group(1)):
            # الاسم بعد الكنية يُلحق بحلقة الراوي نفسه
            s0, _ = segments[-1]
            segments[-1] = (s0, ap.end(1))
            cut = p = ap.end(1)
            continue
        else:
            break
        if _PROPHET_NAME.match(n, name_start) or _NOT_A_NAME.match(n, name_start):
            break
        end = _NAME_END.search(n, name_start + 1)
        name_end = end.start() if end else len(n)
        name = n[name_start:name_end].strip()
        if not name or _PROPHET_INSIDE.search(name) or name_end - name_start > MAX_NAME_LEN or len(name.split()) > MAX_NAME_WORDS:
            break
        links += 1
        segments.append((name_start, name_end))
        cut = name_end
        p = name_end
        aside = _ASIDE.match(n, p)
        if aside:
            cut = p = aside.end()

    if links < 2:
        return "", original.strip(), "no_split", []

    orig_cut = idx[cut] if cut < len(idx) else len(original)
    sanad = original[:orig_cut].strip().rstrip("،,:؛ ‏")
    matn = original[orig_cut:].strip().lstrip("،,:؛ ‏")
    method = "marfu" if _PROPHET_NAME.search(n[cut:cut + 40]) or "رسول الله" in n[cut:cut + 40] else "mawquf"
    links_out = [
        {"name": n[s:e].strip(), "after": n[e:e + 40], "matn_head": n[cut:cut + 60]}
        for s, e in segments
    ]
    return sanad, matn, method, links_out


# ==============================================================================
# 6. مطابقة الرواة بحدود كلمات ومنع التداخل
# ==============================================================================

_AR = "ء-ي"  # حروف عربية بعد التوحيد
# ما يجوز أن يلي اسماً مفرداً (كنية/لقب/اسم واحد) في السند
_SHORT_FOLLOW = re.compile(r"\s*(?:$|[،,:؛.\"«»()]|عن\b|قال|ان\b|انه|انها|حدث|اخبر|يقول|يحدث|سمع|يرفعه|رفعه|رضي)")
# «عن معاوية، - يعني ابن صالح -»: التوضيح يدل على أنه شخص آخر
_CLARIFIED = re.compile(r"[\s،,]*-?\s*(?:يعني|وهو|هو)\s+(?:ابن|بن|ابا|ابو|ابي)\s")
_PRECEDED_BY_SON = re.compile(r"(?:^|\s)و?(?:بن|ابن|بنت|ابنه|ابي|ابو|ابا|ام|مولي|ابنا)\s*$")

# ------------------------------------------------------------------------------
# صيغ ملتبسة: لا تُحتسب إلا إذا أثبت سياق السند عينَ الراوي.
#   «حدثنا عثمان» = عثمان بن أبي شيبة غالباً، «عن أبي سفيان عن جابر» = طلحة بن نافع،
#   «ابن شهاب عن أبي أمامة» = أبو أمامة بن سهل بن حنيف، «معاوية» = معاوية بن صالح وغيره،
#   «حدثنا مسدد عن يحيى بن سعيد» = القطان، «ابن القاسم عن مالك» = المصري صاحب مالك.
# السياق: prev = الراوي عنه (الحلقة السابقة)، next = شيخه (الحلقة التالية)،
# after = ما يلي الاسم، matn = أول المتن إن كان آخر حلقة.
# ------------------------------------------------------------------------------

def _any(text, names):
    """هل اسم الحلقة هو أحد هذه الأسماء (مطابقة من أول الاسم، لا في وسطه)؟
    «عامر بن ربيعة» لا يطابق «ربيعة»، و«مالك بن أنس» يطابق «مالك»."""
    t = text.strip()
    return any(t == (x := norm(n)) or t.startswith(x + " ") for n in names)


# تلاميذ كل صحابي/إمام ممن تثبت روايتهم عنه (الحلقة السابقة مباشرة)
STUDENTS = {
    "uthman": ["أبان بن عثمان", "مروان بن الحكم", "مروان", "سعيد بن المسيب", "ابن المسيب",
               "حمران", "حمران مولى عثمان", "الزهري", "ابن شهاب"],
    "muawiya": ["محمد بن سيرين", "ابن سيرين", "حسان بن عطية", "مكحول", "حميد بن عبد الرحمن",
                "حميد بن عبد الرحمن بن عوف", "عمير بن هانئ", "سعيد بن المسيب", "ابن المسيب",
                "عبد الله بن عامر", "أبو صالح السمان", "معبد الجهني", "ابن محيريز", "عبد الله بن محيريز"],
    "abu_sufyan": ["معاوية", "معاوية بن أبي سفيان", "ابن عباس", "عبد الله بن عباس", "عروة", "عروة بن الزبير"],
    "marwan": ["عروة", "عروة بن الزبير", "الزهري", "ابن شهاب", "سهل بن سعد", "أبو بكر بن عبد الرحمن",
               "عبيد الله بن عبد الله", "علي بن حسين"],
    "abu_umama": ["خالد بن معدان", "سليم بن عامر", "القاسم أبو عبد الرحمن", "القاسم بن عبد الرحمن",
                  "شداد أبو عمار", "محمد بن زياد", "سليمان بن حبيب", "رجاء بن حيوة", "مكحول",
                  "شرحبيل بن مسلم", "لقمان بن عامر", "أبو غالب", "أبي غالب", "أبو سلام", "أبي سلام",
                  "حبيب بن عبيد", "يحيى بن أبي كثير"],
    "yahya_ansari": ["مالك", "الليث", "الليث بن سعد", "سليمان بن بلال", "حماد بن زيد", "عبد الوهاب",
                     "عبد الوهاب الثقفي", "يزيد بن هارون", "سفيان", "ابن عيينة", "سفيان بن عيينة",
                     "شعبة", "هشيم", "جرير", "ابن جريج", "حماد بن سلمة", "أبو خالد الأحمر",
                     "عبد الله بن نمير", "ابن نمير", "يحيى بن سعيد القطان", "أنس بن عياض"],
    "rabia": ["مالك", "الليث", "سليمان بن بلال", "سفيان", "الثوري", "ابن جريج", "عبد العزيز بن محمد",
              "الدراوردي", "إسماعيل بن جعفر", "أنس بن عياض", "يحيى بن سعيد"],
}
# شيوخ يحيى بن سعيد الأنصاري (الحلقة التالية)
YAHYA_ANSARI_TEACHERS = ["سعيد بن المسيب", "ابن المسيب", "القاسم بن محمد", "عمرة", "عمرة بنت عبد الرحمن",
                         "محمد بن إبراهيم", "محمد بن إبراهيم التيمي", "أنس", "أنس بن مالك", "بشير بن يسار",
                         "سليمان بن يسار", "أبي بكر بن محمد", "أبو بكر بن محمد", "عبد الرحمن بن القاسم",
                         "عدي بن ثابت", "سعد بن إبراهيم", "محمد بن يحيى بن حبان", "عبد الله بن دينار",
                         "نافع", "سالم بن عبد الله", "عبيد بن حنين", "عباد بن تميم", "أبي سلمة", "أبو سلمة"]
_PROPHET_START = re.compile(r"^[\s،,.\-]*(?:عن|ان|قال|سمعت|سمع|انه سمع|انه قال قال)?\s*(?:رسول الله|النبي)")


def _ctx_uthman(c):
    return _any(c["prev"], STUDENTS["uthman"])


def _ctx_muawiya(c):
    last_to_prophet = c["next"] is None and _PROPHET_START.match(c["matn"])
    return bool(last_to_prophet) or _any(c["prev"], STUDENTS["muawiya"])


def _ctx_abu_sufyan(c):
    return _any(c["prev"], STUDENTS["abu_sufyan"])


def _ctx_marwan(c):
    return _any(c["prev"], STUDENTS["marwan"])


def _ctx_abu_umama(c):
    if _any(c["prev"], ["الزهري", "ابن شهاب"]) or re.match(r"\s*بن\s+سهل", c["after"]):
        return False  # أبو أمامة بن سهل بن حنيف
    return _any(c["prev"], STUDENTS["abu_umama"])


def _ctx_yahya_ansari(c):
    if re.match(r"\s*(?:القطان|بن\s|الاموي)", c["after"]):
        return False
    return _any(c["prev"], STUDENTS["yahya_ansari"]) or (c["next"] is not None and _any(c["next"], YAHYA_ANSARI_TEACHERS))


def _ctx_rabia(c):
    return _any(c["prev"], STUDENTS["rabia"])


def _ctx_ibn_qasim(c):
    # «عبد الرحمن بن القاسم عن مالك» هو العتقي المصري، أما المدني فيروي عن أبيه
    return not (c["next"] is not None and _any(c["next"], ["مالك", "مالك بن أنس"]))


CONTEXT_RULES = {
    "عثمان": _ctx_uthman,
    "معاوية": _ctx_muawiya,
    "مروان": _ctx_marwan,
    "أبو سفيان": _ctx_abu_sufyan, "أبي سفيان": _ctx_abu_sufyan, "أبا سفيان": _ctx_abu_sufyan,
    "أبو أمامة": _ctx_abu_umama, "أبي أمامة": _ctx_abu_umama, "أبا أمامة": _ctx_abu_umama,
    "يحيى بن سعيد": _ctx_yahya_ansari,
    "ربيعة": _ctx_rabia,
    "عبد الرحمن بن القاسم": _ctx_ibn_qasim,
}


def _compile(pat):
    p = re.escape(norm(pat)).replace(r"\ ", r"\s+")
    return re.compile(rf"(?<![{_AR}]){p}(?![{_AR}])")


def _is_short(pat):
    """اسم مفرد أو كنية بلا نسب كامل: يحتاج قيوداً إضافية."""
    return " بن " not in f" {norm(pat)} " and not norm(pat).startswith("ابن ")


# كل الصيغ مرتبة من الأطول للأقصر، حتى يأخذ الاسم الكامل موضعه قبل الجزئي
_PATTERNS = sorted(
    (
        {"id": n["id"], "raw": p, "re": _compile(p), "short": _is_short(p), "len": len(norm(p)),
         "context": CONTEXT_RULES.get(p)}
        for n in TARGET_NARRATORS for p in n["patterns"]
    ),
    key=lambda x: -x["len"],
)
_BLACKLIST = [_compile(b) for b in STRICT_BLACKLIST]
_REGION = {n["id"]: n["region"] for n in TARGET_NARRATORS}
_ERA = {n["id"]: n["era"] for n in TARGET_NARRATORS}


def _match_in_link(name, after, ctx):
    """الرواة المطابَقون داخل حلقة واحدة (قد تضم معطوفين بلا فاصلة)."""
    text = name + after  # ما يلي الاسم يُحتاج لقيود الاسم المفرد
    limit = len(name)
    taken = [False] * len(text)
    found = []
    for p in _PATTERNS:
        for m in p["re"].finditer(text, 0, limit):
            s, e = m.start(), m.end()
            if any(taken[s:e]):
                continue
            # «سالم بن عبد الله بن عمر» لا يُحسب «عبد الله بن عمر»، و«هشام بن عروة» لا يُحسب «عروة»
            if _PRECEDED_BY_SON.search(text[:s]):
                continue
            # الاسم المفرد يجب أن يليه فاصل سندي، لا «بن» ولا اسم آخر
            if p["short"] and (not _SHORT_FOLLOW.match(text, e) or _CLARIFIED.match(text, e)):
                continue
            if p["context"] and not p["context"]({**ctx, "after": text[e:]}):
                continue
            for k in range(s, e):
                taken[k] = True
            found.append((s, p["id"], p["raw"]))
    return [(nid, raw) for _, nid, raw in sorted(found)]


def match_chain(links):
    """يعيد [(id, الصيغة, رقم الحلقة)] بترتيب السند، بلا تكرار."""
    out, seen = [], set()
    for i, link in enumerate(links):
        ctx = {
            "prev": links[i - 1]["name"] if i > 0 else "",
            "next": links[i + 1]["name"] if i + 1 < len(links) else None,
            "matn": link["matn_head"],
        }
        for nid, raw in _match_in_link(link["name"], link["after"], ctx):
            if nid not in seen:
                seen.add(nid)
                out.append((nid, raw, i))
    return out


# ------------------------------------------------------------------------------
# المراسيل والبلاغات
# ------------------------------------------------------------------------------
_BALAGH = re.compile(rf"(?<![{_AR}])(?:انه\s+)?بلغ(?:ه|ني|نا|ها)(?![{_AR}])")
# تابعيون يكثر إرسالهم؛ إذا كان أحدهم آخر السند والمتن يبدأ بالنبي ﷺ فهو مرسل
IRSAL_TABIIN = [
    "سعيد بن المسيب", "ابن المسيب", "عروة بن الزبير", "عروة", "القاسم بن محمد", "سالم بن عبد الله",
    "سليمان بن يسار", "عطاء بن يسار", "عطاء بن أبي رباح", "عطاء", "الحسن", "الحسن البصري",
    "محمد بن سيرين", "ابن سيرين", "الزهري", "ابن شهاب", "مكحول", "زيد بن أسلم", "محمد بن المنكدر",
    "مجاهد", "قتادة", "الشعبي", "إبراهيم", "إبراهيم النخعي", "أبو سلمة بن عبد الرحمن",
    "أبي سلمة بن عبد الرحمن", "حميد بن عبد الرحمن", "عبد الله بن أبي بكر", "ربيعة", "يحيى بن سعيد",
    "عمر بن عبد العزيز", "رجاء بن حيوة", "حسان بن عطية", "أبو الزناد", "نافع", "سعيد بن جبير",
    "عكرمة", "طاوس", "أبو قلابة", "عبيد الله بن عبد الله بن عتبة", "أبو بكر بن عبد الرحمن",
    "خارجة بن زيد", "علي بن حسين", "محمد بن علي", "أبو جعفر", "صفوان بن سليم", "إسماعيل بن أبي حكيم",
]


# متن محال على ما قبله: «مثل ذلك»، «بهذا الإسناد نحوه»
_NO_OWN_MATN = re.compile(r"^[\s،.\-]*(?:ب?هذا|بمثله|بمثل|مثله|مثل ذلك|نحوه|بنحوه|بنحو|بمعناه|بمعني|معناه|باسناده|باسناد|ح)")


def transmission_note(sanad, links, method):
    """«بلاغ» أو «مرسل» أو None — كشفٌ آلي يحتاج مراجعة، لا حكم نهائي."""
    head = links[-1]["matn_head"] if links else ""
    if _BALAGH.search(norm(sanad)) or _BALAGH.match(head.strip()):
        return "بلاغ"
    if method == "marfu" and links and _any(links[-1]["name"], IRSAL_TABIIN) \
            and _PROPHET_START.match(links[-1]["matn_head"]):
        return "مرسل"
    return None


def blacklisted(sanad):
    n = norm(sanad)
    for pat, b in zip(_BLACKLIST, STRICT_BLACKLIST):
        if pat.search(n):
            return b
    return None


def badge_for(ids):
    sham = any("الشام" in _REGION[i] or "مكة" in _REGION[i] for i in ids)
    madinah = any("المدينة" in _REGION[i] for i in ids)
    if sham and madinah:
        return "مسار مشترك (أموي / مدني)", "مشترك"
    if sham:
        return "مسار أهل الشام والدائرة الأموية", "الشام"
    return "مسار أهل المدينة المنورة", "المدينة"


# ==============================================================================
# 7. التنزيل والتشغيل
# ==============================================================================

def load_book(book):
    os.makedirs(CACHE_DIR, exist_ok=True)
    path = os.path.join(CACHE_DIR, book["file"])
    if not os.path.exists(path):
        req = urllib.request.Request(BASE_URL + book["file"], headers={"User-Agent": "Mozilla/5.0"})
        with urllib.request.urlopen(req, timeout=120) as resp, open(path, "wb") as f:
            f.write(resp.read())
    with open(path, encoding="utf-8") as f:
        return json.load(f)


def execute_pipeline():
    random.seed(RANDOM_SEED)
    corpus = {
        "database_name": "athar_db",
        "version": 2,
        "metadata": {
            "project": "أثر",
            "source": "AhmedBaset/hadith-json",
            "label": ACCEPTED_LABEL,
            "note": "القبول هنا مطابقة أسماء رواة المسار وخلو السند من القائمة السوداء، وليس حكماً على صحة الإسناد.",
        },
        "books": [{k: b[k] for k in ("id", "title", "author", "era")} for b in BOOKS_PIPELINE],
        "narrators": [
            {"id": n["id"], "name": n["name"], "popular_name": None, "region": n["region"],
             "era": n["era"], "is_trusted": 1, "sect_affiliation": None, "notes": None}
            for n in TARGET_NARRATORS
        ],
        "hadiths": [],
    }
    report = {"label": ACCEPTED_LABEL, "books": [], "samples": {"accepted": [], "rejected": {}}}
    accepted_pool, rejected_pool = [], {"blacklist": [], "lt2_narrators": [], "no_split": [], "no_own_matn": []}
    seq = 1

    for b in BOOKS_PIPELINE:
        try:
            data = load_book(b)
        except Exception as e:
            print(f"❌ {b['title']}: تعذر التنزيل ({e})")
            report["books"].append({"id": b["id"], "title": b["title"], "error": str(e)})
            continue

        chapters = {c["id"]: c.get("arabic") or "" for c in data.get("chapters", [])}
        stats = {"total": 0, "empty": 0, "no_split": 0, "blacklist": 0, "lt2_narrators": 0, "accepted": 0,
                 "split_marfu": 0, "split_mawquf": 0, "mursal_or_balagh": 0, "no_own_matn": 0,
                 "badges": {"المدينة": 0, "الشام": 0, "مشترك": 0}}

        for h in data.get("hadiths", []):
            stats["total"] += 1
            text = (h.get("arabic") or "").strip()
            if not text:
                stats["empty"] += 1
                continue
            sanad, matn, method, links = split_sanad_matn(text)
            ref = {"book": b["title"], "idInBook": h.get("idInBook")}
            if method == "no_split":
                stats["no_split"] += 1
                rejected_pool["no_split"].append({**ref, "text": text[:220]})
                continue
            stats["split_" + method] += 1

            bad = blacklisted(sanad)
            if bad:
                stats["blacklist"] += 1
                rejected_pool["blacklist"].append({**ref, "reason": bad, "sanad": sanad})
                continue

            found = match_chain(links)
            ids = [f[0] for f in found]
            if len(ids) < 2:
                stats["lt2_narrators"] += 1
                rejected_pool["lt2_narrators"].append({**ref, "matched": [f[1] for f in found], "sanad": sanad})
                continue

            if _NO_OWN_MATN.match(norm(matn)) or len(norm(matn).split()) < 4:
                stats["no_own_matn"] += 1
                rejected_pool["no_own_matn"].append({**ref, "sanad": sanad, "text": matn[:120]})
                continue

            note = transmission_note(sanad, links, method)
            if note:
                stats["mursal_or_balagh"] += 1
            badge, region_tag = badge_for(ids)
            chapter = chapters.get(h.get("chapterId"), "") or "أبواب السنن والآثار"
            corpus["hadiths"].append({
                "id": seq,
                "book_id": b["id"],
                "chapter": chapter,
                "topic": chapter,
                "path_badge": badge,
                "region_tag": region_tag,
                "raw_sanad": sanad,
                "matn": matn,
                "historical_context": "",
                "is_mursal_or_balagh": note is not None,
                "transmission_note": note,
                "narrator_ids": ids,
                "source_ref": {"book_file": b["file"], "idInBook": h.get("idInBook")},
            })
            accepted_pool.append({**ref, "badge": badge, "note": note, "matched": [f[1] for f in found],
                                  "sanad": sanad, "matn": matn[:160]})
            stats["accepted"] += 1
            stats["badges"][region_tag] += 1
            seq += 1

        report["books"].append({"id": b["id"], "title": b["title"], **stats})
        print(f"✅ {b['title']}: {ACCEPTED_LABEL} {stats['accepted']} من {stats['total']}")

    report["samples"]["accepted"] = random.sample(accepted_pool, min(SAMPLE_SIZE, len(accepted_pool)))
    for k, pool in rejected_pool.items():
        report["samples"]["rejected"][k] = random.sample(pool, min(8, len(pool)))
    report["total_accepted"] = len(corpus["hadiths"])

    with open(OUTPUT_CORPUS, "w", encoding="utf-8") as f:
        json.dump(corpus, f, ensure_ascii=False, indent=2)
    with open(OUTPUT_REPORT, "w", encoding="utf-8") as f:
        json.dump(report, f, ensure_ascii=False, indent=2)
    print(f"\n📊 الإجمالي: {len(corpus['hadiths'])} — {os.path.abspath(OUTPUT_CORPUS)}")


if __name__ == "__main__":
    if hasattr(sys.stdout, "reconfigure"):
        sys.stdout.reconfigure(encoding="utf-8")
    execute_pipeline()
