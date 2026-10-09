import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/*
 TP6 - Page 3 : recapitulatif de la commande validee, paiement et note du restaurant.
 Ici les listeners sont des CLASSES ANONYMES (une par bouton) : autre facon d'ecrire
 un ActionListener, vue au cours 6, pratique quand chaque bouton a son propre code.
 */
public class FenetreCommande extends JFrame {
    private final FenetreAccueil accueil;
    private final Donnees donnees;
    private final Commande commande;

    private final JTextArea recap = new JTextArea(8, 34);
    private final JTextField champPourboire = new JTextField("0", 6);
    private final JTextField champNote = new JTextField(6);
    private final JLabel moyenne = new JLabel(" ");
    private final JLabel etat = new JLabel(" ");
    private final JButton boutonPourboire = new JButton("Appliquer");
    private final JButton boutonNoter = new JButton("Noter le restaurant");
    private final JButton boutonPayer = new JButton("Payer");
    private final JButton boutonAccueil = new JButton("Retour a l'accueil");

    public FenetreCommande(FenetreAccueil accueil, Donnees donnees, Commande commande) {
        super("FastBite - Commande #" + commande.getNumero());
        this.accueil = accueil;
        this.donnees = donnees;
        this.commande = commande;

        JLabel titre = new JLabel("Commande #" + commande.getNumero() + " chez "
                + commande.getRestaurant().getNom(), JLabel.CENTER);
        titre.setFont(new Font("SansSerif", Font.BOLD, 20));

        recap.setEditable(false);
        recap.setFont(new Font("Monospaced", Font.PLAIN, 14));
        JScrollPane centre = new JScrollPane(recap);
        centre.setBorder(BorderFactory.createTitledBorder("Recapitulatif"));

        // Formulaire : GridLayout de 3 lignes x 3 colonnes (libelle, champ, bouton)
        JPanel formulaire = new JPanel(new GridLayout(3, 3, 8, 6));
        formulaire.add(new JLabel("Pourboire (EUR) :"));
        formulaire.add(champPourboire);
        formulaire.add(boutonPourboire);
        formulaire.add(new JLabel("Note du restaurant (0 a 5) :"));
        formulaire.add(champNote);
        formulaire.add(boutonNoter);
        formulaire.add(moyenne);
        formulaire.add(boutonPayer);
        formulaire.add(boutonAccueil);

        JPanel sud = new JPanel(new BorderLayout(4, 6));
        sud.add(formulaire, BorderLayout.CENTER);
        sud.add(etat, BorderLayout.SOUTH);

        JPanel contenu = new JPanel(new BorderLayout(10, 10));
        contenu.setBorder(BorderFactory.createEmptyBorder(12, 12, 8, 12));
        contenu.add(titre, BorderLayout.NORTH);
        contenu.add(centre, BorderLayout.CENTER);
        contenu.add(sud, BorderLayout.SOUTH);
        setContentPane(contenu);

        // --- LISTENERS (classes anonymes)
        boutonPourboire.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { appliquerPourboire(); }
        });
        boutonNoter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { noter(); }
        });
        boutonPayer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { payer(); }
        });
        boutonAccueil.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { retourAccueil(); }
        });

        rafraichir();
        setSize(620, 460);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    // Reecrit le recapitulatif (plats, pourboire, total, statut)
    private void rafraichir() {
        StringBuilder texte = new StringBuilder();
        for (Plat p : commande.getPlats()) {
            texte.append(" - ").append(p.getNom()).append(" : ").append(p.getPrix()).append(" EUR\n");
        }
        texte.append("\n Pourboire : ").append(commande.getPourboire()).append(" EUR\n");
        texte.append(" TOTAL     : ").append(commande.calculerTotal()).append(" EUR\n");
        texte.append(" Statut    : ").append(commande.getStatut()).append("\n");
        recap.setText(texte.toString());
    }

    // Le champ texte contient une String : la conversion peut echouer (NumberFormatException)
    private void appliquerPourboire() {
        try {
            double p = Double.parseDouble(champPourboire.getText().trim().replace(',', '.'));
            if (p < 0) {
                Messages.erreur(this, "Pourboire invalide", "Le pourboire ne peut pas etre negatif.");
                return;
            }
            commande.setPourboire(p);
            rafraichir();
            etat.setText("Pourboire de " + p + " EUR applique.");
        } catch (NumberFormatException ex) {
            Messages.erreur(this, "Pourboire invalide", "\"" + champPourboire.getText() + "\" n'est pas un nombre.");
        }
    }

    // NoteInvalideException (TP5) si la note n'est pas entre 0 et 5
    private void noter() {
        Restaurant r = commande.getRestaurant();
        try {
            double note = Double.parseDouble(champNote.getText().trim().replace(',', '.'));
            r.ajouterNote(note);
            moyenne.setText("Moyenne : " + r.getNoteMoyenne() + " / 5");
            etat.setText("Merci ! Note " + note + " enregistree pour " + r.getNom() + ".");
        } catch (NumberFormatException ex) {
            Messages.erreur(this, "Note invalide", "\"" + champNote.getText() + "\" n'est pas un nombre.");
        } catch (NoteInvalideException ex) {
            Messages.erreur(this, "Note invalide", ex.getMessage());
        } catch (AucuneNoteException ex) {
            moyenne.setText("Pas encore de note");
        }
    }

    // PaiementImpossibleException (TP5) si la commande est deja payee
    private void payer() {
        try {
            donnees.getClient().payerCommande(commande);
            rafraichir();
            etat.setText("Paiement accepte : " + commande.calculerTotal() + " EUR.");
        } catch (PaiementImpossibleException ex) {
            Messages.erreur(this, "Paiement refuse", ex.getMessage());
        }
    }

    // Navigation : retour a la page 1
    private void retourAccueil() {
        accueil.rafraichir();
        accueil.setEtat("Commande #" + commande.getNumero() + " : " + commande.getStatut());
        accueil.setVisible(true);
        dispose();
    }

    // Accesseurs utilises par les tests
    public JTextField getChampPourboire() { return champPourboire; }
    public JTextField getChampNote()      { return champNote; }
    public JButton getBoutonPourboire()   { return boutonPourboire; }
    public JButton getBoutonNoter()       { return boutonNoter; }
    public JButton getBoutonPayer()       { return boutonPayer; }
    public JButton getBoutonAccueil()     { return boutonAccueil; }
    public Commande getCommande()         { return commande; }
    public String getEtat()               { return etat.getText(); }
}
