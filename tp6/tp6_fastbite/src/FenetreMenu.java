import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

/*
 TP6 - Page 2 : menu d'un restaurant et panier du client.
 Listeners : ajouter un plat, valider la commande, enregistrer / charger le menu (.txt), retour.
 */
public class FenetreMenu extends JFrame implements ActionListener {
    private final FenetreAccueil accueil;
    private final Donnees donnees;
    private final int indice;                 // position du restaurant dans la liste
    private Restaurant restaurant;
    private Commande commande;                // commande en cours (le panier)

    // Tableau du menu : le modele contient les donnees, la JTable les affiche
    private final DefaultTableModel modeleMenu = new DefaultTableModel(
            new String[]{"Plat", "Prix (EUR)", "Description", "Allergenes"}, 0) {
        @Override
        public boolean isCellEditable(int ligne, int colonne) { return false; }   // lecture seule
    };
    private final JTable tableMenu = new JTable(modeleMenu);
    private final DefaultListModel<String> modelePanier = new DefaultListModel<String>();
    private final JList<String> listePanier = new JList<String>(modelePanier);
    private final JLabel total = new JLabel("Total : 0.0 EUR");
    private final JLabel etat = new JLabel(" ");

    private final JButton boutonAjouter = new JButton("Ajouter au panier");
    private final JButton boutonValider = new JButton("Valider la commande");
    private final JButton boutonEnregistrer = new JButton("Enregistrer le menu (.txt)");
    private final JButton boutonLire = new JButton("Charger un menu (.txt)");
    private final JButton boutonRetour = new JButton("Retour");

