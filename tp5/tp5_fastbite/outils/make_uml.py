# -*- coding: utf-8 -*-
"""Genere les 3 diagrammes UML du TP5 (SVG + .drawio) dans ../../rendu_tp5/uml/"""
import os, sys
sys.path.insert(0, os.path.dirname(__file__))
from uml_lib import Box, Diagram

OUT = os.path.abspath(os.path.join(os.path.dirname(__file__), "..", "..", "rendu_tp5", "uml"))
os.makedirs(OUT, exist_ok=True)


def stack(boxes, x, y0, gap):
    y = y0
    for b in boxes:
        b.x, b.y = x, y
        y += b.h + gap
    return y


# ------------------------------------------------------------------ Q1 : exceptions
def q1():
    d = Diagram("TP5 Q1 - Exceptions", 1300, 880)
    ev = d.add(Box("ev", "Evaluable", "interface", stereo="interface",
                   attrs=["+ NOTE_MIN : double = 0.0", "+ NOTE_MAX : double = 5.0"],
                   methods=["+ ajouterNote(note : double) : void", "+ getNoteMoyenne() : double"]))
    cl = d.add(Box("cl", "Client", methods=["+ passerCommande(r : Restaurant, n : int) : Commande",
                                           "+ payerCommande(c : Commande) : void",
                                           "+ noterRider(l : Livreur, note : double) : void"]))
    lv = d.add(Box("lv", "Livreur", methods=["+ accepterLivraison(l : Livraison) : void",
                                            "+ ajouterNote(note : double) : void",
                                            "+ getNoteMoyenne() : double"]))
    co = d.add(Box("co", "Commande", methods=["+ ajouterPlat(p : Plat) : void", "+ valider() : void"]))
    li = d.add(Box("li", "Livraison", methods=["+ demarrer() : void", "+ terminer() : void"]))
    re = d.add(Box("re", "Restaurant", methods=["+ gererMenu(p : Plat) : void",
                                               "+ chargerMenu(fichier : String) : void",
                                               "+ ajouterNote(note : double) : void"]))
    pa = d.add(Box("pa", "Panier", methods=["+ ajouterPlat(plat : Plat) : void"]))
    for b in (ev, cl, lv, co, pa, li, re):
        b.w = 410
    y = stack([ev, cl, lv, co], 30, 20, 16)
    stack([pa], 30, y + 20, 16)
    stack([li, re], 30, pa.y + pa.h + 16, 16)

    names = ["NoteInvalideException", "AucuneNoteException", "PaiementImpossibleException",
             "CapaciteDepasseeException", "LivreurIndisponibleException", "PlatIndisponibleException",
             "CommandeVideException", "EtatLivraisonException", "FormatFichierException"]
    exc = {}
    for n in names:
        exc[n] = d.add(Box(n, n, "exception", w=270))
    stack(list(exc.values()), 620, 40, 36)

    fb = d.add(Box("fb", "FastBiteException", "exception", w=210, x=1020, y=0))
    fb.y = (exc[names[0]].y + exc[names[-1]].y) / 2
    jx = d.add(Box("jx", "Exception", "externe", stereo="java.lang", w=210, x=1020, y=fb.y - 130))

    d.link("pa", "co", "gen", side_a="T", side_b="B")
    d.link("fb", "jx", "gen", side_a="T", side_b="B")
    for n in names:
        d.link(n, "fb", "gen", side_a="R", side_b="L")
    deps = [("ev", "NoteInvalideException"), ("ev", "AucuneNoteException"),
            ("cl", "CapaciteDepasseeException"), ("cl", "PaiementImpossibleException"), ("cl", "NoteInvalideException"),
            ("lv", "LivreurIndisponibleException"),
            ("co", "PlatIndisponibleException"), ("co", "CapaciteDepasseeException"), ("co", "CommandeVideException"),
            ("li", "EtatLivraisonException"),
            ("re", "CapaciteDepasseeException"), ("re", "FormatFichierException"),
            ("re", "NoteInvalideException"), ("pa", "PlatIndisponibleException")]
    for a, b in deps:
        d.link(a, b, "dep", side_a="R", side_b="L")

    note = d.add(Box("n1", "", note=True, x=930, y=580, text_lines=[
        "Verifiees (checked) :",
        " toutes les FastBiteException",
        " -> try/catch ou throws obligatoire",
        "Non verifiee (unchecked) :",
        " IllegalArgumentException",
        " (plat null : bug du code, TP4 inchange)"]))
    d.legend = [("dep", "dépendance «throws» : la méthode peut lever l'exception"),
                ("gen", "généralisation (héritage)")]
    return d


