package vue;

import modele.*;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;

/*
 TP7 - VUE (page 3 : recapitulatif de la commande, paiement, suivi de la livraison, notes).
 Elle affiche la commande qu'on lui donne. Les clics sont geres par ControleurCommande, qui
 s'abonne aux boutons exposes ci-dessous ; les regles (pourboire, paiement, livraison, notes)
 sont dans le modele. La vue ne fait que dessiner et lire les champs de saisie.
 */
public class FenetreCommande extends JFrame implements EcouteurModele {
    private final Donnees donnees;
    private final Commande commande;
    private final Livreur livreur;

    private final JPanel lignes = Style.groupe(new GridLayout(0, 1, 0, 8));        // le recapitulatif
    private final Style.Badge badge = new Style.Badge(" ");                         // statut de la commande
    private final Style.Etapes etapes = new Style.Etapes(new String[]{"Payée", "Prise en charge", "En route", "Livrée"});
    private final JTextField champPourboire = Style.champ("0", 5);
    private final JTextField champNote = Style.champ("", 4);
    private final JTextField champNoteLivreur = Style.champ("", 4);
    private final JLabel ligneLivreur = Style.texte(" ", Font.PLAIN, 13, Style.GRIS);
    private final JLabel etat = Style.texte(" ", Font.PLAIN, 13, Style.GRIS);
    private final JButton boutonPourboire = Style.boutonContour("Appliquer");
    private final JButton boutonPayer = Style.boutonVert("Payer");
    private final JButton boutonSuivre = Style.boutonOrange("Suivre la livraison");
    private final JButton boutonNoter = Style.boutonNoir("Noter");
    private final JButton boutonNoterLivreur = Style.boutonNoir("Noter");
    private final JButton boutonAccueil = Style.boutonContour("Retour aux restaurants");

