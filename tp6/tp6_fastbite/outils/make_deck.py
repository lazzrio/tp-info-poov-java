# -*- coding: utf-8 -*-
"""Construit la presentation TP6 (10 slides) avec python-pptx -> ../../rendu_tp6/"""
import os
from pptx import Presentation
from pptx.util import Inches, Pt
from pptx.dml.color import RGBColor
from pptx.enum.text import PP_ALIGN, MSO_ANCHOR
from pptx.enum.shapes import MSO_SHAPE
from PIL import Image

RENDU = os.path.abspath(os.path.join(os.path.dirname(__file__), "..", "..", "rendu_tp6"))
CAP = os.path.join(RENDU, "captures")
NOMS = "[Prénom NOM 1] · [Prénom NOM 2] · [Prénom NOM 3]"
OUTFILE = os.path.join(RENDU, "TP6_FastBite_NOM1_NOM2_NOM3.pptx")

NAVY = RGBColor(0x1F, 0x3A, 0x5F)
ORANGE = RGBColor(0xE8, 0x5D, 0x04)
GREY = RGBColor(0x55, 0x5B, 0x66)
LIGHT = RGBColor(0xF3, 0xF5, 0xF9)
WHITE = RGBColor(0xFF, 0xFF, 0xFF)
DARK = RGBColor(0x1B, 0x1F, 0x27)
PALE = RGBColor(0xC9, 0xD6, 0xEA)

prs = Presentation()
prs.slide_width = Inches(13.333)
prs.slide_height = Inches(7.5)
BLANK = prs.slide_layouts[6]


def rect(slide, x, y, w, h, fill):
    s = slide.shapes.add_shape(MSO_SHAPE.RECTANGLE, Inches(x), Inches(y), Inches(w), Inches(h))
    s.fill.solid()
    s.fill.fore_color.rgb = fill
    s.line.fill.background()
    s.shadow.inherit = False
    return s


def text(slide, x, y, w, h, paras, anchor=MSO_ANCHOR.TOP):
    """paras = [(texte, taille, gras, couleur, puce?)]"""
    tb = slide.shapes.add_textbox(Inches(x), Inches(y), Inches(w), Inches(h))
    tf = tb.text_frame
    tf.word_wrap = True
    tf.vertical_anchor = anchor
    tf.margin_left = tf.margin_right = Inches(0.05)
    tf.margin_top = tf.margin_bottom = Inches(0.03)
    first = True
    for p in paras:
        t, size, bold, color, bullet = (list(p) + [None] * 5)[:5]
        para = tf.paragraphs[0] if first else tf.add_paragraph()
        first = False
        run = para.add_run()
        run.text = ("• " + t) if bullet else t
        run.font.size = Pt(size or 14)
        run.font.bold = bool(bold)
        run.font.name = "Calibri"
        run.font.color.rgb = color or DARK
        para.space_after = Pt(5)
    return tb


def header(slide, title, n):
    rect(slide, 0, 0, 13.333, 0.95, NAVY)
    rect(slide, 0, 0.95, 13.333, 0.06, ORANGE)
    text(slide, 0.45, 0.12, 10.6, 0.75, [(title, 27, True, WHITE)], MSO_ANCHOR.MIDDLE)
    text(slide, 10.6, 0.12, 2.4, 0.75, [("FastBite · TP6", 14, False, PALE)],
         MSO_ANCHOR.MIDDLE).text_frame.paragraphs[0].alignment = PP_ALIGN.RIGHT
    text(slide, 0.45, 7.12, 9, 0.3, [("POO Java — Swing · Listeners · Exceptions · Fichiers", 10, False, GREY)])
    t = text(slide, 11.9, 7.12, 1.0, 0.3, [(str(n), 11, True, GREY)])
    t.text_frame.paragraphs[0].alignment = PP_ALIGN.RIGHT


def picture(slide, name, x, y, w, h, caption=None):
    """Image dans la boite (x,y,w,h), ratio conserve, legende dessous. Retourne le bas."""
    path = os.path.join(CAP, name)
    iw, ih = Image.open(path).size
    cap_h = 0.3 if caption else 0
    scale = min(w / iw, (h - cap_h) / ih)
    pw, ph = iw * scale, ih * scale
    px = x + (w - pw) / 2
    slide.shapes.add_picture(path, Inches(px), Inches(y), Inches(pw), Inches(ph))
    ln = slide.shapes[-1].line
    ln.color.rgb = RGBColor(0xC4, 0xCA, 0xD4)
    ln.width = Pt(0.75)
    if caption:
        t = text(slide, x, y + ph + 0.02, w, 0.3, [(caption, 10, False, GREY)])
        t.text_frame.paragraphs[0].runs[0].font.italic = True
        t.text_frame.paragraphs[0].alignment = PP_ALIGN.CENTER
    return y + ph + cap_h


