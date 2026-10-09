# -*- coding: utf-8 -*-
"""Genere les captures (PNG) : code source colore, sorties console, fichiers .txt / .ser.
Ce sont des rendus fideles du vrai code et des vraies sorties (pas des captures IntelliJ)."""
import os, re, sys, html, subprocess
sys.path.insert(0, os.path.dirname(__file__))
from pygments import highlight
from pygments.lexers import JavaLexer
from pygments.formatters import HtmlFormatter
from shot import html_to_png

BASE = os.path.abspath(os.path.join(os.path.dirname(__file__), ".."))
SRC = os.path.join(BASE, "src")
OUT = os.path.abspath(os.path.join(BASE, "..", "rendu_tp5", "captures"))
os.makedirs(OUT, exist_ok=True)

FONT = "Consolas, 'Cascadia Mono', monospace"
LH = 24  # hauteur de ligne


def read_lines(fname):
    with open(os.path.join(SRC, fname), encoding="utf-8") as f:
        return f.read().split("\n")


def extract(fname, start_re, end_re):
    """Retourne (premiere_ligne_num, [lignes]) du premier bloc entre start_re et end_re (inclus)."""
    lines = read_lines(fname)
    s = next(i for i, l in enumerate(lines) if re.search(start_re, l))
    e = next(i for i in range(s, len(lines)) if re.search(end_re, lines[i]))
    return s + 1, lines[s:e + 1]


def code_png(name, blocks, width=1180):
    """blocks = [(fichier, start_re, end_re)] -> une image avec un onglet par bloc."""
    fmt = HtmlFormatter(style="monokai", nowrap=True, noclasses=True)
    body, total_h = [], 20
    for fname, s_re, e_re in blocks:
        n0, lines = extract(fname, s_re, e_re)
        code = highlight("\n".join(lines), JavaLexer(), fmt).split("\n")
        rows = []
        for k, c in enumerate(code[:len(lines)]):
            rows.append(f'<div class="l"><span class="n">{n0 + k}</span><span class="t">{c or "&nbsp;"}</span></div>')
        body.append(f'<div class="tab">&#9679; {fname}</div><div class="code">{"".join(rows)}</div>')
        total_h += 44 + len(lines) * LH + 30
    page = f"""<html><head><meta charset="utf-8"><style>
body{{margin:0;background:#1e1f1c;font-family:{FONT};}}
.win{{padding:10px 14px 0 14px;}}
.tab{{display:inline-block;background:#3a3b36;color:#f8f8f2;font:600 15px 'Segoe UI',sans-serif;padding:6px 16px;border-radius:6px 6px 0 0;margin-top:14px}}
.code{{background:#272822;border:1px solid #3a3b36;padding:8px 0;margin-bottom:6px}}
.l{{display:flex;height:{LH}px;line-height:{LH}px;font-size:16.5px;white-space:pre}}
.n{{width:54px;text-align:right;color:#75715e;padding-right:16px;flex:none;user-select:none}}
.t{{color:#f8f8f2}}
</style></head><body><div class="win">{"".join(body)}</div></body></html>"""
    html_to_png(page, os.path.join(OUT, name + ".png"), width, total_h)
    print("code", name)


def console_png(name, text_file, cmd, line_from=0, line_to=None, width=1180):
    with open(text_file, encoding="utf-8") as f:
        lines = f.read().rstrip("\n").split("\n")
    lines = lines[line_from:line_to]
    out = []
    for l in lines:
        e = html.escape(l)
        if l.startswith(">>>"):                    cls = "cy"
        elif l.startswith("===="):                 cls = "mg"
        elif l.startswith("Attrape") or l.startswith("Fichier incorrect"): cls = "yl"
        elif l.startswith("ERREUR") or l.startswith("Erreur"): cls = "rd"
        elif l.startswith("---") or l.startswith("Cause"): cls = "gy"
        else:                                      cls = ""
        out.append(f'<div class="{cls}">{e or "&nbsp;"}</div>')
    h = 52 + 34 + (len(lines) + 2) * 21 + 20
    page = f"""<html><head><meta charset="utf-8"><style>
body{{margin:0;background:#0c0c0c;font-family:{FONT};}}
.bar{{background:#1f1f1f;color:#ddd;font:14px 'Segoe UI',sans-serif;padding:9px 16px;border-bottom:1px solid #333}}
.term{{padding:12px 18px;font-size:15.5px;color:#cccccc;line-height:21px;white-space:pre}}
.p{{color:#f2f2f2}} .cy{{color:#61d6d6}} .mg{{color:#d670d6;font-weight:bold}} .yl{{color:#f9f1a5}}
.rd{{color:#f14c4c}} .gy{{color:#8a8a8a}}
</style></head><body><div class="bar">Windows PowerShell</div>
<div class="term"><div class="p">PS ...\\semaine 5\\tp5_fastbite&gt; {html.escape(cmd)}</div>{"".join(out)}
<div class="p">PS ...\\semaine 5\\tp5_fastbite&gt; <span style="background:#ccc">&nbsp;</span></div></div></body></html>"""
    html_to_png(page, os.path.join(OUT, name + ".png"), width, h)
    print("console", name, len(lines), "lignes")


