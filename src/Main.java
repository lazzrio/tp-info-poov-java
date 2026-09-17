/*
 Classe Main : point d'entree du programme.
 Rejoue les 4 sequences UML du TP2 (Q3).
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("=== Deliveroo -- Demo TP3 ===\n");

        // Setup : creation des objets (Q2 : diagramme de classes)
        Restaurant chezMario = new Restaurant("Chez Mario", "12 rue de Paris", "Paris 16");
        Plat pizza = new Plat("Pizza Margherita", 12.50,
                "Tomate, mozzarella, basilic", new String[]{"gluten", "lactose"});
        Plat pasta = new Plat("Pasta Carbonara", 10.00,
                "Lardons, oeuf, parmesan", new String[]{"gluten", "oeuf", "lactose"});
        chezMario.gererMenu(pizza);
        chezMario.gererMenu(pasta);

        Client alice = new Client("Alice", "alice@mail.com", "1234",
                "5 avenue Foch", true);   // abonnement Plus
        Livreur bob = new Livreur("Bob", "bob@mail.com", "abcd", "scooter");


        // Sequence 1 : Passer une commande
        System.out.println("--- Sequence 1 : Passer une commande ---");
        Commande cmd = alice.passerCommande(chezMario, 1001);
        cmd.ajouterPlat(pizza);
        cmd.ajouterPlat(pasta);
        cmd.setPourboire(2.0);


        // Sequence 2 : Confirmation et paiement
        System.out.println("\n--- Sequence 2 : Confirmation et paiement ---");
        cmd.valider();
        alice.payerCommande(cmd);
        chezMario.recevoirCommande(cmd);
        chezMario.preparerCommande(cmd);


        // Sequence 3 : Livraison
        System.out.println("\n--- Sequence 3 : Livraison ---");
        Livraison liv = cmd.genererLivraison(bob, 20);   // 20 minutes estimees
        bob.accepterLivraison(liv);
        liv.demarrer();
        bob.seGeolocaliser("48.87,2.30");
        bob.confirmerLivraison(liv);
        liv.terminer();


        // Sequence 4 : Notation et historique
        System.out.println("\n--- Sequence 4 : Notation et historique ---");
        alice.noterRider(bob, 5);
        System.out.println("Note moyenne de " + bob.getNom() + " : " + bob.getNoteMoyenne());
        System.out.println("Historique de " + alice.getNom() + " : "
                + alice.getNbCommandes() + " commande(s)");
        System.out.println("Statut final commande #" + cmd.getNumero() + " : " + cmd.getStatut());
        System.out.println("Statut final livraison : " + liv.getStatut());

        System.out.println("\n=== Fin de la demo ===");
    }
}
