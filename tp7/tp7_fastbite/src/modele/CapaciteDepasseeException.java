package modele;

/*
 Levee quand un tableau de taille fixe est plein :
 menu d'un Restaurant, plats d'une Commande, historique d'un Client.
 Remplace l'ArrayIndexOutOfBoundsException brutale que Java lancerait.
 */
public class CapaciteDepasseeException extends FastBiteException {
    private static final long serialVersionUID = 1L;

    private final int capacite;

    public CapaciteDepasseeException(String conteneur, int capacite) {
        super("Capacite depassee : " + conteneur + " ne peut pas contenir plus de "
                + capacite + " elements");
        this.capacite = capacite;
    }

    public int getCapacite() { return capacite; }
}
