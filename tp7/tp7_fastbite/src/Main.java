import modele.*;

/*
 Classe Main : point d'entree du programme TP5 (suite du TP4 Question 4).
 Usage : java Main        -> lance Q1, Q2 et Q3
         java Main q1     -> uniquement Q1 (exceptions)
         java Main q2     -> uniquement Q2 (fichier texte)
         java Main q3     -> uniquement Q3 (serialisation)
 */
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class Main {
    // Dossier des fichiers produits par le programme (.txt et .ser)
    private static final String DOSSIER = "data";

    public static void main(String[] args) {
        new File(DOSSIER).mkdirs();
        String choix = (args.length > 0) ? args[0] : "tout";

        // Multi-catch : plusieurs types d'exceptions, meme traitement
        try {
            if (choix.equals("q1") || choix.equals("tout")) demoQ1();
            if (choix.equals("q2") || choix.equals("tout")) demoQ2();
            if (choix.equals("q3") || choix.equals("tout")) demoQ3();
        } catch (FastBiteException | IOException | ClassNotFoundException e) {
            System.out.println("ERREUR INATTENDUE : " + e);
        }
    }


    // ============================================================
    // Outils de mise en forme / de preparation
    // ============================================================

    private static void titre(String texte) {
        System.out.println("\n==================== " + texte + " ====================");
    }

    private static void cas(String texte) {
        System.out.println("\n>>> " + texte);
    }

    // Cree le restaurant du TP3/TP4 (Chez Mario, pizza + pasta)
    private static Restaurant creerChezMario() throws CapaciteDepasseeException {
        Restaurant r = new Restaurant("Chez Mario", "12 rue de Paris", "Paris 16");
        r.gererMenu(new Plat("Pizza Margherita", 12.50,
                "Tomate, mozzarella, basilic", new String[]{"gluten", "lactose"}));
        r.gererMenu(new Plat("Pasta Carbonara", 10.00,
                "Lardons, oeuf, parmesan", new String[]{"gluten", "oeuf", "lactose"}));
        return r;
    }

    // Ecrit quelques lignes dans un fichier texte (sert a fabriquer des fichiers de test)
    private static void ecrireFichier(String fichier, String... lignes) throws IOException {
        try (FileWriter out = new FileWriter(fichier)) {
            for (int i = 0; i < lignes.length; i++) {
                out.write(lignes[i] + "\n");
            }
        }
    }

    // Affiche le contenu brut d'un fichier texte, ligne par ligne
    private static void afficherFichierBrut(String fichier) throws IOException {
        List<String> lignes = Files.readAllLines(Paths.get(fichier));
        System.out.println("--- contenu brut de " + fichier + " ---");
        for (int i = 0; i < lignes.size(); i++) {
            System.out.println((i + 1) + " | " + lignes.get(i));
        }
        System.out.println("--- fin du fichier ---");
    }

    // Affiche une liste de restaurants avec leur menu et leur note
    private static void afficherRestaurants(List<Restaurant> restaurants) {
        for (int i = 0; i < restaurants.size(); i++) {
            Restaurant r = restaurants.get(i);
            String note;
            try {
                note = r.getNoteMoyenne() + "/5";
            } catch (AucuneNoteException e) {
                note = "pas encore note";
            }
            System.out.println(" * " + r.getNom() + " [" + r.getZoneLivraison() + "] - note : " + note
                    + " - commandes recues (transient) : " + r.getNbCommandesRecues());
            Plat[] menu = r.getMenu();
            for (int j = 0; j < r.getNbPlats(); j++) {
                System.out.print("     ");
                menu[j].afficher();
            }
        }
    }

    private static void afficherPaniers(List<Panier> paniers) {
        for (Panier panier : paniers) {
            System.out.printf(Locale.FRANCE, "Panier #%d : %.2f EUR (%d plat(s))%n",
                    panier.getNumero(), panier.calculerTotal(), panier.getNbPlats());
        }
    }


    // ============================================================
    // Q1 : exceptions (lever, propager, attraper)
    // ============================================================

    private static void demoQ1() throws FastBiteException {
        titre("Q1 - EXCEPTIONS");

        // ---- Partie A : le scenario du TP3 + TP4 rejoue. Les exceptions sont PROPAGEES
        //      jusqu'ici ; une seule clause catch sur la classe mere FastBiteException
        //      suffit pour toutes les exceptions metier.
        cas("A. Scenario TP3 + TP4 (propagation + catch sur la classe mere)");
        try {
            scenarioNominal();
        } catch (FastBiteException e) {
            System.out.println("ERREUR METIER : " + e.getMessage());
        }

        // ---- Partie B : un cas d'erreur par exception, chacun attrape localement
        Restaurant r = creerChezMario();
        Plat pizza = r.getMenu()[0];
        Client c = new Client("Alice", "alice@mail.com", "1234", "5 avenue Foch", true);
        Livreur l = new Livreur("Bob", "bob@mail.com", "abcd", "scooter");

        cas("B1. Note invalide (NoteInvalideException)");
        try {
            c.noterRider(l, 7.5);
        } catch (NoteInvalideException e) {
            System.out.println("Attrape : " + e.getMessage());
        }

        cas("B2. Moyenne sans aucune note (AucuneNoteException)");
        try {
            System.out.println("Moyenne de " + l.getNom() + " : " + l.getNoteMoyenne());
        } catch (AucuneNoteException e) {
            System.out.println("Attrape : " + e.getMessage());
        }

        cas("B3. Plat absent du menu (PlatIndisponibleException)");
        Commande cmd = c.passerCommande(r, 2001);
        Plat sushi = new Plat("Sushi saumon", 9.0, "Riz, saumon", new String[0]);
        try {
            cmd.ajouterPlat(sushi);
        } catch (PlatIndisponibleException e) {
            System.out.println("Attrape : " + e.getMessage());
        }

        cas("B4. Commande vide : validation et paiement impossibles");
        try {
            cmd.valider();
        } catch (CommandeVideException e) {
            System.out.println("Attrape : " + e.getMessage());
        }
        try {
            c.payerCommande(cmd);
        } catch (PaiementImpossibleException e) {
            System.out.println("Attrape : " + e.getMessage());
        }

        cas("B5. Livreur deja occupe (LivreurIndisponibleException) + finally");
        cmd.ajouterPlat(pizza);
        Livraison liv1 = cmd.genererLivraison(l, 20);
        l.accepterLivraison(liv1);                       // Bob devient indisponible
        Commande cmd2 = c.passerCommande(r, 2002);
        cmd2.ajouterPlat(pizza);
        Livraison liv2 = cmd2.genererLivraison(l, 25);
        try {
            l.accepterLivraison(liv2);                   // Bob est deja pris
        } catch (LivreurIndisponibleException e) {
            System.out.println("Attrape : " + e.getMessage());
        } finally {
            System.out.println("finally : execute dans tous les cas (exception ou non)");
        }

        cas("B6. Livraison dans un etat incoherent (EtatLivraisonException)");
        try {
            liv2.terminer();                             // liv2 n'est jamais partie
        } catch (EtatLivraisonException e) {
            System.out.println("Attrape : " + e.getMessage());
        }

        cas("B7. Capacite depassee : 21e plat dans un menu de 20 (CapaciteDepasseeException)");
        Restaurant plein = new Restaurant("Resto plein", "1 rue Test", "Paris 1");
        PrintStream console = System.out;
        System.setOut(new PrintStream(new ByteArrayOutputStream()));   // on masque les 20 "ajoute au menu"
        try {
            for (int i = 1; i <= 21; i++) {
                plein.gererMenu(new Plat("Plat " + i, 5.0, "test", new String[0]));
            }
        } catch (CapaciteDepasseeException e) {
            System.setOut(console);
            System.out.println("Attrape : " + e.getMessage());
            System.out.println("Plats effectivement ajoutes : " + plein.getNbPlats());
        } finally {
            System.setOut(console);                      // on rend toujours la console
        }

        cas("B8. Exceptions NON verifiees (IllegalArgumentException) : bugs du code appelant");
        try {
            r.gererMenu(null);
        } catch (IllegalArgumentException e) {
            System.out.println("Attrape : " + e.getMessage());
        }
        try {
            cmd.ajouterPlat(null);
        } catch (IllegalArgumentException e) {
            System.out.println("Attrape : " + e.getMessage());
        }
    }


    // Scenario du TP3 (4 sequences UML) puis du TP4 (Evaluable, Panier). Ne contient AUCUN
    // try/catch : toutes les exceptions metier sont propagees (throws) a l'appelant.
    private static void scenarioNominal() throws FastBiteException {
        Restaurant r = creerChezMario();
        Plat pizza = r.getMenu()[0];
        Plat pasta = r.getMenu()[1];
        Client c = new Client("Alice", "alice@mail.com", "1234", "5 avenue Foch", true);
        Livreur l = new Livreur("Bob", "bob@mail.com", "abcd", "scooter");

        System.out.println("--- Sequence 1 : passer une commande ---");
        Commande cmd = c.passerCommande(r, 1001);
        cmd.ajouterPlat(pizza);
        cmd.ajouterPlat(pasta);
        cmd.setPourboire(2.0);
        c.payerCommande(cmd);
        cmd.valider();
        r.recevoirCommande(cmd);

        System.out.println("--- Sequence 2 : affectation et preparation ---");
        Livraison liv = cmd.genererLivraison(l, 20);
        l.accepterLivraison(liv);
        r.preparerCommande(cmd);

        System.out.println("--- Sequence 3 : retirer et livrer la commande ---");
        liv.demarrer();
        System.out.println(c.getNom() + " consulte le statut : " + liv.getStatut());
        l.confirmerLivraison(liv);
        liv.terminer();

        System.out.println("--- Sequence 4 : accepter ou refuser une mission ---");
        Commande cmd2 = c.passerCommande(r, 1002);
        cmd2.ajouterPlat(pizza);
        cmd2.valider();
        Livraison liv2 = cmd2.genererLivraison(l, 25);
        l.accepterLivraison(liv2);

        System.out.println("--- Bilan TP3 ---");
        c.noterRider(l, 5);
        System.out.println("Note moyenne de " + l.getNom() + " : " + l.getNoteMoyenne());
        c.afficherProfil();
        l.afficherProfil();

        System.out.println("--- TP4 Q1 : meme interface Evaluable pour trois classes ---");
        Livreur david = new Livreur("David", "david@mail.com", "efgh", "velo");
        Evaluable[] elements = {david, pizza, r};
        for (Evaluable element : elements) {
            element.ajouterNote(4);
            element.ajouterNote(5);
            System.out.println(element.getClass().getSimpleName() + " : " + element.getNoteMoyenne() + "/5");
        }

        System.out.println("--- TP4 Q3 : collection et tri des paniers ---");
        Panier p1 = new Panier(2001, c, r);
        p1.ajouterPlat(pizza);
        p1.ajouterPlat(pasta);
        Panier p2 = new Panier(2002, c, r);
        p2.ajouterPlat(pasta);
        Panier p3 = new Panier(2003, c, r);
        p3.ajouterPlat(pizza);
        List<Panier> paniers = new ArrayList<>();
        paniers.add(p1);
        paniers.add(p2);
        paniers.add(p3);
        Collections.sort(paniers);                       // utilise Panier.compareTo
        afficherPaniers(paniers);
    }


    // ============================================================
    // Q2 : fichier texte (ecrire, lire, afficher)
    // ============================================================

    private static void demoQ2() throws FastBiteException {
        titre("Q2 - FICHIER TEXTE (.txt)");

        Restaurant r = creerChezMario();
        r.gererMenu(new Plat("Eau minerale", 2.0, "Bouteille 50cl", new String[0]));
        String fichier = DOSSIER + "/menu_chezmario.txt";

        cas("1. ECRITURE du menu dans " + fichier);
        try {
            r.sauvegarderMenu(fichier);
            afficherFichierBrut(fichier);
        } catch (IOException e) {
            System.out.println("Erreur d'ecriture : " + e.getMessage());
        }

        cas("2. LECTURE du fichier dans un nouveau restaurant, puis AFFICHAGE");
        Restaurant relu = new Restaurant("Chez Mario (relu)", "12 rue de Paris", "Paris 16");
        try {
            relu.chargerMenu(fichier);
            relu.afficherMenu();
        } catch (IOException e) {
            System.out.println("Erreur de lecture : " + e.getMessage());
        } catch (FormatFichierException e) {
            System.out.println("Fichier incorrect : " + e.getMessage());
        }

        cas("3. Fichier introuvable (IOException propagee puis attrapee)");
        try {
            relu.chargerMenu(DOSSIER + "/inexistant.txt");
        } catch (FileNotFoundException e) {
            System.out.println("Attrape (FileNotFoundException) : fichier introuvable -> "
                    + DOSSIER + "/inexistant.txt");
        } catch (IOException e) {
            System.out.println("Attrape (IOException) : " + e.getMessage());
        }

        cas("4. Fichier mal forme : prix non numerique (FormatFichierException + cause)");
        String corrompu1 = DOSSIER + "/menu_corrompu1.txt";
        String corrompu2 = DOSSIER + "/menu_corrompu2.txt";
        try {
            ecrireFichier(corrompu1,
                    "Pizza Margherita;12.5;Tomate, mozzarella;gluten,lactose",
                    "Tiramisu;abc;Dessert maison;lactose");
            ecrireFichier(corrompu2,
                    "Pizza Margherita;12.5;Tomate, mozzarella;gluten,lactose",
                    "Tiramisu;5.5");
            afficherFichierBrut(corrompu1);
            relu.chargerMenu(corrompu1);
        } catch (FormatFichierException e) {
            System.out.println("Attrape : " + e.getMessage());
            System.out.println("Cause d'origine : " + e.getCause());
        } catch (IOException e) {
            System.out.println("Erreur d'E/S : " + e.getMessage());
        }

        cas("5. Fichier mal forme : nombre de champs incorrect");
        try {
            afficherFichierBrut(corrompu2);
            relu.chargerMenu(corrompu2);
        } catch (FormatFichierException e) {
            System.out.println("Attrape : " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Erreur d'E/S : " + e.getMessage());
        }
        System.out.println("Le menu n'a pas ete modifie par les lectures ratees : "
                + relu.getNbPlats() + " plats (comme avant)");
    }


    // ============================================================
    // Q3 : serialisation / deserialisation (.ser)
    // ============================================================

    private static void demoQ3() throws FastBiteException, IOException, ClassNotFoundException {
        titre("Q3 - SERIALISATION (.ser)");

        // Preparation : deux restaurants, des plats et des notes
        Restaurant mario = creerChezMario();
        mario.ajouterNote(4.5);
        mario.ajouterNote(4.0);
        mario.getMenu()[0].ajouterNote(5);
        mario.recevoirCommande(new Commande(9000, null, mario));   // incremente le compteur transient

        Restaurant sushi = new Restaurant("Sushi Tokyo", "8 rue Boileau", "Paris 16");
        sushi.gererMenu(new Plat("Sushi saumon", 9.0, "Riz, saumon", new String[]{"poisson"}));

        ArrayList<Restaurant> restaurants = new ArrayList<Restaurant>();
        restaurants.add(mario);
        restaurants.add(sushi);

        String fichier = DOSSIER + "/restaurants.ser";

        cas("1. Objets AVANT serialisation");
        afficherRestaurants(restaurants);

        cas("2. SERIALISATION dans " + fichier);
        Restaurant.sauvegarderRestaurants(restaurants, fichier);
        System.out.println("Taille du fichier : " + new File(fichier).length() + " octets (binaire)");

        cas("3. DESERIALISATION et affichage des objets recuperes");
        ArrayList<Restaurant> relus = Restaurant.chargerRestaurants(fichier);
        afficherRestaurants(relus);
        System.out.println("Meme objet en memoire (==) ? " + (relus.get(0) == mario) + "   -> c'est une COPIE");
        System.out.println("Meme nom ? " + relus.get(0).getNom().equals(mario.getNom()));
        System.out.println("Note du plat '" + relus.get(0).getMenu()[0].getNom() + "' : "
                + relus.get(0).getMenu()[0].getNoteMoyenne() + "/5 (conservee)");
        System.out.println("Le champ transient nbCommandesRecues : " + mario.getNbCommandesRecues()
                + " avant, " + relus.get(0).getNbCommandesRecues() + " apres (non sauvegarde)");

        cas("4. Classe NON Serializable (NotSerializableException)");
        Client alice = new Client("Alice", "alice@mail.com", "1234", "5 avenue Foch", true);
        String refuse = DOSSIER + "/client_refuse.ser";
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(refuse))) {
            out.writeObject(alice);
        } catch (NotSerializableException e) {
            System.out.println("Attrape : la classe " + e.getMessage()
                    + " n'implemente pas Serializable");
        }
        new File(refuse).delete();

        cas("5. Fichier .ser corrompu (StreamCorruptedException)");
        String corrompu = DOSSIER + "/restaurants_corrompu.ser";
        ecrireFichier(corrompu, "ceci est du texte, pas un objet serialise");
        try {
            Restaurant.chargerRestaurants(corrompu);
        } catch (IOException e) {
            System.out.println("Attrape (" + e.getClass().getSimpleName() + ") : " + e.getMessage());
        }
    }
}
