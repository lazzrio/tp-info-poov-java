/*
 Classe Main : point d'entree du programme.
 Rejoue les 4 sequences UML du TP2 (Q3), dans le meme ordre que les diagrammes.
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("=== Deliveroo -- Demo TP3 ===\n");

        // Setup : creation des objets (Q2 : diagramme de classes)
        Restaurant r = new Restaurant("Chez Mario", "12 rue de Paris", "Paris 16");

        // Tableau d'allergenes pour la pizza
        String[] allergenesPizza = new String[2];
        allergenesPizza[0] = "gluten";
        allergenesPizza[1] = "lactose";
        Plat pizza = new Plat("Pizza Margherita", 12.50,
                "Tomate, mozzarella, basilic", allergenesPizza);

        // Tableau d'allergenes pour les pates
        String[] allergenesPasta = new String[3];
        allergenesPasta[0] = "gluten";
        allergenesPasta[1] = "oeuf";
        allergenesPasta[2] = "lactose";
        Plat pasta = new Plat("Pasta Carbonara", 10.00,
                "Lardons, oeuf, parmesan", allergenesPasta);

        r.gererMenu(pizza);
        r.gererMenu(pasta);

        Client c = new Client("Alice", "alice@mail.com", "1234",
                "5 avenue Foch", true);   // abonnement Plus
        Livreur l = new Livreur("Bob", "bob@mail.com", "abcd", "scooter");


        // ============================================================
        // Sequence 1 : passer une commande
        // ============================================================
        System.out.println("--- Sequence 1 : passer une commande ---");
        Commande cmd = c.passerCommande(r, 1001);
        cmd.ajouterPlat(pizza);
        cmd.ajouterPlat(pasta);
        cmd.setPourboire(2.0);

        c.payerCommande(cmd);
        cmd.valider();
        r.recevoirCommande(cmd);
        System.out.println("Notification -> " + c.getNom() + " : commande acceptee");
        System.out.println("Notification -> " + l.getNom() + " : mission proposee");


        // ============================================================
        // Sequence 2 : affectation et preparation
        // ============================================================
        System.out.println("\n--- Sequence 2 : affectation et preparation ---");
        Livraison liv = cmd.genererLivraison(l, 20);
        l.accepterLivraison(liv);
        System.out.println("Notification -> " + c.getNom() + " : livreur affecte");

        r.preparerCommande(cmd);
        System.out.println("Notification -> " + c.getNom() + " : commande prete");


        // ============================================================
        // Sequence 3 : retirer et livrer la commande
        // ============================================================
        System.out.println("\n--- Sequence 3 : retirer et livrer la commande ---");
        liv.demarrer();
        System.out.println("Notification -> " + r.getNom() + " : commande retiree");
        System.out.println("Notification -> " + c.getNom() + " : commande en route");

        // Le client consulte le statut de sa livraison
        System.out.println(c.getNom() + " consulte le statut : " + liv.getStatut());

        l.confirmerLivraison(liv);
        liv.terminer();
        System.out.println("Notification -> " + c.getNom() + " : commande livree");


        // ============================================================
        // Sequence 4 : accepter ou refuser une mission (alt)
        // ============================================================
        System.out.println("\n--- Sequence 4 : accepter ou refuser une mission ---");

        // Deuxieme commande pour tester le branchement
        Commande cmd2 = c.passerCommande(r, 1002);
        cmd2.ajouterPlat(pizza);
        cmd2.valider();
        Livraison liv2 = cmd2.genererLivraison(l, 25);

        // alt : [le livreur accepte et la mission est disponible]
        if (l.isDisponible()) {
            l.accepterLivraison(liv2);
            System.out.println("Notification -> " + c.getNom() + " : livreur affecte");
        } else {
            // [sinon : refus ou mission indisponible]
            System.out.println("Notification -> " + r.getNom() + " : affectation non confirmee");
            System.out.println("Notification -> " + c.getNom() + " : statut de l'affectation en cours");
        }


        // ============================================================
        // Bilan final
        // ============================================================
        System.out.println("\n--- Bilan ---");
        c.noterRider(l, 5);
        System.out.println("Note moyenne de " + l.getNom() + " : " + l.getNoteMoyenne());
        System.out.println("Historique de " + c.getNom() + " : "
                + c.getNbCommandes() + " commande(s)");
        System.out.println("Statut final commande #1001 : " + cmd.getStatut());
        System.out.println("Statut final livraison : " + liv.getStatut());

        System.out.println("\n=== Fin de la demo ===");
    }
}
