/*
 Classe Livreur, herite de Utilisateur.
 Represente un rider qui assure les livraisons.
 */
public class Livreur extends Utilisateur {
    // Attributs
    private String vehicule;
    private boolean disponible;
    private String positionGPS;
    private double noteMoyenne;

    // Compteur interne pour recalculer la note moyenne
    private int nbNotes;
    private double totalNotes;


    // Constructeur par defaut

    public Livreur() {
        super();
        this.vehicule = "velo";
        this.disponible = true;
        this.positionGPS = "0,0";
        this.noteMoyenne = 0.0;
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
        this.noteMoyenne = 0.0;
    }


    // Methode qui accepte une livraison si le livreur est disponible

    public void accepterLivraison(Livraison l) {
        if (disponible) {
            disponible = false;
            l.setStatut("EN_COURS");
            System.out.println(nom + " (" + vehicule + ") accepte la livraison #"
                    + l.getCommande().getNumero());
        } else {
            System.out.println(nom + " n'est pas disponible.");
        }
    }


    // Methode qui met a jour la position GPS du livreur

    public void seGeolocaliser(String gps) {
        this.positionGPS = gps;
        System.out.println(nom + " est maintenant en " + gps);
    }


    // Methode qui confirme la livraison terminee

    public void confirmerLivraison(Livraison l) {
        l.setStatut("LIVREE");
        disponible = true;
        System.out.println(nom + " a livre la commande #" + l.getCommande().getNumero());
    }


    // Methode interne pour ajouter une note et recalculer la moyenne

    public void recevoirNote(double note) {
        totalNotes += note;
        nbNotes++;
        noteMoyenne = totalNotes / nbNotes;
    }


    // Getters

    public boolean isDisponible() { return disponible; }
    public String getVehicule()   { return vehicule; }
    public String getPositionGPS(){ return positionGPS; }
    public double getNoteMoyenne(){ return noteMoyenne; }
}
