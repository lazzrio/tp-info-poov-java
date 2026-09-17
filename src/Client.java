import java.util.ArrayList;

/*
 Classe Client, herite de Utilisateur.
 Represente un utilisateur qui commande des repas.
 */
public class Client extends Utilisateur {
    // Attributs specifiques au client
    private String adresseLivraison;
    private ArrayList<Commande> historique;
    private double solde;


    // Constructeur par defaut

    public Client() {
        super();
        this.adresseLivraison = "";
        this.historique = new ArrayList<Commande>();
        this.solde = 0.0;
    }

    /*
     Constructeur parametre
     En parametre : nom, email, mot de passe, adresse de livraison
     */
    public Client(String nom, String email, String motDePasse, String adresseLivraison) {
        super(nom, email, motDePasse);   // appel du constructeur de la classe mere
        this.adresseLivraison = adresseLivraison;
        this.historique = new ArrayList<Commande>();
        this.solde = 50.0;               // 50 EUR de credit initial
    }

    /*
     Methode qui cree une nouvelle commande chez un restaurant
     En parametre : le restaurant et le numero de commande
     Renvoie la commande creee
     */
    public Commande passerCommande(Restaurant r, int numero) {
        Commande c = new Commande(numero, this, r);
        historique.add(c);
        System.out.println(nom + " passe la commande #" + numero + " chez " + r.getNom());
        return c;
    }


    // Methode qui debite le solde du client si suffisant

    public boolean payerCommande(Commande c) {
        if (solde >= c.getMontantTotal()) {
            solde -= c.getMontantTotal();
            c.setStatut("PAYEE");
            System.out.println(nom + " a paye " + c.getMontantTotal() + " EUR (solde restant : " + solde + ")");
            return true;
        }
        System.out.println("Solde insuffisant pour " + nom);
        return false;
    }


    // Methode qui permet au client de noter un livreur

    public void noterLivreur(Livreur l, int note) {
        System.out.println(nom + " note " + l.getNom() + " : " + note + "/5");
    }


    // Getters

    public String getAdresseLivraison() { return adresseLivraison; }
    public double getSolde()            { return solde; }
    public ArrayList<Commande> getHistorique() { return historique; }
}
