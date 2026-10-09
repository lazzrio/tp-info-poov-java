import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;

/*
 TP6 - Page 3 : recapitulatif de la commande, paiement, suivi de la livraison par notre
 livreur, notes du restaurant et du livreur.
 Ici les listeners sont des CLASSES ANONYMES (une par bouton) : autre facon d'ecrire
 un ActionListener, vue au cours 6, pratique quand chaque bouton a son propre code.
 */
public class FenetreCommande extends JFrame {
    private final FenetreAccueil accueil;
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

    public FenetreCommande(FenetreAccueil accueil, Donnees donnees, Commande commande) {
        super("FastBite - Commande #" + commande.getNumero());
        this.accueil = accueil;
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

        // --- LISTENERS (classes anonymes)
        boutonPourboire.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { appliquerPourboire(); }
        });
        boutonNoter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { noter(); }
        });
        boutonNoterLivreur.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { noterLivreur(); }
        });
        boutonPayer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { payer(); }
        });
        boutonSuivre.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { avancerLivraison(); }
        });
        boutonAccueil.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { retourAccueil(); }
        });

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
    private void rafraichir() {
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

    // Le champ texte contient une String : la conversion peut echouer (NumberFormatException)
    private void appliquerPourboire() {
        try {
            double p = Double.parseDouble(champPourboire.getText().trim().replace(',', '.'));
            if (p < 0) {
                Messages.erreur(this, "Pourboire invalide", "Le pourboire ne peut pas etre negatif.");
                return;
            }
            if (commande.getStatut().equals("PAYEE")) {
                Messages.erreur(this, "Pourboire impossible", "La commande est deja payee : le total ne peut plus changer.");
                return;
            }
            commande.setPourboire(p);
            rafraichir();
            etat.setText("Pourboire de " + Style.prix(p) + " appliqué.");
        } catch (NumberFormatException ex) {
            Messages.erreur(this, "Pourboire invalide", "\"" + champPourboire.getText() + "\" n'est pas un nombre.");
        }
    }

    // NoteInvalideException (TP5) si la note n'est pas entre 0 et 5
    private void noter() {
        Restaurant r = commande.getRestaurant();
        try {
            double note = Double.parseDouble(champNote.getText().trim().replace(',', '.'));
            r.ajouterNote(note);
            etat.setText("Merci ! Note " + note + " enregistrée pour " + r.getNom()
                    + " (moyenne : " + Style.note(r) + " / 5).");
        } catch (NumberFormatException ex) {
            Messages.erreur(this, "Note invalide", "\"" + champNote.getText() + "\" n'est pas un nombre.");
        } catch (NoteInvalideException ex) {
            Messages.erreur(this, "Note invalide", ex.getMessage());
        }
    }

    // Meme principe pour le livreur, mais seulement une fois la commande livree
    private void noterLivreur() {
        Livraison l = commande.getLivraison();
        if (l == null || !l.getStatut().equals("LIVREE")) {
            Messages.erreur(this, "Trop tot", "Vous pourrez noter " + livreur.getNom() + " une fois la commande livree.");
            return;
        }
        try {
            double note = Double.parseDouble(champNoteLivreur.getText().trim().replace(',', '.'));
            donnees.getClient().noterRider(livreur, note);
            rafraichir();
            etat.setText("Merci ! Note " + note + " enregistrée pour " + livreur.getNom() + ".");
        } catch (NumberFormatException ex) {
            Messages.erreur(this, "Note invalide", "\"" + champNoteLivreur.getText() + "\" n'est pas un nombre.");
        } catch (NoteInvalideException ex) {
            Messages.erreur(this, "Note invalide", ex.getMessage());
        }
    }

    // PaiementImpossibleException (TP5) si la commande est deja payee
    private void payer() {
        try {
            donnees.getClient().payerCommande(commande);
            rafraichir();
            etat.setText("Paiement accepté : " + Style.prix(commande.calculerTotal()) + ".");
        } catch (PaiementImpossibleException ex) {
            Messages.erreur(this, "Paiement refuse", ex.getMessage());
        }
    }

    /*
     Chaque clic fait avancer la livraison d'une etape, avec les methodes du TP3 / TP5 :
     accepterLivraison() leve LivreurIndisponibleException si le livreur a deja une course,
     demarrer() et terminer() levent EtatLivraisonException si l'etape n'est pas la bonne.
     */
    private void avancerLivraison() {
        if (!commande.getStatut().equals("PAYEE")) {
            Messages.erreur(this, "Livraison impossible", "Payez d'abord la commande : le livreur ne part qu'apres le paiement.");
            return;
        }
        try {
            Livraison l = commande.getLivraison();
            if (l == null) {
                l = commande.genererLivraison(livreur, 20);       // composition : la commande cree sa livraison
            }
            String s = l.getStatut();
            if (s.equals("EN_ATTENTE")) {
                livreur.accepterLivraison(l);
                donnees.setCommandeSuivie(commande);              // l'accueil pourra rouvrir cette page
                etat.setText(livreur.getNom() + " a accepté la livraison.");
            } else if (s.equals("EN_COURS")) {
                l.demarrer();
                etat.setText(livreur.getNom() + " est parti à " + l.getDateDepart() + ".");
            } else if (s.equals("EN_ROUTE")) {
                l.terminer();
                livreur.confirmerLivraison(l);                    // le livreur redevient disponible
                donnees.setCommandeSuivie(null);
                etat.setText("Commande livrée à " + l.getDateArrivee() + ". Bon appétit !");
            } else {
                Messages.info(this, "Commande livree", "Cette commande a deja ete livree. Bon appetit !");
            }
            rafraichir();
        } catch (LivreurIndisponibleException ex) {
            Messages.erreur(this, "Livreur indisponible", ex.getMessage());
        } catch (EtatLivraisonException ex) {
            Messages.erreur(this, "Livraison", ex.getMessage());
        }
    }

    // Navigation : retour a la page 1
    private void retourAccueil() {
        accueil.rafraichir();
        accueil.setEtat("Commande #" + commande.getNumero() + " : " + commande.getStatut());
        accueil.setVisible(true);
        dispose();
    }

    // Accesseurs utilises par les tests
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
}
