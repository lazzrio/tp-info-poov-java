# -*- coding: utf-8 -*-
"""Genere les 3 diagrammes UML du TP5 EN PARTANT de l'ancien diagramme de l'equipe
(semaine 4/TP4_Deliveroo_Q2_corrige.drawio) : meme mise en page, memes couleurs, memes
associations. On y ajoute uniquement ce que le TP5 change.
Sortie : rendu_tp5/uml/TP5_Q1_exceptions.drawio, TP5_Q2_fichier.drawio, TP5_Q3_serialisation.drawio (+ PNG)"""
import copy, math, os, sys
import xml.etree.ElementTree as ET

HERE = os.path.dirname(os.path.abspath(__file__))
BASE = os.path.abspath(os.path.join(HERE, "..", "..", "..", "semaine 4", "TP4_Deliveroo_Q2_corrige.drawio"))
OUT = os.path.abspath(os.path.join(HERE, "..", "..", "rendu_tp5", "uml"))
os.makedirs(OUT, exist_ok=True)

NB = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"   # indentation des lignes "throws"
LINE = 15.5                              # hauteur allouee par ligne de texte
WRAP = 44                                # nb de caracteres avant retour a la ligne dans une boite de 290 px


def thr(*names):
    return NB + '<font color="#B00020"><i>throws ' + ", ".join(names) + "</i></font>"


def strip(h):
    import re
    return re.sub(r"<[^>]+>|&nbsp;", lambda m: " " if m.group(0) == "&nbsp;" else "", h).replace("&lt;", "<").replace("&gt;", ">")


def nlines(lines):
    n = 0
    for l in lines:
        n += max(1, math.ceil(len(strip(l)) / WRAP))
    return n


