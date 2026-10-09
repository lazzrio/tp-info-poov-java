/*
 Classe Client, herite de Utilisateur.
 Represente un utilisateur qui commande des repas.
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Client extends Utilisateur {
    private final List<Panier> paniersSauvegardes = new ArrayList<>();

    // TP4 (inchange) : IllegalArgumentException = erreur de programmation, non verifiee
    public void sauvegarderPanier(Panier panier) {
        if (panier == null || panier.getClient() != this) {
            throw new IllegalArgumentException("Ce panier doit appartenir au client.");
        }
        if (!paniersSauvegardes.contains(panier)) paniersSauvegardes.add(panier);
    }

    public List<Panier> getPaniersSauvegardes() {
        return Collections.unmodifiableList(paniersSauvegardes);
    }
    @Override
    public void afficherProfil() {
        System.out.println("Client : " + nom + " | adresse : " + adresseLivraison
                + " | Plus : " + abonnementPlus);
    }

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


    /*
     Methode qui cree une nouvelle commande chez un restaurant.
     TP5 : leve CapaciteDepasseeException si l'historique (tableau de 20) est plein,
     au lieu d'un ArrayIndexOutOfBoundsException a la 21e commande.
     */
    public Commande passerCommande(Restaurant r, int numero) throws CapaciteDepasseeException {
        if (nbCommandes >= historique.length) {
            throw new CapaciteDepasseeException("l'historique de " + nom, historique.length);
        }
        Commande c = new Commande(numero, this, r);
        historique[nbCommandes] = c;
        nbCommandes++;
        System.out.println(nom + " passe la commande #" + numero + " chez " + r.getNom());
        return c;
    }


    /*
     Methode qui paye une commande (livraison gratuite si abonnement Plus).
     TP5 : leve PaiementImpossibleException si la commande est vide ou deja payee.
     */
    public void payerCommande(Commande c) throws PaiementImpossibleException {
        if (c.getNbPlats() == 0) {
            throw new PaiementImpossibleException(c.getNumero(), "la commande est vide");
        }
        if (c.getStatut().equals("PAYEE")) {
            throw new PaiementImpossibleException(c.getNumero(), "la commande est deja payee");
        }
        double montant = c.calculerTotal();
        if (abonnementPlus) {
            System.out.println(nom + " (Plus) paye " + montant + " EUR sans frais de livraison.");
        } else {
            System.out.println(nom + " paye " + montant + " EUR.");
        }
        c.setStatut("PAYEE");
    }


    /*
     Methode qui permet au client de noter un livreur (rider).
     TP5 : PROPAGE la NoteInvalideException levee par Livreur.recevoirNote()
     (pas de try/catch ici : c'est a l'appelant de decider quoi faire).
     */
    public void noterRider(Livreur l, double note) throws NoteInvalideException {
        l.recevoirNote(note);
        System.out.println(nom + " note " + l.getNom() + " : " + note + "/5");
    }


    // Getters

    public String getAdresseLivraison() { return adresseLivraison; }
    public boolean isAbonnementPlus()   { return abonnementPlus; }
    public Commande[] getHistorique()   { return historique; }
    public int getNbCommandes()         { return nbCommandes; }
}
