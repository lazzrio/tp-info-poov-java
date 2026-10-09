/*
 Levee quand un livreur deja en course tente d'accepter une nouvelle livraison.
 */
public class LivreurIndisponibleException extends FastBiteException {
    private static final long serialVersionUID = 1L;


    public LivreurIndisponibleException(String nom) {
        super("Le livreur " + nom + " n'est pas disponible");
    }
}
