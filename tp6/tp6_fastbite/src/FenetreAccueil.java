import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

/*
 TP6 - Page 1 : accueil. Liste des restaurants + sauvegarde / chargement (.ser).
 La classe HERITE de JFrame (c'est une fenetre) et IMPLEMENTE ActionListener :
 elle ecoute elle-meme les clics de ses boutons dans actionPerformed().
 */
public class FenetreAccueil extends JFrame implements ActionListener {
    private final Donnees donnees;

    // Composants dont on a besoin dans les listeners (donc en attributs)
    private final DefaultListModel<String> modeleListe = new DefaultListModel<String>();
    private final JList<String> listeRestaurants = new JList<String>(modeleListe);
    private final JButton boutonMenu = new JButton("Voir le menu");
    private final JButton boutonSauver = new JButton("Sauvegarder (.ser)");
    private final JButton boutonCharger = new JButton("Charger (.ser)");
    private final JButton boutonQuitter = new JButton("Quitter");
    private final JLabel etat = new JLabel("Choisissez un restaurant puis « Voir le menu ».");

    public FenetreAccueil(Donnees donnees) {
        super("FastBite - Restaurants");          // titre de la fenetre
        this.donnees = donnees;

        // --- NORD : titre et client connecte
        JLabel titre = new JLabel("FastBite", JLabel.CENTER);
        titre.setFont(new Font("SansSerif", Font.BOLD, 28));
        titre.setForeground(new Color(232, 93, 4));
        JLabel sousTitre = new JLabel("Connecte : " + donnees.getClient().getNom()
                + "  -  livraison : " + donnees.getClient().getAdresseLivraison(), JLabel.CENTER);
        JPanel nord = new JPanel(new GridLayout(2, 1));
        nord.add(titre);
        nord.add(sousTitre);

        // --- CENTRE : liste des restaurants (une seule selection a la fois)
        listeRestaurants.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listeRestaurants.setFont(new Font("SansSerif", Font.PLAIN, 16));
        JScrollPane centre = new JScrollPane(listeRestaurants);
        centre.setBorder(BorderFactory.createTitledBorder("Restaurants disponibles"));

        // --- EST : colonne de boutons (GridLayout = cases de meme taille)
        JPanel est = new JPanel(new GridLayout(4, 1, 0, 8));
        est.add(boutonMenu);
        est.add(boutonSauver);
        est.add(boutonCharger);
        est.add(boutonQuitter);

        // --- SUD : barre d'etat (resultat de la derniere action)
        etat.setBorder(BorderFactory.createEmptyBorder(6, 4, 2, 4));

        // BorderLayout : 5 zones (NORTH, CENTER, EAST, SOUTH, WEST)
        JPanel contenu = new JPanel(new BorderLayout(10, 10));
        contenu.setBorder(BorderFactory.createEmptyBorder(12, 12, 8, 12));
        contenu.add(nord, BorderLayout.NORTH);
        contenu.add(centre, BorderLayout.CENTER);
        contenu.add(est, BorderLayout.EAST);
        contenu.add(etat, BorderLayout.SOUTH);
        setContentPane(contenu);

        // --- LISTENERS : la fenetre s'abonne aux clics des 4 boutons
        boutonMenu.addActionListener(this);
        boutonSauver.addActionListener(this);
        boutonCharger.addActionListener(this);
        boutonQuitter.addActionListener(this);

        rafraichir();
        setSize(640, 400);
        setLocationRelativeTo(null);              // centre de l'ecran
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    // Recopie la liste des restaurants (modele) dans la JList (vue)
    public void rafraichir() {
        modeleListe.clear();
        for (Restaurant r : donnees.getRestaurants()) {
            modeleListe.addElement(r.getNom() + "   -   " + r.getZoneLivraison()
                    + "   -   " + r.getNbPlats() + " plat(s)");
        }
    }

    /*
     Appelee par Swing a chaque clic. e.getSource() dit QUEL bouton a ete clique.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == boutonMenu) {
            ouvrirMenu();
        } else if (source == boutonSauver) {
            sauvegarder();
        } else if (source == boutonCharger) {
            charger();
        } else if (source == boutonQuitter) {
            dispose();
        }
    }

    // Navigation : page 1 -> page 2 (menu du restaurant selectionne)
    private void ouvrirMenu() {
        int i = listeRestaurants.getSelectedIndex();
        if (i < 0) {
            Messages.erreur(this, "Aucun restaurant", "Selectionnez d'abord un restaurant dans la liste.");
            return;
        }
        try {
            FenetreMenu page = new FenetreMenu(this, donnees, i);
            page.setVisible(true);
            setVisible(false);                    // on cache l'accueil
        } catch (CapaciteDepasseeException ex) {
            // l'historique du client (tableau de 20 commandes) est plein
            Messages.erreur(this, "Commande impossible", ex.getMessage());
        }
    }

    // TP5 Q3 reutilise : serialisation de la liste dans un fichier binaire
    private void sauvegarder() {
        try {
            Restaurant.sauvegarderRestaurants(donnees.getRestaurants(), Donnees.FICHIER_SER);
            etat.setText(donnees.getRestaurants().size() + " restaurant(s) serialise(s) dans " + Donnees.FICHIER_SER);
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
    public JList<String> getListe()      { return listeRestaurants; }
    public JButton getBoutonMenu()       { return boutonMenu; }
    public JButton getBoutonSauver()     { return boutonSauver; }
    public JButton getBoutonCharger()    { return boutonCharger; }
    public String getEtat()              { return etat.getText(); }
    public void setEtat(String texte)    { etat.setText(texte); }
}
