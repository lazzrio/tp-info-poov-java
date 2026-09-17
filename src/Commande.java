import java.util.ArrayList;

/**
 * Classe Commande : passee par un Client aupres d'un Restaurant.
 * - AGREGATION : contient des Plat (les plats existent sans la commande)
 * - COMPOSITION : cree sa propre Livraison (n'existe que pour cette commande)
 */
public class Commande {
    /** Attributs */
    private int numero;
    private String statut;
    private double montantTotal;
    private Client client;
    private Restaurant restaurant;
    private ArrayList<Plat> plats;   // <>-- AGREGATION
    private Livraison livraison;     // <*>-- COMPOSITION

    /*
     * Constructeur par defaut
     */
    public Commande() {
        this.numero = 0;
        this.statut = "NOUVELLE";
        this.montantTotal = 0.0;
        this.plats = new ArrayList<Plat>();
    }

    /*
     * Constructeur parametre
     * @params : numero, client, restaurant
     */
    public Commande(int numero, Client client, Restaurant restaurant) {
        this.numero = numero;
        this.client = client;
        this.restaurant = restaurant;
        this.statut = "NOUVELLE";
        this.montantTotal = 0.0;
        this.plats = new ArrayList<Plat>();
    }

    /*
     * Methode qui ajoute un plat a la commande (agregation)
     * et met a jour le montant total
     */
    public void ajouterPlat(Plat p) {
        plats.add(p);
        montantTotal += p.getPrix();
        System.out.println("  + " + p + " ajoute a la commande #" + numero);
    }

    /*
     * Methode qui cree la livraison associee a la commande (composition)
     */
    public Livraison genererLivraison(Livreur l) {
        this.livraison = new Livraison(this, l);
        System.out.println("Livraison generee pour la commande #" + numero);
        return livraison;
    }

    /*
     * Methode qui valide la commande
     */
    public void validerCommande() {
        this.statut = "VALIDEE";
        System.out.println("Commande #" + numero + " validee (total : " + montantTotal + " EUR)");
    }

    /*
     * Getters / setter statut
     */
    public int getNumero()            { return numero; }
    public String getStatut()         { return statut; }
    public double getMontantTotal()   { return montantTotal; }
    public Livraison getLivraison()   { return livraison; }
    public Restaurant getRestaurant() { return restaurant; }
    public Client getClient()         { return client; }
    public void setStatut(String s)   { this.statut = s; }
}
