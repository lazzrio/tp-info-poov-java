package modele;

/*
 Levee quand un pourboire est refuse : montant negatif, ou commande deja payee
 (le total ne peut plus changer). Cette regle etait dans la fenetre au TP6 :
 elle appartient au modele.
 */
public class PourboireInvalideException extends FastBiteException {
    private static final long serialVersionUID = 1L;

    public PourboireInvalideException(String raison) {
        super(raison);
    }
}
