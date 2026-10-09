import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/*
 TP6 : point d'entree de l'interface graphique FastBite.
 Lancer :  javac -encoding UTF-8 -d out src/*.java   puis   java -cp out FastBiteGUI
 (a lancer depuis le dossier tp6_fastbite, pour que le dossier data/ soit trouve)
 */
public class FastBiteGUI {
    // Apparence « Nimbus » : identique sur Windows, Mac et Linux
    public static void installerApparence() {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            // apparence indisponible : on garde celle par defaut, rien de grave
        }
    }

    public static void main(String[] args) {
        installerApparence();
        // Swing demande que les fenetres soient creees dans son propre fil (thread)
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Donnees donnees = new Donnees();
                FenetreAccueil accueil = new FenetreAccueil(donnees);
                try {
                    donnees.creerJeuDEssai();
                    accueil.rafraichir();
                } catch (CapaciteDepasseeException e) {
                    Messages.erreur(accueil, "Demarrage", e.getMessage());
                }
                accueil.setVisible(true);
            }
        });
    }
}
