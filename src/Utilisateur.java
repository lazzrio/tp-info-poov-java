/*
 Classe mere de tous les utilisateurs de l'application.
 Sert de base a l'heritage vers Client et Livreur.
 */
public class Utilisateur {
    // Attributs
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
     En parametre : nom, email, mot de passe
     */
    public Utilisateur(String nom, String email, String motDePasse) {
        this.nom = nom;
        this.email = email;
        this.motDePasse = motDePasse;
    }


    // Methode qui verifie si les identifiants correspondent

    public boolean seConnecter(String email, String mdp) {
        return this.email.equals(email) && this.motDePasse.equals(mdp);
    }


    // Methode qui simule une deconnexion

    public void seDeconnecter() {
        System.out.println(nom + " s'est deconnecte.");
    }


    // Methode qui permet de modifier les infos de profil

    public void modifierProfil(String nouveauNom, String nouveauEmail) {
        this.nom = nouveauNom;
        this.email = nouveauEmail;
        System.out.println("Profil de " + nom + " mis a jour.");
    }


    // Getters

    public String getNom()   { return nom; }
    public String getEmail() { return email; }
}
