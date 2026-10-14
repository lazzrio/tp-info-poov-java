package modele;

/*
 Classe Livraison : creee par une Commande (COMPOSITION), assuree par un Livreur.
 La Livraison n'existe pas sans sa Commande.
 TP5 : les changements d'etat sont verifies (EtatLivraisonException).
 */
public class Livraison {
    // Attributs
    private String dateDepart;       // heure de depart, ex : "18h30"
    private String dateArrivee;      // heure d'arrivee
    private String statut;           // EN_ATTENTE -> EN_COURS -> EN_ROUTE -> LIVREE
    private int tempsEstime;         // en minutes
    private Commande commande;
    private Livreur livreur;


    // Constructeur par defaut

    public Livraison() {
        this.statut = "EN_ATTENTE";
        this.tempsEstime = 0;
        this.dateDepart = "";
        this.dateArrivee = "";
    }

    /*
     Constructeur parametre
     En parametre : la commande a livrer, le livreur assigne, le temps estime (min)
     */
    public Livraison(Commande commande, Livreur livreur, int tempsEstime) {
        this.commande = commande;
        this.livreur = livreur;
        this.statut = "EN_ATTENTE";
        this.tempsEstime = tempsEstime;
        this.dateDepart = "";
        this.dateArrivee = "";
    }


    /*
     Methode qui demarre la livraison (Sequence 3).
     TP5 : impossible si le livreur n'a pas encore accepte (EN_ATTENTE)
           ou si la livraison est deja partie / terminee.
     */
    public void demarrer() throws EtatLivraisonException {
        if (!statut.equals("EN_COURS")) {
            throw new EtatLivraisonException(commande.getNumero(), statut, "demarrer la livraison");
        }
        this.dateDepart = "18h30";
        this.statut = "EN_ROUTE";
        System.out.println("Livraison #" + commande.getNumero()
                + " demarree a " + dateDepart + " (arrivee dans " + tempsEstime + " min).");
    }


    /*
     Methode qui termine la livraison (Sequence 3).
     TP5 : impossible si la livraison n'est jamais partie (EN_ATTENTE ou EN_COURS).
     */
    public void terminer() throws EtatLivraisonException {
        if (statut.equals("EN_ATTENTE") || statut.equals("EN_COURS")) {
            throw new EtatLivraisonException(commande.getNumero(), statut, "terminer la livraison");
        }
        this.dateArrivee = "18h50";
        this.statut = "LIVREE";
        System.out.println("Livraison #" + commande.getNumero() + " terminee a " + dateArrivee + ".");
    }


    // Getters / setter statut

    public String getStatut()       { return statut; }
    public Commande getCommande()   { return commande; }
    public Livreur getLivreur()     { return livreur; }
    public int getTempsEstime()     { return tempsEstime; }
    public String getDateDepart()   { return dateDepart; }
    public String getDateArrivee()  { return dateArrivee; }
    public void setStatut(String s) { this.statut = s; }
}