def notes(slide, s):
    slide.notes_slide.notes_text_frame.text = s


# ====================================================================== 1. Couverture
s = prs.slides.add_slide(BLANK)
rect(s, 0, 0, 13.333, 7.5, NAVY)
rect(s, 0, 4.55, 13.333, 0.08, ORANGE)
text(s, 0.9, 1.5, 11.5, 0.8, [("FastBite", 54, True, WHITE)])
text(s, 0.9, 2.55, 11.5, 0.6, [("(notre application de livraison de repas, type Deliveroo)", 20, False, PALE)])
text(s, 0.9, 3.35, 11.8, 1.0, [("TP6 — Interface graphique Swing et programmation événementielle", 30, True, ORANGE)])
text(s, 0.9, 4.9, 11.5, 0.5, [(NOMS, 22, True, WHITE)])
text(s, 0.9, 5.5, 11.5, 1.0, [
    ("Programmation orientée objet en Java — ECE Paris, Ing2", 16, False, PALE),
    ("Enseignant : J.-P. Segado · octobre 2026", 16, False, PALE)])
notes(s, "Présentation (10 s). FastBite est notre application de livraison de repas, construite du TP2 au TP5. "
         "Ce TP6 lui donne une interface graphique Swing : trois fenêtres, des boutons avec listeners, "
         "et on réutilise les exceptions, le fichier texte et la sérialisation du TP5.")

# ====================================================================== 2. Sommaire
s = prs.slides.add_slide(BLANK)
header(s, "Sommaire", 2)
items = [("1", "Q1 — Maquette (wireframes)", "les 3 fenêtres dessinées avant de coder (1 slide)"),
         ("2", "Q2 — Storyboard", "la navigation entre les pages, les erreurs, les fichiers (1 slide)"),
         ("3", "Q3 — Fenêtres Swing", "code commenté et fenêtres obtenues (2 slides)"),
         ("4", "Q4 — Listeners, exceptions, fichiers", "clics, erreurs affichées, .txt et .ser (3 slides)"),
         ("5", "Bilan, sources et IA", "ce qui marche, limites, outils utilisés")]
for i, (n, t, d) in enumerate(items):
    y = 1.35 + i * 0.98
    c = s.shapes.add_shape(MSO_SHAPE.OVAL, Inches(0.9), Inches(y), Inches(0.7), Inches(0.7))
    c.fill.solid(); c.fill.fore_color.rgb = ORANGE; c.line.fill.background()
    c.text_frame.text = n
    c.text_frame.paragraphs[0].alignment = PP_ALIGN.CENTER
    c.text_frame.paragraphs[0].runs[0].font.size = Pt(24)
    c.text_frame.paragraphs[0].runs[0].font.bold = True
    c.text_frame.paragraphs[0].runs[0].font.color.rgb = WHITE
    text(s, 1.9, y - 0.04, 10, 0.5, [(t, 24, True, NAVY)])
    text(s, 1.9, y + 0.38, 10, 0.4, [(d, 15, False, GREY)])
rect(s, 0.9, 6.3, 11.5, 0.65, LIGHT)
text(s, 1.05, 6.33, 11.2, 0.6, [("Point de départ : le code du TP5 (classes métier, 9 exceptions, fichier texte, sérialisation). "
                                 "Le TP6 n'ajoute que des classes d'interface ; le métier n'est pas réécrit.", 14, False, DARK)], MSO_ANCHOR.MIDDLE)
notes(s, "Sommaire (10 s). Quatre questions puis un bilan. Nous sommes repartis du code du TP5 sans le réécrire : "
         "l'interface appelle les mêmes méthodes que la console appelait.")

# ====================================================================== 3. Q1 wireframes
s = prs.slides.add_slide(BLANK)
header(s, "Q1 — Maquette fil de fer (wireframes)", 3)
b = picture(s, "q1_wireframes.png", 0.3, 1.15, 12.75, 4.75, "Capture 1 — maquette des 3 fenêtres, générée par IA à partir de nos TP2 à TP5")
text(s, 0.5, b + 0.05, 12.3, 1.1, [
    ("Pourquoi 3 fenêtres ? Elles suivent le cas d'utilisation principal du TP2 : choisir un restaurant, composer sa commande, payer et noter.", 13.5, False, DARK, True),
    ("Chaque zone correspond à un composant Swing (JList, JTable, JTextField, JButton) ; les pastilles bleues sont les boutons qui auront un listener.", 13.5, False, DARK, True)])
