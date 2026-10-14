package vue;

import modele.*;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/*
 TP7 - VUE (page 1 : accueil). Une carte par restaurant + sauvegarde / chargement (.ser)
 + notre livreur et le suivi de la commande en cours.
 Regles du MVC respectees ici :
  - la vue LIT le modele (Donnees) pour s'afficher, elle ne le MODIFIE jamais ;
  - elle ne contient aucune regle metier, aucun try/catch metier, n'ouvre aucune autre fenetre ;
  - elle ne decide pas de ce qu'un clic fait : le controleur lui est fourni (setControleur)
    et s'abonne a ses boutons ;
  - elle est prevenue par le modele (EcouteurModele) et se redessine toute seule.
 */
public class FenetreAccueil extends JFrame implements EcouteurModele {
    private final Donnees donnees;                 // lu pour afficher, jamais modifie

    private final JPanel grille = Style.groupe(new GridLayout(0, 3, 16, 16));     // 3 cartes par ligne
    private final ArrayList<JButton> boutonsMenu = new ArrayList<JButton>();      // un bouton par carte
    private final JButton boutonSauver = Style.boutonContour("Sauvegarder (.ser)");
    private final JButton boutonCharger = Style.boutonContour("Charger (.ser)");
    private final JButton boutonQuitter = Style.boutonContour("Quitter");
    private final JButton boutonSuivi = Style.boutonOrange("Suivre ma commande");
    private final JLabel compteur = Style.texte(" ", Font.PLAIN, 14, Style.GRIS);
    private final JLabel ligneLivreur = Style.texte(" ", Font.PLAIN, 13, Style.GRIS);
    private final JLabel etat = Style.texte("Choisissez un restaurant.", Font.PLAIN, 13, Style.GRIS);

    private ActionListener controleur;             // fourni par le controleur apres la construction

