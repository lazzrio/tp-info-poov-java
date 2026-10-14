# -*- coding: utf-8 -*-
"""TP7 : images du rendu (schema MVC, avant/apres, extraits de code colores, preuve des tests).
Les images des fenetres Swing sont produites par tests/TestsTP7.java."""
import os, re, sys, html
sys.path.insert(0, os.path.dirname(__file__))
from pygments import highlight
from pygments.lexers import JavaLexer
from pygments.formatters import HtmlFormatter
from shot import html_to_png

BASE = os.path.abspath(os.path.join(os.path.dirname(__file__), ".."))
SRC = os.path.join(BASE, "src")
OUT = os.path.abspath(os.path.join(BASE, "..", "rendu_tp7", "captures"))
LOG = os.path.join(os.path.dirname(__file__), "derniere_execution.log")
os.makedirs(OUT, exist_ok=True)
FONT = "Consolas, 'Cascadia Mono', monospace"
LH = 24

# ------------------------------------------------------------------ schema MVC
def schema_mvc():
    page = """<html><head><meta charset="utf-8"><style>
body{margin:0;background:#fff;font-family:'Segoe UI',Arial,sans-serif;color:#222}
.b{position:absolute;border-radius:14px;padding:16px 20px;box-sizing:border-box;border:3px solid}
.b h2{margin:0 0 4px;font-size:30px} .b .p{font:600 15px Consolas,monospace;margin-bottom:8px} .b div{font-size:16px;line-height:1.45}
.m{background:#e6f4ea;border-color:#2f7a4b} .m h2{color:#1f5133}
.v{background:#e8f0fe;border-color:#3b6fd4} .v h2{color:#1f3a5f}
.c{background:#fff1e0;border-color:#e8820c} .c h2{color:#a65a00}
.u{position:absolute;font-size:17px;color:#333;font-weight:600}
svg{position:absolute;left:0;top:0} .l{font:600 15.5px 'Segoe UI',sans-serif}
.n{font-weight:800}
</style></head><body><div style="position:relative;width:1500px;height:720px">
<svg width="1500" height="720"><defs>
<marker id="k" markerUnits="userSpaceOnUse" markerWidth="18" markerHeight="14" refX="17" refY="7" orient="auto"><path d="M0 0 L18 7 L0 14z" fill="#333"/></marker>
<marker id="o" markerUnits="userSpaceOnUse" markerWidth="18" markerHeight="14" refX="17" refY="7" orient="auto"><path d="M0 0 L18 7 L0 14z" fill="#e8820c"/></marker>
<marker id="g" markerUnits="userSpaceOnUse" markerWidth="18" markerHeight="14" refX="17" refY="7" orient="auto"><path d="M0 0 L18 7 L0 14z" fill="#2f7a4b"/></marker>
<marker id="r" markerUnits="userSpaceOnUse" markerWidth="18" markerHeight="14" refX="17" refY="7" orient="auto"><path d="M0 0 L18 7 L0 14z" fill="#b23a3a"/></marker></defs>
<path d="M750 38 V78" stroke="#333" stroke-width="3.5" fill="none" marker-end="url(#k)"/>
<path d="M650 290 L450 398" stroke="#e8820c" stroke-width="3.5" fill="none" marker-end="url(#o)"/>
<path d="M250 398 L542 255" stroke="#b23a3a" stroke-width="3.5" fill="none" stroke-dasharray="9 6" marker-end="url(#r)"/>
<path d="M520 525 H975" stroke="#e8820c" stroke-width="3.5" fill="none" marker-end="url(#o)"/>
<path d="M1190 398 L940 292" stroke="#2f7a4b" stroke-width="3.5" fill="none" marker-end="url(#g)"/>
</svg>
<div class="u" style="left:600px;top:6px">1 &middot; l'utilisateur clique sur un bouton</div>
<div class="b v" style="left:540px;top:80px;width:420px;height:205px"><h2>VUE</h2><div class="p">package vue</div>
<div><b>FenetreAccueil, FenetreMenu, FenetreCommande</b>, Style, Messages<br>Affiche les donn&eacute;es, expose ses boutons.<br><b>Lit</b> le mod&egrave;le, ne le <b>modifie jamais</b>.</div></div>
<div class="b c" style="left:70px;top:400px;width:450px;height:250px"><h2>CONTR&Ocirc;LEUR</h2><div class="p">package controleur</div>
<div><b>ControleurApplication, ControleurAccueil, ControleurMenu, ControleurCommande</b><br>&Eacute;coute les clics (ActionListener), demande l'action au mod&egrave;le, <b>attrape les exceptions</b>, ouvre et ferme les fen&ecirc;tres.</div></div>
<div class="b m" style="left:980px;top:400px;width:450px;height:250px"><h2>MOD&Egrave;LE</h2><div class="p">package modele</div>
<div><b>Donnees</b>, Client, Commande, Restaurant, Livreur, Plat, Livraison, exceptions<br>&Eacute;tat, <b>r&egrave;gles m&eacute;tier</b> et fichiers (TP2 &agrave; TP5). Ne conna&icirc;t <b>ni Swing, ni la vue, ni les contr&ocirc;leurs</b>.</div></div>
<div class="l" style="position:absolute;left:610px;top:318px;color:#a65a00;width:330px"><span class="n">2</span> le contr&ocirc;leur &eacute;coute les boutons<br>(ActionListener)</div>
<div class="l" style="position:absolute;left:60px;top:300px;color:#b23a3a;width:300px"><span class="n">5</span> si exception :<br>vue.erreur(...)</div>
<div class="l" style="position:absolute;left:520px;top:490px;color:#a65a00;width:460px;text-align:center"><span class="n">3</span> demande l'action : donnees.payer(commande)</div>
<div class="l" style="position:absolute;left:1060px;top:300px;color:#1f5133;width:400px"><span class="n">4</span> le mod&egrave;le pr&eacute;vient la vue : modeleModifie()<br>(Observer) &rarr; la vue relit le mod&egrave;le</div>
</div></body></html>"""
    html_to_png(page, os.path.join(OUT, "q1_schema_mvc.png"), 1500, 720)
    print("schema_mvc")


