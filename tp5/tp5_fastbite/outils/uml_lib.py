# -*- coding: utf-8 -*-
"""Mini-bibliotheque UML : decrit un diagramme de classes en Python et le sort en
SVG (pour les captures) et en .drawio (pour pouvoir le modifier dans diagrams.net)."""
import html
from xml.sax.saxutils import escape

CHAR_W = 7.3      # largeur d'un caractere Consolas 13px
LINE_H = 19
TITLE_H = 30
STEREO_H = 15
PAD = 10

PALETTE = {
    "metier":    ("#dbeafe", "#1e40af"),   # fond en-tete, trait
    "exception": ("#fee2e2", "#b91c1c"),
    "interface": ("#dcfce7", "#15803d"),
    "externe":   ("#e5e7eb", "#4b5563"),
    "note":      ("#fef9c3", "#a16207"),
}


class Box:
    def __init__(self, key, name, kind="metier", stereo=None, abstract=False,
                 attrs=(), methods=(), x=0, y=0, w=None, note=False, text_lines=()):
        self.key, self.name, self.kind, self.stereo = key, name, kind, stereo
        self.abstract, self.attrs, self.methods = abstract, list(attrs), list(methods)
        self.x, self.y, self.note = x, y, note
        self.text_lines = list(text_lines)
        self.sections = 0
        if note:
            longest = max(len(t) for t in self.text_lines)
            self.w = w or int(longest * CHAR_W + 2 * PAD + 6)
            self.h = len(self.text_lines) * LINE_H + 2 * PAD
        else:
            longest = max([len(self._t(m)) for m in self.attrs + self.methods] + [len(name) * 1.15])
            self.w = w or int(longest * CHAR_W + 2 * PAD + 4)
            head = TITLE_H + (STEREO_H if stereo else 0)
            body = 0
            if self.attrs or self.methods:
                body += max(len(self.attrs), 0) * LINE_H + 8
                body += len(self.methods) * LINE_H + 8
            self.h = head + body

    @staticmethod
    def _t(m):
        return m[0] if isinstance(m, tuple) else m

    @property
    def cx(self): return self.x + self.w / 2
    @property
    def cy(self): return self.y + self.h / 2


class Edge:
    def __init__(self, a, b, kind, label=None, mult_a=None, mult_b=None, side_a=None, side_b=None):
        self.a, self.b, self.kind, self.label = a, b, kind, label
        self.mult_a, self.mult_b = mult_a, mult_b
        self.side_a, self.side_b = side_a, side_b


