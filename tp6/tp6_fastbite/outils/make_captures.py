# -*- coding: utf-8 -*-
"""TP6 : genere la maquette (wireframes), le storyboard, les extraits de code colores et les
vues des fichiers (.txt, .ser). Les images des fenetres Swing sont produites par tests/TestsTP6.java."""
import os, re, sys, html
sys.path.insert(0, os.path.dirname(__file__))
from pygments import highlight
from pygments.lexers import JavaLexer
from pygments.formatters import HtmlFormatter
from shot import html_to_png

BASE = os.path.abspath(os.path.join(os.path.dirname(__file__), ".."))
SRC = os.path.join(BASE, "src")
OUT = os.path.abspath(os.path.join(BASE, "..", "rendu_tp6", "captures"))
os.makedirs(OUT, exist_ok=True)
FONT = "Consolas, 'Cascadia Mono', monospace"
LH = 24

# ------------------------------------------------------------------ maquette fil de fer
WF_CSS = """
*{box-sizing:border-box} body{margin:0;background:#fff;font-family:'Segoe UI',Arial,sans-serif;color:#333}
h1{font-size:26px;margin:18px 28px 2px} .sub{margin:0 28px 14px;color:#666;font-size:15px}
.row{display:flex;gap:26px;padding:0 28px;align-items:flex-start}
.scr{border:2.5px solid #444;border-radius:6px;background:#fafafa;flex:1;position:relative}
.bar{border-bottom:2.5px solid #444;padding:6px 10px;font-size:14px;background:#e9e9e9;display:flex;justify-content:space-between}
.pad{padding:12px}
.ttl{text-align:center;font-weight:700;font-size:19px;margin:2px 0 10px}
.box{border:2px solid #777;background:#fff;border-radius:3px;padding:6px 8px;font-size:13.5px}
.lst{height:128px} .line{height:11px;background:#d3d3d3;margin:8px 0;border-radius:2px} .sel{background:#9a9a9a}
.btn{border:2px solid #444;border-radius:5px;background:#e3e3e3;text-align:center;padding:6px 4px;font-size:13px;font-weight:600}
.col{display:flex;flex-direction:column;gap:8px} .fl{display:flex;gap:8px}
.st{border-top:2px dashed #999;margin-top:10px;padding-top:6px;font-size:12.5px;color:#666}
.tag{position:absolute;top:-14px;left:-14px;width:30px;height:30px;border-radius:50%;background:#e85d04;color:#fff;font-weight:700;display:grid;place-items:center;font-size:16px}
.n{display:inline-grid;place-items:center;width:19px;height:19px;border-radius:50%;background:#1f3a5f;color:#fff;font-size:11.5px;font-weight:700;margin-right:5px}
.leg{margin:16px 28px 0;display:flex;gap:26px;font-size:14px;color:#333} .leg div{flex:1;line-height:1.55}
.cap{font-size:12.5px;color:#555;margin:6px 2px 0}
"""


def ecran_accueil(tag="1"):
    return f"""<div class="scr"><div class="tag">{tag}</div><div class="bar"><span>FastBite - Restaurants</span><span>&ndash; &#9633; &times;</span></div><div class="pad">
<div class="ttl">FastBite<br><span style="font-weight:400;font-size:13px">Connecté : client &middot; adresse de livraison</span></div>
<div class="fl"><div class="box lst" style="flex:1"><b style="font-size:12.5px">Restaurants disponibles</b><div class="line sel"></div><div class="line"></div><div class="line" style="width:70%"></div></div>
<div class="col" style="width:150px"><div class="btn"><span class="n">a</span>Voir le menu</div><div class="btn"><span class="n">b</span>Sauvegarder (.ser)</div><div class="btn"><span class="n">c</span>Charger (.ser)</div><div class="btn">Quitter</div></div></div>
<div class="st">Barre d'état : résultat de la dernière action</div></div></div>"""