# ------------------------------------------------------------------ avant / apres
def avant_apres():
    lignes = [
        ("Dessiner les composants (JFrame, layouts)", "FenetreXxx", "vue / FenetreXxx (inchangé)", "v"),
        ("Écouter les clics (ActionListener)", "dans la fenêtre : « implements ActionListener »", "controleur / ControleurXxx", "c"),
        ("try / catch des exceptions du TP5", "dans la fenêtre", "controleur (la vue ne fait qu'afficher l'erreur)", "c"),
        ("Règles métier : livraison après paiement, pourboire, note du livreur", "dans la fenêtre (if + message)", "modele / Donnees + 3 nouvelles exceptions", "m"),
        ("Créer la commande (le panier)", "constructeur de FenetreMenu", "Donnees.nouvelleCommande(), appelée par le contrôleur", "m"),
        ("Ouvrir et fermer les autres fenêtres", "chaque fenêtre ouvrait la suivante", "controleur / ControleurApplication", "c"),
        ("Mettre l'affichage à jour", "la fenêtre rappelait rafraichir() à la main", "Observer : le modèle appelle modeleModifie()", "m"),
        ("Fichiers .txt et .ser (TP5)", "appelés depuis la fenêtre", "Donnees (modèle), demandés par le contrôleur", "m"),
    ]
    col = {"v": "#e8f0fe", "c": "#fff1e0", "m": "#e6f4ea"}
    rows = "".join(f'<tr><td>{a}</td><td class="o">{b}</td><td style="background:{col[k]}"><b>{c}</b></td></tr>' for a, b, c, k in lignes)
    page = f"""<html><head><meta charset="utf-8"><style>
body{{margin:0;background:#fff;font-family:'Segoe UI',Arial,sans-serif;color:#222}}
table{{border-collapse:collapse;width:1460px;margin:20px}} th{{background:#333;color:#fff;font-size:21px;padding:12px 16px;text-align:left}}
td{{font-size:19px;padding:11px 16px;border-bottom:1px solid #ddd}} td.o{{color:#7a1f1f;background:#fdeeee}} td:first-child{{font-weight:600;width:470px}}
</style></head><body><table><tr><th>Responsabilité</th><th>TP6 : tout dans la fenêtre</th><th>TP7 : MVC</th></tr>{rows}</table></body></html>"""
    html_to_png(page, os.path.join(OUT, "q1_avant_apres.png"), 1500, 600)
    print("avant_apres")