    public FenetreAccueil(Donnees donnees) {
        super("FastBite - Restaurants");
        this.donnees = donnees;

        // --- NORD : barre blanche (logo, adresse de livraison, boutons de fichier)
        JPanel droite = Style.groupe(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        droite.add(boutonSauver);
        droite.add(boutonCharger);
        droite.add(boutonQuitter);
        JPanel nord = new JPanel(new BorderLayout(24, 0));
        nord.setBackground(Style.BLANC);
        nord.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Style.GRIS_CLAIR),
                BorderFactory.createEmptyBorder(12, 22, 12, 22)));
        nord.add(Style.logo(26), BorderLayout.WEST);
        nord.add(Style.texte("Livrer à : " + donnees.getClient().getAdresseLivraison()
                + "   ·   " + donnees.getClient().getNom(), Font.PLAIN, 14, Style.GRIS), BorderLayout.CENTER);
        nord.add(droite, BorderLayout.EAST);

        // --- CENTRE : titre puis grille de cartes
        JPanel titres = Style.groupe(new GridLayout(2, 1, 0, 2));
        titres.add(Style.texte("Restaurants près de chez vous", Font.BOLD, 24, Style.NOIR));
        titres.add(compteur);
        JPanel enveloppe = Style.groupe(new BorderLayout());
        enveloppe.add(grille, BorderLayout.NORTH);
        JScrollPane defilement = new JScrollPane(enveloppe);
        defilement.setBorder(BorderFactory.createEmptyBorder());
        defilement.setViewportBorder(null);
        defilement.setOpaque(false);
        defilement.getViewport().setOpaque(false);
        defilement.getVerticalScrollBar().setUnitIncrement(16);

        // --- SUD : notre livreur, la commande en cours, puis la barre d'etat
        Style.Carte carteLivreur = new Style.Carte(new BorderLayout(14, 0));
        JPanel textesLivreur = Style.groupe(new GridLayout(2, 1, 0, 2));
        textesLivreur.add(Style.texte("Votre livreur FastBite : " + donnees.getLivreur().getNom(), Font.BOLD, 15, Style.NOIR));
        textesLivreur.add(ligneLivreur);
        ligneLivreur.setIcon(new Style.Etoile(13));
        carteLivreur.add(new Style.Avatar(donnees.getLivreur().getNom(), Style.NOIR, 42), BorderLayout.WEST);
        carteLivreur.add(textesLivreur, BorderLayout.CENTER);
        carteLivreur.add(boutonSuivi, BorderLayout.EAST);
        JPanel sud = Style.groupe(new BorderLayout(0, 8));
        sud.add(carteLivreur, BorderLayout.CENTER);
        sud.add(etat, BorderLayout.SOUTH);

        JPanel centre = new JPanel(new BorderLayout(0, 14));
        centre.setBackground(Style.FOND);
        centre.setBorder(BorderFactory.createEmptyBorder(18, 22, 10, 22));
        centre.add(titres, BorderLayout.NORTH);
        centre.add(defilement, BorderLayout.CENTER);
        centre.add(sud, BorderLayout.SOUTH);
        JPanel contenu = new JPanel(new BorderLayout());
        contenu.add(nord, BorderLayout.NORTH);
        contenu.add(centre, BorderLayout.CENTER);
        setContentPane(contenu);

        // PAS de listener ici : c'est le controleur qui s'abonne aux boutons (setControleur)
        rafraichir();
        setSize(1000, 660);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    /*
     Le controleur se fait connaitre de la vue : elle le branche sur tous ses boutons, y compris
     les boutons « Voir le menu » recrees a chaque rafraichissement.
     */
    public void setControleur(ActionListener nouveau) {
        if (controleur != null) {
            for (JButton b : tousLesBoutons()) b.removeActionListener(controleur);
        }
        controleur = nouveau;
        for (JButton b : tousLesBoutons()) b.addActionListener(controleur);
    }

    private ArrayList<JButton> tousLesBoutons() {
        ArrayList<JButton> liste = new ArrayList<JButton>(boutonsMenu);
        liste.add(boutonSauver);
        liste.add(boutonCharger);
        liste.add(boutonQuitter);
        liste.add(boutonSuivi);
        return liste;
    }

    // Une carte = banniere + nom + note / nombre de plats / adresse + bouton « Voir le menu »
    private JPanel carteRestaurant(Restaurant r, int indice) {
        JLabel details = Style.texte(Style.note(r) + "   ·   " + r.getNbPlats() + " plat(s)   ·   "
                + r.getAdresse(), Font.PLAIN, 13, Style.GRIS);
        details.setIcon(new Style.Etoile(13));
        JPanel infos = Style.groupe(new GridLayout(2, 1, 0, 2));
        infos.add(Style.texte(r.getNom(), Font.BOLD, 17, Style.NOIR));
        infos.add(details);

        JButton bouton = Style.boutonNoir("Voir le menu");
        if (controleur != null) bouton.addActionListener(controleur);
        boutonsMenu.add(bouton);                  // sa position dans la liste = numero du restaurant

        Style.Carte carte = new Style.Carte(new BorderLayout(0, 10));
        carte.add(new Style.Banniere(r.getNom(), r.getZoneLivraison(), 120, false, indice), BorderLayout.NORTH);
        carte.add(infos, BorderLayout.CENTER);
        carte.add(bouton, BorderLayout.SOUTH);
        return carte;
    }

    // Reconstruit l'affichage (vue) a partir des donnees (modele)
    public void rafraichir() {
        grille.removeAll();
        boutonsMenu.clear();
        ArrayList<Restaurant> liste = donnees.getRestaurants();
        for (int i = 0; i < liste.size(); i++) {
            grille.add(carteRestaurant(liste.get(i), i));
        }
        compteur.setText(liste.size() + " restaurant(s) livrent en ce moment");

        Livreur l = donnees.getLivreur();
        Commande suivie = donnees.getCommandeSuivie();
        ligneLivreur.setText(Style.note(l) + "   ·   " + l.getVehicule() + "   ·   "
                + (suivie == null ? "disponible" : "en livraison (commande #" + suivie.getNumero() + ")"));
        boutonSuivi.setVisible(suivie != null);
        grille.revalidate();
        repaint();
    }

    // Observer : le modele a change, la vue se redessine
    @Override
    public void modeleModifie() {
        rafraichir();
    }

    // Fermer la fenetre : le controleur le demande, la vue sait comment
    public void fermer() {
        dispose();
    }

    // Boite d'erreur : la vue sait AFFICHER une erreur, c'est le controleur qui decide quand
    public void erreur(String titre, String message) {
        Messages.erreur(this, titre, message);
    }

    // Accesseurs : le controleur (et les tests) accedent aux boutons et au texte d'etat
    public ArrayList<JButton> getBoutonsMenu() { return boutonsMenu; }
    public JButton getBoutonSauver()           { return boutonSauver; }
    public JButton getBoutonCharger()          { return boutonCharger; }
    public JButton getBoutonQuitter()          { return boutonQuitter; }
    public JButton getBoutonSuivi()            { return boutonSuivi; }
    public String getEtat()                    { return etat.getText(); }
    public void setEtat(String texte)          { etat.setText(texte); }
}