def txt_png(name, path, title, width=1100):
    with open(path, encoding="utf-8") as f:
        lines = f.read().rstrip("\n").split("\n")
    rows = "".join(f'<div class="l"><span class="n">{i + 1}</span>{html.escape(l) or "&nbsp;"}</div>' for i, l in enumerate(lines))
    h = 40 + 30 + len(lines) * 26 + 40
    page = f"""<html><head><meta charset="utf-8"><style>
body{{margin:0;background:#fff;font-family:{FONT};}}
.t{{background:#f3f3f3;border-bottom:1px solid #ccc;font:14px 'Segoe UI',sans-serif;padding:9px 14px;color:#222}}
.b{{padding:10px 8px;font-size:18px}} .l{{height:26px;line-height:26px;white-space:pre;color:#111}}
.n{{display:inline-block;width:36px;color:#999;text-align:right;margin-right:16px}}
.s{{background:#f3f3f3;border-top:1px solid #ccc;font:13px 'Segoe UI',sans-serif;padding:6px 14px;color:#555}}
</style></head><body><div class="t">{html.escape(title)}</div><div class="b">{rows}</div>
<div class="s">UTF-8 &nbsp;|&nbsp; {len(lines)} lignes &nbsp;|&nbsp; separateur : point-virgule</div></body></html>"""
    html_to_png(page, os.path.join(OUT, name + ".png"), width, h)
    print("txt", name)


def hex_png(name, path, title, nbytes=272, width=1100):
    data = open(path, "rb").read()
    total = len(data)
    data = data[:nbytes]
    rows = []
    for off in range(0, len(data), 16):
        chunk = data[off:off + 16]
        hx = " ".join(f"{b:02x}" for b in chunk).ljust(47)
        asc = "".join(chr(b) if 32 <= b < 127 else "." for b in chunk)
        rows.append(f'<div class="l"><span class="o">{off:08x}</span>  <span class="h">{hx}</span>  <span class="a">{html.escape(asc)}</span></div>')
    h = 40 + 24 + len(rows) * 24 + 60
    page = f"""<html><head><meta charset="utf-8"><style>
body{{margin:0;background:#1e1e1e;font-family:{FONT};}}
.t{{background:#2d2d2d;color:#ddd;font:14px 'Segoe UI',sans-serif;padding:9px 14px}}
.b{{padding:14px 18px;font-size:17px}} .l{{height:24px;line-height:24px;white-space:pre}}
.o{{color:#858585}} .h{{color:#9cdcfe}} .a{{color:#ce9178}} .f{{color:#6a9955;font:14px 'Segoe UI',sans-serif;padding:0 18px}}
</style></head><body><div class="t">{html.escape(title)} - {total} octets (fichier binaire, 272 premiers octets affiches)</div>
<div class="b">{"".join(rows)}</div>
<div class="f">ac ed 00 05 = signature d'un flux Java serialise ; on y lit les noms de classes (ArrayList, Restaurant, Plat...) et les valeurs.</div></body></html>"""
    html_to_png(page, os.path.join(OUT, name + ".png"), width, h)
    print("hex", name)


if __name__ == "__main__":
    # ---------------- Q1 : code
    code_png("q1_code_hierarchie", [
        ("FastBiteException.java", r"public class FastBiteException", r"^}"),
        ("NoteInvalideException.java", r"public class NoteInvalideException", r"^}"),
        ("Evaluable.java", r"public interface Evaluable", r"^}"),
    ])
    code_png("q1_code_throw", [
        ("Livreur.java", r"public void accepterLivraison", r"^    }"),
        ("Commande.java", r"public void ajouterPlat", r"^    }"),
        ("Commande.java", r"public void valider", r"^    }"),
        ("Livreur.java", r"public void ajouterNote", r"^    }"),
    ])
    code_png("q1_code_catch", [
        ("Client.java", r"public void noterRider", r"^    }"),
        ("Main.java", r"A\. Scenario TP3", r"^        }$"),
        ("Main.java", r"B5\. Livreur deja occupe", r"^        }$"),
    ])
    # ---------------- Q2 : code
    code_png("q2_code_ecriture", [("Restaurant.java", r"public void sauvegarderMenu", r"^    }")])
    code_png("q2_code_lecture", [("Restaurant.java", r"public void chargerMenu", r"^    }")], width=1300)
    # ---------------- Q3 : code
    code_png("q3_code_serializable", [
        ("Restaurant.java", r"public class Restaurant implements", r"private static final long serialVersionUID"),
        ("Restaurant.java", r"TRANSIENT : compteur", r"private transient int nbCommandesRecues"),
        ("Plat.java", r"public class Plat implements", r"private static final long serialVersionUID"),
    ])
    code_png("q3_code_ser", [
        ("Restaurant.java", r"public static void sauvegarderRestaurants", r"^    }"),
        ("Restaurant.java", r"^    @SuppressWarnings", r"^    }"),
    ])
    # ---------------- consoles
    s = os.path.join(BASE, "sorties")
    q1 = open(os.path.join(s, "console_q1.txt"), encoding="utf-8").read().split("\n")
    iB = next(i for i, l in enumerate(q1) if l.startswith(">>> B1"))
    iA = next(i for i, l in enumerate(q1) if l.startswith(">>> A"))
    console_png("q1_console_A", os.path.join(s, "console_q1.txt"), "java -cp out Main q1", iA, iB - 3)
    console_png("q1_console_B", os.path.join(s, "console_q1.txt"), "java -cp out Main q1", iB - 3)
    console_png("q2_console", os.path.join(s, "console_q2.txt"), "java -cp out Main q2")
    console_png("q3_console", os.path.join(s, "console_q3.txt"), "java -cp out Main q3")
    # ---------------- fichiers
    txt_png("q2_fichier_txt", os.path.join(BASE, "data", "menu_chezmario.txt"), "menu_chezmario.txt - Bloc-notes")
    hex_png("q3_fichier_ser", os.path.join(BASE, "data", "restaurants.ser"), "restaurants.ser")