    public FenetreCommande(Donnees donnees, Commande commande) {
        super("FastBite - Commande #" + commande.getNumero());
        this.donnees = donnees;
        this.commande = commande;
        this.livreur = donnees.getLivreur();

        // --- NORD : barre blanche (logo, titre de la commande, statut)
        JPanel nord = new JPanel(new BorderLayout(24, 0));
        nord.setBackground(Style.BLANC);
        nord.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Style.GRIS_CLAIR),
                BorderFactory.createEmptyBorder(12, 22, 12, 22)));
        JPanel statut = Style.groupe(new FlowLayout(FlowLayout.RIGHT, 0, 4));
        statut.add(badge);
        nord.add(Style.logo(26), BorderLayout.WEST);
        nord.add(Style.texte("Commande #" + commande.getNumero() + "   ·   " + commande.getRestaurant().getNom(),
                Font.BOLD, 18, Style.NOIR), BorderLayout.CENTER);
        nord.add(statut, BorderLayout.EAST);

        // --- GAUCHE : recapitulatif, pourboire, paiement
        JPanel hautRecap = Style.groupe(new BorderLayout());
        hautRecap.add(lignes, BorderLayout.NORTH);
        JPanel saisiePourboire = Style.groupe(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        saisiePourboire.add(champPourboire);
        saisiePourboire.add(boutonPourboire);
        JPanel lignePourboire = Style.groupe(new BorderLayout());
        lignePourboire.add(Style.texte("Pourboire pour " + livreur.getNom() + " (€)", Font.PLAIN, 14, Style.NOIR), BorderLayout.CENTER);
        lignePourboire.add(saisiePourboire, BorderLayout.EAST);
        JPanel basRecap = Style.groupe(new GridLayout(2, 1, 0, 12));
        basRecap.add(lignePourboire);
        basRecap.add(boutonPayer);
        Style.Carte recap = new Style.Carte(new BorderLayout(0, 14));
        recap.add(Style.texte("Récapitulatif", Font.BOLD, 19, Style.NOIR), BorderLayout.NORTH);
        recap.add(hautRecap, BorderLayout.CENTER);
        recap.add(basRecap, BorderLayout.SOUTH);

        // --- DROITE : notre livreur et le suivi de la livraison, puis les notes
        JPanel textesLivreur = Style.groupe(new GridLayout(2, 1, 0, 2));
        textesLivreur.add(Style.texte(livreur.getNom() + ", votre livreur FastBite", Font.BOLD, 16, Style.NOIR));
        textesLivreur.add(ligneLivreur);
        ligneLivreur.setIcon(new Style.Etoile(13));
        JPanel identite = Style.groupe(new BorderLayout(14, 0));
        identite.add(new Style.Avatar(livreur.getNom(), Style.NOIR, 46), BorderLayout.WEST);
        identite.add(textesLivreur, BorderLayout.CENTER);
        Style.Carte carteLivraison = new Style.Carte(new BorderLayout(0, 14));
        carteLivraison.add(identite, BorderLayout.NORTH);
        carteLivraison.add(etapes, BorderLayout.CENTER);
        carteLivraison.add(boutonSuivre, BorderLayout.SOUTH);

        Style.Carte carteAvis = new Style.Carte(new GridLayout(3, 1, 0, 8));
        carteAvis.add(Style.texte("Votre avis (note de 0 à 5)", Font.BOLD, 16, Style.NOIR));
        carteAvis.add(ligneNote(commande.getRestaurant().getNom(), champNote, boutonNoter));
        carteAvis.add(ligneNote(livreur.getNom() + " (livreur)", champNoteLivreur, boutonNoterLivreur));
        JPanel droite = Style.groupe(new BorderLayout(0, 14));
        droite.add(carteLivraison, BorderLayout.NORTH);
        JPanel hautAvis = Style.groupe(new BorderLayout());
        hautAvis.add(carteAvis, BorderLayout.NORTH);
        droite.add(hautAvis, BorderLayout.CENTER);

        // --- SUD
        JPanel sud = Style.groupe(new BorderLayout(12, 0));
        sud.add(boutonAccueil, BorderLayout.WEST);
        sud.add(etat, BorderLayout.CENTER);

        JPanel colonnes = Style.groupe(new GridLayout(1, 2, 16, 0));      // 2 colonnes de meme largeur
        colonnes.add(recap);
        colonnes.add(droite);
        JPanel centre = new JPanel(new BorderLayout(0, 14));
        centre.setBackground(Style.FOND);
        centre.setBorder(BorderFactory.createEmptyBorder(18, 22, 12, 22));
        centre.add(colonnes, BorderLayout.CENTER);
        centre.add(sud, BorderLayout.SOUTH);
        JPanel contenu = new JPanel(new BorderLayout());
        contenu.add(nord, BorderLayout.NORTH);
        contenu.add(centre, BorderLayout.CENTER);
        setContentPane(contenu);

        // PAS de listener ici : ControleurCommande s'abonne aux boutons (classes anonymes)

        rafraichir();
        setSize(1000, 660);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    // Une ligne du bloc « Votre avis » : nom, champ de saisie, bouton
    private JPanel ligneNote(String qui, JTextField champ, JButton bouton) {
        JPanel saisie = Style.groupe(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        saisie.add(champ);
        saisie.add(bouton);
        JPanel ligne = Style.groupe(new BorderLayout());
        ligne.add(Style.texte(qui, Font.PLAIN, 14, Style.NOIR), BorderLayout.CENTER);
        ligne.add(saisie, BorderLayout.EAST);
        return ligne;
    }

    // Reconstruit tout l'affichage a partir de la commande (plats, total, statut, livraison)
    public void rafraichir() {
        lignes.removeAll();
        for (Plat p : commande.getPlats()) {
            lignes.add(Style.ligne(p.getNom(), Style.prix(p.getPrix()), Font.PLAIN, 15));
        }
        lignes.add(new JSeparator());
        lignes.add(Style.ligne("Pourboire", Style.prix(commande.getPourboire()), Font.PLAIN, 15));
        lignes.add(Style.ligne("Total", Style.prix(commande.calculerTotal()), Font.BOLD, 19));

        boolean payee = commande.getStatut().equals("PAYEE");
        badge.setText(commande.getStatut());
        badge.setFond(payee ? Style.VERT : Style.ORANGE);
        boutonPayer.setText(payee ? "Commande payée" : "Payer " + Style.prix(commande.calculerTotal()));

        // Etat de la livraison : EN_ATTENTE -> EN_COURS -> EN_ROUTE -> LIVREE
        Livraison l = commande.getLivraison();
        String s = (l == null) ? "" : l.getStatut();
        int faites = payee ? 1 : 0;
        String action = "Confier la commande à " + livreur.getNom();
        String situation = "attend votre commande";
        if (s.equals("EN_COURS")) {
            faites = 2; action = livreur.getNom() + " part du restaurant"; situation = "récupère votre commande";
        } else if (s.equals("EN_ROUTE")) {
            faites = 3; action = "Confirmer la réception"; situation = "en route, arrivée dans " + l.getTempsEstime() + " min";
        } else if (s.equals("LIVREE")) {
            faites = 4; action = "Commande livrée"; situation = "commande livrée à " + l.getDateArrivee();
        }
        etapes.setFaites(faites);
        boutonSuivre.setText(action);
        ligneLivreur.setText(Style.note(livreur) + "   ·   " + livreur.getVehicule() + "   ·   " + situation);
        lignes.revalidate();
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

    public void erreur(String titre, String message) {
        Messages.erreur(this, titre, message);
    }

    public void info(String titre, String message) {
        Messages.info(this, titre, message);
    }

    // Accesseurs : le controleur (et les tests) lisent les champs et s'abonnent aux boutons
    public String getTextePourboire()        { return champPourboire.getText(); }
    public String getTexteNote()             { return champNote.getText(); }
    public String getTexteNoteLivreur()      { return champNoteLivreur.getText(); }
    public JTextField getChampPourboire()    { return champPourboire; }
    public JTextField getChampNote()         { return champNote; }
    public JTextField getChampNoteLivreur()  { return champNoteLivreur; }
    public JButton getBoutonPourboire()      { return boutonPourboire; }
    public JButton getBoutonNoter()          { return boutonNoter; }
    public JButton getBoutonNoterLivreur()   { return boutonNoterLivreur; }
    public JButton getBoutonPayer()          { return boutonPayer; }
    public JButton getBoutonSuivre()         { return boutonSuivre; }
    public JButton getBoutonAccueil()        { return boutonAccueil; }
    public Commande getCommande()            { return commande; }
    public String getEtat()                  { return etat.getText(); }
    public void setEtat(String texte)        { etat.setText(texte); }
}
