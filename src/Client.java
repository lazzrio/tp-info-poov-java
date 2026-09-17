/*
 Classe Client, herite de Utilisateur.
 Represente un utilisateur qui commande des repas.
 */
public class Client extends Utilisateur {
    // Attributs
    private String adresseLivraison;
    private Commande[] historique;   // tableau de commandes
    private int nbCommandes;         // nombre reel d'elements dans le tableau
    private boolean abonnementPlus;


    // Constructeur par defaut

    public Client() {
        super();
        this.adresseLivraison = "";
        this.historique = new Commande[20];
        this.nbCommandes = 0;
        this.abonnementPlus = false;
    }

    /*
     Constructeur parametre
     En parametre : nom, email, mot de passe, adresse de livraison, abonnement Plus
     */
    public Client(String nom, String email, String motDePasse,
                  String adresseLivraison, boolean abonnementPlus) {
        super(nom, email, motDePasse);
        this.adresseLivraison = adresseLivraison;
        this.historique = new Commande[20];
        this.nbCommandes = 0;
        this.abonnementPlus = abonnementPlus;
    }


    // Methode qui cree une nouvelle commande chez un restaurant

    public Commande passerCommande(Restaurant r, int numero) {
        Commande c = new Commande(numero, this, r);
        historique[nbCommandes] = c;
        nbCommandes++;
        System.out.println(nom + " passe la commande #" + numero + " chez " + r.getNom());
        return c;
    }


    // Methode qui paye une commande (livraison gratuite si abonnement Plus)

    public void payerCommande(Commande c) {
        double montant = c.calculerTotal();
        if (abonnementPlus) {
            System.out.println(nom + " (Plus) paye " + montant + " EUR sans frais de livraison.");
        } else {
            System.out.println(nom + " paye " + montant + " EUR.");
        }
        c.setStatut("PAYEE");
    }


    // Methode qui permet au client de noter un livreur (rider)

    public void noterRider(Livreur l, double note) {
        l.recevoirNote(note);
        System.out.println(nom + " note " + l.getNom() + " : " + note + "/5");
    }


    // Getters

    public String getAdresseLivraison() { return adresseLivraison; }
    public boolean isAbonnementPlus()   { return abonnementPlus; }
    public Commande[] getHistorique()   { return historique; }
    public int getNbCommandes()         { return nbCommandes; }
}
