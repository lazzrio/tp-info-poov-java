import java.util.ArrayList;

/*
 Classe Commande : passee par un Client aupres d'un Restaurant.
 AGREGATION : contient des Plat (les plats existent sans la commande)
 COMPOSITION : cree sa propre Livraison (n'existe que pour cette commande)
 */
public class Commande {
    // Attributs
    private int numero;
    private String statut;
    private double montantTotal;
    private double pourboire;
    private Client client;
    private Restaurant restaurant;
    private ArrayList<Plat> plats;   // AGREGATION
    private Livraison livraison;     // COMPOSITION


    // Constructeur par defaut

    public Commande() {
        this.numero = 0;
        this.statut = "NOUVELLE";
        this.montantTotal = 0.0;
        this.pourboire = 0.0;
        this.plats = new ArrayList<Plat>();
    }

    /*
     Constructeur parametre
     En parametre : numero, client, restaurant
     */
    public Commande(int numero, Client client, Restaurant restaurant) {
        this.numero = numero;
        this.client = client;
        this.restaurant = restaurant;
        this.statut = "NOUVELLE";
        this.montantTotal = 0.0;
        this.pourboire = 0.0;
        this.plats = new ArrayList<Plat>();
    }


    // Methode qui ajoute un plat a la commande (agregation)

    public void ajouterPlat(Plat p) {
        plats.add(p);
        System.out.println("  + " + p + " ajoute a la commande #" + numero);
    }


    // Methode qui calcule le montant total (somme des plats + pourboire)

    public double calculerTotal() {
        double total = 0.0;
        for (Plat p : plats) {
            total += p.getPrix();
        }
        total += pourboire;
        this.montantTotal = total;
        return total;
    }


    // Methode qui valide la commande

    public void valider() {
        calculerTotal();
        this.statut = "VALIDEE";
        System.out.println("Commande #" + numero + " validee (total : " + montantTotal + " EUR)");
    }


    // Methode qui cree la livraison associee a la commande (composition)

    public Livraison genererLivraison(Livreur l, int tempsEstime) {
        this.livraison = new Livraison(this, l, tempsEstime);
        System.out.println("Livraison generee pour la commande #" + numero);
        return livraison;
    }


    // Setter pourboire

    public void setPourboire(double p) { this.pourboire = p; }


    // Getters / setter statut

    public int getNumero()            { return numero; }
    public String getStatut()         { return statut; }
    public double getMontantTotal()   { return montantTotal; }
    public double getPourboire()      { return pourboire; }
    public Livraison getLivraison()   { return livraison; }
    public Restaurant getRestaurant() { return restaurant; }
    public Client getClient()         { return client; }
    public void setStatut(String s)   { this.statut = s; }
}
