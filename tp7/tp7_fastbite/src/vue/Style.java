package vue;

import modele.*;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.LayoutManager;
import java.awt.Polygon;
import javax.swing.Icon;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/*
 TP6 : charte graphique de FastBite (couleurs, polices) et composants dessines a la main.
 Swing permet de redefinir paintComponent() : on dessine nous-memes les boutons arrondis,
 les cartes et les bannieres, pour un rendu proche des applications de livraison actuelles.
 */
public class Style {
    // Couleurs de la marque
    public static final Color ORANGE = new Color(255, 106, 0);      // couleur FastBite
    public static final Color NOIR = new Color(20, 20, 20);
    public static final Color VERT = new Color(6, 193, 103);        // actions « positives »
    public static final Color ROUGE = new Color(214, 48, 49);
    public static final Color FOND = new Color(246, 246, 246);
    public static final Color BLANC = Color.WHITE;
    public static final Color GRIS = new Color(110, 110, 110);
    public static final Color GRIS_CLAIR = new Color(232, 232, 232);

    public static Font police(int style, int taille) {
        return new Font("Segoe UI", style, taille);
    }

    public static void lisser(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }

    public static JLabel texte(String contenu, int style, int taille, Color couleur) {
        JLabel l = new JLabel(contenu);
        l.setFont(police(style, taille));
        l.setForeground(couleur);
        return l;
    }

    // Logo : « Fast » en noir, « Bite » en orange (HTML accepte dans un JLabel)
    public static JLabel logo(int taille) {
        JLabel l = new JLabel("<html><span style='color:#141414'>Fast</span><span style='color:#FF6A00'>Bite</span></html>");
        l.setFont(police(Font.BOLD, taille));
        return l;
    }

