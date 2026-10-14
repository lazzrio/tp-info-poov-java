package controleur;

import modele.*;
import vue.*;

/*
 TP7 - CONTROLEUR general : il assemble le MVC et gere la NAVIGATION entre les pages.
 Au TP6, chaque fenetre en ouvrait une autre (new FenetreMenu(...), setVisible...) : la vue
 decidait du parcours. Ici seul le controleur cree et ferme les fenetres ; chaque fenetre
 recoit son controleur et est abonnee au modele (Observer).
 */
public class ControleurApplication {
    private final Donnees donnees;               // le MODELE
    private FenetreAccueil accueil;              // la page 1 reste en vie, on la cache / montre
    private ControleurMenu menuCourant;          // controleurs actifs (utiles aux tests)
    private ControleurCommande commandeCourante;

    public ControleurApplication(Donnees donnees) {
        this.donnees = donnees;
    }

    // Demarrage : vue + controleur de l'accueil, puis jeu d'essai (le modele previent la vue)
    public void demarrer() {
        accueil = new FenetreAccueil(donnees);
        donnees.ajouterEcouteur(accueil);                        // la vue observe le modele
        accueil.setControleur(new ControleurAccueil(accueil, donnees, this));
        try {
            donnees.creerJeuDEssai();
        } catch (CapaciteDepasseeException e) {
            accueil.erreur("Demarrage", e.getMessage());
        }
        accueil.setVisible(true);
    }

    // Page 1 -> page 2 : le panier (une Commande) est cree par le MODELE, la vue ne fait que l'afficher
    public void ouvrirMenu(int indice) {
        Restaurant restaurant = donnees.getRestaurants().get(indice);
        try {
            Commande panier = donnees.nouvelleCommande(restaurant);
            FenetreMenu page = new FenetreMenu(donnees, restaurant, panier, indice);
            menuCourant = new ControleurMenu(page, donnees, this, restaurant, panier, indice);
            page.setControleur(menuCourant);
            donnees.ajouterEcouteur(page);
            page.setVisible(true);
            accueil.setVisible(false);
        } catch (CapaciteDepasseeException ex) {
            // l'historique du client (tableau de 20 commandes) est plein
            accueil.erreur("Commande impossible", ex.getMessage());
        }
    }

    // Page 2 -> page 3 : la page menu est fermee, la page commande prend le relais
    public void ouvrirCommandeDepuisMenu(FenetreMenu menu, Commande commande) {
        donnees.retirerEcouteur(menu);
        menu.fermer();
        creerPageCommande(commande);
    }

    // Page 1 -> page 3 : « Suivre ma commande » rouvre la commande en cours de livraison
    public void ouvrirSuivi() {
        creerPageCommande(donnees.getCommandeSuivie());
        accueil.setVisible(false);
    }

    private void creerPageCommande(Commande commande) {
        FenetreCommande page = new FenetreCommande(donnees, commande);
        commandeCourante = new ControleurCommande(page, donnees, this, commande);
        donnees.ajouterEcouteur(page);
        page.setVisible(true);
    }

    // Retour a la page 1 depuis le menu
    public void retourAccueil(FenetreMenu menu) {
        donnees.retirerEcouteur(menu);
        menu.fermer();
        accueil.rafraichir();
        accueil.setVisible(true);
    }

    // Retour a la page 1 depuis la page commande
    public void retourAccueil(FenetreCommande page, Commande commande) {
        donnees.retirerEcouteur(page);
        page.fermer();
        accueil.rafraichir();
        accueil.setEtat("Commande #" + commande.getNumero() + " : " + commande.getStatut());
        accueil.setVisible(true);
    }

    public ControleurMenu getMenuCourant()           { return menuCourant; }
    public ControleurCommande getCommandeCourante()  { return commandeCourante; }
}
