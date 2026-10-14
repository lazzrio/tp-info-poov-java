package controleur;

import modele.*;
import vue.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/*
 TP7 - CONTROLEUR de la page 3 (commande, paiement, livraison, notes). Ici on garde l'autre ecriture
 vue au cours 6 : une CLASSE ANONYME par bouton. Le controleur lit les champs de saisie de la vue
 (du texte), convertit en nombre (NumberFormatException possible), demande l'action au modele et
 transforme les exceptions du modele en messages pour la vue.
 */
public class ControleurCommande {
    private final FenetreCommande vue;
    private final Donnees donnees;
    private final ControleurApplication app;
    private final Commande commande;

    public ControleurCommande(FenetreCommande vue, Donnees donnees, ControleurApplication app, Commande commande) {
        this.vue = vue;
        this.donnees = donnees;
        this.app = app;
        this.commande = commande;

        vue.getBoutonPourboire().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { appliquerPourboire(); }
        });
        vue.getBoutonNoter().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { noterRestaurant(); }
        });
        vue.getBoutonNoterLivreur().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { noterLivreur(); }
        });
        vue.getBoutonPayer().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { payer(); }
        });
        vue.getBoutonSuivre().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { avancerLivraison(); }
        });
        vue.getBoutonAccueil().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { app.retourAccueil(vue, commande); }
        });
    }

    // Le champ de la vue contient une String : la conversion peut echouer (NumberFormatException)
    private double lireNombre(String texte) {
        return Double.parseDouble(texte.trim().replace(',', '.'));
    }

    private void appliquerPourboire() {
        try {
            double p = lireNombre(vue.getTextePourboire());
            donnees.appliquerPourboire(commande, p);              // le MODELE verifie la regle
            vue.setEtat("Pourboire de " + Style.prix(p) + " appliqué.");
        } catch (NumberFormatException ex) {
            vue.erreur("Pourboire invalide", "\"" + vue.getTextePourboire() + "\" n'est pas un nombre.");
        } catch (PourboireInvalideException ex) {
            vue.erreur("Pourboire invalide", ex.getMessage());
        }
    }

    // NoteInvalideException (TP5) si la note n'est pas entre 0 et 5
    private void noterRestaurant() {
        Restaurant r = commande.getRestaurant();
        try {
            double note = lireNombre(vue.getTexteNote());
            donnees.noterRestaurant(r, note);
            vue.setEtat("Merci ! Note " + note + " enregistrée pour " + r.getNom()
                    + " (moyenne : " + Style.note(r) + " / 5).");
        } catch (NumberFormatException ex) {
            vue.erreur("Note invalide", "\"" + vue.getTexteNote() + "\" n'est pas un nombre.");
        } catch (NoteInvalideException ex) {
            vue.erreur("Note invalide", ex.getMessage());
        }
    }

    // Meme principe pour le livreur ; le modele refuse tant que la commande n'est pas livree
    private void noterLivreur() {
        Livreur livreur = donnees.getLivreur();
        try {
            double note = lireNombre(vue.getTexteNoteLivreur());
            donnees.noterLivreur(commande, note);
            vue.setEtat("Merci ! Note " + note + " enregistrée pour " + livreur.getNom() + ".");
        } catch (NumberFormatException ex) {
            vue.erreur("Note invalide", "\"" + vue.getTexteNoteLivreur() + "\" n'est pas un nombre.");
        } catch (NotationImpossibleException ex) {
            vue.erreur("Trop tot", ex.getMessage());
        } catch (NoteInvalideException ex) {
            vue.erreur("Note invalide", ex.getMessage());
        }
    }

    // PaiementImpossibleException (TP5) si la commande est deja payee
    private void payer() {
        try {
            donnees.payer(commande);
            vue.setEtat("Paiement accepté : " + Style.prix(commande.calculerTotal()) + ".");
        } catch (PaiementImpossibleException ex) {
            vue.erreur("Paiement refuse", ex.getMessage());
        }
    }

    // Chaque clic fait avancer la livraison d'une etape ; les regles sont dans le MODELE
    private void avancerLivraison() {
        Livraison avant = commande.getLivraison();
        if (avant != null && avant.getStatut().equals("LIVREE")) {
            vue.info("Commande livree", "Cette commande a deja ete livree. Bon appetit !");
            return;
        }
        Livreur livreur = donnees.getLivreur();
        try {
            Livraison l = donnees.avancerLivraison(commande);
            String s = l.getStatut();
            if (s.equals("EN_COURS")) {
                vue.setEtat(livreur.getNom() + " a accepté la livraison.");
            } else if (s.equals("EN_ROUTE")) {
                vue.setEtat(livreur.getNom() + " est parti à " + l.getDateDepart() + ".");
            } else if (s.equals("LIVREE")) {
                vue.setEtat("Commande livrée à " + l.getDateArrivee() + ". Bon appétit !");
            }
        } catch (CommandeNonPayeeException ex) {
            vue.erreur("Livraison impossible", ex.getMessage());
        } catch (LivreurIndisponibleException ex) {
            vue.erreur("Livreur indisponible", ex.getMessage());
        } catch (EtatLivraisonException ex) {
            vue.erreur("Livraison", ex.getMessage());
        }
    }

    public Commande getCommande() { return commande; }
}