notes(s, "Q1 (40 s). La maquette a été générée par IA à partir de notre thème. Elle montre trois fenêtres : l'accueil avec la liste des restaurants, "
         "le menu avec le panier, et la commande avec le paiement et la note. C'est le parcours du client de notre diagramme de cas d'utilisation du TP2. "
         "On a déjà noté quel composant Swing sera utilisé pour chaque zone, ce qui a guidé le code.")

# ====================================================================== 4. Q2 storyboard
s = prs.slides.add_slide(BLANK)
header(s, "Q2 — Storyboard : navigation entre les pages", 4)
picture(s, "q2_storyboard.png", 0.3, 1.15, 8.9, 5.85, "Capture 2 — storyboard généré par IA")
text(s, 9.35, 1.25, 3.75, 5.7, [
    ("Lecture", 16, True, NAVY),
    ("Bleu : un clic change de page (Voir le menu, Valider, Retour).", 13, False, DARK, True),
    ("Rouge : une exception est attrapée ; une boîte d'erreur s'ouvre et on reste sur la page.", 13, False, DARK, True),
    ("Vert : un fichier est écrit ou lu (.ser sur l'accueil, .txt sur le menu).", 13, False, DARK, True),
    ("Choix", 16, True, NAVY),
    ("Une seule fenêtre visible à la fois : l'accueil est caché, pas détruit, pour y revenir.", 13, False, DARK, True),
    ("Les fenêtres partagent un objet Donnees (restaurants, client).", 13, False, DARK, True)])
notes(s, "Q2 (40 s). Le storyboard montre comment on passe d'une page à l'autre. Les flèches bleues sont les clics qui changent de page. "
         "En rouge, les cas d'erreur : l'exception est attrapée, on affiche une boîte et on reste sur la page. En vert, les fichiers. "
         "Une seule fenêtre est visible à la fois, et toutes partagent le même objet Donnees.")

# ====================================================================== 5. Q3 code
s = prs.slides.add_slide(BLANK)
header(s, "Q3 — Construire une fenêtre Swing : le code", 5)
picture(s, "q3_code_fenetre.png", 0.3, 1.15, 7.3, 5.9, "Capture 3 — FenetreAccueil : composants, layouts, abonnement aux boutons")
b = picture(s, "q3_code_main.png", 7.8, 1.2, 5.25, 2.6, "Capture 4 — FastBiteGUI.main : lancement")
text(s, 7.8, b + 0.1, 5.25, 3.3, [
    ("Ce que fait ce code", 15, True, NAVY),
    ("La classe hérite de JFrame : c'est une fenêtre.", 12.5, False, DARK, True),
    ("BorderLayout : 5 zones (nord, centre, est, sud). GridLayout : cases égales pour la colonne de boutons.", 12.5, False, DARK, True),
    ("JScrollPane autour de la JList : barre de défilement si la liste est longue.", 12.5, False, DARK, True),
    ("addActionListener(this) : la fenêtre écoute ses boutons.", 12.5, False, DARK, True),
    ("invokeLater : Swing crée les fenêtres dans son propre fil d'exécution.", 12.5, False, DARK, True)])
notes(s, "Q3, code (50 s). Chaque page est une classe qui hérite de JFrame. Dans le constructeur on crée les composants, "
         "on les range avec des layouts : BorderLayout pour les grandes zones, GridLayout pour la colonne de boutons. "
         "Puis on abonne la fenêtre aux clics avec addActionListener. Le main crée les données et affiche l'accueil dans le fil de Swing avec invokeLater.")

# ====================================================================== 6. Q3 fenêtres
s = prs.slides.add_slide(BLANK)
header(s, "Q3 — Les 3 fenêtres obtenues", 6)
b1 = picture(s, "q3_fenetre_accueil.png", 0.3, 1.2, 5.2, 3.3, "Capture 5 — page 1 : accueil (JList + boutons)")
picture(s, "q3_fenetre_commande.png", 0.3, b1 + 0.1, 5.2, 7.0 - b1 - 0.1, "Capture 7 — page 3 : commande")
b2 = picture(s, "q3_fenetre_menu.png", 5.7, 1.2, 7.35, 3.6, "Capture 6 — page 2 : menu (JTable) et panier (JList)")
text(s, 5.8, b2 + 0.15, 7.2, 2.0, [
    ("Fidèle à la maquette : mêmes zones, mêmes boutons.", 13.5, False, DARK, True),
    ("JTable en lecture seule pour le menu : le tableau lit le vrai menu du restaurant (objets Plat du TP3).", 13.5, False, DARK, True),
    ("Apparence Nimbus : identique sur Windows, Mac et Linux.", 13.5, False, DARK, True)])
