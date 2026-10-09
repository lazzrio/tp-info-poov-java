import java.util.ArrayList;

/*
 TP6 : donnees partagees par toutes les fenetres de l'application.
 Une seule instance est creee dans FastBiteGUI.main() puis passee a chaque fenetre
 (les fenetres ne se parlent que par cet objet et par leurs constructeurs).
 */
public class Donnees {
    // Fichiers utilises par l'interface (dossier data/)
    public static final String FICHIER_SER = "data/restaurants.ser";
    public static final String DOSSIER_DATA = "data";

    private ArrayList<Restaurant> restaurants = new ArrayList<Restaurant>();
    private final Client client;
    private final Livreur livreur;       // notre livreur : il prend en charge les commandes payees
    private int prochainNumero = 1;      // numero de la prochaine commande
    private Commande commandeSuivie;     // commande en cours de livraison (null s'il n'y en a pas)

    public Donnees() {
        this.client = new Client("Alice", "alice@mail.com", "1234", "5 avenue Foch", true);
        this.livreur = new Livreur("Bob", "bob@mail.com", "abcd", "scooter");
    }

    /*
     Jeu d'essai : les restaurants du TP5 + Yoshi Sushi.
     gererMenu() peut lever CapaciteDepasseeException : on la PROPAGE, c'est
     l'appelant (FastBiteGUI) qui l'attrape et l'affiche dans une boite de dialogue.
     */
    public void creerJeuDEssai() throws CapaciteDepasseeException {
        Restaurant mario = new Restaurant("Chez Mario", "12 rue de Paris", "Paris 16");
        mario.gererMenu(new Plat("Pizza Margherita", 12.50, "Tomate, mozzarella, basilic",
                new String[]{"gluten", "lactose"}));
        mario.gererMenu(new Plat("Pasta Carbonara", 10.00, "Lardons, oeuf, parmesan",
                new String[]{"gluten", "oeuf", "lactose"}));
        mario.gererMenu(new Plat("Eau minerale", 2.0, "Bouteille 50cl", new String[0]));

        Restaurant yoshi = new Restaurant("Yoshi Sushi", "27 rue de Passy", "Paris 16");
        yoshi.gererMenu(new Plat("California roll", 8.50, "Saumon, avocat, sesame (6 pieces)",
                new String[]{"poisson", "sesame"}));
        yoshi.gererMenu(new Plat("Sashimi saumon", 12.00, "Saumon frais (8 tranches)",
                new String[]{"poisson"}));
        yoshi.gererMenu(new Plat("Ramen poulet", 11.50, "Bouillon miso, nouilles, poulet, oeuf",
                new String[]{"gluten", "oeuf", "soja"}));
        yoshi.gererMenu(new Plat("Mochi glace", 4.50, "Vanille ou the matcha (2 pieces)",
                new String[]{"lactose"}));

        Restaurant sushi = new Restaurant("Sushi Tokyo", "8 rue Boileau", "Paris 16");
        sushi.gererMenu(new Plat("Sushi saumon", 9.0, "Riz, saumon", new String[]{"poisson"}));
        sushi.gererMenu(new Plat("Maki avocat", 6.5, "Riz, avocat, algue", new String[0]));

        restaurants.add(mario);
        restaurants.add(yoshi);
        restaurants.add(sushi);
    }

    public ArrayList<Restaurant> getRestaurants()          { return restaurants; }
    public void setRestaurants(ArrayList<Restaurant> liste) { this.restaurants = liste; }
    public Client getClient()                               { return client; }
    public Livreur getLivreur()                             { return livreur; }
    public Commande getCommandeSuivie()                     { return commandeSuivie; }
    public void setCommandeSuivie(Commande c)               { this.commandeSuivie = c; }

    // Renvoie un numero de commande puis l'incremente
    public int numeroSuivant() {
        int n = prochainNumero;
        prochainNumero++;
        return n;
    }

    // Nom du fichier texte du menu d'un restaurant : "Chez Mario" -> data/menu_chez_mario.txt
    public static String fichierMenu(Restaurant r) {
        return DOSSIER_DATA + "/menu_" + r.getNom().toLowerCase().replace(' ', '_') + ".txt";
    }
}
