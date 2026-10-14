package modele;

import java.io.IOException;
import java.util.ArrayList;

/*
 TP7 - MODELE du pattern MVC.
 Donnees regroupe l'etat de l'application (restaurants, client, livreur, commande suivie) ET les
 actions metier que les controleurs peuvent demander. Regles du MVC respectees ici :
  - le modele n'importe NI Swing NI la vue NI les controleurs : il ne sait pas qu'ils existent ;
  - apres chaque modification, il PREVIENT ses ecouteurs (pattern Observer) : ce sont les vues
    qui se redessinent, ni le modele ni le controleur ne touchent a l'affichage.
 */
public class Donnees {
    // Fichiers utilises par l'application (dossier data/)
    public static final String FICHIER_SER = "data/restaurants.ser";
    public static final String DOSSIER_DATA = "data";

    private ArrayList<Restaurant> restaurants = new ArrayList<Restaurant>();
    private final Client client;
    private final Livreur livreur;       // notre livreur : il prend en charge les commandes payees
    private int prochainNumero = 1;      // numero de la prochaine commande
    private Commande commandeSuivie;     // commande en cours de livraison (null s'il n'y en a pas)

    // Les vues abonnees (Observer)
    private final ArrayList<EcouteurModele> ecouteurs = new ArrayList<EcouteurModele>();

    public Donnees() {
        this.client = new Client("Alice", "alice@mail.com", "1234", "5 avenue Foch", true);
        this.livreur = new Livreur("Bob", "bob@mail.com", "abcd", "scooter");
    }

    // ------------------------------------------------------------------ Observer
    public void ajouterEcouteur(EcouteurModele e) {
        if (!ecouteurs.contains(e)) ecouteurs.add(e);
    }

    public void retirerEcouteur(EcouteurModele e) {
        ecouteurs.remove(e);
    }

    // Previent toutes les vues : elles relisent le modele et se redessinent
    public void notifierEcouteurs() {
        for (EcouteurModele e : new ArrayList<EcouteurModele>(ecouteurs)) {
            e.modeleModifie();
        }
    }

    // ------------------------------------------------------------------ Jeu d'essai
    /*
     Jeu d'essai : les restaurants du TP5 + Yoshi Sushi.
     gererMenu() peut lever CapaciteDepasseeException : on la PROPAGE a l'appelant (le controleur).
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
        notifierEcouteurs();
    }

    // ------------------------------------------------------------------ Lecture (les vues lisent)
    public ArrayList<Restaurant> getRestaurants()          { return restaurants; }
    public Client getClient()                               { return client; }
    public Livreur getLivreur()                             { return livreur; }
    public Commande getCommandeSuivie()                     { return commandeSuivie; }

    // ------------------------------------------------------------------ Actions metier (les controleurs demandent)

    // Remplace toute la liste (apres un chargement .ser, ou pour « perdre » les donnees dans un test)
    public void setRestaurants(ArrayList<Restaurant> liste) {
        this.restaurants = liste;
        notifierEcouteurs();
    }

    // Le client commence une commande chez ce restaurant : c'est son panier
    public Commande nouvelleCommande(Restaurant r) throws CapaciteDepasseeException {
        Commande c = client.passerCommande(r, prochainNumero);
        prochainNumero++;
        notifierEcouteurs();
        return c;
    }

    public void ajouterPlat(Commande c, Plat p) throws PlatIndisponibleException {
        c.ajouterPlat(p);
        notifierEcouteurs();
    }

    // Valide la commande (CommandeVideException si panier vide) puis la transmet au restaurant
    public void validerCommande(Commande c) throws CommandeVideException {
        c.valider();
        c.getRestaurant().recevoirCommande(c);
        notifierEcouteurs();
    }

    // Regle metier : pas de pourboire negatif, ni sur une commande deja payee
    public void appliquerPourboire(Commande c, double pourboire) throws PourboireInvalideException {
        if (pourboire < 0) {
            throw new PourboireInvalideException("Le pourboire ne peut pas etre negatif.");
        }
        if (c.getStatut().equals("PAYEE")) {
            throw new PourboireInvalideException("La commande est deja payee : le total ne peut plus changer.");
        }
        c.setPourboire(pourboire);
        notifierEcouteurs();
    }

    public void payer(Commande c) throws PaiementImpossibleException {
        client.payerCommande(c);
        notifierEcouteurs();
    }

    public void noterRestaurant(Restaurant r, double note) throws NoteInvalideException {
        r.ajouterNote(note);
        notifierEcouteurs();
    }

    // Regle metier : on ne note le livreur qu'une fois la commande livree
    public void noterLivreur(Commande c, double note) throws NotationImpossibleException, NoteInvalideException {
        Livraison l = c.getLivraison();
        if (l == null || !l.getStatut().equals("LIVREE")) {
            throw new NotationImpossibleException(livreur.getNom());
        }
        client.noterRider(livreur, note);
        notifierEcouteurs();
    }

    /*
     Chaque appel fait avancer la livraison d'une etape :
     EN_ATTENTE -> EN_COURS (le livreur accepte) -> EN_ROUTE (il part) -> LIVREE (livree).
     Regle metier : pas de livraison avant le paiement. Renvoie la livraison pour que la vue
     puisse afficher son nouvel etat.
     */
    public Livraison avancerLivraison(Commande c)
            throws CommandeNonPayeeException, LivreurIndisponibleException, EtatLivraisonException {
        if (!c.getStatut().equals("PAYEE")) {
            throw new CommandeNonPayeeException(c.getNumero());
        }
        Livraison l = c.getLivraison();
        if (l == null) {
            l = c.genererLivraison(livreur, 20);          // composition : la commande cree sa livraison
        }
        String s = l.getStatut();
        if (s.equals("EN_ATTENTE")) {
            livreur.accepterLivraison(l);
            commandeSuivie = c;                           // l'accueil pourra rouvrir cette commande
        } else if (s.equals("EN_COURS")) {
            l.demarrer();
        } else if (s.equals("EN_ROUTE")) {
            l.terminer();
            livreur.confirmerLivraison(l);                // le livreur redevient disponible
            commandeSuivie = null;
        }
        notifierEcouteurs();
        return l;
    }

    // ------------------------------------------------------------------ Fichiers (TP5)
    public void sauvegarderRestaurants() throws IOException {
        Restaurant.sauvegarderRestaurants(restaurants, FICHIER_SER);
    }

    public void chargerRestaurants() throws IOException, ClassNotFoundException {
        setRestaurants(Restaurant.chargerRestaurants(FICHIER_SER));
    }

    public void sauvegarderMenu(Restaurant r) throws IOException {
        r.sauvegarderMenu(fichierMenu(r));
    }

    /*
     Lit un menu dans un restaurant NEUF : si le fichier est incorrect, le restaurant courant n'est pas
     touche. En cas de succes il remplace l'ancien dans la liste ; on le renvoie.
     */
    public Restaurant chargerMenu(int indice, String fichier)
            throws FormatFichierException, IOException, CapaciteDepasseeException {
        Restaurant ancien = restaurants.get(indice);
        Restaurant relu = new Restaurant(ancien.getNom(), ancien.getAdresse(), ancien.getZoneLivraison());
        relu.chargerMenu(fichier);
        restaurants.set(indice, relu);
        notifierEcouteurs();
        return relu;
    }

    // Nom du fichier texte du menu d'un restaurant : "Chez Mario" -> data/menu_chez_mario.txt
    public static String fichierMenu(Restaurant r) {
        return DOSSIER_DATA + "/menu_" + r.getNom().toLowerCase().replace(' ', '_') + ".txt";
    }
}
