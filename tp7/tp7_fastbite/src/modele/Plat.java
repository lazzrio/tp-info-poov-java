package modele;

import java.io.Serializable;

/*
 Classe Plat : un plat propose au menu d'un Restaurant.
 Un Plat est agrege par une Commande (existe sans la commande).
 TP5 : implemente Serializable (Q3). Obligatoire : un Restaurant ne peut etre
 serialise que si les Plat de son menu le sont aussi.
 */
public class Plat implements Evaluable, Serializable {
    // Numero de version de la classe pour la serialisation
    private static final long serialVersionUID = 1L;

    private double noteMoyenne;
    private int nbNotes;
    private double totalNotes;
    // Attributs
    private String nom;
    private double prix;
    private String description;
    private String[] allergenes;


    // Constructeur par defaut

    public Plat() {
        this.nom = "Plat";
        this.prix = 0.0;
        this.description = "";
        this.allergenes = new String[0];
    }

    /*
     Constructeur parametre
     En parametre : nom, prix, description, tableau d'allergenes
     */
    public Plat(String nom, double prix, String description, String[] allergenes) {
        this.nom = nom;
        this.prix = prix;
        this.description = description;
        this.allergenes = allergenes;
    }


    // Methode qui affiche le plat + ses allergenes

    public void afficher() {
        System.out.print("- " + nom + " : " + prix + " EUR (" + description + ")");
        if (allergenes.length > 0) {
            System.out.print(" [allergenes : ");
            for (int i = 0; i < allergenes.length; i++) {
                System.out.print(allergenes[i]);
                if (i < allergenes.length - 1) System.out.print(", ");
            }
            System.out.print("]");
        }
        System.out.println();
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
            throw new AucuneNoteException("le plat \"" + nom + "\"");
        }
        return noteMoyenne;
    }

    // Getters

    public String getNom()           { return nom; }
    public double getPrix()          { return prix; }
    public String getDescription()   { return description; }
    public String[] getAllergenes()  { return allergenes; }

    @Override
    public String toString() {
        return nom + " (" + prix + " EUR)";
    }
}
