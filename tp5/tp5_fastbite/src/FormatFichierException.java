/*
 Levee quand une ligne d'un fichier texte n'a pas le format attendu
 (mauvais nombre de champs, prix non numerique, ligne PLAT avant la ligne RESTAURANT...).
 On garde le numero de ligne pour aider l'utilisateur a corriger son fichier.
 */
public class FormatFichierException extends FastBiteException {
    private static final long serialVersionUID = 1L;

    private final int ligne;

    public FormatFichierException(String fichier, int ligne, String contenu, String raison) {
        super("Fichier " + fichier + ", ligne " + ligne + " : " + raison + " [" + contenu + "]");
        this.ligne = ligne;
    }

    // Version avec la cause d'origine (ex : NumberFormatException de Double.parseDouble)
    public FormatFichierException(String fichier, int ligne, String contenu, String raison, Throwable cause) {
        super("Fichier " + fichier + ", ligne " + ligne + " : " + raison + " [" + contenu + "]", cause);
        this.ligne = ligne;
    }

    public int getLigne() { return ligne; }
}
