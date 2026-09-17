/*
 Classe Livreur, herite de Utilisateur.
 Represente un livreur qui prend en charge les livraisons.
 */
public class Livreur extends Utilisateur {
    // Attributs specifiques au livreur
    private String vehicule;
    private boolean disponible;
    private String positionGPS;


    // Constructeur par defaut

    public Livreur() {
        super();
        this.vehicule = "velo";
        this.disponible = true;
        this.positionGPS = "0,0";
    }

    /*
     Constructeur parametre
     En parametre : nom, email, mot de passe, type de vehicule
     */
    public Livreur(String nom, String email, String motDePasse, String vehicule) {
        super(nom, email, motDePasse);
        this.vehicule = vehicule;
        this.disponible = true;
        this.positionGPS = "48.85,2.35";
    }


    // Methode qui accepte une livraison si le livreur est disponible

    public void accepterLivraison(Livraison l) {
        if (disponible) {
            disponible = false;
            l.setStatut("EN_COURS");
            System.out.println(nom + " (" + vehicule + ") accepte la livraison #" + l.getCommande().getNumero());
        } else {
            System.out.println(nom + " n'est pas disponible.");
        }
    }


    // Methode qui confirme la livraison terminee

    public void confirmerLivraison(Livraison l) {
        l.setStatut("LIVREE");
        disponible = true;
        System.out.println(nom + " a livre la commande #" + l.getCommande().getNumero());
    }


    // Methode qui met a jour la position GPS du livreur

    public void mettreAJourPosition(String gps) {
        this.positionGPS = gps;
    }


    // Getters

    public boolean isDisponible() { return disponible; }
    public String getVehicule()   { return vehicule; }
    public String getPositionGPS(){ return positionGPS; }
}
