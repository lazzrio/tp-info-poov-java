package modele;

/*
 Classe Restaurant : propose un menu de Plats et prepare les commandes.
 TP5 : - Q2 : ecriture / lecture / affichage du menu dans un fichier texte (.txt)
       - Q3 : implemente Serializable (liste de restaurants enregistree dans un .ser)

 FORMAT DU FICHIER MENU (.txt) : une ligne = un plat, 4 champs separes par ';'
     nom;prix;description;allergene1,allergene2,...
 - le prix utilise le point comme separateur decimal (12.5)
 - les allergenes sont separes par des virgules ; champ vide = aucun allergene
 - la description ne doit pas contenir de ';'
 Exemple :  Pizza Margherita;12.5;Tomate, mozzarella, basilic;gluten,lactose
 */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class Restaurant implements Evaluable, Serializable {
    // Numero de version de la classe pour la serialisation
    private static final long serialVersionUID = 1L;

    // Separateurs du format de fichier
    private static final String SEPARATEUR = ";";
    private static final String SEPARATEUR_ALLERGENES = ",";

    private int nbNotes;
    private double totalNotes;
    // Attributs
    private String nom;
    private String adresse;
    private Plat[] menu;             // tableau de plats
    private int nbPlats;             // nombre reel de plats dans le menu
    private double noteMoyenne;
    private String zoneLivraison;

    // TRANSIENT : compteur de session, volontairement NON sauvegarde par la serialisation.
    // Apres une deserialisation il vaut 0 (valeur par defaut), c'est normal.
    private transient int nbCommandesRecues;


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


    /*
     Methode qui gere le menu : ajoute un plat.
     TP5 : leve CapaciteDepasseeException si le menu (tableau de 20) est plein
           (exception verifiee) et IllegalArgumentException si le plat est null
           (exception non verifiee : c'est un bug du code appelant, pas un cas a gerer).
     */
    public void gererMenu(Plat p) throws CapaciteDepasseeException {
        if (p == null) {
            throw new IllegalArgumentException("le plat ne peut pas etre null");
        }
        if (nbPlats >= menu.length) {
            throw new CapaciteDepasseeException("le menu de " + nom, menu.length);
        }
        menu[nbPlats] = p;
        nbPlats++;
        System.out.println(p.getNom() + " ajoute au menu de " + nom);
    }


    // Methode qui indique si un plat (meme nom) figure au menu

    public boolean proposePlat(Plat p) {
        for (int i = 0; i < nbPlats; i++) {
            if (menu[i].getNom().equals(p.getNom())) {
                return true;
            }
        }
        return false;
    }


    // Methode qui recoit une commande envoyee par le client

    public void recevoirCommande(Commande c) {
        nbCommandesRecues++;
        System.out.println(nom + " a recu la commande #" + c.getNumero());
    }


    // Methode qui simule la preparation de la commande

    public void preparerCommande(Commande c) {
        c.setStatut("EN_PREPARATION");
        System.out.println(nom + " prepare la commande #" + c.getNumero());
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
            throw new AucuneNoteException("le restaurant " + nom);
        }
        return noteMoyenne;
    }


    // ============================================================
    // TP5 Q2 : fichier texte (.txt)
    // ============================================================

    /*
     Ecrit le menu dans un fichier texte, une ligne par plat (format decrit en haut).
     try-with-resources : le fichier est ferme automatiquement, meme en cas d'erreur.
     IOException est PROPAGEE a l'appelant (throws) : c'est lui qui decide quoi faire.
     */
    public void sauvegarderMenu(String fichier) throws IOException {
        try (BufferedWriter out = new BufferedWriter(new FileWriter(fichier))) {
            for (int i = 0; i < nbPlats; i++) {
                Plat p = menu[i];
                String[] allergenes = p.getAllergenes();
                String listeAllergenes = "";
                for (int j = 0; j < allergenes.length; j++) {
                    listeAllergenes += allergenes[j];
                    if (j < allergenes.length - 1) listeAllergenes += SEPARATEUR_ALLERGENES;
                }
                out.write(p.getNom() + SEPARATEUR + p.getPrix() + SEPARATEUR
                        + p.getDescription() + SEPARATEUR + listeAllergenes);
                out.newLine();
            }
        }
        System.out.println("Menu de " + nom + " ecrit dans " + fichier + " (" + nbPlats + " plats)");
    }


    /*
     Lit un fichier texte et ajoute les plats au menu.
     - IOException : fichier introuvable / illisible (propagee)
     - FormatFichierException : une ligne ne respecte pas le format (numero de ligne indique)
     - CapaciteDepasseeException : le menu serait trop grand
     Les plats sont d'abord lus dans une liste temporaire : si une ligne est
     incorrecte, le menu n'est pas modifie du tout (pas d'etat a moitie charge).
     */
    public void chargerMenu(String fichier)
            throws IOException, FormatFichierException, CapaciteDepasseeException {
        ArrayList<Plat> lus = new ArrayList<Plat>();

        try (BufferedReader in = new BufferedReader(new FileReader(fichier))) {
            String ligne;
            int numeroLigne = 0;
            while ((ligne = in.readLine()) != null) {
                numeroLigne++;
                if (ligne.trim().isEmpty()) continue;       // on ignore les lignes vides

                String[] champs = ligne.split(SEPARATEUR, -1);   // -1 : garde le champ vide final
                if (champs.length != 4) {
                    throw new FormatFichierException(fichier, numeroLigne, ligne,
                            "4 champs attendus, " + champs.length + " trouves");
                }

                double prix;
                try {
                    prix = Double.parseDouble(champs[1]);
                } catch (NumberFormatException e) {
                    // CHAINAGE : on garde l'erreur d'origine comme "cause"
                    throw new FormatFichierException(fichier, numeroLigne, ligne,
                            "prix non numerique", e);
                }

                String[] allergenes;
                if (champs[3].isEmpty()) {
                    allergenes = new String[0];
                } else {
                    allergenes = champs[3].split(SEPARATEUR_ALLERGENES);
                }
                lus.add(new Plat(champs[0], prix, champs[2], allergenes));
            }
        }

        // Tout s'est bien passe : on peut modifier le menu
        for (int i = 0; i < lus.size(); i++) {
            gererMenu(lus.get(i));
        }
        System.out.println(lus.size() + " plat(s) lu(s) depuis " + fichier);
    }


    // Methode qui affiche le menu complet a l'ecran

    public void afficherMenu() {
        System.out.println("Menu de " + nom + " (" + zoneLivraison + ") :");
        for (int i = 0; i < nbPlats; i++) {
            menu[i].afficher();
        }
    }


    // ============================================================
    // TP5 Q3 : serialisation / deserialisation (.ser)
    // ============================================================

    /*
     Serialisation : ecrit toute la liste de restaurants (et, avec eux, leurs Plat)
     en binaire dans un fichier. ArrayList est deja Serializable.
     IOException (dont NotSerializableException) est propagee a l'appelant.
     */
    public static void sauvegarderRestaurants(ArrayList<Restaurant> restaurants, String fichier)
            throws IOException {
        try (FileOutputStream flux = new FileOutputStream(fichier);
             ObjectOutputStream out = new ObjectOutputStream(flux)) {
            out.writeObject(restaurants);
        }
        System.out.println(restaurants.size() + " restaurant(s) serialise(s) dans " + fichier);
    }


    /*
     Deserialisation : relit le fichier et reconstruit la liste.
     readObject() renvoie un Object : on doit le CASTER en ArrayList<Restaurant>.
     (Le compilateur ne peut pas verifier le type generique : d'ou @SuppressWarnings.)
     ClassNotFoundException (verifiee) : la classe lue n'existe pas dans ce programme.
     */
    @SuppressWarnings("unchecked")
    public static ArrayList<Restaurant> chargerRestaurants(String fichier)
            throws IOException, ClassNotFoundException {
        // TP6 : deux ressources separees. Si le fichier n'est pas un flux Java, le constructeur
        // d'ObjectInputStream leve une IOException : le FileInputStream est quand meme ferme.
        try (FileInputStream flux = new FileInputStream(fichier);
             ObjectInputStream in = new ObjectInputStream(flux)) {
            ArrayList<Restaurant> restaurants = (ArrayList<Restaurant>) in.readObject();
            System.out.println(restaurants.size() + " restaurant(s) deserialise(s) depuis " + fichier);
            return restaurants;
        }
    }


    // Getters

    public String getNom()              { return nom; }
    public String getAdresse()          { return adresse; }
    public Plat[] getMenu()             { return menu; }
    public int getNbPlats()             { return nbPlats; }
    public String getZoneLivraison()    { return zoneLivraison; }
    public int getNbCommandesRecues()   { return nbCommandesRecues; }
}