def ecran_menu(tag="2"):
    return f"""<div class="scr" style="flex:1.35"><div class="tag">{tag}</div><div class="bar"><span>FastBite - Menu de &lt;restaurant&gt;</span><span>&ndash; &#9633; &times;</span></div><div class="pad">
<div class="ttl">Nom du restaurant - adresse</div>
<div class="fl"><div class="box lst" style="flex:1.1"><b style="font-size:12.5px">Menu (tableau : plat | prix | description | allergènes)</b><div class="line sel"></div><div class="line"></div><div class="line"></div></div>
<div class="box lst" style="flex:1"><b style="font-size:12.5px">Panier - commande n°</b><div class="line" style="width:60%"></div><div class="line" style="width:45%"></div><div style="font-weight:700;margin-top:34px">Total : 0.0 EUR</div></div></div>
<div class="fl" style="margin-top:10px;flex-wrap:wrap"><div class="btn" style="flex:1"><span class="n">d</span>Ajouter au panier</div><div class="btn" style="flex:1"><span class="n">e</span>Valider</div><div class="btn" style="flex:1.2"><span class="n">f</span>Enregistrer (.txt)</div><div class="btn" style="flex:1.2"><span class="n">g</span>Charger (.txt)</div><div class="btn" style="flex:.7">Retour</div></div>
<div class="st">Barre d'état</div></div></div>"""


def ecran_commande(tag="3"):
    return f"""<div class="scr"><div class="tag">{tag}</div><div class="bar"><span>FastBite - Commande n°</span><span>&ndash; &#9633; &times;</span></div><div class="pad">
<div class="ttl">Commande n° chez &lt;restaurant&gt;</div>
<div class="box" style="height:96px"><b style="font-size:12.5px">Récapitulatif (plats, pourboire, total, statut)</b><div class="line" style="width:55%"></div><div class="line" style="width:40%"></div><div class="line" style="width:48%"></div></div>
<div class="fl" style="margin-top:9px"><div style="flex:1.3;font-size:13px;padding-top:6px">Pourboire :</div><div class="box" style="flex:1">&nbsp;</div><div class="btn" style="flex:1.2"><span class="n">h</span>Appliquer</div></div>
<div class="fl" style="margin-top:7px"><div style="flex:1.3;font-size:13px;padding-top:6px">Note (0 à 5) :</div><div class="box" style="flex:1">&nbsp;</div><div class="btn" style="flex:1.2"><span class="n">i</span>Noter</div></div>
<div class="fl" style="margin-top:7px"><div style="flex:1.3;font-size:13px;padding-top:6px">Moyenne : - / 5</div><div class="btn" style="flex:1"><span class="n">j</span>Payer</div><div class="btn" style="flex:1.2">Accueil</div></div>
<div class="st">Barre d'état</div></div></div>"""


def wireframes():
    page = f"""<html><head><meta charset="utf-8"><style>{WF_CSS}</style></head><body>
<h1>FastBite &mdash; maquette fil de fer (wireframes)</h1>
<p class="sub">3 fenêtres Swing : gris = zones, cadres = composants, pastilles bleues = boutons qui auront un listener (Q4).</p>
<div class="row" style="margin-top:22px">{ecran_accueil()}{ecran_menu()}{ecran_commande()}</div>
<div class="leg">
<div><b>1 &middot; Accueil</b> &mdash; JFrame, BorderLayout.<br>JList des restaurants au centre, colonne de JButton à l'est (GridLayout), JLabel d'état au sud.</div>
<div style="flex:1.35"><b>2 &middot; Menu et panier</b> &mdash; JTable du menu (lecture seule) et JList du panier côte à côte (GridLayout 1&times;2), rangée de JButton (FlowLayout).</div>
<div><b>3 &middot; Commande</b> &mdash; JTextArea du récapitulatif, formulaire JLabel / JTextField / JButton (GridLayout 3&times;3).</div>
</div></body></html>"""
    html_to_png(page, os.path.join(OUT, "q1_wireframes.png"), 1500, 560)
    print("wireframes")