class Model:
    def __init__(self, title):
        self.tree = ET.parse(BASE)
        self.root = self.tree.getroot()
        self.graph = self.root.find("diagram").find("mxGraphModel")
        self.r = self.graph.find("root")
        self.cells = {c.get("id"): c for c in self.r.iter("mxCell")}
        self.cells["titre"].set("value", title)
        self.n = 0

    # ---------------------------------------------------------------- geometrie
    def geo(self, cid):
        return self.cells[cid].find("mxGeometry")

    def setg(self, cid, **kw):
        g = self.geo(cid)
        for k, v in kw.items():
            g.set(k, str(int(v)) if isinstance(v, (int, float)) else v)

    def getg(self, cid, k):
        return float(self.geo(cid).get(k))

    # ---------------------------------------------------------------- boites
    def members(self, key, attrs, methods, start=None):
        """Remplace les attributs / methodes d'une boite existante et recalcule les hauteurs."""
        box = self.cells[key]
        st = start if start is not None else int(float(self.getg(key, "height")) and self._start(key))
        a_h = max(1, nlines(attrs)) * LINE + 10
        m_h = nlines(methods) * LINE + 12
        self.cells[key + "_a"].set("value", "<br>".join(attrs))
        self.cells[key + "_m"].set("value", "<br>".join(methods))
        self.setg(key + "_a", y=st, height=a_h)
        self.setg(key + "_l", y=st + a_h, height=8)
        self.setg(key + "_m", y=st + a_h + 8, height=m_h)
        self.setg(key, height=st + a_h + 8 + m_h)
        return st + a_h + 8 + m_h

    def _start(self, key):
        import re
        return int(re.search(r"startSize=(\d+)", self.cells[key].get("style")).group(1))

    def new_id(self, p="n"):
        self.n += 1
        return f"{p}{self.n}"

    def add_cell(self, cid, value, style, x, y, w, h, parent="1", vertex=True):
        c = ET.SubElement(self.r, "mxCell", {"id": cid, "value": value, "style": style,
                                             ("vertex" if vertex else "edge"): "1", "parent": parent})
        ET.SubElement(c, "mxGeometry", {"x": str(x), "y": str(y), "width": str(w), "height": str(h), "as": "geometry"})
        self.cells[cid] = c
        return c

    def add_class(self, key, title, x, y, w, fill, stroke, attrs, methods, start=30, plain_fill=None):
        sw = ("swimlane;fontStyle=0;align=center;verticalAlign=top;childLayout=stackLayout;horizontal=1;"
              f"startSize={start};horizontalStack=0;resizeParent=1;resizeParentMax=0;resizeLast=0;collapsible=0;"
              f"marginBottom=0;whiteSpace=wrap;html=1;fillColor={fill};strokeColor={stroke};")
        txt = self.cells["Client_a"].get("style")
        line = self.cells["Client_l"].get("style")
        self.add_cell(key, title, sw, x, y, w, 100)
        for suffix, st, v in (("_a", txt, ""), ("_l", line, ""), ("_m", txt, "")):
            c = ET.SubElement(self.r, "mxCell", {"id": key + suffix, "value": v, "style": st, "vertex": "1", "parent": key})
            ET.SubElement(c, "mxGeometry", {"y": "0", "width": str(w), "height": "10", "as": "geometry"})
            self.cells[key + suffix] = c
        a_h = max(1, nlines(attrs)) * LINE + 10 if attrs else 6
        m_h = nlines(methods) * LINE + 12 if methods else 6
        self.cells[key + "_a"].set("value", "<br>".join(attrs))
        self.cells[key + "_m"].set("value", "<br>".join(methods))
        self.setg(key + "_a", y=start, height=a_h)
        self.setg(key + "_l", y=start + a_h, height=8)
        self.setg(key + "_m", y=start + a_h + 8, height=m_h)
        for suffix in ("_a", "_m", "_l"):
            self.geo(key + suffix).set("width", str(w))
        self.setg(key, height=start + a_h + 8 + m_h)
        return key

    def add_note(self, key, html, x, y, w, h, fill="#fff2cc", stroke="#d6b656"):
        st = f"shape=note;whiteSpace=wrap;html=1;align=left;verticalAlign=top;spacingLeft=6;spacingTop=4;fontSize=11;fillColor={fill};strokeColor={stroke};"
        self.add_cell(key, html, st, x, y, w, h)

    def add_edge(self, eid, src, dst, style, points=None, label=None):
        c = ET.SubElement(self.r, "mxCell", {"id": eid, "value": "", "style": style, "edge": "1", "parent": "1",
                                             "source": src, "target": dst})
        g = ET.SubElement(c, "mxGeometry", {"relative": "1", "as": "geometry"})
        if points:
            arr = ET.SubElement(g, "Array", {"as": "points"})
            for (px, py) in points:
                ET.SubElement(arr, "mxPoint", {"x": str(px), "y": str(py)})
        self.cells[eid] = c

    def save(self, name, page_w, page_h):
        self.graph.set("pageWidth", str(page_w))
        self.graph.set("pageHeight", str(page_h))
        self.graph.set("dx", str(page_w))
        self.graph.set("dy", str(page_h))
        path = os.path.join(OUT, name + ".drawio")
        self.tree.write(path, encoding="utf-8", xml_declaration=False)
        return path


GEN = "endArrow=block;endFill=0;endSize=14;html=1;"
REAL = "endArrow=block;endFill=0;endSize=14;dashed=1;html=1;"
ORANGE = ("#ffe6cc", "#d79b00")
GREYC = ("#f5f5f5", "#666666")


def exception_cluster(m, names, x0=1470, y0=90, w=255, step=44, fb_x=1790, ext=None):
    """Colonne d'exceptions + FastBiteException + java.lang.Exception. Retourne le bas de la colonne."""
    ys = [y0 + i * step for i in range(len(names))]
    fb_y = (ys[0] + ys[-1]) // 2
    m.add_class("Exception", "«java.lang»<br><b>Exception</b>", fb_x, fb_y - 120, 200, GREYC[0], GREYC[1], [], [], start=46)
    m.cells["Exception"].set("style", m.cells["Exception"].get("style"))
    m.add_class("FastBiteException", "<b>FastBiteException</b>", fb_x, fb_y, 200, *ORANGE[:2], [], [], start=30)
    for n, y in zip(names, ys):
        m.add_class(n, f"<b>{n}</b>", x0, y, w, ORANGE[0], ORANGE[1], [], [], start=30)
        m.add_edge("g_" + n, n, "FastBiteException", GEN + "exitX=1;exitY=0.5;entryX=0;entryY=0.5;edgeStyle=none;")
    m.add_edge("g_fb", "FastBiteException", "Exception", GEN + "exitX=0.5;exitY=0;entryX=0.5;entryY=1;")
    for n in names + ["FastBiteException"]:
        shrink_empty(m, n, 30)
    shrink_empty(m, "Exception", 46)
    return ys[-1] + 60


