import java.util.ArrayList;

/*
 Classe Restaurant : propose un menu de Plats et recoit les commandes.
 */
public class Restaurant {
    // Attributs
    private String nom;
    private String adresse;
    private ArrayList<Plat> menu;


    // Constructeur par defaut

    public Restaurant() {
        this.nom = "Restaurant";
        this.adresse = "";
        this.menu = new ArrayList<Plat>();
    }

    /*
     Constructeur parametre
     En parametre : nom, adresse
     */
    public Restaurant(String nom, String adresse) {
        this.nom = nom;
        this.adresse = adresse;
        this.menu = new ArrayList<Plat>();
    }


    // Methode qui ajoute un plat au menu du restaurant

    public void ajouterPlat(Plat p) {
        menu.add(p);
    }


    // Methode qui verifie si un plat est disponible dans le menu

    public boolean confirmerDisponibilite(Plat p) {
        boolean dispo = menu.contains(p);
        System.out.println(nom + " confirme disponibilite de " + p.getNom() + " : " + dispo);
        return dispo;
    }


    // Methode qui recoit une commande envoyee par le client

    public void recevoirCommande(Commande c) {
        System.out.println(nom + " a recu la commande #" + c.getNumero());
    }


    // Getters

    public String getNom()           { return nom; }
    public String getAdresse()       { return adresse; }
    public ArrayList<Plat> getMenu() { return menu; }
}
