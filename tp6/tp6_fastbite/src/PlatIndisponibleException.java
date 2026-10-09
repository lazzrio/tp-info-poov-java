/*
 Levee quand on ajoute a une commande un plat qui n'est pas au menu du restaurant.
 */
public class PlatIndisponibleException extends FastBiteException {
    private static final long serialVersionUID = 1L;


    public PlatIndisponibleException(String plat, String restaurant) {
        super("Le plat \"" + plat + "\" n'est pas au menu de " + restaurant);
    }
}
