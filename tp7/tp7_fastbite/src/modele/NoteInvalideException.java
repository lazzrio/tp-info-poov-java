package modele;

/*
 Levee quand une note n'est pas comprise entre NOTE_MIN et NOTE_MAX.
 Utilisee par ajouterNote() des classes qui implementent Evaluable
 (Livreur, Plat, Restaurant) et par Client.noterRider().
 */
public class NoteInvalideException extends FastBiteException {
    private static final long serialVersionUID = 1L;

    private final double note;

    public NoteInvalideException(double note) {
        super("Note invalide : " + note
                + " (attendu entre " + Evaluable.NOTE_MIN + " et " + Evaluable.NOTE_MAX + ")");
        this.note = note;
    }

    public double getNote() { return note; }
}