def shrink_empty(m, key, h=30):
    """Boite sans membres : on supprime les cases vides et on garde juste l'en-tete."""
    for s in ("_a", "_l", "_m"):
        c = m.cells.pop(key + s, None)
        if c is not None:
            m.r.remove(c)
    m.setg(key, height=h)


def build(variant):
    titles = {"q1": "Diagramme de classes - Deliveroo (TP5 Q1 : exceptions, base TP4 Q2)",
              "q2": "Diagramme de classes - Deliveroo (TP5 Q2 : fichier texte, base TP4 Q2)",
              "q3": "Diagramme de classes - Deliveroo (TP5 Q3 : sérialisation, base TP4 Q2)"}
    m = Model(titles[variant])
    T = variant == "q1"
    F = variant == "q2"
    S = variant == "q3"

    # ------------------------------------------------ mise a niveau avec le vrai code (attributs/signatures)
    def t(*names):
        return [thr(*names)] if T else []

    m.members("Evaluable",
              ["+ NOTE_MIN : double = 0.0", "+ NOTE_MAX : double = 5.0"] if T else [],
              ["+ ajouterNote(note : double) : void"] + t("NoteInvalideException")
              + ["+ getNoteMoyenne() : double"] + t("AucuneNoteException"), start=46)
    m.members("Utilisateur", ["# nom : String", "# email : String", "# motDePasse : String"],
              ["+ Utilisateur(nom : String, email : String, motDePasse : String)", "+ seConnecter() : boolean",
               "+ seDeconnecter() : void", "+ modifierProfil() : void", "<i>+ afficherProfil() : void</i>"])
    m.members("Client", ["- adresseLivraison : String", "- historique : Commande[20]", "- abonnementPlus : boolean",
                         "- paniersSauvegardes : List&lt;Panier&gt;"],
              ["+ Client(nom : String, email : String, motDePasse : String, adresseLivraison : String)",
               "+ passerCommande(r : Restaurant, numero : int) : Commande"] + t("CapaciteDepasseeException")
              + ["+ payerCommande(c : Commande) : void"] + t("PaiementImpossibleException")
              + ["+ noterRider(l : Livreur, note : double) : void"] + t("NoteInvalideException")
              + ["+ sauvegarderPanier(p : Panier) : void", "+ afficherProfil() : void"])
    m.members("Livreur", ["- vehicule : String", "- disponible : boolean", "- positionGPS : String",
                          "- noteMoyenne : double", "- nbNotes : int", "- totalNotes : double"],
              ["+ Livreur(nom : String, email : String, motDePasse : String, vehicule : String)",
               "+ accepterLivraison(l : Livraison) : void"] + t("LivreurIndisponibleException")
              + ["+ seGeolocaliser() : String", "+ confirmerLivraison() : void",
                 "+ recevoirNote(note : double) : void"] + t("NoteInvalideException")
              + ["+ ajouterNote(note : double) : void"] + t("NoteInvalideException")
              + ["+ getNoteMoyenne() : double"] + t("AucuneNoteException") + ["+ afficherProfil() : void"])
    plat_attrs = ["- nom : String", "- prix : double", "- description : String", "- allergenes : String[]",
                  "- noteMoyenne : double", "- nbNotes : int", "- totalNotes : double"]
    if S:
        plat_attrs.insert(0, "- {static final} serialVersionUID : long")
    m.members("Plat", plat_attrs,
              ["+ Plat(nom : String, prix : double, description : String, allergenes : String[])",
               "+ getPrix() : double", "+ getDescription() : String", "+ afficher() : void",
               "+ ajouterNote(note : double) : void"] + t("NoteInvalideException")
              + ["+ getNoteMoyenne() : double"] + t("AucuneNoteException"))
    rest_attrs = ["- nom : String", "- adresse : String", "- menu : Plat[20]", "- nbPlats : int",
                  "- zoneLivraison : String", "- noteMoyenne : double", "- nbNotes : int", "- totalNotes : double"]
    rest_m = ["+ Restaurant(nom : String, adresse : String, zoneLivraison : String)",
              "+ gererMenu(p : Plat) : void"] + t("CapaciteDepasseeException") \
             + ["+ proposePlat(p : Plat) : boolean", "+ recevoirCommande(c : Commande) : void",
                "+ preparerCommande(c : Commande) : void", "+ ajouterNote(note : double) : void"] + t("NoteInvalideException") \
             + ["+ getNoteMoyenne() : double"] + t("AucuneNoteException")
    if F:
        rest_m += ["<b>+ sauvegarderMenu(fichier : String) : void</b>", thr("IOException"),
                   "<b>+ chargerMenu(fichier : String) : void</b>",
                   NB + '<font color="#B00020"><i>throws IOException, FormatFichierException,</i></font>',
                   NB + '<font color="#B00020"><i>CapaciteDepasseeException</i></font>',
                   "<b>+ afficherMenu() : void</b>"]
        rest_m[1:1] = []
    if S:
        rest_attrs.insert(0, "- {static final} serialVersionUID : long")
        rest_attrs.append("<b>- nbCommandesRecues : int {transient}</b>")
        rest_m += ["<u>+ sauvegarderRestaurants(liste : ArrayList&lt;Restaurant&gt;, fichier : String) : void</u>",
                   "<u>+ chargerRestaurants(fichier : String) : ArrayList&lt;Restaurant&gt;</u>"]
    m.members("Restaurant", rest_attrs, rest_m)
    m.members("Commande", ["- numero : int", "- statut : String", "- montantTotal : double", "- pourboire : double",
                           "# plats : List&lt;Plat&gt;"],
              ["+ Commande(numero : int, client : Client, restaurant : Restaurant)",
               "+ ajouterPlat(p : Plat) : void"] + t("PlatIndisponibleException")
              + ["+ calculerTotal() : double", "+ valider() : void"] + t("CommandeVideException"))
    m.members("Livraison", ["- dateDepart : String", "- dateArrivee : String", "- statut : String", "- tempsEstime : int"],
              ["+ Livraison(commande : Commande, livreur : Livreur, tempsEstime : int)",
               "+ demarrer() : void"] + t("EtatLivraisonException") + ["+ terminer() : void"] + t("EtatLivraisonException")
              + ["+ getStatut() : String"])

    # ------------------------------------------------ decalage de la 2e rangee si les boites du haut ont grandi
    row1_bottom = max(m.getg(k, "y") + m.getg(k, "height") for k in ("Client", "Livreur", "Plat", "Restaurant"))
    dy = max(0, int(row1_bottom + 95 - m.getg("Commande", "y")))
    for k in ("Livraison", "Commande"):
        m.setg(k, y=m.getg(k, "y") + dy)
    low = max(m.getg(k, "y") + m.getg(k, "height") for k in ("Livraison", "Commande"))
    pts = m.cells["a6"].find("mxGeometry").find("Array")
    for p in pts:
        p.set("y", str(int(low + 45)))

    # ------------------------------------------------ Panier (TP4 Q3) : extends Commande implements Comparable
    cy = m.getg("Commande", "y")
    m.add_class("Panier", "<b>Panier</b><br><font style=\"font-size:11px\">implements Comparable&lt;Panier&gt;</font>",
                1090, cy + 150, 290, "#f8cecc", "#b85450", [],
                ["+ ajouterPlat(p : Plat) : void"] + t("PlatIndisponibleException")
                + ["+ compareTo(autre : Panier) : int", "+ getListePlats() : List&lt;Plat&gt;",
                   "+ creerPanier() : Panier", "+ sauvegarderPanier() : void"], start=46)
    m.add_edge("g_panier", "Panier", "Commande", GEN + "edgeStyle=orthogonalEdgeStyle;rounded=0;exitX=0;exitY=0.5;exitDx=0;exitDy=0;entryX=1;entryY=0.85;entryDx=0;entryDy=0;")

    page_w, page_h = 1480, int(max(1083, cy + 150 + m.getg("Panier", "height") + 60, cy + m.getg("Commande", "height") + 120))

    # ------------------------------------------------ elements propres a chaque question
    if T:
        names = ["NoteInvalideException", "AucuneNoteException", "PaiementImpossibleException",
                 "CapaciteDepasseeException", "LivreurIndisponibleException", "PlatIndisponibleException",
                 "CommandeVideException", "EtatLivraisonException", "FormatFichierException"]
        bottom = exception_cluster(m, names)
        m.add_note("note_q1",
                   "<b>Notation</b> : « throws X » = la méthode peut lever X (UML : {exceptions = X}).<br>"
                   "Toutes les FastBiteException sont <b>vérifiées</b> (try/catch ou throws obligatoire).<br>"
                   "<b>Non vérifiée</b> : IllegalArgumentException (plat null) – bug du code, inchangé depuis le TP4.<br>"
                   "<b>Nouveau TP5</b> : Evaluable déclare les throws ; Livreur, Plat, Restaurant doivent les respecter ;<br>"
                   "Panier redéfinit ajouterPlat avec le même throws que Commande.",
                   1470, bottom + 20, 520, 125)
        page_w = 2020
        page_h = max(page_h, bottom + 190)
    if F:
        # colonne de droite : IOException (java.io), FastBiteException, les 2 exceptions du fichier
        m.add_class("IOException", "«java.io»<br><b>IOException</b>", 1470, 90, 255, GREYC[0], GREYC[1], [], [], start=46)
        shrink_empty(m, "IOException", 46)
        names = ["FormatFichierException", "CapaciteDepasseeException"]
        bottom = exception_cluster(m, names, y0=230, step=50, fb_x=1790)
        m.add_note("note_q2",
                   "<b>Format du fichier menu_chezmario.txt</b><br>1 ligne = 1 plat, 4 champs séparés par « ; »<br>"
                   "<b>nom;prix;description;allergène1,allergène2</b><br>prix : point décimal ; allergènes séparés par « , »<br>"
                   "(champ vide = aucun allergène)<br><br>Exemple :<br>Pizza Margherita;12.5;Tomate, mozzarella;gluten,lactose",
                   1470, bottom + 20, 520, 150)
        page_w = 2020
        page_h = max(page_h, bottom + 200)
    if S:
        m.add_class("Serializable", "«interface» java.io<br><b>Serializable</b>", 1130, 80, 250,
                    "#ffffff", "#000000", [], [], start=46)
        m.cells["Serializable"].set("style", m.cells["Evaluable"].get("style"))
        shrink_empty(m, "Serializable", 50)
        m.add_edge("rz_rest", "Restaurant", "Serializable", REAL + "edgeStyle=orthogonalEdgeStyle;rounded=0;exitX=0.8;exitY=0;exitDx=0;exitDy=0;entryX=0.8;entryY=1;entryDx=0;entryDy=0;")
        m.add_edge("rz_plat", "Plat", "Serializable", REAL + "edgeStyle=orthogonalEdgeStyle;rounded=0;exitX=0.95;exitY=0;exitDx=0;exitDy=0;entryX=0.2;entryY=1;entryDx=0;entryDy=0;",
                   points=[(1015, 330), (1180, 330)])
        m.add_note("note_q3",
                   "<b>Sérialisation</b> : writeObject / readObject sur une ArrayList&lt;Restaurant&gt;<br>"
                   "(ArrayList est déjà Serializable). Restaurant ne peut être sérialisé que si<br>"
                   "<b>Plat</b> l'est aussi (tout le graphe d'objets).<br><br>"
                   "<b>transient</b> : nbCommandesRecues n'est pas enregistré (0 après readObject).<br>"
                   "<b>Non Serializable</b> : Utilisateur, Client, Livreur, Commande, Panier, Livraison<br>"
                   "(sessions et transactions) → NotSerializableException.<br>"
                   "Méthodes <u>soulignées</u> = static.",
                   1430, 400, 400, 215)
        page_w = 1860
    m.save({"q1": "TP5_Q1_exceptions", "q2": "TP5_Q2_fichier", "q3": "TP5_Q3_serialisation"}[variant], page_w, page_h)
    return page_w, page_h


if __name__ == "__main__":
    sys.path.insert(0, HERE)
    from render_drawio import render
    for v, name in (("q1", "TP5_Q1_exceptions"), ("q2", "TP5_Q2_fichier"), ("q3", "TP5_Q3_serialisation")):
        w, h = build(v)
        render(os.path.join(OUT, name + ".drawio"), os.path.join(OUT, name + ".png"), w, h)
        print("ok", name, w, h)
