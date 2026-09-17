/**
 * Classe Plat : un plat propose au menu d'un Restaurant.
 * Un Plat est agrege par une Commande (existe sans la commande).
 */
public class Plat {
    /** Attributs */
    private String nom;
    private double prix;
    private String description;

    /*
     * Constructeur par defaut
     */
    public Plat() {
        this.nom = "Plat";
        this.prix = 0.0;
        this.description = "";
    }

    /*
     * Constructeur parametre
     * @params : nom, prix, description
     */
    public Plat(String nom, double prix, String description) {
        this.nom = nom;
        this.prix = prix;
        this.description = description;
    }

    /*
     * Methode qui applique une remise en pourcentage sur le prix
     */
    public void appliquerRemise(double pourcentage) {
        this.prix = prix * (1 - pourcentage / 100.0);
    }

    /*
     * Methode d'affichage
     */
    public void afficher() {
        System.out.println("- " + nom + " : " + prix + " EUR (" + description + ")");
    }

    /*
     * Getters
     */
    public String getNom()         { return nom; }
    public double getPrix()        { return prix; }
    public String getDescription() { return description; }

    @Override
    public String toString() {
        return nom + " (" + prix + " EUR)";
    }
}
