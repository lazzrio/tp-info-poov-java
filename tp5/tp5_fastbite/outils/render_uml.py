# -*- coding: utf-8 -*-
"""Transforme les SVG UML en PNG (apres make_uml.py)."""
import os, re, sys
sys.path.insert(0, os.path.dirname(__file__))
from shot import html_to_png

base = os.path.abspath(os.path.join(os.path.dirname(__file__), "..", "..", "rendu_tp5", "uml"))
for n in ("q1_exceptions", "q2_fichier", "q3_serialisation"):
    svg = open(os.path.join(base, n + ".svg"), encoding="utf-8").read()
    w, h = map(int, re.search(r'width="(\d+)" height="(\d+)"', svg).groups())
    html = '<html><body style="margin:0;background:#fff">' + svg + '</body></html>'
    html_to_png(html, os.path.join(base, n + ".png"), w, h)
    print("png", n, w, h)