class Diagram:
    def __init__(self, title, width, height):
        self.title, self.width, self.height = title, width, height
        self.boxes, self.edges = {}, []
        self.legend = []

    def add(self, box):
        self.boxes[box.key] = box
        return box

    def link(self, a, b, kind, **kw):
        self.edges.append(Edge(a, b, kind, **kw))

    # ------------------------------------------------------------------ geometry
    def _sides(self):
        """Choisit le cote de chaque extremite puis repartit les points d'attache."""
        for e in self.edges:
            A, B = self.boxes[e.a], self.boxes[e.b]
            dx, dy = B.cx - A.cx, B.cy - A.cy
            horiz = abs(dx) * 0.55 > abs(dy) or (abs(dx) > (A.w + B.w) / 2 and abs(dy) < 200)
            if horiz:
                sa, sb = ("R", "L") if dx > 0 else ("L", "R")
            else:
                sa, sb = ("B", "T") if dy > 0 else ("T", "B")
            e.side_a = e.side_a or sa
            e.side_b = e.side_b or sb
        groups = {}
        for i, e in enumerate(self.edges):
            groups.setdefault((e.a, e.side_a), []).append((i, "a"))
            groups.setdefault((e.b, e.side_b), []).append((i, "b"))
        self.pts = {}
        for (key, side), items in groups.items():
            box = self.boxes[key]

            def other_pos(it):
                e = self.edges[it[0]]
                o = self.boxes[e.b if it[1] == "a" else e.a]
                return o.cy if side in "LR" else o.cx
            items.sort(key=other_pos)
            n = len(items)
            for k, it in enumerate(items):
                f = (k + 1) / (n + 1)
                if side == "L":   p = (box.x, box.y + box.h * f)
                elif side == "R": p = (box.x + box.w, box.y + box.h * f)
                elif side == "T": p = (box.x + box.w * f, box.y)
                else:             p = (box.x + box.w * f, box.y + box.h)
                self.pts[(it[0], it[1])] = p

    # ------------------------------------------------------------------ SVG
    def svg(self):
        self._sides()
        o = []
        o.append(f'<svg xmlns="http://www.w3.org/2000/svg" width="{self.width}" height="{self.height}" '
                 f'viewBox="0 0 {self.width} {self.height}" font-family="Segoe UI, Arial, sans-serif">')
        o.append('''<defs>
<marker id="tri" markerWidth="14" markerHeight="14" refX="13" refY="7" orient="auto" markerUnits="userSpaceOnUse">
 <path d="M1,1 L13,7 L1,13 z" fill="#fff" stroke="#111" stroke-width="1.4"/></marker>
<marker id="open" markerWidth="12" markerHeight="12" refX="11" refY="6" orient="auto" markerUnits="userSpaceOnUse">
 <path d="M1,1 L11,6 L1,11" fill="none" stroke="#b91c1c" stroke-width="1.6"/></marker>
<marker id="dia" markerWidth="18" markerHeight="14" refX="1" refY="7" orient="auto" markerUnits="userSpaceOnUse">
 <path d="M1,7 L9,1 L17,7 L9,13 z" fill="#fff" stroke="#111" stroke-width="1.4"/></marker>
<marker id="diaf" markerWidth="18" markerHeight="14" refX="1" refY="7" orient="auto" markerUnits="userSpaceOnUse">
 <path d="M1,7 L9,1 L17,7 L9,13 z" fill="#111" stroke="#111" stroke-width="1.4"/></marker>
</defs>''')
        o.append(f'<rect width="{self.width}" height="{self.height}" fill="#ffffff"/>')
        # aretes
        for i, e in enumerate(self.edges):
            x1, y1 = self.pts[(i, "a")]
            x2, y2 = self.pts[(i, "b")]
            style = {"gen":  'stroke="#111" stroke-width="1.5" marker-end="url(#tri)"',
                     "real": 'stroke="#111" stroke-width="1.5" stroke-dasharray="7 5" marker-end="url(#tri)"',
                     "dep":  'stroke="#b91c1c" stroke-width="1.4" stroke-dasharray="6 4" marker-end="url(#open)"',
                     "agg":  'stroke="#111" stroke-width="1.5" marker-start="url(#dia)"',
                     "comp": 'stroke="#111" stroke-width="1.5" marker-start="url(#diaf)"',
                     "note": 'stroke="#a16207" stroke-width="1.2" stroke-dasharray="3 3"',
                     "assoc": 'stroke="#111" stroke-width="1.5"'}[e.kind]
            o.append(f'<line x1="{x1:.1f}" y1="{y1:.1f}" x2="{x2:.1f}" y2="{y2:.1f}" {style}/>')
            if e.label:
                mx, my = (x1 + x2) / 2, (y1 + y2) / 2
                o.append(f'<text x="{mx:.1f}" y="{my - 4:.1f}" font-size="12" text-anchor="middle" '
                         f'fill="#b91c1c" font-style="italic">{escape(e.label)}</text>')
            for mult, (px, py), (qx, qy) in ((e.mult_a, (x1, y1), (x2, y2)), (e.mult_b, (x2, y2), (x1, y1))):
                if mult:
                    import math
                    d = math.hypot(qx - px, qy - py) or 1
                    tx, ty = px + (qx - px) / d * 26, py + (qy - py) / d * 26 - 6
                    o.append(f'<text x="{tx:.1f}" y="{ty:.1f}" font-size="12" text-anchor="middle" fill="#111">{escape(mult)}</text>')
        # boites
        for b in self.boxes.values():
            if b.note:
                o.append(f'<rect x="{b.x}" y="{b.y}" width="{b.w}" height="{b.h}" fill="{PALETTE["note"][0]}" '
                         f'stroke="{PALETTE["note"][1]}" stroke-width="1.3"/>')
                for k, t in enumerate(b.text_lines):
                    o.append(f'<text x="{b.x + PAD}" y="{b.y + PAD + 14 + k * LINE_H}" font-size="13" '
                             f'font-family="Consolas, monospace" fill="#422006">{escape(t)}</text>')
                continue
            fill, stroke = PALETTE[b.kind]
            head = TITLE_H + (STEREO_H if b.stereo else 0)
            o.append(f'<rect x="{b.x}" y="{b.y}" width="{b.w}" height="{b.h}" fill="#fff" stroke="{stroke}" stroke-width="1.6"/>')
            o.append(f'<rect x="{b.x}" y="{b.y}" width="{b.w}" height="{head}" fill="{fill}" stroke="{stroke}" stroke-width="1.6"/>')
            ty = b.y + 20
            if b.stereo:
                o.append(f'<text x="{b.cx}" y="{b.y + 14}" font-size="12" text-anchor="middle" fill="#111">'
                         f'&#171;{escape(b.stereo)}&#187;</text>')
                ty = b.y + 14 + 17
            italic = ' font-style="italic"' if b.abstract else ""
            o.append(f'<text x="{b.cx}" y="{ty}" font-size="15" font-weight="700" text-anchor="middle" fill="#111"{italic}>'
                     f'{escape(b.name)}</text>')
            y = b.y + head
            if b.attrs or b.methods:
                for k, a in enumerate(b.attrs):
                    o.append(self._member(b, a, y + 15 + k * LINE_H))
                y += len(b.attrs) * LINE_H + 8
                o.append(f'<line x1="{b.x}" y1="{y - 4 - len(b.attrs) * LINE_H if False else b.y + head + len(b.attrs) * LINE_H + 4}" '
                         f'x2="{b.x + b.w}" y2="{b.y + head + len(b.attrs) * LINE_H + 4}" stroke="{stroke}" stroke-width="1.3"/>')
                for k, m in enumerate(b.methods):
                    o.append(self._member(b, m, y + 15 + k * LINE_H))
        # legende
        ly = self.height - 12 - len(self.legend) * 18
        for k, (kind, text) in enumerate(self.legend):
            yy = ly + k * 18
            lx = 20
            if kind == "dep":
                o.append(f'<line x1="{lx}" y1="{yy}" x2="{lx + 40}" y2="{yy}" stroke="#b91c1c" stroke-width="1.4" '
                         f'stroke-dasharray="6 4" marker-end="url(#open)"/>')
            elif kind == "gen":
                o.append(f'<line x1="{lx}" y1="{yy}" x2="{lx + 40}" y2="{yy}" stroke="#111" stroke-width="1.5" marker-end="url(#tri)"/>')
            elif kind == "real":
                o.append(f'<line x1="{lx}" y1="{yy}" x2="{lx + 40}" y2="{yy}" stroke="#111" stroke-width="1.5" '
                         f'stroke-dasharray="7 5" marker-end="url(#tri)"/>')
            elif kind == "agg":
                o.append(f'<line x1="{lx}" y1="{yy}" x2="{lx + 40}" y2="{yy}" stroke="#111" stroke-width="1.5" marker-start="url(#dia)"/>')
            o.append(f'<text x="{lx + 50}" y="{yy + 4}" font-size="12" fill="#333">{escape(text)}</text>')
        o.append('</svg>')
        return "\n".join(o)

    def _member(self, b, m, y):
        text, style = (m if isinstance(m, tuple) else (m, ""))
        deco = ' text-decoration="underline"' if "u" in style else ""
        ital = ' font-style="italic"' if "i" in style else ""
        return (f'<text x="{b.x + PAD}" y="{y}" font-size="13" font-family="Consolas, monospace" fill="#111"'
                f'{deco}{ital} xml:space="preserve">{escape(text)}</text>')

    # ------------------------------------------------------------------ draw.io
    def drawio(self):
        self._sides()
        cells = []
        ids = {}
        n = [10]

        def nid():
            n[0] += 1
            return f"c{n[0]}"

        for b in self.boxes.values():
            cid = nid()
            ids[b.key] = cid
            fill, stroke = PALETTE["note" if b.note else b.kind]
            if b.note:
                v = "&#xa;".join(escape(t) for t in b.text_lines)
                cells.append(f'<mxCell id="{cid}" value="{v}" style="shape=note;whiteSpace=wrap;html=1;align=left;'
                             f'spacingLeft=6;fillColor={fill};strokeColor={stroke};fontFamily=Courier New;fontSize=11;" '
                             f'vertex="1" parent="1"><mxGeometry x="{b.x}" y="{b.y}" width="{b.w}" height="{b.h}" as="geometry"/></mxCell>')
                continue
            title = escape(b.name)
            if b.stereo:
                title = f"&#171;{escape(b.stereo)}&#187;<br>{title}"
            if b.abstract:
                title = f"<i>{title}</i>"
            head = TITLE_H + (STEREO_H if b.stereo else 0)
            cells.append(f'<mxCell id="{cid}" value="{escape(title)}" style="swimlane;fontStyle=1;align=center;verticalAlign=top;'
                         f'childLayout=stackLayout;horizontal=1;startSize={head};horizontalStack=0;resizeParent=1;'
                         f'resizeParentMax=0;resizeLast=0;collapsible=0;marginBottom=0;whiteSpace=wrap;html=1;'
                         f'fillColor={fill};strokeColor={stroke};" vertex="1" parent="1">'
                         f'<mxGeometry x="{b.x}" y="{b.y}" width="{b.w}" height="{b.h}" as="geometry"/></mxCell>')
            if b.attrs or b.methods:
                ah = len(b.attrs) * LINE_H + 4
                av = "&#xa;".join(escape(Box._t(a)) for a in b.attrs)
                cells.append(f'<mxCell id="{nid()}" value="{av}" style="text;strokeColor=none;fillColor=#ffffff;align=left;'
                             f'verticalAlign=top;spacingLeft=4;overflow=hidden;rotatable=0;whiteSpace=wrap;html=0;fontFamily=Courier New;fontSize=11;" '
                             f'vertex="1" parent="{cid}"><mxGeometry y="{head}" width="{b.w}" height="{ah}" as="geometry"/></mxCell>')
                cells.append(f'<mxCell id="{nid()}" value="" style="line;strokeWidth=1;fillColor=none;align=left;verticalAlign=middle;'
                             f'spacingTop=-1;rotatable=0;" vertex="1" parent="{cid}"><mxGeometry y="{head + ah}" width="{b.w}" height="8" as="geometry"/></mxCell>')
                mv = "&#xa;".join(escape(Box._t(m)) for m in b.methods)
                mh = len(b.methods) * LINE_H + 4
                cells.append(f'<mxCell id="{nid()}" value="{mv}" style="text;strokeColor=none;fillColor=#ffffff;align=left;'
                             f'verticalAlign=top;spacingLeft=4;overflow=hidden;rotatable=0;whiteSpace=wrap;html=0;fontFamily=Courier New;fontSize=11;" '
                             f'vertex="1" parent="{cid}"><mxGeometry y="{head + ah + 8}" width="{b.w}" height="{mh}" as="geometry"/></mxCell>')
        styles = {
            "gen":  "endArrow=block;endFill=0;endSize=12;html=1;",
            "real": "endArrow=block;endFill=0;endSize=12;dashed=1;html=1;",
            "dep":  "endArrow=open;endSize=10;dashed=1;html=1;strokeColor=#b91c1c;fontColor=#b91c1c;",
            "agg":  "startArrow=diamondThin;startFill=0;startSize=14;endArrow=none;html=1;",
            "comp": "startArrow=diamondThin;startFill=1;startSize=14;endArrow=none;html=1;",
            "note": "endArrow=none;dashed=1;html=1;strokeColor=#a16207;",
            "assoc": "endArrow=none;html=1;",
        }
        for e in self.edges:
            lab = escape(e.label) if e.label else ""
            cells.append(f'<mxCell id="{nid()}" value="{lab}" style="{styles[e.kind]}" edge="1" parent="1" '
                         f'source="{ids[e.a]}" target="{ids[e.b]}"><mxGeometry relative="1" as="geometry"/></mxCell>')
        body = "\n".join(cells)
        return (f'<mxfile host="app.diagrams.net"><diagram name="{escape(self.title)}" id="d1">'
                f'<mxGraphModel dx="1200" dy="800" grid="1" gridSize="10" guides="1" tooltips="1" connect="1" arrows="1" '
                f'fold="1" page="1" pageScale="1" pageWidth="{self.width}" pageHeight="{self.height}" math="0" shadow="0">'
                f'<root><mxCell id="0"/><mxCell id="1" parent="0"/>\n{body}\n</root></mxGraphModel></diagram></mxfile>')
