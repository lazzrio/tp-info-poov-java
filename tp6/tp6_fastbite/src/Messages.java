import java.awt.Component;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

/*
 TP6 : boites de dialogue de l'application (une seule facon d'afficher une erreur).
 Chaque exception attrapee dans un listener finit ici : l'utilisateur voit le message
 de l'exception au lieu d'une trace dans la console.
 */
public class Messages {
    // true = boite modale classique. Mis a false par le programme de tests/captures
    // pour que la boite ne bloque pas le programme en attendant un clic sur OK.
    public static boolean modal = true;
    // Derniere boite ouverte (utile aux tests pour la verifier puis la fermer)
    public static JDialog derniere = null;
    public static String dernierTexte = "";

    // Erreur : icone rouge, titre = type du probleme
    public static void erreur(Component parent, String titre, String message) {
        afficher(parent, titre, message, JOptionPane.ERROR_MESSAGE);
    }

    // Information : icone bleue
    public static void info(Component parent, String titre, String message) {
        afficher(parent, titre, message, JOptionPane.INFORMATION_MESSAGE);
    }

    private static void afficher(Component parent, String titre, String message, int type) {
        dernierTexte = message;
        // Message long : on le coupe en deux lignes a l'espace le plus proche du milieu
        if (message.length() > 70) {
            int coupe = message.indexOf(' ', message.length() / 2);
            if (coupe > 0) {
                message = message.substring(0, coupe) + "\n" + message.substring(coupe + 1);
            }
        }
        JOptionPane contenu = new JOptionPane(message, type);
        JDialog boite = contenu.createDialog(parent, titre);
        boite.setModal(modal);
        derniere = boite;
        if (modal) {
            boite.setVisible(true);   // attend le clic sur OK
        }
    }
}