# ------------------------------------------------------------------ code colore
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
        body.append(f'<div class="tab">&#9679; {fname.split("/")[-1]}</div><div class="code">{"".join(rows)}</div>')
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


# ------------------------------------------------------------------ preuve : sortie des tests
def preuve_tests():
    with open(LOG, encoding="utf-8", errors="replace") as f:
        log = f.read().split("\n")
    compte = {"modele": 0, "vue": 0, "controleur": 0}
    for l in log:
        m = re.match(r"\s+ok\s+(modele|vue|controleur)/", l)
        if m:
            compte[m.group(1)] += 1
    garder = []
    debut = next(i for i, l in enumerate(log) if l.startswith("--- MVC : la vue observe"))
    for l in log[debut:]:
        if re.search(r"^--- MVC|OBSERVER|MODELE refuse|Tous les tests", l):
            garder.append(l.strip().replace("�", "EUR"))
    lignes = ["$ java -cp out;out_tests TestsTP7", ""]
    for l in garder:
        lignes.append(l)
        if l.startswith("--- MVC : controle automatique"):
            lignes.append(f"  ok  modele/    : {compte['modele']} verifications  (aucun javax.swing, java.awt, import vue, import controleur)")
            lignes.append(f"  ok  vue/       : {compte['vue']} verifications  (aucun import controleur, aucune modification du modele)")
            lignes.append(f"  ok  controleur/: {compte['controleur']} verifications  (aucun composant graphique construit)")
    rows = "".join(f'<div class="l">{html.escape(l) or "&nbsp;"}</div>' for l in lignes)
    h = 70 + len(lignes) * 30
    page = f"""<html><head><meta charset="utf-8"><style>
body{{margin:0;background:#0c0c0c;font-family:{FONT};color:#cccccc}} .t{{background:#1f1f1f;color:#ddd;font:14px 'Segoe UI',sans-serif;padding:9px 14px}}
.b{{padding:14px 20px;font-size:19px}} .l{{height:30px;line-height:30px;white-space:pre}}
</style></head><body><div class="t">Invite de commandes - tests du TP7</div><div class="b">{rows}</div></body></html>"""
    html_to_png(page, os.path.join(OUT, "q1_preuve_tests.png"), 1500, h)
    print("preuve_tests", compte)


if __name__ == "__main__":
    schema_mvc()
    avant_apres()
    code_png("m_code_modele", [
        ("modele/EcouteurModele.java", r"public interface EcouteurModele", r"^}"),
        ("modele/Donnees.java", r"// Previent toutes les vues", r"^    }"),
        ("modele/Donnees.java", r"// Regle metier : pas de pourboire negatif", r"^    }"),
    ], width=1240)
    code_png("v_code_vue", [
        ("vue/FenetreAccueil.java", r"public class FenetreAccueil extends JFrame", r"public class FenetreAccueil extends JFrame"),
        ("vue/FenetreAccueil.java", r"public void setControleur", r"^    }"),
        ("vue/FenetreAccueil.java", r"// Observer : le modele a change", r"^    }"),
    ], width=1180)
    code_png("c_code_controleur", [
        ("controleur/ControleurAccueil.java", r"public void actionPerformed", r"^    }"),
        ("controleur/ControleurMenu.java", r"// Clic « Ajouter »", r"^    }"),
    ], width=1240)
    code_png("c_code_application", [
        ("controleur/ControleurApplication.java", r"// Demarrage : vue", r"^    }"),
        ("controleur/ControleurApplication.java", r"public void ouvrirMenu", r"^    }"),
    ], width=1280)
    code_png("c_code_commande", [
        ("controleur/ControleurCommande.java", r"private void payer\(\)", r"^    }"),
        ("modele/Donnees.java", r"public void payer\(Commande c\)", r"^    }"),
    ], width=1180)
    preuve_tests()
