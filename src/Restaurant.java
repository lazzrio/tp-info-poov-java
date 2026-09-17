/*
 Classe Restaurant : propose un menu de Plats et prepare les commandes.
 */
public class Restaurant {
    // Attributs
    private String nom;
    private String adresse;
    private Plat[] menu;             // tableau de plats
    private int nbPlats;             // nombre reel de plats dans le menu
    private double noteMoyenne;
    private String zoneLivraison;


    // Constructeur par defaut

    public Restaurant() {
        this.nom = "Restaurant";
        this.adresse = "";
        this.menu = new Plat[20];
        this.nbPlats = 0;
        this.noteMoyenne = 0.0;
        this.zoneLivraison = "";
    }

    /*
     Constructeur parametre
     En parametre : nom, adresse, zone de livraison
     */
    public Restaurant(String nom, String adresse, String zoneLivraison) {
        this.nom = nom;
        this.adresse = adresse;
        this.menu = new Plat[20];
        this.nbPlats = 0;
        this.noteMoyenne = 0.0;
        this.zoneLivraison = zoneLivraison;
    }


    // Methode qui gere le menu : ajoute un plat

    public void gererMenu(Plat p) {
        menu[nbPlats] = p;
        nbPlats++;
        System.out.println(p.getNom() + " ajoute au menu de " + nom);
    }


    // Methode qui recoit une commande envoyee par le client

    public void recevoirCommande(Commande c) {
        System.out.println(nom + " a recu la commande #" + c.getNumero());
    }


    // Methode qui simule la preparation de la commande

    public void preparerCommande(Commande c) {
        c.setStatut("EN_PREPARATION");
        System.out.println(nom + " prepare la commande #" + c.getNumero());
    }


    // Getters

    public String getNom()           { return nom; }
    public String getAdresse()       { return adresse; }
    public Plat[] getMenu()          { return menu; }
    public int getNbPlats()          { return nbPlats; }
    public double getNoteMoyenne()   { return noteMoyenne; }
    public String getZoneLivraison() { return zoneLivraison; }
}
