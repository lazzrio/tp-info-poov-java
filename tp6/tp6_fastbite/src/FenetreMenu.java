import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/*
 TP6 - Page 2 : menu d'un restaurant (une carte par plat) et panier du client.
 Listeners : ajouter un plat, commander, enregistrer / charger le menu (.txt), retour.
 */
public class FenetreMenu extends JFrame implements ActionListener {
    private final FenetreAccueil accueil;
    private final Donnees donnees;
    private final int indice;                 // position du restaurant dans la liste
    private Restaurant restaurant;
    private Commande commande;                // commande en cours (le panier)

    private final JPanel cartesPlats = Style.groupe(new GridLayout(0, 1, 0, 10));     // une carte par ligne
    private final ArrayList<JButton> boutonsAjouter = new ArrayList<JButton>();       // un bouton par plat
    private final JPanel lignesPanier = Style.groupe(new GridLayout(0, 1, 0, 8));
    private final JLabel total = Style.texte("0,00 €", Font.BOLD, 18, Style.NOIR);
    private final JLabel etat = Style.texte(" ", Font.PLAIN, 13, Style.GRIS);

    private final JButton boutonValider = Style.boutonVert("Commander");
    private final JButton boutonEnregistrer = Style.boutonContour("Enregistrer le menu (.txt)");
    private final JButton boutonLire = Style.boutonContour("Charger un menu (.txt)");
    private final JButton boutonRetour = Style.boutonContour("Retour aux restaurants");

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

        // --- NORD : banniere du restaurant, puis note et adresse
        JLabel details = Style.texte(Style.note(restaurant) + "   ·   " + restaurant.getAdresse()
                + "   ·   livraison à " + donnees.getClient().getAdresseLivraison(), Font.PLAIN, 14, Style.GRIS);
        details.setIcon(new Style.Etoile(14));
        JPanel nord = Style.groupe(new BorderLayout(0, 10));
        nord.add(new Style.Banniere(restaurant.getNom(), restaurant.getZoneLivraison(), 120, true, indice), BorderLayout.CENTER);
        nord.add(details, BorderLayout.SOUTH);

        // --- CENTRE : les plats (avec defilement)
        JPanel enveloppe = Style.groupe(new BorderLayout());
        enveloppe.add(cartesPlats, BorderLayout.NORTH);
        JScrollPane defilement = new JScrollPane(enveloppe);
        defilement.setBorder(BorderFactory.createEmptyBorder());
        defilement.setViewportBorder(null);
        defilement.setOpaque(false);
        defilement.getViewport().setOpaque(false);
        defilement.getVerticalScrollBar().setUnitIncrement(16);

        // --- EST : le panier (carte blanche de largeur fixe)
        JPanel titrePanier = Style.groupe(new GridLayout(2, 1, 0, 2));
        titrePanier.add(Style.texte("Votre panier", Font.BOLD, 19, Style.NOIR));
        titrePanier.add(Style.texte("Commande #" + commande.getNumero() + "   ·   " + donnees.getClient().getNom(),
                Font.PLAIN, 13, Style.GRIS));
        JPanel hautPanier = Style.groupe(new BorderLayout());
        hautPanier.add(lignesPanier, BorderLayout.NORTH);
        JPanel ligneTotal = Style.groupe(new BorderLayout());
        ligneTotal.add(Style.texte("Total", Font.BOLD, 18, Style.NOIR), BorderLayout.CENTER);
        ligneTotal.add(total, BorderLayout.EAST);
        JPanel basPanier = Style.groupe(new GridLayout(2, 1, 0, 10));
        basPanier.add(ligneTotal);
        basPanier.add(boutonValider);
        Style.Carte panier = new Style.Carte(new BorderLayout(0, 14));
        panier.setPreferredSize(new Dimension(330, 100));
        panier.add(titrePanier, BorderLayout.NORTH);
        panier.add(hautPanier, BorderLayout.CENTER);
        panier.add(basPanier, BorderLayout.SOUTH);

        // --- SUD : boutons secondaires (FlowLayout = a la suite, de gauche a droite) et barre d'etat
        JPanel boutons = Style.groupe(new FlowLayout(FlowLayout.LEFT, 8, 0));
        boutons.add(boutonRetour);
        boutons.add(boutonEnregistrer);
        boutons.add(boutonLire);
        JPanel sud = Style.groupe(new BorderLayout(12, 0));
        sud.add(boutons, BorderLayout.WEST);
        sud.add(etat, BorderLayout.CENTER);

