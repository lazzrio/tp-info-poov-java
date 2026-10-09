import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/*
 TP6 - Page 1 : accueil. Une carte par restaurant + sauvegarde / chargement (.ser)
 + notre livreur et le suivi de la commande en cours.
 La classe HERITE de JFrame (c'est une fenetre) et IMPLEMENTE ActionListener :
 elle ecoute elle-meme les clics de ses boutons dans actionPerformed().
 */
public class FenetreAccueil extends JFrame implements ActionListener {
    private final Donnees donnees;

    // Composants dont on a besoin dans les listeners (donc en attributs)
    private final JPanel grille = Style.groupe(new GridLayout(0, 3, 16, 16));     // 3 cartes par ligne
    private final ArrayList<JButton> boutonsMenu = new ArrayList<JButton>();      // un bouton par carte
    private final JButton boutonSauver = Style.boutonContour("Sauvegarder (.ser)");
    private final JButton boutonCharger = Style.boutonContour("Charger (.ser)");
    private final JButton boutonQuitter = Style.boutonContour("Quitter");
    private final JButton boutonSuivi = Style.boutonOrange("Suivre ma commande");
    private final JLabel compteur = Style.texte(" ", Font.PLAIN, 14, Style.GRIS);
    private final JLabel ligneLivreur = Style.texte(" ", Font.PLAIN, 13, Style.GRIS);
    private final JLabel etat = Style.texte("Choisissez un restaurant.", Font.PLAIN, 13, Style.GRIS);

    public FenetreAccueil(Donnees donnees) {
        super("FastBite - Restaurants");          // titre de la fenetre
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

        // --- CENTRE : titre puis grille de cartes (GridLayout = cases de meme taille)
        JPanel titres = Style.groupe(new GridLayout(2, 1, 0, 2));
        titres.add(Style.texte("Restaurants près de chez vous", Font.BOLD, 24, Style.NOIR));
        titres.add(compteur);
        JPanel enveloppe = Style.groupe(new BorderLayout());
        enveloppe.add(grille, BorderLayout.NORTH);            // au nord : les cartes gardent leur hauteur
        JScrollPane defilement = new JScrollPane(enveloppe);  // barre de defilement s'il y a beaucoup de restaurants
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

        // BorderLayout : 5 zones (NORTH, CENTER, EAST, SOUTH, WEST)
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

        // --- LISTENERS : la fenetre s'abonne aux clics de ses boutons
        boutonSauver.addActionListener(this);
        boutonCharger.addActionListener(this);
        boutonQuitter.addActionListener(this);
        boutonSuivi.addActionListener(this);

        rafraichir();
        setSize(1000, 660);
        setLocationRelativeTo(null);              // centre de l'ecran
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
        bouton.addActionListener(this);           // meme listener pour toutes les cartes
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
        boutonSuivi.setVisible(suivie != null);   // le bouton n'apparait que s'il y a une livraison en cours
        grille.revalidate();                      // recalcule la mise en page
        repaint();
    }

    /*
     Appelee par Swing a chaque clic. e.getSource() dit QUEL bouton a ete clique.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        int indice = boutonsMenu.indexOf(source);     // -1 si ce n'est pas un bouton « Voir le menu »
        if (indice >= 0) {
            ouvrirMenu(indice);
        } else if (source == boutonSauver) {
            sauvegarder();
        } else if (source == boutonCharger) {
            charger();
        } else if (source == boutonSuivi) {
            ouvrirSuivi();
        } else if (source == boutonQuitter) {
            dispose();
        }
    }

    // Navigation : page 1 -> page 2 (menu du restaurant de la carte cliquee)
    private void ouvrirMenu(int indice) {
        try {
            FenetreMenu page = new FenetreMenu(this, donnees, indice);
            page.setVisible(true);
            setVisible(false);                    // on cache l'accueil
        } catch (CapaciteDepasseeException ex) {
            // l'historique du client (tableau de 20 commandes) est plein
            Messages.erreur(this, "Commande impossible", ex.getMessage());
        }
    }

    // Navigation : page 1 -> page 3 (commande en cours de livraison)
    private void ouvrirSuivi() {
        FenetreCommande page = new FenetreCommande(this, donnees, donnees.getCommandeSuivie());
        page.setVisible(true);
        setVisible(false);
    }

    // TP5 Q3 reutilise : serialisation de la liste dans un fichier binaire
    private void sauvegarder() {
        try {
            Restaurant.sauvegarderRestaurants(donnees.getRestaurants(), Donnees.FICHIER_SER);
            etat.setText(donnees.getRestaurants().size() + " restaurant(s) sérialisé(s) dans " + Donnees.FICHIER_SER);
        } catch (IOException ex) {
            Messages.erreur(this, "Sauvegarde impossible", "Ecriture de " + Donnees.FICHIER_SER + " : " + ex.getMessage());
        }
    }

    // TP5 Q3 reutilise : deserialisation, puis mise a jour de l'affichage
    private void charger() {
        try {
            ArrayList<Restaurant> lus = Restaurant.chargerRestaurants(Donnees.FICHIER_SER);
            donnees.setRestaurants(lus);
            rafraichir();
            etat.setText(lus.size() + " restaurant(s) relu(s) depuis " + Donnees.FICHIER_SER);
        } catch (IOException ex) {
            // fichier absent, illisible ou qui n'est pas un flux d'objets Java
            Messages.erreur(this, "Chargement impossible", "Lecture de " + Donnees.FICHIER_SER + " : " + ex.getMessage());
        } catch (ClassNotFoundException ex) {
            Messages.erreur(this, "Chargement impossible", "Classe inconnue dans le fichier : " + ex.getMessage());
        }
    }

    // Accesseurs utilises par les tests (simuler un clic, lire l'etat)
    public ArrayList<JButton> getBoutonsMenu() { return boutonsMenu; }
    public JButton getBoutonSauver()           { return boutonSauver; }
    public JButton getBoutonCharger()          { return boutonCharger; }
    public JButton getBoutonSuivi()            { return boutonSuivi; }
    public String getEtat()                    { return etat.getText(); }
    public void setEtat(String texte)          { etat.setText(texte); }
}
