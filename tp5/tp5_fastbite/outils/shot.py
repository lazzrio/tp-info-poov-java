# -*- coding: utf-8 -*-
"""Capture d'une page HTML locale en PNG avec Edge/Chrome headless."""
import os, subprocess, tempfile, pathlib, time

BROWSERS = [r"C:\Program Files (x86)\Microsoft\Edge\Application\msedge.exe",
            r"C:\Program Files\Google\Chrome\Application\chrome.exe"]


def browser():
    for b in BROWSERS:
        if os.path.exists(b):
            return b
    raise RuntimeError("Edge/Chrome introuvable")


def screenshot(html_path, png_path, width, height, scale=2):
    prof = tempfile.mkdtemp(prefix="shot_")
    url = pathlib.Path(html_path).resolve().as_uri()
    cmd = [browser(), "--headless=new", "--disable-gpu", "--hide-scrollbars",
           f"--user-data-dir={prof}", f"--window-size={width},{height}",
           f"--force-device-scale-factor={scale}", f"--screenshot={png_path}", url]
    if os.path.exists(png_path):
        os.remove(png_path)
    subprocess.run(cmd, check=True, capture_output=True, timeout=120)
    # Edge rend parfois la main avant d'avoir fini d'ecrire le PNG : on attend qu'il soit stable
    last = -1
    for _ in range(60):
        if os.path.exists(png_path):
            size = os.path.getsize(png_path)
            if size > 0 and size == last:
                return
            last = size
        time.sleep(0.5)
    raise RuntimeError("capture non creee : " + png_path)


def html_to_png(html_text, png_path, width, height, scale=2):
    tmp = os.path.splitext(png_path)[0] + ".tmp.html"
    with open(tmp, "w", encoding="utf-8") as f:
        f.write(html_text)
    try:
        screenshot(tmp, png_path, width, height, scale)
    finally:
        if os.path.exists(tmp):
            os.remove(tmp)
