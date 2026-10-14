# -*- coding: utf-8 -*-
"""Deck TP6 dans le style de la reference (fond creme, serif, majuscules, NN/10).
Ne touche pas au deck existant : ecrit TP7_FastBite_NOM1_NOM2_NOM3.pptx"""
import os, random, re
from pptx import Presentation
from pptx.util import Inches, Pt
from pptx.dml.color import RGBColor
from pptx.enum.text import PP_ALIGN, MSO_ANCHOR
from pptx.enum.shapes import MSO_SHAPE, MSO_CONNECTOR
from PIL import Image, ImageFilter

HERE = os.path.dirname(os.path.abspath(__file__))
RENDU = os.path.abspath(os.path.join(HERE, "..", "..", "rendu_tp7"))
CAP = os.path.join(RENDU, "captures")
OUT = os.path.join(RENDU, "TP7_FastBite_NOM1_NOM2_NOM3.pptx")
BG = os.path.join(HERE, "fond_creme.png")

OLIVE = RGBColor(0x5A, 0x5B, 0x48)
WHITE = RGBColor(0xFF, 0xFF, 0xFF)
FONT = "Times New Roman"
TOTAL = 10


def make_background():
    random.seed(7)
    w, h = 1920, 1080
    base = Image.new("RGB", (w, h), (251, 247, 237))
    noise = Image.effect_noise((w, h), 14).convert("L").filter(ImageFilter.GaussianBlur(1.2))
    tint = Image.merge("RGB", [noise.point(lambda v: 238 + (v - 128) // 9)] * 3)
    img = Image.blend(base, Image.merge("RGB", (
        noise.point(lambda v: 251 + (v - 128) // 10),
        noise.point(lambda v: 247 + (v - 128) // 10),
        noise.point(lambda v: 236 + (v - 128) // 9))), 0.9)
    img.save(BG)


make_background()
prs = Presentation()
prs.slide_width = Inches(13.333)
prs.slide_height = Inches(7.5)
BLANK = prs.slide_layouts[6]
old_notes = []


def add_runs(para, txt, size, bold=False, caps=True, color=OLIVE, spc=-30):
    """Majuscules sauf les segments entre `backticks` (noms de classes, extensions)."""
    parts = re.split(r"(`[^`]*`)", txt)
    for part in parts:
        if not part:
            continue
        code = part.startswith("`")
        t = part.strip("`") if code else (part.upper() if caps else part)
        r = para.add_run()
        r.text = t
        r.font.name = FONT
        r.font.size = Pt(size)
        r.font.bold = bold
        r.font.color.rgb = color
        r._r.get_or_add_rPr().set("spc", str(spc))


def text(slide, x, y, w, h, paras, size=24, align=PP_ALIGN.CENTER, caps=True, anchor=MSO_ANCHOR.TOP,
         bold=False, spacing=6, line=1.0, spc=-30):
    tb = slide.shapes.add_textbox(Inches(x), Inches(y), Inches(w), Inches(h))
    tf = tb.text_frame
    tf.word_wrap = True
    tf.vertical_anchor = anchor
    tf.margin_left = tf.margin_right = Inches(0.04)
    tf.margin_top = tf.margin_bottom = Inches(0.02)
    if isinstance(paras, str):
        paras = [paras]
    for i, p in enumerate(paras):
        para = tf.paragraphs[0] if i == 0 else tf.add_paragraph()
        para.alignment = align
        para.space_after = Pt(spacing)
        para.line_spacing = line
        add_runs(para, p, size, bold, caps, spc=spc)
    return tb


def picture(slide, name, x, y, w, h, rounded=True):
    """Image centree dans la boite, ratio conserve ; renvoie (bas, haut_reel)."""
    path = os.path.join(CAP, name)
    iw, ih = Image.open(path).size
    sc = min(w / iw, h / ih)
    pw, ph = iw * sc, ih * sc
    px, py = x + (w - pw) / 2, y + (h - ph) / 2
    pic = slide.shapes.add_picture(path, Inches(px), Inches(py), Inches(pw), Inches(ph))
    if rounded:
        pic.auto_shape_type = MSO_SHAPE.ROUNDED_RECTANGLE
        av = pic._element.spPr.find(".//{http://schemas.openxmlformats.org/drawingml/2006/main}avLst")
        gd = av.makeelement("{http://schemas.openxmlformats.org/drawingml/2006/main}gd", {"name": "adj", "fmla": "val 3500"})
        av.append(gd)
    return py + ph


def new_slide(n, title=None, title_size=38):
    s = prs.slides.add_slide(BLANK)
    s.shapes.add_picture(BG, 0, 0, prs.slide_width, prs.slide_height)
    if title:
        text(s, 0.4, 0.12, 12.5, 0.95, title, title_size, anchor=MSO_ANCHOR.MIDDLE)
    if n > 1:
        text(s, 11.3, 6.88, 1.75, 0.5, f"{n:02d}/{TOTAL}", 24, align=PP_ALIGN.RIGHT, caps=False)
    if n - 1 < len(old_notes):
        s.notes_slide.notes_text_frame.text = old_notes[n - 1]
    return s


def arrow(slide, x1, x2, y):
    c = slide.shapes.add_connector(MSO_CONNECTOR.STRAIGHT, Inches(x1), Inches(y), Inches(x2), Inches(y))
    c.line.color.rgb = RGBColor(0x1A, 0x1A, 0x1A)
    c.line.width = Pt(1.6)
    ln = c.line._get_or_add_ln()
    tail = ln.makeelement("{http://schemas.openxmlformats.org/drawingml/2006/main}tailEnd", {"type": "arrow", "w": "lg", "len": "lg"})
    ln.append(tail)



NOTES = [
 "Presentation (10 s). FastBite, notre application de livraison de repas des TP2 a TP6. Ce TP7 ne change rien pour l'utilisateur : on reorganise le code selon le pattern MVC, Modele-Vue-Controleur.",
 "Sommaire (10 s). D'abord le principe et ce qui change entre le TP6 et le TP7, puis le code du modele, de la vue et du controleur, enfin les preuves par les tests et le bilan.",
 "Q1 (50 s). Trois roles. Le modele : les classes metier des TP2 a TP5, les regles et les fichiers ; il ne connait ni Swing ni la vue. La vue : nos fenetres du TP6, elle affiche et ne modifie jamais le modele. Le controleur : il ecoute les clics, demande l'action au modele, attrape les exceptions. Quand le modele change, il previent la vue : c'est le pattern Observer.",
 "Avant / apres (40 s). Au TP6 chaque fenetre faisait tout : dessiner, ecouter les clics, attraper les exceptions, appliquer les regles, ouvrir la fenetre suivante. Maintenant chaque responsabilite est dans le bon package. Les regles metier ont migre dans le modele, avec trois nouvelles exceptions, et la fenetre ne rappelle plus rafraichir a la main.",
 "Le modele (50 s). Donnees est le point d'entree : il garde l'etat et offre les actions, par exemple appliquerPourboire qui verifie la regle. Apres chaque modification il appelle notifierEcouteurs. Il ne connait que l'interface EcouteurModele, pas les fenetres, et n'importe pas Swing.",
 "La vue (40 s). FenetreAccueil lit Donnees pour s'afficher, jamais pour le modifier. Le controleur lui est fourni par setControleur et s'abonne a ses boutons, y compris ceux recrees a chaque rafraichissement. Quand le modele la previent, modeleModifie la redessine.",
 "Le controleur (50 s). C'est lui qui implemente ActionListener, plus la fenetre. getSource dit quel bouton a ete clique. Il appelle le modele, par exemple donnees.payer, puis demande a la vue d'afficher le resultat ; si le modele leve une exception, il appelle vue.erreur. A droite, le chemin complet pour payer : controleur puis modele.",
 "Assemblage (40 s). main cree le modele et le ControleurApplication. demarrer cree la vue, l'abonne au modele et branche son controleur. Pour ouvrir le menu, le modele cree la commande, le controleur cree la fenetre. Plus aucune fenetre n'en ouvre une autre.",
 "Preuves (50 s). Le programme de test lit nos fichiers source : aucun import de Swing ni de la vue dans le modele, aucune modification du modele depuis la vue, aucun composant construit dans le controleur. 273 verifications passent, y compris les scenarios de clics du TP6 : le comportement n'a pas change.",
 "Bilan (30 s). Les regles sont maintenant testables sans fenetre et on pourrait changer d'interface sans toucher au modele. Limites : le modele garde des println des TP3 et le modele notifie sans preciser ce qui a change. Sources : cours de M. Segado, OpenClassrooms, Wikipedia. L'IA Claude a refactorise le code et ecrit les tests : nous devons savoir expliquer chaque partie.",
]
old_notes[:] = NOTES

# 1 ============================================================== couverture
s = new_slide(1)
text(s, 0.4, 0.05, 12.5, 1.5, "FastBite", 96, anchor=MSO_ANCHOR.MIDDLE, spc=-60)
text(s, 0.9, 1.5, 11.5, 1.0, ["TP7 — Pattern MVC (Modèle-Vue-Contrôleur)"], 32)
card = s.shapes.add_shape(MSO_SHAPE.ROUNDED_RECTANGLE, Inches(3.15), Inches(2.6), Inches(7.05), Inches(3.35))
card.fill.solid(); card.fill.fore_color.rgb = WHITE
card.line.fill.background(); card.shadow.inherit = False
card.adjustments[0] = 0.04
picture(s, "q1_schema_mvc.png", 3.3, 2.7, 6.75, 3.15, rounded=False)
text(s, 0.15, 6.0, 4.9, 1.4, ["[Prénom NOM 1]", "[Prénom NOM 2]", "[Prénom NOM 3]"], 22, spacing=0)
text(s, 4.6, 6.55, 4.2, 0.6, "ECE Paris · Ing2 · J.-P. Segado", 18)
text(s, 8.9, 6.7, 4.2, 0.6, "POO-JAVA_2026", 32, align=PP_ALIGN.RIGHT, caps=False)

# 2 ============================================================== sommaire
s = new_slide(2, "Sommaire", 46)
items = ["Q1 — Le pattern MVC dans FastBite", "Avant / après : du TP6 au TP7", "Modèle, vue, contrôleur : le code",
         "Preuves : tests automatiques", "Bilan, sources et usage de l'IA"]
y = 1.5
for it in items:
    text(s, 0.75, y, 7.4, 1.0, "• " + it, 27, align=PP_ALIGN.LEFT, anchor=MSO_ANCHOR.MIDDLE)
    y += 1.02
picture(s, "q3_fenetre_accueil.png", 8.1, 2.45, 4.9, 3.4)

# 3 ============================================================== Q1 schema
s = new_slide(3, "Q1 — MVC : qui fait quoi ?")
picture(s, "q1_schema_mvc.png", 0.5, 1.15, 12.3, 4.45, rounded=False)
text(s, 0.6, 5.65, 12.1, 1.3, [
    "Modèle : données, règles métier et fichiers (TP2 à TP5) — ne connaît ni Swing ni la vue",
    "Vue : fenêtres Swing (TP6), lecture seule du modèle — Contrôleur : clics, appels au modèle, exceptions"],
    20, spacing=3)

# 4 ============================================================== avant / apres
s = new_slide(4, "Avant / après : du TP6 au TP7")
picture(s, "q1_avant_apres.png", 0.5, 1.2, 12.3, 4.9, rounded=False)
text(s, 0.6, 6.15, 10.4, 0.7, "Même interface, même comportement : le code est simplement rangé là où il doit vivre", 20)

# 5 ============================================================== modele
s = new_slide(5, "Le modèle : état, règles, Observer")
picture(s, "m_code_modele.png", 0.3, 1.25, 7.0, 5.55)
text(s, 7.55, 1.3, 5.5, 5.5, [
    "1. `Donnees` est le point d'entrée du modèle : état (restaurants, client, livreur) et actions métier.",
    "2. `EcouteurModele` est la seule chose que le modèle connaît des vues : il ne les importe pas.",
    "3. `notifierEcouteurs()` : après chaque modification, toutes les vues abonnées sont prévenues.",
    "4. La règle « pas de pourboire négatif ni sur une commande payée » est ici, levée en exception (elle était dans la fenêtre au TP6).",
    "5. Aucun import de `javax.swing` dans le package `modele`."],
    14.5, align=PP_ALIGN.LEFT, caps=False, spacing=6, spc=0)

# 6 ============================================================== vue
s = new_slide(6, "La vue : elle affiche, c'est tout")
picture(s, "v_code_vue.png", 0.3, 1.25, 6.7, 3.2)
picture(s, "q3_fenetre_accueil.png", 0.6, 4.55, 4.1, 2.3)
text(s, 4.8, 4.75, 2.3, 2.0, ["Page 1 :", "même fenêtre qu'au TP6"], 16, spacing=2, caps=False, spc=0)
text(s, 7.25, 1.3, 5.8, 5.5, [
    "1. `FenetreAccueil` implémente `EcouteurModele` : le modèle la prévient, elle se redessine (`modeleModifie`).",
    "2. Elle lit `Donnees` pour s'afficher mais ne le modifie jamais.",
    "3. `setControleur(...)` : le contrôleur lui est fourni et se branche sur ses boutons, y compris ceux recréés à chaque rafraîchissement.",
    "4. Plus de `implements ActionListener`, plus de `try / catch` métier, plus d'ouverture d'autres fenêtres : elle expose seulement ses boutons et `erreur(...)`."],
    14.5, align=PP_ALIGN.LEFT, caps=False, spacing=6, spc=0)

# 7 ============================================================== controleur
s = new_slide(7, "Le contrôleur : clics, modèle, erreurs")
picture(s, "c_code_controleur.png", 0.3, 1.25, 6.7, 5.55)
picture(s, "c_code_commande.png", 7.2, 1.3, 5.85, 2.55)
text(s, 7.2, 3.95, 5.85, 2.9, [
    "1. C'est le contrôleur qui implémente `ActionListener` (plus la fenêtre) ; `getSource()` dit quel bouton.",
    "2. Il demande l'action au modèle (`donnees.ajouterPlat`, `donnees.payer`) puis `vue.setEtat(...)`.",
    "3. Si le modèle refuse, il attrape l'exception et appelle `vue.erreur(...)`.",
    "4. `ControleurCommande` garde les classes anonymes du TP6."],
    14, align=PP_ALIGN.LEFT, caps=False, spacing=5, spc=0)

# 8 ============================================================== assemblage
s = new_slide(8, "L'assemblage et la navigation")
picture(s, "c_code_application.png", 0.3, 1.25, 7.2, 5.55)
picture(s, "q3_fenetre_menu.png", 7.9, 1.3, 5.1, 2.6)
text(s, 7.75, 4.05, 5.35, 2.8, [
    "1. `FastBiteGUI.main` crée le modèle puis le `ControleurApplication`.",
    "2. `demarrer()` crée la vue, l'abonne au modèle et branche son contrôleur.",
    "3. `ouvrirMenu()` : le modèle crée la commande, le contrôleur crée la fenêtre.",
    "4. Plus aucune fenêtre n'en ouvre une autre : toute la navigation est ici."],
    14.5, align=PP_ALIGN.LEFT, caps=False, spacing=5, spc=0)

# 9 ============================================================== preuves
s = new_slide(9, "Preuves : le MVC est respecté")
picture(s, "q1_preuve_tests.png", 0.5, 1.2, 12.3, 4.15, rounded=True)
text(s, 0.6, 5.45, 10.6, 1.4, [
    "Le test lit nos fichiers source : le modèle n'importe ni Swing ni la vue, la vue ne modifie pas le modèle, le contrôleur ne construit aucun composant",
    "Les scénarios de clics du TP6 passent toujours : le comportement n'a pas changé"], 17, spacing=3)

# 10 ============================================================= bilan + sources
s = new_slide(10, "Bilan, sources et usage de l'IA", 36)
text(s, 0.4, 1.15, 6.2, 0.6, "Bilan", 30, align=PP_ALIGN.LEFT)
text(s, 0.4, 1.8, 6.2, 5.0, [
    "• Fait : modèle, vue et contrôleurs séparés en 3 packages, Observer, navigation centralisée",
    "• Vérifié : `TestsTP7`, 273 vérifications, dont le contrôle des imports",
    "• Appris : qui décide, qui affiche, qui retient les règles ; Observer",
    "• Limites : le modèle garde des `println` des TP3 ; il prévient les vues sans dire ce qui a changé ; `Style` attrape `AucuneNoteException` pour afficher « Nouveau »",
    "• Améliorations : retirer les `println` du modèle, événements plus précis, tests du modèle sans fenêtre"],
    17, align=PP_ALIGN.LEFT, spacing=9)
text(s, 6.85, 1.15, 6.2, 0.6, "Sources et IA", 30)
rows = [("Cours de M. Segado", "TP7 et cours 4 à 6 : interfaces, Swing"),
        ("OpenClassrooms, Wikipédia", "Le pattern MVC"),
        ("Claude IA", "Refonte du code, tests, rapport"),
        ("IntelliJ", "Compilation et exécution")]
y = 1.95
for lab, what in rows:
    text(s, 6.85, y, 2.75, 0.8, lab, 18, align=PP_ALIGN.LEFT, anchor=MSO_ANCHOR.MIDDLE)
    arrow(s, 9.55, 10.15, y + 0.4)
    text(s, 10.2, y, 2.95, 0.8, what, 15, anchor=MSO_ANCHOR.MIDDLE)
    y += 0.92
text(s, 6.85, 5.7, 6.2, 1.1, ["Part d'aide de l'IA : [ XX % ] (à compléter)",
                             "Prompt : « fais le tp7 (pattern MVC) à partir du tp6 »"], 15, spacing=3)

prs.save(OUT)
print("OK", OUT)
