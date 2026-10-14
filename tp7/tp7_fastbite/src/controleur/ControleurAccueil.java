package controleur;

import modele.*;
import vue.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/*
 TP7 - CONTROLEUR de la page 1. C'est LUI qui implemente ActionListener (au TP6 c'etait la fenetre).
 Role : recevoir les clics de la vue, demander l'action au modele (Donnees), attraper les exceptions
 du modele et demander a la vue d'afficher le resultat. Il ne dessine rien et ne contient pas de
 regle metier.
 */
public class ControleurAccueil implements ActionListener {
    private final FenetreAccueil vue;
    private final Donnees donnees;
    private final ControleurApplication app;

    public ControleurAccueil(FenetreAccueil vue, Donnees donnees, ControleurApplication app) {
        this.vue = vue;
        this.donnees = donnees;
        this.app = app;
    }

    // Appelee par Swing a chaque clic sur un bouton de la vue : getSource() dit QUEL bouton
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        int indice = vue.getBoutonsMenu().indexOf(source);       // -1 si ce n'est pas un bouton « Voir le menu »
        if (indice >= 0) {
            app.ouvrirMenu(indice);
        } else if (source == vue.getBoutonSauver()) {
            sauvegarder();
        } else if (source == vue.getBoutonCharger()) {
            charger();
        } else if (source == vue.getBoutonSuivi()) {
            app.ouvrirSuivi();
        } else if (source == vue.getBoutonQuitter()) {
            vue.fermer();
        }
    }

    // TP5 reutilise : serialisation de la liste dans un fichier binaire
    private void sauvegarder() {
        try {
            donnees.sauvegarderRestaurants();
            vue.setEtat(donnees.getRestaurants().size() + " restaurant(s) sérialisé(s) dans " + Donnees.FICHIER_SER);
        } catch (IOException ex) {
            vue.erreur("Sauvegarde impossible", "Ecriture de " + Donnees.FICHIER_SER + " : " + ex.getMessage());
        }
    }

    // TP5 reutilise : deserialisation ; le modele remplace sa liste puis previent la vue
    private void charger() {
        try {
            donnees.chargerRestaurants();
            vue.setEtat(donnees.getRestaurants().size() + " restaurant(s) relu(s) depuis " + Donnees.FICHIER_SER);
        } catch (IOException ex) {
            vue.erreur("Chargement impossible", "Lecture de " + Donnees.FICHIER_SER + " : " + ex.getMessage());
        } catch (ClassNotFoundException ex) {
            vue.erreur("Chargement impossible", "Classe inconnue dans le fichier : " + ex.getMessage());
        }
    }
}
