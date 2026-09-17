/*
 Classe Plat : un plat propose au menu d'un Restaurant.
 Un Plat est agrege par une Commande (existe sans la commande).
 */
public class Plat {
    // Attributs
    private String nom;
    private double prix;
    private String description;
    private String[] allergenes;


    // Constructeur par defaut

    public Plat() {
        this.nom = "Plat";
        this.prix = 0.0;
        this.description = "";
        this.allergenes = new String[0];
    }

    /*
     Constructeur parametre
     En parametre : nom, prix, description, tableau d'allergenes
     */
    public Plat(String nom, double prix, String description, String[] allergenes) {
        this.nom = nom;
        this.prix = prix;
        this.description = description;
        this.allergenes = allergenes;
    }


    // Methode qui affiche le plat + ses allergenes

    public void afficher() {
        System.out.print("- " + nom + " : " + prix + " EUR (" + description + ")");
        if (allergenes.length > 0) {
            System.out.print(" [allergenes : ");
            for (int i = 0; i < allergenes.length; i++) {
                System.out.print(allergenes[i]);
                if (i < allergenes.length - 1) System.out.print(", ");
            }
            System.out.print("]");
        }
        System.out.println();
    }


    // Getters

    public String getNom()           { return nom; }
    public double getPrix()          { return prix; }
    public String getDescription()   { return description; }
    public String[] getAllergenes()  { return allergenes; }

    @Override
    public String toString() {
        return nom + " (" + prix + " EUR)";
    }
}