def storyboard():
    css = WF_CSS + """
.sb{position:relative;width:1500px;height:800px} .pos{position:absolute} .pos .scr{width:100%}
.mini .lst{height:100px} .mini .ttl{font-size:15px;margin-bottom:6px} .mini .btn{font-size:11px;padding:4px 2px} .mini .box{font-size:11px}
.mini .st{display:none} .mini .bar{font-size:12px;padding:4px 8px}
.err{position:absolute;border:2.5px solid #b23a3a;background:#fdeeee;border-radius:6px;padding:8px 10px;font-size:13px;color:#7a1f1f;width:250px}
.err b{display:block;color:#b23a3a} .file{position:absolute;border:2.5px solid #2f7a4b;background:#eaf6ee;border-radius:6px;padding:8px 10px;font-size:13px;color:#1f5133;width:230px}
.file b{display:block;color:#2f7a4b} svg{position:absolute;left:0;top:0} .lab{font:600 13px 'Segoe UI',sans-serif;fill:#1f3a5f}
"""
    page = f"""<html><head><meta charset="utf-8"><style>{css}</style></head><body>
<h1>FastBite &mdash; storyboard (navigation entre les pages)</h1>
<p class="sub">Flèches bleues : clics qui changent de page. Rouge : exception attrapée, affichée dans une boîte, on reste sur la page. Vert : fichier écrit ou lu.</p>
<div class="sb">
<svg width="1500" height="800"><defs>
<marker id="a" markerUnits="userSpaceOnUse" markerWidth="16" markerHeight="12" refX="15" refY="6" orient="auto"><path d="M0 0 L16 6 L0 12z" fill="#1f3a5f"/></marker>
<marker id="r" markerUnits="userSpaceOnUse" markerWidth="16" markerHeight="12" refX="15" refY="6" orient="auto"><path d="M0 0 L16 6 L0 12z" fill="#b23a3a"/></marker>
<marker id="g" markerUnits="userSpaceOnUse" markerWidth="16" markerHeight="12" refX="15" refY="6" orient="auto"><path d="M0 0 L16 6 L0 12z" fill="#2f7a4b"/></marker></defs>
<path d="M400 130 H560" stroke="#1f3a5f" stroke-width="3" fill="none" marker-end="url(#a)"/><text class="lab" x="412" y="120">Voir le menu</text>
<path d="M560 195 H400" stroke="#1f3a5f" stroke-width="3" fill="none" marker-end="url(#a)"/><text class="lab" x="452" y="216">Retour</text>
<path d="M1010 130 H1130" stroke="#1f3a5f" stroke-width="3" fill="none" marker-end="url(#a)"/><text class="lab" x="1020" y="120">Valider</text>
<path d="M1300 345 V410 H230 V298" stroke="#1f3a5f" stroke-width="3" fill="none" marker-end="url(#a)"/><text class="lab" x="980" y="402">Retour à l'accueil (commande payée)</text>
<path d="M120 296 V500" stroke="#2f7a4b" stroke-width="3" fill="none" marker-end="url(#g)" stroke-dasharray="7 5"/>
<path d="M700 290 V500" stroke="#2f7a4b" stroke-width="3" fill="none" marker-end="url(#g)" stroke-dasharray="7 5"/>
<path d="M330 296 V640" stroke="#b23a3a" stroke-width="3" fill="none" marker-end="url(#r)" stroke-dasharray="7 5"/>
<path d="M930 290 V500" stroke="#b23a3a" stroke-width="3" fill="none" marker-end="url(#r)" stroke-dasharray="7 5"/>
<path d="M1400 345 V500" stroke="#b23a3a" stroke-width="3" fill="none" marker-end="url(#r)" stroke-dasharray="7 5"/>
</svg>
<div class="pos mini" style="left:40px;top:40px;width:360px">{ecran_accueil("1")}</div>
<div class="pos mini" style="left:560px;top:40px;width:450px">{ecran_menu("2")}</div>
<div class="pos mini" style="left:1130px;top:40px;width:340px">{ecran_commande("3")}</div>
<div class="file" style="left:40px;top:505px"><b>Sauvegarder / Charger (.ser)</b>data/restaurants.ser<br>sérialisation de la liste des restaurants</div>
<div class="err" style="left:205px;top:645px"><b>Boîte d'erreur</b>aucun restaurant sélectionné &middot; .ser illisible (IOException) &middot; historique plein (CapaciteDepasseeException)</div>
<div class="file" style="left:600px;top:505px"><b>Enregistrer / Charger (.txt)</b>data/menu_chez_mario.txt<br>une ligne par plat, séparateur « ; »</div>
<div class="err" style="left:850px;top:505px;width:230px"><b>Boîte d'erreur</b>panier vide (CommandeVideException) &middot; fichier mal formé (FormatFichierException)</div>
<div class="err" style="left:1215px;top:505px;width:255px"><b>Boîte d'erreur</b>note hors 0-5 (NoteInvalideException) &middot; texte au lieu d'un nombre &middot; déjà payée (PaiementImpossibleException)</div>
</div></body></html>"""
    html_to_png(page, os.path.join(OUT, "q2_storyboard.png"), 1500, 860)
    print("storyboard")


