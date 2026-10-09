/*
 Levee quand le paiement d'une commande est refuse
 (commande vide ou deja payee).
 */
public class PaiementImpossibleException extends FastBiteException {
    private static final long serialVersionUID = 1L;


    public PaiementImpossibleException(int numero, String raison) {
        super("Paiement impossible pour la commande #" + numero + " : " + raison);
    }
}
