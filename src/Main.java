/*
 Classe Main : point d'entree du programme FastBite.
 Rejoue les 4 sequences UML du TP2 (Q3).
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("=== FastBite -- Demo TP3 ===\n");

        // Setup : creation des objets (Q2 : diagramme de classes)
        Restaurant chezMario = new Restaurant("Chez Mario", "12 rue de Paris");
        Plat pizza = new Plat("Pizza Margherita", 12.50, "Tomate, mozzarella, basilic");
        Plat pasta = new Plat("Pasta Carbonara", 10.00, "Lardons, oeuf, parmesan");
        chezMario.ajouterPlat(pizza);
        chezMario.ajouterPlat(pasta);

        Client alice = new Client("Alice", "alice@mail.com", "1234", "5 avenue Foch");
        Livreur bob  = new Livreur("Bob", "bob@mail.com", "abcd", "scooter");


        // Sequence 1 : Passer une commande
        System.out.println("--- Sequence 1 : Passer une commande ---");
        Commande cmd = alice.passerCommande(chezMario, 1001);
        chezMario.confirmerDisponibilite(pizza);   // <<include>> Verifier disponibilite
        cmd.ajouterPlat(pizza);
        cmd.ajouterPlat(pasta);


        // Sequence 2 : Confirmation et paiement
        System.out.println("\n--- Sequence 2 : Confirmation et paiement ---");
        cmd.validerCommande();
        alice.payerCommande(cmd);
        chezMario.recevoirCommande(cmd);


        // Sequence 3 : Livraison
        System.out.println("\n--- Sequence 3 : Livraison ---");
        Livraison liv = cmd.genererLivraison(bob);
        bob.accepterLivraison(liv);
        liv.demarrer();
        bob.confirmerLivraison(liv);
        liv.terminer();


        // Sequence 4 : Notation et historique
        System.out.println("\n--- Sequence 4 : Notation et historique ---");
        alice.noterLivreur(bob, 5);
        System.out.println("Historique de " + alice.getNom() + " : "
                + alice.getHistorique().size() + " commande(s)");
        System.out.println("Statut final commande #" + cmd.getNumero() + " : " + cmd.getStatut());
        System.out.println("Statut final livraison : " + liv.getStatut());

        System.out.println("\n=== Fin de la demo ===");
    }
}
