import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/*
 TP6 : boites de dialogue de l'application (une seule facon d'afficher une erreur).
 Chaque exception attrapee dans un listener finit ici : l'utilisateur voit le message
 de l'exception au lieu d'une trace dans la console.
 */
public class Messages {
    // true = boite modale classique. Mis a false par le programme de tests/captures
    // pour que la boite ne bloque pas le programme en attendant un clic sur OK.
    public static boolean modal = true;
    // Derniere boite ouverte (utile aux tests pour la verifier)
    public static JDialog derniere = null;
    public static String dernierTexte = "";

    public static void erreur(Component parent, String titre, String message) {
        afficher(parent, titre, message, Style.ROUGE, "!");
    }

    public static void info(Component parent, String titre, String message) {
        afficher(parent, titre, message, Style.VERT, "i");
    }

    private static void afficher(Component parent, String titre, String message, final Color couleur, final String signe) {
        dernierTexte = message;
        Window fenetre = (parent == null) ? null : SwingUtilities.getWindowAncestor(parent);
        if (parent instanceof Window) fenetre = (Window) parent;
        final JDialog boite = new JDialog(fenetre, titre);
        boite.setModal(modal);

        // Pastille ronde coloree avec « ! » ou « i »
        JComponent pastille = new JComponent() {
            @Override
            protected void paintComponent(Graphics g0) {
                Graphics2D g = (Graphics2D) g0.create();
                Style.lisser(g);
                g.setColor(couleur);
                g.fillOval(4, 4, 44, 44);
                g.setColor(Color.WHITE);
                g.setFont(Style.police(Font.BOLD, 28));
                int l = g.getFontMetrics().stringWidth(signe);
                g.drawString(signe, 26 - l / 2, 36);
                g.dispose();
            }
        };
        pastille.setPreferredSize(new Dimension(54, 54));

        // Le HTML dans un JLabel permet le retour a la ligne automatique (largeur fixee)
        JLabel titreL = Style.texte(titre, Font.BOLD, 17, Style.NOIR);
        JLabel texteL = new JLabel("<html><div style='width:330px'>" + message.replace("<", "&lt;") + "</div></html>");
        texteL.setFont(Style.police(Font.PLAIN, 14));
        texteL.setForeground(Style.GRIS);
        JPanel textes = new JPanel(new BorderLayout(0, 6));
        textes.setOpaque(false);
        textes.add(titreL, BorderLayout.NORTH);
        textes.add(texteL, BorderLayout.CENTER);

        Style.Bouton ok = Style.boutonNoir("OK");
        ok.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { boite.dispose(); }
        });
        JPanel bas = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        bas.setOpaque(false);
        bas.add(ok);

        JPanel contenu = new JPanel(new BorderLayout(16, 14));
        contenu.setBackground(Color.WHITE);
        contenu.setBorder(BorderFactory.createEmptyBorder(20, 22, 16, 22));
        contenu.add(pastille, BorderLayout.WEST);
        contenu.add(textes, BorderLayout.CENTER);
        contenu.add(bas, BorderLayout.SOUTH);
        boite.setContentPane(contenu);
        boite.pack();
        boite.setLocationRelativeTo(fenetre);
        derniere = boite;
        if (modal) {
            boite.setVisible(true);   // attend le clic sur OK
        }
    }
}