        JPanel contenu = new JPanel(new BorderLayout(16, 14));
        contenu.setBackground(Style.FOND);
        contenu.setBorder(BorderFactory.createEmptyBorder(16, 22, 12, 22));
        contenu.add(nord, BorderLayout.NORTH);
        contenu.add(defilement, BorderLayout.CENTER);
        contenu.add(panier, BorderLayout.EAST);
        contenu.add(sud, BorderLayout.SOUTH);
        setContentPane(contenu);

        // --- LISTENERS
        boutonValider.addActionListener(this);
        boutonEnregistrer.addActionListener(this);
        boutonLire.addActionListener(this);
        boutonRetour.addActionListener(this);

        remplirMenu();
        rafraichirPanier();
        setSize(1000, 660);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    // Une carte par plat du restaurant : nom, description, allergenes, prix, bouton « Ajouter »
    private void remplirMenu() {
        cartesPlats.removeAll();
        boutonsAjouter.clear();
        for (int i = 0; i < restaurant.getNbPlats(); i++) {
            Plat p = restaurant.getMenu()[i];
            String allergenes = (p.getAllergenes().length == 0) ? "Sans allergène déclaré"
                    : "Allergènes : " + String.join(", ", p.getAllergenes());
            JPanel textes = Style.groupe(new GridLayout(3, 1, 0, 2));
            textes.add(Style.texte(p.getNom(), Font.BOLD, 16, Style.NOIR));
            textes.add(Style.texte(p.getDescription(), Font.PLAIN, 13, Style.GRIS));
            textes.add(Style.texte(allergenes, Font.ITALIC, 12, Style.GRIS));

            JButton bouton = Style.boutonOrange("Ajouter");
            bouton.addActionListener(this);
            boutonsAjouter.add(bouton);           // position dans la liste = numero du plat dans le menu
            JLabel prix = Style.texte(Style.prix(p.getPrix()), Font.BOLD, 16, Style.NOIR);
            prix.setHorizontalAlignment(JLabel.RIGHT);
            JPanel droite = Style.groupe(new GridLayout(2, 1, 0, 6));
            droite.add(prix);
            droite.add(bouton);

            Style.Carte carte = new Style.Carte(new BorderLayout(14, 0));
            carte.add(textes, BorderLayout.CENTER);
            carte.add(droite, BorderLayout.EAST);
            cartesPlats.add(carte);
        }
        cartesPlats.revalidate();
        repaint();
    }

    // Recopie les plats de la commande dans le panier et recalcule le total
    private void rafraichirPanier() {
        lignesPanier.removeAll();
        if (commande.getNbPlats() == 0) {
            lignesPanier.add(Style.texte("Votre panier est vide.", Font.PLAIN, 14, Style.GRIS));
            lignesPanier.add(Style.texte("Ajoutez un plat pour commencer.", Font.PLAIN, 13, Style.GRIS));
        }
        for (Plat p : commande.getPlats()) {
            lignesPanier.add(Style.ligne(p.getNom(), Style.prix(p.getPrix()), Font.PLAIN, 14));
        }
        total.setText(Style.prix(commande.calculerTotal()));
        lignesPanier.revalidate();
        repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        int numero = boutonsAjouter.indexOf(source);      // -1 si ce n'est pas un bouton « Ajouter »
        if (numero >= 0) {
            ajouterAuPanier(numero);
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

    // Clic « Ajouter » sur la carte d'un plat : ce plat rejoint la commande
    private void ajouterAuPanier(int numero) {
        Plat plat = restaurant.getMenu()[numero];
        try {
            commande.ajouterPlat(plat);
            rafraichirPanier();                                   // mise a jour de l'affichage
            etat.setText(plat.getNom() + " ajouté au panier.");
        } catch (PlatIndisponibleException ex) {
            Messages.erreur(this, "Plat indisponible", ex.getMessage());
        }
    }

    // Clic « Commander » : CommandeVideException si le panier est vide, sinon page 3
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
            etat.setText("Menu enregistré dans " + fichier + " (" + restaurant.getNbPlats() + " plats).");
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
    public ArrayList<JButton> getBoutonsAjouter() { return boutonsAjouter; }
    public JButton getBoutonValider()             { return boutonValider; }
    public JButton getBoutonEnregistrer()         { return boutonEnregistrer; }
    public JButton getBoutonRetour()              { return boutonRetour; }
    public Commande getCommande()                 { return commande; }
    public String getEtat()                       { return etat.getText(); }
    public String getTotal()                      { return total.getText(); }
}
