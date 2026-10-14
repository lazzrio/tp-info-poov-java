import controleur.ControleurApplication;
import modele.Donnees;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/*
 TP7 : point d'entree de l'interface graphique FastBite, organisee en MVC (modele / vue / controleur).
 Lancer :  javac -encoding UTF-8 -d out -sourcepath src src/FastBiteGUI.java   puis   java -cp out FastBiteGUI
 (a lancer depuis le dossier tp7_fastbite, pour que le dossier data/ soit trouve)
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
                Donnees modele = new Donnees();                          // M : le modele
                new ControleurApplication(modele).demarrer();            // C : cree la vue (V) et la relie au modele
            }
        });
    }
}
