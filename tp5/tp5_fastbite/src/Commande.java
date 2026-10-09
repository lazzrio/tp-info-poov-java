/*
 Classe Commande : passee par un Client aupres d'un Restaurant.
 AGREGATION : contient des Plat (les plats existent sans la commande)
 COMPOSITION : cree sa propre Livraison (n'existe que pour cette commande)
 */
import java.util.ArrayList;
import java.util.List;

public class Commande {
    // Attributs
    private int numero;
    private String statut;
    private double montantTotal;
    private double pourboire;
    private Client client;
    private Restaurant restaurant;
    protected final List<Plat> plats = new ArrayList<>(); // Collection heritee par Panier
    private Livraison livraison;     // COMPOSITION


    // Constructeur par defaut

    public Commande() {
        this.numero = 0;
        this.statut = "NOUVELLE";
        this.montantTotal = 0.0;
        this.pourboire = 0.0;
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
    }


    /*
     Methode qui ajoute un plat a la commande (agregation).
     TP4 (inchange) : IllegalArgumentException si plat == null (bug de programmation).
     TP5 : PlatIndisponibleException si le restaurant de la commande ne propose pas ce plat.
     */
    public void ajouterPlat(Plat p) throws PlatIndisponibleException {
        if (p == null) throw new IllegalArgumentException("Plat manquant.");
        if (restaurant != null && !restaurant.proposePlat(p)) {
            throw new PlatIndisponibleException(p.getNom(), restaurant.getNom());
        }
        plats.add(p);
        System.out.println("  + " + p + " ajoute a la commande #" + numero);
    }


    // Methode qui calcule le montant total (somme des plats + pourboire)

    public double calculerTotal() {
        double total = 0.0;
        for (Plat plat : plats) {
            total += plat.getPrix();
        }
        total += pourboire;
        this.montantTotal = total;
        return total;
    }


    /*
     Methode qui valide la commande.
     TP5 : CommandeVideException si aucun plat n'a ete ajoute.
     */
    public void valider() throws CommandeVideException {
        if (plats.isEmpty()) {
            throw new CommandeVideException(numero);
        }
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
    public double getMontantTotal()   { return calculerTotal(); }
    public double getPourboire()      { return pourboire; }
    public Livraison getLivraison()   { return livraison; }
    public Restaurant getRestaurant() { return restaurant; }
    public Client getClient()         { return client; }
    public Plat[] getPlats()          { return plats.toArray(new Plat[0]); }
    public int getNbPlats()           { return plats.size(); }
    public void setStatut(String s)   { this.statut = s; }
}