    /*
     Le constructeur PROPAGE CapaciteDepasseeException : passerCommande() la leve si
     l'historique du client est plein. C'est FenetreAccueil qui l'attrape.
     */
    public FenetreMenu(FenetreAccueil accueil, Donnees donnees, int indice) throws CapaciteDepasseeException {
        super("FastBite - Menu");
        this.accueil = accueil;
        this.donnees = donnees;
        this.indice = indice;
        this.restaurant = donnees.getRestaurants().get(indice);
        this.commande = donnees.getClient().passerCommande(restaurant, donnees.numeroSuivant());
        setTitle("FastBite - Menu de " + restaurant.getNom());

        // --- NORD
        JLabel titre = new JLabel(restaurant.getNom() + "  -  " + restaurant.getAdresse(), JLabel.CENTER);
        titre.setFont(new Font("SansSerif", Font.BOLD, 20));

        // --- CENTRE : menu a gauche, panier a droite
        tableMenu.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableMenu.setRowHeight(24);
        int[] largeurs = {150, 95, 200, 150};           // largeur de depart de chaque colonne
        for (int c = 0; c < largeurs.length; c++) {
            tableMenu.getColumnModel().getColumn(c).setPreferredWidth(largeurs[c]);
        }
        JScrollPane gauche = new JScrollPane(tableMenu);
        gauche.setBorder(BorderFactory.createTitledBorder("Menu"));

        total.setFont(new Font("SansSerif", Font.BOLD, 16));
        JPanel droite = new JPanel(new BorderLayout(4, 4));
        droite.setBorder(BorderFactory.createTitledBorder("Panier - commande #" + commande.getNumero()));
        droite.add(new JScrollPane(listePanier), BorderLayout.CENTER);
        droite.add(total, BorderLayout.SOUTH);

        JPanel centre = new JPanel(new GridLayout(1, 2, 10, 0));
        centre.add(gauche);
        centre.add(droite);

        // --- SUD : boutons (FlowLayout = a la suite, de gauche a droite) puis barre d'etat
        JPanel boutons = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 4));
        boutons.add(boutonAjouter);
        boutons.add(boutonValider);
        boutons.add(boutonEnregistrer);
        boutons.add(boutonLire);
        boutons.add(boutonRetour);
        JPanel sud = new JPanel(new GridLayout(2, 1));
        sud.add(boutons);
        sud.add(etat);

        JPanel contenu = new JPanel(new BorderLayout(10, 10));
        contenu.setBorder(BorderFactory.createEmptyBorder(12, 12, 8, 12));
        contenu.add(titre, BorderLayout.NORTH);
        contenu.add(centre, BorderLayout.CENTER);
        contenu.add(sud, BorderLayout.SOUTH);
        setContentPane(contenu);

        // --- LISTENERS
        boutonAjouter.addActionListener(this);
        boutonValider.addActionListener(this);
        boutonEnregistrer.addActionListener(this);
        boutonLire.addActionListener(this);
        boutonRetour.addActionListener(this);

        remplirMenu();
        setSize(1000, 430);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    // Recopie le menu du restaurant dans le tableau
    private void remplirMenu() {
        modeleMenu.setRowCount(0);
        for (int i = 0; i < restaurant.getNbPlats(); i++) {
            Plat p = restaurant.getMenu()[i];
            modeleMenu.addRow(new Object[]{p.getNom(), p.getPrix(), p.getDescription(),
                    String.join(", ", p.getAllergenes())});
        }
    }

    // Recopie les plats de la commande dans la liste du panier et recalcule le total
    private void rafraichirPanier() {
        modelePanier.clear();
        for (Plat p : commande.getPlats()) {
            modelePanier.addElement(p.toString());
        }
        total.setText("Total : " + commande.calculerTotal() + " EUR");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == boutonAjouter) {
            ajouterAuPanier();
        } else if (source == boutonValider) {
            validerCommande();
        } else if (source == boutonEnregistrer) {
            enregistrerMenu();
        } else if (source == boutonLire) {
            // JFileChooser : boite standard de choix de fichier (vue au cours 6)
            JFileChooser choix = new JFileChooser(Donnees.DOSSIER_DATA);
            if (choix.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                chargerMenu(choix.getSelectedFile().getPath());
            }
        } else if (source == boutonRetour) {
            retour();
        }
    }

    // Clic « Ajouter au panier » : le plat selectionne dans le tableau rejoint la commande
    private void ajouterAuPanier() {
        int ligne = tableMenu.getSelectedRow();
        if (ligne < 0) {
            Messages.erreur(this, "Aucun plat", "Selectionnez d'abord un plat dans le menu.");
            return;
        }
        try {
            commande.ajouterPlat(restaurant.getMenu()[ligne]);
            rafraichirPanier();                                   // mise a jour de l'affichage
            etat.setText(restaurant.getMenu()[ligne].getNom() + " ajoute au panier.");
        } catch (PlatIndisponibleException ex) {
            Messages.erreur(this, "Plat indisponible", ex.getMessage());
        }
    }

    // Clic « Valider » : CommandeVideException si le panier est vide, sinon page 3
    private void validerCommande() {
        try {
            commande.valider();
            restaurant.recevoirCommande(commande);
            FenetreCommande page = new FenetreCommande(accueil, donnees, commande);
            page.setVisible(true);
            dispose();                                            // on ferme la page 2
        } catch (CommandeVideException ex) {
            Messages.erreur(this, "Commande vide", ex.getMessage());
        }
    }

    // TP5 Q2 reutilise : ecriture du menu dans un fichier texte (separateur ;)
    private void enregistrerMenu() {
        String fichier = Donnees.fichierMenu(restaurant);
        try {
            restaurant.sauvegarderMenu(fichier);
            etat.setText("Menu enregistre dans " + fichier + " (" + restaurant.getNbPlats() + " plats).");
        } catch (IOException ex) {
            Messages.erreur(this, "Enregistrement impossible", ex.getMessage());
        }
    }

    /*
     TP5 Q2 reutilise : lecture d'un fichier texte. On charge dans un restaurant NEUF :
     si le fichier est incorrect (FormatFichierException), le menu affiche n'est pas touche.
     Methode publique pour pouvoir la tester sans passer par la boite de choix de fichier.
     */
    public void chargerMenu(String fichier) {
        Restaurant relu = new Restaurant(restaurant.getNom(), restaurant.getAdresse(), restaurant.getZoneLivraison());
        try {
            relu.chargerMenu(fichier);
            donnees.getRestaurants().set(indice, relu);           // remplace l'ancien restaurant
            restaurant = relu;
            commande = new Commande(commande.getNumero(), donnees.getClient(), relu);   // panier vide
            remplirMenu();
            rafraichirPanier();
            etat.setText(relu.getNbPlats() + " plat(s) lu(s) depuis " + fichier);
        } catch (FormatFichierException ex) {
            Messages.erreur(this, "Fichier incorrect", ex.getMessage());
        } catch (IOException ex) {
            Messages.erreur(this, "Lecture impossible", ex.getMessage());
        } catch (CapaciteDepasseeException ex) {
            Messages.erreur(this, "Menu trop long", ex.getMessage());
        }
    }

    // Navigation : retour a la page 1
    private void retour() {
        accueil.rafraichir();
        accueil.setVisible(true);
        dispose();
    }

    // Accesseurs utilises par les tests
    public JTable getTable()               { return tableMenu; }
    public JButton getBoutonAjouter()      { return boutonAjouter; }
    public JButton getBoutonValider()      { return boutonValider; }
    public JButton getBoutonEnregistrer()  { return boutonEnregistrer; }
    public JButton getBoutonRetour()       { return boutonRetour; }
    public Commande getCommande()          { return commande; }
    public String getEtat()                { return etat.getText(); }
    public String getTotal()               { return total.getText(); }
}
