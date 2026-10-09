/*
 Interface Evaluable (TP4 Q1) : tout ce qui peut recevoir une note des clients.
 Implementee par Livreur, Plat et Restaurant.
 TP5 : les deux methodes declarent maintenant les exceptions qu'elles peuvent lever (throws).
 */
public interface Evaluable {
    // Bornes des notes (constantes : public static final implicites dans une interface)
    double NOTE_MIN = 0.0;
    double NOTE_MAX = 5.0;

    // TP4 : IllegalArgumentException (non verifiee)  ->  TP5 : NoteInvalideException (verifiee)
    void ajouterNote(double note) throws NoteInvalideException;

    // TP4 : retournait 0 sans note (ambigu avec une vraie note 0)  ->  TP5 : AucuneNoteException
    double getNoteMoyenne() throws AucuneNoteException;
}
