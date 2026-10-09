/*
 Levee quand on valide une commande qui ne contient aucun plat.
 */
public class CommandeVideException extends FastBiteException {
    private static final long serialVersionUID = 1L;


    public CommandeVideException(int numero) {
        super("La commande #" + numero + " est vide : validation impossible");
    }
}
