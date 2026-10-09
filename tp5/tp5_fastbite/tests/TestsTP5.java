/*
 Tests du TP4 adaptes au TP5 : on verifie que les changements (exceptions) n'ont rien casse.
 Lancer :  javac -encoding UTF-8 -cp out -d out tests/TestsTP5.java  puis  java -cp out TestsTP5
 */
import java.io.File;
import java.util.ArrayList;

public class TestsTP5 {
    static void check(boolean ok, String message) {
        if (!ok) throw new AssertionError(message);
    }

    public static void main(String[] args) throws Exception {
        // --- Evaluable (TP4 Q1) : moyenne, notes invalides, pas de note
        Evaluable[] objets = {new Livreur(), new Plat(), new Restaurant()};
        for (Evaluable e : objets) {
            try { e.getNoteMoyenne(); throw new AssertionError("Moyenne sans note acceptee"); }
            catch (AucuneNoteException attendu) { }
            e.ajouterNote(2); e.ajouterNote(4); e.ajouterNote(5);
            check(Math.abs(e.getNoteMoyenne() - 11.0 / 3) < 1e-10, "Moyenne");
            for (double note : new double[]{-1, 6, Double.NaN, Double.POSITIVE_INFINITY}) {
                try { e.ajouterNote(note); throw new AssertionError("Note invalide acceptee"); }
                catch (NoteInvalideException attendu) { }
            }
            check(Math.abs(e.getNoteMoyenne() - 11.0 / 3) < 1e-10, "Moyenne inchangee apres notes refusees");
        }

        // --- Panier (TP4 Q3) : collection dynamique, heritage, comparaison
        Panier p = new Panier();
        Plat plat = new Plat("Test", 0.75, "", new String[0]);
        for (int i = 0; i < 25; i++) p.ajouterPlat(plat);
        check(p.getNbPlats() == 25, "Collection dynamique");
        Commande c = p;
        check(c.getPlats().length == 25 && c.calculerTotal() == 18.75, "Heritage");
        c.setPourboire(1);
        check(c.getMontantTotal() == 19.75, "Pourboire");
        Panier q = new Panier(); q.ajouterPlat(new Plat("Egal", 19.75, "", new String[0]));
        check(p.compareTo(q) == 0, "Egalite");
        q.ajouterPlat(plat);
        check(p.compareTo(q) < 0 && q.compareTo(p) > 0, "Comparaison");
        Client client = new Client();
        Panier sauvegarde = new Panier(1, client, new Restaurant());
        sauvegarde.sauvegarderPanier(); sauvegarde.sauvegarderPanier();
        check(client.getPaniersSauvegardes().size() == 1, "Sauvegarde sans doublon");
        check(java.lang.reflect.Modifier.isAbstract(Utilisateur.class.getModifiers()), "Classe abstraite");

        // --- TP5 : nouvelles exceptions
        Restaurant r = new Restaurant("R", "a", "z");
        Plat pizza = new Plat("Pizza", 10, "", new String[0]);
        r.gererMenu(pizza);
        Commande cmd = new Commande(1, client, r);
        try { cmd.valider(); throw new AssertionError("Commande vide validee"); }
        catch (CommandeVideException attendu) { }
        try { client.payerCommande(cmd); throw new AssertionError("Commande vide payee"); }
        catch (PaiementImpossibleException attendu) { }
        try { cmd.ajouterPlat(new Plat("Absent", 1, "", new String[0])); throw new AssertionError("Plat absent accepte"); }
        catch (PlatIndisponibleException attendu) { }
        cmd.ajouterPlat(pizza);
        client.payerCommande(cmd);
        try { client.payerCommande(cmd); throw new AssertionError("Double paiement"); }
        catch (PaiementImpossibleException attendu) { }
        try { cmd.ajouterPlat(null); throw new AssertionError("null accepte"); }
        catch (IllegalArgumentException attendu) { }

        Livreur l = new Livreur();
        Livraison liv = cmd.genererLivraison(l, 10);
        try { liv.demarrer(); throw new AssertionError("Demarrage avant acceptation"); }
        catch (EtatLivraisonException attendu) { }
        l.accepterLivraison(liv);
        try { l.accepterLivraison(liv); throw new AssertionError("Livreur occupe"); }
        catch (LivreurIndisponibleException attendu) { }
        liv.demarrer();
        try { liv.demarrer(); throw new AssertionError("Double demarrage"); }
        catch (EtatLivraisonException attendu) { }
        liv.terminer();

        Restaurant plein = new Restaurant();
        for (int i = 0; i < 20; i++) plein.gererMenu(new Plat("P" + i, 1, "", new String[0]));
        try { plein.gererMenu(pizza); throw new AssertionError("Menu depasse"); }
        catch (CapaciteDepasseeException attendu) { }
        Client plein2 = new Client();
        for (int i = 0; i < 20; i++) plein2.passerCommande(r, i);
        try { plein2.passerCommande(r, 99); throw new AssertionError("Historique depasse"); }
        catch (CapaciteDepasseeException attendu) { }

        // --- TP5 : fichier texte (aller-retour) et erreurs de format
        File f = File.createTempFile("menu", ".txt");
        Restaurant src = new Restaurant("Src", "a", "z");
        src.gererMenu(new Plat("Pizza", 12.5, "Tomate, basilic", new String[]{"gluten", "lactose"}));
        src.gererMenu(new Plat("Eau", 2.0, "50cl", new String[0]));
        src.sauvegarderMenu(f.getPath());
        Restaurant dst = new Restaurant("Dst", "a", "z");
        dst.chargerMenu(f.getPath());
        check(dst.getNbPlats() == 2, "Relecture : 2 plats");
        check(dst.getMenu()[0].getAllergenes().length == 2 && dst.getMenu()[1].getAllergenes().length == 0, "Allergenes");
        check(dst.getMenu()[0].getPrix() == 12.5 && dst.getMenu()[0].getDescription().equals("Tomate, basilic"), "Prix/description");
        try (java.io.FileWriter w = new java.io.FileWriter(f)) { w.write("Pizza;abc;x;\n"); }
        try { new Restaurant().chargerMenu(f.getPath()); throw new AssertionError("Prix invalide accepte"); }
        catch (FormatFichierException attendu) { check(attendu.getCause() instanceof NumberFormatException, "Cause chainee"); }
        f.delete();

        // --- TP5 : serialisation (aller-retour)
        File s = File.createTempFile("resto", ".ser");
        ArrayList<Restaurant> liste = new ArrayList<Restaurant>();
        Restaurant orig = new Restaurant("Mario", "a", "Paris 16");
        orig.gererMenu(new Plat("Pizza", 12.5, "x", new String[]{"gluten"}));
        orig.ajouterNote(4);
        orig.recevoirCommande(new Commande(1, null, orig));
        liste.add(orig);
        Restaurant.sauvegarderRestaurants(liste, s.getPath());
        ArrayList<Restaurant> relus = Restaurant.chargerRestaurants(s.getPath());
        Restaurant copie = relus.get(0);
        check(copie != orig && copie.getNom().equals("Mario") && copie.getNbPlats() == 1, "Copie identique");
        check(copie.getNoteMoyenne() == 4.0, "Note conservee");
        check(copie.getNbCommandesRecues() == 0 && orig.getNbCommandesRecues() == 1, "transient remis a 0");
        s.delete();

        System.out.println("Tous les tests TP5 sont passes.");
    }
}
