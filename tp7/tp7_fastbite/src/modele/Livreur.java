package modele;

/*
 Classe Livreur, herite de Utilisateur.
 Represente un rider qui assure les livraisons.
 */
public class Livreur extends Utilisateur implements Evaluable {
    @Override
    public void afficherProfil() {
        System.out.println("Livreur : " + nom + " | vehicule : " + vehicule
                + " | disponible : " + disponible);
    }

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


    /*
     Methode qui accepte une livraison si le livreur est disponible.
     TP5 : leve LivreurIndisponibleException au lieu d'afficher un simple message
     (l'appelant peut ainsi reagir : proposer la mission a un autre livreur).
     */
    public void accepterLivraison(Livraison l) throws LivreurIndisponibleException {
        if (!disponible) {
            throw new LivreurIndisponibleException(nom);
        }
        disponible = false;
        l.setStatut("EN_COURS");
        System.out.println(nom + " (" + vehicule + ") accepte la livraison #"
                + l.getCommande().getNumero());
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

    public void recevoirNote(double note) throws NoteInvalideException {
        ajouterNote(note); // Compatibilite avec le code du TP3.
    }


    // Q1 : moyenne de toutes les notes, avec controle de la saisie.
    // TP5 : NoteInvalideException (verifiee) remplace IllegalArgumentException.
    @Override
    public void ajouterNote(double note) throws NoteInvalideException {
        if (Double.isNaN(note) || Double.isInfinite(note) || note < NOTE_MIN || note > NOTE_MAX) {
            throw new NoteInvalideException(note);
        }
        totalNotes += note;
        nbNotes++;
        noteMoyenne = totalNotes / nbNotes;
    }

    // TP5 : AucuneNoteException si aucune note (TP4 renvoyait 0.0)
    @Override
    public double getNoteMoyenne() throws AucuneNoteException {
        if (nbNotes == 0) {
            throw new AucuneNoteException("le livreur " + nom);
        }
        return noteMoyenne;
    }

    // Getters

    public boolean isDisponible() { return disponible; }
    public String getVehicule()   { return vehicule; }
    public String getPositionGPS(){ return positionGPS; }
}
