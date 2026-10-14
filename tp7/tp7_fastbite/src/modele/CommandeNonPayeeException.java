package modele;

/*
 Levee quand on essaie de lancer la livraison d'une commande pas encore payee :
 le livreur ne part qu'apres le paiement. Regle metier (elle etait dans la fenetre au TP6).
 */
public class CommandeNonPayeeException extends FastBiteException {
    private static final long serialVersionUID = 1L;

    public CommandeNonPayeeException(int numero) {
        super("Payez d'abord la commande #" + numero + " : le livreur ne part qu'apres le paiement.");
    }
}
