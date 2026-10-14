package modele;

/*
 Levee quand on demande une note moyenne alors qu'aucune note n'a ete enregistree
 (sinon on diviserait la somme des notes par 0).
 */
public class AucuneNoteException extends FastBiteException {
    private static final long serialVersionUID = 1L;


    public AucuneNoteException(String proprietaire) {
        super("Aucune note enregistree pour " + proprietaire + " : moyenne impossible");
    }
}
