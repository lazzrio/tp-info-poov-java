# -*- coding: utf-8 -*-
"""Rend un fichier .drawio en PNG via le visualiseur officiel diagrams.net (Edge headless).
Usage : python render_drawio.py fichier.drawio sortie.png [largeur hauteur]"""
import sys, os, json, html, subprocess, tempfile, pathlib, time
sys.path.insert(0, os.path.dirname(__file__))
from shot import browser


def render(drawio, png, width=1500, height=1100, scale=2):
    xml = open(drawio, encoding="utf-8").read()
    cfg = {"highlight": "#0000ff", "nav": False, "resize": False, "toolbar": "", "edit": "_blank", "xml": xml}
    page = ('<html><head><meta charset="utf-8"></head><body style="margin:0;background:#fff">'
            f'<div class="mxgraph" style="max-width:100%" data-mxgraph="{html.escape(json.dumps(cfg))}"></div>'
            '<script src="https://viewer.diagrams.net/js/viewer-static.min.js"></script></body></html>')
    tmp = os.path.splitext(png)[0] + ".tmp.html"
    open(tmp, "w", encoding="utf-8").write(page)
    if os.path.exists(png):
        os.remove(png)
    prof = tempfile.mkdtemp(prefix="drawio_")
    cmd = [browser(), "--headless=new", "--disable-gpu", "--hide-scrollbars", f"--user-data-dir={prof}",
           f"--window-size={width},{height}", f"--force-device-scale-factor={scale}",
           "--virtual-time-budget=15000", f"--screenshot={png}", pathlib.Path(tmp).as_uri()]
    subprocess.run(cmd, check=True, capture_output=True, timeout=180)
    last = -1
    for _ in range(60):
        if os.path.exists(png):
            sz = os.path.getsize(png)
            if sz > 0 and sz == last:
                break
            last = sz
        time.sleep(0.5)
    os.remove(tmp)
    return png


if __name__ == "__main__":
    a = sys.argv
    render(a[1], a[2], *(int(x) for x in a[3:5]))
    print("ok", a[2])
