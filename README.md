# TP POO Java — FastBite (ECE ING2)

Application de livraison de repas (thème libre, type Deliveroo), construite TP après TP.

| Dossier | Contenu |
|---|---|
| `src/` | TP3 — implémentation des diagrammes UML |
| `tp5/` | TP5 — exceptions, fichier texte, sérialisation (`tp5_fastbite/` = code, `rendu_tp5/` = présentation, captures, UML) |
| `tp6/` | TP6 — interface graphique Swing et listeners (`tp6_fastbite/` = code, `rendu_tp6/` = présentation, captures) |
| `tp7/` | TP7 — pattern MVC : `modele/`, `vue/`, `controleur/` (`tp7_fastbite/` = code, `rendu_tp7/` = présentation, captures) |

## Lancer le TP7 (MVC)

```sh
cd tp7/tp7_fastbite
javac -encoding UTF-8 -d out $(find src -name "*.java")
java -cp out FastBiteGUI
```

## Lancer le TP6

```sh
cd tp6/tp6_fastbite
javac -encoding UTF-8 -d out src/*.java
java -cp out FastBiteGUI
```

## Lancer le TP5

```sh
cd tp5/tp5_fastbite
javac -encoding UTF-8 -d out src/*.java
java -cp out Main
```