# ------------------------------------------------------------------ code, fichiers
def extract(fname, start_re, end_re):
    with open(os.path.join(SRC, fname), encoding="utf-8") as f:
        lines = f.read().split("\n")
    s = next(i for i, l in enumerate(lines) if re.search(start_re, l))
    e = next(i for i in range(s, len(lines)) if re.search(end_re, lines[i]))
    return s + 1, lines[s:e + 1]


def code_png(name, blocks, width=1180):
    fmt = HtmlFormatter(style="monokai", nowrap=True, noclasses=True)
    body, total_h = [], 20
    for fname, s_re, e_re in blocks:
        n0, lines = extract(fname, s_re, e_re)
        code = highlight("\n".join(lines), JavaLexer(), fmt).split("\n")
        rows = [f'<div class="l"><span class="n">{n0 + k}</span><span class="t">{c or "&nbsp;"}</span></div>'
                for k, c in enumerate(code[:len(lines)])]
        body.append(f'<div class="tab">&#9679; {fname}</div><div class="code">{"".join(rows)}</div>')
        total_h += 44 + len(lines) * LH + 30
    page = f"""<html><head><meta charset="utf-8"><style>
body{{margin:0;background:#1e1f1c;font-family:{FONT};}} .win{{padding:10px 14px 0 14px;}}
.tab{{display:inline-block;background:#3a3b36;color:#f8f8f2;font:600 15px 'Segoe UI',sans-serif;padding:6px 16px;border-radius:6px 6px 0 0;margin-top:14px}}
.code{{background:#272822;border:1px solid #3a3b36;padding:8px 0;margin-bottom:6px}}
.l{{display:flex;height:{LH}px;line-height:{LH}px;font-size:16.5px;white-space:pre}}
.n{{width:54px;text-align:right;color:#75715e;padding-right:16px;flex:none}} .t{{color:#f8f8f2}}
</style></head><body><div class="win">{"".join(body)}</div></body></html>"""
    html_to_png(page, os.path.join(OUT, name + ".png"), width, total_h)
    print("code", name)


def txt_png(name, path, title, width=1000):
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


def hex_png(name, path, title, nbytes=192, width=1000):
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
</style></head><body><div class="t">{html.escape(title)} - {total} octets (fichier binaire, {nbytes} premiers octets)</div>
<div class="b">{"".join(rows)}</div>
<div class="f">ac ed 00 05 = signature d'un flux Java serialise ; on y lit ArrayList, Restaurant, Plat...</div></body></html>"""
    html_to_png(page, os.path.join(OUT, name + ".png"), width, h)
    print("hex", name)


if __name__ == "__main__":
    wireframes()
    storyboard()
    # Q3 : construction d'une fenetre
    code_png("q3_code_fenetre", [
        ("FenetreAccueil.java", r"public class FenetreAccueil extends JFrame", r"public class FenetreAccueil extends JFrame"),
        ("FenetreAccueil.java", r"// --- CENTRE : liste des restaurants", r"setDefaultCloseOperation"),
    ], width=1120)
    code_png("q3_code_main", [("FastBiteGUI.java", r"public static void main", r"^    }")], width=1000)
    # Q4 : listeners
    code_png("q4_code_listener", [
        ("FenetreAccueil.java", r"public void actionPerformed", r"^    }"),
        ("FenetreMenu.java", r"private void ajouterAuPanier", r"^    }"),
    ], width=1120)
    code_png("q4_code_anonyme", [("FenetreCommande.java", r"// --- LISTENERS \(classes anonymes\)", r"public void actionPerformed\(ActionEvent e\) \{ noter\(\); \}")], width=1000)
    code_png("q4_code_exceptions", [
        ("FenetreMenu.java", r"private void validerCommande", r"^    }"),
        ("FenetreCommande.java", r"private void noter\(\)", r"^    }"),
    ], width=1150)
    code_png("q4_code_fichiers", [
        ("FenetreMenu.java", r"public void chargerMenu\(String fichier\)", r"^    }"),
        ("FenetreAccueil.java", r"private void charger\(\)", r"^    }"),
    ], width=1280)
    txt_png("q4_fichier_txt", os.path.join(BASE, "data", "menu_chez_mario.txt"), "data/menu_chez_mario.txt - Bloc-notes")
    hex_png("q4_fichier_ser", os.path.join(BASE, "data", "restaurants.ser"), "data/restaurants.ser")