notes(s, "Q3, résultat (40 s). Les trois fenêtres obtenues suivent la maquette. Le tableau du menu est rempli à partir des vrais objets Plat "
         "du restaurant, ce n'est pas du texte en dur. Nous avons choisi l'apparence Nimbus pour avoir le même rendu sur tous les ordinateurs.")

# ====================================================================== 7. Q4 listeners
s = prs.slides.add_slide(BLANK)
header(s, "Q4 — Listeners : un clic met à jour l'affichage", 7)
picture(s, "q4_code_listener.png", 0.3, 1.15, 6.6, 5.9, "Capture 8 — actionPerformed + getSource, puis le traitement d'un clic")
b = picture(s, "q4_listener_panier.png", 7.1, 1.2, 5.95, 2.75, "Capture 9 — test : 2 clics « Ajouter au panier » → total 14.5 EUR")
b = picture(s, "q4_code_anonyme.png", 7.1, b + 0.08, 5.95, 1.75, "Capture 10 — autre écriture : classe anonyme (page 3)")
text(s, 7.1, b + 0.05, 5.95, 1.2, [
    ("Deux façons vues au cours 6 : la fenêtre implémente ActionListener (getSource dit quel bouton), ou une classe anonyme par bouton.", 12, False, DARK, True),
    ("Le listener modifie le modèle (commande.ajouterPlat) puis rafraîchit la vue.", 12, False, DARK, True)])
notes(s, "Q4, listeners (50 s). Quand on clique, Swing appelle actionPerformed. getSource nous dit quel bouton a été cliqué. "
         "Pour Ajouter au panier, on prend la ligne sélectionnée dans le tableau, on appelle ajouterPlat sur la commande, puis on rafraîchit la liste "
         "du panier et le total. À droite le test : deux clics, le total passe à 14,5 euros. Sur la page 3 nous avons utilisé l'autre écriture, "
         "une classe anonyme par bouton.")

# ====================================================================== 8. Q4 exceptions
s = prs.slides.add_slide(BLANK)
header(s, "Q4 — Les exceptions du TP5 dans l'interface", 8)
picture(s, "q4_code_exceptions.png", 0.3, 1.15, 6.9, 5.9, "Capture 11 — try / catch dans les listeners : le message de l'exception va dans une boîte")
y = 1.2
for img, cap in [("q4_exc_commande_vide.png", "Capture 12 — Valider avec un panier vide : CommandeVideException"),
                 ("q4_exc_note.png", "Capture 13 — note 7 : NoteInvalideException"),
                 ("q4_exc_paiement.png", "Capture 14 — 2e clic sur Payer : PaiementImpossibleException")]:
    y = picture(s, img, 7.4, y, 5.65, 1.45, cap) + 0.1
text(s, 7.4, y, 5.65, 1.3, [
    ("Les classes métier lèvent ou propagent (TP5) ; c'est le listener qui attrape et affiche.", 12.5, False, DARK, True),
    ("Après une erreur, la fenêtre reste utilisable et les données sont intactes.", 12.5, False, DARK, True),
    ("Nouveau cas : NumberFormatException si le champ ne contient pas un nombre.", 12.5, False, DARK, True)])
notes(s, "Q4, exceptions (50 s). Les méthodes du TP5 lèvent toujours leurs exceptions. Dans l'interface, c'est le listener qui fait le try/catch "
         "et qui affiche le message de l'exception dans une boîte de dialogue. Trois tests à droite : valider un panier vide, donner une note de 7, "
         "payer deux fois. Un nouveau cas apparaît avec l'interface : l'utilisateur peut taper du texte dans un champ numérique, on attrape NumberFormatException.")

