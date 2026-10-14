package modele;

/*
 Levee quand on veut noter le livreur avant la fin de la livraison.
 Regle metier (elle etait dans la fenetre au TP6).
 */
public class NotationImpossibleException extends FastBiteException {
    private static final long serialVersionUID = 1L;

    public NotationImpossibleException(String livreur) {
        super("Vous pourrez noter " + livreur + " une fois la commande livree.");
    }
}
