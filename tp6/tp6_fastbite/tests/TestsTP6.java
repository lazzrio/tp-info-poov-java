import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;

/*
 TP6 : tests de l'interface. On simule les clics (doClick) et on verifie l'affichage,
 les exceptions attrapees et les fichiers produits. Chaque etape enregistre aussi une
 image de la fenetre (PNG) dans le dossier donne en argument (captures du rendu).
 Lancer depuis tp6_fastbite :  java -cp out TestsTP6 ../rendu_tp6/captures
 */
public class TestsTP6 {
    static String dossier = "captures";
    static int nbTests = 0;

    static void verifier(boolean condition, String quoi) {
        nbTests++;
        if (!condition) throw new AssertionError("ECHEC : " + quoi);
        System.out.println("  ok  " + quoi);
    }

    // Image d'une fenetre ou d'une boite : barre de titre dessinee + contenu reel rendu par Swing
    static void capturer(Window w, String titre, String nom) throws Exception {
        JRootPane racine = (w instanceof JFrame) ? ((JFrame) w).getRootPane() : ((JDialog) w).getRootPane();
        if (!w.isDisplayable()) w.addNotify();
        if (w instanceof JDialog) {                    // taille reelle de la boite (+ marge)
            w.pack();
            w.setSize(w.getWidth() + 70, w.getHeight());
        }
        w.validate();
        int l = racine.getWidth(), h = racine.getHeight(), barre = 32, k = 2;
        BufferedImage img = new BufferedImage((l + 2) * k, (h + barre + 2) * k, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.scale(k, k);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setColor(new Color(120, 120, 120));
        g.fillRect(0, 0, l + 2, h + barre + 2);
        g.setColor(Color.WHITE);
        g.fillRect(1, 1, l, barre);
        g.setColor(new Color(30, 30, 30));
        g.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        g.drawString(titre, 12, 21);
        int bx = l - 100;                               // boutons - [] x dessines au trait
        g.drawLine(bx, 17, bx + 9, 17);
        g.drawRect(bx + 36, 12, 9, 9);
        g.drawLine(bx + 72, 12, bx + 81, 21);
        g.drawLine(bx + 81, 12, bx + 72, 21);
        g.translate(1, barre + 1);
        racine.printAll(g);
        g.dispose();
        new File(dossier).mkdirs();
        ImageIO.write(img, "png", new File(dossier, nom + ".png"));
        System.out.println("  capture " + nom + ".png");
    }

    static void capturerBoite(String nom) throws Exception {
        capturer(Messages.derniere, Messages.derniere.getTitle(), nom);
    }

    @SuppressWarnings("unchecked")
    static <T extends Window> T fenetre(Class<T> type) {
        T trouve = null;
        for (Window w : Window.getWindows()) {
            if (type.isInstance(w) && w.isDisplayable()) trouve = (T) w;
        }
        return trouve;
    }

    public static void main(String[] args) throws Exception {
        if (args.length > 0) dossier = args[0];
        FastBiteGUI.installerApparence();
        Messages.modal = false;                       // les boites ne bloquent pas les tests
        new File(Donnees.FICHIER_SER).delete();

        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                try {
                    scenario();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
        System.out.println("Tous les tests TP6 sont passes (" + nbTests + " verifications).");
        System.exit(0);
    }

    static void scenario() throws Exception {
        Donnees donnees = new Donnees();
        donnees.creerJeuDEssai();
        FenetreAccueil accueil = new FenetreAccueil(donnees);
        accueil.setVisible(true);

        System.out.println("--- Page 1 : accueil");
        verifier(accueil.getListe().getModel().getSize() == 2, "2 restaurants dans la liste");
        accueil.getListe().setSelectedIndex(0);
        capturer(accueil, accueil.getTitle(), "q3_fenetre_accueil");
        accueil.getListe().clearSelection();
        accueil.getBoutonMenu().doClick();
        verifier(Messages.dernierTexte.contains("Selectionnez"), "clic sans selection -> message d'erreur");

        System.out.println("--- Page 2 : menu (listener « Voir le menu »)");
        accueil.getListe().setSelectedIndex(0);
        accueil.getBoutonMenu().doClick();
        FenetreMenu menu = fenetre(FenetreMenu.class);
        verifier(menu != null && !accueil.isVisible(), "la page menu s'ouvre et l'accueil se cache");
        verifier(menu.getTable().getRowCount() == 3, "3 plats dans le tableau");
        capturer(menu, menu.getTitle(), "q3_fenetre_menu");

        menu.getBoutonValider().doClick();
        verifier(Messages.dernierTexte.contains("est vide"), "CommandeVideException attrapee et affichee");
        capturerBoite("q4_exc_commande_vide");

        menu.getTable().setRowSelectionInterval(0, 0);
        menu.getBoutonAjouter().doClick();
        menu.getTable().setRowSelectionInterval(2, 2);
        menu.getBoutonAjouter().doClick();
        verifier(menu.getTotal().equals("Total : 14.5 EUR"), "2 clics « Ajouter » -> total 14.5 EUR");
        capturer(menu, menu.getTitle(), "q4_listener_panier");

        System.out.println("--- Fichier texte");
        menu.getBoutonEnregistrer().doClick();
        File txt = new File("data/menu_chez_mario.txt");
        verifier(txt.exists() && menu.getEtat().contains("3 plats"), "menu enregistre dans data/menu_chez_mario.txt");
        capturer(menu, menu.getTitle(), "q4_txt_enregistre");
        menu.chargerMenu("data/menu_corrompu1.txt");
        verifier(Messages.dernierTexte.contains("ligne 2"), "FormatFichierException attrapee (prix non numerique, ligne 2)");
        verifier(menu.getTable().getRowCount() == 3 && menu.getCommande().getNbPlats() == 2, "menu et panier intacts apres l'erreur");
        capturerBoite("q4_exc_fichier");
        menu.chargerMenu("data/inexistant.txt");
        verifier(Messages.derniere.getTitle().equals("Lecture impossible"), "IOException attrapee (fichier absent)");

        System.out.println("--- Page 3 : commande (listener « Valider »)");
        menu.getBoutonValider().doClick();
        FenetreCommande page = fenetre(FenetreCommande.class);
        verifier(page != null && page.getCommande().getStatut().equals("VALIDEE"), "la page commande s'ouvre, statut VALIDEE");
        capturer(page, page.getTitle(), "q3_fenetre_commande");

        page.getChampPourboire().setText("abc");
        page.getBoutonPourboire().doClick();
        verifier(Messages.dernierTexte.contains("n'est pas un nombre"), "NumberFormatException attrapee (pourboire « abc »)");
        page.getChampPourboire().setText("2");
        page.getBoutonPourboire().doClick();
        verifier(page.getCommande().calculerTotal() == 16.5, "pourboire 2 -> total 16.5 EUR");

        page.getChampNote().setText("7");
        page.getBoutonNoter().doClick();
        verifier(Messages.dernierTexte.contains("Note invalide"), "NoteInvalideException attrapee (note 7)");
        capturerBoite("q4_exc_note");
        page.getChampNote().setText("4.5");
        page.getBoutonNoter().doClick();
        verifier(page.getEtat().contains("4.5"), "note 4.5 acceptee");

        page.getBoutonPayer().doClick();
        verifier(page.getCommande().getStatut().equals("PAYEE"), "paiement accepte, statut PAYEE");
        capturer(page, page.getTitle(), "q4_commande_payee");
        page.getBoutonPayer().doClick();
        verifier(Messages.dernierTexte.contains("deja payee"), "PaiementImpossibleException attrapee (2e clic sur Payer)");
        capturerBoite("q4_exc_paiement");

        System.out.println("--- Retour a l'accueil, serialisation");
        page.getBoutonAccueil().doClick();
        verifier(accueil.isVisible() && accueil.getEtat().contains("PAYEE"), "retour a l'accueil");
        accueil.getBoutonSauver().doClick();
        File ser = new File(Donnees.FICHIER_SER);
        verifier(ser.exists() && ser.length() > 100, "restaurants.ser cree (" + ser.length() + " octets)");
        capturer(accueil, accueil.getTitle(), "q4_ser_sauvegarde");

        donnees.setRestaurants(new ArrayList<Restaurant>());     // on « perd » les donnees
        accueil.rafraichir();
        verifier(accueil.getListe().getModel().getSize() == 0, "liste videe");
        accueil.getBoutonCharger().doClick();
        verifier(accueil.getListe().getModel().getSize() == 2, "Charger (.ser) -> 2 restaurants relus");
        verifier(donnees.getRestaurants().get(0).getNbPlats() == 3, "le menu de Chez Mario est revenu (3 plats)");
        verifier(donnees.getRestaurants().get(0).getNbCommandesRecues() == 0, "champ transient remis a 0");
        verifier(donnees.getRestaurants().get(0).getNoteMoyenne() == 4.5, "la note 4.5 a ete conservee");
        capturer(accueil, accueil.getTitle(), "q4_ser_charge");

        File sauvegarde = new File("data/restaurants_ok.tmp");
        java.nio.file.Files.move(ser.toPath(), sauvegarde.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        FileWriter faux = new FileWriter(Donnees.FICHIER_SER);
        faux.write("ceci n'est pas un fichier serialise");
        faux.close();
        accueil.getBoutonCharger().doClick();
        verifier(Messages.derniere.getTitle().equals("Chargement impossible"), "IOException attrapee (.ser corrompu)");
        verifier(accueil.getListe().getModel().getSize() == 2, "liste intacte apres l'erreur");
        capturerBoite("q4_exc_ser");
        java.nio.file.Files.move(sauvegarde.toPath(), ser.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);


        System.out.println("--- Historique plein");
        boolean leve = false;
        for (int i = 0; i < 25 && !leve; i++) {
            accueil.getListe().setSelectedIndex(1);
            Messages.dernierTexte = "";
            accueil.getBoutonMenu().doClick();
            if (Messages.dernierTexte.contains("historique")) {
                leve = true;
            } else {
                fenetre(FenetreMenu.class).getBoutonRetour().doClick();
            }
        }
        verifier(leve, "CapaciteDepasseeException attrapee (21e commande)");
    }
}