# ====================================================================== 9. Q4 fichiers
s = prs.slides.add_slide(BLANK)
header(s, "Q4 — Fichier texte et sérialisation depuis l'interface", 9)
b = picture(s, "q4_code_fichiers.png", 0.3, 1.15, 6.5, 4.2, "Capture 15 — chargerMenu (.txt) et charger (.ser) : appel des méthodes du TP5 + catch")
b = picture(s, "q4_fichier_txt.png", 0.3, b + 0.05, 6.5, 0.95, "Capture 16 — data/menu_chez_mario.txt écrit par le bouton « Enregistrer »")
b2 = picture(s, "q4_ser_charge.png", 7.0, 1.2, 3.6, 2.45, "Capture 17 — après « Charger (.ser) »")
picture(s, "q4_fichier_ser.png", 10.7, 1.2, 2.4, 2.45, "Capture 18 — le .ser (binaire)")
b3 = picture(s, "q4_exc_fichier.png", 7.0, b2 + 0.12, 6.05, 1.25, "Capture 19 — fichier .txt mal formé : FormatFichierException")
b3 = picture(s, "q4_exc_ser.png", 7.0, b3 + 0.08, 6.05, 1.2, "Capture 20 — fichier .ser corrompu : IOException")
text(s, 7.0, b3 + 0.02, 6.05, 0.8, [
    ("Le menu est lu dans un restaurant neuf : si le fichier est faux, l'écran n'est pas modifié.", 11.5, False, DARK, True),
    ("Test : liste vidée puis « Charger » → 2 restaurants, menus et note 4.5 retrouvés.", 11.5, False, DARK, True)])
notes(s, "Q4, fichiers (50 s). Les boutons appellent directement les méthodes du TP5 : sauvegarderMenu et chargerMenu pour le fichier texte, "
         "sauvegarderRestaurants et chargerRestaurants pour la sérialisation. Test de la sérialisation : on sauvegarde, on vide la liste, on recharge, "
         "les deux restaurants reviennent avec leurs menus et la note. Deux cas d'erreur : un fichier texte avec un prix non numérique, et un fichier "
         ".ser corrompu. Ce dernier test nous a fait trouver un bug du TP5 : le fichier restait ouvert après l'erreur, nous l'avons corrigé.")

# ====================================================================== 10. Bilan + sources
s = prs.slides.add_slide(BLANK)
header(s, "Bilan, sources et IA", 10)
text(s, 0.4, 1.2, 6.3, 5.8, [
    ("Bilan", 22, True, NAVY),
    ("Fait : maquette, storyboard, 3 fenêtres Swing reliées, 13 boutons avec listener, exceptions affichées, fichier .txt et .ser depuis l'interface.", 15, False, DARK, True),
    ("Vérifié : TestsTP6 simule les clics (doClick) : 27 vérifications passent ; les tests du TP5 passent toujours.", 15, False, DARK, True),
    ("Appris : JFrame et layouts, ActionListener (this ou classe anonyme), séparer l'interface du métier, le fil d'exécution de Swing.", 15, False, DARK, True),
    ("Bug trouvé grâce aux tests : après la lecture d'un .ser corrompu, le fichier restait ouvert (TP5). Corrigé avec deux ressources dans le try.", 15, False, DARK, True),
    ("Limites : un seul client, pas d'écran de connexion ni de livreur ; les images de fenêtres sont des rendus du programme de test.", 15, False, DARK, True),
    ("Améliorations : séparer en Modèle-Vue-Contrôleur (TP7), ajouter la page de suivi de livraison.", 15, False, DARK, True)])
rect(s, 6.95, 1.2, 6.0, 5.8, LIGHT)
text(s, 7.1, 1.25, 5.75, 5.7, [
    ("Sources", 22, True, NAVY),
    ("J.-P. Segado, TP6 et cours 6 « Interface graphique Swing, AWT, Listeners » (ECE).", 14, False, DARK, True),
    ("Documentation Java SE (Oracle) : JFrame, ActionListener, ActionEvent, JOptionPane.", 14, False, DARK, True),
    ("Notre code du TP5 (exceptions, fichier, sérialisation).", 14, False, DARK, True),
    ("IA utilisée", 22, True, NAVY),
    ("Claude (Anthropic) : maquette, storyboard, code Swing, tests et slides générés par IA. "
     "Part d'aide de l'IA : à indiquer par l'équipe.", 14, False, DARK, True),
    ("Prompt utilisé :", 15, True, ORANGE),
    ("« fais le tp6 dans la suite du tp5 fastbite » (avec le sujet du TP6 et notre code du TP5 fournis à l'IA)", 13.5, False, GREY, True)])
notes(s, "Bilan (30 s). Les quatre questions sont traitées et vérifiées par un programme de test qui simule les clics. "
         "Ce que nous retenons : séparer l'interface du métier nous a permis de réutiliser le TP5 sans le modifier. "
         "Les tests ont même révélé un bug du TP5. Limites : un seul client et pas de page livreur. "
         "Sources : le cours et le sujet de M. Segado, la documentation Oracle. L'IA Claude a généré la maquette, le storyboard et le code ; "
         "nous devons savoir expliquer chaque partie.")

prs.save(OUTFILE)
print("OK", OUTFILE)
