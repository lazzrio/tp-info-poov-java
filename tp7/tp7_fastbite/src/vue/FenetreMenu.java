package vue;

import modele.*;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/*
 TP7 - VUE (page 2 : menu d'un restaurant et panier du client).
 Elle affiche le restaurant et la commande qu'on lui donne ; elle ne cree pas la commande, ne
 l'ajoute pas, ne la valide pas : tout cela est fait par ControleurMenu via le modele.
 */
public class FenetreMenu extends JFrame implements EcouteurModele {
    private final Donnees donnees;            // lu pour le client
    private Restaurant restaurant;            // restaurant affiche (fourni par le controleur)
    private Commande commande;                // panier affiche (fourni par le controleur)

    private final JPanel cartesPlats = Style.groupe(new GridLayout(0, 1, 0, 10));     // une carte par ligne
    private final ArrayList<JButton> boutonsAjouter = new ArrayList<JButton>();       // un bouton par plat
    private final JPanel lignesPanier = Style.groupe(new GridLayout(0, 1, 0, 8));
    private final JLabel total = Style.texte("0,00 €", Font.BOLD, 18, Style.NOIR);
    private final JLabel etat = Style.texte(" ", Font.PLAIN, 13, Style.GRIS);

    private final JButton boutonValider = Style.boutonVert("Commander");
    private final JButton boutonEnregistrer = Style.boutonContour("Enregistrer le menu (.txt)");
    private final JButton boutonLire = Style.boutonContour("Charger un menu (.txt)");
    private final JButton boutonRetour = Style.boutonContour("Retour aux restaurants");

    private ActionListener controleur;

    public FenetreMenu(Donnees donnees, Restaurant restaurant, Commande commande, int indice) {
        super("FastBite - Menu");
        this.donnees = donnees;
        this.restaurant = restaurant;
        this.commande = commande;
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

        // --- SUD : boutons secondaires et barre d'etat
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

        remplirMenu();
        rafraichirPanier();
        setSize(1000, 660);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    // Le controleur se branche sur tous les boutons (y compris les boutons « Ajouter » recrees)
    public void setControleur(ActionListener nouveau) {
        if (controleur != null) {
            for (JButton b : tousLesBoutons()) b.removeActionListener(controleur);
        }
        controleur = nouveau;
        for (JButton b : tousLesBoutons()) b.addActionListener(controleur);
    }

    private ArrayList<JButton> tousLesBoutons() {
        ArrayList<JButton> liste = new ArrayList<JButton>(boutonsAjouter);
        liste.add(boutonValider);
        liste.add(boutonEnregistrer);
        liste.add(boutonLire);
        liste.add(boutonRetour);
        return liste;
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
            if (controleur != null) bouton.addActionListener(controleur);
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

    // Le controleur change le restaurant / le panier affiches (apres le chargement d'un menu .txt)
    public void afficher(Restaurant nouveauRestaurant, Commande nouvelleCommande) {
        this.restaurant = nouveauRestaurant;
        this.commande = nouvelleCommande;
        remplirMenu();
        rafraichirPanier();
    }

    // Observer : le modele a change, la vue se redessine
    @Override
    public void modeleModifie() {
        remplirMenu();
        rafraichirPanier();
    }

    // Boite de choix de fichier : un element d'interface, donc dans la vue. Renvoie null si annule.
    public String choisirFichier() {
        JFileChooser choix = new JFileChooser(Donnees.DOSSIER_DATA);
        if (choix.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            return choix.getSelectedFile().getPath();
        }
        return null;
    }

    // Fermer la fenetre : le controleur le demande, la vue sait comment
    public void fermer() {
        dispose();
    }

    public void erreur(String titre, String message) {
        Messages.erreur(this, titre, message);
    }

    public ArrayList<JButton> getBoutonsAjouter() { return boutonsAjouter; }
    public JButton getBoutonValider()             { return boutonValider; }
    public JButton getBoutonEnregistrer()         { return boutonEnregistrer; }
    public JButton getBoutonLire()                { return boutonLire; }
    public JButton getBoutonRetour()              { return boutonRetour; }
    public String getEtat()                       { return etat.getText(); }
    public void setEtat(String texte)             { etat.setText(texte); }
    public String getTotal()                      { return total.getText(); }
}