# ------------------------------------------------------------------ Q2 : fichier texte
def q2():
    d = Diagram("TP5 Q2 - Fichier texte", 1180, 720)
    io = d.add(Box("io", "IOException", "externe", stereo="java.io", w=240, x=40, y=20))
    re = d.add(Box("re", "Restaurant",
                   attrs=["- nom : String", "- menu : Plat[20]", "- nbPlats : int", "- noteMoyenne : double"],
                   methods=["+ sauvegarderMenu(fichier : String) : void",
                            "+ chargerMenu(fichier : String) : void",
                            "+ afficherMenu() : void"], x=40, y=150, w=470))
    pl = d.add(Box("pl", "Plat", attrs=["- nom : String", "- prix : double", "- description : String",
                                        "- allergenes : String[]"], x=780, y=170, w=290))
    ff = d.add(Box("ff", "FormatFichierException", "exception", w=300, x=40, y=440))
    cd = d.add(Box("cd", "CapaciteDepasseeException", "exception", w=300, x=520, y=440))
    fb = d.add(Box("fb", "FastBiteException", "exception", w=240, x=280, y=570))
    note = d.add(Box("nt", "", note=True, x=560, y=20, text_lines=[
        "Format du fichier menu_chezmario.txt",
        "1 ligne = 1 plat, 4 champs separes par ';'",
        "nom;prix;description;allergene1,allergene2",
        "prix : point decimal / allergenes : ',' (vide = aucun)"]))
    d.link("re", "io", "dep", label="«throws»")
    d.link("re", "ff", "dep", label="«throws»")
    d.link("re", "cd", "dep")
    d.link("re", "pl", "agg", mult_a="", mult_b="0..20")
    d.link("ff", "fb", "gen", side_a="B", side_b="T")
    d.link("cd", "fb", "gen", side_a="B", side_b="T")
    d.link("nt", "re", "note")
    d.legend = [("dep", "dépendance «throws»"), ("agg", "agrégation (le plat existe sans le restaurant)"),
                ("gen", "généralisation")]
    return d


# ------------------------------------------------------------------ Q3 : serialisation
def q3():
    d = Diagram("TP5 Q3 - Serialisation", 1260, 720)
    se = d.add(Box("se", "Serializable", "interface", stereo="interface java.io", w=240, x=480, y=20))
    al = d.add(Box("al", "ArrayList<Restaurant>", "externe", stereo="java.util", w=300, x=30, y=210))
    re = d.add(Box("re", "Restaurant",
                   attrs=["- {static final} serialVersionUID : long", "- nom : String", "- menu : Plat[20]",
                          "- noteMoyenne : double", "- nbCommandesRecues : int {transient}"],
                   methods=["+ gererMenu(p : Plat) : void",
                            ("+ sauvegarderRestaurants(liste, fichier) : void", "u"),
                            ("+ chargerRestaurants(fichier) : ArrayList<Restaurant>", "u")], x=420, y=210))
    pl = d.add(Box("pl", "Plat",
                   attrs=["- {static final} serialVersionUID : long", "- nom : String", "- prix : double",
                          "- allergenes : String[]", "- noteMoyenne : double"], x=900, y=210, w=340))
    d.add(Box("n1", "", note=True, x=30, y=500, text_lines=[
        "Client, Livreur, Commande, Panier, Livraison : NON Serializable",
        "(sessions et transactions, jamais enregistrees)",
        "-> writeObject(client) = NotSerializableException"]))
    d.add(Box("n2", "", note=True, x=640, y=520, text_lines=[
        "transient : champ ignore par la serialisation",
        "(vaut 0 apres readObject)"]))
    d.link("al", "se", "real")
    d.link("re", "se", "real")
    d.link("pl", "se", "real")
    d.link("al", "re", "agg", mult_b="0..*")
    d.link("re", "pl", "agg", mult_b="0..20")
    d.link("n2", "re", "note")
    d.legend = [("real", "réalisation (implements)"), ("agg", "agrégation"),
                ("", "méthodes soulignées = static")]
    return d


for name, fn in (("q1_exceptions", q1), ("q2_fichier", q2), ("q3_serialisation", q3)):
    dg = fn()
    with open(os.path.join(OUT, name + ".svg"), "w", encoding="utf-8") as f:
        f.write(dg.svg())
    with open(os.path.join(OUT, name + ".drawio"), "w", encoding="utf-8") as f:
        f.write(dg.drawio())
    print("ok", name, dg.width, dg.height)
