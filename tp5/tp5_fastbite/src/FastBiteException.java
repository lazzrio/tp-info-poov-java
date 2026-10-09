/*
 Exception de base de l'application FastBite.
 Toutes les exceptions "metier" en heritent : un seul catch (FastBiteException e)
 permet de les attraper toutes, et on peut aussi attraper chacune separement.
 C'est une exception verifiee (checked) : le compilateur oblige a la traiter
 (try/catch) ou a la propager (throws).
 */
public class FastBiteException extends Exception {
    private static final long serialVersionUID = 1L;


    public FastBiteException() {
        super();
    }

    public FastBiteException(String message) {
        super(message);
    }

    // Constructeur avec cause : garde la trace de l'exception d'origine (ex : NumberFormatException)
    public FastBiteException(String message, Throwable cause) {
        super(message, cause);
    }
}
