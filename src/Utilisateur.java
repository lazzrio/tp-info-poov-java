import java.util.Objects;

/*
 Classe mere de tous les utilisateurs de l'application FastBite.
 Sert de base a l'heritage vers Client et Livreur (Q2 TP2).
 */
public class Utilisateur {
    // Attributs communs a tous les utilisateurs
    protected String nom;
    protected String email;
    protected String motDePasse;


    // Constructeur par defaut

    public Utilisateur() {
        this.nom = "Inconnu";
        this.email = "";
        this.motDePasse = "";
    }

    /*
     Constructeur parametre
     En parametre : nom, email et mot de passe
     */
    public Utilisateur(String nom, String email, String motDePasse) {
        this.nom = nom;
        this.email = email;
        this.motDePasse = motDePasse;
    }


    // Methode qui verifie si les identifiants correspondent

    public boolean seConnecter(String email, String mdp) {
        return Objects.equals(this.email, email) && Objects.equals(this.motDePasse, mdp);
    }


    // Methode qui simule une deconnexion

    public void seDeconnecter() {
        System.out.println(nom + " s'est deconnecte.");
    }


    // Getters

    public String getNom()   { return nom; }
    public String getEmail() { return email; }


    // Affichage textuel de l'utilisateur

    @Override
    public String toString() {
        return "Utilisateur[" + nom + ", " + email + "]";
    }
}
