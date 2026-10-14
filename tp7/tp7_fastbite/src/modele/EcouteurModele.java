package modele;

/*
 TP7 - Pattern Observer, utilise par MVC : le MODELE ne connait pas les vues, il connait
 seulement cette interface. Toute vue qui veut etre prevenue d'un changement des donnees
 l'implemente et s'enregistre aupres du modele (Donnees.ajouterEcouteur).
 */
public interface EcouteurModele {
    // Appelee par le modele apres chaque modification : la vue relit alors le modele et se redessine
    void modeleModifie();
}
