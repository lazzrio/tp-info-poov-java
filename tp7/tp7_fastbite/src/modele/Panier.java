package modele;

import java.util.Collections;
import java.util.List;

/** Q3 (TP4) : panier de plats, conforme a l'heritage du diagramme fourni.
 * La List<Plat> est heritee de Commande pour ne pas dupliquer les plats.
 * Ordre naturel : montant total croissant, pourboire inclus comme dans Commande.
 * TP5 : ajouterPlat doit redeclarer "throws PlatIndisponibleException" : une methode
 * redefinie ne peut pas lever PLUS d'exceptions verifiees que celle de la classe mere.
 */
public class Panier extends Commande implements Comparable<Panier> {
    public Panier() { super(); }

    public Panier(int numero, Client client, Restaurant restaurant) {
        super(numero, client, restaurant);
    }

    // Methode d'ajout explicite pour la question 3.
    @Override
    public void ajouterPlat(Plat plat) throws PlatIndisponibleException { super.ajouterPlat(plat); }

    public List<Plat> getListePlats() {
        return Collections.unmodifiableList(plats);
    }

    @Override
    public int compareTo(Panier autre) {
        return Double.compare(calculerTotal(), autre.calculerTotal());
    }

    public Panier creerPanier() {
        return new Panier(getNumero(), getClient(), getRestaurant());
    }

    // Sauvegarde en memoire chez le client, sans ecriture sur disque.
    // TP4 (inchange) : IllegalStateException = erreur de programmation, non verifiee.
    public void sauvegarderPanier() {
        if (getClient() == null) throw new IllegalStateException("Client manquant.");
        getClient().sauvegarderPanier(this);
    }
}
