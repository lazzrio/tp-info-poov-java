import java.util.Date;

/*
 Classe Livraison : creee par une Commande (COMPOSITION), assuree par un Livreur.
 La Livraison n'existe pas sans sa Commande.
 */
public class Livraison {
    // Attributs
    private Date dateDepart;
    private Date dateArrivee;
    private String statut;
    private int tempsEstime;         // en minutes
    private Commande commande;
    private Livreur livreur;


    // Constructeur par defaut

    public Livraison() {
        this.statut = "EN_ATTENTE";
        this.tempsEstime = 0;
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
    }


    // Methode qui demarre la livraison

    public void demarrer() {
        this.dateDepart = new Date();
        this.statut = "EN_COURS";
        System.out.println("Livraison #" + commande.getNumero()
                + " demarree (arrivee prevue dans " + tempsEstime + " min).");
    }


    // Methode qui termine la livraison

    public void terminer() {
        this.dateArrivee = new Date();
        this.statut = "LIVREE";
        System.out.println("Livraison #" + commande.getNumero() + " terminee.");
    }


    // Getters / setter statut

    public String getStatut()       { return statut; }
    public Commande getCommande()   { return commande; }
    public Livreur getLivreur()     { return livreur; }
    public int getTempsEstime()     { return tempsEstime; }
    public Date getDateDepart()     { return dateDepart; }
    public Date getDateArrivee()    { return dateArrivee; }
    public void setStatut(String s) { this.statut = s; }
}
