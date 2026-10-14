package modele;

/*
 Levee quand on appelle une action de Livraison dans un etat incoherent
 (terminer une livraison jamais demarree, demarrer une livraison deja en route).
 */
public class EtatLivraisonException extends FastBiteException {
    private static final long serialVersionUID = 1L;


    public EtatLivraisonException(int numero, String statut, String action) {
        super("Livraison #" + numero + " : impossible de " + action + " (statut actuel : " + statut + ")");
    }
}