    public static JTextField champ(String valeur, int colonnes) {
        JTextField c = new JTextField(valeur, colonnes);
        c.setFont(police(Font.PLAIN, 15));
        c.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GRIS_CLAIR, 2, true),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        return c;
    }

    // ------------------------------------------------------------ bouton arrondi
    public static class Bouton extends JButton {
        private final Color fond;
        private final Color couleurTexte;
        private final boolean contour;        // true = bouton blanc avec un trait

        public Bouton(String libelle, Color fond, Color couleurTexte, boolean contour) {
            super(libelle);
            this.fond = fond;
            this.couleurTexte = couleurTexte;
            this.contour = contour;
            setFont(police(Font.BOLD, 14));
            setContentAreaFilled(false);      // on dessine le fond nous-memes
            setBorderPainted(false);
            setFocusPainted(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(BorderFactory.createEmptyBorder(9, 18, 9, 18));
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            lisser(g);
            int rayon = getHeight();          // demi-cercles sur les cotes (« pilule »)
            Color c = fond;
            if (getModel().isPressed()) c = c.darker();
            else if (getModel().isRollover()) c = contour ? new Color(240, 240, 240) : c.brighter();
            g.setColor(c);
            g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, rayon, rayon);
            if (contour) {
                g.setColor(GRIS_CLAIR);
                g.setStroke(new BasicStroke(2f));
                g.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, rayon, rayon);
            }
            g.setFont(getFont());
            g.setColor(couleurTexte);
            FontMetrics fm = g.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(getText())) / 2;
            int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
            g.drawString(getText(), x, y);
            g.dispose();
        }
    }

    public static Bouton boutonNoir(String t)    { return new Bouton(t, NOIR, BLANC, false); }
    public static Bouton boutonVert(String t)    { return new Bouton(t, VERT, BLANC, false); }
    public static Bouton boutonOrange(String t)  { return new Bouton(t, ORANGE, BLANC, false); }
    public static Bouton boutonContour(String t) { return new Bouton(t, BLANC, NOIR, true); }

    // ------------------------------------------------------------ carte blanche arrondie
    public static class Carte extends JPanel {
        public Carte(LayoutManager disposition) {
            super(disposition);
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            lisser(g);
            g.setColor(BLANC);
            g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 22, 22);
            g.setColor(GRIS_CLAIR);
            g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 22, 22);
            g.dispose();
        }
    }

    // ------------------------------------------------------------ banniere d'un restaurant
    // Pas de photo : un degrade (couleur choisie par un numero), l'initiale en filigrane
    // et une pastille blanche (la zone de livraison).
    public static class Banniere extends JComponent {
        private final String nom;
        private final String delai;           // texte de la pastille
        private final boolean avecNom;        // true = le nom est ecrit dans la banniere
        private final int couleur;
        private static final Color[][] PALETTE = {
            {new Color(255, 106, 0), new Color(255, 170, 60)},
            {new Color(214, 48, 49), new Color(255, 118, 117)},
            {new Color(9, 132, 227), new Color(116, 185, 255)},
            {new Color(0, 148, 110), new Color(85, 239, 196)},
            {new Color(108, 92, 231), new Color(162, 155, 254)},
        };

        public Banniere(String nom, String pastille, int hauteur, boolean avecNom, int couleur) {
            this.nom = nom;
            this.delai = pastille;
            this.avecNom = avecNom;
            this.couleur = couleur;
            setPreferredSize(new Dimension(200, hauteur));
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            lisser(g);
            Color[] c = PALETTE[Math.abs(couleur) % PALETTE.length];
            g.setPaint(new GradientPaint(0, 0, c[0], getWidth(), getHeight(), c[1]));
            g.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
            // grande initiale en filigrane
            g.setColor(new Color(255, 255, 255, 70));
            g.setFont(police(Font.BOLD, getHeight()));
            g.drawString(nom.substring(0, 1), getWidth() - (int) (getHeight() * 0.85), (int) (getHeight() * 0.88));
            // nom du restaurant
            if (avecNom) {
                g.setColor(BLANC);
                g.setFont(police(Font.BOLD, 28));
                g.drawString(nom, 18, getHeight() - 18);
            }
            // pastille « delai »
            if (delai != null) {
                g.setFont(police(Font.BOLD, 12));
                int l = g.getFontMetrics().stringWidth(delai) + 18;
                g.setColor(BLANC);
                g.fillRoundRect(12, 12, l, 24, 24, 24);
                g.setColor(NOIR);
                g.drawString(delai, 21, 29);
            }
            g.dispose();
        }
    }

    // ------------------------------------------------------------ petite etoile (icone d'un JLabel)
    public static class Etoile implements Icon {
        private final int taille;

        public Etoile(int taille) { this.taille = taille; }

        @Override public int getIconWidth()  { return taille + 2; }
        @Override public int getIconHeight() { return taille; }

        @Override
        public void paintIcon(Component c, Graphics g0, int x, int y) {
            Graphics2D g = (Graphics2D) g0.create();
            lisser(g);
            Polygon p = new Polygon();
            double r = taille / 2.0;
            for (int i = 0; i < 10; i++) {            // 5 pointes : rayon long / rayon court en alternance
                double rayon = (i % 2 == 0) ? r : r * 0.42;
                double angle = -Math.PI / 2 + i * Math.PI / 5;
                p.addPoint((int) Math.round(x + r + rayon * Math.cos(angle)),
                           (int) Math.round(y + r + rayon * Math.sin(angle)));
            }
            g.setColor(ORANGE);
            g.fillPolygon(p);
            g.dispose();
        }
    }

    // ------------------------------------------------------------ rond de couleur avec une initiale
    public static class Avatar extends JComponent {
        private final String lettre;
        private final Color fond;

        public Avatar(String nom, Color fond, int taille) {
            this.lettre = nom.substring(0, 1).toUpperCase();
            this.fond = fond;
            setPreferredSize(new Dimension(taille, taille));
        }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            lisser(g);
            int d = Math.min(getWidth(), getHeight()) - 2;
            int x = (getWidth() - d) / 2, y = (getHeight() - d) / 2;
            g.setColor(fond);
            g.fillOval(x, y, d, d);
            g.setColor(BLANC);
            g.setFont(police(Font.BOLD, d / 2));
            FontMetrics fm = g.getFontMetrics();
            g.drawString(lettre, x + (d - fm.stringWidth(lettre)) / 2, y + (d - fm.getHeight()) / 2 + fm.getAscent());
            g.dispose();
        }
    }

    // ------------------------------------------------------------ etiquette arrondie (statut)
    public static class Badge extends JLabel {
        private Color fond = GRIS;

        public Badge(String contenu) {
            super(contenu);
            setFont(police(Font.BOLD, 12));
            setForeground(BLANC);
            setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));
        }

        public void setFond(Color fond) { this.fond = fond; repaint(); }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            lisser(g);
            g.setColor(fond);
            g.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
            g.dispose();
            super.paintComponent(g0);                 // puis le texte du JLabel par-dessus
        }
    }

    // ------------------------------------------------------------ frise des etapes de la livraison
    public static class Etapes extends JComponent {
        private final String[] noms;
        private int faites = 0;                       // nombre d'etapes terminees

        public Etapes(String[] noms) {
            this.noms = noms;
            setPreferredSize(new Dimension(300, 58));
        }

        public void setFaites(int n) { this.faites = n; repaint(); }
        public int getFaites()       { return faites; }

        @Override
        protected void paintComponent(Graphics g0) {
            Graphics2D g = (Graphics2D) g0.create();
            lisser(g);
            int marge = 48, y = 14, r = 9;
            int pas = (getWidth() - 2 * marge) / (noms.length - 1);
            g.setStroke(new BasicStroke(4f));
            for (int i = 0; i < noms.length - 1; i++) {           // traits entre les ronds
                g.setColor(i + 1 < faites ? VERT : GRIS_CLAIR);
                g.drawLine(marge + i * pas, y, marge + (i + 1) * pas, y);
            }
            g.setFont(police(Font.BOLD, 12));
            FontMetrics fm = g.getFontMetrics();
            for (int i = 0; i < noms.length; i++) {
                int x = marge + i * pas;
                g.setColor(i < faites ? VERT : GRIS_CLAIR);
                g.fillOval(x - r, y - r, 2 * r, 2 * r);
                g.setColor(i < faites ? NOIR : GRIS);
                g.drawString(noms[i], x - fm.stringWidth(noms[i]) / 2, y + 30);
            }
            g.dispose();
        }
    }

    // Panneau transparent (pour ranger des composants sans cacher le fond)
    public static JPanel groupe(LayoutManager disposition) {
        JPanel p = new JPanel(disposition);
        p.setOpaque(false);
        return p;
    }

    // Une ligne « libelle a gauche ......... valeur a droite »
    public static JPanel ligne(String gauche, String droite, int style, int taille) {
        JPanel p = groupe(new BorderLayout(10, 0));
        p.add(texte(gauche, style, taille, NOIR), BorderLayout.CENTER);
        p.add(texte(droite, style, taille, NOIR), BorderLayout.EAST);
        return p;
    }

    // Note moyenne d'un restaurant ou d'un livreur : « 4,5 » ou « Nouveau » s'il n'a aucune note
    public static String note(Evaluable e) {
        try {
            return String.format("%.1f", e.getNoteMoyenne()).replace('.', ',');
        } catch (AucuneNoteException ex) {
            return "Nouveau";
        }
    }

    // Prix a la francaise : 12.5 -> « 12,50 € »
    public static String prix(double montant) {
        return String.format("%.2f", montant).replace('.', ',') + " €";
    }
}
