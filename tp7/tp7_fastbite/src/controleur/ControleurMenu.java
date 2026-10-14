package controleur;

import modele.*;
import vue.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/*
 TP7 - CONTROLEUR de la page 2 (menu et panier). Un seul ActionListener pour tous les boutons de la
 vue : getSource() dit lequel a ete clique. Chaque action passe par le MODELE (Donnees) ; si le
 modele refuse (exception), le controleur demande a la vue d'afficher l'erreur.
 */
public class ControleurMenu implements ActionListener {
    private final FenetreMenu vue;
    private final Donnees donnees;
    private final ControleurApplication app;
    private final int indice;                 // position du restaurant dans la liste du modele
    private Restaurant restaurant;
    private Commande commande;                // le panier en cours

    public ControleurMenu(FenetreMenu vue, Donnees donnees, ControleurApplication app,
                          Restaurant restaurant, Commande commande, int indice) {
        this.vue = vue;
        this.donnees = donnees;
        this.app = app;
        this.restaurant = restaurant;
        this.commande = commande;
        this.indice = indice;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        int numero = vue.getBoutonsAjouter().indexOf(source);     // -1 si ce n'est pas un bouton « Ajouter »
        if (numero >= 0) {
            ajouterAuPanier(numero);
        } else if (source == vue.getBoutonValider()) {
            validerCommande();
        } else if (source == vue.getBoutonEnregistrer()) {
            enregistrerMenu();
        } else if (source == vue.getBoutonLire()) {
            String fichier = vue.choisirFichier();                // boite de choix = element d'interface (vue)
            if (fichier != null) {
                chargerMenu(fichier);
            }
        } else if (source == vue.getBoutonRetour()) {
            app.retourAccueil(vue);
        }
    }

    // Clic « Ajouter » : le modele ajoute le plat et previent la vue, qui redessine le panier
    private void ajouterAuPanier(int numero) {
        Plat plat = restaurant.getMenu()[numero];
        try {
            donnees.ajouterPlat(commande, plat);
            vue.setEtat(plat.getNom() + " ajouté au panier.");
        } catch (PlatIndisponibleException ex) {
            vue.erreur("Plat indisponible", ex.getMessage());
        }
    }

    // Clic « Commander » : CommandeVideException si le panier est vide, sinon page 3
    private void validerCommande() {
        try {
            donnees.validerCommande(commande);
            app.ouvrirCommandeDepuisMenu(vue, commande);
        } catch (CommandeVideException ex) {
            vue.erreur("Commande vide", ex.getMessage());
        }
    }

    // TP5 reutilise : ecriture du menu dans un fichier texte
    private void enregistrerMenu() {
        try {
            donnees.sauvegarderMenu(restaurant);
            vue.setEtat("Menu enregistré dans " + Donnees.fichierMenu(restaurant)
                    + " (" + restaurant.getNbPlats() + " plats).");
        } catch (IOException ex) {
            vue.erreur("Enregistrement impossible", ex.getMessage());
        }
    }

    /*
     TP5 reutilise : lecture d'un fichier texte. Le modele charge dans un restaurant NEUF : si le
     fichier est incorrect, rien n'est modifie. Public pour pouvoir etre teste sans la boite de choix.
     */
    public void chargerMenu(String fichier) {
        try {
            Restaurant relu = donnees.chargerMenu(indice, fichier);
            restaurant = relu;
            commande = new Commande(commande.getNumero(), donnees.getClient(), relu);   // panier vide
            vue.afficher(relu, commande);
            vue.setEtat(relu.getNbPlats() + " plat(s) lu(s) depuis " + fichier);
        } catch (FormatFichierException ex) {
            vue.erreur("Fichier incorrect", ex.getMessage());
        } catch (IOException ex) {
            vue.erreur("Lecture impossible", ex.getMessage());
        } catch (CapaciteDepasseeException ex) {
            vue.erreur("Menu trop long", ex.getMessage());
        }
    }

    public Commande getCommande()     { return commande; }
    public Restaurant getRestaurant() { return restaurant; }
}
