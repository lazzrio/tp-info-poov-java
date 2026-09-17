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
    private Commande commande;
    private Livreur livreur;


    // Constructeur par defaut

    public Livraison() {
        this.statut = "EN_ATTENTE";
    }

    /*
     Constructeur parametre
     En parametre : la commande a livrer, le livreur assigne
     */
    public Livraison(Commande commande, Livreur livreur) {
        this.commande = commande;
        this.livreur = livreur;
        this.statut = "EN_ATTENTE";
        this.dateDepart = new Date();
    }


    // Methode qui demarre la livraison

    public void demarrer() {
        this.dateDepart = new Date();
        this.statut = "EN_COURS";
        System.out.println("Livraison #" + commande.getNumero() + " demarree.");
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
    public Date getDateDepart()     { return dateDepart; }
    public Date getDateArrivee()    { return dateArrivee; }
    public void setStatut(String s) { this.statut = s; }
}
